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
          <el-select v-model="searchForm.modelId" placeholder="请选择车型" clearable>
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

        <el-table-column prop="salePrice" label="售价(元)" width="120" align="right">
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
            <div v-if="scope.row.status !== 'sold'">
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
                  @click="updateStatus(scope.row.vin, 'sold')"
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, del, put } from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 表格数据
const tableData = ref([])
// 车型列表
const carModelList = ref([])
// 搜索表单
const searchForm = ref({
  vin: '',
  modelId: '',
  status: ''
})
// 分页参数
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const statusMap = {
  'in_stock': { text: '在库', type: 'success' },
  'sold': { text: '已售', type: 'info' },
  'reserved': { text: '预定', type: 'warning' },
  'in_transit': { text: '在途', type: 'primary' }
}

onMounted(() => {
  fetchCarModels()
  fetchData()
})

const fetchCarModels = async () => {
  try {
    const response = await get('/api/car-models/page', { current: 1, size: 100 })
    carModelList.value = response.data.records
  } catch (error) {
    ElMessage.error('获取车型列表失败')
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

const getStatusText = (status) => statusMap[status]?.text || status
const getStatusType = (status) => statusMap[status]?.type || 'default'

const handleSearch = () => {
  pagination.value.currentPage = 1
  fetchData()
}
const handleReset = () => {
  searchForm.value = { vin: '', modelId: '', status: '' }
  pagination.value.currentPage = 1
  fetchData()
}
const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  fetchData()
}
const handleCurrentChange = (current) => {
  pagination.value.currentPage = current
  fetchData()
}
const goToAdd = () => router.push('/vehicles/add')
const goToEdit = (vin) => router.push(`/vehicles/edit/${vin}`)

// 更新车辆状态（出售）
const updateStatus = (vin, status) => {
  const statusText = status === 'sold' ? '售出' : getStatusText(status)

  ElMessageBox.confirm(`确定要将该车辆标记为【${statusText}】吗？\n标记售出后将无法再进行编辑或删除。`, '确认出售', {
    confirmButtonText: '确定出售',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await put(`/api/vehicles/${vin}/status?status=${status}`)
      ElMessage.success(`车辆已成功标记为${statusText}`)
      fetchData() // 刷新列表，此时该行操作列将变为空
    } catch (error) {
      ElMessage.error('更新车辆状态失败')
      console.error('Error:', error)
    }
  }).catch(() => {})
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
  }).catch(() => {})
}
</script>

<style scoped>
.vehicle-list-container {
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
/* 其他样式保持不变 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h1 {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
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
.table-container {
  margin-bottom: 20px;
}
.pagination-container {
  display: flex;
  justify-content: flex-end;
}
</style>