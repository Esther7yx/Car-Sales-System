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

      <el-form-item label="车型信息" prop="modelId">
        <el-select v-model="form.modelId" placeholder="请选择车型" filterable @change="handleModelChange">
          <el-option
            v-for="model in availableModels"
            :key="model.modelId"
            :label="`${model.manufacturerName} ${model.modelName} ${model.year}`"
            :value="model.modelId"
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
          <el-form-item label="采购员" prop="purchaser">
            <el-input v-model="form.purchaser" placeholder="请输入采购员姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="备注" prop="remarks">
            <el-input v-model="form.remarks" placeholder="请输入备注信息" />
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
import { get, post } from '../../utils/request'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const isEdit = ref(false)

const form = reactive({
  supplierId: '',
  modelId: '',
  purchaseDate: '',
  purchasePrice: 0,
  purchaser: '',
  remarks: ''
})

const rules = reactive({
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
  modelId: [{ required: true, message: '请选择车型', trigger: 'change' }],
  purchaseDate: [{ required: true, message: '请选择进货日期', trigger: 'change' }],
  purchasePrice: [{ required: true, message: '请输入进货价格', trigger: 'blur' }],
  purchaser: [{ required: true, message: '请输入采购员姓名', trigger: 'blur' }]
})

const suppliers = ref([])
const availableModels = ref([])

const handleModelChange = (modelId) => {
  const model = availableModels.value.find(m => m.modelId === modelId)
  if (model) {
    form.purchasePrice = model.guidePrice || 0
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    // 生成订单编号（格式：PO + 时间戳）
    const orderNumber = 'PO' + Date.now()
    
    const purchaseOrderData = {
      orderNumber: orderNumber,
      manufacturerId: form.supplierId,
      operatorId: 1, // 暂时使用固定操作员ID
      paymentMethod: form.paymentMethod,
      totalAmount: form.purchasePrice,
      status: 'pending'
    }
    
    if (isEdit.value) {
      await post('/api/purchase', purchaseOrderData)
      ElMessage.success('进货订单更新成功')
    } else {
      await post('/api/purchase', purchaseOrderData)
      ElMessage.success('进货订单创建成功')
    }
    
    router.push('/purchase-orders')
  } catch (error) {
    ElMessage.error('表单提交失败：' + (error.response?.data?.message || error.message))
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

const fetchAvailableModels = async () => {
  try {
    const response = await get('/api/car-models/page?current=1&size=100')
    availableModels.value = response.data.records.map(model => ({
      modelId: model.modelId,
      manufacturerName: model.manufacturerName,
      modelName: model.modelName,
      year: model.year,
      guidePrice: model.guidePrice
    }))
  } catch (error) {
    ElMessage.error('获取车型列表失败')
    availableModels.value = []
  }
}

onMounted(() => {
  fetchSuppliers()
  fetchAvailableModels()
  
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