package com.ruoyi.product.controller;

import java.util.List;

import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.product.vo.TbCategoryVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.product.domain.TbCategory;
import com.ruoyi.product.service.ITbCategoryService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.page.TableDataInfo;

/**
 * 商品分类Controller
 *
 * @author ruoyi
 * @date 2026-04-15
 */
@RestController
@RequestMapping("/category")
public class TbCategoryController extends BaseController {
    @Autowired
    private ITbCategoryService tbCategoryService;

    /**
     * 查询商品分类列表
     */
    @RequiresPermissions("product:category:list")
    @GetMapping("/list")
    public TableDataInfo list(TbCategory tbCategory) {
        startPage();
        List<TbCategoryVo> list = tbCategoryService.selectTbCategoryList(tbCategory);
        return getDataTable(list);
    }

    /**
     * 获取商品分类详细信息
     */
    @RequiresPermissions("product:category:query")
    @GetMapping(value = "/{parentId}")
    public AjaxResult getInfo(@PathVariable String parentId) {
        return success(tbCategoryService.selectTbCategoryByParentId(parentId));
    }

    /**
     * 获取一级商品分类信息
     */
    @RequiresPermissions("product:category:query")
    @GetMapping(value = "/topLevelList/{parentId}")
    public AjaxResult getInfoList(@PathVariable String parentId) {
        return success(tbCategoryService.selectTbCategoryListByParentId(parentId));
    }

    /**
     * 新增商品分类
     */
    @RequiresPermissions("product:category:add")
    @Log(title = "商品分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TbCategory tbCategory) {
        return toAjax(tbCategoryService.insertTbCategory(tbCategory));
    }

    /**
     * 修改商品分类
     */
    @RequiresPermissions("product:category:edit")
    @Log(title = "商品分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TbCategory tbCategory) {
        return toAjax(tbCategoryService.updateTbCategory(tbCategory));
    }

    /**
     * 删除商品分类
     */
    @RequiresPermissions("product:category:remove")
    @Log(title = "商品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{uniqueKeys}")
    public AjaxResult remove(@PathVariable String[] uniqueKeys) {
        return toAjax(tbCategoryService.deleteTbCategoryByParentIds(uniqueKeys));
    }
}
