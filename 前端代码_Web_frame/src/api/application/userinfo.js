import {
    post
} from '../../utils/http';

// 更新个人信息
export const editUserInfoApi = p => post('/aUserInfo/updateUserInfo', p);

// 更新用户密码
export const editUserPassword = p => post('/aUserInfo/updateUserPassword', p);