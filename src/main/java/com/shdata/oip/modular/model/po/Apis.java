package com.shdata.oip.modular.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwj
 * @since 2022-01-05
 */
@Data
@TableName("OIP_APIS")
public class Apis {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId("APIID")
    private BigDecimal apiid;

    /**
     * ServiceID
     */
    @TableField("ServiceID")
    private String serviceID;

    /**
     * 服务标识
     */
    @TableField("SID")
    private BigDecimal sid;

    @TableField("API_NAME")
    private String apiName;

    @TableField("API_URL")
    private String apiUrl;

    /**
     * 接口状态 0：下线 1：上线 2：未知
     */
    @TableField("API_STATUS")
    private String apiStatus;


    public static final String APIID = "APIID";

    public static final String SERVICEID = "ServiceID";

    public static final String SID = "SID";

    public static final String API_NAME = "API_NAME";

    public static final String API_URL = "API_URL";

    public static final String API_STATUS = "API_STATUS";

}
