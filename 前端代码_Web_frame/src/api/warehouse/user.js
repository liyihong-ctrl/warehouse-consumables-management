import {
    get,
    post
} from '../../utils/http';

// 获取管理员信息(分页)
export const getAdministratorsApi = p => post('/wUser/getAdministrators', p);

// 新增管理员
export const addAdministratorApi = p => post('/wUser/addAdministrator', p);

// 编辑管理员
export const editAdministratorApi = p => post('/wUser/editAdministrator', p);

// 编辑管理员状态
export const editAdministratorStateApi = p => get('/wUser/editAdministratorState', p);

// 删除管理员
export const deleteAdministratorApi = p => get('/wUser/deleteAdministrator', p);

// 重置管理员密码
export const resetAdministratorPasswordApi = p => get('/wUser/resetAdministratorPassword', p);

// 获取用户分配仓库的初始化数据
export const getAllWarehousesApi = p => get('/wUser/getAssignWarehouseInitData', p);

// 编辑用户分配仓库
export const updateAssignWarehouseApi = p => post('/wUser/updateAssignWarehouse', p);