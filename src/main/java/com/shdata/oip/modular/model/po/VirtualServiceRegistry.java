package com.shdata.oip.modular.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwj
 * @since 2022-01-05
 */
@Data
@TableName("OIP_VIRTUAL_SERVICE_REGITRY")
public class VirtualServiceRegistry {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId("ID")
    private Long id;

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
     * IP
     */
    @TableField("IP")
    private String ip;

    /**
     * PORT
     */
    @TableField("PORT")
    private int port;

    /**
     * METADATA
     */
    @TableField("METADATA")
    private String metadata;

    /**
     * 注册中心地址
     */
    @TableField("ADDRESS")
    private String address;

    /**
     * 注册时间
     */
    @TableField("REG_TIME")
    private Date regTime;

    /**
     * 检测时间
     */
    @TableField("LAST_TIME")
    private Date lastTime;

    /**
     * 注册状态 0：失败 1：成功 2：未知
     */
    @TableField("STATUS")
    private Integer status;


    public static final String ID = "ID";

    public static final String SERVICEID = "ServiceID";

    public static final String SERVICENAME = "ServiceName";

    public static final String SERVICETYPE = "ServiceType";

    public static final String IP = "IP";

    public static final String PORT = "PORT";

    public static final String METADATA = "METADATA";

    public static final String ADDRESS = "ADDRESS";

    public static final String REG_TIME = "REG_TIME";

    public static final String LAST_TIME = "LAST_TIME";

    public static final String STATUS = "STATUS";

}
