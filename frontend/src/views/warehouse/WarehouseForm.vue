<template>
  <div class="warehouse-form">
    <div class="header">
      <h2>{{ isEdit ? '编辑仓库' : '添加仓库' }}</h2>
    </div>

    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="仓库名称" prop="warehouseName">
        <el-input v-model="form.warehouseName" placeholder="请输入仓库名称" />
      </el-form-item>

      <el-form-item label="仓库地址" prop="address">
        <el-input v-model="form.address" placeholder="请输入仓库详细地址" />
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
          <el-form-item label="负责人" prop="managerName">
            <el-input v-model="form.managerName" placeholder="请输入负责人姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="联系电话" prop="managerPhone">
            <el-input v-model="form.managerPhone" placeholder="请输入联系电话" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio label="active">启用</el-radio>
          <el-radio label="inactive">停用</el-radio>
        </el-radio-group>
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
import { post, put, get } from '../../utils/request'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const isEdit = ref(false)

const form = reactive({
  warehouseName: '',
  address: '',
  capacity: 100,
  managerName: '',
  managerPhone: '',
  status: 'active'
})

const rules = reactive({
  warehouseName: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入仓库位置', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入仓库容量', trigger: 'blur' }],
  managerName: [{ required: true, message: '请输入负责人', trigger: 'blur' }],
  managerPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
})

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    if (isEdit.value) {
      await put('/api/warehouse', form)
      ElMessage.success('仓库更新成功')
    } else {
      await post('/api/warehouse', form)
      ElMessage.success('仓库创建成功')
    }
    
    router.push('/warehouses')
  } catch (error) {
    ElMessage.error('表单提交失败：' + (error.response?.data?.message || error.message))
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
  try {
    const response = await get(`/api/warehouse/${route.params.id}`)
    const warehouse = response.data
    
    Object.assign(form, {
      warehouseId: warehouse.warehouseId,
      warehouseName: warehouse.warehouseName,
      address: warehouse.address,
      capacity: warehouse.capacity,
      managerName: warehouse.managerName,
      managerPhone: warehouse.managerPhone,
      status: warehouse.status
    })
  } catch (error) {
    ElMessage.error('加载仓库数据失败：' + (error.response?.data?.message || error.message))
  }
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