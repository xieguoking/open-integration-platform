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
     *
     * @param virtualService
     */
    void VirtualServiceUp(VirtualService virtualService);
}
