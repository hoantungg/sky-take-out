<script setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {getEmployeePage, createEmployee} from "../api/employee.js";

const queryParams = reactive({
  name: '',
  page: 1,
  pageSize: 10
})

const employees = ref([])
const total = ref(0)

const totalPages = computed(() => {
  return Math.max(
      1,Math.ceil(total.value/queryParams.pageSize)
  )
})

const isLoading = ref(false)
const errorMessage = ref('')
const isFormOpen = ref(false)
const isSaving = ref(false)
const formError = ref('')
const employeeForm = reactive({
  username: '',
  name: '',
  phone: '',
  sex: '1',
  idNumber: ''
})

const resetEmployeeForm = () => {
  Object.assign(employeeForm, {
    username: '',
    name: '',
    phone: '',
    sex: '1',
    idNumber: ''
  })
  formError.value = ''
}

const openCreateForm = () => {
  resetEmployeeForm()
  isFormOpen.value = true
}

const closeEmployeeForm = () => {
  if (isSaving.value) {
    return
  }
  isFormOpen.value = false
  resetEmployeeForm()
}

const loadEmployees = async () => {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await getEmployeePage(
        queryParams
    )

    const pageResult = response.data.data

    employees.value = pageResult.records
    total.value = pageResult.total
  } catch (error) {
    errorMessage.value = '员工数据加载失败，请稍后重试'
    console.error('员工数据加载失败：', error)
  } finally {
    isLoading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  loadEmployees()
}

const changePage =(page) => {
  if(
      page < 1 ||
      page > totalPages.value ||
      page === queryParams.page ||
      isLoading.value
  ) {
    return
  }
  queryParams.page = page
  loadEmployees()
}

const handleSave = async () => {
  if (isSaving.value) {
    return
  }

  formError.value = ''

  if (!employeeForm.username) {
    formError.value = '请输入员工账号'
    return
  }

  if (!employeeForm.name) {
    formError.value = '请输入员工姓名'
    return
  }

  if (!employeeForm.phone) {
    formError.value = '请输入手机号'
    return
  }

  if (!employeeForm.idNumber) {
    formError.value = '请输入身份证号'
    return
  }

  isSaving.value = true

  try {
    const response = await createEmployee(employeeForm)
    const result = response.data

    if (result.code !== 1) {
      formError.value = result.msg || '新增员工失败'
      return
    }

    isFormOpen.value = false
    resetEmployeeForm()

    queryParams.name = ''
    queryParams.page = 1
    await loadEmployees()
  } catch (error) {
    formError.value = '提交失败，请检查网络或稍后重试'
    console.error('新增员工请求失败：', error)
  } finally {
    isSaving.value = false
  }
}



onMounted(loadEmployees)

</script>

<template>
  <main class="employee-page">
    <header class="page-header">
      <div>
        <p class="eyebrow">
          EMPLOYEE MANAGEMENT
        </p>
        <h1>员工管理</h1>
        <p class="page-description">
          查询和维护门店员工信息
        </p>
      </div>
      <button type="button" @click="openCreateForm()">
        新增员工
      </button>
    </header>

    <form v-if="isFormOpen" class="employee-form-panel" @submit.prevent="handleSave">
      <h2>新增员工</h2>
      <label>
        <span>员工账号</span>
        <input
          v-model.trim="employeeForm.username"
          type="text"
          placeholder="请输入员工账号"
          autocomplete="off">
      </label>

      <label>
        <span>员工姓名</span>
        <input
            v-model.trim="employeeForm.name"
            type="text"
            placeholder="请输入员工姓名"
            autocomplete="off"
        >
      </label>

      <label>
        <span>手机号</span>
        <input
            v-model.trim="employeeForm.phone"
            type="tel"
            placeholder="请输入手机号"
            maxlength="11"
            autocomplete="off"
        >
      </label>

      <label>
        <span>性别</span>
        <select v-model="employeeForm.sex">
          <option value="1">男</option>
          <option value="0">女</option>
        </select>
      </label>

      <label>
        <span>身份证号</span>
        <input
            v-model.trim="employeeForm.idNumber"
            type="text"
            placeholder="请输入身份证号"
            maxlength="18"
            autocomplete="off"
        >
      </label>

      <button
        type="button"
        :disabled="isSaving"
        @click="closeEmployeeForm">取消</button>
    </form>

    <section class="employee-panel">
      <form class="employee-toolbar" @submit.prevent="handleSearch">
        <label class="search-field">
          <span>员工姓名</span>
          <input
            v-model.trim="queryParams.name"
            type="text"
            placeholder="请输入员工姓名"
            autocomplete="off">
        </label>
        <button type="submit" :disabled="isLoading">
          {{isLoading ? '查询中...' : '查询'}}
        </button>
      </form>

      <p v-if="isLoading" class="state-message">
        员工数据加载中...
      </p>
      <p v-else-if="errorMessage" class="state-message error">
        {{errorMessage}}
      </p>
      <div v-else class="table-wrapper">
        <table class="employee-table">
          <thead>
          <tr>
            <th>ID</th>
            <th>账号</th>
            <th>姓名</th>
            <th>手机号</th>
            <th>性别</th>
            <th>状态</th>
          </tr>
          </thead>

          <tbody>
          <tr v-if="employees.length === 0">
            <td colspan="6">
              暂无员工数据
            </td>
          </tr>
          <tr v-for="employee in employees" :key="employee.id">
            <td>{{ employee.id }}</td>
            <td>{{ employee.username }}</td>
            <td>{{ employee.name }}</td>
            <td>{{ employee.phone }}</td>
            <td>{{employee.sex === '1' ? '男' : '女'}}</td>
            <td>{{employee.status === 1 ? '启用' : '禁用'}}</td>
          </tr>
          </tbody>
        </table>

        <footer v-if="employees.length > 0" class="pagination">
          <p>
            共 {{ total }} 条，
            第 {{ queryParams.page }}
            / {{ totalPages }} 页
          </p>

          <div class="pagination-actions">
            <button type="button" :disabled="isLoading || queryParams.page <= 1"
                    @click="changePage(queryParams.page - 1)">
              上一页
            </button>
            <button type="button" :disabled="isLoading || queryParams.page >= totalPages"
                    @click="changePage(queryParams.page + 1)">
              下一页
            </button>
          </div>
        </footer>
      </div>
    </section>


  </main>
</template>


























