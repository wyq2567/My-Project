package com.ruoyi.product.service;

import java.util.List;
import com.ruoyi.product.domain.TbProduct;

/**
 * 商品管理Service接口
 * 
 * @author ruoyi
 * @date 2026-04-15
 */
public interface ITbProductService 
{
    /**
     * 查询商品管理
     * 
     * @param productNo 商品管理主键
     * @return 商品管理
     */
    public TbProduct selectTbProductByProductNo(String productNo);

    /**
     * 查询商品管理列表
     * 
     * @param tbProduct 商品管理
     * @return 商品管理集合
     */
    public List<TbProduct> selectTbProductList(TbProduct tbProduct);

    /**
     * 新增商品管理
     * 
     * @param tbProduct 商品管理
     * @return 结果
     */
    public int insertTbProduct(TbProduct tbProduct);

    /**
     * 修改商品管理
     * 
     * @param tbProduct 商品管理
     * @return 结果
     */
    public int updateTbProduct(TbProduct tbProduct);

    /**
     * 批量删除商品管理
     * 
     * @param productNos 需要删除的商品管理主键集合
     * @return 结果
     */
    public int deleteTbProductByProductNos(String[] productNos);

    /**
     * 删除商品管理信息
     * 
     * @param productNo 商品管理主键
     * @return 结果
     */
    public int deleteTbProductByProductNo(String productNo);
}
