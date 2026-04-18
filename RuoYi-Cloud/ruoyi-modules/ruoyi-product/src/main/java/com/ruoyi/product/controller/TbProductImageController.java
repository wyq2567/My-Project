package com.ruoyi.product.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.product.domain.TbProductImage;
import com.ruoyi.product.service.ITbProductImageService;
import com.ruoyi.product.vo.TbProductImageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品图片表 前端控制器
 * </p>
 *
 * @author ruoyi
 * @since 2026-04-17
 */
@RestController
@RequestMapping("/productImage")
public class TbProductImageController extends BaseController {

    @Autowired
    private ITbProductImageService tbProductImageService;

    /**
     * 查询商品分类列表
     */
    @RequiresPermissions("product:productImage:list")
    @GetMapping("/list")
    public TableDataInfo list(TbProductImage tbProductImage) {
        startPage();
        List<TbProductImageVo> list = tbProductImageService.selectTbProductImageList(tbProductImage);
        return getDataTable(list);
    }

    /**
     * 新增商品图片
     */
    @RequiresPermissions("product:productImage:add")
    @Log(title = "商品分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TbProductImage tbProductImage) {
        return toAjax(tbProductImageService.insertProductImage(tbProductImage));
    }

    /**
     * 删除商品图片
     */
    @RequiresPermissions("product:productImage:remove")
    @Log(title = "商品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        return toAjax(tbProductImageService.deleteProductImageById(id));
    }

}
