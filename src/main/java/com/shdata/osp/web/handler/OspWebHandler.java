package com.shdata.osp.web.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.shdata.osp.vs.DefaultVirtualService;
import com.shdata.osp.web.plugin.OspPlugin;
import com.shdata.osp.web.strategy.TransformStrategy;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 */
public class OspWebHandler implements OspHandler {

    private DiscoveryClient discoveryClient;

    private Map<String, OspPlugin> plugins;

    private Map<String, TransformStrategy> strategies;

    public OspWebHandler(DiscoveryClient discoveryClient, final List<OspPlugin> plugins, final List<TransformStrategy> strategies) {
        this.discoveryClient = discoveryClient;
        this.plugins = plugins.stream().collect(Collectors.toMap(OspPlugin::named, OspPlugin -> OspPlugin));
        this.strategies = strategies.stream().collect(Collectors.toMap(TransformStrategy::named, TransformStrategy -> TransformStrategy));
    }

    @Override
    public ModelAndView handle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws IOException {
        //获取路径的contextPath 就是虚拟服务servieId
        String removePrefixString = StrUtil.removePrefix(httpServletRequest.getRequestURI(), "/");
        String serviceId = removePrefixString.split("\\/")[0];

        //注册中心的虚拟服务的元数据
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceId);
        Assert.notEmpty(serviceInstanceList, String.format("[%s],虚拟服务不存在请确认!", serviceId));

        Map<String, String> metadata = serviceInstanceList.stream().findFirst().get().getMetadata();
        String keyServiceType = metadata.get(DefaultVirtualService.KEY_SERVICE_TYPE);
        String keyServiceStrategy = metadata.get(DefaultVirtualService.KEY_SERVICE_STRATEGY);
        Assert.notNull(plugins.get(keyServiceType), String.format("[%s],服务类型插件不存在,请求确认下游服务类型", keyServiceType));
        Assert.notNull(strategies.get(keyServiceStrategy), String.format("[%s],接受请求处理协议插件不存在,请确认", keyServiceStrategy));

        plugins.get(keyServiceType).execute(httpServletRequest, httpServletResponse, strategies.get(keyServiceStrategy));
        return null;
    }
}
