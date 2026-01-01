<template>
  <div class="car-model-list-container">
    <div class="page-header">
      <h1>车型管理</h1>
      <el-button type="primary" @click="goToAdd">添加车型</el-button>
    </div>

    <div class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="车型名称">
          <el-input v-model="searchForm.modelName" placeholder="请输入车型名称" clearable />
        </el-form-item>
        <el-form-item label="所属厂商">
          <el-select v-model="searchForm.manufacturerId" placeholder="请选择厂商" clearable>
            <el-option
                v-for="manufacturer in manufacturerList"
                :key="manufacturer.manufacturerId"
                :label="manufacturer.manufacturerName"
                :value="manufacturer.manufacturerId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="年份">
          <el-input v-model="searchForm.year" placeholder="请输入年份" type="number" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" stripe border style="width: 100%">
        <el-table-column prop="modelId" label="车型ID" width="80" align="center" />

        <el-table-column prop="modelName" label="车型名称" width="180" />

        <el-table-column label="所属厂商" width="150">
          <template #default="scope">
            {{ scope.row.manufacturer ? scope.row.manufacturer.manufacturerName : '-' }}
          </template>
        </el-table-column>

        <el-table-column prop="year" label="年份" width="100" align="center" />

        <el-table-column prop="fuelType" label="燃油类型" width="100" />

        <el-table-column prop="engineType" label="发动机" width="150" />

        <el-table-column prop="transmission" label="变速箱" width="120" />

        <el-table-column prop="guidePrice" label="指导价格(元)" width="150" align="right">
          <template #default="scope">
            {{ scope.row.guidePrice ? scope.row.guidePrice.toLocaleString() : '-' }}
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="创建时间" width="180" align="center" />

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="goToEdit(scope.row.modelId)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.modelId)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, del } from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 表格数据
const tableData = ref([])

// 厂商列表（用于下拉选择）
const manufacturerList = ref([])

// 搜索表单
const searchForm = ref({
  modelName: '',
  manufacturerId: '',
  year: '' // 新增年份搜索
})

// 分页参数
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 页面加载时获取数据
onMounted(() => {
  fetchManufacturers()
  fetchData()
})

// 获取厂商列表（用于搜索条件）
const fetchManufacturers = async () => {
  try {
    // 这里使用之前修复好的 manufacturer 接口
    const response = await get('/api/manufacturer/page', { current: 1, size: 1000 })
    // 注意：manufacturer 接口返回的是 page 对象
    manufacturerList.value = response.data.records
  } catch (error) {
    ElMessage.error('获取厂商列表失败')
    console.error('获取厂商列表失败:', error)
  }
}

// 获取车型数据
const fetchData = async () => {
  try {
    const params = {
      current: pagination.value.currentPage,
      size: pagination.value.pageSize,
      modelName: searchForm.value.modelName,
      // 如果为空字符串，传 null 或者 undefined，否则后端可能报错
      year: searchForm.value.year || undefined
    }
    // 如果选了厂商，才传 manufacturerId（之前后端Controller并没有实现按厂商ID筛选，但这里先保留参数结构）
    // 注意：你的后端 pageQuery 目前只接收 modelName 和 year，没有接收 manufacturerId
    // 如果需要按厂商筛选，需要修改后端 Controller 和 Service

    const response = await get('/api/car-models/page', params)

    // 修复后的后端返回的是 IPage 对象
    tableData.value = response.data.records
    pagination.value.total = response.data.total
  } catch (error) {
    ElMessage.error('获取车型列表失败')
    console.error('获取车型列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.value.currentPage = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.value = {
    modelName: '',
    manufacturerId: '',
    year: ''
  }
  pagination.value.currentPage = 1
  fetchData()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  fetchData()
}

// 当前页变化
const handleCurrentChange = (current) => {
  pagination.value.currentPage = current
  fetchData()
}

// 跳转到添加页面
const goToAdd = () => {
  router.push('/car-models/add')
}

// 跳转到编辑页面
const goToEdit = (id) => {
  router.push(`/car-models/edit/${id}`)
}

// 删除车型
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该车型吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await del(`/api/car-models/${id}`)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error('删除车型失败:', error)
    }
  }).catch(() => {
    // 取消删除操作
  })
}
</script>

<style scoped>
.car-model-list-container {
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.search-container {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.search-form {
  display: flex;
  align-items: center;
}

.table-container {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
}
</style>