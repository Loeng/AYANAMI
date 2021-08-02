package com.whoiszxl.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 转冷钱包记录
 * </p>
 *
 * @author whoiszxl
 * @since 2021-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dc_cold_record")
@ApiModel(value="ColdRecord对象", description="转冷钱包记录")
public class ColdRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "币种ID")
    private Integer currencyId;

    @ApiModelProperty(value = "货币名称")
    private String currencyName;

    @ApiModelProperty(value = "转冷金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "转冷钱包交易hash")
    private String txHash;

    @ApiModelProperty(value = "业务系统中交易或其他绑定的地址")
    private String fromAddress;

    @ApiModelProperty(value = "冷钱包地址")
    private String toAddress;

    @ApiModelProperty(value = "上链时间")
    private Date upchainAt;

    @ApiModelProperty(value = "上链成功时间")
    private Date upchainSuccessAt;

    @ApiModelProperty(value = "上链状态，1：成功 2：失败 3：上链后等待确认中")
    private Boolean upchainStatus;

    @ApiModelProperty(value = "乐观锁")
    private Long version;

    @ApiModelProperty(value = "逻辑删除 1: 已删除， 0: 未删除")
    private Integer isDeleted;

    @ApiModelProperty(value = "创建者")
    private String createdBy;

    @ApiModelProperty(value = "更新者")
    private String updatedBy;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    private Date updatedAt;


}