package frame.server.controller.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.ApprovalStatusConstant;
import frame.server.constant.Messages;
import frame.server.constant.PermissionsConstant;
import frame.server.constant.UserTypeEnum;
import frame.server.controller.BaseController;
import frame.server.domain.Warehouse;
import frame.server.dto.ApplicationDto;
import frame.server.dto.ConsumableDto;
import frame.server.dto.PdfView;
import frame.server.dto.UserDto;
import frame.server.service.application.AApplicationService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.StringUtil;
import frame.server.vo.ApiResult;
import frame.server.vo.Page;
import frame.server.vo.application.AApplicationController.AAddApplicationModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 消耗品申请系统：申请管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "消耗品申请系统：申请管理控制器")
@RequestMapping("/aApplication")
@RestController
public class AApplicationController extends BaseController {

	@Autowired
	private AApplicationService aApplicationService;

	/**
	 * 消耗品申请系统：获取全部申请信息(分页)
	 *
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品申请系统：获取全部申请信息(分页)")
	@OperationAnnotation(value = "消耗品申请系统：获取全部申请信息(分页)")
	@RequiresPermissions(PermissionsConstant.A_LOGIN_STR)
	@RequestMapping(value = "/getApplications", method = RequestMethod.POST)
	public ApiResult getApplications(Page page) {
		UserDto user = super.getLoginUserInfo();

		// 校验当前用户类型 (系统管理员 || 其他人员)
		if (!(user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))
				|| user.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue())))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行查询操作
		PageInfo<ApplicationDto> pageInfo = aApplicationService.getApplications(super.getUserId(), page);

		return ApiResultUtil.success(pageInfo);
	}

	/**
	 * 消耗品申请系统：根据申请ID获取消耗品信息
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品申请系统：根据申请ID获取消耗品信息")
	@OperationAnnotation(value = "消耗品申请系统：根据申请ID获取消耗品信息")
	@RequiresPermissions(PermissionsConstant.A_LOGIN_STR)
	@RequestMapping(value = "/getApplication", method = RequestMethod.GET)
	public ApiResult getApplication(String applicationId) {
		UserDto user = super.getLoginUserInfo();

		// 校验当前用户类型 (系统管理员 || 其他人员)
		if (!(user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))
				|| user.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue())))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行查询操作
		ApplicationDto application = aApplicationService.getApplicationDetailById(applicationId);

		return ApiResultUtil.success(application);
	}

	/**
	 * 消耗品申请系统：检测用户是否可以申请
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品申请系统：检测用户是否可以申请")
	@OperationAnnotation(value = "消耗品申请系统：检测用户是否可以申请")
	@RequiresPermissions(PermissionsConstant.A_LOGIN_STR)
	@RequestMapping(value = "/canApplication", method = RequestMethod.GET)
	public ApiResult canApplication() {
		UserDto user = super.getLoginUserInfo();

		// 校验当前用户类型 (系统管理员 || 其他人员)
		if (!(user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))
				|| user.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue())))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		return ApiResultUtil.success(aApplicationService.canApplication(super.getUserId(), user.getType()));
	}

	/**
	 * 消耗品申请系统：获取系统仓库信息
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品申请系统：获取系统仓库信息")
	@OperationAnnotation(value = "消耗品申请系统：获取系统仓库信息")
	@RequiresPermissions(PermissionsConstant.A_LOGIN_STR)
	@RequestMapping(value = "/getWarehouses", method = RequestMethod.GET)
	public ApiResult getWarehouses() {
		UserDto user = super.getLoginUserInfo();

		// 校验当前用户类型 (系统管理员 || 其他人员)
		if (!(user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))
				|| user.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue())))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行查询操作
		List<Warehouse> warehouses = aApplicationService.getWarehouses(super.getUserId(), user.getType());

		return ApiResultUtil.success(warehouses);
	}

	/**
	 * 消耗品申请系统：获取消耗品信息
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品申请系统：获取系统消耗品信息")
	@OperationAnnotation(value = "消耗品申请系统：获取系统消耗品信息")
	@RequiresPermissions(PermissionsConstant.A_LOGIN_STR)
	@RequestMapping(value = "/getConsumables", method = RequestMethod.POST)
	public ApiResult getConsumables(Page page) {
		UserDto user = super.getLoginUserInfo();

		// 校验当前用户类型 (系统管理员 || 其他人员)
		if (!(user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))
				|| user.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue())))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行查询操作
		PageInfo<ConsumableDto> consumables = aApplicationService.getConsumables(page);

		return ApiResultUtil.success(consumables);
	}

	/**
	 * 消耗品申请系统：新增申请
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品申请系统：新增申请")
	@OperationAnnotation(value = "消耗品申请系统：新增申请")
	@RequiresPermissions(PermissionsConstant.A_LOGIN_STR)
	@RequestMapping(value = "/addApplication", method = RequestMethod.POST)
	public ApiResult addApplication(@Validated AAddApplicationModel model) {
		UserDto user = super.getLoginUserInfo();

		// 校验当前用户类型 (系统管理员 || 其他人员)
		if (!(user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))
				|| user.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue())))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验仓库
		if (!aApplicationService.isWarehouseExist(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 检验消耗品数据
		if (!aApplicationService.checkConsumables(model)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行新增操作
		ApplicationDto application = aApplicationService.addApplication(super.getUserId(), model);

		return ApiResultUtil.success(application);
	}

	/**
	 * 消耗品申请系统：取消申请
	 * 
	 * @paramapplication
	 * @return
	 */
	@ApiOperation(value = "消耗品申请系统：取消申请")
	@OperationAnnotation(value = "消耗品申请系统：取消申请")
	@RequiresPermissions(PermissionsConstant.A_LOGIN_STR)
	@RequestMapping(value = "/cancelApplication", method = RequestMethod.GET)
	public ApiResult cancelApplication(String applicationId) {
		UserDto user = super.getLoginUserInfo();

		// 校验当前用户类型 (系统管理员 || 其他人员)
		if (!(user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))
				|| user.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue())))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 检查申请ID
		if (StringUtil.isBlank(applicationId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		ApplicationDto application = aApplicationService.getApplicationDetailById(applicationId);

		// 检查申请是否存在
		if (application == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 检查申请是否属于当前用户
		if (!application.getUserId().equals(super.getUserId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 检查申请状态
		if (application.getApprovalStatus() != ApprovalStatusConstant.NO_REVIEW) {
			return ApiResultUtil.error("取消申请错误：该申请状态改变，不可取消申请");
		}

		// 执行取消申请操作
		aApplicationService.cancelApplication(applicationId);

		return ApiResultUtil.success(application);
	}

	/**
	 * 消耗品申请系统：检查申请是否可以导出
	 * 
	 * @param applicationId
	 * @return
	 */
	@ApiOperation(value = "消耗品申请系统：检查申请是否可以导出")
	@OperationAnnotation(value = "消耗品申请系统：检查申请是否可以导出")
	@RequiresPermissions(PermissionsConstant.A_LOGIN_STR)
	@RequestMapping(value = "/canExport", method = RequestMethod.GET)
	public ApiResult checkApplicationCanExport(String applicationId) {
		UserDto user = super.getLoginUserInfo();

		// 校验当前用户类型 (系统管理员 || 其他人员)
		if (!(user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))
				|| user.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue())))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 检查申请ID
		if (StringUtil.isBlank(applicationId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		ApplicationDto application = aApplicationService.getApplicationDetailById(applicationId);

		// 检查申请是否存在
		if (application == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 检查申请是否属于当前用户
		if (!application.getUserId().equals(super.getUserId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 审核通过
		if (application.getApprovalStatus() == ApprovalStatusConstant.EXAMINATION_PASSED) {
			return ApiResultUtil.success();
		}

		return ApiResultUtil.error("导出错误：申请状态错误");
	}

	/**
	 * 消耗品申请系统：打印申请单
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品申请系统：打印申请单")
	@OperationAnnotation(value = "消耗品申请系统：打印申请单")
	@RequestMapping(value = "/export/pdf", method = RequestMethod.GET)
	public ModelAndView importApplications(String applicationId) {
		ApplicationDto application = aApplicationService.getApplicationDetailById(applicationId);

		// 不为审核通过状态则不可以打印申请单
		if (application == null || application.getApprovalStatus() != ApprovalStatusConstant.EXAMINATION_PASSED) {
			return new ModelAndView();
		}

		Map<String, Object> model = new HashMap<>();
		model.put("application", application);

		return new ModelAndView(new PdfView(), model);
	}

}
