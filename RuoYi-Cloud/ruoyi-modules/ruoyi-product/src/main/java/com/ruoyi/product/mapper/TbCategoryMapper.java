package com.ruoyi.product.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.product.domain.TbCategory;

/**
 * 商品分类Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-15
 */
public interface TbCategoryMapper extends BaseMapper<TbCategory> {
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
    public List<TbCategory> selectTbCategoryList(TbCategory tbCategory);

    /**
     * 查询商品分类列表
     *
     * @param parentId 商品分类主键
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
     * 删除商品分类
     *
     * @param parentId     商品父分类id
     * @param categoryCode 商品分类code
     * @return 结果
     */
    public int deleteTbCategoryByParentIdAndCode(String parentId, String categoryCode);

    /**
     * 批量删除商品分类
     *
     * @param uniqueKeys 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTbCategoryByUniqueKeys(String[] uniqueKeys);}
