package com.cc.springbootredisredpacket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CarlosXiao on 2018/7/3.
 */
@RestController
@RequestMapping("red_packet")
@Slf4j
public class RedPacketController {

    @Resource
    private JedisPool jedisPool;

    @GetMapping("init/{count}/{sum}")
    public Map<String, Object> init(@PathVariable int count , @PathVariable BigDecimal sum) throws JsonProcessingException {
        RedPacket redPacket = new RedPacket(count, sum);
        Map<String, Object> resultMap = new HashMap<>(count);
        ObjectMapper objectMapper = new ObjectMapper();
        for (int i = 0; i < redPacket.getCount(); i++) {
            BigDecimal red = redPacket.nextRedPacket();
            log.info("第{}个红包的金额:{}", i+1, red.toPlainString());
            resultMap.put(String.valueOf(i+1), red.toPlainString());
            Jedis jedis = jedisPool.getResource();
            Map<String, Object> map = new HashMap<>(2);
            map.put("userId", null);
            map.put("monty", red.toPlainString());
            jedis.lpush(RedPacketUtil.RED_PACKET_LIST, objectMapper.writeValueAsString(map));
            jedis.close();
        }
        return resultMap;
    }

    @GetMapping("get")
    public String get(HttpServletRequest request) {
        String userId = request.getSession().getId();
        log.info("userId : {}", userId);
        Jedis jedis = jedisPool.getResource();
        return RedPacketUtil.getRedPacket(jedis, userId);
    }
}
