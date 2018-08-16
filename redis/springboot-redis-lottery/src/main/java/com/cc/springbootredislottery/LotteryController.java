package com.cc.springbootredislottery;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by CarlosXiao on 2018/7/5.
 */

@RestController
@RequestMapping("lottery")
public class LotteryController {

    @Resource
    private LotteryService lotteryService;

    @GetMapping("init/{count}")
    public void init(@PathVariable int count) {
        lotteryService.initLottery(count);
    }

    @GetMapping("get")
    public String get() {
        return lotteryService.getLottery();
    }
}
