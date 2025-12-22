<template>
  <div class="vehicle-form-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>{{ isEdit ? '编辑车辆' : '添加车辆' }}</h1>
    </div>

    <!-- 表单内容 -->
    <div class="form-container">
      <el-form
        :model="formData"
        :rules="formRules"
        ref="formRef"
        label-width="120px"
        class="vehicle-form"
      >
        <el-form-item label="VIN码" prop="vin">
          <el-input
            v-model="formData.vin"
            placeholder="请输入VIN码"
            maxlength="17"
            show-word-limit
            :disabled="isEdit"
          />
        </el-form-item>

        <el-form-item label="车型" prop="modelId">
          <el-select
            v-model="formData.modelId"
            placeholder="请选择车型"
            clearable
          >
            <el-option
              v-for="carModel in carModelList"
              :key="carModel.modelId"
              :label="carModel.modelName"
              :value="carModel.modelId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="颜色" prop="color">
          <el-input
            v-model="formData.color"
            placeholder="请输入车辆颜色"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="年份" prop="year">
          <el-input-number
            v-model="formData.year"
            :min="1990"
            :max="new Date().getFullYear() + 1"
            placeholder="请输入年份"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="里程(km)" prop="mileage">
          <el-input-number
            v-model="formData.mileage"
            :min="0"
            :step="100"
            placeholder="请输入里程数"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="进价(元)" prop="purchasePrice">
          <el-input-number
            v-model="formData.purchasePrice"
            :min="0"
            :step="1000"
            placeholder="请输入进价"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="售价(元)" prop="salePrice">
          <el-input-number
            v-model="formData.salePrice"
            :min="0"
            :step="1000"
            placeholder="请输入售价"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="车辆状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择车辆状态">
            <el-option label="在库" value="in_stock" />
            <el-option label="已售" value="sold" />
            <el-option label="预定" value="reserved" />
            <el-option label="维修中" value="maintenance" />
          </el-select>
        </el-form-item>

        <el-form-item label="进货日期" prop="purchaseDate">
          <el-date-picker
            v-model="formData.purchaseDate"
            type="date"
            placeholder="选择进货日期"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="销售日期" prop="saleDate">
          <el-date-picker
            v-model="formData.saleDate"
            type="date"
            placeholder="选择销售日期"
            style="width: 100%"
            :disabled="formData.status !== 'sold'"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            placeholder="请输入备注信息"
            type="textarea"
            rows="4"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { get, post, put } from '../../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 表单引用
const formRef = ref(null)

// 判断是否为编辑模式
const isEdit = ref(!!route.params.vin)

// 表单数据
const formData = reactive({
  vin: '',
  modelId: null,
  color: '',
  year: null,
  mileage: 0,
  purchasePrice: null,
  salePrice: null,
  status: 'in_stock',
  purchaseDate: null,
  saleDate: null,
  remark: ''
})

// 车型列表（用于下拉选择）
const carModelList = ref([])

// 表单验证规则
const formRules = reactive({
  vin: [
    { required: true, message: '请输入VIN码', trigger: 'blur' },
    { min: 17, max: 17, message: 'VIN码长度必须为17位', trigger: 'blur' }
  ],
  modelId: [
    { required: true, message: '请选择车型', trigger: 'change' }
  ],
  color: [
    { required: true, message: '请输入车辆颜色', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  year: [
    { required: true, message: '请输入年份', trigger: 'blur' },
    { type: 'number', min: 1990, message: '年份必须大于1990', trigger: 'blur' }
  ],
  purchasePrice: [
    { required: true, message: '请输入进价', trigger: 'blur' },
    { type: 'number', min: 0, message: '进价必须大于0', trigger: 'blur' }
  ],
  salePrice: [
    { required: true, message: '请输入售价', trigger: 'blur' },
    { type: 'number', min: 0, message: '售价必须大于0', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (formData.purchasePrice && value < formData.purchasePrice) {
          callback(new Error('售价不能低于进价'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  status: [
    { required: true, message: '请选择车辆状态', trigger: 'change' }
  ],
  purchaseDate: [
    { required: true, message: '请选择进货日期', trigger: 'change' }
  ],
  saleDate: [
    {
      validator: (rule, value, callback) => {
        if (formData.status === 'sold' && !value) {
          callback(new Error('已售车辆必须填写销售日期'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
})

// 监听车辆状态变化，当状态变为已售时，如果销售日期为空，则自动填充当前日期
watch(
  () => formData.status,
  (newStatus, oldStatus) => {
    if (newStatus === 'sold' && !formData.saleDate) {
      formData.saleDate = new Date()
    }
  }
)

// 页面加载时获取数据
onMounted(() => {
  fetchCarModels()
  if (isEdit.value) {
    fetchVehicleData()
  }
})

// 获取车型列表
const fetchCarModels = async () => {
  try {
    const response = await get('/car-models')
    carModelList.value = response.data
  } catch (error) {
    ElMessage.error('获取车型列表失败')
    console.error('获取车型列表失败:', error)
  }
}

// 获取车辆数据（编辑模式）
const fetchVehicleData = async () => {
  try {
    const response = await get(`/vehicles/${route.params.vin}`)
    Object.assign(formData, response.data)
    // 处理日期格式
    if (formData.purchaseDate) {
      formData.purchaseDate = new Date(formData.purchaseDate)
    }
    if (formData.saleDate) {
      formData.saleDate = new Date(formData.saleDate)
    }
  } catch (error) {
    ElMessage.error('获取车辆信息失败')
    console.error('获取车辆信息失败:', error)
    router.push('/vehicles')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          // 编辑模式
          await put(`/vehicles/${formData.vin}`, formData)
          ElMessage.success('编辑车辆成功')
        } else {
          // 添加模式
          await post('/vehicles', formData)
          ElMessage.success('添加车辆成功')
        }
        router.push('/vehicles')
      } catch (error) {
        ElMessage.error(isEdit.value ? '编辑车辆失败' : '添加车辆失败')
        console.error('保存车辆失败:', error)
      }
    }
  })
}

// 取消操作
const handleCancel = () => {
  router.push('/vehicles')
}
</script>

<style scoped>
.vehicle-form-container {
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.form-container {
  max-width: 800px;
}

.vehicle-form {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>