<script setup>
import {reactive, ref, onMounted} from "vue"

import {
    getBusinessData,
    getDishOverview,
    getOrderOverview,
    getSetmealOverview
} from "../api/workspace.js";

const businessData = reactive({
  turnover: 0,
  validOrderCount: 0,
  orderCompletionRate: 0,
  unitPrice: 0,
  newUsers: 0
})

const orderOverview = reactive({
  waitingOrders: 0,
  deliveredOrders: 0,
  completedOrders: 0,
  cancelledOrders: 0,
  allOrders: 0
})

const dishOverview = reactive({
  sold: 0,
  discontinued: 0
})

const setmealOverview = reactive({
  sold: 0,
  discontinued: 0
})

const isLoading = ref(false)
const errorMessage = ref('')

const loadDashboardData = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const [
      businessResponse,
      orderResponse,
      dishResponse,
      setmealResponse
    ] = await Promise.all([
      getBusinessData(),
      getOrderOverview(),
      getDishOverview(),
      getSetmealOverview()
    ])
    Object.assign(
        businessData,
        businessResponse.data.data
    )

    Object.assign(
        orderOverview,
        orderResponse.data.data
    )

    Object.assign(
        dishOverview,
        dishResponse.data.data
    )

    Object.assign(
        setmealOverview,
        setmealResponse.data.data
    )
  } catch (error) {
    errorMessage.value = '工作台数据加载失败，请稍后重试'
    console.error('工作台数据加载失败：', error)
  } finally {
    isLoading.value = false
  }
}
onMounted(loadDashboardData)

</script>

<template>
  <main class="dashboard-page">
    <header class="dashboard-header">
      <div>
        <p class="eyebrow">SKY TAKE OUT</p>
        <h1>工作台</h1>
        <p class="dashboard-description">
          查看今日门店经营情况
        </p>
      </div>
    </header>
    <p
      v-if="isLoading"
      class="state-message">
      工作台数据加载中...
    </p>
    <p
      v-else-if="errorMessage"
      class="state-message error">
      {{errorMessage}}
    </p>
    <div
      v-else
      class="dashboard-content">
      <section class="dashboard-section">
        <div class="section-heading">
          <div>
            <p class="section-label">
              BUSINESS DATA
            </p>
            <h2>今日经营数据</h2>
          </div>
        </div>

        <div class="business-grid">
          <article class="data-card">
            <span>营业额</span>
            <strong>
              ￥{{businessData.turnover.toFixed(2)}}
            </strong>
          </article>

          <article class="data-card">
            <span>有效订单</span>
            <strong>
              {{businessData.validOrderCount}}
            </strong>
          </article>

          <article class="data-card">
            <span>订单完成率</span>
            <strong>
              {{
                (
                    businessData.orderCompletionRate * 100
                ).toFixed(1)
              }}%
            </strong>
          </article>

          <article class="data-card">
            <span>平均客单价</span>
            <strong>
              ￥{{businessData.unitPrice.toFixed(2)}}
            </strong>
          </article>

          <article class="data-card">
            <span>新增用户</span>
            <strong>
              {{businessData.newUsers}}
            </strong>
          </article>
        </div>
      </section>

      <section class="dashboard-section">
        <div class="section-heading">
          <div>
            <p class="section-label">
              ORDER OVERVIEW
            </p>
            <h2>今日订单概览</h2>
          </div>
        </div>

        <div class="order-grid">
          <article class="data-card">
            <span>待接单</span>
            <strong>
              {{orderOverview.waitingOrders}}
            </strong>
          </article>

          <article class="data-card">
            <span>待派送</span>
            <strong>
              {{orderOverview.deliveredOrders}}
            </strong>
          </article>

          <article class="data-card">
            <span>已完成</span>
            <strong>
              {{orderOverview.completedOrders }}
            </strong>
          </article>

          <article class="data-card">
            <span>已取消</span>
            <strong>
              {{orderOverview.cancelledOrders}}
            </strong>
          </article>

          <article class="data-card">
            <span>全部订单</span>
            <strong>
              {{orderOverview.allOrders}}
            </strong>
          </article>
        </div>
      </section>

      <div class="overview-grid">
        <section class="dashboard-section">
          <div class="section-heading">
            <div>
              <p class="section-label">
                DISH OVERVIEW
              </p>
              <h2>菜品概览</h2>
            </div>
          </div>

          <div class="status-grid">
            <article class="data-card">
              <span>起售菜品</span>
              <strong>
                {{dishOverview.sold}}
              </strong>
            </article>

            <article class="data-card">
              <span>停售菜品</span>
              <strong>
                {{dishOverview.discontinued}}
              </strong>
            </article>
          </div>
        </section>

        <section class="dashboard-section">
          <div class="section-heading">
            <div>
              <p class="section-label">
                SETMEAL OVERVIEW
              </p>
              <h2>套餐概览</h2>
            </div>
          </div>

          <div class="status-grid">
            <article class="data-card">
              <span>起售套餐</span>
              <strong>
                {{setmealOverview.sold}}
              </strong>
            </article>

            <article class="data-card">
              <span>停售套餐</span>
              <strong>
                {{setmealOverview.discontinued}}
              </strong>
            </article>
          </div>
        </section>
      </div>
    </div>
  </main>
</template>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
  color: #f4f7fb;
}
.dashboard-header {
  padding-top: 4px;
}
.dashboard-header h1 {
  margin: 6px 0 0;
  font-size: 32px;
  line-height: 1.2;
}
.eyebrow {
  margin: 0;
  color: #4ea1ff;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.14em;
}
.dashboard-description {
  margin: 8px 0 0;
  color: #8895a7;
  font-size: 14px;
}
.dashboard-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.dashboard-section {
  padding: 22px;

  background: linear-gradient(
      145deg,
      rgba(18, 25, 39, 0.92),
      rgba(11, 17, 28, 0.92)
  );
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  box-shadow: 0 18px 50px rgba(0, 0, 0, 0.18);
}
.section-heading {
  margin-bottom: 18px;
}
.section-heading h2 {
  margin: 4px 0 0;
  font-size: 20px;
  line-height: 1.3;
}
.section-label {
  margin: 0;
  color: #4ea1ff;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
}
.business-grid,
.order-grid {
  display: grid;
  grid-template-columns: repeat(
      5,
      minmax(0, 1fr)
    );
  gap: 14px;
}
.overview-grid {
  display: grid;
  grid-template-columns: repeat(
      2,
      minmax(0, 1fr)
    );
  gap: 20px;
}
.status-grid {
  display: grid;
  grid-template-columns: repeat(
      2,
      minmax(0, 1fr)
    );
  gap: 14px;
}
.data-card {
  min-width: 0;
  min-height: 112px;
  padding: 18px;

  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 14px;

  background: rgba(255, 255, 255, 0.035);
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 14px;

  transition:
      transform 0.2s ease,
      border-color 0.2s ease,
      background 0.2s ease;
}
.data-card span {
  color: #8895a7;
  font-size: 13px;
}
.data-card strong {
  color: #ffffff;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.1;
}
.data-card:hover {
  transform: translateY(-3px);
  border-color: rgba(78, 161, 255, 0.32);
  background: rgba(78, 161, 255, 0.07);
}
.state-message {
  margin: 0;
  padding: 22px;

  color: #94a3b8;
  text-align: center;

  background: rgba(255, 255, 255, 0.035);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
}
.state-message.error {
  color: #fda4af;
  background: rgba(244, 63, 94, 0.08);
  border-color: rgba(244, 63, 94, 0.24);
}
@media (max-width: 1100px) {
  .business-grid,
  .order-grid {
    grid-template-columns: repeat(
        2,
        minmax(0, 1fr)
      );
  }

  .overview-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 700px) {
  .dashboard-page {
    gap: 18px;
  }

  .dashboard-header h1 {
    font-size: 28px;
  }

  .dashboard-section {
    padding: 18px;
    border-radius: 16px;
  }

  .business-grid,
  .order-grid,
  .status-grid {
    grid-template-columns: 1fr;
  }

  .data-card {
    min-height: 96px;
  }
}

</style>












