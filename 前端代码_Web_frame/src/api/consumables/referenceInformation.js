import {
    post
} from '../../utils/http';

// 分页获取基准信息模板Api
export const getReferenceInformationTemplatesApi = p => post("/cReferenceInformation/getReferenceInformationTemplates", p);

// 新增基准信息模板Api
export const addReferenceInformationTemplateApi = p => post("/cReferenceInformation/addReferenceInformationTemplate", p);

// 更新基准信息模板Api
export const updateReferenceInformationTemplateApi = p => post("/cReferenceInformation/updateReferenceInformationTemplate", p);

// 删除基准信息模板Api
export const deleteReferenceInformationTemplateApi = p => post("/cReferenceInformation/deleteReferenceInformationTemplate", p);

// 批量导入基准信息模板Api
export const importReferenceInformationTemplatesApi = p => post("/cReferenceInformation/importReferenceInformationTemplates", p);