package frame.server.controller.consumables;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.ApprovalStatusConstant;
import frame.server.constant.Messages;
import frame.server.constant.PermissionsConstant;
import frame.server.controller.BaseController;
import frame.server.domain.Application;
import frame.server.dto.ApplicationDto;
import frame.server.service.consumables.CApplicationService;
import frame.server.utils.ApiResultUtil;
import frame.server.vo.ApiResult;
import frame.server.vo.Page;
import frame.server.vo.consumables.CApplicationController.ApprovalModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 消耗品管理系统-申请管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "消耗品管理系统-申请管理控制器")
@RequestMapping("/cApplication")
@RestController
public class CApplicationController extends BaseController {

	@Autowired
	private CApplicationService cApplicationService;

	/**
	 * 获取全部申请信息(分页)
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：获取全部申请信息(分页)")
	@OperationAnnotation(value = "消耗品管理系统：获取全部申请信息(分页)")
	@RequiresPermissions(PermissionsConstant.C_APPLICATION_SELECT_STR)
	@RequestMapping(value = "/getApplications", method = RequestMethod.POST)
	public ApiResult getApplications(Page page) {
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
		PageInfo<ApplicationDto> applications = cApplicationService.getApplications(page);

		return ApiResultUtil.success(applications);
	}

	/**
	 * 查看申请详细时更新申请状态(未审核=>审核中)
	 * 
	 * @param applicationId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：查看申请详细时更新申请状态(未审核=>审核中)")
	@OperationAnnotation(value = "消耗品管理系统：查看申请详细时更新申请状态(未审核=>审核中)")
	@RequiresPermissions(PermissionsConstant.C_APPLICATION_SELECT_STR)
	@RequestMapping(value = "/viewApplicationDetail", method = RequestMethod.GET)
	public ApiResult viewApplicationDetail(String warehouseId, String applicationId) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		Application application = cApplicationService.getApplication(applicationId);
		
		if(application== null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		if(application.getApprovalStatus() != ApprovalStatusConstant.NO_REVIEW) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		// 更新审批状态
		cApplicationService.setApplicationApprovalUnderReview(applicationId);
		
		return ApiResultUtil.success();
	}

	/**
	 * 审批申请
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：审批申请")
	@OperationAnnotation(value = "消耗品管理系统：审批申请")
	@RequiresPermissions(PermissionsConstant.C_APPLICATION_STATE_MANAGEMENT_STR)
	@RequestMapping(value = "/doApproval", method = RequestMethod.POST)
	public ApiResult doApproval(@Validated ApprovalModel model) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		Application application = cApplicationService.getApplication(model.getApplicationId());

		// 校验申请ID
		if (application == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验审批状态
		if (!(model.getApprovalState() == ApprovalStatusConstant.UNDER_REVIEW
				|| model.getApprovalState() == ApprovalStatusConstant.NOT_PASS
				|| model.getApprovalState() == ApprovalStatusConstant.EXAMINATION_PASSED
				|| model.getApprovalState() == ApprovalStatusConstant.COMPLETED)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		if (application.getApprovalStatus() >= model.getApprovalState()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行审批操作
		cApplicationService.doApproval(model);

		return ApiResultUtil.success();
	}

	/**
	 * 批量导出申请信息
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：批量导出申请信息")
	@OperationAnnotation(value = "消耗品管理系统：批量导出申请信息")
	@RequiresPermissions(PermissionsConstant.C_APPLICATION_SELECT_STR)
	@RequestMapping(value = "/exportApplications", method = RequestMethod.POST)
	public ApiResult exportApplications() {
		return ApiResultUtil.success();
	}

}
