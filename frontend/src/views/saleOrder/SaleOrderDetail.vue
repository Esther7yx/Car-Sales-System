<template>
  <div class="sale-order-detail">
    <div class="header">
      <h2>销售订单详情</h2>
      <el-button @click="handleBack">返回</el-button>
    </div>

    <div class="detail-content">
      <el-card class="order-info">
        <template #header>
          <div class="card-header">
            <span>订单基本信息</span>
            <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
          </div>
        </template>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ order.orderNumber }}</el-descriptions-item>
          <el-descriptions-item label="客户名称">{{ order.customerName }}</el-descriptions-item>
          <el-descriptions-item label="客户电话">{{ order.customerPhone }}</el-descriptions-item>
          <el-descriptions-item label="销售日期">{{ order.saleDate }}</el-descriptions-item>
          <el-descriptions-item label="销售价格">¥{{ formatAmount(order.salePrice) }}</el-descriptions-item>
          <el-descriptions-item label="付款方式">{{ getPaymentMethodText(order.paymentMethod) }}</el-descriptions-item>
          <el-descriptions-item label="销售员">{{ order.salesperson }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ order.remarks || '无' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card class="vehicle-info">
        <template #header>
          <div class="card-header">
            <span>车辆信息</span>
          </div>
        </template>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="VIN码">{{ order.vehicleVin }}</el-descriptions-item>
          <el-descriptions-item label="品牌">{{ order.vehicleBrand }}</el-descriptions-item>
          <el-descriptions-item label="车型">{{ order.vehicleModel }}</el-descriptions-item>
          <el-descriptions-item label="年份">{{ order.vehicleYear }}</el-descriptions-item>
          <el-descriptions-item label="发动机类型">{{ order.vehicleEngineType }}</el-descriptions-item>
          <el-descriptions-item label="变速箱">{{ order.vehicleTransmission }}</el-descriptions-item>
          <el-descriptions-item label="燃油类型">{{ order.vehicleFuelType }}</el-descriptions-item>
          <el-descriptions-item label="指导价">¥{{ formatAmount(order.vehicleGuidePrice) }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card class="sale-details" v-if="order.saleDetails && order.saleDetails.length > 0">
        <template #header>
          <div class="card-header">
            <span>销售明细</span>
          </div>
        </template>
        
        <el-table :data="order.saleDetails">
          <el-table-column prop="itemName" label="项目名称" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="unitPrice" label="单价" width="120">
            <template #default="{ row }">¥{{ formatAmount(row.unitPrice) }}</template>
          </el-table-column>
          <el-table-column prop="totalPrice" label="总价" width="120">
            <template #default="{ row }">¥{{ formatAmount(row.totalPrice) }}</template>
          </el-table-column>
          <el-table-column prop="remarks" label="备注" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const order = ref({
  id: 1,
  orderNumber: 'SO202601020001',
  customerName: '张三',
  customerPhone: '13800138001',
  saleDate: '2026-01-01',
  salePrice: 130000,
  paymentMethod: 'full',
  salesperson: '销售员A',
  status: 'completed',
  createTime: '2026-01-01 10:30:00',
  remarks: '测试销售订单',
  vehicleVin: '11111111111111111',
  vehicleBrand: '奥迪',
  vehicleModel: 'A3',
  vehicleYear: '2026',
  vehicleEngineType: '1.5T 160马力 L4',
  vehicleTransmission: '自动',
  vehicleFuelType: '汽油',
  vehicleGuidePrice: 120000,
  saleDetails: [
    {
      id: 1,
      itemName: '车辆销售',
      quantity: 1,
      unitPrice: 130000,
      totalPrice: 130000,
      remarks: '主车辆销售'
    },
    {
      id: 2,
      itemName: '保险费用',
      quantity: 1,
      unitPrice: 5000,
      totalPrice: 5000,
      remarks: '第一年保险'
    }
  ]
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

const getPaymentMethodText = (method) => {
  const texts = {
    'full': '全款',
    'installment': '分期',
    'loan': '贷款'
  }
  return texts[method] || method
}

const formatAmount = (amount) => {
  return new Intl.NumberFormat('zh-CN').format(amount)
}

const handleBack = () => {
  router.push('/sale-orders')
}

const loadOrderDetail = async () => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 300))
    // 这里应该根据route.params.id加载订单数据
    // 现在使用模拟数据
  } catch (error) {
    ElMessage.error('加载订单详情失败')
  }
}

onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.sale-order-detail {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>