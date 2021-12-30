package com.shdata.osp.vs.shenyu.nacos;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.apache.shenyu.common.exception.ShenyuException;
import org.apache.shenyu.common.utils.ContextPathUtils;
import org.apache.shenyu.common.utils.GsonUtils;
import org.apache.shenyu.register.client.api.ShenyuClientRegisterRepository;
import org.apache.shenyu.register.client.nacos.NacosClientRegisterRepository;
import org.apache.shenyu.register.common.config.ShenyuRegisterCenterConfig;
import org.apache.shenyu.register.common.dto.MetaDataRegisterDTO;
import org.apache.shenyu.register.common.dto.URIRegisterDTO;
import org.apache.shenyu.register.common.path.RegisterPathConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 * <p>
 * 固定registerService boolean属性，可每次注册新的客户端 ：原来代码逻辑不变 copy 2.4.1
 * @see NacosClientRegisterRepository
 */
public class ShdataNacosClientRegisterRepository implements ShenyuClientRegisterRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosClientRegisterRepository.class);
    private ConfigService configService;
    private NamingService namingService;
    private final ConcurrentLinkedQueue<String> metadataCache = new ConcurrentLinkedQueue();
    private boolean registerService;

    public ShdataNacosClientRegisterRepository() {
    }

    public void init(ShenyuRegisterCenterConfig config) {
        String serverAddr = config.getServerLists();
        Properties properties = config.getProps();
        Properties nacosProperties = new Properties();
        nacosProperties.put("serverAddr", serverAddr);
        String nameSpace = "nacosNameSpace";
        nacosProperties.put("namespace", properties.getProperty(nameSpace));
        nacosProperties.put("username", properties.getProperty("username", ""));
        nacosProperties.put("password", properties.getProperty("password", ""));
        nacosProperties.put("accessKey", properties.getProperty("accessKey", ""));
        nacosProperties.put("secretKey", properties.getProperty("secretKey", ""));

        try {
            this.configService = ConfigFactory.createConfigService(nacosProperties);
            this.namingService = NamingFactory.createNamingService(nacosProperties);
        } catch (NacosException var7) {
            throw new ShenyuException(var7);
        }
    }

    public void close() {
        try {
            this.configService.shutDown();
            this.namingService.shutDown();
        } catch (NacosException var2) {
            LOGGER.error("NacosClientRegisterRepository close error!", var2);
        }

    }

    public void persistInterface(MetaDataRegisterDTO metadata) {
        String rpcType = metadata.getRpcType();
        String contextPath = ContextPathUtils.buildRealNode(metadata.getContextPath(), metadata.getAppName());
        this.registerConfig(rpcType, contextPath, metadata);
    }

    public void persistURI(URIRegisterDTO registerDTO) {
        String rpcType = registerDTO.getRpcType();
        String contextPath = ContextPathUtils.buildRealNode(registerDTO.getContextPath(), registerDTO.getAppName());
        String host = registerDTO.getHost();
        int port = registerDTO.getPort();
        this.registerService(rpcType, contextPath, host, port, registerDTO);
    }

    private synchronized void registerService(String rpcType, String contextPath, String host, int port, URIRegisterDTO registerDTO) {
        if (!this.registerService) {
//            this.registerService = true; 注释掉可重新注册新的客户端
            Instance instance = new Instance();
            instance.setEphemeral(true);
            instance.setIp(host);
            instance.setPort(port);
            Map<String, String> metadataMap = new HashMap();
            metadataMap.put("contextPath", contextPath);
            metadataMap.put("uriMetadata", GsonUtils.getInstance().toJson(registerDTO));
            instance.setMetadata(metadataMap);
            String serviceName = RegisterPathConstants.buildServiceInstancePath(rpcType);

            try {
                this.namingService.registerInstance(serviceName, instance);
            } catch (NacosException var10) {
                throw new ShenyuException(var10);
            }

            LOGGER.info("register service uri success: {}", serviceName);
        }
    }

    private synchronized void registerConfig(String rpcType, String contextPath, MetaDataRegisterDTO metadata) {
        this.metadataCache.add(GsonUtils.getInstance().toJson(metadata));
        String configName = RegisterPathConstants.buildServiceConfigPath(rpcType, contextPath);

        try {
            String defaultGroup = "DEFAULT_GROUP";
            this.configService.publishConfig(configName, "DEFAULT_GROUP", GsonUtils.getInstance().toJson(this.metadataCache));
        } catch (NacosException var6) {
            throw new ShenyuException(var6);
        }

        LOGGER.info("register metadata success: {}", metadata.getRuleName());
    }
}
