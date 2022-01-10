package com.shdata.oip.core.vs;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.shdata.oip.core.spi.VirtualService;
import com.shdata.oip.core.spi.VirtualServiceRegistry;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Nacos虚拟服务注册表
 *
 * @author xieguojun
 * @author (2021 / 12 / 24 add by xieguojun)
 * @version 1.0
 * @since 1.0
 */
public class NacosVirtualServiceRegistry implements VirtualServiceRegistry, ApplicationContextAware {

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    private Map<String, NacosRegistration> alreadyRegisterNacos = new ConcurrentHashMap<>();
    private ApplicationContext context;

    @Override
    public void register(VirtualService service) {
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
        alreadyRegisterNacos.put(service.getService(), nacosRegistration);
    }


    @Override
    public void deRegister(String serviceId) {
        NacosRegistration nacosRegistration = alreadyRegisterNacos.get(serviceId);
        //deregister nacos
        if (Objects.nonNull(nacosRegistration)) {
            serviceRegistry.deregister(nacosRegistration);
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
