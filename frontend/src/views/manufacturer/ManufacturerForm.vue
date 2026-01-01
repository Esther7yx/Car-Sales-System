<template>
  <div class="manufacturer-form-container">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑厂商' : '添加厂商' }}</h1>
    </div>

    <div class="form-container">
      <el-form
          :model="formData"
          :rules="formRules"
          ref="formRef"
          label-width="120px"
          class="manufacturer-form"
      >
        <el-form-item label="厂商名称" prop="manufacturerName">
          <el-input
              v-model="formData.manufacturerName"
              placeholder="请输入厂商名称"
              maxlength="50"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="联系人" prop="contactPerson">
          <el-input
              v-model="formData.contactPerson"
              placeholder="请输入联系人姓名"
              maxlength="20"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="联系电话" prop="contactPhone">
          <el-input
              v-model="formData.contactPhone"
              placeholder="请输入联系电话"
              maxlength="15"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input
              v-model="formData.address"
              placeholder="请输入厂商地址"
              type="textarea"
              rows="3"
              maxlength="200"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="合作状态" prop="cooperationStatus">
          <el-radio-group v-model="formData.cooperationStatus">
            <el-radio label="active">合作中</el-radio>
            <el-radio label="inactive">已终止</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
// ... script 部分保持不变 ...
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
  manufacturerId: undefined,
  manufacturerName: '',
  contactPerson: '',
  contactPhone: '',
  address: '',
  cooperationStatus: 'active'
})

const formRules = reactive({
  manufacturerName: [
    { required: true, message: '请输入厂商名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入厂商地址', trigger: 'blur' },
    { min: 5, max: 200, message: '长度在 5 到 200 个字符', trigger: 'blur' }
  ],
  cooperationStatus: [
    { required: true, message: '请选择合作状态', trigger: 'change' }
  ]
})

onMounted(() => {
  if (isEdit.value) {
    fetchManufacturerData()
  }
})

const fetchManufacturerData = async () => {
  try {
    const response = await get(`/api/manufacturer/${route.params.id}`)
    const { manufacturerId, manufacturerName, contactPerson, contactPhone, address, cooperationStatus } = response.data
    Object.assign(formData, { manufacturerId, manufacturerName, contactPerson, contactPhone, address, cooperationStatus })
  } catch (error) {
    console.error('获取厂商信息失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        const submitData = { ...formData }
        if (!isEdit.value) {
          delete submitData.manufacturerId
        }

        if (isEdit.value) {
          await put(`/api/manufacturer/${formData.manufacturerId}`, submitData)
          ElMessage.success('编辑厂商成功')
        } else {
          await post('/api/manufacturer', submitData)
          ElMessage.success('添加厂商成功')
        }
        router.push('/manufacturers')
      } catch (error) {
        console.error('保存厂商失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const handleCancel = () => {
  router.push('/manufacturers')
}
</script>

<style scoped>
.manufacturer-form-container {
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

.manufacturer-form {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>