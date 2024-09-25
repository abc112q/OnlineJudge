package com.example.ojbackendquestionservice.manager;

import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Ariel
 * 专门提供限流服务
 * 底层是令牌桶算法
 */

@Slf4j
@Service
public class RedissonLimiter {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 进行限流
     * @param key
     * @return
     */
    public boolean doRateLimit(String key){
        // 创建一个限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // 每秒最多访问两次  OVERALL代表对整体流量进行限流 控制系统在单位时间内的总请求数
        final boolean setRate = rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        if(setRate){
            log.info("限流成功");
            log.info("init rate = {}, interval = {}", rateLimiter.getConfig().getRate(), rateLimiter.getConfig().getRateInterval());
        }
        // 每来一个请求，获取一个令牌
        final boolean b = rateLimiter.tryAcquire(1);
        return b;
    }

}
