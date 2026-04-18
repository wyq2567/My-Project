package com.ruoyi.product.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 商品管理对象 tb_product
 * 
 * @author ruoyi
 * @date 2026-04-15
 */
public class TbProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品编号 */
    @Excel(name = "商品编号")
    private String productNo;

    /** 序号 */
    @Excel(name = "序号")
    private Long sequenceNo;

    /** 商品一级分类 */
    @Excel(name = "商品一级分类")
    private String parentId;

    /** 商品二级分类 */
    @Excel(name = "商品二级分类")
    private String categoryCode;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String productName;

    /** 是否启用 */
    @Excel(name = "是否启用")
    private Integer isEnabled;

    /** 单位 */
    @Excel(name = "单位")
    private String unitCode;

    /** 记忆最近售价 */
    @Excel(name = "记忆最近售价")
    private Integer rememberLastPrice;

    /** 单位重量 */
    @Excel(name = "单位重量")
    private BigDecimal unitWeight;

    /** 单位体积 */
    @Excel(name = "单位体积")
    private BigDecimal unitVolume;

    /** 图片上传 */
    private String imageCode;

    /** 修改时间 */
    private Date dateTime;

    /** 删除标志: 0-正常 1-已删除 */
    private String delFlg;

    public void setProductNo(String productNo) 
    {
        this.productNo = productNo;
    }

    public String getProductNo() 
    {
        return productNo;
    }

    public void setSequenceNo(Long sequenceNo) 
    {
        this.sequenceNo = sequenceNo;
    }

    public Long getSequenceNo() 
    {
        return sequenceNo;
    }

    public void setParentId(String parentId) 
    {
        this.parentId = parentId;
    }

    public String getParentId() 
    {
        return parentId;
    }

    public void setCategoryCode(String categoryCode) 
    {
        this.categoryCode = categoryCode;
    }

    public String getCategoryCode() 
    {
        return categoryCode;
    }

    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }

    public void setIsEnabled(Integer isEnabled) 
    {
        this.isEnabled = isEnabled;
    }

    public Integer getIsEnabled() 
    {
        return isEnabled;
    }

    public void setUnitCode(String unitCode) 
    {
        this.unitCode = unitCode;
    }

    public String getUnitCode() 
    {
        return unitCode;
    }

    public void setRememberLastPrice(Integer rememberLastPrice) 
    {
        this.rememberLastPrice = rememberLastPrice;
    }

    public Integer getRememberLastPrice() 
    {
        return rememberLastPrice;
    }

    public void setUnitWeight(BigDecimal unitWeight) 
    {
        this.unitWeight = unitWeight;
    }

    public BigDecimal getUnitWeight() 
    {
        return unitWeight;
    }

    public void setUnitVolume(BigDecimal unitVolume) 
    {
        this.unitVolume = unitVolume;
    }

    public BigDecimal getUnitVolume() 
    {
        return unitVolume;
    }

    public void setImageCode(String imageCode) 
    {
        this.imageCode = imageCode;
    }

    public String getImageCode() 
    {
        return imageCode;
    }

    public void setDateTime(Date dateTime) 
    {
        this.dateTime = dateTime;
    }

    public Date getDateTime() 
    {
        return dateTime;
    }

    public void setDelFlg(String delFlg) 
    {
        this.delFlg = delFlg;
    }

    public String getDelFlg() 
    {
        return delFlg;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("productNo", getProductNo())
            .append("sequenceNo", getSequenceNo())
            .append("parentId", getParentId())
            .append("categoryCode", getCategoryCode())
            .append("productName", getProductName())
            .append("isEnabled", getIsEnabled())
            .append("unitCode", getUnitCode())
            .append("rememberLastPrice", getRememberLastPrice())
            .append("unitWeight", getUnitWeight())
            .append("unitVolume", getUnitVolume())
            .append("remark", getRemark())
            .append("imageCode", getImageCode())
            .append("createTime", getCreateTime())
            .append("dateTime", getDateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("delFlg", getDelFlg())
            .toString();
    }
}
