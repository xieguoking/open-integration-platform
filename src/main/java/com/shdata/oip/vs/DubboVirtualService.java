package com.shdata.oip.vs;

import javax.annotation.PostConstruct;

/**
 * @author xieguojun
 * @author (2021 / 12 / 24 add by xieguojun)
 * @version 1.0
 * @since 1.0
 */
public class DubboVirtualService extends DefaultVirtualService {

    public static final String KEY_PACKAGE_PREFIX = "SHDATA.dubbo.package.prefix";

    public void setPackagePrefix(String packagePrefix) {
        getMetadata().put(KEY_PACKAGE_PREFIX, packagePrefix);
    }

    public String getPackagePrefix() {
        return getMetadata().get(KEY_PACKAGE_PREFIX);
    }

    @PostConstruct
    public void init() {
        getMetadata().put("preserved.register.source", "SPRING_CLOUD");
    }
}
