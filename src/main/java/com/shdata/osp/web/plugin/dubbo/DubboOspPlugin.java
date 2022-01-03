package com.shdata.osp.web.plugin.dubbo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.shdata.osp.web.plugin.OspPlugin;
import com.shdata.osp.web.plugin.OspPluginChain;
import com.shdata.osp.web.plugin.base.BodyParamUtils;
import com.shdata.osp.web.plugin.base.PluginEnum;
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

public class DubboOspPlugin implements OspPlugin {

    private Map<String, GenericService> genericServiceCache = new ConcurrentHashMap();

    private DubboRegistryServerSync dubboRegistryServerSync;

    public DubboOspPlugin(DubboRegistryServerSync dubboRegistryServerSync) {
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
    public boolean skip(HttpServletRequest httpServletRequest) {
        return false;
    }

    @Override
    public void execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, OspPluginChain ospPluginChain) throws IOException {
        //入参
        String body = (String) httpServletRequest.getAttribute("rpc_param");
        //URL协议
        Map<String, String> paris = (Map<String, String>) httpServletRequest.getAttribute("strategy_rule_paris");

        //先手动执行 后续用监听 设置缓存的东西
        String interfaceName = paris.get("interfaceName");
        String methodName = paris.get("method");
        String groupName = paris.get("group");
        String versionName = paris.get("version");

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

    private MetaData test() {
        return new MetaData();
    }
}
