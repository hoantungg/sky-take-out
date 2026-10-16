import request from "../utils/request.js"
export const getBusinessData = () => {
    return request.get(
        '/workspace/businessData'
    )
}
export const getOrderOverview = () => {
    return request.get(
        '/workspace/overviewOrders'
    )
}
export const getDishOverview = () => {
    return request.get(
        '/workspace/overviewDishes'
    )
}
export const getSetmealOverview = () => {
    return request.get(
        '/workspace/overviewSetmeals'
    )
}
