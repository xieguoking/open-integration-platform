package com.shdata.osp.vs.shenyu;

import com.shdata.osp.dto.ServiceConfigDTO;

public interface ShenYuCenterServiceRegistry {


    /**
     * 自定义shenYy 向注册中心 注册
     *
     * @param serviceConfigDTO 服务配置
     */
    void register(ServiceConfigDTO serviceConfigDTO);

}
