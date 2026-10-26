package frame.server.controller.consumables;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.StringUtil;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.FileUploadSettingConstant;
import frame.server.constant.Messages;
import frame.server.constant.PermissionsConstant;
import frame.server.constant.UserTypeEnum;
import frame.server.controller.BaseController;
import frame.server.domain.User;
import frame.server.dto.UserDto;
import frame.server.service.consumables.CUserService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.ExportExcelUtils;
import frame.server.utils.FileNameUtils;
import frame.server.utils.FileUtils;
import frame.server.vo.ApiResult;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CUserController.CAddUserModel;
import frame.server.vo.consumables.CUserController.CEditUserModel;
import frame.server.vo.consumables.CUserController.CImportUserDetailModel;
import frame.server.vo.consumables.CUserController.CImportUserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 消耗品管理系统：人员管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "消耗品管理系统：人员管理控制器")
@RequestMapping("/cUser")
@RestController
public class CUserController extends BaseController {

	// 用户头像大小限制为2M
	private static final long AVATAR_SIZE = 1024 * 1024 * 2;

	@Autowired
	private CUserService cUserService;

	/**
	 * 消耗品管理系统：获取全部用户信息
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：获取全部用户信息")
	@OperationAnnotation(value = "消耗品管理系统：获取全部用户信息")
	@RequiresPermissions(PermissionsConstant.C_USER_SELECT_STR)
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ApiResult allUsers(String warehouseId) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		return ApiResultUtil.success(cUserService.getAllUsers());
	}

	/**
	 * 消耗品管理系统：获取全部用户信息(分页)
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：获取全部用户信息(分页)")
	@OperationAnnotation(value = "消耗品管理系统：获取全部用户信息(分页)")
	@RequiresPermissions(PermissionsConstant.C_USER_SELECT_STR)
	@RequestMapping(value = "/getUsers", method = RequestMethod.POST)
	public ApiResult getUsers(Page page) {
		JSONObject searchCondition = page.getSearchCondition();

		// 校验参数
		if (searchCondition == null || StringUtil.isEmpty(searchCondition.getString("warehouseId"))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(searchCondition.getString("warehouseId"))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		return ApiResultUtil.success(cUserService.getUsers(page));
	}

	/**
	 * 消耗品管理系统：获取申请用户信息(分页)
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：获取申请用户信息(分页)")
	@OperationAnnotation(value = "消耗品管理系统：获取申请用户信息(分页)")
	@RequiresPermissions(PermissionsConstant.C_USER_SELECT_STR)
	@RequestMapping(value = "/getApplicationUsers", method = RequestMethod.POST)
	public ApiResult getApplicationUsers(Page page) {
		JSONObject searchCondition = page.getSearchCondition();

		// 校验参数
		if (searchCondition == null || StringUtil.isEmpty(searchCondition.getString("warehouseId"))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(searchCondition.getString("warehouseId"))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		return ApiResultUtil.success(cUserService.getApplicationUsers(page));
	}

	/**
	 * 消耗品管理系统：获取全部角色信息
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：获取全部角色信息")
	@OperationAnnotation(value = "消耗品管理系统：获取全部角色信息")
	@RequiresPermissions(logical = Logical.OR, value = { PermissionsConstant.C_USER_ADD_STR,
			PermissionsConstant.C_USER_UPDATE_STR, })
	@RequestMapping(value = "/getRoles", method = RequestMethod.GET)
	public ApiResult allRoles(String warehouseId) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		return ApiResultUtil.success(cUserService.getAllRoles(warehouseId));
	}

	/**
	 * 消耗品管理系统：上传用户头像
	 * 
	 * @param multipartFile
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：上传用户头像至服务器")
	@RequiresPermissions(PermissionsConstant.C_LOGIN_STR)
	@RequestMapping(value = "/uploadAvatar", method = RequestMethod.POST)
	public ApiResult uploadAvatar(@RequestParam("file") MultipartFile multipartFile) {
		try {
			// 检查图片是否为空
			if (multipartFile == null) {
				return ApiResultUtil.badRequest("上传错误：请选择图片");
			}

			// 检查图片大小
			if (multipartFile.getSize() > AVATAR_SIZE) {
				return ApiResultUtil.badRequest("上传错误：请上传2M以内的图片");
			}

			// 检查图片格式
			BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
			if (bi == null) {
				return ApiResultUtil.badRequest("上传错误：上传的文件不是图片");
			}

			// 保存至本地并返回URL
			String path = FileUploadSettingConstant.UPLOAD_DRIVE + FileUploadSettingConstant.UPLOAD_FOLDER_NAME
					+ FileUploadSettingConstant.CONSUMABLES_AVATAR;
			String fileName = FileNameUtils.getFileName(multipartFile.getOriginalFilename());
			FileUtils.upload(multipartFile, path, fileName);

			String imagePath = FileUploadSettingConstant.SERVER_IP_ADDRESS
					+ FileUploadSettingConstant.CONSUMABLES_AVATAR + fileName;

			JSONObject res = new JSONObject();
			res.put("imgPath", imagePath);
			return ApiResultUtil.success(res);
		} catch (IOException e) {
			e.printStackTrace();
			return ApiResultUtil.badRequest();
		}
	}

	/**
	 * 消耗品管理系统：新增用户
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：新增用户")
	@OperationAnnotation(value = "消耗品管理系统：新增用户")
	@RequiresPermissions(PermissionsConstant.C_USER_ADD_STR)
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public ApiResult addUser(@Validated CAddUserModel model) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验角色Ids
		if (!cUserService.validateRoleIds(model.getWarehouseId(), model.getRoleIds())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验账号是否重复
		if (cUserService.isAccountRepeat(model.getAccount())) {
			return ApiResultUtil.error("账号已存在");
		}

		// 执行新增用户操作
		UserDto user = cUserService.addUser(model);

		return ApiResultUtil.success(user);
	}

	/**
	 * 更新用户
	 * 
	 * 1. 自己无法修改自己的角色
	 * 
	 * 2. 仓库管理员的角色无法修改
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：更新用户")
	@OperationAnnotation(value = "消耗品管理系统：更新用户")
	@RequiresPermissions(PermissionsConstant.C_USER_UPDATE_STR)
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public ApiResult updateUser(@Validated CEditUserModel model) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验角色Ids
		if (!cUserService.validateRoleIds(model.getWarehouseId(), model.getRoleIds())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		User user = cUserService.getUserByUserId(model.getId());

		// 校验用户是否存在
		if (user == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行更新用户信息的操作
		UserDto userNew = cUserService.updateUser(model);

		return ApiResultUtil.success(userNew);
	}

	/**
	 * 消耗品管理系统：更新用户状态
	 * 
	 * @param userId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：更新用户状态")
	@OperationAnnotation(value = "消耗品管理系统：更新用户状态")
	@RequiresPermissions(PermissionsConstant.C_USER_STATE_MANAGEMENT_STR)
	@RequestMapping(value = "/updateUserState", method = RequestMethod.GET)
	public ApiResult updateUserState(String warehouseId, String userId) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		User user = cUserService.getUserByUserId(userId);

		// 校验用户是否存在
		if (user == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验是否是自己，自己无法更新自己的状态
		User currentLoginUser = super.getLoginUserInfo();
		if (currentLoginUser.getId().equals(user.getId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行更新用户状态操作
		cUserService.updateUserState(userId);

		return ApiResultUtil.success();
	}

	/**
	 * 消耗品管理系统：重置用户密码
	 * 
	 * @param userId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：重置用户密码")
	@OperationAnnotation(value = "消耗品管理系统：重置用户密码")
	@RequiresPermissions(PermissionsConstant.C_USER_UPDATE_STR)
	@RequestMapping(value = "/resetUserPassword", method = RequestMethod.GET)
	public ApiResult resetUserPassword(String warehouseId, String userId) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		User user = cUserService.getUserByUserId(userId);

		// 校验用户是否存在
		if (user == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行重置用户密码操作
		cUserService.resetUserPassword(userId);

		return ApiResultUtil.success();
	}

	/**
	 * 消耗品管理系统：删除用户
	 * 
	 * @param userId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：删除用户")
	@OperationAnnotation(value = "消耗品管理系统：删除用户")
	@RequiresPermissions(PermissionsConstant.C_USER_DELETE_STR)
	@RequestMapping(value = "/deleteUser", method = RequestMethod.GET)
	public ApiResult deleteUser(String warehouseId, String userId) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		User user = cUserService.getUserByUserId(userId);

		// 校验用户是否存在
		if (user == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行删除操作
		cUserService.deleteUser(userId);

		return ApiResultUtil.success();
	}

	/**
	 * 消耗品管理系统：下载模板文件
	 * 
	 * @param warehouseId
	 * @throws Exception
	 */
	@ApiOperation(value = "下载模板文件")
	@RequestMapping(value = "/template", method = RequestMethod.GET)
	public void getImportUsersTemplate(String warehouseId) throws Exception {
		ExcelDataModel data = cUserService.getImportTemplateData(warehouseId);
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		ExportExcelUtils.exportExcel(response, data.getName() + ".xlsx", data);
	}

	/**
	 * 消耗品管理系统：批量导入用户
	 * 
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：批量导入用户")
	@RequestMapping(value = "/importUsers", method = RequestMethod.POST)
	public ApiResult importUsers(@Validated CImportUserModel model) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据转换
		List<CImportUserDetailModel> importUsers = JSONObject.parseArray(model.getExcelData(),
				CImportUserDetailModel.class);

		// 数据校验
		JSONObject errorData = cUserService.checkImportUsers(model.getWarehouseId(), importUsers);

		if (errorData != null) {
			return ApiResultUtil.excelError(errorData);
		}

		// 执行导入操作
		List<UserDto> users = cUserService.importUsers(model.getWarehouseId(), importUsers);

		return ApiResultUtil.success(users);
	}

	/**
	 * 消耗品管理系统：批量导出用户
	 * 
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "消耗品管理系统：批量导出用户")
	@OperationAnnotation(value = "消耗品管理系统：批量导出用户")
	@RequestMapping(value = "/exportUsers", method = RequestMethod.GET)
	public void exportUsers(String warehouseId) throws Exception {
		ExcelDataModel data = cUserService.getExportUsersData(warehouseId);
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		ExportExcelUtils.exportExcel(response, data.getName() + ".xlsx", data);
	}

	/**
	 * 消耗品管理系统：更新用户可申请状态
	 * 
	 * @param userId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统：更新用户状态")
	@OperationAnnotation(value = "消耗品管理系统：更新用户状态")
	@RequiresPermissions(PermissionsConstant.C_USER_SELECT_STR)
	@RequestMapping(value = "/updateUserApplicationState", method = RequestMethod.GET)
	public ApiResult updateUserApplicationState(String warehouseId, String userId) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		User user = cUserService.getApplicationUserByUserId(userId);

		// 校验用户是否存在
		if (user == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验用户类型
		if (!user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行更新用户状态操作
		boolean res = cUserService.updateUserApplicationState(warehouseId, userId);

		return ApiResultUtil.success(res);
	}

}
