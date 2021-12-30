package com.shdata.osp.web.plugin;

import cn.hutool.json.JSONUtil;
import com.shdata.osp.web.strategy.TransformStrategy;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 */

public class DubboOspPlugin implements OspPlugin {


    private Map<String, GenericService> genericServiceCache = new ConcurrentHashMap();

    @Override
    public String named() {
        return "dubbo";
    }


    @Override
    public void execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, TransformStrategy transformStrategy) throws IOException {
        //入参
        String body = httpServletRequest.getParameter(httpServletRequest.getParameterNames().nextElement());
        Map<String, String> paris = transformStrategy.resolve(httpServletRequest);

        GenericService genericService = genericServiceCache.get(paris.get("serviceId"));
        //加锁 防止重复初始化
        synchronized (Object.class) {
            if (genericService == null) {
                ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
                reference.setInterface(paris.get("interfaceName"));
                reference.setVersion(paris.get("version"));
                reference.setGeneric(true);
                reference.setGroup(paris.get("group"));
                genericService = reference.get();
                genericServiceCache.put(paris.get("serviceId"), genericService);
            }
        }

        Object object = genericService.$invoke(paris.get("method"), new String[]{"java.lang.String"}, new String[]{body});
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().println(JSONUtil.toJsonStr(object));
        httpServletResponse.getWriter().flush();
    }


}
