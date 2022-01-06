package com.shdata.oip.modular.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwj
 * @since 2022-01-05
 */
@Data
@TableName("OIP_SERVICE_CONFIG_PLUGINS")
public class ServiceConfigPlugins {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId("PID")
    private Long pid;

    /**
     * 服务标识
     */
    @TableField("SID")
    private Long sid;

    /**
     * 插件类型
     */
    @TableField("PLUGIN_TYPE")
    private String pluginType;

    /**
     * 插件名称
     */
    @TableField("PLUGIN_NAME")
    private String pluginName;

    /**
     * 插件优先级
     */
    @TableField("PLUGIN_PRIORITY")
    private int pluginPriority;

    /**
     * 创建人
     */
    @TableField("CREATE_BY")
    private String createBy;

    /**
     * 更新人
     */
    @TableField("UPDATE_BY")
    private String updateBy;

    /**
     * 创建时间
     */
    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("UPDATE_TIME")
    private LocalDateTime updateTime;


    public static final String PID = "PID";

    public static final String SID = "SID";

    public static final String PLUGIN_TYPE = "PLUGIN_TYPE";

    public static final String PLUGIN_NAME = "PLUGIN_NAME";

    public static final String PLUGIN_PRIORITY = "PLUGIN_PRIORITY";

    public static final String CREATE_BY = "CREATE_BY";

    public static final String UPDATE_BY = "UPDATE_BY";

    public static final String CREATE_TIME = "CREATE_TIME";

    public static final String UPDATE_TIME = "UPDATE_TIME";

}
