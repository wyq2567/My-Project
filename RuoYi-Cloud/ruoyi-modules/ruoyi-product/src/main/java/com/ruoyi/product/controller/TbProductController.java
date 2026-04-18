package com.ruoyi.product.controller;

import java.util.List;
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
import com.ruoyi.product.domain.TbProduct;
import com.ruoyi.product.service.ITbProductService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.page.TableDataInfo;

/**
 * 商品管理Controller
 * 
 * @author ruoyi
 * @date 2026-04-15
 */
@RestController
@RequestMapping("/product")
public class TbProductController extends BaseController
{
    @Autowired
    private ITbProductService tbProductService;

    /**
     * 查询商品管理列表
     */
    @RequiresPermissions("product:product:list")
    @GetMapping("/list")
    public TableDataInfo list(TbProduct tbProduct)
    {
        startPage();
        List<TbProduct> list = tbProductService.selectTbProductList(tbProduct);
        return getDataTable(list);
    }

    /**
     * 导出商品管理列表
     */
    @RequiresPermissions("product:product:export")
    @Log(title = "商品管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TbProduct tbProduct)
    {
        List<TbProduct> list = tbProductService.selectTbProductList(tbProduct);
        ExcelUtil<TbProduct> util = new ExcelUtil<TbProduct>(TbProduct.class);
        util.exportExcel(response, list, "商品管理数据");
    }

    /**
     * 获取商品管理详细信息
     */
    @RequiresPermissions("product:product:query")
    @GetMapping(value = "/{productNo}")
    public AjaxResult getInfo(@PathVariable("productNo") String productNo)
    {
        return success(tbProductService.selectTbProductByProductNo(productNo));
    }

    /**
     * 新增商品管理
     */
    @RequiresPermissions("product:product:add")
    @Log(title = "商品管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TbProduct tbProduct)
    {
        return toAjax(tbProductService.insertTbProduct(tbProduct));
    }

    /**
     * 修改商品管理
     */
    @RequiresPermissions("product:product:edit")
    @Log(title = "商品管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TbProduct tbProduct)
    {
        return toAjax(tbProductService.updateTbProduct(tbProduct));
    }

    /**
     * 删除商品管理
     */
    @RequiresPermissions("product:product:remove")
    @Log(title = "商品管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{productNos}")
    public AjaxResult remove(@PathVariable String[] productNos)
    {
        return toAjax(tbProductService.deleteTbProductByProductNos(productNos));
    }
}
