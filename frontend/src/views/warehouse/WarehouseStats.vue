<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>进销存统计 (近12个月)</span>
          <el-button @click="initChart">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>
      </template>

      <div v-loading="loading" class="chart-wrapper">
        <div ref="chartRef" style="width: 100%; height: 500px;"></div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { get } from '../../utils/request'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const chartRef = ref(null)
let chartInstance = null

const initChart = async () => {
  loading.value = true
  try {
    const res = await get('/api/warehouse/stats')
    const data = res.data

    // 数据反转，让最近的月份在右边
    const sortedData = data.reverse()

    const months = sortedData.map(item => item.monthStr)
    const purchaseCounts = sortedData.map(item => item.purchaseCount)
    const salesCounts = sortedData.map(item => item.salesCount)
    const purchaseAmounts = sortedData.map(item => item.purchaseAmount)
    const salesAmounts = sortedData.map(item => item.salesAmount)

    if (chartInstance) {
      chartInstance.dispose()
    }

    chartInstance = echarts.init(chartRef.value)

    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'cross' }
      },
      legend: {
        data: ['采购次数', '销售次数', '采购金额', '销售金额'] // 修改 Legend 名称更准确
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: [
        {
          type: 'category',
          data: months,
          axisPointer: { type: 'shadow' }
        }
      ],
      yAxis: [
        {
          type: 'value',
          name: '数量 (次)', // 【修改点 1】将单位从 "辆" 改为 "次"
          position: 'left',
          axisLine: { show: true, lineStyle: { color: '#5470C6' } },
          axisLabel: { formatter: '{value}' }
        },
        {
          type: 'value',
          name: '金额 (元)',
          position: 'right',
          axisLine: { show: true, lineStyle: { color: '#91CC75' } },
          axisLabel: { formatter: '{value}' },
          splitLine: { show: false }
        }
      ],
      series: [
        {
          name: '采购次数', // 【修改点 2】与 Legend 保持一致
          type: 'bar',
          data: purchaseCounts,
          itemStyle: { color: '#5470C6' },
          tooltip: {
            valueFormatter: (value) => value + ' 次'
          }
        },
        {
          name: '销售次数', // 【修改点 3】与 Legend 保持一致
          type: 'bar',
          data: salesCounts,
          itemStyle: { color: '#91CC75' },
          tooltip: {
            valueFormatter: (value) => value + ' 次'
          }
        },
        {
          name: '采购金额',
          type: 'line',
          yAxisIndex: 1,
          data: purchaseAmounts,
          itemStyle: { color: '#EE6666' },
          tooltip: {
            valueFormatter: (value) => '¥' + value
          }
        },
        {
          name: '销售金额',
          type: 'line',
          yAxisIndex: 1,
          data: salesAmounts,
          itemStyle: { color: '#FAC858' },
          tooltip: {
            valueFormatter: (value) => '¥' + value
          }
        }
      ]
    }

    chartInstance.setOption(option)

  } catch (error) {
    console.error(error)
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

const handleResize = () => {
  chartInstance?.resize()
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
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
.chart-wrapper {
  margin-top: 20px;
}
</style>