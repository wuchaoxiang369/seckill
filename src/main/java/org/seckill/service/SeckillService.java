package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口：站在“使用者”的角度设计
 * 1.方法粒度
 * 2.参数
 * 3.返回值
 * Created by wuchaoxiang on 2017/8/5
 */
public interface SeckillService {

    List<Seckill> getAllSeckill();

    Seckill getSeckill(long seckillId);

    /**
     * 秒杀开启时暴露秒杀地址，否则显示系统时间和秒杀时间
     * 返回类型需要在dto里定义
     * @param seckillId
     */
    Exposer exposeSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillCloseException, RepeatKillException, SeckillException;

    /**
     * 通过存储过程执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeProcSeckill(long seckillId, long userPhone, String md5);

}
