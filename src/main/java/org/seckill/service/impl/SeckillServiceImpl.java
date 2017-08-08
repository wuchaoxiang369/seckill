package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuchaoxiang on 2017/8/5
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入service的依赖，会自动从spring容器中查找依赖的实例
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    //md5加密字符串，盐值混淆
    private String slat = "nfiajfksd*jfklajskf^#Y&^*Q#&*^&#";

    public List<Seckill> getAllSeckill() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getSeckill(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exposeSeckillUrl(long seckillId) {
        /**优化点: 一致性维护 在超时的基础上
         * 1.先从redis中查找
         */
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null) {
            //2.缓存没有，访问数据库
            seckill = seckillDao.queryById(seckillId);
            if(seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //3.放入缓存中，以便下一次查找
                redisDao.putSeckill(seckill);
            }
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false,seckillId,nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //转化特定的字符串，md5转化不可逆
        String md5 = getMD5(seckillId);
        //秒杀开启暴露url
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 使用注解的方式配置事务的优点：
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间更短，不要穿插其他的网络操作：RPC/HTTP请求或者剥离到事务方法外
     * 3.不是所以的方法都需要事务
     * @param seckillId
     * @param userPhone
     * @param md5
     * seckillId,userphone,md5 都是用户传入
     * @return
     * @throws SeckillCloseException
     * @throws RepeatKillException
     * @throws SeckillException
     */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillCloseException, RepeatKillException, SeckillException {
        try {
            if(md5 == null || !md5.equals(getMD5(seckillId))) {
                throw new SeckillException("seckill data rewrite");
            }
            /**执行秒杀逻辑：
             * 1.第一个优化
             * 记录用户购买行为，减库存
             * 调整减库存和记录用户购买行为的顺序：
             * insert不需要行级锁，可以并发允许
             * update会获得行级锁，如果insert在后面 会使持有行级锁的时间变长
             * 2.事务操作放在MySQL端进行
             */
            Date killTime = new Date();
            //记录用户购买行为
            //进行唯一性检查
            int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
            if(insertCount <=0 ) {
                //重复秒杀
                throw new RepeatKillException("seckill repeat");
            } else {
                //减库存
                int reduceCount = seckillDao.reduceNumber(seckillId,killTime);
                if(reduceCount <=0 ) {
                    //没有成功减库存
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new SeckillException("seckill inner error : " + e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeProcSeckill(long seckillId, long userPhone, String md5) {
        if(md5 == null || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("seckillId", seckillId);
        params.put("phone", userPhone);
        params.put("killTime", new Date());
        params.put("result",null);
        try {
            seckillDao.killByProc(params);
            //获取返回result
            int result = MapUtils.getInteger(params, "result", -2);
            if(result == 1) {
                SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
            } else {
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }
}
