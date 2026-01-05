<template>
  <div class="app-container">
    <el-card shadow="never">
      <div class="filter-container">
        <el-input
            v-model="searchKeyword"
            placeholder="请输入VIN码或车型名称"
            style="width: 300px; margin-right: 10px;"
            clearable
            @clear="fetchData"
            @keyup.enter="fetchData"
        />
        <el-button type="primary" @click="fetchData">
          <el-icon><Search /></el-icon> 查询
        </el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="vin" label="车辆VIN码" width="180" fixed />
        <el-table-column prop="manufacturerName" label="厂商" width="120" />
        <el-table-column prop="modelName" label="车型" min-width="150" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.status === 'in_stock'">在库</el-tag>
            <el-tag type="warning" v-else-if="scope.row.status === 'reserved'">已预定</el-tag>
            <el-tag type="info" v-else>{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="purchasePrice" label="采购成本" width="140" align="right">
          <template #default="scope">
            ¥ {{ formatMoney(scope.row.purchasePrice) }}
          </template>
        </el-table-column>
        <el-table-column prop="warehouseLocation" label="库位备注" width="180">
          <template #default="scope">
            {{ scope.row.warehouseLocation || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="entryDate" label="入库时间" width="180" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.entryDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="daysInStock" label="库龄(天)" width="100" align="center">
          <template #default="scope">
            <span :class="getAgeClass(scope.row.daysInStock)">
              {{ scope.row.daysInStock }}
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
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

const fetchData = async () => {
  loading.value = true
  try {
    const res = await get('/api/warehouse/details', {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
    ElMessage.error('获取明细数据失败')
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

const formatDate = (val) => {
  if (!val) return '-'
  return new Date(val).toLocaleString('zh-CN', { hour12: false })
}

// 库龄颜色标记：超过90天显示红色，超过60天显示橙色
const getAgeClass = (days) => {
  if (days > 90) return 'text-danger'
  if (days > 60) return 'text-warning'
  return ''
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.filter-container {
  margin-bottom: 20px;
  display: flex;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.text-danger {
  color: #f56c6c;
  font-weight: bold;
}
.text-warning {
  color: #e6a23c;
  font-weight: bold;
}
</style>