import {
    get,
    post
} from "../../utils/http";

// 分页获取报废列表
export const getScrapsApi = p => post("/cScrap/getScraps", p);

// 新增报废记录
export const addScrapApi = p => post("/cScrap/add", p);

// 编辑报废记录
export const updateScrapApi = p => post("/cScrap/update", p);

// 删除报废记录
export const deleteScrapApi = p => get("/cScrap/delete", p);

// 审批通过
export const approveScrapApi = p => get("/cScrap/approve", p);

// 驳回报废
export const rejectScrapApi = p => get("/cScrap/reject", p);
