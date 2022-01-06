package com.shdata.oip.core.web.plugin.dubbo.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.shdata.oip.core.web.plugin.dubbo.meta.MetaData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.apache.shenyu.common.exception.ShenyuException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


/**
 * The type Application config cache.
 */
public final class DubboConfigCache {

    private static final Logger LOG = LoggerFactory.getLogger(DubboConfigCache.class);

    /**
     * cache max count.
     */
    private static final int CACHE_MAX_COUNT = 1000;

    private final LoadingCache<String, ReferenceConfig<GenericService>> cache = CacheBuilder.newBuilder()
            .maximumSize(CACHE_MAX_COUNT)
            .removalListener((RemovalListener<Object, ReferenceConfig<GenericService>>) notification -> {
                ReferenceConfig<GenericService> config = notification.getValue();
                if (Objects.nonNull(config)) {
                    try {
                        Field field = FieldUtils.getDeclaredField(config.getClass(), "ref", true);
                        field.set(config, null);
                    } catch (NullPointerException | IllegalAccessException e) {
                        LOG.error("modify ref have exception", e);
                    }
                }
            })
            .build(new CacheLoader<String, ReferenceConfig<GenericService>>() {
                @Override
                @Nonnull
                public ReferenceConfig<GenericService> load(@Nonnull final String key) {
                    return new ReferenceConfig<>();
                }
            });


    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static DubboConfigCache getInstance() {
        return ApplicationConfigCacheInstance.INSTANCE;
    }


    /**
     * Init ref reference config.
     *
     * @param metaData the meta data
     * @return the reference config
     */
    public ReferenceConfig<GenericService> initRef(final MetaData metaData) {
        try {
            ReferenceConfig<GenericService> referenceConfig = cache.get(metaData.getInterfaceName());
            if (StringUtils.isNoneBlank(referenceConfig.getInterface())) {
                return referenceConfig;
            }
        } catch (ExecutionException e) {
            LOG.error("init dubbo ref exception", e);
        }
        return build(metaData);
    }

    /**
     * Build reference config.
     *
     * @param metaData the meta data
     * @return the reference config
     */
    @SuppressWarnings("deprecation")
    public ReferenceConfig<GenericService> build(final MetaData metaData) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();

        reference.setInterface(metaData.getInterfaceName());
        reference.setVersion(metaData.getVersion());
        reference.setGeneric(true);
        reference.setGroup(metaData.getGroup());

        try {
            Object obj = reference.get();
            if (Objects.nonNull(obj)) {
                LOG.info("init apache dubbo reference success there meteData is :{}", metaData);
                cache.put(String.format("%s:%s:%s", metaData.getInterfaceName(), metaData.getVersion(), metaData.getGroup()), reference);
            }
        } catch (Exception e) {
            LOG.error("init apache dubbo reference exception", e);
        }
        return reference;
    }

    /**
     * Get reference config.
     *
     * @param interfaceMergerKey the interfaceName
     * @return the reference config
     */
    public ReferenceConfig<GenericService> get(final String interfaceMergerKey) {
        try {
            return cache.get(interfaceMergerKey);
        } catch (ExecutionException e) {
            throw new ShenyuException(e.getCause());
        }
    }

    /**
     * Invalidate.
     *
     * @param interfaceMergerKey the interfaceMergerKey
     */
    public void invalidate(final String interfaceMergerKey) {
        cache.invalidate(interfaceMergerKey);
    }

    /**
     * Invalidate all.
     */
    public void invalidateAll() {
        cache.invalidateAll();
    }

    /**
     * The type Application config cache instance.
     */
    static final class ApplicationConfigCacheInstance {
        /**
         * The Instance.
         */
        static final DubboConfigCache INSTANCE = new DubboConfigCache();

        private ApplicationConfigCacheInstance() {

        }
    }
}
