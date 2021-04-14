package org.geektimes.cache.redis;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.geektimes.cache.AbstractCache;
import org.geektimes.cache.ExpirableEntry;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public class LettuceCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {
    private final StatefulRedisConnection<K, V> connection;
    private final RedisCommands<K, V> syncCommands;
    protected LettuceCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration, StatefulRedisConnection<K, V> connection) {
        super(cacheManager, cacheName, configuration);
        this.connection = connection;
        syncCommands = connection.sync();
    }

    @Override
    protected void doClose() {
        connection.close();
    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        return syncCommands.exists(key) > 0L;
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        return ExpirableEntry.of(key, syncCommands.get(key));
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> entry) throws CacheException, ClassCastException {
        syncCommands.set(entry.getKey(), entry.getValue());
    }

    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException {
        ExpirableEntry<K, V> oldEntry = getEntry(key);
        syncCommands.del(key);
        return oldEntry;
    }

    @Override
    protected void clearEntries() throws CacheException {
        keySet().forEach(this::removeEntry);
    }

    @Override
    protected Set<K> keySet() {
        // ??
        return new HashSet<>(syncCommands.keys((K) "*"));
    }
}
