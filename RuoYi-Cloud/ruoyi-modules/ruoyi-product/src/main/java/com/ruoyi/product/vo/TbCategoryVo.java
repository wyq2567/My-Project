package com.ruoyi.product.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TbCategoryVo {

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

    private String  uniqueKey;
    private List<TbCategoryVo> children = new ArrayList<>();
}
