package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by wuchaoxiang on 2017/8/7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class RedisDaoTest {

    long seckillId = 1000L;
    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testSeckill() throws Exception {
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null) {
            seckill = seckillDao.queryById(seckillId);
            if(seckill != null) {
                String result = redisDao.putSeckill(seckill);
                System.out.println("result : " + result);
                seckill = redisDao.getSeckill(seckillId);
                System.out.println("seckill : " + seckill);
            }
        }
    }

}