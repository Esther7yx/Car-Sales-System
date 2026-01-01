<template>
  <div class="car-model-form-container">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑车型' : '添加车型' }}</h1>
    </div>

    <div class="form-container">
      <el-form
          :model="formData"
          :rules="formRules"
          ref="formRef"
          label-width="120px"
          class="car-model-form"
      >
        <el-form-item label="车型名称" prop="modelName">
          <el-input
              v-model="formData.modelName"
              placeholder="请输入车型名称（如：帕萨特 330TSI）"
              maxlength="50"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="所属厂商" prop="manufacturerId">
          <el-select
              v-model="formData.manufacturerId"
              placeholder="请选择所属厂商"
              clearable
              style="width: 100%"
          >
            <el-option
                v-for="manufacturer in manufacturerList"
                :key="manufacturer.manufacturerId"
                :label="manufacturer.manufacturerName"
                :value="manufacturer.manufacturerId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="年份" prop="year">
          <el-date-picker
              v-model="yearValue"
              type="year"
              placeholder="请选择年份"
              value-format="YYYY"
              style="width: 100%"
              @change="handleYearChange"
          />
        </el-form-item>

        <el-form-item label="燃油类型" prop="fuelType">
          <el-select v-model="formData.fuelType" placeholder="请选择燃油类型" style="width: 100%">
            <el-option label="汽油" value="汽油" />
            <el-option label="柴油" value="柴油" />
            <el-option label="电动" value="电动" />
            <el-option label="混合动力" value="混合动力" />
          </el-select>
        </el-form-item>

        <el-form-item label="发动机型号" prop="engineType">
          <el-input
              v-model="formData.engineType"
              placeholder="请输入发动机型号（如：2.0T L4）"
              maxlength="50"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="变速箱" prop="transmission">
          <el-select v-model="formData.transmission" placeholder="请选择变速箱类型" style="width: 100%">
            <el-option label="手动" value="手动" />
            <el-option label="自动" value="自动" />
            <el-option label="双离合" value="双离合" />
            <el-option label="CVT无级变速" value="CVT" />
            <el-option label="电动单速" value="电动单速" />
          </el-select>
        </el-form-item>

        <el-form-item label="指导价格(元)" prop="guidePrice">
          <el-input-number
              v-model="formData.guidePrice"
              :min="0"
              :step="1000"
              placeholder="请输入指导价格"
              style="width: 100%"
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
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { get, post, put } from '../../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 表单引用
const formRef = ref(null)

// 判断是否为编辑模式
const isEdit = ref(!!route.params.id)

// 单独定义年份绑定的变量，方便处理 DatePicker 的格式
const yearValue = ref('')

// 表单数据（与数据库字段对齐）
const formData = reactive({
  modelId: undefined,
  modelName: '',
  manufacturerId: undefined,
  year: new Date().getFullYear(), // 默认为当前年份
  fuelType: '',
  engineType: '',
  transmission: '',
  guidePrice: undefined
  // features: '' // 暂不处理JSON配置，后续可扩展
})

// 厂商列表（用于下拉选择）
const manufacturerList = ref([])

// 表单验证规则
const formRules = reactive({
  modelName: [
    { required: true, message: '请输入车型名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  manufacturerId: [
    { required: true, message: '请选择所属厂商', trigger: 'change' }
  ],
  year: [
    { required: true, message: '请选择年份', trigger: 'change' }
  ],
  fuelType: [
    { required: true, message: '请选择燃油类型', trigger: 'change' }
  ],
  engineType: [
    { required: true, message: '请输入发动机型号', trigger: 'blur' }
  ],
  transmission: [
    { required: true, message: '请选择变速箱类型', trigger: 'change' }
  ],
  guidePrice: [
    { required: true, message: '请输入指导价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格必须大于0', trigger: 'blur' }
  ]
})

// 页面加载时获取数据
onMounted(() => {
  fetchManufacturers()
  if (isEdit.value) {
    fetchCarModelData()
  } else {
    // 新增模式，初始化年份选择器显示
    yearValue.value = String(formData.year)
  }
})

// 处理年份变化
const handleYearChange = (val) => {
  formData.year = val ? parseInt(val) : null
}

// 获取厂商列表
const fetchManufacturers = async () => {
  try {
    // 使用修复后的 manufacturer 接口，pageQuery 返回 page 对象
    const response = await get('/api/manufacturer/page', { current: 1, size: 1000 })
    manufacturerList.value = response.data.records
  } catch (error) {
    ElMessage.error('获取厂商列表失败')
    console.error('获取厂商列表失败:', error)
  }
}

// 获取车型数据（编辑模式）
const fetchCarModelData = async () => {
  try {
    const response = await get(`/api/car-models/${route.params.id}`)
    const data = response.data

    // 赋值给 formData
    Object.assign(formData, {
      modelId: data.modelId,
      modelName: data.modelName,
      manufacturerId: data.manufacturerId,
      year: data.year,
      fuelType: data.fuelType,
      engineType: data.engineType,
      transmission: data.transmission,
      guidePrice: data.guidePrice
    })

    // 设置年份选择器的显示值
    if (data.year) {
      yearValue.value = String(data.year)
    }

  } catch (error) {
    ElMessage.error('获取车型信息失败')
    console.error('获取车型信息失败:', error)
    router.push('/car-models')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 确保ID处理正确
        const submitData = { ...formData }
        if (!isEdit.value) {
          delete submitData.modelId
        }

        if (isEdit.value) {
          await put(`/api/car-models/${formData.modelId}`, submitData)
          ElMessage.success('编辑车型成功')
        } else {
          await post('/api/car-models', submitData)
          ElMessage.success('添加车型成功')
        }
        router.push('/car-models')
      } catch (error) {
        ElMessage.error(isEdit.value ? '编辑车型失败' : '添加车型失败')
        console.error('保存车型失败:', error)
      }
    }
  })
}

// 取消操作
const handleCancel = () => {
  router.push('/car-models')
}
</script>

<style scoped>
.car-model-form-container {
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

.car-model-form {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>