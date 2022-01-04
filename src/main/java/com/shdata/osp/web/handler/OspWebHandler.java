package com.shdata.osp.web.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.shdata.osp.web.plugin.OspPlugin;
import com.shdata.osp.web.plugin.OspPluginChain;
import com.shdata.osp.web.plugin.base.OspConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 */
@Slf4j
public class OspWebHandler implements OspHandler {

    private DiscoveryClient discoveryClient;

    private List<OspPlugin> plugins;


    public OspWebHandler(DiscoveryClient discoveryClient, final List<OspPlugin> plugins) {
        this.discoveryClient = discoveryClient;
        this.plugins = plugins;
    }

    @Override
    public ModelAndView handle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws IOException {
        //获取路径的contextPath 就是虚拟服务servieId
        String removePrefixString = StrUtil.removePrefix(httpServletRequest.getRequestURI(), "/");
        String serviceId = removePrefixString.split("\\/")[0];

        //注册中心的虚拟服务的元数据
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceId);
        Assert.notEmpty(serviceInstanceList, String.format("[%s],虚拟服务不存在请确认!", serviceId));

        Map<String, String> serviceIdMetadata = serviceInstanceList.stream().findFirst().get().getMetadata();
        httpServletRequest.setAttribute(OspConstants.OSP_CONTEXT, serviceIdMetadata);

        new DefaultOspChain(plugins).execute(httpServletRequest, httpServletResponse);
        return null;
    }

    private static class DefaultOspChain implements OspPluginChain {

        private int index;

        private final List<OspPlugin> plugins;

        public DefaultOspChain(final List<OspPlugin> plugins) {
            this.plugins = plugins;
        }

        @SneakyThrows
        @Override
        public void execute(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
            if (this.index < plugins.size()) {
                OspPlugin plugin = plugins.get(this.index++);
                boolean skip = plugin.skip(httpServletRequest);
                if (skip) {
                    this.execute(httpServletRequest, httpServletResponse);
                }
                plugin.execute(httpServletRequest, httpServletResponse, this);
            }
        }
    }

}
