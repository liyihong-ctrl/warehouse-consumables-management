import {
    post
} from '../../utils/http';

// 获取初始化数据
export const getRolesApi = p => post("/cRole/getRoles", p);

// 新增角色
export const addRoleApi = p => post("/cRole/addRole", p);

// 更新角色
export const editRoleApi = p => post("/cRole/updateRole", p);

// 删除角色
export const deleteRoleApi = p => post("/cRole/deleteRole", p);

// 批量导入
export const importRolesApi = p => post("/cRole/importRoles", p);