package com.ruoyi.product.vo;


import lombok.Data;

import java.util.List;

@Data
public class TbProductImageVo {
    private Integer id;

    private String imageCode;

    private String productName;

    private List<FileInfo> imageUrlList;

    @Data
    public static class FileInfo {
        private String name;
        private String url;
    }
}
