package com.shdata.oip.core.dubbo.po;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/30
 */
@Getter
@Setter
public class Methods {

    private String name;

    private List<String> parameterTypes;

    private String returnType;
}
