package com.ruoyi.product.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商品分类对象 tb_category
 *
 * @author ruoyi
 * @date 2026-04-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_category")
public class TbCategory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 父分类编码
     */
    private String parentId;

    /**
     * 父分类名称
     */
    private String parentName;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类层级
     */
    private Long level;

    /**
     * 排序顺序
     */
    private Long sortOrder;

}
