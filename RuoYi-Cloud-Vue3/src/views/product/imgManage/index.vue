<template>
    <div class="app-container">
        <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
            <el-form-item label="商品编号" prop="imageCode">
                <el-input v-model="queryParams.imageCode" placeholder="请输入商品编号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="商品名称" prop="productName">
                <el-input v-model="queryParams.productName" placeholder="请输入商品名称" clearable
                    @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
        </el-form>
        <el-row></el-row>
        <el-table v-loading="loading" :data="imgList">
            <el-table-column align="center" prop="id" v-if="false" />
            <el-table-column label="图片编号" align="center" prop="imageCode" width="150" />
            <el-table-column label="商品名称" align="center" prop="productName" width="200" />
            <el-table-column label="图片内容" align="center" prop="url">
                <template v-slot="scope">
                    <el-upload v-model:file-list="scope.row.imageUrlList" list-type="picture-card"
                        :http-request="(options) => handleUpload(options, scope.row)"
                        :on-remove="(file) => handleRemove(file, scope.row)" :on-preview="handlePictureCardPreview"
                        accept=".jpg,.jpeg,.png,.gif,.webp">
                        <el-icon>
                            <Plus />
                        </el-icon>
                    </el-upload>

                </template>
            </el-table-column>
        </el-table>
        <el-dialog v-model="dialogVisible">
            <img w-full :src="dialogImageUrl" alt="Preview Image" />
        </el-dialog>

        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

</template>
<script setup name="ImgManage">
import { listImg, delImg, updTbProductImg, addImg, delDbImg } from "@/api/product/imgManage"
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { id } from "element-plus/es/locales.mjs"

const { proxy } = getCurrentInstance()

const showSearch = ref(true)
const imgList = ref([])
const loading = ref(true)
const total = ref(0)

const data = reactive({
    form: {},
    queryParams: {
        pageNum: 1,
        pageSize: 100,
        productName: null,
        imageCode: null
    },
    rules: {
    }
})

const { queryParams, form, rules } = toRefs(data)

// 表单重置
function reset() {
    form.value = {
        id: null,
        imageCode: null,
        imageSeq: null,
        ImageUrl: null
    }
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

/** 查询商品管理列表 */
function getList() {
    loading.value = true
    listImg(queryParams.value).then(response => {
        imgList.value = response.rows
        total.value = response.total
        loading.value = false
    })
}




const dialogImageUrl = ref('')
const dialogVisible = ref(false)
/**
 * 自定义图片上传
 * @param {Object} options - el-upload 提供的上传选项，包含 file 等
 * @param {Object} row - 当前行数据
 */
const handleUpload = (options, row) => {
    const formData = new FormData()
    formData.append('file', options.file) // 'file' 需与后端接收参数名一致

    // 使用若依 request 上传，自动携带 Token
    updTbProductImg(formData).then(res => {
        console.log(res)
        console.log(row)
        if (res.code === 200) {
            const params = {
                imageCode: row.imageCode,
                productName: row.productName,
                imageUrl: res.data.url
            }
            addImg(params).then(() => {
                getList()
            })
        } else {
            ElMessage.error(res.msg || '上传失败')

        }
    }).catch(err => {
        console.error(err)
        ElMessage.error('上传请求异常')
    })
}
/**
 * 删除图片
 * @param {Object} file - 被删除的文件对象
 * @param {Object} row - 当前行数据
 */
const handleRemove = (file, row) => {

    if (row.id) {
        delDbImg(row.id).catch(err => console.warn('删除数据库记录失败', err));
    }
    delImg(file.url).then(() => {
        getList()
    }).catch(() => {
        ElMessage.error('删除失败')
    })
}
const handlePictureCardPreview = (uploadFile) => {
    dialogImageUrl.value = uploadFile.url
    dialogVisible.value = true
}



getList()
</script>