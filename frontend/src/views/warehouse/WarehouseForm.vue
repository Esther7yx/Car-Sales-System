<template>
  <div class="warehouse-form">
    <div class="header">
      <h2>{{ isEdit ? '编辑仓库' : '添加仓库' }}</h2>
    </div>

    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="仓库名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入仓库名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="仓库类型" prop="type">
            <el-select v-model="form.type" placeholder="请选择仓库类型">
              <el-option label="整车仓库" value="vehicle" />
              <el-option label="配件仓库" value="parts" />
              <el-option label="综合仓库" value="comprehensive" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="仓库位置" prop="location">
        <el-input v-model="form.location" placeholder="请输入仓库详细地址" />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="仓库容量" prop="capacity">
            <el-input-number
              v-model="form.capacity"
              :min="1"
              :max="10000"
              placeholder="请输入容量"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="负责人" prop="manager">
            <el-input v-model="form.manager" placeholder="请输入负责人姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入联系电话" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio label="active">启用</el-radio>
          <el-radio label="inactive">停用</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="备注" prop="remarks">
        <el-input
          v-model="form.remarks"
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
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

const form = reactive({
  name: '',
  type: '',
  location: '',
  capacity: 100,
  manager: '',
  phone: '',
  status: 'active',
  remarks: ''
})

const rules = reactive({
  name: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择仓库类型', trigger: 'change' }],
  location: [{ required: true, message: '请输入仓库位置', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入仓库容量', trigger: 'blur' }],
  manager: [{ required: true, message: '请输入负责人', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
})

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    ElMessage.success(isEdit.value ? '仓库更新成功' : '仓库创建成功')
    router.push('/warehouses')
  } catch (error) {
    ElMessage.error('表单验证失败')
  }
}

const handleCancel = () => {
  router.push('/warehouses')
}

onMounted(() => {
  if (route.params.id) {
    isEdit.value = true
    // 如果是编辑模式，加载仓库数据
    loadWarehouseData()
  }
})

const loadWarehouseData = async () => {
  // 模拟加载仓库数据
  Object.assign(form, {
    name: '主仓库',
    type: 'vehicle',
    location: '北京市朝阳区',
    capacity: 100,
    manager: '张三',
    phone: '13800138001',
    status: 'active',
    remarks: '主要存储整车'
  })
}
</script>

<style scoped>
.warehouse-form {
  padding: 20px;
}

.header {
  margin-bottom: 30px;
}

.el-form {
  max-width: 800px;
}
</style>