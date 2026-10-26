import {
    post
} from '../../utils/http';

// 获取操作履历
export const getOperationHistorysApi = p => post("/cOperationHistory/getOperationHistorys", p);
