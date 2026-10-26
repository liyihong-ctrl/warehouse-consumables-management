package frame.server.service.consumables;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

import frame.server.domain.Role;
import frame.server.dto.RoleDto;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CRoleController.CAddRoleModel;
import frame.server.vo.consumables.CRoleController.CEditRoleModel;
import frame.server.vo.consumables.CRoleController.CImportRoleDetailModel;

public interface CRoleService {
	
	/**
	 * 获取全部角色
	 * 
	 * @return
	 */
	List<Role> getAllRoles(String warehouseId);
	
	/**
	 * 获取全部角色(分页)
	 * 
	 * @return
	 */
	PageInfo<RoleDto> getRoles(Page page);
	
	/**
	 * 获取角色详情
	 * 
	 * @param warehouseId
	 * @param roleId
	 * @return
	 */
	Role getRole(String warehouseId, String roleId);
	
	/**
	 * 校验仓库ID
	 * 1. 是否存在
	 * 2. 当前用户是否可用
	 * 
	 * @param warehouseId
	 * @return
	 */
	boolean validateWarehouseId(String userId, String warehouseId);
	
	/**
	 * 校验权限Strs
	 * 
	 * @param permissionIds
	 * @return
	 */
	boolean validatePermissionIds(List<String> permissionStrs);
	
	/**
	 * 校验角色名称是否重复
	 * 
	 * @param warehouseId
	 * @param roleName
	 * @return
	 */
	boolean isRoleNameExist(String warehouseId, String roleName);
	
	/**
	 * 校验角色标识是否重复
	 * 
	 * @param warehouseId
	 * @param roleStr
	 * @return
	 */
	boolean isRoleStrExist(String warehouseId, String roleStr);
	
	/**
	 * 新增角色
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	RoleDto addRole(CAddRoleModel model);
	
	/**
	 * 根据角色ID和仓库ID校验该角色是否存在
	 * 
	 * @param roleId
	 * @param warehouseId
	 * @return
	 */
	boolean isRoleExist(String roleId, String warehouseId);
	
	/**
	 * 根据角色ID查询全部的用户ID
	 * 
	 * @param roleId
	 * @return
	 */
	List<String> getUserIdsByRoleId(String roleId);
	
	/**
	 * 更新角色
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	RoleDto updateRole(CEditRoleModel model);
	
	/**
	 * 检查指定角色是否可以被删除
	 * 
	 * @param roleId
	 * @return
	 */
	boolean canDeleteRole(String roleId);
	
	/**
	 * 删除角色
	 * 
	 * @param roleId
	 */
	@Transactional
	void deleteRole(String roleId);
	
	/**
	 * 导入角色：获取样本文件
	 * 
	 * @param warehouseId
	 * @return
	 */
	ExcelDataModel getImportTemplateData(String warehouseId);
	
	/**
	 * 导入角色：检查字段
	 * 
	 * @param importRoles
	 * @return
	 */
	JSONObject checkImportRoles(List<CImportRoleDetailModel> importRoles);
	
	/**
	 * 导入角色
	 * 
	 * 角色名称存在则更新、角色名称不存在则添加
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	List<RoleDto> importRoles(String warehouseId, List<CImportRoleDetailModel> importRoles);
	
	/**
	 * 导出角色
	 * 
	 * @param warehouseId
	 * @return
	 */
	ExcelDataModel getExportRolesData(String warehouseId);

}
