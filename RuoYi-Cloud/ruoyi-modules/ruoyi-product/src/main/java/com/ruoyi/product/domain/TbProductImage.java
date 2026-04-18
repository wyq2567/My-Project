package com.ruoyi.product.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品图片表
 * </p>
 *
 * @author ruoyi
 * @since 2026-04-17
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("tb_product_image")
public class TbProductImage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商品图片编号: T+商品编号+序号, 例如T20250115001001
     */
    private String imageCode;

    /**
     * 商品图片编号: T+商品编号+序号, 例如T20250115001001
     */
    private String productName;

    /**
     * 图片序号: 同一商品下的图片排序序号, 从1开始
     */
    private Integer imageSeq;

    /**
     * 图片URL: 图片存储地址
     */
    private String imageUrl;

    /**
     * 文件格式: jpg/png/webp等
     */
    private String fileFormat;

}
