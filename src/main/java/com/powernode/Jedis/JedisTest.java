package com.powernode.Jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/9/22
 */
public class JedisTest {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.172.18",6379);
        jedis.auth("123456");
        Set<String> keys = jedis.keys("*");
        System.out.println(keys);
        for (String key : keys) {
            String type = jedis.type(key);
            System.out.println(key+":"+type);
        }
//        jedis.rpush("friuts", "peach");
//        System.out.println(jedis.lrange("friuts",0,-1));
//        jedis.rpop("friuts");
//        System.out.println(jedis.lrange("friuts",0,-1));
        System.out.println(jedis.zrangeWithScores("score", 0, -1));
        Set<Tuple> score = jedis.zrangeWithScores("score", 0, -1);
        System.out.println(jedis.hgetAll("student"));
        Map<String, String> stringMap = jedis.hgetAll("student");
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("addr", "上海市");
        stringStringHashMap.put("username", "zs");
        jedis.hmset("student",stringStringHashMap);
        System.out.println(jedis.hgetAll("student"));


    }
}
