package com.ruoyi.product.service;

import java.util.List;

import com.ruoyi.product.domain.TbCategory;
import com.ruoyi.product.vo.TbCategoryVo;

/**
 * 商品分类Service接口
 *
 * @author ruoyi
 * @date 2026-04-15
 */
public interface ITbCategoryService {
    /**
     * 查询商品分类
     *
     * @param parentId 商品分类主键
     * @return 商品分类
     */
    public TbCategory selectTbCategoryByParentId(String parentId);


    /**
     * 查询商品分类列表
     *
     * @param tbCategory 商品分类
     * @return 商品分类集合
     */
    public List<TbCategoryVo> selectTbCategoryList(TbCategory tbCategory);

    /**
     * 查询商品分类信息
     *
     * @param parentId 父类商品id
     * @return 商品分类集合
     */
    public List<TbCategory> selectTbCategoryListByParentId(String parentId);

    /**
     * 新增商品分类
     *
     * @param tbCategory 商品分类
     * @return 结果
     */
    public int insertTbCategory(TbCategory tbCategory);

    /**
     * 修改商品分类
     *
     * @param tbCategory 商品分类
     * @return 结果
     */
    public int updateTbCategory(TbCategory tbCategory);

    /**
     * 批量删除商品分类
     *
     * @param uniqueKeys 需要删除的商品分类主键集合
     * @return 结果
     */
    public int deleteTbCategoryByParentIds(String[] uniqueKeys);

    /**
     * 删除商品分类信息
     *
     * @param parentId 商品分类主键
     * @return 结果
     */
    public int deleteTbCategoryByParentId(String parentId);
}
