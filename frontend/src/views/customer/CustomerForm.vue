<template>
  <div class="customer-form-container">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑客户' : '添加客户' }}</h1>
    </div>

    <div class="form-container">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px" class="customer-form">

        <el-form-item label="客户类型" prop="customerType">
          <el-radio-group v-model="formData.customerType">
            <el-radio label="individual">个人</el-radio>
            <el-radio label="enterprise">企业</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="客户姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名或企业名称" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="证件号码" prop="idNumber">
          <el-input v-model="formData.idNumber" placeholder="身份证号或统一信用代码" />
        </el-form-item>

        <el-form-item label="信用评级" prop="creditRating">
          <el-select v-model="formData.creditRating" placeholder="请选择">
            <el-option label="A级 (优秀)" value="A" />
            <el-option label="B级 (良好)" value="B" />
            <el-option label="C级 (一般)" value="C" />
            <el-option label="D级 (风险)" value="D" />
          </el-select>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="可选填" />
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" type="textarea" rows="2" placeholder="可选填" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">保存</el-button>
          <el-button @click="router.back()">取消</el-button>
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
const formRef = ref(null)
const isEdit = ref(!!route.params.id)
const loading = ref(false)

const formData = reactive({
  customerId: undefined,
  customerType: 'individual',
  name: '',
  phone: '',
  idNumber: '',
  creditRating: 'B',
  email: '',
  address: ''
})

const formRules = reactive({
  name: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  idNumber: [{ required: true, message: '请输入证件号码', trigger: 'blur' }]
})

onMounted(() => {
  if (isEdit.value) fetchCustomerData()
})

const fetchCustomerData = async () => {
  try {
    const res = await get(`/api/customers/${route.params.id}`)
    Object.assign(formData, res.data)
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (isEdit.value) {
          await put('/api/customers', formData)
          ElMessage.success('更新成功')
        } else {
          await post('/api/customers', formData)
          ElMessage.success('创建成功')
        }
        router.push('/customers')
      } catch (error) {
        ElMessage.error('保存失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.customer-form-container {
  padding: 20px;
  background-color: white;
  border-radius: 8px;
}
.form-container {
  max-width: 600px;
  margin-top: 20px;
}
</style>