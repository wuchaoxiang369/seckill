package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wuchaoxiang on 2017/8/5
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getAllSeckill() throws Exception {
        List<Seckill> seckillList  = seckillService.getAllSeckill();
        logger.info("seckillList={}",seckillList);
    }

    @Test
    public void getSeckill() throws Exception {
        Seckill seckill = seckillService.getSeckill(1000L);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void testSeckillUrl() throws Exception {
        //exposer=Exposer{exposed=true,
        // md5='be536808999acf05027190caa85de228', seckillId=1000, now=0, start=0, end=0}
        Exposer exposer = seckillService.exposeSeckillUrl(1001L);
        if(exposer.isExposed()) {
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(1000L,18204608384L,"be536808999acf05027190caa85de228");
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.warn("exposer={}", exposer);
        }
    }

    @Test
    public void testProcSeckill() throws Exception {
        long seckillId = 1002L;
        long phone = 18204608384L;
        Exposer exposer = seckillService.exposeSeckillUrl(seckillId);
        if(exposer.isExposed()) {
            String md5 = exposer.getMd5();
            SeckillExecution seckillExecution = seckillService.executeProcSeckill(seckillId, phone,md5);
            logger.info(seckillExecution.getStateInfo());

        }
    }

}