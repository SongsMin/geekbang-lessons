package org.geektimes.configuration.microprofile.config;


import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

import java.util.*;

public class JavaConfig implements Config {

    /**
     * 内部可变的集合，不要直接暴露在外面
     */
    private List<ConfigSource> configSources = new LinkedList<>();

    private static Map<Class<?>, Converter> converters = new HashMap<>();
    static {
        converters.put(Boolean.class, Boolean::valueOf);
        converters.put(boolean.class, Boolean::parseBoolean);
        converters.put(Byte.class, Byte::valueOf);
        converters.put(byte.class, Byte::parseByte);
        converters.put(Short.class, Short::valueOf);
        converters.put(short.class, Short::parseShort);
        converters.put(Integer.class, Integer::valueOf);
        converters.put(int.class, Integer::parseInt);
        converters.put(Long.class, Long::valueOf);
        converters.put(long.class, Long::parseLong);
        converters.put(Float.class, Float::valueOf);
        converters.put(float.class, Float::parseFloat);
        converters.put(Double.class, Double::valueOf);
        converters.put(double.class, Double::parseDouble);
        converters.put(String.class, str -> str);
    }

    private static Comparator<ConfigSource> configSourceComparator = new Comparator<ConfigSource>() {
        @Override
        public int compare(ConfigSource o1, ConfigSource o2) {
            return Integer.compare(o2.getOrdinal(), o1.getOrdinal());
        }
    };

    public JavaConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        ServiceLoader<ConfigSource> serviceLoader = ServiceLoader.load(ConfigSource.class, classLoader);
        serviceLoader.forEach(configSources::add);
        // 排序
        configSources.sort(configSourceComparator);
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        // String 转换成目标类型
        return null;
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        return null;
    }

    protected String getPropertyValue(String propertyName) {
        String propertyValue = null;
        for (ConfigSource configSource : configSources) {
            propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null) {
                break;
            }
        }
        return propertyValue;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        T value = getValue(propertyName, propertyType);
        return Optional.ofNullable(value);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return null;
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return Collections.unmodifiableList(configSources);
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
//        return Optional.empty();
        return Optional.ofNullable(converters.get(forType));
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }
}
