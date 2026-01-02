<template>
  <div class="customer-list">
    <div class="header">
      <h2>客户管理</h2>
      <el-button type="primary" @click="handleAdd">添加客户</el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="客户姓名">
          <el-input v-model="searchForm.name" placeholder="请输入客户姓名" clearable />
        </el-form-item>
        <el-form-item label="客户类型">
          <el-select v-model="searchForm.customerType" placeholder="请选择客户类型" clearable>
            <el-option label="个人客户" value="individual" />
            <el-option label="企业客户" value="enterprise" />
          </el-select>
        </el-form-item>
        <el-form-item label="信用等级">
          <el-select v-model="searchForm.creditRating" placeholder="请选择信用等级" clearable>
            <el-option label="A级" value="A" />
            <el-option label="B级" value="B" />
            <el-option label="C级" value="C" />
            <el-option label="D级" value="D" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="customerList" v-loading="loading" border stripe>
      <el-table-column type="selection" width="55" />
      <el-table-column prop="customerId" label="客户ID" width="80" />
      <el-table-column prop="name" label="客户姓名" width="120" />
      <el-table-column prop="customerType" label="客户类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.customerType === 'individual' ? 'success' : 'warning'">
            {{ row.customerType === 'individual' ? '个人客户' : '企业客户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="idNumber" label="身份证号" width="180" />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="email" label="邮箱" width="150" />
      <el-table-column prop="creditRating" label="信用等级" width="100">
        <template #default="{ row }">
          <el-tag :type="getCreditRatingType(row.creditRating)">
            {{ row.creditRating }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="160">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.customerId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <customer-form
      v-model="dialogVisible"
      :customer="currentCustomer"
      :is-edit="isEdit"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CustomerForm from './CustomerForm.vue'
import { customerApi } from '@/api/customer'

const loading = ref(false)
const customerList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const currentCustomer = ref({})
const isEdit = ref(false)

const searchForm = ref({
  name: '',
  customerType: '',
  creditRating: ''
})

const getCreditRatingType = (rating) => {
  const typeMap = {
    'A': 'success',
    'B': 'primary',
    'C': 'warning',
    'D': 'danger'
  }
  return typeMap[rating] || 'info'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

const fetchCustomers = async () => {
  loading.value = true
  try {
    const response = await customerApi.getCustomerList({
      current: currentPage.value,
      size: pageSize.value,
      ...searchForm.value
    })
    customerList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('获取客户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchCustomers()
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    customerType: '',
    creditRating: ''
  }
  currentPage.value = 1
  fetchCustomers()
}

const handleAdd = () => {
  currentCustomer.value = {}
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (customer) => {
  currentCustomer.value = { ...customer }
  isEdit.value = true
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个客户吗？', '提示', {
      type: 'warning'
    })
    
    await customerApi.deleteCustomer(id)
    ElMessage.success('删除成功')
    fetchCustomers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  fetchCustomers()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchCustomers()
}

const handleFormSuccess = () => {
  dialogVisible.value = false
  fetchCustomers()
}

onMounted(() => {
  fetchCustomers()
})
</script>

<style scoped>
.customer-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 20px;
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>