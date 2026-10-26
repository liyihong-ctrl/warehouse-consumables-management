package frame.server.controller.consumables;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.Messages;
import frame.server.constant.PermissionsConstant;
import frame.server.controller.BaseController;
import frame.server.service.consumables.CHomeService;
import frame.server.utils.ApiResultUtil;
import frame.server.vo.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 消耗品管理系统：首页Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "消耗品管理系统：首页控制器")
@RequestMapping("/cHome")
@RestController
public class CHomeController extends BaseController {
	
	@Autowired
	private CHomeService CHomeService;
	
	/**
	 * 获取首页数据
	 * 
	 * @param warehouseId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：获取首页数据")
	@OperationAnnotation(value = "消耗品管理系统：获取首页数据")
	@RequiresPermissions(PermissionsConstant.C_LOGIN_STR)
	@RequestMapping(value = "/getInitData", method = RequestMethod.GET)
	public ApiResult getInitData(String warehouseId) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		JSONObject jsonObject = new JSONObject();
		
		// 角色总数
		jsonObject.put("roleCount", CHomeService.getRoleCount(warehouseId));
		
		// 人员总数
		jsonObject.put("userCount", CHomeService.getUserCount(warehouseId));
		
		// 类型总数
		jsonObject.put("typeCount", CHomeService.getTypeCount(warehouseId));
		
		// 基准信息模板总数
		jsonObject.put("templateCount", CHomeService.getTemplateCount(warehouseId));
		
		// 消耗品总数
		jsonObject.put("consumableCount", CHomeService.getConsumableCount(warehouseId));
		
		// 申请总数
		jsonObject.put("applicationCount", CHomeService.getApplicationCount(warehouseId));
		
		return ApiResultUtil.success(jsonObject);
	}

	
	/**
	 * 获取报警消耗品数据
	 * 
	 * @param warehouseId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：获取报警消耗品数据")
	@OperationAnnotation(value = "消耗品管理系统：获取报警消耗品数据")
	@RequiresPermissions(PermissionsConstant.C_LOGIN_STR)
	@RequestMapping(value = "/getAlarmConsumables", method = RequestMethod.GET)
	public ApiResult getAlarmConsumables(String warehouseId) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		JSONObject jsonObject = new JSONObject();
		
		// 安全在公报警信息
		jsonObject.put("alarmConsumables", CHomeService.getAlarmData(warehouseId));
		
		return ApiResultUtil.success(jsonObject);
	}

}
