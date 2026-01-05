<template>
  <div class="app-container">
    <div class="page-header">
      <h2>销售订单列表</h2>
      <el-button type="primary" @click="$router.push('/sales/create')">
        <el-icon><Plus /></el-icon> 新建销售单
      </el-button>
    </div>

    <div class="filter-container">
      <el-input
          v-model="searchKeyword"
          placeholder="请输入订单号搜索"
          style="width: 200px; margin-right: 10px;"
          clearable
          @clear="fetchData"
          @keyup.enter="fetchData"
      />
      <el-button type="primary" @click="fetchData">
        <el-icon><Search /></el-icon> 查询
      </el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="orderNumber" label="订单号" width="180" />
      <el-table-column prop="customerName" label="客户姓名" width="120" />
      <el-table-column prop="customerPhone" label="客户电话" width="150" />
      <el-table-column prop="totalAmount" label="订单总额" align="right" width="150">
        <template #default="scope">
          <span style="color: #f56c6c; font-weight: bold;">
            ¥ {{ formatMoney(scope.row.totalAmount) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="销售时间" width="180" align="center">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="operatorName" label="销售员" width="120" />
      <el-table-column prop="status" label="状态" align="center" width="100">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.status === 'delivered'">已成交</el-tag>
          <el-tag type="danger" v-else-if="scope.row.status === 'cancelled'">已取消</el-tag>
          <el-tag v-else>{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" min-width="120">
        <template #default="scope">
          <el-button link type="primary" @click="showDetails(scope.row)">查看明细</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog v-model="detailVisible" title="订单明细" width="700px">
      <el-descriptions title="基础信息" :column="2" border>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNumber }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ currentOrder.customerName }}</el-descriptions-item>
        <el-descriptions-item label="销售时间">{{ formatDate(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="经办人">{{ currentOrder.operatorName }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px;">
        <h4>车辆明细</h4>
        <el-table :data="detailList" border>
          <el-table-column prop="vin" label="车辆VIN" width="180" />
          <el-table-column prop="model_name" label="车型" />
          <el-table-column prop="purchase_price" label="进价" align="right" width="120">
            <template #default="scope">¥{{ formatMoney(scope.row.purchase_price) }}</template>
          </el-table-column>
          <el-table-column prop="unit_price" label="成交价" align="right" width="120">
            <template #default="scope">
              <span style="color: #67c23a; font-weight: bold;">¥{{ formatMoney(scope.row.unit_price) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="毛利" align="right" width="120">
            <template #default="scope">
              ¥{{ formatMoney(scope.row.unit_price - scope.row.purchase_price) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '../../utils/request'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

// 详情相关
const detailVisible = ref(false)
const currentOrder = ref({})
const detailList = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await get('/api/sale-orders', {
      current: currentPage.value,
      size: pageSize.value,
      orderNumber: searchKeyword.value
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
    ElMessage.error('获取销售列表失败')
  } finally {
    loading.value = false
  }
}

const showDetails = async (row) => {
  currentOrder.value = row
  try {
    const res = await get(`/api/sale-orders/${row.saleId}/details`)
    detailList.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取明细失败')
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchData()
}
const formatMoney = (val) => {
  if (!val) return '0.00'
  return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
const formatDate = (val) => {
  if (!val) return '-'
  return new Date(val).toLocaleString('zh-CN', { hour12: false })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.app-container { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.filter-container { margin-bottom: 20px; }
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>