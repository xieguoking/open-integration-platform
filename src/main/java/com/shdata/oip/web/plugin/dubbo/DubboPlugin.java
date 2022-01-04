package com.shdata.oip.web.plugin.dubbo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.shdata.oip.vs.DefaultVirtualService;
import com.shdata.oip.web.plugin.OspPlugin;
import com.shdata.oip.web.plugin.OspPluginChain;
import com.shdata.oip.web.plugin.base.BodyParamUtils;
import com.shdata.oip.web.plugin.base.OspConstants;
import com.shdata.oip.web.plugin.base.PluginEnum;
import com.shdata.oip.web.plugin.dubbo.meta.DubboRegistryServerSync;
import com.shdata.oip.web.plugin.dubbo.meta.MetaData;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.apache.shenyu.common.constant.Constants;
import org.apache.shenyu.common.utils.ParamCheckUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 */

public class DubboPlugin implements OspPlugin {

    private Map<String, GenericService> genericServiceCache = new ConcurrentHashMap();

    private DubboRegistryServerSync dubboRegistryServerSync;

    public DubboPlugin(DubboRegistryServerSync dubboRegistryServerSync) {
        this.dubboRegistryServerSync = dubboRegistryServerSync;
    }

    @Override
    public String named() {
        return PluginEnum.DUBBO.getName();
    }


    @Override
    public int getOrder() {
        return PluginEnum.DUBBO.getOrder();
    }

    @Override
    public boolean skip(final HttpServletRequest httpServletRequest) {
        return skip(httpServletRequest, DefaultVirtualService.KEY_SERVICE_TYPE, PluginEnum.DUBBO);
    }

    @Override
    public void execute(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final OspPluginChain ospPluginChain) throws IOException {
        //入参
        String body = (String) httpServletRequest.getAttribute(OspConstants.RPC_PARAM_KEY);
        //URL协议
        Map<String, String> paris = (Map<String, String>) httpServletRequest.getAttribute(OspConstants.STRATEGY_RULE_PARIS_KEY);

        //先手动执行 后续用监听 设置缓存的东西
        String interfaceName = paris.get(OspConstants.INTERFACE_NAME_KEY);
        String methodName = paris.get(OspConstants.METHOD_KEY);
        String groupName = paris.get(OspConstants.GROUP_KEY);
        String versionName = paris.get(OspConstants.VERSION_KEY);

        MetaData metaData = dubboRegistryServerSync.getRegistryMetaCache()
                .get(interfaceName) != null ? dubboRegistryServerSync.getRegistryMetaCache().get(interfaceName)
                : dubboRegistryServerSync.getProvider(interfaceName);

        GenericService genericService = genericServiceCache.get(interfaceName);
        synchronized (Object.class) {
            if (genericService == null) {
                ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
                reference.setInterface(interfaceName);
                reference.setVersion(versionName);
                reference.setGeneric(true);
                reference.setGroup(groupName);
                genericService = reference.get();
                genericServiceCache.put(interfaceName, genericService);
            }
        }

        Pair<String[], Object[]> pair;
        String parameterTypes = StrUtil.join(",", metaData.getMethods().get(methodName).getParameterTypes());
        if (StrUtil.isBlank(parameterTypes) || ParamCheckUtils.dubboBodyIsEmpty(body)) {
            pair = new ImmutablePair<>(new String[]{}, new Object[]{});
        } else {
            pair = BodyParamUtils.buildParameters(body, parameterTypes);
        }

        Object object = genericService.$invoke(methodName, pair.getLeft(), pair.getRight());

        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().println(JSONUtil.toJsonStr(Objects.isNull(object) ? Constants.DUBBO_RPC_RESULT_EMPTY : object));
        httpServletResponse.getWriter().flush();
    }
}
