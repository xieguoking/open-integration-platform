package com.shdata.oip.modular.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shdata.oip.core.spi.VirtualService;
import com.shdata.oip.modular.model.po.ServiceConfig;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangwj
 * @since 2022-01-05
 */
public interface IServiceConfigService extends IService<ServiceConfig> {

    /**
     * 解析入库
     */
    void analysisIntoDb(VirtualService virtualService);

    /**
     * 读库
     */
    List<VirtualService> analysisReadDb();
}
