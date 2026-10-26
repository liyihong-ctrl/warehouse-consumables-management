import {
    get,
    post
} from "../../utils/http";

// 获取类型信息
export const getTypesApi = p => post("/cType/getTypes", p);

// 新增类型
export const addTypeApi = p => post("/cType/addType", p);

// 编辑类型
export const updateTypeApi = p => post("/cType/updateType", p);

// 删除类型
export const deleteTypeApi = p => get("/cType/deleteType", p);

// 批量导入类型
export const importTypesApi = p => post("/cType/importTypes", p);