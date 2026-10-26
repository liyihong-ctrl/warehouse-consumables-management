import {
    get,
    post
} from '../../utils/http';

// 获取申请
export const getApplicationsApi = p => post("/cApplication/getApplications", p);

// 查看申请详细
export const viewApplicationDetailApi = p => get("/cApplication/viewApplicationDetail", p);

// 审批申请
export const doApprovalApi = p => post("/cApplication/doApproval", p);
