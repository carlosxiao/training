package com.cc.springbootredisredpacket;

import redis.clients.jedis.Jedis;

/**
 * Created by CarlosXiao on 2018/7/3.
 */
public class RedPacketUtil {

    static final String RED_PACKET_LIST = "RED_PACKET_LIST";
    static final String CONSUMED_RED_PACKET_LIST = "CONSUMED_RED_PACKET_LIST";
    static final String CONSUMED_RED_PACKET_MAP = "CONSUMED_RED_PACKET_MAP";

    static final String LUA_SCRIPT =
            "if redis.call('hexists', KEYS[3], KEYS[4]) ~= 0 then\n" +
            "\treturn nil\n" +
            "else\n" +
            "\tlocal redPacket = redis.call('rpop', KEYS[1]);\n" +
            //"\tprint('redPacket:', redPacket);\n" +
            "\tif redPacket then\n" +
            "\tlocal x = cjson.decode(redPacket);\n" +
            "\tx['userId'] = KEYS[4];\n" +
            "\tlocal re = cjson.encode(x);\n" +
            "\tredis.call('hset', KEYS[3], KEYS[4], KEYS[4]);\n" +
            "\tredis.call('lpush', KEYS[2], re);\n" +
            "\treturn re;\n" +
            "\tend\n" +
            "end\n" +
            "return nil";

    public static String getRedPacket(Jedis jedis, String userId) {
        jedis.scriptLoad(LUA_SCRIPT);
        Object object = jedis.eval(LUA_SCRIPT, 4, RED_PACKET_LIST, CONSUMED_RED_PACKET_LIST, CONSUMED_RED_PACKET_MAP, userId);
        if (null == object) {
            throw new RuntimeException("sold out");
        }
        return object.toString();
    }
}
