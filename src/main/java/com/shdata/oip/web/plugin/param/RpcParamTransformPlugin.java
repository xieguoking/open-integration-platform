package com.shdata.oip.web.plugin.param;

import cn.hutool.json.JSONUtil;
import com.shdata.oip.vs.DefaultVirtualService;
import com.shdata.oip.web.plugin.OspPlugin;
import com.shdata.oip.web.plugin.OspPluginChain;
import com.shdata.oip.web.plugin.base.OspConstants;
import com.shdata.oip.web.plugin.base.PluginEnum;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
    public boolean skip(final HttpServletRequest httpServletRequest) {
        return skip(httpServletRequest, DefaultVirtualService.KEY_SERVICE_TYPE, PluginEnum.DUBBO);
    }

    @Override
    public void execute(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final OspPluginChain chain) throws IOException {
        String mediaType = httpServletRequest.getHeader("Content-Type");
        if (MediaType.APPLICATION_JSON.toString().equalsIgnoreCase(mediaType)) {
            body(httpServletRequest);
        } else {
            formData(httpServletRequest);
        }
        chain.execute(httpServletRequest, httpServletResponse);
    }

    @SneakyThrows
    private void body(HttpServletRequest httpServletRequest) {
        ServletInputStream inputStream = httpServletRequest.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bfReader = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bfReader.readLine()) != null) {
            sb.append(line);
        }
        httpServletRequest.setAttribute(OspConstants.RPC_PARAM_KEY, sb.toString());
    }

    private void formData(HttpServletRequest httpServletRequest) {
        Map<String, String> map = new HashMap<>();
        httpServletRequest.getParameterMap().forEach((key, value) -> {
            map.put(key, String.join(",", value));
        });
        String body = JSONUtil.toJsonStr(map);
        httpServletRequest.setAttribute(OspConstants.RPC_PARAM_KEY, body);
    }
}
