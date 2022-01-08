package com.shdata.oip.core.vs;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.shdata.oip.core.spi.VirtualService;
import com.shdata.oip.core.spi.VirtualServiceRegistry;
import org.apache.commons.lang.StringUtils;
import org.apache.shenyu.common.enums.RpcTypeEnum;
import org.apache.shenyu.register.client.nacos.NacosClientRegisterRepository;
import org.apache.shenyu.register.common.config.ShenyuRegisterCenterConfig;
import org.apache.shenyu.register.common.dto.MetaDataRegisterDTO;
import org.apache.shenyu.register.common.dto.URIRegisterDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 * <p>
 * shenyu-sdk注册nacos
 */

public class ShenYuNacosServiceRegistry implements VirtualServiceRegistry, ApplicationContextAware {

    @Autowired
    private ServiceRegistry serviceRegistry;
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    private ApplicationContext context;
    private ShenyuRegisterCenterConfig shenyuRegisterCenterConfig;

    public ShenYuNacosServiceRegistry(ShenyuRegisterCenterConfig shenyuRegisterCenterConfig) {
        this.shenyuRegisterCenterConfig = shenyuRegisterCenterConfig;
    }

    @Override
    public void register(VirtualService service) {
        MetaDataRegisterDTO metaDataRegisterDTO = new MetaDataRegisterDTO();
        //对应的是shenYu admin select 的 serviceId 最终网关会获取serviceId获取负载地址
        metaDataRegisterDTO.setAppName(service.getService());
        //规则名称
        metaDataRegisterDTO.setRuleName(String.format("/%s/**", service.getService()));
        //路径匹配规则
        metaDataRegisterDTO.setPath(String.format("/%s/**", service.getService()));
        //选择器的上下文路径
        metaDataRegisterDTO.setContextPath(String.format("/%s", service.getService()));
        //规则描述
        metaDataRegisterDTO.setPathDesc(service.getServiceName());
        //规则匹配shenyu-admin的插件规则，最终会走到SpringCloud网关插件执行
        metaDataRegisterDTO.setRpcType(RpcTypeEnum.SPRING_CLOUD.getName());
        //转发地址IP
        metaDataRegisterDTO.setHost(service.getIp());
        //端口
        metaDataRegisterDTO.setPort(service.getPort());
        //规则需要开启
        metaDataRegisterDTO.setEnabled(true);
        //还没用到
        metaDataRegisterDTO.setRegisterMetaData(false);

        URIRegisterDTO urlRegisterDTO = URIRegisterDTO.transForm(metaDataRegisterDTO);

        NacosClientRegisterRepository nacosClientRegisterRepository = new NacosClientRegisterRepository();
        nacosClientRegisterRepository.init(shenyuRegisterCenterConfig);

        //注册服务URL，对应的是nacos 服务发现 和 shenyu admin选择器
        nacosClientRegisterRepository.persistURI(urlRegisterDTO);
        //注册元数据，对应的是nacos 配置文件 和 shenyu admin选择器的规则
        nacosClientRegisterRepository.persistInterface(metaDataRegisterDTO);
        //nacos 注册中心注册
        this.nacosRegistry(service);
    }

    /**
     * 这里nacosClientRegisterRepository.persistURI，它只注册一次，又不想引用 NacosVirtualServiceRegistry.class
     * 就再写一次吧
     */
    private void nacosRegistry(VirtualService service) {

        NacosDiscoveryProperties nacosDiscoveryProperties = new NacosDiscoveryProperties();
        BeanUtils.copyProperties(this.nacosDiscoveryProperties, nacosDiscoveryProperties);

        if (service.getPort() != -1) {
            nacosDiscoveryProperties.setPort(service.getPort());
        }
        if (StringUtils.isNotBlank(service.getIp())) {
            nacosDiscoveryProperties.setIp(service.getIp());
        }

        final String serviceName;
        if (StringUtils.isNotBlank(service.getServiceName())) {
            serviceName = service.getServiceName();
        } else {
            serviceName = service.getService();
        }
        nacosDiscoveryProperties.setService(service.getService());
        nacosDiscoveryProperties.setLogName(serviceName);
        if (nacosDiscoveryProperties.getMetadata() == null) {
            nacosDiscoveryProperties.setMetadata(service.getMetadata());
        } else {
            nacosDiscoveryProperties.getMetadata().putAll(service.getMetadata());
        }

        NacosRegistration nacosRegistration = new NacosRegistration(nacosDiscoveryProperties, context);

        serviceRegistry.register(nacosRegistration);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
