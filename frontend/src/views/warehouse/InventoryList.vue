<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>库存车辆统计</span>
          <el-button type="primary" @click="fetchData">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="manufacturerName" label="厂商" width="150" />
        <el-table-column prop="modelName" label="车型名称" min-width="150" />
        <el-table-column prop="year" label="年款" width="100" align="center">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.year }}款</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="guidePrice" label="官方指导价" width="150" align="right">
          <template #default="scope">
            ¥ {{ formatMoney(scope.row.guidePrice) }}
          </template>
        </el-table-column>
        <el-table-column prop="stockQuantity" label="当前库存 (辆)" width="150" align="center">
          <template #default="scope">
            <el-tag type="success" effect="dark" style="font-size: 14px;">
              {{ scope.row.stockQuantity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalStockValue" label="库存总成本" width="180" align="right">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold;">
              ¥ {{ formatMoney(scope.row.totalStockValue) }}
            </span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '../../utils/request'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await get('/api/warehouse/inventory-summary', {
      page: currentPage.value,
      size: pageSize.value
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
    ElMessage.error('获取库存数据失败')
  } finally {
    loading.value = false
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

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>