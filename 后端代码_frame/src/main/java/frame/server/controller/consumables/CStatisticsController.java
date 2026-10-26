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
import frame.server.dto.ConsumableDto;
import frame.server.service.consumables.CConsumableService;
import frame.server.service.consumables.CStatisticsService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.ExportExcelUtils;
import frame.server.vo.ApiResult;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CStatisticsController.CConsumableStatisticsModel;
import frame.server.vo.consumables.CStatisticsController.CGetConsumableStatisticsSearchModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 消耗品管理系统-统计信息管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "消耗品管理系统：统计信息管理控制器")
@RequestMapping("/cStatistics")
@RestController
public class CStatisticsController extends BaseController {

	@Autowired
	private CConsumableService cConsumableService;

	@Autowired
	private CStatisticsService cStatisticsService;

	/**
	 * 消耗品管理系统：获取全部消耗品信息(分页)
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统-获取全部消耗品信息(分页)")
	@OperationAnnotation(value = "消耗品管理系统-获取全部消耗品信息(分页)")
	@RequiresPermissions(PermissionsConstant.C_STATISTICS_SELECT_STR)
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
	 * 消耗品管理系统：获取消耗品统计信息信息
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统-获取全部消耗品信息(分页)")
	@OperationAnnotation(value = "消耗品管理系统-获取全部消耗品信息(分页)")
	@RequiresPermissions(PermissionsConstant.C_STATISTICS_SELECT_STR)
	@RequestMapping(value = "/getConsumableStatistics", method = RequestMethod.POST)
	public ApiResult getConsumableStatistics(@Validated CGetConsumableStatisticsSearchModel model) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行检索操作
		List<CConsumableStatisticsModel> data = cStatisticsService.getConsumableStatics(model);

		return ApiResultUtil.success(data);
	}

	/**
	 * 消耗品管理系统：批量导出统计信息
	 * 
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "消耗品管理系统：批量导出用户")
	@OperationAnnotation(value = "消耗品管理系统：批量导出用户")
	@RequestMapping(value = "/exportStatistics", method = RequestMethod.GET)
	public void exportStatistics(String warehouseId, short staticType, String consumableIds, String startTime,
			String endTime) throws Exception {
		if (StringUtil.isEmpty(consumableIds)) {
			return;
		}

		List<String> consumableIdList = JSONObject.parseArray(consumableIds, String.class);
		CGetConsumableStatisticsSearchModel model = new CGetConsumableStatisticsSearchModel(warehouseId, staticType,
				consumableIdList, startTime, endTime);

		if (!model.validate()) {
			return;
		}

		ExcelDataModel data = cStatisticsService.getExportStatisticsData(model);
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		ExportExcelUtils.exportExcel(response, data.getName() + ".xlsx", data);
	}

}
