import {createRouter,  createWebHistory} from 'vue-router'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/DashboardView.vue'
import EmployeeView from '../views/EmployeeView.vue'
import AdminLayout from '../layouts/AdminLayout.vue'

const routes = [
    {
        path: '/login',
        name: 'login',
        component: LoginView
    },
    {
        path:'/',
        component: AdminLayout,
        redirect: '/login',
        meta: {
            requiresAuth: true
        },
        children: [
            {
                path: 'dashboard',
                name: 'dashboard',
                component: DashboardView
            },
            {
                path: 'employees',
                name: 'employees',
                component: EmployeeView
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

router.beforeEach((to) =>{
    const token = localStorage.getItem('sky-admin-token')
    if (to.meta.requiresAuth && !token){
        return {
            name: 'login'
        }
    }
    if (to.name === 'login' && token) {
        return {
            name: 'dashboard'
        }
    }
    return true
})

export default router