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
@TableName("OIP_API_VISIT_LOG")
public class ApiVisitLog {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId("ID")
    private BigDecimal id;

    /**
     * ServiceID
     */
    @TableField("ServiceID")
    private String ServiceID;

    /**
     * 服务名称
     */
    @TableField("ServiceName")
    private String ServiceName;

    /**
     * 服务类型
     */
    @TableField("ServiceType")
    private String ServiceType;

    /**
     * 访问IP
     */
    @TableField("REMOTE_IP")
    private String remoteIp;

    /**
     * 访问用户
     */
    @TableField("USERID")
    private String userid;

    /**
     * URL
     */
    @TableField("URL")
    private String url;

    /**
     * 访问时间
     */
    @TableField("VISIT_TIME")
    private LocalDateTime visitTime;

    /**
     * 访问状态 Y：成功 N：失败
     */
    @TableField("SUCCESS")
    private String success;


    public static final String ID = "ID";

    public static final String SERVICEID = "ServiceID";

    public static final String SERVICENAME = "ServiceName";

    public static final String SERVICETYPE = "ServiceType";

    public static final String REMOTE_IP = "REMOTE_IP";

    public static final String USERID = "USERID";

    public static final String URL = "URL";

    public static final String VISIT_TIME = "VISIT_TIME";

    public static final String SUCCESS = "SUCCESS";

}
