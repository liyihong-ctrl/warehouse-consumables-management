package frame.server.controller.consumables;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
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
import frame.server.dto.ConsumableOperationHistoryDto;
import frame.server.service.consumables.COperationHistoryService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.ExportExcelUtils;
import frame.server.vo.ApiResult;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CSearchConditionModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 消耗品管理系统：操作履历控制器
 * 
 * @author Administrator
 *
 */
@Api(tags = "消耗品管理系统-申请管理控制器")
@RequestMapping("/cOperationHistory")
@RestController
public class COperationHistoryController extends BaseController {

	@Autowired
	private COperationHistoryService cOperationHistoryService;

	/**
	 * 消耗品管理系统：获取操作履历(分页)
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：获取操作履历(分页)")
	@OperationAnnotation(value = "消耗品管理系统：获取操作履历(分页)")
	@RequiresPermissions(PermissionsConstant.C_USEROPERATE_SELECT_STR)
	@RequestMapping(value = "/getOperationHistorys", method = RequestMethod.POST)
	public ApiResult getOperationHistorys(Page page) {
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
		PageInfo<ConsumableOperationHistoryDto> pageInfo = cOperationHistoryService.getOperationHistorys(page);

		return ApiResultUtil.success(pageInfo);
	}

	/**
	 * 消耗品管理系统：批量导出操作履历
	 * 
	 * @param warehouseId
	 * @param searchType
	 * @param searchStr
	 * @param startTime
	 * @param endTime
	 * @throws Exception
	 */
	@ApiOperation(value = "消耗品管理系统：批量导出操作履历")
	@OperationAnnotation(value = "消耗品管理系统：批量导出操作履历")
	@RequestMapping(value = "/exportConsumables", method = RequestMethod.GET)
	public void exportOperationHistorys(String warehouseId, String searchType, String searchStr, String startTime,
			String endTime) throws Exception {
		CSearchConditionModel model = new CSearchConditionModel(warehouseId, searchType, searchStr, startTime, endTime, false);

		ExcelDataModel data = cOperationHistoryService.getExportRolesData(model);

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();

		ExportExcelUtils.exportExcel(response, data.getName() + ".xlsx", data);

	}

}
