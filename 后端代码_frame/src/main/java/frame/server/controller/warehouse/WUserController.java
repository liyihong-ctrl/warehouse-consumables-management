package frame.server.controller.warehouse;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.FileUploadSettingConstant;
import frame.server.constant.Messages;
import frame.server.constant.RolesConstant;
import frame.server.constant.UserAccountConstant;
import frame.server.constant.UserTypeEnum;
import frame.server.controller.BaseController;
import frame.server.domain.User;
import frame.server.domain.Warehouse;
import frame.server.dto.UserDto;
import frame.server.service.warehouse.WUserService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.FileNameUtils;
import frame.server.utils.FileUtils;
import frame.server.utils.StringUtil;
import frame.server.vo.ApiResult;
import frame.server.vo.Page;
import frame.server.vo.warehouse.WUserController.AddAdministratorModel;
import frame.server.vo.warehouse.WUserController.EditAdministratorModel;
import frame.server.vo.warehouse.WUserController.UpdateAssignWarehouseModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 仓库管理系统：人员管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "仓库管理系统：人员管理控制器")
@RequestMapping("/wUser")
@RestController
public class WUserController extends BaseController {

	// 用户头像大小限制为2M
	private static final long AVATAR_SIZE = 1024 * 1024 * 2;

	@Autowired
	private WUserService wUserService;

	/**
	 * 仓库管理系统：获取用户信息
	 * 
	 * @param
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：获取用户信息")
	@OperationAnnotation(value = "仓库管理系统：获取用户信息")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ApiResult getAllUsers() {
		return ApiResultUtil.success(wUserService.getAllUsers());
	}

	/**
	 * 仓库管理系统：获取用户信息(分页)
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：获取用户信息(分页)")
	@OperationAnnotation(value = "仓库管理系统：获取用户信息(分页)")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/getAdministrators", method = RequestMethod.POST)
	public ApiResult getUsers(Page page) {
		PageInfo<UserDto> pageInfo = wUserService.getUsers(page);
		if (pageInfo == null) {
			return ApiResultUtil.error("类型名称错误");
		}
		return ApiResultUtil.success(pageInfo);
	}

	/**
	 * 仓库管理系统：上传用户头像
	 * 
	 * 不保存操作履历
	 * 
	 * @param multipartFile
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：上传用户头像")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/uploadAvatar", method = RequestMethod.POST)
	public ApiResult uploadAvatar(@RequestParam("file") MultipartFile multipartFile) {
		try {
			// 检查图片是否为空
			if (multipartFile == null) {
				return ApiResultUtil.badRequest("请选择图片");
			}

			// 检查图片大小
			if (multipartFile.getSize() > AVATAR_SIZE) {
				return ApiResultUtil.badRequest("请上传2M以内的图片");
			}

			// 检查图片格式
			BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
			if (bi == null) {
				return ApiResultUtil.badRequest("上传的文件不是图片");
			}

			// 保存至本地并返回URL
			// 说明：仓库管理系统上传图片会上传至EchoCrushResources\warehouse\avatar文件夹内
			String path = FileUploadSettingConstant.UPLOAD_DRIVE + FileUploadSettingConstant.UPLOAD_FOLDER_NAME
					+ FileUploadSettingConstant.WAREHOUSE_AVATAR;
			String fileName = FileNameUtils.getFileName(multipartFile.getOriginalFilename());
			FileUtils.upload(multipartFile, path, fileName);

			String imagePath = FileUploadSettingConstant.SERVER_IP_ADDRESS + FileUploadSettingConstant.WAREHOUSE_AVATAR
					+ fileName;

			JSONObject res = new JSONObject();
			res.put("imgPath", imagePath);
			return ApiResultUtil.success(res);
		} catch (IOException e) {
			e.printStackTrace();
			return ApiResultUtil.badRequest();
		}
	}

	/**
	 * 仓库管理系统：添加用户
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：添加用户")
	@OperationAnnotation(value = "仓库管理系统：添加用户")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/addAdministrator", method = RequestMethod.POST)
	public ApiResult addAdministrator(@Validated AddAdministratorModel model) {
		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验账号是否重复
		if (wUserService.isAccountRepeat(model.getAccount())) {
			return ApiResultUtil.error("账号已存在");
		}

		// 执行新增操作
		UserDto user = wUserService.addUser(model);

		return ApiResultUtil.success(user);
	}

	/**
	 * 仓库管理系统：修改用户信息
	 * 
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "仓库管理系统：修改用户信息")
	@OperationAnnotation(value = "仓库管理系统：修改用户信息")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/editAdministrator", method = RequestMethod.POST)
	public ApiResult editAdministrator(@Validated EditAdministratorModel model) throws IOException {
		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		User user = wUserService.getUserByUserId(model.getId());

		// 校验用户是否存在
		if (user == null) {
			return ApiResultUtil.badRequest("用户不存在");
		}

		// 校验是否是自己，自己无法修改自己的类型
		User currentLoginUser = super.getLoginUserInfo();
		if (currentLoginUser.getId().equals(user.getId())) {
			// 判断是否修改了用户类型
			if (!currentLoginUser.getType().equals(model.getType())) {
				return ApiResultUtil.badRequest(Messages.badRequest);
			}
		}

		// 执行修改用户信息操作
		UserDto userNew = wUserService.updateUser(model);

		return ApiResultUtil.success(userNew);
	}

	/**
	 * 修改用户状态
	 * 
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "仓库管理系统：修改用户状态")
	@OperationAnnotation(value = "仓库管理系统：修改用户状态")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/editAdministratorState", method = RequestMethod.GET)
	public ApiResult editAdministratorState(String userId) throws IOException {
		// 数据校验
		if (StringUtil.isBlank(userId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		User user = wUserService.getUserByUserId(userId);

		// 校验用户是否存在
		if (user == null) {
			return ApiResultUtil.badRequest("用户不存在");
		}

		// 校验是否是自己
		if (userId.equals(super.getUserId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行更新状态操作
		wUserService.updateAdministratorState(userId);

		return ApiResultUtil.success();
	}

	/**
	 * 仓库管理系统：删除用户
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：删除用户")
	@OperationAnnotation(value = "仓库管理系统：删除用户")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/deleteAdministrator", method = RequestMethod.GET)
	public ApiResult deleteAdministrator(String userId) {
		// 数据校验
		if (StringUtil.isBlank(userId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		User user = wUserService.getUserByUserId(userId);

		// 校验用户是否存在
		if (user == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验是否是自己
		if (userId.equals(super.getUserId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		// 检测删除用户是否为申请人员，若为申请人员需要检测该用户下有无未完成的申请
		if(user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))) {
			if(wUserService.checkUserHasApplication(userId)) {
				return ApiResultUtil.error("无法删除：该用户有未完成的申请");
			}
		}

		// 执行删除操作
		wUserService.deleteAdministrator(userId);

		return ApiResultUtil.success();
	}

	/**
	 * 仓库管理系统：重置用户密码
	 * 
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "仓库管理系统：重置用户密码")
	@OperationAnnotation(value = "仓库管理系统：重置用户密码")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/resetAdministratorPassword", method = RequestMethod.GET)
	public ApiResult resetAdministratorPassword(String userId) throws IOException {
		// 数据校验
		if (StringUtil.isBlank(userId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		User user = wUserService.getUserByUserId(userId);

		// 校验用户是否存在
		if (user == null) {
			return ApiResultUtil.badRequest("用户不存在");
		}

		boolean isRootAdmin = false;
		// 校验用户是否是根用户
		if (user.getAccount().equals(UserAccountConstant.root_admin_account)) {
			isRootAdmin = true;
		}

		// 执行重置用户密码操作
		wUserService.updateAdministratorPassword(userId, isRootAdmin);

		return ApiResultUtil.success();
	}

	/**
	 * 仓库管理系统：获取用户分配仓库的初始化数据
	 * 
	 * @param userId
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：获取用户分配仓库的初始化数据")
	@OperationAnnotation(value = "仓库管理系统：获取用户分配仓库的初始化数据")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/getAssignWarehouseInitData", method = RequestMethod.GET)
	public ApiResult getAssignWarehouseInitData(String userId) {
		// 数据校验
		if (StringUtil.isBlank(userId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		User user = wUserService.getUserByUserId(userId);

		// 校验用户是否存在
		if (user == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验当前用户是否是仓库管理员
		if (!user.getType().equals(Integer.toString(UserTypeEnum.WAREHOUSEMANAGER.getValue()))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		Map<String, List<Warehouse>> res = wUserService.getAssignedWarehouses(userId);
		if (res == null || res.get("warehouses").isEmpty()) {
			return ApiResultUtil.error("分配失败：暂无仓库");
		}

		return ApiResultUtil.success(res);
	}

	/**
	 * 仓库管理系统：给用户动态分配仓库
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：给用户动态分配仓库")
	@OperationAnnotation(value = "仓库管理系统：给用户动态分配仓库")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/updateAssignWarehouse", method = RequestMethod.POST)
	public ApiResult updateAssignWarehouse(@Validated UpdateAssignWarehouseModel model) {
		User user = wUserService.getUserByUserId(model.getUserId());

		// 校验用户是否存在
		if (user == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验当前用户是否是仓库管理员
		if (!user.getType().equals(Integer.toString(UserTypeEnum.WAREHOUSEMANAGER.getValue()))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验仓库ID是否正确
		if (!wUserService.checkWarehouseIds(model.getWarehouseIds())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行分配操作
		List<Warehouse> res = wUserService.assignWarehouse(model);

		return ApiResultUtil.success(res);
	}

}
