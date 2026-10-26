import {
    get,
    post
} from "../../utils/http";

// 获取仓库管理员信息
export const getAdministratorsApi = p => get('/wWarehouse/getAllWarehouseManagers', p);

// 获取仓库信息(分页)
export const getWarehousesApi = p => post("/wWarehouse/getWarehouses", p);

// 新增仓库
export const addWarehouseApi = p => post("/wWarehouse/addWarehouse", p);

// 编辑仓库
export const editWarehouseApi = p => post("/wWarehouse/editWarehouse", p);

// 编辑仓库状态
export const editWarehouseStateApi = p => get("/wWarehouse/editWarehouseState", p);

// 删除仓库
export const deleteWarehouseApi = p => get("/wWarehouse/deleteWarehouse", p);

// 分配人员
export const updateAssignUsersApi = p => post("/wWarehouse/updateAssignUser", p);