<template>
  <div class="permission-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" @click="handleAdd" v-if="hasPermission('system:permission:add')">
            <el-icon><Plus /></el-icon> 新增
          </el-button>
        </div>
      </template>

      <el-table :data="permissionList" style="width: 100%" v-loading="loading" row-key="id" :tree-props="{ children: 'children' }">
        <el-table-column prop="name" label="权限名称" width="200" />
        <el-table-column prop="code" label="权限编码" width="200" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'primary' : row.type === 2 ? 'success' : 'warning'">
              {{ row.type === 1 ? '菜单' : row.type === 2 ? '按钮' : '接口' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="前端路由" />
        <el-table-column prop="icon" label="图标" width="100">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" v-if="hasPermission('system:permission:edit') || hasPermission('system:permission:delete')">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-if="hasPermission('system:permission:edit')">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-if="hasPermission('system:permission:delete')">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑权限' : '新增权限'"
      width="500px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="父级权限" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="permissionTreeOptions"
            :props="{ children: 'children', label: 'name', value: 'id' }"
            check-strictly
            placeholder="选择父级权限（顶级为 0）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入权限编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="权限类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="菜单" :value="1" />
            <el-option label="按钮" :value="2" />
            <el-option label="接口" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="前端路由" prop="path" v-if="form.type === 1">
          <el-input v-model="form.path" placeholder="请输入前端路由路径" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="form.type === 1">
          <el-input v-model="form.icon" placeholder="请输入图标名称（Element Plus 图标）" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getPermissionList, addPermission, updatePermission, deletePermission } from '@/api/permission'

const userStore = useUserStore()
const hasPermission = (permission) => userStore.hasPermission(permission)

const permissionList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)

const formRef = ref(null)
const form = reactive({
  id: null,
  parentId: 0,
  name: '',
  code: '',
  type: 1,
  path: '',
  icon: '',
  sort: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择权限类型', trigger: 'change' }]
}

const permissionTreeOptions = computed(() => {
  const tree = [{ id: 0, name: '顶级权限', children: [] }]
  const map = {}

  permissionList.value.forEach(p => {
    map[p.id] = { ...p, children: [] }
  })

  permissionList.value.forEach(p => {
    if (p.parentId === 0 || p.parentId === null) {
      tree[0].children.push(map[p.id])
    } else if (map[p.parentId]) {
      map[p.parentId].children.push(map[p.id])
    }
  })

  return tree
})

const buildTree = (permissions) => {
  const tree = []
  const map = {}

  permissions.forEach(p => {
    map[p.id] = { ...p, children: [] }
  })

  permissions.forEach(p => {
    if (p.parentId === 0 || p.parentId === null) {
      tree.push(map[p.id])
    } else if (map[p.parentId]) {
      map[p.parentId].children.push(map[p.id])
    }
  })

  return tree
}

const loadPermissionList = async () => {
  loading.value = true
  try {
    const res = await getPermissionList()
    permissionList.value = buildTree(res.data)
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.parentId = 0
  form.name = ''
  form.code = ''
  form.type = 1
  form.path = ''
  form.icon = ''
  form.sort = 0
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.parentId = row.parentId || 0
  form.name = row.name
  form.code = row.code
  form.type = row.type
  form.path = row.path || ''
  form.icon = row.icon || ''
  form.sort = row.sort
  form.status = row.status
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个权限吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    loadPermissionList()
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
        if (isEdit.value) {
          await updatePermission(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await addPermission(form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadPermissionList()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  loadPermissionList()
})
</script>

<style scoped>
.permission-manage {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
