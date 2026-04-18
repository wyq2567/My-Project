package com.ruoyi.product.service.impl;

import java.util.List;
import com.ruoyi.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.product.mapper.TbProductMapper;
import com.ruoyi.product.domain.TbProduct;
import com.ruoyi.product.service.ITbProductService;

/**
 * 商品管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-15
 */
@Service
public class TbProductServiceImpl implements ITbProductService 
{
    @Autowired
    private TbProductMapper tbProductMapper;

    /**
     * 查询商品管理
     * 
     * @param productNo 商品管理主键
     * @return 商品管理
     */
    @Override
    public TbProduct selectTbProductByProductNo(String productNo)
    {
        return tbProductMapper.selectTbProductByProductNo(productNo);
    }

    /**
     * 查询商品管理列表
     * 
     * @param tbProduct 商品管理
     * @return 商品管理
     */
    @Override
    public List<TbProduct> selectTbProductList(TbProduct tbProduct)
    {
        return tbProductMapper.selectTbProductList(tbProduct);
    }

    /**
     * 新增商品管理
     * 
     * @param tbProduct 商品管理
     * @return 结果
     */
    @Override
    public int insertTbProduct(TbProduct tbProduct)
    {
        tbProduct.setCreateTime(DateUtils.getNowDate());
        return tbProductMapper.insertTbProduct(tbProduct);
    }

    /**
     * 修改商品管理
     * 
     * @param tbProduct 商品管理
     * @return 结果
     */
    @Override
    public int updateTbProduct(TbProduct tbProduct)
    {
        return tbProductMapper.updateTbProduct(tbProduct);
    }

    /**
     * 批量删除商品管理
     * 
     * @param productNos 需要删除的商品管理主键
     * @return 结果
     */
    @Override
    public int deleteTbProductByProductNos(String[] productNos)
    {
        return tbProductMapper.deleteTbProductByProductNos(productNos);
    }

    /**
     * 删除商品管理信息
     * 
     * @param productNo 商品管理主键
     * @return 结果
     */
    @Override
    public int deleteTbProductByProductNo(String productNo)
    {
        return tbProductMapper.deleteTbProductByProductNo(productNo);
    }
}
