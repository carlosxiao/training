package com.cc.springbootredissubscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by CarlosXiao on 2018/7/1.
 */
public class Receiver {

    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
    private CountDownLatch latch;

    public Receiver(CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(String msg) {
        logger.info("receive msg: {} ", msg);
        latch.countDown();
    }
}
