<template>
  <div class="purchase-list-container">
    <div class="page-header">
      <h1>进货管理</h1>
      <el-button type="primary" @click="$router.push('/purchase/add')">新建采购单</el-button>
    </div>

    <div class="search-container">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="待入库" value="pending" />
            <el-option label="已入库" value="received" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" stripe border v-loading="loading">
        <el-table-column prop="orderNumber" label="采购单号" width="180" />
        <el-table-column prop="manufacturerName" label="供应商" width="150" />
        <el-table-column prop="totalAmount" label="总金额" width="150" align="right">
          <template #default="scope">¥{{ scope.row.totalAmount.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'received' ? 'success' : 'warning'">
              {{ scope.row.status === 'received' ? '已入库' : '待入库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="showDetails(scope.row)">查看明细</el-button>
            <el-button
                v-if="scope.row.status === 'pending'"
                type="success"
                size="small"
                @click="openReceiveDialog(scope.row)"
            >
              入库
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
      />
    </div>

    <el-dialog v-model="detailVisible" title="采购单明细" width="600px">
      <el-table :data="currentDetails" border>
        <el-table-column prop="model_name" label="车型" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="unit_price" label="单价" align="right">
          <template #default="scope">¥{{ scope.row.unit_price.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="subtotal" label="小计" align="right">
          <template #default="scope">¥{{ scope.row.subtotal.toLocaleString() }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog
        v-model="receiveVisible"
        title="车辆入库确认"
        width="800px"
        :close-on-click-modal="false"
    >
      <div style="margin-bottom: 15px; color: #666;">
        系统已自动生成 VIN 码，请确认信息后点击提交。
      </div>
      <el-table :data="receiveList" border height="400">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="modelName" label="车型" width="150" />
        <el-table-column label="VIN码 (自动生成)" width="200">
          <template #default="scope">
            <el-input v-model="scope.row.vin" placeholder="请输入VIN" />
          </template>
        </el-table-column>
        <el-table-column label="进价" width="120">
          <template #default="scope">
            <el-input v-model="scope.row.price" disabled />
          </template>
        </el-table-column>
        <el-table-column label="库位" width="150">
          <template #default="scope">
            <el-input v-model="scope.row.location" placeholder="例如: A-01" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="receiveVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitReceive">确认入库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '../../utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ status: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })

// 详情相关
const detailVisible = ref(false)
const currentDetails = ref([])

// 入库相关
const receiveVisible = ref(false)
const receiveList = ref([]) // 展开后的具体车辆列表
const currentOrderId = ref(null)
const submitting = ref(false)

onMounted(() => fetchData())

// 获取列表
const fetchData = async () => {
  loading.value = true
  try {
    const res = await get('/api/purchase-orders', {
      current: pagination.current,
      size: pagination.size,
      status: searchForm.status
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (err) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
const showDetails = async (row) => {
  const res = await get(`/api/purchase-orders/${row.orderId}/details`)
  currentDetails.value = res.data
  detailVisible.value = true
}

// 打开入库弹窗：自动生成车辆明细
const openReceiveDialog = async (row) => {
  currentOrderId.value = row.orderId
  try {
    // 1. 获取订单买了什么车
    const res = await get(`/api/purchase-orders/${row.orderId}/details`)
    const details = res.data

    // 2. 在前端“展开”成具体的车辆列表
    const list = []
    details.forEach(item => {
      for (let i = 0; i < item.quantity; i++) {
        list.push({
          model_id: item.model_id,
          modelName: item.model_name,
          price: item.unit_price,
          // 自动生成 VIN: 年月日 + 随机数
          vin: 'VIN' + Date.now().toString().slice(-6) + Math.floor(Math.random()*1000),
          location: 'A-' + Math.floor(Math.random() * 99 + 1) // 随机库位
        })
      }
    })
    receiveList.value = list
    receiveVisible.value = true
  } catch (err) {
    ElMessage.error('无法获取订单明细')
  }
}

// 提交入库
const submitReceive = async () => {
  submitting.value = true
  try {
    await post(`/api/purchase-orders/${currentOrderId.value}/receive`, {
      vehicleList: receiveList.value
    })
    ElMessage.success('入库成功！库存已更新')
    receiveVisible.value = false
    fetchData() // 刷新列表
  } catch (err) {
    console.error(err)
    ElMessage.error('入库失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.purchase-list-container { padding: 20px; background: #fff; }
.page-header { display: flex; justify-content: space-between; margin-bottom: 20px; }
.search-container { background: #f5f7fa; padding: 15px; margin-bottom: 20px; }
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>