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
@TableName("OIP_SERVICE_CONFIG_METADATA")
public class ServiceConfigMetadata {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId("ID")
    private Long id;

    /**
     * 服务标识
     */
    @TableField("SID")
    private Long sid;

    /**
     * 元数据代码
     */
    @TableField("META_CODE")
    private String metaCode;

    /**
     * 元数据名称
     */
    @TableField("META_NAME")
    private String metaName;

    /**
     * 元数据值
     */
    @TableField("META_VALUE")
    private String metaValue;

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

    public static final String SID = "SID";

    public static final String META_CODE = "META_CODE";

    public static final String META_NAME = "META_NAME";

    public static final String META_VALUE = "META_VALUE";

    public static final String CREATE_BY = "CREATE_BY";

    public static final String UPDATE_BY = "UPDATE_BY";

    public static final String CREATE_TIME = "CREATE_TIME";

    public static final String UPDATE_TIME = "UPDATE_TIME";

}
