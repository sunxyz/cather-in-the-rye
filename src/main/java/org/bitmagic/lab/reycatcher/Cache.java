package org.bitmagic.lab.reycatcher;

import lombok.Value;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author yangrd
 */
public class Cache<K, V> {

    private final ConcurrentHashMap<K, CacheEntry> cache = new ConcurrentHashMap<>();

    private long expireMillisecond = 5000;

    {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        }, expireMillisecond, expireMillisecond, TimeUnit.MILLISECONDS);
    }

    public Cache() {
    }

    public Cache(long expireMillisecond) {
        this.expireMillisecond = expireMillisecond;
    }

    public V get(K key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.getValue();
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry(value));
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    @Value
    class CacheEntry {
        V value;
        long createTime;

        public CacheEntry(V value) {
            this.value = value;
            this.createTime = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - createTime > expireMillisecond;
        }
    }
}
