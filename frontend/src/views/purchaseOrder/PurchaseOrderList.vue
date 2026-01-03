<template>
  <div class="purchase-order-list">
    <div class="header">
      <h2>进货订单管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加进货订单
      </el-button>
    </div>

    <div class="filter">
      <el-form :model="filterForm" inline>
        <el-form-item label="供应商">
          <el-input v-model="filterForm.supplierName" placeholder="请输入供应商名称" clearable />
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

    <el-table :data="tableData" v-loading="loading" style="width: 100%">
      <el-table-column prop="orderNumber" label="订单号" width="200" show-overflow-tooltip />
      <el-table-column prop="supplierName" label="供应商" width="200" show-overflow-tooltip />
      <el-table-column prop="vehicleCount" label="车辆数量" width="120" align="center" />
      <el-table-column prop="totalAmount" label="总金额" width="150" align="right">
        <template #default="{ row }">
          ¥{{ formatAmount(row.totalAmount) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="220" fixed="right" align="center">
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
import { get, put } from '../../utils/request'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])

const filterForm = ref({
  supplierName: '',
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
    supplierName: '',
    status: ''
  }
  handleSearch()
}

const handleAdd = () => {
  router.push('/purchase-orders/add')
}

const handleDetail = (row) => {
  router.push(`/purchase-orders/detail/${row.id}`)
}

const handleEdit = (row) => {
  router.push(`/purchase-orders/edit/${row.orderId}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消此订单吗？', '提示', {
      type: 'warning'
    })
    // 调用取消订单API
    await put(`/api/purchase/${row.orderId}/status?status=cancelled`)
    ElMessage.success('订单已取消')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败：' + (error.response?.data?.message || error.message))
    }
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
    const response = await get('/api/purchase/page', {
      current: pagination.value.currentPage,
      size: pagination.value.pageSize,
      orderNumber: filterForm.value.supplierName,
      status: filterForm.value.status
    })
    tableData.value = response.data.records
    pagination.value.total = response.data.total
  } catch (error) {
    console.error('获取数据失败:', error)
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
.purchase-order-list {
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