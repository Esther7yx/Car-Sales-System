<template>
  <div class="vehicle-list-container">
    <div class="page-header">
      <h1>车辆管理</h1>
      <el-button type="primary" @click="goToAdd">添加车辆</el-button>
    </div>

    <div class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="VIN码">
          <el-input v-model="searchForm.vin" placeholder="请输入VIN码" clearable />
        </el-form-item>
        <el-form-item label="车型">
          <el-select v-model="searchForm.modelId" placeholder="请选择车型" clearable filterable>
            <el-option
                v-for="carModel in carModelList"
                :key="carModel.modelId"
                :label="carModel.modelName"
                :value="carModel.modelId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆状态">
          <el-select v-model="searchForm.status" placeholder="请选择车辆状态" clearable>
            <el-option label="在库" value="in_stock" />
            <el-option label="已售" value="sold" />
            <el-option label="预定" value="reserved" />
            <el-option label="在途" value="in_transit" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" stripe border style="width: 100%">
        <el-table-column prop="vin" label="VIN码" width="180" fixed />

        <el-table-column label="车型" width="150">
          <template #default="scope">
            {{ scope.row.carModel ? scope.row.carModel.modelName : '-' }}
          </template>
        </el-table-column>

        <el-table-column label="厂商" width="150">
          <template #default="scope">
            {{ scope.row.manufacturer ? scope.row.manufacturer.manufacturerName : '-' }}
          </template>
        </el-table-column>

        <el-table-column label="年份" width="100" align="center">
          <template #default="scope">
            {{ scope.row.carModel ? scope.row.carModel.year : '-' }}
          </template>
        </el-table-column>

        <el-table-column prop="warehouseLocation" label="仓库位置" width="150" />

        <el-table-column prop="purchasePrice" label="进价(元)" width="120" align="right">
          <template #default="scope">
            {{ scope.row.purchasePrice ? scope.row.purchasePrice.toLocaleString() : '-' }}
          </template>
        </el-table-column>

        <el-table-column prop="salePrice" label="指导价(元)" width="120" align="right">
          <template #default="scope">
            {{ scope.row.salePrice ? scope.row.salePrice.toLocaleString() : '-' }}
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="purchaseDate" label="进货日期" width="120" align="center" />
        <el-table-column prop="saleDate" label="销售日期" width="120" align="center" />

        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="scope">
            <div v-if="scope.row.status === 'in_stock'">
              <el-button
                  type="primary"
                  size="small"
                  @click="goToEdit(scope.row.vin)"
              >
                编辑
              </el-button>

              <el-button
                  type="success"
                  size="small"
                  @click="openSaleDialog(scope.row)"
              >
                出售
              </el-button>

              <el-button
                  type="danger"
                  size="small"
                  @click="handleDelete(scope.row.vin)"
              >
                删除
              </el-button>
            </div>
            <div v-else-if="scope.row.status === 'sold'">
              <el-tag type="info">已完成销售</el-tag>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog
        v-model="saleDialogVisible"
        title="办理车辆销售"
        width="500px"
        :close-on-click-modal="false"
    >
      <el-form :model="saleForm" label-width="100px" ref="saleFormRef">
        <el-form-item label="VIN码">
          <el-input v-model="currentVehicle.vin" disabled />
        </el-form-item>
        <el-form-item label="车型名称">
          <el-input v-model="currentVehicle.modelName" disabled />
        </el-form-item>

        <el-form-item label="选择客户" required>
          <el-select v-model="saleForm.customerId" placeholder="请选择购车客户" filterable style="width: 100%">
            <el-option
                v-for="item in customerList"
                :key="item.customerId"
                :label="item.name + ' (' + item.phone + ')'"
                :value="item.customerId"
            />
          </el-select>
          <div style="font-size: 12px; color: #999; margin-top: 5px;">
            没有找到客户？请先去<el-link type="primary" @click="router.push('/customers/add')">添加客户</el-link>
          </div>
        </el-form-item>

        <el-form-item label="成交价格" required>
          <el-input-number
              v-model="saleForm.actualPrice"
              :min="0"
              :precision="2"
              :step="1000"
              style="width: 100%"
          />
          <div style="font-size: 12px; color: #666;">
            指导价: {{ currentVehicle.guidePrice }} 元
          </div>
        </el-form-item>

        <el-form-item label="支付方式" required>
          <el-select v-model="saleForm.paymentMethod" style="width: 100%">
            <el-option label="现金/转账" value="cash" />
            <el-option label="按揭贷款" value="loan" />
            <el-option label="分期付款" value="installment" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="saleDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitSale">
            确认销售
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, del, post } from '../../utils/request' // 引入 post
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// --- 基础数据 ---
const tableData = ref([])
const carModelList = ref([])
const customerList = ref([]) // 客户列表
const pagination = ref({ currentPage: 1, pageSize: 10, total: 0 })
const searchForm = ref({ vin: '', modelId: '', status: '' })

// --- 销售弹窗相关 ---
const saleDialogVisible = ref(false)
const submitLoading = ref(false)
const currentVehicle = reactive({ vin: '', modelName: '', guidePrice: 0 })
const saleForm = reactive({
  customerId: undefined,
  actualPrice: 0,
  paymentMethod: 'cash'
})

// 状态字典
const statusMap = {
  'in_stock': { text: '在库', type: 'success' },
  'sold': { text: '已售', type: 'info' },
  'reserved': { text: '预定', type: 'warning' },
  'in_transit': { text: '在途', type: 'primary' }
}

onMounted(() => {
  fetchCarModels()
  fetchCustomers() // 加载客户
  fetchData()
})

// --- API 请求 ---

const fetchCarModels = async () => {
  try {
    const response = await get('/api/car-models/page', { current: 1, size: 100 })
    carModelList.value = response.data.records
  } catch (error) {
    console.error(error)
  }
}

// 获取客户列表（用于下拉框）
const fetchCustomers = async () => {
  try {
    const response = await get('/api/customers') // 假设返回所有客户
    customerList.value = response.data || []
  } catch (error) {
    console.error('加载客户失败', error)
  }
}

const fetchData = async () => {
  try {
    const params = {
      current: pagination.value.currentPage,
      size: pagination.value.pageSize,
      vin: searchForm.value.vin,
      modelId: searchForm.value.modelId || undefined,
      status: searchForm.value.status || undefined
    }
    const response = await get('/api/vehicles/page', params)
    tableData.value = response.data.records
    pagination.value.total = response.data.total
  } catch (error) {
    ElMessage.error('获取车辆列表失败')
  }
}

// --- 业务操作 ---

// 1. 打开销售弹窗
const openSaleDialog = (row) => {
  // 初始化当前选中车辆信息
  currentVehicle.vin = row.vin
  currentVehicle.modelName = row.carModel ? row.carModel.modelName : '未知车型'
  currentVehicle.guidePrice = row.salePrice || 0

  // 初始化表单
  saleForm.customerId = undefined
  saleForm.actualPrice = row.salePrice || 0 // 默认填入指导价
  saleForm.paymentMethod = 'cash'

  saleDialogVisible.value = true
}

// 2. 提交销售订单
const submitSale = async () => {
  if (!saleForm.customerId) {
    ElMessage.warning('请选择客户')
    return
  }
  if (!saleForm.actualPrice) {
    ElMessage.warning('请输入成交价格')
    return
  }

  try {
    submitLoading.value = true

    // 构建复杂的销售载荷，匹配后端存储过程要求
    const payload = {
      customerId: saleForm.customerId,
      operatorId: 1, // 简化处理，默认管理员ID，实际应取当前登录用户
      paymentMethod: saleForm.paymentMethod,
      // 关键：将商品明细转为 JSON 字符串发送给后端 Mapper
      saleItemsJson: JSON.stringify([
        {
          vin: currentVehicle.vin,
          unit_price: saleForm.actualPrice,
          discount: 0 // 简化处理
        }
      ])
    }

    // 调用我们在 Step 2.2 写好的 API
    await post('/api/sales/create', payload)

    ElMessage.success('销售办理成功！')
    saleDialogVisible.value = false
    fetchData() // 刷新列表，该车状态应变为“已售”

  } catch (error) {
    console.error(error)
    ElMessage.error('销售失败：' + (error.response?.data?.message || '请检查库存状态'))
  } finally {
    submitLoading.value = false
  }
}

// 删除车辆
const handleDelete = (vin) => {
  ElMessageBox.confirm('确定要删除该车辆吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await del(`/api/vehicles/${vin}`)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// --- 辅助函数 ---
const getStatusText = (status) => statusMap[status]?.text || status
const getStatusType = (status) => statusMap[status]?.type || 'default'

const handleSearch = () => { pagination.value.currentPage = 1; fetchData() }
const handleReset = () => { searchForm.value = { vin: '', modelId: '', status: '' }; handleSearch() }
const handleSizeChange = (size) => { pagination.value.pageSize = size; fetchData() }
const handleCurrentChange = (current) => { pagination.value.currentPage = current; fetchData() }
const goToAdd = () => router.push('/vehicles/add')
const goToEdit = (vin) => router.push(`/vehicles/edit/${vin}`)

</script>

<style scoped>
.vehicle-list-container {
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.search-container {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}
</style>