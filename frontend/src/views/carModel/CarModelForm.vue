<template>
  <div class="car-model-form-container">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑车型' : '添加车型' }}</h1>
      <el-button @click="goBack">返回</el-button>
    </div>

    <div class="form-container">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="车型名称" prop="modelName">
          <el-input v-model="formData.modelName" placeholder="请输入车型名称" />
        </el-form-item>

        <el-form-item label="所属厂商" prop="manufacturerId">
          <el-select v-model="formData.manufacturerId" placeholder="请选择厂商" style="width: 100%">
            <el-option
                v-for="manufacturer in manufacturerList"
                :key="manufacturer.manufacturerId"
                :label="manufacturer.manufacturerName"
                :value="manufacturer.manufacturerId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="年份" prop="year">
          <el-input v-model="formData.year" type="number" placeholder="请输入年份" />
        </el-form-item>

        <el-form-item label="燃油类型" prop="fuelType">
          <el-select v-model="formData.fuelType" placeholder="请选择燃油类型" style="width: 100%">
            <el-option label="汽油" value="汽油" />
            <el-option label="柴油" value="柴油" />
            <el-option label="电动" value="电动" />
            <el-option label="混合动力" value="混合动力" />
          </el-select>
        </el-form-item>

        <el-form-item label="发动机类型" prop="engineType">
          <el-input v-model="formData.engineType" placeholder="请输入发动机类型" />
        </el-form-item>

        <el-form-item label="变速箱" prop="transmission">
          <el-select v-model="formData.transmission" placeholder="请选择变速箱" style="width: 100%">
            <el-option label="手动" value="手动" />
            <el-option label="自动" value="自动" />
            <el-option label="CVT" value="CVT" />
            <el-option label="双离合" value="双离合" />
          </el-select>
        </el-form-item>

        <el-form-item label="排量(L)" prop="displacement">
          <el-input v-model="formData.displacement" type="number" step="0.1" placeholder="请输入排量" />
        </el-form-item>

        <el-form-item label="指导价(元)" prop="guidePrice">
          <el-input v-model="formData.guidePrice" type="number" placeholder="请输入指导价" />
        </el-form-item>

        <el-form-item label="车型描述" prop="description">
          <el-input
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="请输入车型描述"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
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
const modelId = ref('')

// 表单数据
const formData = reactive({
  modelName: '',
  manufacturerId: '',
  year: '',
  fuelType: '',
  engineType: '',
  transmission: '',
  displacement: '',
  guidePrice: '',
  description: ''
})

// 厂商列表
const manufacturerList = ref([])

// 表单验证规则
const rules = {
  modelName: [
    { required: true, message: '请输入车型名称', trigger: 'blur' }
  ],
  manufacturerId: [
    { required: true, message: '请选择所属厂商', trigger: 'change' }
  ],
  year: [
    { required: true, message: '请输入年份', trigger: 'blur' }
  ],
  fuelType: [
    { required: true, message: '请选择燃油类型', trigger: 'change' }
  ]
}

// 获取厂商列表
const getManufacturerList = async () => {
  try {
    // 模拟数据
    manufacturerList.value = [
      { manufacturerId: 1, manufacturerName: '丰田' },
      { manufacturerId: 2, manufacturerName: '本田' },
      { manufacturerId: 3, manufacturerName: '大众' },
      { manufacturerId: 4, manufacturerName: '宝马' },
      { manufacturerId: 5, manufacturerName: '奔驰' }
    ]
  } catch (error) {
    console.error('获取厂商列表失败:', error)
    ElMessage.error('获取厂商列表失败')
  }
}

// 获取车型详情
const getModelDetail = async () => {
  if (!isEdit.value) return
  
  try {
    // 模拟获取详情
    const mockData = {
      modelName: '卡罗拉',
      manufacturerId: 1,
      year: 2023,
      fuelType: '汽油',
      engineType: '1.8L自然吸气',
      transmission: 'CVT',
      displacement: 1.8,
      guidePrice: 129800,
      description: '家用轿车，经济实用'
    }
    
    Object.assign(formData, mockData)
  } catch (error) {
    console.error('获取车型详情失败:', error)
    ElMessage.error('获取车型详情失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
    
    // 模拟提交
    ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
    
    // 延迟跳转
    setTimeout(() => {
      router.push('/car-models')
    }, 1000)
    
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  }
}

// 返回
const goBack = () => {
  router.push('/car-models')
}

onMounted(() => {
  // 判断是编辑还是添加
  if (route.params.id) {
    isEdit.value = true
    modelId.value = route.params.id
  }
  
  getManufacturerList()
  getModelDetail()
})
</script>

<style scoped>
.car-model-form-container {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  min-height: calc(100vh - 140px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.page-header h1 {
  margin: 0;
  color: #303133;
  font-size: 20px;
}

.form-container {
  max-width: 600px;
  margin: 0 auto;
}

.el-form-item {
  margin-bottom: 22px;
}
</style>