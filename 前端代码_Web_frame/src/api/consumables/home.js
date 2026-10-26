import {
    get
} from '../../utils/http';

// 获取首页数据
export const getInitDataApi = p => get("/cHome/getInitData", p);