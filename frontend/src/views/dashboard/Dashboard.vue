<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <!-- 今日统计卡片 -->
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" @click="showFishPieChart('today')">
          <div class="stat-content">
            <div class="stat-icon today">
              <el-icon><Sunny /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">今日销售额</div>
              <div class="stat-value">¥{{ todayStats.totalAmount || 0 }} 元</div>
              <div class="stat-subtitle">售出 {{ todayStats.totalQuantity || 0 }} 斤</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 本周统计卡片 -->
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" @click="showFishPieChart('week')">
          <div class="stat-content">
            <div class="stat-icon week">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">本周销售额</div>
              <div class="stat-value">¥{{ weekStats.totalAmount || 0 }} 元</div>
              <div class="stat-subtitle">售出 {{ weekStats.totalQuantity || 0 }} 斤</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 本月统计卡片 -->
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" @click="showFishPieChart('month')">
          <div class="stat-content">
            <div class="stat-icon month">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">本月销售额</div>
              <div class="stat-value">¥{{ monthStats.totalAmount || 0 }} 元</div>
              <div class="stat-subtitle">售出 {{ monthStats.totalQuantity || 0 }} 斤</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 本年统计卡片 -->
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" @click="showFishPieChart('year')">
          <div class="stat-content">
            <div class="stat-icon year">
              <el-icon><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">本年销售额</div>
              <div class="stat-value">¥{{ yearStats.totalAmount || 0 }} 元</div>
              <div class="stat-subtitle">售出 {{ yearStats.totalQuantity || 0 }} 斤</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 销售趋势图表 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>近 7 日销售趋势</span>
            </div>
          </template>
          <div ref="dailyChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>本月每日销售</span>
            </div>
          </template>
          <div ref="monthlyChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 鱼类销售排行 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>本月鱼类销售排行</span>
            </div>
          </template>
          <el-table :data="fishRankList" style="width: 100%" :show-header="true">
            <el-table-column type="index" label="排名" width="80" />
            <el-table-column prop="fishName" label="鱼类名称" />
            <el-table-column prop="totalQuantity" label="销售斤数" sortable />
            <el-table-column prop="totalAmount" label="销售金额" sortable>
              <template #default="{ row }">
                ¥{{ row.totalAmount }} 元
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 鱼类占比饼图对话框 -->
    <el-dialog
      v-model="pieDialogVisible"
      :title="pieDialogTitle"
      width="800px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <div style="text-align: center; font-weight: bold; margin-bottom: 10px">销售斤数占比</div>
          <div ref="quantityPieRef" style="height: 350px"></div>
        </el-col>
        <el-col :span="12">
          <div style="text-align: center; font-weight: bold; margin-bottom: 10px">销售额占比</div>
          <div ref="amountPieRef" style="height: 350px"></div>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { Sunny, Calendar, Clock, Timer } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDailyStats, getWeeklyStats, getMonthlyStats, getYearlyStats, getSaleList } from '@/api/sale'
import { getFishList } from '@/api/fish'

const todayStats = ref({})
const weekStats = ref({})
const monthStats = ref({})
const yearStats = ref({})

const dailyChartRef = ref(null)
const monthlyChartRef = ref(null)
const fishRankList = ref([])

let dailyChart = null
let monthlyChart = null
let quantityPieChart = null
let amountPieChart = null

// 饼图相关
const pieDialogVisible = ref(false)
const pieDialogTitle = ref('')
const quantityPieRef = ref(null)
const amountPieRef = ref(null)

// 获取指定时间段的鱼类销售数据
const getFishSalesData = async (startDate, endDate) => {
  const res = await getSaleList({
    page: 1,
    size: 1000,
    startDate,
    endDate
  })

  const fishMap = new Map()
  res.data.records.forEach(record => {
    const fishId = record.fish?.id || record.fishId
    const fishName = record.fish?.name || '未知鱼类'
    if (!fishMap.has(fishId)) {
      fishMap.set(fishId, { fishId, fishName, totalQuantity: 0, totalAmount: 0 })
    }
    const item = fishMap.get(fishId)
    item.totalQuantity = parseFloat(item.totalQuantity) + parseFloat(record.quantity || 0)
    item.totalAmount += parseFloat(record.totalPrice)
  })

  return Array.from(fishMap.values())
}

// 显示鱼类占比饼图
const showFishPieChart = async (type) => {
  const today = new Date()
  let startDate, endDate, title

  switch (type) {
    case 'today':
      startDate = today.toISOString().split('T')[0]
      endDate = startDate
      title = '今日鱼类销售占比'
      break
    case 'week':
      const dayOfWeek = today.getDay() || 7
      const monday = new Date(today)
      monday.setDate(today.getDate() - dayOfWeek + 1)
      startDate = monday.toISOString().split('T')[0]
      endDate = today.toISOString().split('T')[0]
      title = '本周鱼类销售占比'
      break
    case 'month':
      const year = today.getFullYear()
      const month = today.getMonth() + 1
      startDate = `${year}-${String(month).padStart(2, '0')}-01`
      endDate = `${year}-${String(month).padStart(2, '0')}-${String(new Date(year, month, 0).getDate()).padStart(2, '0')}`
      title = '本月鱼类销售占比'
      break
    case 'year':
      const currentYear = today.getFullYear()
      startDate = `${currentYear}-01-01`
      endDate = `${currentYear}-12-31`
      title = '本年鱼类销售占比'
      break
  }

  pieDialogTitle.value = title
  pieDialogVisible.value = true

  // 等待对话框打开后渲染图表
  nextTick(async () => {
    const fishData = await getFishSalesData(startDate, endDate)

    // 创建按斤数占比的数据
    const quantityData = fishData.map(item => ({
      name: item.fishName,
      value: parseFloat(item.totalQuantity.toFixed(2))
    }))

    // 创建按金额占比的数据
    const amountData = fishData.map(item => ({
      name: item.fishName,
      value: parseFloat(item.totalAmount.toFixed(2))
    }))

    // 渲染斤数占比饼图
    if (quantityPieRef.value) {
      quantityPieChart = echarts.init(quantityPieRef.value)
      quantityPieChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}斤 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          top: 'middle'
        },
        series: [
          {
            name: '销售斤数',
            type: 'pie',
            radius: '60%',
            data: quantityData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            label: {
              formatter: '{b}\n{c}斤'
            }
          }
        ]
      })
    }

    // 渲染销售额占比饼图
    if (amountPieRef.value) {
      amountPieChart = echarts.init(amountPieRef.value)
      amountPieChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: ¥{c} 元 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          top: 'middle'
        },
        series: [
          {
            name: '销售额',
            type: 'pie',
            radius: '60%',
            data: amountData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            label: {
              formatter: '{b}\n¥{c} 元'
            }
          }
        ]
      })
    }
  })
}

const loadStats = async () => {
  const today = new Date().toISOString().split('T')[0]
  const todayObj = new Date()

  // 计算本周一日期
  const dayOfWeek = todayObj.getDay() || 7
  const monday = new Date(todayObj)
  monday.setDate(todayObj.getDate() - dayOfWeek + 1)
  const weekStart = monday.toISOString().split('T')[0]

  // 本月 1 号
  const monthStart = new Date(todayObj.getFullYear(), todayObj.getMonth(), 1).toISOString().split('T')[0]

  // 本年 1 月 1 号
  const yearStart = `${todayObj.getFullYear()}-01-01`

  try {
    const [dailyRes, weeklyRes, monthlyRes, yearlyRes] = await Promise.all([
      getDailyStats(today),
      getWeeklyStats(today),
      getMonthlyStats(today),
      getYearlyStats(todayObj.getFullYear())
    ])

    todayStats.value = dailyRes.data
    weekStats.value = weeklyRes.data
    monthStats.value = monthlyRes.data
    yearStats.value = yearlyRes.data
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadChartData = async () => {
  try {
    const today = new Date()
    const dates = []
    const amounts = []

    // 获取近 7 日数据
    for (let i = 6; i >= 0; i--) {
      const date = new Date(today)
      date.setDate(today.getDate() - i)
      const dateStr = date.toISOString().split('T')[0]
      dates.push(dateStr)

      try {
        const res = await getDailyStats(dateStr)
        amounts.push(res.data.totalAmount || 0)
      } catch (e) {
        amounts.push(0)
      }
    }

    // 渲染近 7 日销售趋势图
    if (dailyChartRef.value) {
      dailyChart = echarts.init(dailyChartRef.value)
      dailyChart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: '{b}<br/>销售额：¥{c}'
        },
        xAxis: {
          type: 'category',
          data: dates.map(d => d.slice(5)),
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [{
          name: '销售额',
          type: 'line',
          data: amounts,
          smooth: true,
          areaStyle: {
            color: 'rgba(64, 158, 255, 0.2)'
          },
          itemStyle: {
            color: '#409EFF'
          }
        }]
      })
    }

    // 获取本月每日数据
    const year = today.getFullYear()
    const month = today.getMonth() + 1
    const daysInMonth = new Date(year, month, 0).getDate()
    const monthDates = []
    const monthAmounts = []

    for (let day = 1; day <= daysInMonth; day++) {
      const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
      monthDates.push(day)

      try {
        const res = await getDailyStats(dateStr)
        monthAmounts.push(res.data.totalAmount || 0)
      } catch (e) {
        monthAmounts.push(0)
      }
    }

    // 渲染本月每日销售图
    if (monthlyChartRef.value) {
      monthlyChart = echarts.init(monthlyChartRef.value)
      monthlyChart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: '{b}日<br/>销售额：¥{c}'
        },
        xAxis: {
          type: 'category',
          data: monthDates,
          name: '日期'
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [{
          name: '销售额',
          type: 'bar',
          data: monthAmounts,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#67c23a' },
              { offset: 1, color: 'rgba(103, 194, 58, 0.3)' }
            ])
          }
        }]
      })
    }

    // 加载鱼类销售排行
    await loadFishRank()

  } catch (error) {
    console.error('加载图表数据失败:', error)
  }
}

const loadFishRank = async () => {
  try {
    const today = new Date()
    const year = today.getFullYear()
    const month = today.getMonth() + 1
    const monthStart = `${year}-${String(month).padStart(2, '0')}-01`
    const monthEnd = `${year}-${String(month).padStart(2, '0')}-${String(new Date(year, month, 0).getDate()).padStart(2, '0')}`

    const fishData = await getFishSalesData(monthStart, monthEnd)

    fishRankList.value = fishData
      .sort((a, b) => b.totalQuantity - a.totalQuantity)
      .slice(0, 10)

  } catch (error) {
    console.error('加载鱼类排行失败:', error)
  }
}

onMounted(() => {
  loadStats()
  nextTick(() => {
    loadChartData()
  })

  // 窗口大小改变时重新渲染图表
  window.addEventListener('resize', () => {
    dailyChart?.resize()
    monthlyChart?.resize()
    quantityPieChart?.resize()
    amountPieChart?.resize()
  })
})

// 对话框关闭时销毁图表
watch(pieDialogVisible, (val) => {
  if (!val) {
    quantityPieChart?.dispose()
    amountPieChart?.dispose()
    quantityPieChart = null
    amountPieChart = null
  }
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.stat-icon.today {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.week {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.month {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.year {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
  margin-left: 15px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-subtitle {
  font-size: 12px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
