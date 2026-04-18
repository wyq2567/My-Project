<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入商品名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['product:product:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['product:product:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['product:product:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['product:product:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品编号" align="center" prop="productNo" />
      <el-table-column label="序号" align="center" prop="sequenceNo" />
      <el-table-column label="商品一级分类" align="center" prop="parentId" />
      <el-table-column label="商品二级分类" align="center" prop="categoryCode" />
      <el-table-column label="商品名称" align="center" prop="productName" />
      <el-table-column label="是否启用" align="center" prop="isEnabled" />
      <el-table-column label="单位" align="center" prop="unitCode" />
      <el-table-column label="记忆最近售价" align="center" prop="rememberLastPrice" />
      <el-table-column label="单位重量" align="center" prop="unitWeight" />
      <el-table-column label="单位体积" align="center" prop="unitVolume" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['product:product:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['product:product:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改商品管理对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="productRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="商品名称" prop="productName">
              <el-input v-model="form.productName" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unitCode">
              <el-input v-model="form.unitCode" placeholder="请输入单位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位重量" prop="unitWeight">
              <el-input v-model="form.unitWeight" placeholder="请输入单位重量" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位体积" prop="unitVolume">
              <el-input v-model="form.unitVolume" placeholder="请输入单位体积" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <editor v-model="form.remark" :min-height="192" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图片上传" prop="imageCode">
              <image-upload v-model="form.imageCode" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Product">
import { listProduct, getProduct, delProduct, addProduct, updateProduct } from "@/api/product/product"

const { proxy } = getCurrentInstance()

const productList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productNo: null,
    sequenceNo: null,
    parentId: null,
    categoryCode: null,
    productName: null,
  },
  rules: {
    parentId: [
      { required: true, message: "商品一级分类不能为空", trigger: "change" }
    ],
    categoryCode: [
      { required: true, message: "商品二级分类不能为空", trigger: "change" }
    ],
    productName: [
      { required: true, message: "商品名称不能为空", trigger: "blur" }
    ],
    isEnabled: [
      { required: true, message: "是否启用不能为空", trigger: "change" }
    ],
    unitCode: [
      { required: true, message: "单位不能为空", trigger: "blur" }
    ],
    rememberLastPrice: [
      { required: true, message: "记忆最近售价不能为空", trigger: "change" }
    ],
    unitWeight: [
      { required: true, message: "单位重量不能为空", trigger: "blur" }
    ],
    unitVolume: [
      { required: true, message: "单位体积不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询商品管理列表 */
function getList() {
  loading.value = true
  listProduct(queryParams.value).then(response => {
    productList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    productNo: null,
    sequenceNo: null,
    parentId: null,
    categoryCode: null,
    productName: null,
    isEnabled: null,
    unitCode: null,
    rememberLastPrice: null,
    unitWeight: null,
    unitVolume: null,
    remark: null,
    imageCode: null,
    createTime: null,
    dateTime: null,
    createBy: null,
    updateBy: null,
    delFlg: null
  }
  proxy.resetForm("productRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.productNo)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加商品管理"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _productNo = row.productNo || ids.value
  getProduct(_productNo).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改商品管理"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["productRef"].validate(valid => {
    if (valid) {
      if (form.value.productNo != null) {
        updateProduct(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addProduct(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _productNos = row.productNo || ids.value
  proxy.$modal.confirm('是否确认删除商品管理编号为"' + _productNos + '"的数据项？').then(function () {
    return delProduct(_productNos)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('product/product/export', {
    ...queryParams.value
  }, `product_${new Date().getTime()}.xlsx`)
}

getList()
</script>
