<template>
  <div class="car-model-form-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>{{ isEdit ? '编辑车型' : '添加车型' }}</h1>
    </div>

    <!-- 表单内容 -->
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
            placeholder="请输入车型名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="所属厂商" prop="manufacturerId">
          <el-select
            v-model="formData.manufacturerId"
            placeholder="请选择所属厂商"
            clearable
          >
            <el-option
              v-for="manufacturer in manufacturerList"
              :key="manufacturer.manufacturerId"
              :label="manufacturer.manufacturerName"
              :value="manufacturer.manufacturerId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="品牌" prop="brand">
          <el-input
            v-model="formData.brand"
            placeholder="请输入品牌名称"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="车型类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择车型类型">
            <el-option label="轿车" value="sedan" />
            <el-option label="SUV" value="suv" />
            <el-option label="MPV" value="mpv" />
            <el-option label="两厢车" value="hatchback" />
            <el-option label="跑车" value="coupe" />
            <el-option label="皮卡" value="pickup" />
            <el-option label="电动车" value="electric" />
          </el-select>
        </el-form-item>

        <el-form-item label="发动机型号" prop="engine">
          <el-input
            v-model="formData.engine"
            placeholder="请输入发动机型号"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="变速箱" prop="transmission">
          <el-select v-model="formData.transmission" placeholder="请选择变速箱类型">
            <el-option label="手动" value="manual" />
            <el-option label="自动" value="automatic" />
            <el-option label="双离合" value="dct" />
            <el-option label="无级变速" value="cvt" />
          </el-select>
        </el-form-item>

        <el-form-item label="指导价格(元)" prop="price">
          <el-input-number
            v-model="formData.price"
            :min="0"
            :step="1000"
            placeholder="请输入指导价格"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="车型状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="on_sale">在售</el-radio>
            <el-radio label="discontinued">停售</el-radio>
          </el-radio-group>
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

// 表单数据
const formData = reactive({
  modelId: null,
  modelName: '',
  manufacturerId: null,
  brand: '',
  type: '',
  engine: '',
  transmission: '',
  price: null,
  status: 'on_sale',
  remark: ''
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
  brand: [
    { required: true, message: '请输入品牌名称', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择车型类型', trigger: 'change' }
  ],
  engine: [
    { required: true, message: '请输入发动机型号', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  transmission: [
    { required: true, message: '请选择变速箱类型', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入指导价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格必须大于0', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择车型状态', trigger: 'change' }
  ]
})

// 页面加载时获取数据
onMounted(() => {
  fetchManufacturers()
  if (isEdit.value) {
    fetchCarModelData()
  }
})

// 获取厂商列表
const fetchManufacturers = async () => {
  try {
    const response = await get('/manufacturers')
    manufacturerList.value = response.data
  } catch (error) {
    ElMessage.error('获取厂商列表失败')
    console.error('获取厂商列表失败:', error)
  }
}

// 获取车型数据（编辑模式）
const fetchCarModelData = async () => {
  try {
    const response = await get(`/car-models/${route.params.id}`)
    Object.assign(formData, response.data)
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
        if (isEdit.value) {
          // 编辑模式
          await put(`/car-models/${formData.modelId}`, formData)
          ElMessage.success('编辑车型成功')
        } else {
          // 添加模式
          await post('/car-models', formData)
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