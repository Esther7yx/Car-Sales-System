<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>库存车辆</span>
              <el-tag type="success">实时</el-tag>
            </div>
          </template>
          <div class="card-value">{{ summary.total_stock_count || 0 }}</div>
          <div class="card-desc">当前在库待售</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>累计销量</span>
              <el-tag type="warning">总计</el-tag>
            </div>
          </template>
          <div class="card-value">{{ summary.total_sold_count || 0 }}</div>
          <div class="card-desc">历史销售总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>销售总额</span>
              <el-tag type="danger">RMB</el-tag>
            </div>
          </template>
          <div class="card-value">¥ {{ formatMoney(summary.total_revenue) }}</div>
          <div class="card-desc">历史成交总金额</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>客户数量</span>
              <el-tag>Total</el-tag>
            </div>
          </template>
          <div class="card-value">{{ summary.total_customers || 0 }}</div>
          <div class="card-desc">建档客户总数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>近12个月销售/利润趋势</span>
            </div>
          </template>
          <div ref="lineChartRef" style="height: 400px; width: 100%;"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>各品牌库存占比</span>
            </div>
          </template>
          <div ref="pieChartRef" style="height: 400px; width: 100%;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { get } from '../utils/request'
import { ElMessage } from 'element-plus'

const summary = ref({})
const lineChartRef = ref(null)
const pieChartRef = ref(null)
let lineChartInstance = null
let pieChartInstance = null

onMounted(async () => {
  await fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (lineChartInstance) lineChartInstance.dispose()
  if (pieChartInstance) pieChartInstance.dispose()
})

const fetchData = async () => {
  try {
    const res = await get('/api/report/dashboard')
    const data = res.data

    // 1. 设置顶部卡片数据
    if (data.summary) {
      summary.value = data.summary
    }

    // 2. 渲染折线图 (销售趋势)
    if (data.salesLine) {
      initLineChart(data.salesLine)
    }

    // 3. 渲染饼图 (库存分布)
    if (data.stockPie) {
      initPieChart(data.stockPie)
    }

  } catch (error) {
    console.error(error)
    ElMessage.error('加载报表数据失败')
  }
}

// 初始化折线图
const initLineChart = (data) => {
  lineChartInstance = echarts.init(lineChartRef.value)
  const months = data.map(item => item.sale_month)
  const revenues = data.map(item => item.total_revenue)
  const profits = data.map(item => item.total_profit)

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '净利润'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: months },
    yAxis: { type: 'value' },
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: revenues,
        itemStyle: { color: '#409EFF' },
        areaStyle: { opacity: 0.1 }
      },
      {
        name: '净利润',
        type: 'line',
        smooth: true,
        data: profits,
        itemStyle: { color: '#67C23A' }
      }
    ]
  }
  lineChartInstance.setOption(option)
}

// 初始化饼图
const initPieChart = (data) => {
  pieChartInstance = echarts.init(pieChartRef.value)
  const pieData = data.map(item => ({
    name: item.manufacturer_name,
    value: item.stock_count
  }))

  const option = {
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [
      {
        name: '库存数量',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false, position: 'center' },
        emphasis: {
          label: { show: true, fontSize: 20, fontWeight: 'bold' }
        },
        labelLine: { show: false },
        data: pieData
      }
    ]
  }
  pieChartInstance.setOption(option)
}

const handleResize = () => {
  lineChartInstance?.resize()
  pieChartInstance?.resize()
}

const formatMoney = (val) => {
  if (!val) return '0.00'
  return val.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.data-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 10px 0;
}
.card-desc {
  font-size: 14px;
  color: #909399;
}
.chart-header {
  font-weight: bold;
}
</style>