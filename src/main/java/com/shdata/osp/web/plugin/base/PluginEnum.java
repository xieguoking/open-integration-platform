package com.shdata.osp.web.plugin.base;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/3
 */

public enum PluginEnum {

    /**
     * Global plugin enum.
     */
    GLOBAL(10, "global"),

    /**
     * STRATEGY_DEFAULT
     */
    STRATEGY_DEFAULT(20, "default"),

    /**
     * Param transform plugin enum.
     */
    RPC_PARAM_TRANSFORM(30, "paramTransform"),

    /**
     * Dubbo plugin enum.
     */
    DUBBO(300, "dubbo");


    private final int order;

    private final String name;

    PluginEnum(int order, String name) {
        this.order = order;
        this.name = name;
    }

    /**
     * get code.
     *
     * @return code code
     */
    public int getOrder() {
        return order;
    }

    /**
     * get name.
     *
     * @return name name
     */
    public String getName() {
        return name;
    }

}
