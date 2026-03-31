<template>
  <div class="sale-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>销售记录</span>
          <el-button type="primary" @click="handleAdd" v-if="hasPermission('sale:add')">
            <el-icon><Plus /></el-icon> 新增
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="鱼类">
          <el-select v-model="searchForm.fishId" placeholder="选择鱼类" clearable style="width: 150px">
            <el-option
              v-for="fish in fishOptions"
              :key="fish.id"
              :label="fish.name"
              :value="fish.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="saleList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="fish.name" label="鱼类名称" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="totalPrice" label="总价" width="100">
          <template #default="{ row }">
            ¥{{ row.totalPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="saleDate" label="销售日期" width="120" />
        <el-table-column label="操作" width="100" v-if="hasPermission('sale:delete')">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        class="pagination"
      />
    </el-card>

    <!-- 新增对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="新增销售记录"
      width="500px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="鱼类" prop="fishId">
          <el-select v-model="form.fishId" placeholder="请选择鱼类" style="width: 100%">
            <el-option
              v-for="fish in fishOptions"
              :key="fish.id"
              :label="fish.name"
              :value="fish.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="销售日期" prop="saleDate">
          <el-date-picker
            v-model="form.saleDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getSaleList, addSale, deleteSale, getFishList } from '@/api/sale'

const userStore = useUserStore()
const hasPermission = (permission) => userStore.hasPermission(permission)

const saleList = ref([])
const fishOptions = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)

const searchForm = reactive({
  fishId: null,
  startDate: null,
  endDate: null
})

const dateRange = ref([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const formRef = ref(null)
const form = reactive({
  fishId: null,
  quantity: 1,
  saleDate: new Date().toISOString().split('T')[0]
})

const rules = {
  fishId: [{ required: true, message: '请选择鱼类', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  saleDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      fishId: searchForm.fishId || undefined,
      startDate: searchForm.startDate || undefined,
      endDate: searchForm.endDate || undefined
    }
    const [saleRes, fishRes] = await Promise.all([
      getSaleList(params),
      getFishList()
    ])
    saleList.value = saleRes.data.records
    pagination.total = saleRes.data.total
    pagination.page = saleRes.data.page
    pagination.size = saleRes.data.size
    fishOptions.value = fishRes.data
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.startDate = dateRange.value[0]
    searchForm.endDate = dateRange.value[1]
  } else {
    searchForm.startDate = null
    searchForm.endDate = null
  }
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.fishId = null
  searchForm.startDate = null
  searchForm.endDate = null
  dateRange.value = []
  pagination.page = 1
  loadData()
}

const handleAdd = () => {
  form.fishId = null
  form.quantity = 1
  form.saleDate = new Date().toISOString().split('T')[0]
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteSale(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await addSale(form)
        ElMessage.success('添加成功')
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.sale-list {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
