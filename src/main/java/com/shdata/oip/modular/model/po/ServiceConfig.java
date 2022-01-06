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
@TableName("OIP_SERVICE_CONFIG")
public class ServiceConfig {

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
     * 服务IP
     */
    @TableField("VIP")
    private String vip;

    /**
     * 服务端口
     */
    @TableField("VPort")
    private int VPort;

    /**
     * 元数据
     */
    @TableField("Metadata")
    private String Metadata;

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


    public static final String ID = "ID";

    public static final String SERVICEID = "ServiceID";

    public static final String SERVICENAME = "ServiceName";

    public static final String SERVICETYPE = "ServiceType";

    public static final String VIP = "VIP";

    public static final String VPORT = "VPort";

    public static final String METADATA = "Metadata";

    public static final String CREATE_BY = "CREATE_BY";

    public static final String UPDATE_BY = "UPDATE_BY";

    public static final String CREATE_TIME = "CREATE_TIME";

    public static final String UPDATE_TIME = "UPDATE_TIME";

}
