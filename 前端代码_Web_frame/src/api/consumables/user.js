import {
    get,
    post
} from '../../utils/http';

// 获取角色
export const getRolesApi = p => get("/cUser/getRoles", p);

// 获取用户信息(分页)
export const getUsersApi = p => post('/cUser/getUsers', p);

// 获取申请用户信息(分页)
export const getApplicationUsersApi = p => post('/cUser/getApplicationUsers', p);

// 新增用户
export const addUserApi = p => post('/cUser/addUser', p);

// 编辑用户
export const updateUserApi = p => post('/cUser/updateUser', p);

// 修改用户状态
export const updateUserStateApi = p => get('/cUser/updateUserState', p);

// 删除用户
export const deleteUserApi = p => get('/cUser/deleteUser', p);

// 重置管理员密码
export const resetUserPasswordApi = p => get('/cUser/resetUserPassword', p);

// 批量导入
export const importUsersApi = p => post("/cUser/importUsers", p);

// 设置申请用户是否可以申请当前仓库
export const updateUserCanApplicationStateApi = p => get("/cUser/updateUserApplicationState", p);