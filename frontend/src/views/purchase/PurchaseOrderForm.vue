<template>
  <div class="purchase-form-container">
    <div class="page-header">
      <h1>新建采购单</h1>
    </div>

    <el-card>
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商" required>
              <el-select v-model="form.manufacturerId" placeholder="请选择供应商" filterable style="width: 100%">
                <el-option
                    v-for="item in manufacturers"
                    :key="item.manufacturerId"
                    :label="item.manufacturerName"
                    :value="item.manufacturerId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经办人">
              <el-input value="管理员" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="section-title">采购明细</div>
        <el-table :data="form.items" border style="margin-bottom: 20px">
          <el-table-column label="车型" min-width="200">
            <template #default="scope">
              <el-select v-model="scope.row.model_id" placeholder="选择车型" filterable style="width: 100%">
                <el-option
                    v-for="m in carModels"
                    :key="m.modelId"
                    :label="m.modelName + ' (' + m.year + ')'"
                    :value="m.modelId"
                />
              </el-select>
            </template>
          </el-table-column>

          <el-table-column label="单价" width="180">
            <template #default="scope">
              <el-input-number v-model="scope.row.unit_price" :min="0" :step="1000" style="width: 100%" />
            </template>
          </el-table-column>

          <el-table-column label="数量" width="150">
            <template #default="scope">
              <el-input-number v-model="scope.row.quantity" :min="1" style="width: 100%" />
            </template>
          </el-table-column>

          <el-table-column label="小计" width="150" align="right">
            <template #default="scope">
              {{ (scope.row.unit_price * scope.row.quantity).toLocaleString() }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="100" align="center">
            <template #default="scope">
              <el-button type="danger" link @click="removeItem(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-button type="primary" plain style="width: 100%; margin-bottom: 20px" @click="addItem">
          + 添加一行
        </el-button>

        <div class="footer-bar">
          <div class="total-info">
            总金额: <span class="price">¥{{ totalAmount.toLocaleString() }}</span>
          </div>
          <div>
            <el-button @click="$router.back()">取消</el-button>
            <el-button type="primary" :loading="submitting" @click="submitOrder">提交采购单</el-button>
          </div>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, post } from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const manufacturers = ref([])
const carModels = ref([])
const submitting = ref(false)

const form = reactive({
  manufacturerId: undefined,
  items: [
    { model_id: undefined, unit_price: 0, quantity: 1 }
  ]
})

// 计算总金额
const totalAmount = computed(() => {
  return form.items.reduce((sum, item) => sum + (item.unit_price * item.quantity), 0)
})

onMounted(async () => {
  // 加载下拉框数据
  const mRes = await get('/api/manufacturer/page', { size: 100 })
  manufacturers.value = mRes.data.records

  const cRes = await get('/api/car-models/page', { size: 100 })
  carModels.value = cRes.data.records
})

const addItem = () => {
  form.items.push({ model_id: undefined, unit_price: 0, quantity: 1 })
}

const removeItem = (index) => {
  if (form.items.length > 1) {
    form.items.splice(index, 1)
  } else {
    ElMessage.warning('至少需要一条明细')
  }
}

const submitOrder = async () => {
  if (!form.manufacturerId) return ElMessage.warning('请选择供应商')
  // 简单校验
  for (const item of form.items) {
    if (!item.model_id) return ElMessage.warning('请选择车型')
    if (item.unit_price <= 0) return ElMessage.warning('单价必须大于0')
  }

  submitting.value = true
  try {
    await post('/api/purchase-orders/create', form)
    ElMessage.success('采购单创建成功')
    router.push('/purchase')
  } catch (err) {
    ElMessage.error('创建失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.purchase-form-container { padding: 20px; }
.section-title { font-weight: bold; margin: 20px 0 10px; border-left: 4px solid #409EFF; padding-left: 10px; }
.footer-bar { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #eee; padding-top: 20px; }
.total-info { font-size: 16px; }
.price { color: #f56c6c; font-size: 24px; font-weight: bold; margin-left: 10px; }
</style>