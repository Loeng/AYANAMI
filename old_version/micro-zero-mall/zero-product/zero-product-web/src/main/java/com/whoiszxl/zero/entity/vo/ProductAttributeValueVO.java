package com.whoiszxl.zero.entity.vo;

import com.whoiszxl.zero.bean.AbstractObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品属性值表
 * 
 * @author whoiszxl
 * @email whoiszxl@gmail.com
 * 
 */
@Data
public class ProductAttributeValueVO extends AbstractObject implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private Long id;
	/**
	 * 商品ID
	 */
	private Long productId;
	/**
	 * 属性id
	 */
	private Long attributeId;
	/**
	 * 属性名
	 */
	private String attributeName;
	/**
	 * 属性值
	 */
	private String attributeValue;
	/**
	 * 创建时间
	 */
	private Date createdAt;
	/**
	 * 更新时间
	 */
	private Date updatedAt;

}
