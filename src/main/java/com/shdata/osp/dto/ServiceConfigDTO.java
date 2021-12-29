package com.shdata.osp.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 * <p>
 * 服务配置入参
 */
@Setter
@Getter
public class ServiceConfigDTO {


    /**
     * 服务类型:dubbo|socket|cics|webservice
     */
    @NotBlank
    private String serviceType;


    /**
     * 服务名称
     */
    @NotBlank
    private String serviceName;


    /**
     * springCloud 注册的服务名|也作为shenyu-admin的contextPath
     */
    @NotBlank
    private String serviceId;


    /**
     * 服务描述
     */
    private String serviceDesc;


    /**
     * 服务转换策略
     */
    private String serviceConvStrategy;


    /**
     * 开放服务支持平台iP，也可以是负载的IP
     */
    @NotBlank
    private String ospIp;


    /**
     * 开放服务支持平台端口
     */
    @NotNull
    private Integer ospPort;

}
