<template>
  <div class="manufacturer-form-container">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑厂商' : '添加厂商' }}</h1>
      <el-button @click="goBack">返回</el-button>
    </div>

    <div class="form-container">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="厂商名称" prop="manufacturerName">
          <el-input v-model="formData.manufacturerName" placeholder="请输入厂商名称" />
        </el-form-item>

        <el-form-item label="厂商代码" prop="manufacturerCode">
          <el-input v-model="formData.manufacturerCode" placeholder="请输入厂商代码" />
        </el-form-item>

        <el-form-item label="国家/地区" prop="country">
          <el-select v-model="formData.country" placeholder="请选择国家/地区" style="width: 100%">
            <el-option label="中国" value="中国" />
            <el-option label="日本" value="日本" />
            <el-option label="德国" value="德国" />
            <el-option label="美国" value="美国" />
            <el-option label="韩国" value="韩国" />
            <el-option label="法国" value="法国" />
            <el-option label="英国" value="英国" />
            <el-option label="意大利" value="意大利" />
          </el-select>
        </el-form-item>

        <el-form-item label="成立年份" prop="foundedYear">
          <el-input v-model="formData.foundedYear" type="number" placeholder="请输入成立年份" />
        </el-form-item>

        <el-form-item label="官方网站" prop="website">
          <el-input v-model="formData.website" placeholder="请输入官方网站" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input
              v-model="formData.address"
              type="textarea"
              :rows="3"
              placeholder="请输入地址"
          />
        </el-form-item>

        <el-form-item label="厂商描述" prop="description">
          <el-input
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="请输入厂商描述"
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
const manufacturerId = ref('')

// 表单数据
const formData = reactive({
  manufacturerName: '',
  manufacturerCode: '',
  country: '',
  foundedYear: '',
  website: '',
  phone: '',
  email: '',
  address: '',
  description: ''
})

// 表单验证规则
const rules = {
  manufacturerName: [
    { required: true, message: '请输入厂商名称', trigger: 'blur' }
  ],
  manufacturerCode: [
    { required: true, message: '请输入厂商代码', trigger: 'blur' }
  ],
  country: [
    { required: true, message: '请选择国家/地区', trigger: 'change' }
  ]
}

// 获取厂商详情
const getManufacturerDetail = async () => {
  if (!isEdit.value) return
  
  try {
    // 模拟获取详情
    const mockData = {
      manufacturerName: '丰田',
      manufacturerCode: 'TOYOTA',
      country: '日本',
      foundedYear: 1937,
      website: 'https://www.toyota.com',
      phone: '400-810-1212',
      email: 'contact@toyota.com',
      address: '日本爱知县丰田市',
      description: '全球知名汽车制造商，以可靠性和耐用性著称'
    }
    
    Object.assign(formData, mockData)
  } catch (error) {
    console.error('获取厂商详情失败:', error)
    ElMessage.error('获取厂商详情失败')
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
      router.push('/manufacturers')
    }, 1000)
    
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  }
}

// 返回
const goBack = () => {
  router.push('/manufacturers')
}

onMounted(() => {
  // 判断是编辑还是添加
  if (route.params.id) {
    isEdit.value = true
    manufacturerId.value = route.params.id
  }
  
  getManufacturerDetail()
})
</script>

<style scoped>
.manufacturer-form-container {
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