import {
    get,
    post
} from '../../utils/http';

// 获取全部类型信息
export const getTypesApi = p => get("/cConsumable/getTypes", p);

// 获取基准信息模板
export const getReferenceInformationTemplates = p => get("/cReferenceInformation/all", p);

// 获取消耗品(分页)
export const getConsumablesApi = p => post("/cConsumable/getConsumables", p);

// 获取消耗品(不分页)
export const getAllConsumablesApi = p => post("/cConsumable/getAllConsumables", p);

// 新增消耗品
export const addConsumableApi = p => post("/cConsumable/addConsumable", p);

// 更新消耗品
export const updateConsumableApi = p => post("/cConsumable/updateConsumable", p);

// 批量删除消耗品
export const deleteConsumablesApi = p => post("/cConsumable/deleteConsumables", p);

// 删除消耗品
export const deleteConsumableApi = p => post("/cConsumable/deleteConsumable", p);

// 出库
export const outboundApi = p => post("/cConsumable/outboundConsumables", p);

// 入库
export const inboundApi = p => post("/cConsumable/inboundConsumables", p);

// 批量导入
export const importConsumablesApi = p => post("/cConsumable/importConsumables", p);