package com.shdata.oip.core.vs;

import cn.hutool.core.date.DateUtil;
import com.shdata.oip.core.common.OipConstants;
import com.shdata.oip.core.spi.VirtualService;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
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


    private String service;
    private String serviceName;
    private String ip;
    private int port;
    private Map<String, String> metadata = new HashMap<>();

    public DefaultVirtualService() {
        getMetadata().put(OipConstants.KEY_SERVICE_REG_TIME, DateUtil.formatDateTime(new Date()));
    }

    public void setServiceType(String serviceType) {
        metadata.put(OipConstants.KEY_SERVICE_TYPE, serviceType);
    }

    public void setTransformStrategy(String transformStrategyName) {
        metadata.put(OipConstants.KEY_SERVICE_STRATEGY, transformStrategyName);
    }

    public void setServiceDesc(String serviceDesc) {
        metadata.put(OipConstants.KEY_SERVICE_DESC, serviceDesc);
    }


    @Override
    public String getServiceType() {
        return metadata.get(OipConstants.KEY_SERVICE_TYPE);
    }

    public void setPrefix(String prefix) {
        metadata.put(OipConstants.KEY_SERVICE_PREFIX, prefix);
    }

    @Override
    public String getPrefix() {
        return metadata.get(OipConstants.KEY_SERVICE_PREFIX);
    }

}
