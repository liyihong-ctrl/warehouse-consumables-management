import {
    post
} from '../../utils/http';

// 更新个人信息
export const editUserInfoApi = p => post('/wUserInfo/updateUserInfo', p);

// 更新用户密码
export const editUserPassword = p => post('/wUserInfo/updateUserPassword', p);