package com.ruoyi.product.service;

import com.ruoyi.product.domain.TbProductImage;
import com.ruoyi.product.vo.TbProductImageVo;

import java.util.List;

/**
 * <p>
 * 商品图片表 服务类
 * </p>
 *
 * @author ruoyi
 * @since 2026-04-17
 */
public interface ITbProductImageService {

    List<TbProductImageVo> selectTbProductImageList(TbProductImage tbProductImage);

    int deleteProductImageById(String id);

    int insertProductImage(TbProductImage tbProductImage);
}
