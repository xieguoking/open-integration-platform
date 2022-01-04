package com.shdata.oip.vs.shenyu;

import com.shdata.oip.dto.ServiceConfigDTO;

public interface ShenYuCenterServiceRegistry {


    /**
     * 自定义shenYy 向注册中心 注册
     *
     * @param serviceConfigDTO 服务配置
     */
    void register(ServiceConfigDTO serviceConfigDTO);

}
