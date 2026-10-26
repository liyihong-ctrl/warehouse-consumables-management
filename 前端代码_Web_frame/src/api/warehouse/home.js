import {
    get
} from '../../utils/http';

// 获取初始化仓库管理系统首页的数据
export const getInitDataApi = p => get('/wHome/getInitData', p);