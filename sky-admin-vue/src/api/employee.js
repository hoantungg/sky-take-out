import request from '../utils/request.js'
export const getEmployeePage = (params) => {
    return request.get(
        '/employee/page',
        {
            params
        }
    )
}
export const createEmployee = (data) => {
    return request.post(
        '/employee',
        data
    )
}