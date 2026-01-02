<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑客户' : '添加客户'"
    width="600px"
    @close="handleClose"
  >
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
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { customerApi } from '@/api/customer'

const props = defineProps({
  modelValue: Boolean,
  customer: Object,
  isEdit: Boolean
})

const emit = defineEmits(['update:modelValue', 'success'])

const dialogVisible = ref(false)
const formRef = ref()
const loading = ref(false)

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
    { pattern: /^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  address: [
    { max: 200, message: '地址长度不能超过200个字符', trigger: 'blur' }
  ],
  creditRating: [
    { required: true, message: '请选择信用等级', trigger: 'change' }
  ]
}

watch(() => props.modelValue, (val) => {
  dialogVisible.value = val
  if (val) {
    nextTick(() => {
      if (props.isEdit && props.customer) {
        form.value = { ...props.customer }
      } else {
        resetForm()
      }
    })
  }
})

watch(dialogVisible, (val) => {
  emit('update:modelValue', val)
  if (!val) {
    resetForm()
  }
})

const resetForm = () => {
  form.value = {
    customerType: 'individual',
    name: '',
    idNumber: '',
    phone: '',
    email: '',
    address: '',
    creditRating: 'A'
  }
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleClose = () => {
  dialogVisible.value = false
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  const valid = await formRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    if (props.isEdit) {
      await customerApi.updateCustomer(form.value)
      ElMessage.success('更新客户成功')
    } else {
      await customerApi.addCustomer(form.value)
      ElMessage.success('添加客户成功')
    }
    emit('success')
    dialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>