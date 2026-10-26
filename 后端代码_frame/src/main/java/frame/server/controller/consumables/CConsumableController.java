package frame.server.controller.consumables;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
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
import frame.server.constant.ConsumableMaxCountConstant;
import frame.server.constant.Messages;
import frame.server.constant.PermissionsConstant;
import frame.server.controller.BaseController;
import frame.server.domain.Consumable;
import frame.server.dto.ConsumableDto;
import frame.server.service.consumables.CConsumableService;
import frame.server.service.consumables.CTypeService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.ExportExcelUtils;
import frame.server.vo.ApiResult;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CSearchConditionModel;
import frame.server.vo.consumables.DeleteCommonModel;
import frame.server.vo.consumables.CConsumableController.CAddConsumableModel;
import frame.server.vo.consumables.CConsumableController.CBatchDeleteConsumableModel;
import frame.server.vo.consumables.CConsumableController.CEditConsumableModel;
import frame.server.vo.consumables.CConsumableController.CImportConsumableDetailModel;
import frame.server.vo.consumables.CConsumableController.CImportConsumableModel;
import frame.server.vo.consumables.CConsumableController.InboundModel;
import frame.server.vo.consumables.CConsumableController.OutboundModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 消耗品管理系统：消耗品管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "消耗品管理系统：消耗品管理控制器")
@RequestMapping("/cConsumable")
@RestController
public class CConsumableController extends BaseController {

	@Autowired
	private CConsumableService cConsumableService;

	@Autowired
	private CTypeService cTypeService;

	/**
	 * 消耗品管理系统：获取全部消耗品信息
	 * 
	 * @param warehouseId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统-获取全部消耗品信息")
	@OperationAnnotation(value = "消耗品管理系统-获取全部消耗品信息")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLES_SELECT_STR)
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ApiResult allConsumables(String warehouseId) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行检索操作
		List<ConsumableDto> consumables = cConsumableService.getAllConsumables(warehouseId);

		return ApiResultUtil.success(consumables);
	}

	/**
	 * 消耗品管理系统：获取全部消耗品信息(分页)
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统-获取全部消耗品信息(分页)")
	@OperationAnnotation(value = "消耗品管理系统-获取全部消耗品信息(分页)")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLES_SELECT_STR)
	@RequestMapping(value = "/getConsumables", method = RequestMethod.POST)
	public ApiResult getConsumables(Page page) {
		JSONObject searchCondition = page.getSearchCondition();

		// 校验参数
		if (searchCondition == null || StringUtil.isEmpty(searchCondition.getString("warehouseId"))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(searchCondition.getString("warehouseId"))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行检索操作
		PageInfo<ConsumableDto> pageInfo = cConsumableService.getConsumables(page);

		return ApiResultUtil.success(pageInfo);
	}

	/**
	 * 消耗品管理系统：获取全部消耗品信息
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统-获取全部消耗品信息")
	@OperationAnnotation(value = "消耗品管理系统-获取全部消耗品信息")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLES_SELECT_STR)
	@RequestMapping(value = "/getAllConsumables", method = RequestMethod.POST)
	public ApiResult getAllConsumables(@Validated CSearchConditionModel model) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行检索操作
		List<ConsumableDto> consumables = cConsumableService.getAllConsumablesBySearchCondition(model);

		return ApiResultUtil.success(consumables);
	}

	/**
	 * 消耗品管理系统：获取全部类型信息
	 * 
	 * @param warehouseId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统-获取全部类型信息")
	@OperationAnnotation(value = "消耗品管理系统-获取全部类型信息")
	@RequiresPermissions(logical = Logical.OR, value = { PermissionsConstant.C_CONSUMABLES_ADD_STR,
			PermissionsConstant.C_CONSUMABLES_UPDATE_STR })
	@RequestMapping(value = "/getTypes", method = RequestMethod.GET)
	public ApiResult allTypes(String warehouseId) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		return ApiResultUtil.success(cConsumableService.getTypes(warehouseId));
	}

	/**
	 * 消耗品管理系统：新增消耗品
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统-新增消耗品")
	@OperationAnnotation(value = "消耗品管理系统-新增消耗品")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLES_ADD_STR)
	@RequestMapping(value = "/addConsumable", method = RequestMethod.POST)
	public ApiResult addConsumable(@Validated CAddConsumableModel model) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验类型ID
		if (!cTypeService.isTypeExist(model.getWarehouseId(), model.getTypeId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验消耗品Code是否重复
		if (cConsumableService.isConsumableCodeExist(model.getWarehouseId(), model.getCode())) {
			return ApiResultUtil.badRequest("新增错误：消耗品Code重复");
		}

		// 执行新增消耗品操作
		ConsumableDto consumableNew = cConsumableService.addConsumable(super.getUserId(), model);

		return ApiResultUtil.success(consumableNew);
	}

	/**
	 * 消耗品管理系统：更新消耗品
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统-更新消耗品")
	@OperationAnnotation(value = "消耗品管理系统-更新消耗品")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLES_UPDATE_STR)
	@RequestMapping(value = "/updateConsumable", method = RequestMethod.POST)
	public ApiResult updateConsumable(@Validated CEditConsumableModel model) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		Consumable consumable = cConsumableService.getConsumable(model.getWarehouseId(), model.getId());

		// 校验消耗品ID
		if (consumable == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验类型ID
		if (!cTypeService.isTypeExist(model.getWarehouseId(), model.getTypeId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验数量
		if (consumable.getCount() != model.getCount()) {
			return ApiResultUtil.badRequest("更新错误：消耗品数量不可修改");
		}

		// 校验Code
		if (!consumable.getCode().equals(model.getCode())) {
			return ApiResultUtil.badRequest("更新错误：消耗品Code不可修改");
		}

		// 执行更新操作
		ConsumableDto consumableNew = cConsumableService.editConsumable(model);

		return ApiResultUtil.success(consumableNew);
	}
	
	/**
	 * 消耗品管理系统：批量删除消耗品
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：批量删除消耗品")
	@OperationAnnotation(value = "消耗品管理系统：批量删除消耗品")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLES_DELETE_STR)
	@RequestMapping(value = "/deleteConsumables", method = RequestMethod.POST)
	public ApiResult deleteConsumables(@Validated CBatchDeleteConsumableModel model) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		// 检查消耗品Ids
		for(String id : model.getIds()) {
			if (!cConsumableService.isConsumableExist(model.getWarehouseId(), id)) {
				return ApiResultUtil.badRequest(Messages.badRequest);
			}
		}
		
		// 检查消耗品是否已经被申请
		for(String id : model.getIds()) {
			if (cConsumableService.isConsumableApplied(model.getWarehouseId(), id)) {
				return ApiResultUtil.badRequest("批量删除错误：存在消耗品已被申请");
			}
		}
		
		// 执行删除操作
		for(String id : model.getIds()) {
			cConsumableService.deleteConsumable(model.getWarehouseId(), super.getUserId(), id);
		}
		
		return ApiResultUtil.success();
	}

	/**
	 * 消耗品管理系统：删除消耗品
	 * 
	 * @param consumableId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：删除消耗品")
	@OperationAnnotation(value = "消耗品管理系统：删除消耗品")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLES_DELETE_STR)
	@RequestMapping(value = "/deleteConsumable", method = RequestMethod.POST)
	public ApiResult deleteConsumable(DeleteCommonModel model) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验消耗品ID
		if (!cConsumableService.isConsumableExist(model.getWarehouseId(), model.getId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 检查消耗品是否已经被申请
		if (cConsumableService.isConsumableApplied(model.getWarehouseId(), model.getId())) {
			return ApiResultUtil.badRequest("删除错误：该消耗品已被申请");
		}

		// 执行删除操作
		cConsumableService.deleteConsumable(model.getWarehouseId(), super.getUserId(), model.getId());

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
	public void getImportConsumablesTemplate(String warehouseId) throws Exception {
		ExcelDataModel data = cConsumableService.getImportTemplateData(warehouseId);
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		ExportExcelUtils.exportExcel(response, data.getName() + ".xlsx", data);
	}

	/**
	 * 消耗品管理系统：批量导入消耗品
	 * 
	 * @param consumableId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：批量导入消耗品")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLES_IMPORT_STR)
	@RequestMapping(value = "/importConsumables", method = RequestMethod.POST)
	public ApiResult importConsumables(@Validated CImportConsumableModel model) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据转换
		List<CImportConsumableDetailModel> importConsumables = JSONObject.parseArray(model.getExcelData(),
				CImportConsumableDetailModel.class);

		// 数据校验
		JSONObject errorData = cConsumableService.checkImportConsumables(model.getWarehouseId(), importConsumables);

		if (errorData != null) {
			return ApiResultUtil.excelError(errorData);
		}

		// 执行导入操作
		List<ConsumableDto> res = cConsumableService.importConsumables(model.getWarehouseId(), super.getUserId(),
				importConsumables);

		return ApiResultUtil.success(res);
	}

	/**
	 * 消耗品管理系统：批量导出消耗品
	 * 
	 * @param warehouseId
	 * @param searchType
	 * @param searchStr
	 * @param startTime
	 * @param endTime
	 * @throws Exception
	 */
	@ApiOperation(value = "消耗品管理系统：批量导出消耗品")
	@OperationAnnotation(value = "消耗品管理系统：批量导出消耗品")
	@RequestMapping(value = "/exportConsumables", method = RequestMethod.GET)
	public void exportConsumables(String warehouseId, String searchType, String searchStr, String startTime,
			String endTime, boolean isViewAlarmData) throws Exception {
		CSearchConditionModel model = new CSearchConditionModel(warehouseId, searchType, searchStr, startTime, endTime,
				isViewAlarmData);

		ExcelDataModel data = cConsumableService.getExportConsumablesData(model);

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();

		ExportExcelUtils.exportExcel(response, data.getName() + ".xlsx", data);
	}

	/**
	 * 消耗品管理系统：出库
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：出库")
	@OperationAnnotation(value = "消耗品管理系统：出库")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLES_OUTBOUND_STR)
	@RequestMapping(value = "/outboundConsumables", method = RequestMethod.POST)
	public ApiResult outboundConsumables(@Validated OutboundModel model) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		Consumable consumable = cConsumableService.getConsumable(model.getWarehouseId(), model.getId());

		// 校验消耗品ID
		if (consumable == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验消耗品数量
		if (consumable.getCount() < model.getOutboundCount()) {
			return ApiResultUtil.badRequest("出库失败：出库数大于库存数");
		}

		// 执行出库操作
		return ApiResultUtil.success(cConsumableService.outbound(super.getUserId(), model));
	}

	/**
	 * 消耗品管理系统：入库
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：入库")
	@OperationAnnotation(value = "消耗品管理系统：入库")
	@RequiresPermissions(PermissionsConstant.C_CONSUMABLES_INBOUND_STR)
	@RequestMapping(value = "/inboundConsumables", method = RequestMethod.POST)
	public ApiResult inboundConsumables(@Validated InboundModel model) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		Consumable consumable = cConsumableService.getConsumable(model.getWarehouseId(), model.getId());

		// 校验消耗品ID
		if (consumable == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验消耗品数量
		if ((consumable.getCount() + model.getInboundCount()) > ConsumableMaxCountConstant.MAXINVENTORYCOUNT) {
			return ApiResultUtil.badRequest("入库失败：入库总数超最大存储数9999999");
		}

		// 执行入库操作
		return ApiResultUtil.success(cConsumableService.inbound(super.getUserId(), model));
	}

}
