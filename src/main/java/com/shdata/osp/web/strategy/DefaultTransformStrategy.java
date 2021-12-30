package com.shdata.osp.web.strategy;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/30
 * 默认地址转换策略
 */

public class DefaultTransformStrategy implements TransformStrategy {

    @Override
    public String named() {
        return "default";
    }

    /**
     * 完整的请求路径/dubbo-service/public/v1/com/shdata/a/b/UserService/add
     *
     * @param httpServletRequest
     */
    public Map<String, String> resolve(HttpServletRequest httpServletRequest) {
        return resolveUrlToReferenceRule(httpServletRequest.getRequestURI());
    }

    private Map<String, String> resolveUrlToReferenceRule(final String url) {
        String removePrefixString = StrUtil.removePrefix(url, "/");
        String[] removePrefixStringArr = removePrefixString.split("\\/");
        String serviceId = removePrefixStringArr[0];
        String group = removePrefixStringArr[1];
        String version = removePrefixStringArr[2];
        String method = removePrefixStringArr[removePrefixStringArr.length - 1];

        String interfaceName = Arrays.asList(removePrefixStringArr).stream()
                .filter(x -> !ArrayUtil.contains(new String[]{serviceId, group, version, method}, x))
                .collect(Collectors.joining("."));

        Map<String, String> map = new HashMap();
        map.put("interfaceName", interfaceName);
        map.put("version", "~".equals(version) ? "" : version);
        map.put("group", "~".equals(group) ? "" : group);
        map.put("serviceId", serviceId);
        map.put("method", method);
        return map;
    }
}
