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

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.Messages;
import frame.server.constant.PermissionsConstant;
import frame.server.controller.BaseController;
import frame.server.domain.Type;
import frame.server.dto.TypeDto;
import frame.server.service.consumables.CTypeService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.ExportExcelUtils;
import frame.server.utils.StringUtil;
import frame.server.vo.ApiResult;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CTypeController.CAddConsumableTypeModel;
import frame.server.vo.consumables.CTypeController.CEditConsumableTypeModel;
import frame.server.vo.consumables.CTypeController.CImportTypeDetailModel;
import frame.server.vo.consumables.CTypeController.CImportTypeModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 消耗品管理系统：消耗品类型管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "消耗品管理系统：消耗品类型管理控制器")
@RequestMapping("/cType")
@RestController
public class CTypeController extends BaseController {

	@Autowired
	private CTypeService cTypeService;

	/**
	 * 获取全部类型信息(分页)
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：获取全部类型信息(分页)")
	@OperationAnnotation(value = "消耗品管理系统：获取全部类型信息(分页)")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLESTYPE_SELECT_STR)
	@RequestMapping(value = "/getTypes", method = RequestMethod.POST)
	public ApiResult getTypes(Page page) {
		JSONObject searchCondition = page.getSearchCondition();

		// 校验参数
		if (searchCondition == null || StringUtil.isBlank(searchCondition.getString("warehouseId"))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(searchCondition.getString("warehouseId"))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		return ApiResultUtil.success(cTypeService.getTypes(page));
	}

	/**
	 * 新增类型
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：新增类型")
	@OperationAnnotation(value = "消耗品管理系统：新增类型")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLESTYPE_ADD_STR)
	@RequestMapping(value = "/addType", method = RequestMethod.POST)
	public ApiResult addType(@Validated CAddConsumableTypeModel model) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验父类型ID
		if (!StringUtil.isBlank(model.getParentId())
				&& !cTypeService.isTypeExist(model.getWarehouseId(), model.getParentId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验新增类型名称是否重复
		if (cTypeService.isTypeNameRepeat(model.getWarehouseId(), model.getName())) {
			return ApiResultUtil.error("新增失败: 类型名称重复");
		}

		// 执行新增操作
		cTypeService.addType(model);

		return ApiResultUtil.success();
	}

	/**
	 * 更新类型
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：更新类型")
	@OperationAnnotation(value = "消耗品管理系统：更新类型")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLESTYPE_UPDATE_STR)
	@RequestMapping(value = "/updateType", method = RequestMethod.POST)
	public ApiResult updateType(@Validated CEditConsumableTypeModel model) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		Type type = cTypeService.getTypeByTypeId(model.getWarehouseId(), model.getId());

		// 校验类型ID
		if (type == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验父类型ID
		if (!StringUtil.isBlank(type.getParentId())) {
			if (!type.getParentId().equals(model.getParentId())) {
				return ApiResultUtil.badRequest(Messages.badRequest);
			}
		}

		if (!StringUtil.isBlank(model.getParentId())
				&& !cTypeService.isTypeExist(model.getWarehouseId(), model.getParentId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验新增类型名称是否重复
		if (!model.getName().equals(type.getName())) {
			if (cTypeService.isTypeNameRepeat(model.getWarehouseId(), model.getName())) {
				return ApiResultUtil.error("修改失败: 类型名称重复");
			}
		}

		// 执行更新操作
		cTypeService.updateType(model);

		return ApiResultUtil.success();
	}

	/**
	 * 删除类型
	 * 
	 * @param typeId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：删除类型")
	@OperationAnnotation(value = "消耗品管理系统：删除类型")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLESTYPE_DELETE_STR)
	@RequestMapping(value = "/deleteType", method = RequestMethod.GET)
	public ApiResult deleteType(String warehouseId, String typeId) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 检查类型ID
		if (!cTypeService.isTypeExist(warehouseId, typeId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 检查该类型下是否有消耗品
		if (cTypeService.isConsumableBoundType(typeId)) {
			return ApiResultUtil.error("无法删除：已有消耗品绑定了该类型");
		}

		// 执行删除操作
		cTypeService.deleteType(typeId);

		return ApiResultUtil.success();
	}

	/**
	 * 下载模板文件
	 * 
	 * @param warehouseId
	 * @throws Exception
	 */
	@ApiOperation(value = "下载模板文件")
	@RequestMapping(value = "/template", method = RequestMethod.GET)
	public void getImportTypesTemplate(String warehouseId) throws Exception {
		ExcelDataModel data = cTypeService.getImportTemplateData(warehouseId);
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		ExportExcelUtils.exportExcel(response, data.getName() + ".xlsx", data);
	}

	/**
	 * 消耗品管理系统：批量导入消耗品类型
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：批量导入消耗品类型")
	@RequestMapping(value = "/importTypes", method = RequestMethod.POST)
	public ApiResult importTypes(@Validated CImportTypeModel model) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据转换
		List<CImportTypeDetailModel> importTypes = JSONObject.parseArray(model.getExcelData(),
				CImportTypeDetailModel.class);

		// 数据校验
		JSONObject errorData = cTypeService.checkImportTypes(model.getWarehouseId(), importTypes);
		
		if (errorData != null) {
			return ApiResultUtil.excelError(errorData);
		}

		// 执行导入操作
		List<TypeDto> res =  cTypeService.importTypes(model.getWarehouseId(), importTypes);
		
		return ApiResultUtil.success(res);
	}

	/**
	 * 消耗品管理系统：批量导出消耗品类型
	 * 
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "消耗品管理系统：批量导出消耗品类型")
	@OperationAnnotation(value = "消耗品管理系统：批量导出消耗品类型")
	@RequestMapping(value = "/exportTypes", method = RequestMethod.GET)
	public void exportTypes(String warehouseId) throws Exception {
		ExcelDataModel data = cTypeService.getExportTypesData(warehouseId);
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		ExportExcelUtils.exportExcel(response, data.getName() + ".xlsx", data);
	}
	
}
