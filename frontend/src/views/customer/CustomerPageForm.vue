<template>
  <div class="customer-page-form">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑客户' : '添加客户' }}</h2>
      <el-button @click="handleBack">返回列表</el-button>
    </div>

    <div class="form-container">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="客户类型" prop="customerType">
          <el-radio-group v-model="form.customerType">
            <el-radio label="individual">个人客户</el-radio>
            <el-radio label="enterprise">企业客户</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="客户姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入客户姓名" />
        </el-form-item>

        <el-form-item label="身份证号" prop="idNumber">
          <el-input v-model="form.idNumber" placeholder="请输入身份证号" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" type="textarea" :rows="2" placeholder="请输入地址" />
        </el-form-item>

        <el-form-item label="信用等级" prop="creditRating">
          <el-select v-model="form.creditRating" placeholder="请选择信用等级">
            <el-option label="A级" value="A" />
            <el-option label="B级" value="B" />
            <el-option label="C级" value="C" />
            <el-option label="D级" value="D" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            {{ isEdit ? '更新' : '添加' }}
          </el-button>
          <el-button @click="handleBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { get, post, put } from '@/utils/request'

const route = useRoute()
const router = useRouter()

const formRef = ref()
const loading = ref(false)
const isEdit = ref(false)

const form = ref({
  customerType: 'individual',
  name: '',
  idNumber: '',
  phone: '',
  email: '',
  address: '',
  creditRating: 'A'
})

const rules = {
  customerType: [
    { required: true, message: '请选择客户类型', trigger: 'change' }
  ],
  name: [
    { required: true, message: '请输入客户姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在2-50个字符', trigger: 'blur' }
  ],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' }
  ],
  creditRating: [
    { required: true, message: '请选择信用等级', trigger: 'change' }
  ]
}

const handleBack = () => {
  router.push('/customers')
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    loading.value = true
    
    if (isEdit.value) {
      await put(`/api/customer/${route.params.id}`, form.value)
      ElMessage.success('客户信息更新成功')
    } else {
      await post('/api/customer', form.value)
      ElMessage.success('客户添加成功')
    }
    
    router.push('/customers')
  } catch (error) {
    if (error.errors) {
      ElMessage.error('表单验证失败，请检查输入')
    } else {
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    }
  } finally {
    loading.value = false
  }
}

const loadCustomerData = async (customerId) => {
  try {
    const response = await get(`/api/customer/${customerId}`)
    form.value = response.data
  } catch (error) {
    ElMessage.error('获取客户信息失败')
    router.push('/customers')
  }
}

onMounted(() => {
  if (route.params.id) {
    isEdit.value = true
    loadCustomerData(route.params.id)
  }
})
</script>

<style scoped>
.customer-page-form {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.form-container {
  max-width: 600px;
}
</style>