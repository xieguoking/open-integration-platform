package com.shdata.oip.core.common;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/6
 */

public class OipConstants {

    public static final String KEY_SERVICE_TYPE = "SHDATA.service.type";
    public static final String KEY_SERVICE_PREFIX = "SHDATA.service.prefix";
    public static final String KEY_SERVICE_STRATEGY = "SHDATA.service.strategy";
    public static final String KEY_SERVICE_DESC = "SHDATA.service.desc";
    public static final String KEY_SERVICE_REG_TIME = "SHDATA.service.reg.time";
    public static final String KEY_PACKAGE_PREFIX = "SHDATA.package.prefix";

    /**
     * 注册状态 0：失败 1：成功 2：未知 ,3:下线
     */
    public static final Integer REGISTRY_STATUS_ERROR = 0;
    public static final Integer REGISTRY_STATUS_SUCCESS = 1;
    public static final Integer REGISTRY_STATUS_UNKNOWN = 2;
    public static final Integer REGISTRY_STATUS_DOWN = 3;
}
