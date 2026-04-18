package com.ruoyi.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.product.domain.TbProductImage;
import com.ruoyi.product.mapper.TbProductImageMapper;
import com.ruoyi.product.service.ITbProductImageService;
import com.ruoyi.product.vo.TbProductImageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品图片表 服务实现类
 * </p>
 *
 * @author ruoyi
 * @since 2026-04-17
 */
@Service
public class TbProductImageServiceImpl implements ITbProductImageService {
    @Autowired
    private TbProductImageMapper tbProductImageMapper;

    @Override
    public List<TbProductImageVo> selectTbProductImageList(TbProductImage tbProductImage) {
        String imageCode = tbProductImage.getImageCode();
        String productName = tbProductImage.getProductName();
        LambdaQueryWrapper<TbProductImage> queryWrapper = Wrappers.<TbProductImage>lambdaQuery();
        List<TbProductImage> tbProductImageDtoList;
        if (StringUtils.isNotEmpty(imageCode)) {
            queryWrapper.eq(TbProductImage::getImageCode, imageCode);
        }
        if (StringUtils.isNotEmpty(productName)) {
            queryWrapper.like(TbProductImage::getProductName, productName);
        }
        queryWrapper.eq(TbProductImage::getDelFlg, "0")
                .orderByAsc(TbProductImage::getImageSeq);

        // 按 imageCode 精确查询
        tbProductImageDtoList = tbProductImageMapper.selectList(queryWrapper);
        return convertToVoList(tbProductImageDtoList);
    }

    @Override
    public int deleteProductImageById(String id) {

        return tbProductImageMapper.deleteById(id);
    }

    @Override
    public int insertProductImage(TbProductImage tbProductImage) {
        TbProductImage dto = new TbProductImage();
        dto.setImageCode(tbProductImage.getImageCode());
        dto.setProductName(tbProductImage.getProductName());
        dto.setImageSeq(getMaxImageSeqByImageCode(tbProductImage.getImageCode()) + 1);
        dto.setImageUrl(tbProductImage.getImageUrl());
        dto.setFileFormat(extractFileFormat(tbProductImage.getFileFormat()));
        return tbProductImageMapper.insert(dto);
    }

    private Integer getMaxImageSeqByImageCode(String imageCode) {
        LambdaQueryWrapper<TbProductImage> queryWrapper = Wrappers.<TbProductImage>lambdaQuery();
        queryWrapper.eq(TbProductImage::getImageCode, imageCode)
                .eq(TbProductImage::getDelFlg, "0")
                .orderByDesc(TbProductImage::getImageSeq)
                .last("LIMIT 1");

        TbProductImage maxRecord = tbProductImageMapper.selectOne(queryWrapper);
        return maxRecord != null ? maxRecord.getImageSeq() : 0;
    }

    private String extractFileFormat(String imageUrl) {
        if (StringUtils.isEmpty(imageUrl)) {
            return "";
        }

        int lastDotIndex = imageUrl.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == imageUrl.length() - 1) {
            return "";
        }

        String format = imageUrl.substring(lastDotIndex + 1);
        int questionMarkIndex = format.indexOf("?");
        if (questionMarkIndex != -1) {
            format = format.substring(0, questionMarkIndex);
        }

        return format.toLowerCase();
    }

    private List<TbProductImageVo> convertToVoList(List<TbProductImage> tbProductImageList) {
        if (tbProductImageList == null || tbProductImageList.isEmpty()) {
            return Collections.emptyList();
        }

        // 按 imageCode 分组
        Map<String, List<TbProductImage>> groupedMap = tbProductImageList.stream()
                .collect(Collectors.groupingBy(TbProductImage::getImageCode));

        // 转换为 TbProductImageVo 列表
        List<TbProductImageVo> voList = new ArrayList<>();
        for (Map.Entry<String, List<TbProductImage>> entry : groupedMap.entrySet()) {
            String imageCode = entry.getKey();
            List<TbProductImage> images = entry.getValue();

            TbProductImageVo vo = new TbProductImageVo();
            vo.setImageCode(imageCode);
            // 将同一 imageCode 的图片转换为 FileInfo 列表
            List<TbProductImageVo.FileInfo> fileInfoList = images.stream()
                    .sorted(Comparator.comparing(TbProductImage::getImageSeq))
                    .map(image -> {
                        TbProductImageVo.FileInfo fileInfo = new TbProductImageVo.FileInfo();
                        vo.setProductName(image.getProductName());
                        fileInfo.setName(String.valueOf(image.getImageSeq()));
                        fileInfo.setUrl(image.getImageUrl());
                        return fileInfo;
                    })
                    .collect(Collectors.toList());

            vo.setImageUrlList(fileInfoList);
            voList.add(vo);
        }

        return voList;
    }
}
