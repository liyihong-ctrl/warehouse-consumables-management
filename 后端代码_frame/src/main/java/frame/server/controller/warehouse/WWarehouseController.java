package frame.server.controller.warehouse;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

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

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.FileUploadSettingConstant;
import frame.server.constant.Messages;
import frame.server.constant.RolesConstant;
import frame.server.domain.User;
import frame.server.domain.Warehouse;
import frame.server.dto.WarehouseDto;
import frame.server.service.warehouse.WWarehouseService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.FileNameUtils;
import frame.server.utils.FileUtils;
import frame.server.utils.StringUtil;
import frame.server.vo.ApiResult;
import frame.server.vo.Page;
import frame.server.vo.warehouse.WWarehouseController.AddWarehouseModel;
import frame.server.vo.warehouse.WWarehouseController.EditWarehouseModel;
import frame.server.vo.warehouse.WWarehouseController.UpdateAssignUserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 仓库管理系统：仓库管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "仓库管理系统：仓库管理控制器")
@RequestMapping("/wWarehouse")
@RestController
public class WWarehouseController {

	// 仓库照片大小限制为2M
	private static final long IMAGE_SIZE = 1024 * 1024 * 2;

	@Autowired
	private WWarehouseService wWarehouseService;

	/**
	 * 仓库管理系统：获取仓库信息
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：获取仓库信息(分页)")
	@OperationAnnotation(value = "仓库管理系统：获取仓库信息(分页)")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/all", method = RequestMethod.POST)
	public ApiResult all() {
		return ApiResultUtil.success(wWarehouseService.getAllWarehouses());
	}

	/**
	 * 仓库管理系统：获取仓库信息(分页)
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：获取仓库信息(分页)")
	@OperationAnnotation(value = "仓库管理系统：获取仓库信息(分页)")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/getWarehouses", method = RequestMethod.POST)
	public ApiResult getWarehouses(Page page) {
		return ApiResultUtil.success(wWarehouseService.getWarehouses(page));
	}

	/**
	 * 仓库管理系统：上传仓库照片
	 * 
	 * @param multipartFile
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：上传仓库照片至服务器")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
	public ApiResult uploadWarehouseImage(@RequestParam("file") MultipartFile multipartFile) {
		try {
			// 检查图片是否为空
			if (multipartFile == null) {
				return ApiResultUtil.badRequest("上传错误：请选择图片");
			}

			// 检查图片大小
			if (multipartFile.getSize() > IMAGE_SIZE) {
				return ApiResultUtil.badRequest("上传错误：请上传2M以内的图片");
			}

			// 检查图片格式
			BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
			if (bi == null) {
				return ApiResultUtil.badRequest("上传错误：上传的文件不是图片");
			}

			// 保存至本地并返回URL
			String path = FileUploadSettingConstant.UPLOAD_DRIVE + FileUploadSettingConstant.UPLOAD_FOLDER_NAME
					+ FileUploadSettingConstant.WAREHOUSE_WAREHOUSE;
			String fileName = FileNameUtils.getFileName(multipartFile.getOriginalFilename());
			FileUtils.upload(multipartFile, path, fileName);

			String imagePath = FileUploadSettingConstant.SERVER_IP_ADDRESS
					+ FileUploadSettingConstant.WAREHOUSE_WAREHOUSE + fileName;

			JSONObject res = new JSONObject();
			res.put("imgPath", imagePath);
			return ApiResultUtil.success(res);
		} catch (IOException e) {
			e.printStackTrace();
			return ApiResultUtil.badRequest();
		}
	}

	/**
	 * 仓库管理系统：添加仓库
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：添加仓库")
	@OperationAnnotation(value = "仓库管理系统：添加仓库")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/addWarehouse", method = RequestMethod.POST)
	public ApiResult addWarehouse(@Validated AddWarehouseModel model) {
		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验编号是否重复
		if (wWarehouseService.isWarehouseCodeRepeat(model.getCode())) {
			return ApiResultUtil.error("仓库编号重复");
		}

		// 校验名称是否重复
		if (wWarehouseService.isWarehouseNameRepeat(model.getName())) {
			return ApiResultUtil.error("仓库名称重复");
		}

		// 执行添加仓库的操作
		WarehouseDto warehouse = wWarehouseService.addWarehouse(model);

		return ApiResultUtil.success(warehouse);
	}

	/**
	 * 仓库管理系统：修改仓库信息
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：修改仓库信息")
	@OperationAnnotation(value = "仓库管理系统：修改仓库信息")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/editWarehouse", method = RequestMethod.POST)
	public ApiResult editWarehouse(@Validated EditWarehouseModel model) {
		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		WarehouseDto warehouse = wWarehouseService.getWarehouseByWarehouseId(model.getId());

		// 检查仓库是否存在
		if (warehouse == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验编号是否重复
		if (!model.getCode().equals(warehouse.getCode())) {
			if (wWarehouseService.isWarehouseCodeRepeat(model.getCode())) {
				return ApiResultUtil.error("仓库编号重复");
			}
		}

		// 校验名称是否重复
		if (!model.getName().equals(warehouse.getName())) {
			if (wWarehouseService.isWarehouseNameRepeat(model.getName())) {
				return ApiResultUtil.error("仓库名称重复");
			}
		}

		// 执行更新操作
		WarehouseDto warehouseNew = wWarehouseService.updateWarehouse(model);

		return ApiResultUtil.success(warehouseNew);
	}

	/**
	 * 仓库管理系统：修改仓库状态
	 * 
	 * @param warehouseId
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：修改仓库状态")
	@OperationAnnotation(value = "仓库管理系统：修改仓库状态")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/editWarehouseState", method = RequestMethod.GET)
	public ApiResult editWarehouseState(String warehouseId) {
		// 数据校验
		if (StringUtil.isBlank(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		Warehouse warehouse = wWarehouseService.getWarehouseByWarehouseId(warehouseId);

		// 校验仓库是否存在
		if (warehouse == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 若操作是停用当前仓库，则需要检查仓库是否可以被停用
		if (warehouse.getIsEnable() && !wWarehouseService.isWarehouseCanDeleteOrDisable(warehouseId)) {
			return ApiResultUtil.error("无法停用：该仓库下存在尚未完成的申请");
		}

		// 执行更新仓库状态操作
		wWarehouseService.updateWarehouseState(warehouseId);

		return ApiResultUtil.success();
	}

	/**
	 * 仓库管理系统：删除仓库
	 * 
	 * @param warehouseId
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：删除仓库")
	@OperationAnnotation(value = "仓库管理系统：删除仓库")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/deleteWarehouse", method = RequestMethod.GET)
	public ApiResult deleteWarehouse(String warehouseId) {
		// 数据校验
		if (StringUtil.isBlank(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验仓库是否存在
		if (wWarehouseService.getWarehouseByWarehouseId(warehouseId) == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 检查仓库是否可以被删除
		if (!wWarehouseService.isWarehouseCanDeleteOrDisable(warehouseId)) {
			return ApiResultUtil.error("无法删除：该仓库下存在尚未完成的申请");
		}

		// 执行删除仓库操作
		wWarehouseService.deleteWarehouse(warehouseId);

		return ApiResultUtil.success();
	}

	/**
	 * 仓库管理系统：获取仓库用户信息
	 * 
	 * @param
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：获取仓库用户信息")
	@OperationAnnotation(value = "仓库管理系统：获取仓库用户信息")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/getAllWarehouseManagers", method = RequestMethod.GET)
	public ApiResult getAllWarehouseManagers() {
		List<User> users = wWarehouseService.getAllWarehouseManagers();
		return ApiResultUtil.success(users);
	}

	/**
	 * 仓库管理系统：分配用户
	 * 
	 * @param
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：分配用户")
	@OperationAnnotation(value = "仓库管理系统：分配用户")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/updateAssignUser", method = RequestMethod.POST)
	public ApiResult updateAssignUser(@Validated UpdateAssignUserModel model) {
		// 校验仓库是否存在
		if (wWarehouseService.getWarehouseByWarehouseId(model.getWarehouseId()) == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验分配人员是否存在且是否为仓库管理员类型
		if (!wWarehouseService.checkWarehouseManagerIds(model.getUserIds())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行分配操作
		WarehouseDto warehouse = wWarehouseService.updateAssignUsers(model);
		
		return ApiResultUtil.success(warehouse);
	}

}
