<template>
  <div class="role-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd" v-if="hasPermission('system:role:add')">
            <el-icon><Plus /></el-icon> 新增
          </el-button>
        </div>
      </template>

      <el-table :data="roleList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="code" label="角色编码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" v-if="hasPermission('system:role:edit') || hasPermission('system:role:delete')">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-if="hasPermission('system:role:edit')">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-if="hasPermission('system:role:delete')">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑角色' : '新增角色'"
      width="500px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入角色编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="权限" prop="permissionIds">
          <el-tree
            ref="treeRef"
            :data="permissionTree"
            :props="{ children: 'children', label: 'name' }"
            show-checkbox
            node-key="id"
            class="permission-tree"
          />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getRoleList, addRole, updateRole, deleteRole } from '@/api/role'
import { getPermissionList } from '@/api/permission'

const userStore = useUserStore()
const hasPermission = (permission) => userStore.hasPermission(permission)

const roleList = ref([])
const permissionTree = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)

const formRef = ref(null)
const treeRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  code: '',
  description: '',
  permissionIds: [],
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const buildPermissionTree = (permissions) => {
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

const loadData = async () => {
  loading.value = true
  try {
    const [roleRes, permissionRes] = await Promise.all([getRoleList(), getPermissionList()])
    roleList.value = roleRes.data
    permissionTree.value = buildPermissionTree(permissionRes.data)
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.name = ''
  form.code = ''
  form.description = ''
  form.permissionIds = []
  form.status = 1
  if (treeRef.value) {
    treeRef.value.setCheckedKeys([])
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  form.code = row.code
  form.description = row.description
  form.permissionIds = row.permissions.map(p => p.id)
  form.status = row.status
  if (treeRef.value) {
    treeRef.value.setCheckedKeys(form.permissionIds)
  }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个角色吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteRole(row.id)
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
        const permissionIds = treeRef.value ? treeRef.value.getCheckedKeys() : form.permissionIds
        const data = {
          ...form,
          permissions: permissionIds.map(id => ({ id }))
        }
        if (isEdit.value) {
          await updateRole(form.id, data)
          ElMessage.success('更新成功')
        } else {
          await addRole(data)
          ElMessage.success('添加成功')
        }
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
.role-manage {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.permission-tree {
  width: 100%;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
}
</style>
