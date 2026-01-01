<template>
  <div class="customer-list-container">
    <div class="page-header">
      <h1>客户管理</h1>
      <el-button type="primary" @click="goToAdd">添加客户</el-button>
    </div>

    <div class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="输入姓名或电话搜索" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" stripe border style="width: 100%">
        <el-table-column prop="customerId" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="客户姓名" width="150" />
        <el-table-column prop="customerType" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.customerType === 'individual' ? '' : 'warning'">
              {{ scope.row.customerType === 'individual' ? '个人' : '企业' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="idNumber" label="身份证/信用代码" width="200" />
        <el-table-column prop="creditRating" label="信用评级" width="100" align="center">
          <template #default="scope">
            <el-tag effect="dark" :type="getCreditColor(scope.row.creditRating)">
              {{ scope.row.creditRating || 'N/A' }}级
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="录入时间" width="180" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="goToEdit(scope.row.customerId)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.customerId)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, del } from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const tableData = ref([])
const searchForm = ref({ keyword: '' })

onMounted(() => { fetchData() })

const fetchData = async () => {
  try {
    // 后端 Controller 接收 keyword 参数
    const params = { keyword: searchForm.value.keyword }
    const response = await get('/api/customers', params)
    tableData.value = response.data
  } catch (error) {
    ElMessage.error('获取客户列表失败')
  }
}

const handleSearch = () => fetchData()
const handleReset = () => {
  searchForm.value.keyword = ''
  fetchData()
}

const goToAdd = () => router.push('/customers/add')
const goToEdit = (id) => router.push(`/customers/edit/${id}`)

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该客户吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await del(`/api/customers/${id}`)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const getCreditColor = (rating) => {
  const map = { 'A': 'success', 'B': 'primary', 'C': 'warning', 'D': 'danger' }
  return map[rating] || 'info'
}
</script>

<style scoped>
.customer-list-container {
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
.search-container {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>