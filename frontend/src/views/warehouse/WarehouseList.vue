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
          <el-input v-model="filterForm.warehouseName" placeholder="请输入仓库名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" value="active" />
            <el-option label="停用" value="inactive" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="warehouseName" label="仓库名称" />
      <el-table-column prop="address" label="仓库地址" />
      <el-table-column prop="capacity" label="容量" width="100" />
      <el-table-column prop="currentStock" label="当前库存" width="100" />
      <el-table-column prop="managerName" label="负责人" width="120" />
      <el-table-column prop="managerPhone" label="联系电话" width="140" />
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

    <!-- 仓库详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="仓库详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="仓库ID">{{ currentWarehouse.warehouseId }}</el-descriptions-item>
        <el-descriptions-item label="仓库名称">{{ currentWarehouse.warehouseName }}</el-descriptions-item>
        <el-descriptions-item label="仓库地址" :span="2">{{ currentWarehouse.address }}</el-descriptions-item>
        <el-descriptions-item label="仓库容量">{{ currentWarehouse.capacity }}</el-descriptions-item>
        <el-descriptions-item label="当前库存">{{ currentWarehouse.currentStock }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ currentWarehouse.managerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentWarehouse.managerPhone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentWarehouse.status === 'active' ? 'success' : 'danger'">
            {{ currentWarehouse.status === 'active' ? '启用' : '停用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentWarehouse.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentWarehouse.updatedAt }}</el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { get, post, del } from '../../utils/request'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const detailDialogVisible = ref(false)
const currentWarehouse = ref({})

const filterForm = ref({
  warehouseName: '',
  status: ''
})

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const handleSearch = () => {
  pagination.value.currentPage = 1
  fetchData()
}

const handleReset = () => {
  filterForm.value = {
    warehouseName: '',
    status: ''
  }
  handleSearch()
}

const handleAdd = () => {
  router.push('/warehouses/add')
}

const handleDetail = (row) => {
  currentWarehouse.value = row
  detailDialogVisible.value = true
}

const handleEdit = (row) => {
  router.push(`/warehouses/edit/${row.warehouseId}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此仓库吗？', '提示', {
      type: 'warning'
    })
    // 调用删除API
    await del('/api/warehouse/' + row.warehouseId)
    ElMessage.success('仓库删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除仓库失败：' + (error.response?.data?.message || error.message))
    }
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
    const response = await get('/api/warehouse/page', {
      current: pagination.value.currentPage,
      size: pagination.value.pageSize,
      warehouseName: filterForm.value.warehouseName,
      status: filterForm.value.status
    })
    tableData.value = response.data.records
    pagination.value.total = response.data.total
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