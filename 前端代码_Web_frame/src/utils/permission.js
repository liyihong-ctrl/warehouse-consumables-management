export const HAS_PERMISSION = (permissions, curPermissionStr) => {
    if (!permissions.find((item) => {
        return item.str === curPermissionStr;
    })) {
        return false;
    }
    return true;
};

export const INIT_WEB_PERMISSIONS = (permissions) => {
    var webPermissions = {
        loginPermissions: [],
        roleManagementPermissions: [],
        userManagementPermissions: [],
        consumablesTypeManagementPermissions: [],
        referenctInformationTemplatePermissions: [],
        consumablesManagementPermissions: [],
        applicationManagementPermissions: [],
        userOperateManagementPermissions: [],
        statisticsManagementPermissions: []
    };

    permissions.forEach(p => {
        switch (p.str) {
            case "c_login":
                webPermissions.loginPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_role_add":
                webPermissions.roleManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_role_delete":
                webPermissions.roleManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_role_update":
                webPermissions.roleManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_role_select":
                webPermissions.roleManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_role_import":
                webPermissions.roleManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_role_export":
                webPermissions.roleManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_user_add":
                webPermissions.userManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_user_delete":
                webPermissions.userManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_user_update":
                webPermissions.userManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_user_select":
                webPermissions.userManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_user_state_management":
                webPermissions.userManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_user_import":
                webPermissions.userManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_user_export":
                webPermissions.userManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumablesType_add":
                webPermissions.consumablesTypeManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumablesType_delete":
                webPermissions.consumablesTypeManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumablesType_update":
                webPermissions.consumablesTypeManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumablesType_select":
                webPermissions.consumablesTypeManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumablesType_state_management":
                webPermissions.consumablesTypeManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumablesType_import":
                webPermissions.consumablesTypeManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumablesType_export":
                webPermissions.consumablesTypeManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;

            case "c_referenceInformationTemplate_add":
                webPermissions.referenctInformationTemplatePermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_referenceInformationTemplate_delete":
                webPermissions.referenctInformationTemplatePermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_referenceInformationTemplate_update":
                webPermissions.referenctInformationTemplatePermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_referenceInformationTemplate_select":
                webPermissions.referenctInformationTemplatePermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumables_add":
                webPermissions.consumablesManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumables_delete":
                webPermissions.consumablesManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumables_update":
                webPermissions.consumablesManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumables_select":
                webPermissions.consumablesManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumables_state_management":
                webPermissions.consumablesManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumables_inbound":
                webPermissions.consumablesManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumables_outbound":
                webPermissions.consumablesManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumables_import":
                webPermissions.consumablesManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_consumables_export":
                webPermissions.consumablesManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_application_select":
                webPermissions.applicationManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_application_state_management":
                webPermissions.applicationManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_userOperate_select":
                webPermissions.userOperateManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_userOperate_export":
                webPermissions.userOperateManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_statistics_select":
                webPermissions.statisticsManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
            case "c_statistics_export":
                webPermissions.statisticsManagementPermissions.push({
                    id: p.id,
                    key: p.str,
                    value: p.name
                });
                break;
        }
    });

    return webPermissions;
};