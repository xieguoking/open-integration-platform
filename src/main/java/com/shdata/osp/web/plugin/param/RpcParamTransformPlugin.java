package com.shdata.osp.web.plugin.param;

import cn.hutool.json.JSONUtil;
import com.shdata.osp.web.plugin.OspPlugin;
import com.shdata.osp.web.plugin.OspPluginChain;
import com.shdata.osp.web.plugin.base.PluginEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/3
 */

public class RpcParamTransformPlugin implements OspPlugin {

    @Override
    public String named() {
        return PluginEnum.RPC_PARAM_TRANSFORM.getName();
    }

    @Override
    public int getOrder() {
        return PluginEnum.RPC_PARAM_TRANSFORM.getOrder();
    }

    @Override
    public boolean skip(HttpServletRequest httpServletRequest) {
        return false;
    }

    @Override
    public void execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, OspPluginChain chain) throws IOException {

        Map<String, String> map = new HashMap<>();
        httpServletRequest.getParameterMap().forEach((key, value) -> {
            map.put(key, String.join(",", value));
        });
        String body = JSONUtil.toJsonStr(map);

        httpServletRequest.setAttribute("rpc_param", body);
        chain.execute(httpServletRequest, httpServletResponse);

//        Map<String, String> serviceIdMetadata = (Map<String, String>) httpServletRequest.getAttribute("context");
//        if (Objects.nonNull(serviceIdMetadata)) {
//            String mediaType = httpServletRequest.getHeader("Content-Type");
//            if (MediaType.APPLICATION_JSON.toString().equalsIgnoreCase(mediaType)) {
//                body(httpServletRequest, chain);
//            }
//            if (MediaType.APPLICATION_FORM_URLENCODED.toString().equalsIgnoreCase(mediaType)) {
//                formData(httpServletRequest, chain);
//            }
//            query(httpServletRequest, chain);
//        }
//        chain.execute(httpServletRequest, httpServletResponse);
    }

    private void body(HttpServletRequest httpServletRequest, final OspPluginChain chain) {
    }

    private void formData(HttpServletRequest httpServletRequest, final OspPluginChain chain) {

    }

    private void query(HttpServletRequest httpServletRequest, final OspPluginChain chain) {

    }
}
