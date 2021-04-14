package org.geektimes.cache.serializer;

import javax.cache.CacheException;

public interface Serializer<O, S> {
    S serialize(O object) throws CacheException;
    O deserialize(S serialized) throws CacheException;
}
