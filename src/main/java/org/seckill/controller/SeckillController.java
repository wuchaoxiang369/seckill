package org.seckill.controller;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by wuchaoxiang on 2017/8/6
 */
@Controller
/**
 * 代表模块 使用Restful规范
 */
@RequestMapping("/seckill")
public class SeckillController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    /**
     * 获取秒杀商品列表数据
     */
    @RequestMapping(value = {"/list"},method = RequestMethod.GET)
    public String list(Model model) {
        List<Seckill> seckills = seckillService.getAllSeckill();
        model.addAttribute("seckills", seckills);
        return "list";
    }

    /**
     * 获取单个商品的详情
     */
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if(seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getSeckill(seckillId);
        if(seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    /**
     * 暴露秒杀接口
     */
    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result = null;
        try {
            Exposer exposer = seckillService.exposeSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    /**
     * 开始执行秒杀
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execute", method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "userPhone", required = false) Long userPhone) {
        if(userPhone == null) {
            return new SeckillResult<SeckillExecution>(false, "请输入手机");
        }
        SeckillResult<SeckillExecution> result = null;
        try {
            SeckillExecution seckillExecution = seckillService.executeProcSeckill(seckillId, userPhone, md5);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (SeckillCloseException e1) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (RepeatKillException e2) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_ERROR);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (Exception e) {
            logger.error(e.getMessage());
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        }
    }
    /**
     * 获取系统时间
     */
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> getTime() {
        Date now = new Date();
        return new SeckillResult<Long>(true, now.getTime());
    }

}
