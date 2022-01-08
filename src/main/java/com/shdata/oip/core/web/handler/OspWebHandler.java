package com.shdata.oip.core.web.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.shdata.oip.core.manage.ServiceInstanceManager;
import com.shdata.oip.core.web.plugin.OspPlugin;
import com.shdata.oip.core.web.plugin.OspPluginChain;
import com.shdata.oip.core.web.plugin.base.OspConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
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

    private ServiceInstanceManager serviceInstanceManager;

    private List<OspPlugin> plugins;


    public OspWebHandler(ServiceInstanceManager serviceInstanceManager, final List<OspPlugin> plugins) {
        this.serviceInstanceManager = serviceInstanceManager;
        this.plugins = plugins;
    }

    @Override
    public ModelAndView handle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws IOException {
        //获取路径的contextPath 就是虚拟服务servieId
        String removePrefixString = StrUtil.removePrefix(httpServletRequest.getRequestURI(), "/");
        String serviceId = removePrefixString.split("\\/")[0];

        //注册中心的虚拟服务的元数据
        ServiceInstance serviceInstance = serviceInstanceManager.getServiceInstanceOne(serviceId);
        Assert.notNull(serviceInstance, String.format("[%s],虚拟服务无实例，请确认!", serviceId));

        Map<String, String> serviceIdMetadata = serviceInstance.getMetadata();
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
