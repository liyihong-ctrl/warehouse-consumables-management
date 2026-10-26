import {
    get,
    post
} from "../utils/http";

// 登录
export const loginApi = p => post("/login", p);

// 注册
export const registerApi = p => post("/register", p);

// 登出
export const logoutApi = p => post("/logout", p);

// 切换仓库
export const switchSystemApi = p => get("/switchSystem", p);

// 获取报警消耗品数据
export const getAlarmConsumablesApi = p => get("/cHome/getAlarmConsumables", p);