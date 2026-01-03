<template>
  <div class="sale-order-list">
    <div class="header">
      <h2>销售订单管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加销售订单
      </el-button>
    </div>

    <div class="filter">
      <el-form :model="filterForm" inline>
        <el-form-item label="客户名称">
          <el-input v-model="filterForm.customerName" placeholder="请输入客户名称" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
            <el-option label="待处理" value="pending" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="orderNumber" label="订单号" width="180" />
      <el-table-column prop="customerName" label="客户名称" />
      <el-table-column prop="vehicleInfo" label="车辆信息" />
      <el-table-column prop="totalAmount" label="总金额" width="120">
        <template #default="{ row }">
          ¥{{ formatAmount(row.totalAmount) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
          <el-button type="warning" link @click="handleEdit(row)" v-if="row.status === 'pending'">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)" v-if="row.status === 'pending'">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { get } from '../../utils/request'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])

const filterForm = ref({
  customerName: '',
  status: ''
})

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const getStatusType = (status) => {
  const types = {
    'pending': 'warning',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'pending': '待处理',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return texts[status] || status
}

const formatAmount = (amount) => {
  return new Intl.NumberFormat('zh-CN').format(amount)
}

const handleSearch = () => {
  pagination.value.currentPage = 1
  fetchData()
}

const handleReset = () => {
  filterForm.value = {
    customerName: '',
    status: ''
  }
  handleSearch()
}

const handleAdd = () => {
  router.push('/sale-orders/add')
}

const handleDetail = (row) => {
  router.push(`/sale-orders/detail/${row.id}`)
}

const handleEdit = (row) => {
  // 编辑逻辑
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消此订单吗？', '提示', {
      type: 'warning'
    })
    // 调用取消订单API
    ElMessage.success('订单已取消')
    fetchData()
  } catch {
    // 用户取消操作
  }
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  fetchData()
}

const handleCurrentChange = (page) => {
  pagination.value.currentPage = page
  fetchData()
}

const fetchData = async () => {
  loading.value = true
  try {
    const response = await get('/api/sale/page', {
      current: pagination.value.currentPage,
      size: pagination.value.pageSize,
      customerName: filterForm.value.customerName,
      status: filterForm.value.status
    })
    tableData.value = response.data.records
    pagination.value.total = response.data.total
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.sale-order-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>