<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>新建销售单</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 900px;">

        <el-form-item label="选择客户" prop="customerId">
          <el-select
              v-model="form.customerId"
              filterable
              placeholder="请搜索并选择客户 (姓名/电话)"
              :loading="customerLoading"
              style="width: 100%;"
              no-match-text="无匹配客户"
              no-data-text="暂无客户数据"
              clearable
          >
            <el-option
                v-for="item in customerOptions"
                :key="item.customerId"
                :label="item.name + ' (' + item.phone + ')'"
                :value="item.customerId"
            />
          </el-select>
          <el-button type="text" style="margin-left: 10px;" @click="$router.push('/customers/add')">新建客户</el-button>
        </el-form-item>

        <el-form-item label="销售车辆" required>
          <div class="vehicle-selection">
            <el-button type="primary" plain @click="openVehicleSelector">
              <el-icon><Plus /></el-icon> 添加车辆
            </el-button>

            <el-table :data="form.vehicles" border style="margin-top: 10px;">
              <el-table-column prop="modelName" label="车型" />
              <el-table-column prop="vin" label="VIN码" width="180" />
              <el-table-column prop="purchasePrice" label="进价" width="120" align="right">
                <template #default="scope">¥{{ formatMoney(scope.row.purchasePrice) }}</template>
              </el-table-column>
              <el-table-column label="实际成交价" width="180">
                <template #default="scope">
                  <el-input-number
                      v-model="scope.row.actualPrice"
                      :min="0"
                      :precision="2"
                      :step="1000"
                      style="width: 140px;"
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" align="center">
                <template #default="scope">
                  <el-button type="danger" link @click="removeVehicle(scope.$index)">移除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="total-bar" v-if="form.vehicles.length > 0">
              订单总额: <span class="total-price">¥ {{ formatMoney(totalAmount) }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="支付方式" prop="paymentMethod">
          <el-radio-group v-model="form.paymentMethod">
            <el-radio label="cash">现金/转账</el-radio>
            <el-radio label="loan">银行贷款</el-radio>
            <el-radio label="installment">分期付款</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submitForm">确认成交</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="vehicleDialogVisible" title="选择库存车辆" width="800px">
      <div style="margin-bottom: 15px;">
        <el-input v-model="vehicleSearch" placeholder="搜索车型或VIN..." style="width: 250px;" clearable @input="fetchVehicles">
          <template #append><el-button :icon="Search" /></template>
        </el-input>
      </div>
      <el-table :data="vehicleList" v-loading="vehicleLoading" border height="400">
        <el-table-column prop="modelName" label="车型" />
        <el-table-column prop="vin" label="VIN码" width="180" />
        <el-table-column prop="warehouseLocation" label="库位" width="120" />
        <el-table-column prop="daysInStock" label="库龄(天)" width="100" sortable />
        <el-table-column label="操作" width="100" align="center">
          <template #default="scope">
            <el-button
                type="primary"
                size="small"
                :disabled="isVehicleSelected(scope.row.vin)"
                @click="selectVehicle(scope.row)"
            >
              {{ isVehicleSelected(scope.row.vin) ? '已选' : '选择' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { get, post } from '../../utils/request'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  customerId: null,
  paymentMethod: 'cash',
  vehicles: []
})

const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  paymentMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
}

// --- 客户列表 (与客户管理列表一致) ---
const customerLoading = ref(false)
const customerOptions = ref([])

const initCustomers = async () => {
  customerLoading.value = true
  try {
    // 调用 /api/customers 接口
    // 注意：虽然传了 current 和 size，但您的后端 CustomerController 目前忽略了这些参数，直接返回所有列表，这对“下拉选择”功能正好适用
    const res = await get('/api/customers', { current: 1, size: 10000 })

    // 【修改点】后端返回的是 List，不是 Page，所以直接取 res.data
    // 为了稳健，我们可以做一个简单的判断：
    if (Array.isArray(res.data)) {
      // 情况 A: 后端直接返回数组 (当前情况)
      customerOptions.value = res.data
    } else if (res.data && Array.isArray(res.data.records)) {
      // 情况 B: 后端返回分页对象 (以防未来您改成了分页)
      customerOptions.value = res.data.records
    } else {
      customerOptions.value = []
    }

  } catch (error) {
    console.error(error)
    ElMessage.error('加载客户列表失败')
  } finally {
    customerLoading.value = false
  }
}

// --- 车辆选择 ---
const vehicleDialogVisible = ref(false)
const vehicleLoading = ref(false)
const vehicleList = ref([])
const vehicleSearch = ref('')

const openVehicleSelector = () => {
  vehicleDialogVisible.value = true
  fetchVehicles()
}

const fetchVehicles = async () => {
  vehicleLoading.value = true
  try {
    const res = await get('/api/warehouse/details', {
      keyword: vehicleSearch.value,
      size: 50
    })
    vehicleList.value = res.data.records.filter(v => v.status === 'in_stock')
  } finally {
    vehicleLoading.value = false
  }
}

const selectVehicle = (row) => {
  form.vehicles.push({
    vin: row.vin,
    modelName: row.modelName,
    purchasePrice: row.purchasePrice,
    actualPrice: row.purchasePrice // 默认成交价等于进价
  })
  vehicleDialogVisible.value = false
  ElMessage.success('已添加车辆')
}

const removeVehicle = (index) => {
  form.vehicles.splice(index, 1)
}

const isVehicleSelected = (vin) => {
  return form.vehicles.some(v => v.vin === vin)
}

// --- 计算总价 ---
const totalAmount = computed(() => {
  return form.vehicles.reduce((sum, item) => sum + Number(item.actualPrice || 0), 0)
})

// --- 提交表单 ---
const submitForm = async () => {
  if (form.vehicles.length === 0) {
    ElMessage.warning('请至少选择一辆车')
    return
  }

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await ElMessageBox.confirm(`确认创建销售单？总金额: ¥${formatMoney(totalAmount.value)}`, '确认', {
          confirmButtonText: '确定成交',
          cancelButtonText: '取消',
          type: 'success'
        })

        submitting.value = true
        await post('/api/sale-orders/create', {
          customerId: form.customerId,
          paymentMethod: form.paymentMethod,
          vehicles: form.vehicles
        })

        ElMessage.success('销售单创建成功！')
        router.push('/sales')

      } catch (err) {
        if (err !== 'cancel') {
          console.error(err)
          ElMessage.error('创建失败')
        }
      } finally {
        submitting.value = false
      }
    }
  })
}

const resetForm = () => {
  formRef.value.resetFields()
  form.vehicles = []
}

const formatMoney = (val) => {
  return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2 })
}

onMounted(() => {
  initCustomers()
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.vehicle-selection { border: 1px solid #dcdfe6; padding: 15px; border-radius: 4px; }
.total-bar { margin-top: 15px; text-align: right; font-size: 16px; }
.total-price { color: #f56c6c; font-weight: bold; font-size: 20px; margin-left: 10px; }
</style>