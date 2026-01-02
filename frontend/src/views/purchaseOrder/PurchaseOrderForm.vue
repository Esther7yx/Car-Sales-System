<template>
  <div class="purchase-order-form">
    <div class="header">
      <h2>{{ isEdit ? '编辑进货订单' : '添加进货订单' }}</h2>
    </div>

    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="供应商" prop="supplierId">
            <el-select v-model="form.supplierId" placeholder="请选择供应商" filterable>
              <el-option
                v-for="supplier in suppliers"
                :key="supplier.id"
                :label="supplier.name"
                :value="supplier.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="进货日期" prop="purchaseDate">
            <el-date-picker
              v-model="form.purchaseDate"
              type="date"
              placeholder="选择进货日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="车辆信息" prop="vehicleId">
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
          <el-form-item label="进货价格" prop="purchasePrice">
            <el-input-number
              v-model="form.purchasePrice"
              :min="0"
              :precision="2"
              placeholder="请输入进货价格"
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
          <el-form-item label="采购员" prop="purchaser">
            <el-input v-model="form.purchaser" placeholder="请输入采购员姓名" />
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
  supplierId: '',
  vehicleId: '',
  purchaseDate: '',
  purchasePrice: 0,
  paymentMethod: '',
  purchaser: '',
  remarks: ''
})

const rules = reactive({
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
  vehicleId: [{ required: true, message: '请选择车辆', trigger: 'change' }],
  purchaseDate: [{ required: true, message: '请选择进货日期', trigger: 'change' }],
  purchasePrice: [{ required: true, message: '请输入进货价格', trigger: 'blur' }],
  paymentMethod: [{ required: true, message: '请选择付款方式', trigger: 'change' }]
})

const suppliers = ref([])
const availableVehicles = ref([])

const handleVehicleChange = (vehicleId) => {
  const vehicle = availableVehicles.value.find(v => v.vin === vehicleId)
  if (vehicle) {
    form.purchasePrice = vehicle.purchasePrice || 0
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    ElMessage.success(isEdit.value ? '进货订单更新成功' : '进货订单创建成功')
    router.push('/purchase-orders')
  } catch (error) {
    ElMessage.error('表单验证失败')
  }
}

const handleCancel = () => {
  router.push('/purchase-orders')
}

const fetchSuppliers = async () => {
  // 模拟获取供应商数据
  suppliers.value = [
    { id: 1, name: '奥迪厂商', contact: '张经理', phone: '13800138001' },
    { id: 2, name: '宝马厂商', contact: '李经理', phone: '13800138002' },
    { id: 3, name: '奔驰厂商', contact: '王经理', phone: '13800138003' }
  ]
}

const fetchAvailableVehicles = async () => {
  // 模拟获取可采购车辆数据
  availableVehicles.value = [
    { 
      vin: '22222222222222222', 
      brand: '宝马', 
      model: '3系 2026款', 
      purchasePrice: 200000 
    }
  ]
}

onMounted(() => {
  fetchSuppliers()
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
    supplierId: 1,
    vehicleId: '22222222222222222',
    purchaseDate: '2026-01-01',
    purchasePrice: 200000,
    paymentMethod: 'full',
    purchaser: '采购员A',
    remarks: '测试进货订单'
  })
}
</script>

<style scoped>
.purchase-order-form {
  padding: 20px;
}

.header {
  margin-bottom: 30px;
}

.el-form {
  max-width: 800px;
}
</style>