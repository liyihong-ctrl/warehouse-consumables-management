package frame.server.controller.consumables;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.Messages;
import frame.server.constant.PermissionsConstant;
import frame.server.controller.BaseController;
import frame.server.dto.ReferenceInformationTemplateDto;
import frame.server.service.consumables.CReferenceInformationTempalteService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.ExportExcelUtils;
import frame.server.vo.ApiResult;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.DeleteCommonModel;
import frame.server.vo.consumables.CReferenceInformationController.CAddReferenceInformationTemplateModel;
import frame.server.vo.consumables.CReferenceInformationController.CEditReferenceInformationTemplateModel;
import frame.server.vo.consumables.CReferenceInformationController.CImportReferenceInfomationTempalteDetailModel;
import frame.server.vo.consumables.CReferenceInformationController.CImportReferenceInfomationTemplateModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 消耗品管理系统：基准信息模板管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "消耗品管理系统：人员管理控制器")
@RequestMapping("/cReferenceInformation")
@RestController
public class CReferenceInformationTempalteController extends BaseController {

	@Autowired
	private CReferenceInformationTempalteService cReferenceInformationTempalteService;

	/**
	 * 消耗品管理系统：获取全部基准信息模板
	 * 
	 * @param warehouseId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：获取全部基准信息模板(分页)")
	@OperationAnnotation(value = "消耗品管理系统：获取全部基准信息模板(分页)")
	@RequiresPermissions(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_SELECT_STR)
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ApiResult getAllReferenceInformationTemplates(String warehouseId) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		List<ReferenceInformationTemplateDto> referenceInformationTemplates = cReferenceInformationTempalteService
				.getAll(warehouseId);

		return ApiResultUtil.success(referenceInformationTemplates);
	}

	/**
	 * 消耗品管理系统：获取全部基准信息模板(分页)
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：获取全部基准信息模板(分页)")
	@OperationAnnotation(value = "消耗品管理系统：获取全部基准信息模板(分页)")
	@RequiresPermissions(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_SELECT_STR)
	@RequestMapping(value = "/getReferenceInformationTemplates", method = RequestMethod.POST)
	public ApiResult getReferenceInformationTemplates(Page page) {
		JSONObject searchCondition = page.getSearchCondition();

		// 校验参数
		if (searchCondition == null || StringUtil.isEmpty(searchCondition.getString("warehouseId"))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(searchCondition.getString("warehouseId"))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		PageInfo<ReferenceInformationTemplateDto> pageInfo = cReferenceInformationTempalteService
				.getReferenceInformationTemplates(page);

		return ApiResultUtil.success(pageInfo);
	}

	/**
	 * 消耗品管理系统：新增基准信息模板
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：新增基准信息模板")
	@OperationAnnotation(value = "消耗品管理系统：新增基准信息模板")
	@RequiresPermissions(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_ADD_STR)
	@RequestMapping(value = "/addReferenceInformationTemplate", method = RequestMethod.POST)
	public ApiResult addReferenceInformationTemplate(@Validated CAddReferenceInformationTemplateModel model) {
		// 校验登录基准信息模板是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest("保存失败：基准信息名长度范围为1~255");
		}

		// 校验模板名称是否重复
		if (cReferenceInformationTempalteService.checkReferenceInformationTemplateNameIsExist(model.getWarehouseId(),
				model.getName())) {
			return ApiResultUtil.badRequest("保存失败：基准信息名称重复");
		}

		// 执行新增基准信息模板操作
		ReferenceInformationTemplateDto referenceInformationTemplate = cReferenceInformationTempalteService
				.addReferenceInformationTemplate(model);

		return ApiResultUtil.success(referenceInformationTemplate);
	}

	/**
	 * 消耗品管理系统：更新基准信息模板
	 * 
	 * 系统内置模板不可更新
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：更新基准信息模板")
	@OperationAnnotation(value = "消耗品管理系统：更新基准信息模板")
	@RequiresPermissions(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_EDIT_STR)
	@RequestMapping(value = "/updateReferenceInformationTemplate", method = RequestMethod.POST)
	public ApiResult updateUser(@Validated CEditReferenceInformationTemplateModel model) {
		// 校验登录基准信息模板是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest("保存失败：基准信息名长度范围为1~255");
		}

		ReferenceInformationTemplateDto template = cReferenceInformationTempalteService
				.getReferenceInformationTemplate(model.getWarehouseId(), model.getId());
		
		if(template == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		

		// 校验模板名称是否重复
		if(!model.getName().equals(template.getName())) {
			if (cReferenceInformationTempalteService.checkReferenceInformationTemplateNameIsExist(model.getWarehouseId(),
					model.getName())) {
				return ApiResultUtil.badRequest("保存失败：基准信息名称重复");
			}
		}

		// 执行更新基准信息模板信息的操作
		ReferenceInformationTemplateDto referenceInformationTemplate = cReferenceInformationTempalteService
				.updateReferenceInformationTemplate(model);

		return ApiResultUtil.success(referenceInformationTemplate);
	}

	/**
	 * 消耗品管理系统：删除基准信息模板
	 * 
	 * 系统内置模板不可删除
	 * 
	 * @param warehouseId
	 * @param tempalteId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：删除基准信息模板")
	@OperationAnnotation(value = "消耗品管理系统：删除基准信息模板")
	@RequiresPermissions(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_DELETE_STR)
	@RequestMapping(value = "/deleteReferenceInformationTemplate", method = RequestMethod.POST)
	public ApiResult deleteUser(@Validated DeleteCommonModel model) {
		// 校验登录基准信息模板是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验基准信息模板ID
		if (!cReferenceInformationTempalteService.checkReferenceInformationTemplateId(model.getWarehouseId(),
				model.getId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行删除操作
		cReferenceInformationTempalteService.deleteReferenceInformationTemplate(model.getWarehouseId(), model.getId());

		return ApiResultUtil.success();
	}

	/**
	 * 消耗品管理系统：下载模板文件
	 * 
	 * @param warehouseId
	 * @throws Exception
	 */
	@ApiOperation(value = "消耗品管理系统：下载模板文件")
	@RequestMapping(value = "/template", method = RequestMethod.GET)
	public void getImportConsumablesTemplate(String warehouseId) throws Exception {
		ExcelDataModel data = cReferenceInformationTempalteService.getImportTemplateData(warehouseId);
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		ExportExcelUtils.exportExcel(response, data.getName() + ".xlsx", data);
	}

	/**
	 * 消耗品管理系统：批量导入基准信息模板
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：批量导入基准信息模板")
	@RequestMapping(value = "/importReferenceInformationTemplates", method = RequestMethod.POST)
	public ApiResult importReferenceInformationTemplates(CImportReferenceInfomationTemplateModel model) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据转换
		List<CImportReferenceInfomationTempalteDetailModel> importReferenceInformationTemplates = JSONObject
				.parseArray(model.getExcelData(), CImportReferenceInfomationTempalteDetailModel.class);
		if (importReferenceInformationTemplates == null || importReferenceInformationTemplates.isEmpty()) {
			return ApiResultUtil.success();
		}

		// 数据校验
		JSONObject errorData = cReferenceInformationTempalteService
				.checkImportReferenceInformationTemplates(importReferenceInformationTemplates);
		if (errorData != null) {
			return ApiResultUtil.excelError(errorData);
		}

		// 执行批量导入操作
		List<ReferenceInformationTemplateDto> res = cReferenceInformationTempalteService
				.importTempaltes(model.getWarehouseId(), super.getUserId(), importReferenceInformationTemplates);

		return ApiResultUtil.success(res);
	}

	/**
	 * 消耗品管理系统：批量导出基准信息模板
	 * 
	 * @return
	 * @throws Exception 
	 */
	@ApiOperation(value = "消耗品管理系统：批量导出基准信息模板")
	@OperationAnnotation(value = "消耗品管理系统：批量导出基准信息模板")
	@RequestMapping(value = "/exportReferenceInformationTemplates", method = RequestMethod.GET)
	public void exportReferenceInformationTemplates(String warehouseId) throws Exception {
		ExcelDataModel data = cReferenceInformationTempalteService.getExportTempaltesData(warehouseId);
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		ExportExcelUtils.exportExcel(response, data.getName() + ".xlsx", data);
	}

}
