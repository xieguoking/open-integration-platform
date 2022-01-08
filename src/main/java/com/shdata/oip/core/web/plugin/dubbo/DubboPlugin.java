package com.shdata.oip.core.web.plugin.dubbo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.shdata.oip.core.common.OipConstants;
import com.shdata.oip.core.dubbo.DubboMetaDataManager;
import com.shdata.oip.core.web.plugin.OspPlugin;
import com.shdata.oip.core.web.plugin.OspPluginChain;
import com.shdata.oip.core.web.plugin.base.BodyParamUtils;
import com.shdata.oip.core.web.plugin.base.OspConstants;
import com.shdata.oip.core.web.plugin.base.PluginEnum;
import com.shdata.oip.core.web.plugin.dubbo.cache.DubboConfigCache;
import com.shdata.oip.core.dubbo.po.MetaData;
import org.apache.commons.lang3.StringUtils;
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

    private DubboMetaDataManager dubboMetaDataManager;

    public DubboPlugin(DubboMetaDataManager dubboMetaDataManager) {
        this.dubboMetaDataManager = dubboMetaDataManager;
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
        return skip(httpServletRequest, OipConstants.KEY_SERVICE_TYPE, PluginEnum.DUBBO);
    }

    @Override
    public void execute(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final OspPluginChain ospPluginChain) throws IOException {
        //入参
        String body = (String) httpServletRequest.getAttribute(OspConstants.RPC_PARAM_KEY);
        //URL协议
        Map<String, String> paris = (Map<String, String>) httpServletRequest.getAttribute(OspConstants.STRATEGY_RULE_PARIS_KEY);

        String interfaceName = paris.get(OspConstants.INTERFACE_NAME_KEY);
        String methodName = paris.get(OspConstants.METHOD_KEY);
        String groupName = paris.get(OspConstants.GROUP_KEY);
        String versionName = paris.get(OspConstants.VERSION_KEY);
        String interfaceMergerKey = String.format("%s:%s:%s", interfaceName, versionName, groupName);

        //获取元数据：最最最最最最需要优化的一个点
        MetaData metaData = dubboMetaDataManager.getMetaData(interfaceMergerKey);

        ReferenceConfig<GenericService> reference = DubboConfigCache.getInstance().get(interfaceMergerKey);
        if (Objects.isNull(reference) || StringUtils.isEmpty(reference.getInterface())) {
            DubboConfigCache.getInstance().invalidate(interfaceMergerKey);
            reference = DubboConfigCache.getInstance().initRef(metaData);
        }

        Pair<String[], Object[]> pair;
        String parameterTypes = StrUtil.join(",", metaData.getMethods().get(methodName).getParameterTypes());
        if (StrUtil.isBlank(parameterTypes) || ParamCheckUtils.dubboBodyIsEmpty(body)) {
            pair = new ImmutablePair<>(new String[]{}, new Object[]{});
        } else {
            pair = BodyParamUtils.buildParameters(body, parameterTypes);
        }

        GenericService genericService = reference.get();
        Object object = genericService.$invoke(methodName, pair.getLeft(), pair.getRight());

        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().println(JSONUtil.toJsonStr(Objects.isNull(object) ? Constants.DUBBO_RPC_RESULT_EMPTY : object));
        httpServletResponse.getWriter().flush();
    }
}
