<template>
  <div class="sale-order-form">
    <div class="header">
      <h2>{{ isEdit ? '编辑销售订单' : '添加销售订单' }}</h2>
    </div>

    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="客户" prop="customerId">
            <el-select v-model="form.customerId" placeholder="请选择客户" filterable>
              <el-option
                v-for="customer in customers"
                :key="customer.id"
                :label="customer.name"
                :value="customer.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="销售日期" prop="saleDate">
            <el-date-picker
              v-model="form.saleDate"
              type="date"
              placeholder="选择销售日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="销售车辆" prop="vehicleId">
        <el-select v-model="form.vehicleId" placeholder="请选择车辆" filterable @change="handleVehicleChange">
          <el-option
            v-for="vehicle in availableVehicles"
            :key="vehicle.vin"
            :label="`${vehicle.brand} ${vehicle.model} - ${vehicle.vin}`"
            :value="vehicle.vin"
          />
        </el-select>
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="销售价格" prop="salePrice">
            <el-input-number
              v-model="form.salePrice"
              :min="0"
              :precision="2"
              placeholder="请输入销售价格"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="付款方式" prop="paymentMethod">
            <el-select v-model="form.paymentMethod" placeholder="请选择付款方式">
              <el-option label="全款" value="full" />
              <el-option label="分期" value="installment" />
              <el-option label="贷款" value="loan" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="销售员" prop="salesperson">
            <el-input v-model="form.salesperson" placeholder="请输入销售员姓名" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="备注" prop="remarks">
        <el-input
          v-model="form.remarks"
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const isEdit = ref(false)

const form = reactive({
  customerId: '',
  vehicleId: '',
  saleDate: '',
  salePrice: 0,
  paymentMethod: '',
  salesperson: '',
  remarks: ''
})

const rules = reactive({
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  vehicleId: [{ required: true, message: '请选择车辆', trigger: 'change' }],
  saleDate: [{ required: true, message: '请选择销售日期', trigger: 'change' }],
  salePrice: [{ required: true, message: '请输入销售价格', trigger: 'blur' }],
  paymentMethod: [{ required: true, message: '请选择付款方式', trigger: 'change' }]
})

const customers = ref([])
const availableVehicles = ref([])

const handleVehicleChange = (vehicleId) => {
  const vehicle = availableVehicles.value.find(v => v.vin === vehicleId)
  if (vehicle) {
    form.salePrice = vehicle.salePrice || 0
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    ElMessage.success(isEdit.value ? '销售订单更新成功' : '销售订单创建成功')
    router.push('/sale-orders')
  } catch (error) {
    ElMessage.error('表单验证失败')
  }
}

const handleCancel = () => {
  router.push('/sale-orders')
}

const fetchCustomers = async () => {
  // 模拟获取客户数据
  customers.value = [
    { id: 1, name: '张三', phone: '13800138001' },
    { id: 2, name: '李四', phone: '13800138002' },
    { id: 3, name: '王五', phone: '13800138003' }
  ]
}

const fetchAvailableVehicles = async () => {
  // 模拟获取可售车辆数据
  availableVehicles.value = [
    { 
      vin: '11111111111111111', 
      brand: '奥迪', 
      model: 'A3 2026款', 
      salePrice: 130000 
    }
  ]
}

onMounted(() => {
  fetchCustomers()
  fetchAvailableVehicles()
  
  if (route.params.id) {
    isEdit.value = true
    // 如果是编辑模式，加载订单数据
    loadOrderData()
  }
})

const loadOrderData = async () => {
  // 模拟加载订单数据
  Object.assign(form, {
    customerId: 1,
    vehicleId: '11111111111111111',
    saleDate: '2026-01-01',
    salePrice: 130000,
    paymentMethod: 'full',
    salesperson: '销售员A',
    remarks: '测试订单'
  })
}
</script>

<style scoped>
.sale-order-form {
  padding: 20px;
}

.header {
  margin-bottom: 30px;
}

.el-form {
  max-width: 800px;
}
</style>