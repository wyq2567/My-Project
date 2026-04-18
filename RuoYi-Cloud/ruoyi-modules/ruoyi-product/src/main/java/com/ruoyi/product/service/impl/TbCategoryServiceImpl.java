package com.ruoyi.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.product.domain.TbCategory;
import com.ruoyi.product.mapper.TbCategoryMapper;
import com.ruoyi.product.service.ITbCategoryService;
import com.ruoyi.product.vo.TbCategoryVo;
import com.ruoyi.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品分类Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-15
 */
@Service
public class TbCategoryServiceImpl implements ITbCategoryService {
    @Autowired
    private TbCategoryMapper tbCategoryMapper;

    @Override
    public TbCategory selectTbCategoryByParentId(String uniqueKey) {
        if (StringUtils.isEmpty(uniqueKey) || !uniqueKey.contains("-")) {
            throw new IllegalArgumentException("uniqueKey格式不正确，应为：parentId-categoryCode");
        }

        String[] parts = uniqueKey.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("uniqueKey格式不正确，应为：parentId-categoryCode");
        }

        String parentId = parts[0];
        String categoryCode = parts[1];
        return tbCategoryMapper.selectOne(Wrappers.<TbCategory>lambdaQuery().eq(TbCategory::getCategoryCode, categoryCode).eq(TbCategory::getParentId, parentId));
    }


    /**
     * 查询商品分类列表
     * <p>
     * 业务逻辑：
     * 1. 查询所有符合条件的分类数据
     * 2. 如果查询结果中没有顶级分类（level=1），则自动补充对应的父级分类数据
     * 3. 将分类数据转换为VO对象，并构建父子层级关系
     *
     * @param tbCategory 商品分类查询条件
     * @return 商品分类VO列表（树形结构）
     */
    @Override
    public List<TbCategoryVo> selectTbCategoryList(TbCategory tbCategory) {
        // 查询分类列表
        List<TbCategory> list = tbCategoryMapper.selectTbCategoryList(tbCategory);

        // 空值检查，避免后续处理空集合
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        // 补充父级分类数据（如果列表中缺少顶级分类）
        supplementParentCategories(list);

        // 转换为VO列表并构建树形结构
        return convertToVoList(list);
    }

    /**
     * 补充父级分类数据
     * <p>
     * 当查询结果中所有分类都不是顶级分类（level != 1）时，
     * 根据每个分类的parentId查询对应的父级分类，并添加到列表中
     *
     * @param categoryList 原始分类列表
     */
    private void supplementParentCategories(List<TbCategory> categoryList) {
        // 判断是否所有分类都不是顶级分类（level != 1）
        boolean allNotLevelOne = categoryList.stream()
                .noneMatch(category -> Long.valueOf(1L).equals(category.getLevel()));

        // 如果存在顶级分类，无需补充父级数据
        if (!allNotLevelOne) {
            return;
        }

        // 记录已处理的父级ID，避免重复查询和添加
        Set<String> processedParentIds = new HashSet<>();
        // 临时存储待添加的父级分类
        List<TbCategory> parentCategories = new ArrayList<>();

        // 遍历所有分类，查找需要补充的父级分类
        for (TbCategory category : categoryList) {
            String parentId = category.getParentId();

            // 跳过已处理过的父级ID
            if (processedParentIds.contains(parentId)) {
                continue;
            }

            // 查询父级分类数据（parentId='000' 且 categoryCode=当前parentId）
            TbCategory parentCategory = fetchParentCategory(parentId);
            if (parentCategory != null) {
                parentCategories.add(parentCategory);
                processedParentIds.add(parentId);
            }
        }

        // 将所有父级分类一次性添加到原列表中
        categoryList.addAll(parentCategories);
    }

    /**
     * 查询父级分类数据
     * <p>
     * 根据分类编码查询对应的父级分类信息
     *
     * @param categoryCode 分类编码（作为父级的categoryCode）
     * @return 父级分类对象，不存在则返回null
     */
    private TbCategory fetchParentCategory(String categoryCode) {
        return tbCategoryMapper.selectOne(Wrappers.<TbCategory>lambdaQuery()
                .eq(TbCategory::getParentId, Constants.PARENT_ID_ZERO)
                .eq(TbCategory::getCategoryCode, categoryCode));
    }

    /**
     * 将分类列表转换为VO列表（树形结构）
     * <p>
     * 只转换顶级分类（level=1），并为每个顶级分类关联其子分类
     *
     * @param allCategories 所有分类数据（包含父级和子级）
     * @return 顶级分类VO列表，每个VO包含其子分类列表
     */
    private List<TbCategoryVo> convertToVoList(List<TbCategory> allCategories) {
        return allCategories.stream()
                // 只处理顶级分类（level=1）
                .filter(category -> Long.valueOf(1L).equals(category.getLevel()))
                // 构建每个顶级分类的VO对象，并关联子分类
                .map(category -> buildCategoryVo(category, allCategories))
                .collect(Collectors.toList());
    }

    /**
     * 构建单个分类VO对象
     * <p>
     * 将TbCategory转换为TbCategoryVo，并为其设置子分类列表
     *
     * @param category      分类实体对象
     * @param allCategories 所有分类数据（用于查找子分类）
     * @return 分类VO对象
     */
    private TbCategoryVo buildCategoryVo(TbCategory category, List<TbCategory> allCategories) {
        TbCategoryVo vo = new TbCategoryVo();
        vo.setParentId(category.getParentId());
        vo.setParentName(category.getParentName());
        vo.setCategoryCode(category.getCategoryCode());
        vo.setCategoryName(category.getCategoryName());
        // 生成唯一键：parentId-categoryCode
        vo.setUniqueKey(category.getParentId() + "-" + category.getCategoryCode());
        // 构建并设置子分类列表
        vo.setChildren(buildChildCategoryList(category.getCategoryCode(), allCategories));
        return vo;
    }

    /**
     * 构建子分类列表
     * <p>
     * 根据父级分类编码查找所有直接子分类，并转换为VO对象
     *
     * @param parentId      父级分类编码
     * @param allCategories 所有分类数据
     * @return 子分类VO列表
     */
    private List<TbCategoryVo> buildChildCategoryList(String parentId, List<TbCategory> allCategories) {
        return allCategories.stream()
                // 筛选出parentId匹配的子分类
                .filter(category -> parentId.equals(category.getParentId()))
                // 转换为简单VO对象（不包含子分类）
                .map(this::convertToSimpleVo)
                .collect(Collectors.toList());
    }

    /**
     * 将分类实体转换为简单VO对象
     * <p>
     * 用于子分类的转换，不包含children字段
     *
     * @param category 分类实体对象
     * @return 分类VO对象
     */
    private TbCategoryVo convertToSimpleVo(TbCategory category) {
        TbCategoryVo vo = new TbCategoryVo();
        vo.setParentId(category.getParentId());
        vo.setParentName(category.getParentName());
        vo.setCategoryCode(category.getCategoryCode());
        vo.setCategoryName(category.getCategoryName());
        // 生成唯一键：parentId-categoryCode
        vo.setUniqueKey(category.getParentId() + "-" + category.getCategoryCode());
        return vo;
    }
// ... existing code ...


    @Override
    public List<TbCategory> selectTbCategoryListByParentId(String parentId) {
        return tbCategoryMapper.selectTbCategoryListByParentId(parentId);
    }


    /**
     * 新增商品分类
     * <p>
     * 业务逻辑：
     * 1. 根据父分类ID判断分类层级（顶级分类 level=0 或子分类 level=1）
     * 2. 自动生成分类编码：同级分类按顺序递增（000, 001, 002...）
     * 3. 自动设置排序号、删除标志、创建/更新时间等字段
     *
     * @param tbCategory 商品分类对象，需包含 parentId、categoryName
     * @return 影响行数
     */
    @Override
    public int insertTbCategory(TbCategory tbCategory) {
        // 1. 创建新分类对象并设置基本信息
        TbCategory newCategory = new TbCategory();
        newCategory.setParentId(tbCategory.getParentId());
        newCategory.setCategoryName(tbCategory.getCategoryName());

        // 2. 判断是否为顶级分类（parentId = "000"）
        if (StringUtils.equals(tbCategory.getParentId(), Constants.PARENT_ID_ZERO) || StringUtils.isEmpty(tbCategory.getParentId())) {
            // 顶级分类
            tbCategory.setParentId(Constants.PARENT_ID_ZERO);
            newCategory.setParentName("顶级分类");
            newCategory.setLevel(1L);
        } else {
            // 子分类：查询父分类信息
            TbCategory parentCategory = tbCategoryMapper.selectOne(Wrappers.<TbCategory>lambdaQuery()
                    .eq(TbCategory::getParentId, Constants.PARENT_ID_ZERO)
                    .eq(TbCategory::getCategoryCode, tbCategory.getParentId()));
            if (parentCategory != null) {
                newCategory.setParentName(parentCategory.getCategoryName());
                newCategory.setLevel(2L);
            } else {
                throw new IllegalArgumentException("父分类不存在，parentId: " + tbCategory.getParentId());
            }
        }

        // 3. 生成分类编码和排序号
        // 查询当前父分类下最大的分类编码（按降序取第一条）
        QueryWrapper<TbCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", tbCategory.getParentId());
        queryWrapper.orderByDesc("category_code");
        queryWrapper.last("limit 1");
        TbCategory lastCategory = tbCategoryMapper.selectOne(queryWrapper);

        if (lastCategory != null && StringUtils.isNotEmpty(lastCategory.getCategoryCode())) {
            // 3.1 存在同级分类：在最大编码基础上+1
            String lastCode = lastCategory.getCategoryCode();
            // 提取前缀和后3位数字，数字部分+1后重新组合（例如：ABC001 -> ABC002）
            String prefix = lastCode.substring(0, lastCode.length() - 3);
            int lastNumber = Integer.parseInt(lastCode.substring(lastCode.length() - 3));
            String newCode = prefix + String.format("%03d", lastNumber + 1);
            newCategory.setCategoryCode(newCode);

            // 排序号递增（防止 null 值）
            Long lastSortOrder = lastCategory.getSortOrder();
            newCategory.setSortOrder(lastSortOrder != null ? lastSortOrder + 1 : 1L);
        } else {
            // 3.2 首个子分类：编码为"001"，排序号为1
            newCategory.setCategoryCode(Constants.PARENT_ID_ONE);
            newCategory.setSortOrder(1L);
        }

        // 4. 设置审计字段
        Date now = DateUtils.getNowDate();
        newCategory.setCreateTime(now);
        newCategory.setUpdateTime(now);
        newCategory.setDelFlg("0"); // 0-正常

        // 获取登录用户对象
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 设置创建人和更新人为当前登录用户
        newCategory.setCreateBy(loginUser.getUsername());
        newCategory.setUpdateBy(loginUser.getUsername());
        // 5. 执行插入
        return tbCategoryMapper.insertTbCategory(newCategory);
    }


    /**
     * 修改商品分类
     *
     * @param tbCategory 商品分类
     * @return 结果
     */
    @Override
    public int updateTbCategory(TbCategory tbCategory) {
        return tbCategoryMapper.updateTbCategory(tbCategory);
    }

    /**
     * 批量删除商品分类
     *
     * @param uniqueKeys 需要删除的商品分类主键
     * @return 结果
     */
    @Override
    public int deleteTbCategoryByParentIds(String[] uniqueKeys) {
        if (uniqueKeys == null || uniqueKeys.length == 0) {
            return 0;
        }
        // 验证每个 uniqueKey 的格式
        for (String uniqueKey : uniqueKeys) {
            if (StringUtils.isEmpty(uniqueKey) || !uniqueKey.contains("-")) {
                throw new IllegalArgumentException("uniqueKey格式不正确，应为：parentId-categoryCode");
            }
            String[] parts = uniqueKey.split("-");
            if (parts.length != 2) {
                throw new IllegalArgumentException("uniqueKey格式不正确，应为：parentId-categoryCode");
            }
        }

        return tbCategoryMapper.deleteTbCategoryByUniqueKeys(uniqueKeys);
    }

    /**
     * 删除商品分类信息
     *
     * @param parentId 商品分类主键
     * @return 结果
     */
    @Override
    public int deleteTbCategoryByParentId(String uniqueKey) {
        if (StringUtils.isEmpty(uniqueKey) || !uniqueKey.contains("-")) {
            throw new IllegalArgumentException("uniqueKey格式不正确，应为：parentId-categoryCode");
        }

        String[] parts = uniqueKey.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("uniqueKey格式不正确，应为：parentId-categoryCode");
        }

        String parentId = parts[0];
        String categoryCode = parts[1];
        return tbCategoryMapper.deleteTbCategoryByParentIdAndCode(parentId, categoryCode);
    }
}
