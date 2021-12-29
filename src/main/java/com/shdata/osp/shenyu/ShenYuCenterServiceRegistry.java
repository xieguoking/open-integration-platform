package com.shdata.osp.shenyu;

import com.shdata.osp.dto.ServiceConfigDTO;

public interface ShenYuCenterServiceRegistry {


    /**
     * 自定义shenYy 向nacos 注册
     *
     * @param serviceConfigDTO 服务配置
     */
    void register(ServiceConfigDTO serviceConfigDTO);

}
