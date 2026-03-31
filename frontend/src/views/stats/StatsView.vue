<template>
  <div class="stats-view">
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="统计类型">
          <el-select v-model="filterForm.type" @change="loadStats">
            <el-option label="每日" value="daily" />
            <el-option label="每周" value="weekly" />
            <el-option label="每月" value="monthly" />
            <el-option label="每季度" value="quarterly" />
            <el-option label="每年" value="yearly" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" v-if="filterForm.type !== 'yearly'">
          <el-date-picker
            v-model="filterForm.date"
            type="date"
            placeholder="选择日期"
            style="width: 200px"
            value-format="YYYY-MM-DD"
            @change="loadStats"
          />
        </el-form-item>
        <el-form-item label="年份" v-else>
          <el-input-number v-model="filterForm.year" :min="2000" :max="2100" @change="loadStats" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20" class="stats-cards">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">总销售额</div>
            <div class="stat-value">¥{{ stats.totalAmount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">总销量</div>
            <div class="stat-value">{{ stats.totalQuantity || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">订单数</div>
            <div class="stat-value">{{ stats.totalRecords || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="table-card">
      <template #header>
        <span>销售明细</span>
      </template>
      <el-table :data="stats.records || []" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="fish.name" label="鱼类名称" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="totalPrice" label="总价" width="100">
          <template #default="{ row }">
            ¥{{ row.totalPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="saleDate" label="销售日期" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getDailyStats, getWeeklyStats, getMonthlyStats, getQuarterlyStats, getYearlyStats } from '@/api/sale'

const filterForm = reactive({
  type: 'daily',
  date: new Date().toISOString().split('T')[0],
  year: new Date().getFullYear()
})

const stats = ref({})

const loadStats = async () => {
  try {
    let res
    switch (filterForm.type) {
      case 'daily':
        res = await getDailyStats(filterForm.date)
        break
      case 'weekly':
        res = await getWeeklyStats(filterForm.date)
        break
      case 'monthly':
        res = await getMonthlyStats(filterForm.date)
        break
      case 'quarterly':
        res = await getQuarterlyStats(filterForm.date)
        break
      case 'yearly':
        res = await getYearlyStats(filterForm.year)
        break
    }
    stats.value = res.data
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.stats-view {
  height: 100%;
}

.filter-card {
  margin-bottom: 20px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 10px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}

.table-card {
  margin-top: 20px;
}
</style>
