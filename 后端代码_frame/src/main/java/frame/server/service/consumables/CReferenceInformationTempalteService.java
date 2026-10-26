package frame.server.service.consumables;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

import frame.server.dto.ReferenceInformationTemplateDto;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CReferenceInformationController.CAddReferenceInformationTemplateModel;
import frame.server.vo.consumables.CReferenceInformationController.CEditReferenceInformationTemplateModel;
import frame.server.vo.consumables.CReferenceInformationController.CImportReferenceInfomationTempalteDetailModel;

public interface CReferenceInformationTempalteService {

	/**
	 * 获取全部基准信息模板
	 * 
	 * @param warehouseId
	 * @return
	 */
	List<ReferenceInformationTemplateDto> getAll(String warehouseId);

	/**
	 * 获取全部基准信息模板(分页)
	 * 
	 * @return
	 */
	PageInfo<ReferenceInformationTemplateDto> getReferenceInformationTemplates(Page page);

	/**
	 * 根据基准信息模板ID获取基准信息模板数据
	 * 
	 * @param warehouseId
	 * @param tempalteId
	 * @return
	 */
	ReferenceInformationTemplateDto getReferenceInformationTemplate(String warehouseId, String tempalteId);
	
	/**
	 * 检查基准信息模板ID是否存在
	 * 
	 * @param warehouseId
	 * @param tempalteId
	 * @return
	 */
	boolean checkReferenceInformationTemplateId(String warehouseId, String tempalteId);

	/**
	 * 检查基准信息模板名称是否存在
	 * 
	 * @param warehouseId
	 * @param tempalteName
	 * @return
	 */
	boolean checkReferenceInformationTemplateNameIsExist(String warehouseId, String tempalteName);
	
	/**
	 * 新增基准信息模板
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	ReferenceInformationTemplateDto addReferenceInformationTemplate(CAddReferenceInformationTemplateModel model);

	/**
	 * 更新基准信息模板
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	ReferenceInformationTemplateDto updateReferenceInformationTemplate(CEditReferenceInformationTemplateModel model);

	/**
	 * 删除基准信息模板
	 * 
	 * @param ReferenceInformationTemplateId
	 */
	@Transactional
	void deleteReferenceInformationTemplate(String warehouseId, String tempalteId);

	/**
	 * 批量导入基准信息模板：获取样本文件
	 * 
	 * @param warehouseId
	 * @return
	 */
	ExcelDataModel getImportTemplateData(String warehouseId);

	/**
	 * 批量导入基准信息模板：数据校验
	 * 
	 * @param importReferenceInformationTemplates
	 * @return
	 */
	JSONObject checkImportReferenceInformationTemplates(
			List<CImportReferenceInfomationTempalteDetailModel> importReferenceInformationTemplates);

	/**
	 * 批量导入基准信息模板：导入
	 * 
	 * @param warehouseId
	 * @param userId
	 * @param importReferenceInformationTemplates
	 * @return
	 */
	List<ReferenceInformationTemplateDto> importTempaltes(String warehouseId, String userId,
			List<CImportReferenceInfomationTempalteDetailModel> importReferenceInformationTemplates);

	/**
	 * 批量导出基准信息模板
	 * 
	 * @param warehouseId
	 * @return
	 */
	ExcelDataModel getExportTempaltesData(String warehouseId);

}
