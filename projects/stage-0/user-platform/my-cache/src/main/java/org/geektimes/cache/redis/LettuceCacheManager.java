package org.geektimes.cache.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.RedisCodec;
import org.geektimes.cache.AbstractCacheManager;
import org.geektimes.cache.serializer.ByteArraySerializer;
import org.geektimes.cache.serializer.Serializer;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Properties;

public class LettuceCacheManager extends AbstractCacheManager {
    private final RedisClient redisClient;
    public LettuceCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
        this.redisClient = RedisClient.create(RedisURI.create(uri));
    }

    private static class ByteBufferSerializer<T> implements Serializer<T, ByteBuffer> {
        private final ByteArraySerializer<T> innerSerializer = new ByteArraySerializer<>();
        @Override
        public ByteBuffer serialize(T object) throws CacheException {
            byte[] byteArraySerialized = innerSerializer.serialize(object);
            ByteBuffer byteBuffer = ByteBuffer.allocate(byteArraySerialized.length)
                    .put(byteArraySerialized);
            byteBuffer.flip();
            return byteBuffer;
        }

        @Override
        public T deserialize(ByteBuffer serialized) throws CacheException {
            serialized.flip();
            int len = serialized.limit() - serialized.position();
            byte[] byteArraySerialized = new byte[len];
            for (int i = 0; i < len; i++) {
                byteArraySerialized[i] = serialized.get();
            }
            return innerSerializer.deserialize(byteArraySerialized);
        }

    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache<K, V> doCreateCache(String cacheName, C configuration) {
        Serializer<K, ByteBuffer> keySerializer = new ByteBufferSerializer<>();
        Serializer<V, ByteBuffer> valueSerializer = new ByteBufferSerializer<>();
        return new LettuceCache(this, cacheName, configuration, redisClient.connect(new RedisCodec<K, V>() {
            @Override
            public K decodeKey(ByteBuffer bytes) {
                return keySerializer.deserialize(bytes);
            }

            @Override
            public V decodeValue(ByteBuffer bytes) {
                return valueSerializer.deserialize(bytes);
            }

            @Override
            public ByteBuffer encodeKey(K key) {
                return keySerializer.serialize(key);
            }

            @Override
            public ByteBuffer encodeValue(V value) {
                return valueSerializer.serialize(value);
            }
        }));
    }
}
