<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="父分类名称" prop="parentName" label-width="auto">
        <el-input v-model="queryParams.parentName" placeholder="父分类名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="分类名称" prop="categoryName">
        <el-input v-model="queryParams.categoryName" placeholder="分类名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['product:category:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['product:category:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain :icon="isExpandAll ? 'SortUp' : 'SortDown'" @click="toggleExpandAll">
          {{ isExpandAll ? '全部折叠' : '全部展开' }}
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table :data="categoryList" style="width: 100%; margin-bottom: 20px" row-key="uniqueKey" border
      ref="categoryTableRef" @selection-change="handleSelectionChange" @select="handleSelect"
      @select-all="handleSelectAll">
      <el-table-column type="selection" width="55" align="center" :selectable="checkSelectable" />
      <el-table-column label="父分类编码" align="center" prop="parentId" />
      <el-table-column label="父分类名称" align="center" prop="parentName" />
      <el-table-column label="分类编码" align="center" prop="categoryCode" />
      <el-table-column label="分类名称" align="center" prop="categoryName" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['product:category:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['product:category:remove']">删除</el-button>
          <el-button v-if="scope.row.parentId === '000'" link type="warning" icon="FolderOpened"
            @click="handleAddChildren(scope.row.categoryCode)">
            增加子类
          </el-button>

        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加商品分类对话框 -->
    <el-dialog :title="title" v-model="openAdd" width="500px" append-to-body>
      <el-form ref="categoryRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="分类名称" prop="categoryName">
              <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="上级分类" prop="sortOrder">
              <el-select v-model="form.parentId" placeholder="请选择下拉选择" clearable :style="{ width: '100%' }">
                <el-option v-for="(topLevel, index) in topLeveList" :key="topLevel.categoryCode"
                  :label="topLevel.categoryName" :value="topLevel.categoryCode" :disabled="false"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAddForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改商品分类对话框 -->
    <el-dialog :title="title" v-model="openUpd" width="500px" append-to-body>
      <el-form ref="categoryRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="分类名称" prop="categoryName">
              <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.parentId !== '000'">
            <el-form-item label="上级分类" prop="sortOrder">
              <el-select v-model="form.parentId" placeholder="请选择下拉选择" clearable :style="{ width: '100%' }"
                :disabled="true">
                <el-option v-for="(topLevel, index) in topLeveList" :key="topLevel.categoryCode"
                  :label="topLevel.categoryName" :value="topLevel.categoryCode"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitUpdForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>


  </div>
</template>

<script setup name="Category">
import { listCategory, getCategory, delCategory, addCategory, updateCategory, getTopLevelList } from "@/api/product/category"
import { getCurrentInstance, ref, reactive, toRefs } from 'vue' // 确保引入了 ref

const { proxy } = getCurrentInstance()

const categoryTableRef = ref(null)

const categoryList = ref([])
const openAdd = ref(false)
const openUpd = ref(false)
const isExpandAll = ref(false) // 【新增】记录展开状态，默认折叠

const isSelectAll = ref(false) //

const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const topLeveId = "000"

const topLeveList = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 100,
    parentName: null,
    categoryName: null,
  },
  rules: {
    parentName: [
      { required: true, message: "父分类名称不能为空", trigger: "blur" }
    ],
    categoryName: [
      { required: true, message: "分类名称不能为空", trigger: "blur" }
    ],
  }
})
const disabledKeys = ref(new Set())

const { queryParams, form, rules } = toRefs(data)

/** 
 * 辅助函数：递归切换行的展开状态 
 * @param {Array} data - 数据列表
 * @param {Boolean} expanded - true为展开，false为折叠
 */
function toggleTreeExpansion(data, expanded) {
  if (!data || !Array.isArray(data)) return

  data.forEach(item => {
    // 1. 切换当前行的展开状态
    if (categoryTableRef.value) {
      categoryTableRef.value.toggleRowExpansion(item, expanded)
    }

    // 2. 如果有子节点，递归处理子节点
    if (item.children && item.children.length > 0) {
      toggleTreeExpansion(item.children, expanded)
    }
  })
}

/** 【合并】切换展开/折叠状态 */
function toggleExpandAll() {
  // 取反当前状态
  const newState = !isExpandAll.value

  // 执行展开或折叠操作
  toggleTreeExpansion(categoryList.value, newState)

  // 更新状态变量
  isExpandAll.value = newState
}
/** 查询商品分类列表 */
function getList() {
  loading.value = true
  listCategory(queryParams.value).then(response => {
    categoryList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  openAdd.value = false
  openUpd.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    parentId: null,
    parentName: null,
    categoryCode: null,
    categoryName: null,
    level: null,
    sortOrder: null,
    createTime: null,
    dateTime: null,
    createBy: null,
    updateBy: null,
    delFlg: null
  }
  proxy.resetForm("categoryRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
  // 执行展开操作
  toggleTreeExpansion(categoryList.value, true)

}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  disabledKeys.value.clear()
  if (selection.length > 0) {
    selection.forEach(itemparent => {
      console.log("handleSelectionChange " + itemparent)
      if (itemparent.parentId === "000") {
        selection.forEach(itemson => {
          if (itemson.parentId === itemparent.categoryCode) {
            disabledKeys.value.add(itemson.uniqueKey)
          }
        })
      }
    })
  }
  ids.value = selection.map(item => item.uniqueKey)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  loadTopLevelList().then(() => {
    openAdd.value = true
    title.value = "修改商品分类"
  })
}
function handleAddChildren(parentId) {
  reset()
  loadTopLevelList().then(() => {
    openAdd.value = true
    title.value = "修改商品分类"
  })
}

/** 加载顶级分类列表 */
function loadTopLevelList() {
  return getTopLevelList(topLeveId).then(response => {
    topLeveList.value = response.data
  })
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _uniqueKey = row.uniqueKey || ids.value
  Promise.all([
    getCategory(_uniqueKey),
    loadTopLevelList()
  ]).then(([categoryRes, _]) => {
    form.value = categoryRes.data
    openUpd.value = true
    title.value = "修改商品分类"
  }).catch(error => {
    console.error("加载数据失败", error)
  })
}

/** 提交按钮 */
function submitUpdForm() {
  proxy.$refs["categoryRef"].validate(valid => {
    if (valid) {
      updateCategory(form.value).then(() => {
        proxy.$modal.msgSuccess("修改成功")
        openUpd.value = false
        getList()
      })
    }
  })
}

/** 提交按钮 */
function submitAddForm() {
  proxy.$refs["categoryRef"].validate(valid => {
    if (valid) {
      addCategory(form.value).then(() => {
        proxy.$modal.msgSuccess("新增成功")
        openAdd.value = false
        getList()
      })
    }
  })
}
// /** 删除按钮操作 */
// function handleDelete(row) {
//   const _parentIds = row.parentId || ids.value
//   proxy.$modal.confirm('是否确认删除商品分类编号为"' + _parentIds + '"的数据项？').then(function () {
//     return delCategory(_parentIds)
//   }).then(() => {
//     getList()
//     proxy.$modal.msgSuccess("删除成功")
//   }).catch(() => { })
// }

/** 【重构】删除按钮操作 (支持单删和批删，并检查子节点) */
function handleDelete(row) {
  // 1. 确定要删除的 ID 集合
  const deleteIds = row ? [row.uniqueKey] : ids.value

  // 2. 校验是否有可删除的数据
  if (!deleteIds || deleteIds.length === 0) {
    proxy.$modal.msgWarning("请选择要删除的数据")
    return
  }

  // 3. 【新增逻辑】如果是单行删除，检查是否存在子节点
  if (row) {
    // 注意：根据后端返回的数据结构，子节点可能在 children 字段中
    // 如果后端返回的是扁平列表并在前端组装树，可能需要检查其他标识
    // 这里假设 row.children 存在且长度大于0表示有子节点
    if (row.children && row.children.length > 0) {
      proxy.$modal.msgWarning("该分类下存在子分类，请先删除子分类！")
      return
    }
  } else {
    // 如果是批量删除，建议遍历检查选中项中是否有包含子节点的项
    // 获取当前选中的完整对象以便检查 children
    // 由于 ids.value 只存了 ID，我们需要从 categoryList 中查找或者依赖 selection
    // 这里简化处理：批量删除时通常不强制检查子节点（取决于业务需求），或者你可以遍历 selection
    // 如果需要严格检查批量删除中的父节点：
    const hasChildren = selection.some(item => item.children && item.children.length > 0)
    if (hasChildren) {
      proxy.$modal.msgWarning("选中的数据中包含存在子分类的项，请先删除子分类！")
      return
    }
  }

  // 4. 构造提示信息
  proxy.$modal.confirm('是否确认删除？').then(function () {
    // 5. 调用后端删除接口
    return delCategory(deleteIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

const checkSelectable = (row) => {
  // 如果 ID 在禁用集合里，返回 false (变灰不可点)
  return !disabledKeys.value.has(row.uniqueKey)
}

/**
 * 【优化】处理单行选中/取消选中事件
 */
function handleSelect(selection, row) {
  const isChecked = selection.some(item => item.uniqueKey === row.uniqueKey);

  // 只有顶级父节点 (parentId === '000') 才触发联动
  if (row.parentId === "000" && row.children && row.children.length > 0) {
    const childIds = getAllChildrenIds(row.children);

    proxy.$nextTick(() => {
      childIds.forEach(id => {
        const childRow = findNodeById(categoryList.value, id);
        if (childRow && categoryTableRef.value) {
          // 同步子节点的选中状态与父节点一致
          categoryTableRef.value.toggleRowSelection(childRow, isChecked);
        }
      });
    });
  }
}
/**
 * 【新增】递归获取所有子节点 ID
 */
function getAllChildrenIds(children) {
  let ids = [];
  if (!children) return ids;
  children.forEach(child => {
    ids.push(child.uniqueKey);
    if (child.children && child.children.length > 0) {
      ids = ids.concat(getAllChildrenIds(child.children));
    }
  });
  return ids;
}

/**
 * 【新增】根据 uniqueKey 递归查找节点对象
 */
function findNodeById(list, id) {
  for (const item of list) {
    if (item.uniqueKey === id) return item;
    if (item.children && item.children.length > 0) {
      const found = findNodeById(item.children, id);
      if (found) return found;
    }
  }
  return null;
}

/**
 * 【新增】处理全选/全不选事件
 * @param {Array} selection - 当前所有选中的行（全不选时为空数组）
 */
function handleSelectAll(selection) {
  const newState = !isSelectAll.value

  // 遍历所有顶层数据，找出所有的父节点和子节点进行联动
  categoryList.value.forEach(row => {
    console.log("row", row);
    if (row.parentId === "000" && row.children && row.children.length > 0) {
      const childIds = getAllChildrenIds(row.children);

      proxy.$nextTick(() => {
        childIds.forEach(id => {
          const childRow = findNodeById(categoryList.value, id);
          if (childRow && categoryTableRef.value) {
            console.log(newState);
            // 如果全选，则选中子节点；如果全不选，则取消选中子节点
            categoryTableRef.value.toggleRowSelection(childRow, newState);
          }
        });
      });
    }
  });
  isSelectAll.value = newState

}


getList()
</script>
