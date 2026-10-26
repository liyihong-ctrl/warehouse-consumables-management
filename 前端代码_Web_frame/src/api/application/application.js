import {
    get,
    post
} from "../../utils/http";

// 获取全部仓库信息
export const getWarehousesApi = p => get("/aApplication/getWarehouses", p);

// 获取全部消耗品信息
export const getConsumablesApi = p => post("/aApplication/getConsumables", p);

// 获取申请信息(分页)
export const getApplicationsApi = p => post("/aApplication/getApplications", p);

// 获取申请信息详情
export const getApplicationApi = p => get("/aApplication/getApplication", p);

// 检查用户是否可以申请
export const canApplicationApi = p => get("/aApplication/canApplication", p);

// 新增申请
export const addApplicationApi = p => post("/aApplication/addApplication", p);

// 编辑申请
export const editApplicationApi = p => post("/aApplication/updateApplication", p);

// 检查申请是否可以导出
export const canExportApi = p => get("/aApplication/canExport", p);
