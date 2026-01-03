<template>
  <div class="dashboard-container">
    <el-card shadow="never" class="dashboard-card">
      <template #header>
        <div class="card-header">
          <span>系统概览</span>
        </div>
      </template>
      <div class="dashboard-stats">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon car">
                  <el-icon><i-ep-car /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ totalVehicles }}</div>
                  <div class="stat-label">总车辆数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon inventory">
                  <el-icon><i-ep-box /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ inStockVehicles }}</div>
                  <div class="stat-label">库存车辆 (在库+在途)</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon sale">
                  <el-icon><i-ep-shopping-cart /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ totalSales }}</div>
                  <div class="stat-label">总销售 (已售)</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon profit">
                  <el-icon><i-ep-money /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ formatMoney(totalProfit) }}</div>
                  <div class="stat-label">总利润</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { get } from '../utils/request'

const totalVehicles = ref(0)
const inStockVehicles = ref(0)
const totalSales = ref(0)
const totalProfit = ref(0)

// 格式化金额
const formatMoney = (val) => {
  if (val === undefined || val === null) return '¥0'
  return '¥' + Number(val).toLocaleString()
}

// 获取统计数据
const fetchStats = async () => {
  try {
    const res = await get('/api/vehicles/stats')
    if (res.data) {
      totalVehicles.value = res.data.totalVehicles || 0
      inStockVehicles.value = res.data.inStockVehicles || 0
      totalSales.value = res.data.totalSales || 0
      totalProfit.value = res.data.totalProfit || 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.dashboard-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-stats {
  margin-top: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24px;
  margin-right: 20px;
  color: #fff;
}

.stat-icon.car {
  background-color: #409eff;
}

.stat-icon.inventory {
  background-color: #67c23a;
}

.stat-icon.sale {
  background-color: #e6a23c;
}

.stat-icon.profit {
  background-color: #f56c6c;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}
</style>