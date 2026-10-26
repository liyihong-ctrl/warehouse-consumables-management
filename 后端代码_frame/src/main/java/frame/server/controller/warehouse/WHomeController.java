package frame.server.controller.warehouse;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.RolesConstant;
import frame.server.controller.BaseController;
import frame.server.service.warehouse.WHomeService;
import frame.server.utils.ApiResultUtil;
import frame.server.vo.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 仓库管理系统：首页管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "仓库管理系统：首页管理控制器")
@RequestMapping("/wHome")
@RestController
public class WHomeController extends BaseController {
	
	@Autowired
	private WHomeService wHomeService;

	/**
	 * 获取初始化数据
	 * 
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：获取初始化数据")
	@OperationAnnotation(value = "仓库管理系统：获取初始化数据")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/getInitData", method = RequestMethod.GET)
	public ApiResult getInitData() {
		JSONObject jsonObject = new JSONObject();
		
		// 获取人员总数(系统管理员、仓库管理员、申请人员)
		jsonObject.put("userCount", wHomeService.getUserCount());
		
		// 获取仓库总数
		jsonObject.put("warehouseCount", wHomeService.getWarehouseCount());
		
		return ApiResultUtil.success(jsonObject);
	}
	
}
