package com.shdata.oip.modular.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwj
 * @since 2022-01-05
 */
@Data
@TableName("OIP_API_ARGS")
public class ApiArgs {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId("APIID")
    private Long apiid;

    /**
     * ServiceID
     */
    @TableField("ServiceID")
    private String serviceID;

    /**
     * 服务标识
     */
    @TableField("SID")
    private Long sid;

    /**
     * 参数名称
     */
    @TableField("ARG_NAME")
    private String argName;

    /**
     * 参数描述
     */
    @TableField("ARG_DESC")
    private String argDesc;

    /**
     * 参数类型
     */
    @TableField("ARG_TYPE")
    private String argType;

    /**
     * 参数顺序
     */
    @TableField("ARG_ORDER")
    private int argOrder;


    public static final String APIID = "APIID";

    public static final String SERVICEID = "ServiceID";

    public static final String SID = "SID";

    public static final String ARG_NAME = "ARG_NAME";

    public static final String ARG_DESC = "ARG_DESC";

    public static final String ARG_TYPE = "ARG_TYPE";

    public static final String ARG_ORDER = "ARG_ORDER";

}
