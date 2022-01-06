package com.shdata.oip.core.web.plugin.dubbo.meta;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/30
 * <p>
 * 元数据
 */
@Getter
@Setter
public class MetaData {

    private String interfaceName;

    private String version;

    private String group;

    private String application;

    private Map<String, Methods> methods;
}
