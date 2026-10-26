import {
    get,
    post
} from "../../utils/http";

// 分页获取供应商列表
export const getSuppliersApi = p => post("/cSupplier/getSuppliers", p);

// 获取某仓库全部供应商（下拉选择用）
export const allSuppliersApi = p => get("/cSupplier/allSuppliers", p);

// 新增供应商
export const addSupplierApi = p => post("/cSupplier/add", p);

// 编辑供应商
export const updateSupplierApi = p => post("/cSupplier/update", p);

// 删除供应商
export const deleteSupplierApi = p => get("/cSupplier/delete", p);
