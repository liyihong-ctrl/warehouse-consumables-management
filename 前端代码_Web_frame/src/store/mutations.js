function injectAuthorizationInCookie(token) {
    document.cookie = `Authorization=${token}`
}

export const initToken = (state, token) => {
    state.token = token;
    injectAuthorizationInCookie(token)
};

export const updateToken = (state, token) => {
    state.token = token;
    injectAuthorizationInCookie(token)
};

export const initUserInfo = (state, userInfo) => {
    state.userInfo = userInfo
};

export const updateUserInfo = (state, userInfo) => {
    state.userInfo = userInfo
};

export const updateName = (state, name) => {
    state.userInfo.name = name;
};

export const updateAvatar = (state, avatar) => {
    state.userInfo.avatar = avatar;
};

export const updateGender = (state, gender) => {
    state.userInfo.gender = gender;
};

export const updateEmail = (state, email) => {
    state.userInfo.email = email;
};

export const updateWarehouses = (state, warehouses) => {
    state.userInfo.warehouses = warehouses;
};

export const initSystemPermissions = (state, permission) => {
    state.systemPermissions = permission;
};

export const initSystemPermissionsType = (state) => {
    state.systemPermissionsType = [{
        typeName: "登录",
        typeStr: "c_login",
        icon: "el-icon-arrow-down",
        size: "large",
        type: "primary",
        color: "#3c9d9b"
    },
    {
        typeName: "角色",
        typeStr: "c_role_",
        icon: "el-icon-arrow-down",
        size: "large",
        type: "primary",
        color: "#3c9d9b"
    },
    {
        typeName: "用户",
        typeStr: "c_user_",
        icon: "el-icon-arrow-down",
        size: "large",
        type: "primary",
        color: "#3c9d9b"
    },
    {
        typeName: "消耗品类型",
        typeStr: "c_consumablesType_",
        icon: "el-icon-arrow-down",
        size: "large",
        type: "primary",
        color: "#3c9d9b"
    },
    {
        typeName: "基准信息模板",
        typeStr: "c_referenceInformationTemplate_",
        icon: "el-icon-arrow-down",
        size: "large",
        type: "primary",
        color: "#3c9d9b"
    },
    {
        typeName: "消耗品",
        typeStr: "c_consumables_",
        icon: "el-icon-arrow-down",
        size: "large",
        type: "primary",
        color: "#3c9d9b"
    },
    {
        typeName: "消耗品申请",
        typeStr: "c_application_",
        icon: "el-icon-arrow-down",
        size: "large",
        type: "primary",
        color: "#3c9d9b"
    },
    {
        typeName: "操作履历",
        typeStr: "c_userOperate_",
        icon: "el-icon-arrow-down",
        size: "large",
        type: "primary",
        color: "#3c9d9b"
    },
    {
        typeName: "统计信息",
        typeStr: "c_statistics_",
        icon: "el-icon-aim",
        size: "large",
        type: "primary",
        color: "#3c9d9b"
    }];
};

export const initDefaultLogo = (state, defaultLogo) => {
    state.defaultLogo = defaultLogo;
};

export const updateDefaultLogo = (state, defaultLogo) => {
    state.defaultLogo = defaultLogo;
};

export const initCurWarehouse = (state, curWarehouse) => {
    state.curWarehouse = curWarehouse;
};

export const updatecurWarehouse = (state, curWarehouse) => {
    state.curWarehouse = curWarehouse;
};

export const initAlarmConsumables = (state, alarmConsumables) => {
    state.alarmConsumables = alarmConsumables;
};

export const updateAlarmConsumables = (state, alarmConsumables) => {
    state.alarmConsumables = alarmConsumables;
};

export const logout = (state) => {
    state.token = "";
    state.userInfo = null;
    state.systemPermissions = [];
    state.systemPermissionsType = [];
    state.defaultLogo = "";
    state.consumables = [];
};