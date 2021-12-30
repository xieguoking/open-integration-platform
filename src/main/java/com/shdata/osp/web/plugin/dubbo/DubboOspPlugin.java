package com.shdata.osp.web.plugin.dubbo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.shdata.osp.web.plugin.OspPlugin;
import com.shdata.osp.web.strategy.TransformStrategy;
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
import java.util.HashMap;
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
        return "dubbo";
    }


    @Override
    public void execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, TransformStrategy transformStrategy) throws IOException {
        //入参
        Map<String, String> map = new HashMap<>();
        httpServletRequest.getParameterMap().forEach((key, value) -> {
            map.put(key, String.join(",", value));
        });
        String body = JSONUtil.toJsonStr(map);
        Map<String, String> paris = transformStrategy.resolve(httpServletRequest);

        //先手动执行 后续用监听 设置缓存的东西
        dubboRegistryServerSync.getProvider();
        MetaData metaData = dubboRegistryServerSync.getRegistryMetaCache().get(paris.get("interfaceName"));


        //dubbo-service.com.xxx.xx.UserService 后续还得加版本号 group
        GenericService genericService = genericServiceCache.get(paris.get("interfaceName"));
        //加锁 防止重复初始化
        synchronized (Object.class) {
            if (genericService == null) {
                ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
                reference.setInterface(paris.get("interfaceName"));
                reference.setVersion(paris.get("version"));
                reference.setGeneric(true);
                reference.setGroup(paris.get("group"));
                genericService = reference.get();
                genericServiceCache.put(paris.get("interfaceName"), genericService);
            }
        }

        Pair<String[], Object[]> pair;

        String parameterTypes = StrUtil.join(",", metaData.getMethods().get(paris.get("method")).getParameterTypes());
        if (StrUtil.isBlank(parameterTypes) || ParamCheckUtils.dubboBodyIsEmpty(body)) {
            pair = new ImmutablePair<>(new String[]{}, new Object[]{});
        } else {
            pair = BodyParamUtils.buildParameters(body, parameterTypes);
        }

        Object object = genericService.$invoke(paris.get("method"), pair.getLeft(), pair.getRight());
        if (Objects.isNull(object)) {
            object = Constants.DUBBO_RPC_RESULT_EMPTY;
        }
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().println(JSONUtil.toJsonStr(object));
        httpServletResponse.getWriter().flush();
    }
}
