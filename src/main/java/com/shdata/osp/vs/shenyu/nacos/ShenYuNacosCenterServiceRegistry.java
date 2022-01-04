package com.shdata.osp.vs.shenyu.nacos;

import com.shdata.osp.dto.ServiceConfigDTO;
import com.shdata.osp.vs.shenyu.ShenYuCenterServiceRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.shenyu.common.enums.RpcTypeEnum;
import org.apache.shenyu.register.client.nacos.NacosClientRegisterRepository;
import org.apache.shenyu.register.common.config.ShenyuRegisterCenterConfig;
import org.apache.shenyu.register.common.dto.MetaDataRegisterDTO;
import org.apache.shenyu.register.common.dto.URIRegisterDTO;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 * <p>
 * shenyu-sdk注册nacos
 */

@Service
@RequiredArgsConstructor
public class ShenYuNacosCenterServiceRegistry implements ShenYuCenterServiceRegistry, ApplicationContextAware {

    private ApplicationContext context;
    private final ShenyuRegisterCenterConfig shenyuRegisterCenterConfig;


    @Override
    public void register(final ServiceConfigDTO serviceConfigDTO) {
        MetaDataRegisterDTO metaDataRegisterDTO = new MetaDataRegisterDTO();
        //对应的是shenYu admin select 的 serviceId 最终网关会获取serviceId获取负载地址
        metaDataRegisterDTO.setAppName(serviceConfigDTO.getServiceId());
        //规则名称
        metaDataRegisterDTO.setRuleName(String.format("/%s/**", serviceConfigDTO.getServiceId()));
        //路径匹配规则
        metaDataRegisterDTO.setPath(String.format("/%s/**", serviceConfigDTO.getServiceId()));
        //选择器的上下文路径
        metaDataRegisterDTO.setContextPath(String.format("/%s", serviceConfigDTO.getServiceId()));
        //规则描述
        metaDataRegisterDTO.setPathDesc(serviceConfigDTO.getServiceDesc());
        //规则匹配shenyu-admin的插件规则，最终会走到SpringCloud网关插件执行
        metaDataRegisterDTO.setRpcType(RpcTypeEnum.SPRING_CLOUD.getName());
        //转发地址IP
        metaDataRegisterDTO.setHost(serviceConfigDTO.getOspIp());
        //端口
        metaDataRegisterDTO.setPort(serviceConfigDTO.getOspPort());
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
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
