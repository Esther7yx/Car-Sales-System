<template>
  <div class="vehicle-form-container">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑车辆' : '添加车辆' }}</h1>
    </div>

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
              placeholder="请输入17位车架号"
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
              style="width: 100%"
              filterable
          >
            <el-option
                v-for="carModel in carModelList"
                :key="carModel.modelId"
                :label="carModel.modelName"
                :value="carModel.modelId"
            >
              <span style="float: left">{{ carModel.modelName }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                {{ carModel.manufacturer?.manufacturerName }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="仓库位置" prop="warehouseLocation">
          <el-input
              v-model="formData.warehouseLocation"
              placeholder="请输入仓库位置（如：A区-01车位）"
              maxlength="50"
              show-word-limit
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
          <el-select v-model="formData.status" placeholder="请选择车辆状态" style="width: 100%">
            <el-option label="在库" value="in_stock" />
            <el-option label="已售" value="sold" />
            <el-option label="预定" value="reserved" />
            <el-option label="在途" value="in_transit" />
          </el-select>
        </el-form-item>

        <el-form-item label="进货日期" prop="purchaseDate">
          <el-date-picker
              v-model="formData.purchaseDate"
              type="date"
              placeholder="选择进货日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="销售日期" prop="saleDate">
          <el-date-picker
              v-model="formData.saleDate"
              type="date"
              placeholder="选择销售日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
              :disabled="formData.status !== 'sold'"
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

// 表单数据（严格对齐数据库字段）
const formData = reactive({
  vin: '',
  modelId: null,
  purchasePrice: undefined,
  salePrice: undefined,
  status: 'in_transit', // 默认为在途
  warehouseLocation: '', // 新增字段
  purchaseDate: new Date().toISOString().split('T')[0], // 默认为今天
  saleDate: null
})

// 车型列表（用于下拉选择）
const carModelList = ref([])

// 表单验证规则
const formRules = reactive({
  vin: [
    { required: true, message: '请输入VIN码', trigger: 'blur' },
    { min: 17, max: 17, message: 'VIN码必须为17位字符', trigger: 'blur' },
    { pattern: /^[A-Z0-9]+$/, message: '只能包含大写字母和数字', trigger: 'blur' }
  ],
  modelId: [
    { required: true, message: '请选择车型', trigger: 'change' }
  ],
  warehouseLocation: [
    { max: 100, message: '长度不能超过100个字符', trigger: 'blur' }
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
          callback(new Error('售价建议不低于进价')) // 仅为建议，或根据业务需求设为强制
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

// 监听车辆状态变化，当状态变为已售时，自动填充当前日期
watch(
    () => formData.status,
    (newStatus) => {
      if (newStatus === 'sold' && !formData.saleDate) {
        formData.saleDate = new Date().toISOString().split('T')[0]
      } else if (newStatus !== 'sold') {
        formData.saleDate = null
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
    // 【修正】使用 page 接口获取列表（模拟获取全部，size设大一点）
    const response = await get('/api/car-models/page', { current: 1, size: 500 })
    carModelList.value = response.data.records
  } catch (error) {
    ElMessage.error('获取车型列表失败')
    console.error('获取车型列表失败:', error)
  }
}

// 获取车辆数据（编辑模式）
const fetchVehicleData = async () => {
  try {
    const response = await get(`/api/vehicles/${route.params.vin}`)
    const data = response.data

    Object.assign(formData, {
      vin: data.vin,
      modelId: data.modelId,
      purchasePrice: data.purchasePrice,
      salePrice: data.salePrice,
      status: data.status,
      warehouseLocation: data.warehouseLocation,
      purchaseDate: data.purchaseDate,
      saleDate: data.saleDate
    })

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
          await put(`/api/vehicles/${formData.vin}`, formData)
          ElMessage.success('编辑车辆成功')
        } else {
          await post('/api/vehicles', formData)
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