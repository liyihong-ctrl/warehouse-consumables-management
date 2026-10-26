/**
 * 数据校验工具
 */

// 检验邮箱
export const validateEmail = (value) => {
    var emialReg = /^([a-zA-Z]|[0-9])(\w|-)+@[a-zA-Z0-9]+.([a-zA-Z]{2,4})$/;
    return emialReg.test(value);
};

// 校验手机号码
export const validatePhone = (value) => {
    var phoneReg = /^1(3|4|5|6|7|8|9)\d{9}$/;
    return phoneReg.test(value);
};

// 校验密码
export const validatePassword = (value) => {
    var passwordReg = /^(\w){6,20}$/;
    return passwordReg.test(value);
};

// 校验图片格式
export const validateImgFormat = (value) => {
    var imgReg = /.(gif|jpg|jpeg|png|gif|jpg|png)$/;
    return imgReg.test(value);
};

// 校验图片大小 <=2M
export const validateImgSize = (value) => {
    return value / 1024 / 1024 < 2;
};

// 校验Code
export const validateCode = (value) => {
    var codeReg = /^[A-Za-z0-9]{1,20}$/;
    return codeReg.test(value);
}

// 校验时间(16:15)格式
export const validateTimeSelect = (value) => {
    var TimeSelectReg = /^(0?[0-9]|1[0-9]|2[0-4])[:](0?[0-9]|[1-5][0-9])$/;
    return TimeSelectReg.test(value);
};

// 校验价格
export const validatePrice = (value) => {
    var priceReg = /^-?\d{1,9}(?:\.\d{1,2})?$/;
    return priceReg.test(value);
};