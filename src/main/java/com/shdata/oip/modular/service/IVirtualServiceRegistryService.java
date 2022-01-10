package com.shdata.oip.modular.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shdata.oip.core.spi.VirtualService;
import com.shdata.oip.modular.model.po.VirtualServiceRegistry;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangwj
 * @since 2022-01-05
 */
public interface IVirtualServiceRegistryService extends IService<VirtualServiceRegistry> {

    /**
     * 虚拟服务上线
     */
    void virtualServiceUp(VirtualService virtualService);

    /**
     * 虚拟服务下线
     */
    void virtualServiceDown(VirtualService virtualService);

    /**
     * 虚拟服务状态
     */
    void virtualServiceStatus(String serviceId, Integer status);
}
