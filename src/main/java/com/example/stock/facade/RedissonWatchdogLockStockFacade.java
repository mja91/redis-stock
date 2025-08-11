package com.example.stock.facade;

import com.example.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonWatchdogLockStockFacade {

    private RedissonClient redissonClient;

    private StockService stockService;

    public RedissonWatchdogLockStockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) {
        RLock lock = redissonClient.getLock(id.toString());
        lock.lock(); // run watchdog

        try {
            stockService.decrease(id, quantity);
        } finally {
            lock.unlock();
        }
    }
}
