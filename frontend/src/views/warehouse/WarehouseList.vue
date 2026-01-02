<template>
  <div class="warehouse-list">
    <div class="header">
      <h2>仓库管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加仓库
      </el-button>
    </div>

    <div class="filter">
      <el-form :model="filterForm" inline>
        <el-form-item label="仓库名称">
          <el-input v-model="filterForm.name" placeholder="请输入仓库名称" clearable />
        </el-form-item>
        <el-form-item label="仓库类型">
          <el-select v-model="filterForm.type" placeholder="请选择类型" clearable>
            <el-option label="整车仓库" value="vehicle" />
            <el-option label="配件仓库" value="parts" />
            <el-option label="综合仓库" value="comprehensive" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="name" label="仓库名称" />
      <el-table-column prop="type" label="仓库类型" width="120">
        <template #default="{ row }">
          <el-tag :type="getTypeType(row.type)">{{ getTypeText(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="location" label="仓库位置" />
      <el-table-column prop="capacity" label="容量" width="100" />
      <el-table-column prop="currentStock" label="当前库存" width="100" />
      <el-table-column prop="manager" label="负责人" width="120" />
      <el-table-column prop="phone" label="联系电话" width="140" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
            {{ row.status === 'active' ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
          <el-button type="warning" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])

const filterForm = ref({
  name: '',
  type: ''
})

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const getTypeType = (type) => {
  const types = {
    'vehicle': 'primary',
    'parts': 'warning',
    'comprehensive': 'success'
  }
  return types[type] || 'info'
}

const getTypeText = (type) => {
  const texts = {
    'vehicle': '整车仓库',
    'parts': '配件仓库',
    'comprehensive': '综合仓库'
  }
  return texts[type] || type
}

const handleSearch = () => {
  pagination.value.currentPage = 1
  fetchData()
}

const handleReset = () => {
  filterForm.value = {
    name: '',
    type: ''
  }
  handleSearch()
}

const handleAdd = () => {
  router.push('/warehouses/add')
}

const handleDetail = (row) => {
  // 查看详情逻辑
}

const handleEdit = (row) => {
  router.push(`/warehouses/edit/${row.id}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此仓库吗？', '提示', {
      type: 'warning'
    })
    // 调用删除API
    ElMessage.success('仓库删除成功')
    fetchData()
  } catch {
    // 用户取消操作
  }
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  fetchData()
}

const handleCurrentChange = (page) => {
  pagination.value.currentPage = page
  fetchData()
}

const fetchData = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    tableData.value = [
      {
        id: 1,
        name: '主仓库',
        type: 'vehicle',
        location: '北京市朝阳区',
        capacity: 100,
        currentStock: 0,
        manager: '张三',
        phone: '13800138001',
        status: 'active'
      }
    ]
    pagination.value.total = 1
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.warehouse-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>