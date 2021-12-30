package com.shdata.osp.vs;

import com.shdata.osp.spi.VirtualService;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xieguojun
 * @author (2021 / 12 / 24 add by xieguojun)
 * @version 1.0
 * @since 1.0
 */
@Getter
@Setter
public class DefaultVirtualService implements VirtualService {

    public static final String KEY_SERVICE_TYPE = "SHDATA.service.type";
    public static final String KEY_PREFIX = "SHDATA.service.prefix";
    public static final String KEY_SERVICE_STRATEGY = "SHDATA.service.strategy";

    private String service;
    private String serviceName;
    private String ip;
    private int port;
    private Map<String, String> metadata = new HashMap<>();

    public void setServiceType(String serviceType) {
        metadata.put(KEY_SERVICE_TYPE, serviceType);
    }

    public void setTransformStrategy(String transformStrategyName) {
        metadata.put(KEY_SERVICE_STRATEGY, transformStrategyName);
    }


    @Override
    public String getServiceType() {
        return metadata.get(KEY_SERVICE_TYPE);
    }

    public void setPrefix(String prefix) {
        metadata.put(KEY_PREFIX, prefix);
    }

    @Override
    public String getPrefix() {
        return metadata.get(KEY_PREFIX);
    }

}
