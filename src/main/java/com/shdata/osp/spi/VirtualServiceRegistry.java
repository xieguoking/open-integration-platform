package com.shdata.osp.spi;

/**
 * 虚拟服务注册表
 *
 * @author xieguojun
 * @author (2021 / 12 / 24 add by xieguojun)
 * @version 1.0
 * @since 1.0
 */
public interface VirtualServiceRegistry {

    /**
     * 进行服务注册
     *
     * @param service
     */
    void register(VirtualService service);
}
