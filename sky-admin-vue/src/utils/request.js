import axios from 'axios'
import router from '../router'

const request = axios.create({
    baseURL:'/api',
    timeout:10000
})

request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('sky-admin-token')

        if (token){
            config.headers.token = token
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)
request.interceptors.response.use(
    response => {
        return response
    },
    error => {
        if (
            error.response && error.response.status === 401
        ) {
            localStorage.removeItem('sky-admin-token')
            router.push({
                name: 'login'
            })
        }
        return Promise.reject(error)
    }
)

export default request