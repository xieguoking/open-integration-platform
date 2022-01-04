package com.shdata.oip.spi;

import java.util.Map;

/**
 * 虚拟服务
 *
 * @author xieguojun
 * @author (2021 / 12 / 24 add by xieguojun)
 * @version 1.0
 * @since 1.0
 */
public interface VirtualService {

    /**
     * 唯一的服务
     *
     * @return
     */
    String getService();

    /**
     * 服务名
     *
     * @return
     */
    String getServiceName();

    /**
     * 服务类型：dubbo、socket、webservice
     *
     * @return
     */
    String getServiceType();

    /**
     * 服务注册地址
     *
     * @return
     */
    String getIp();

    /**
     * 服务注册端口
     *
     * @return
     */
    int getPort();

    /**
     * 服务的元数据
     *
     * @return
     */
    Map<String, String> getMetadata();

    /**
     * URL前缀
     *
     * @return
     */
    String getPrefix();

}
