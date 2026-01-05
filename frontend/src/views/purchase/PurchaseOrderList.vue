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
            <el-option label="待处理" value="pending" />
            <el-option label="已入库" value="received" />
            <el-option label="已撤销" value="cancelled" />
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
            <el-tag type="success" v-if="scope.row.status === 'received'">已入库</el-tag>
            <el-tag type="warning" v-else-if="scope.row.status === 'pending'">待处理</el-tag>
            <el-tag type="info" v-else-if="scope.row.status === 'cancelled'">已撤销</el-tag>
            <el-tag v-else>{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
          <template #default="scope">{{ formatDate(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="showDetails(scope.row)">明细</el-button>

            <template v-if="scope.row.status === 'pending'">
              <el-button
                  type="success"
                  size="small"
                  @click="openReceiveDialog(scope.row)"
              >
                同意
              </el-button>
              <el-popconfirm
                  title="确定要撤销此采购单吗？"
                  @confirm="handleCancel(scope.row)"
              >
                <template #reference>
                  <el-button type="danger" size="small">撤销</el-button>
                </template>
              </el-popconfirm>
            </template>

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
        title="审核通过 - 确认入库信息"
        width="800px"
        :close-on-click-modal="false"
    >
      <div style="margin-bottom: 15px; color: #666; background: #e1f3d8; padding: 10px; border-radius: 4px;">
        <el-icon><InfoFilled /></el-icon>
        同意采购单将自动生成库存记录。系统已自动生成 VIN 码，请确认无误后点击“确认同意”。
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
        <el-table-column label="库位备注" width="150">
          <template #default="scope">
            <el-input v-model="scope.row.location" placeholder="例如: A区" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="receiveVisible = false">取消</el-button>
        <el-button type="success" :loading="submitting" @click="submitReceive">确认同意并入库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '../../utils/request'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ status: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })

// 详情相关
const detailVisible = ref(false)
const currentDetails = ref([])

// 入库相关
const receiveVisible = ref(false)
const receiveList = ref([])
const currentOrderId = ref(null)
const submitting = ref(false)

onMounted(() => fetchData())

const formatDate = (val) => {
  if(!val) return ''
  return new Date(val).toLocaleString('zh-CN', { hour12: false })
}

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

// 撤销操作
const handleCancel = async (row) => {
  try {
    await post(`/api/purchase-orders/${row.orderId}/cancel`)
    ElMessage.success('撤销成功')
    fetchData()
  } catch (err) {
    ElMessage.error('撤销失败')
  }
}

// 打开“同意”弹窗（复用入库逻辑）
const openReceiveDialog = async (row) => {
  currentOrderId.value = row.orderId
  try {
    const res = await get(`/api/purchase-orders/${row.orderId}/details`)
    const details = res.data

    const list = []
    // 获取当前时间戳
    const timestamp = Date.now().toString().slice(-6)

    // 全局计数器，确保循环内唯一
    let globalIndex = 0

    details.forEach(item => {
      for (let i = 0; i < item.quantity; i++) {
        globalIndex++
        // 【修改】VIN生成逻辑：加入 globalIndex 避免同一毫秒内的重复
        // 格式：VIN + 时间戳后6位 + 索引(2位) + 随机数(3位)
        const uniqueVin = 'VIN' + timestamp +
            globalIndex.toString().padStart(2, '0') +
            Math.floor(Math.random() * 1000).toString().padStart(3, '0')

        list.push({
          model_id: item.model_id,
          modelName: item.model_name,
          price: item.unit_price,
          vin: uniqueVin, // 使用更唯一的VIN
          location: '默认库位'
        })
      }
    })
    receiveList.value = list
    receiveVisible.value = true
  } catch (err) {
    ElMessage.error('无法获取订单明细')
  }
}

// 提交同意（入库）
const submitReceive = async () => {
  submitting.value = true
  try {
    await post(`/api/purchase-orders/${currentOrderId.value}/receive`, {
      vehicleList: receiveList.value
    })
    ElMessage.success('操作成功！仓库数据已同步')
    receiveVisible.value = false
    fetchData()
  } catch (err) {
    console.error(err)
    ElMessage.error('操作失败')
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