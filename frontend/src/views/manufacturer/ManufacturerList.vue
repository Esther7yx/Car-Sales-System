<template>
  <div class="manufacturer-list-container">
    <div class="page-header">
      <h1>厂商管理</h1>
      <el-button type="primary" @click="goToAdd">添加厂商</el-button>
    </div>

    <div class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="厂商名称">
          <el-input v-model="searchForm.manufacturerName" placeholder="请输入厂商名称" clearable />
        </el-form-item>
        <el-form-item label="合作状态">
          <el-select v-model="searchForm.cooperationStatus" placeholder="请选择合作状态" clearable>
            <el-option label="合作中" value="active" />
            <el-option label="已终止" value="inactive" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" stripe border style="width: 100%">
        <el-table-column prop="manufacturerId" label="厂商ID" width="80" align="center" />
        <el-table-column prop="manufacturerName" label="厂商名称" width="180" />
        <el-table-column prop="contactPerson" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="cooperationStatus" label="合作状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.cooperationStatus === 'active' ? 'success' : 'danger'">
              {{ scope.row.cooperationStatus === 'active' ? '合作中' : '已终止' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="goToEdit(scope.row.manufacturerId)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.manufacturerId)">
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

// 搜索表单
const searchForm = ref({
  manufacturerName: '',
  cooperationStatus: ''
})

// 分页参数
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 页面加载时获取数据
onMounted(() => {
  fetchData()
})

// 获取数据
const fetchData = async () => {
  try {
    const params = {
      current: pagination.value.currentPage,
      size: pagination.value.pageSize,
      manufacturerName: searchForm.value.manufacturerName,
      cooperationStatus: searchForm.value.cooperationStatus
    }
    const response = await get('/api/manufacturer/page', params)
    tableData.value = response.data.records
    pagination.value.total = response.data.total
  } catch (error) {
    ElMessage.error('获取厂商列表失败')
    console.error('获取厂商列表失败:', error)
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
    manufacturerName: '',
    cooperationStatus: ''
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
  router.push('/manufacturers/add')
}

// 跳转到编辑页面
const goToEdit = (id) => {
  router.push(`/manufacturers/edit/${id}`)
}

// 删除厂商
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该厂商吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await del(`/api/manufacturer/${id}`)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error('删除厂商失败:', error)
    }
  }).catch(() => {
    // 取消删除操作
  })
}
</script>

<style scoped>
.manufacturer-list-container {
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