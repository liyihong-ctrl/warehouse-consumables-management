package frame.server.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import frame.server.domain.Permission;
import frame.server.domain.Role;
import frame.server.domain.RolePermission;
import frame.server.domain.User;
import frame.server.domain.UserPermission;
import frame.server.domain.UserRole;
import frame.server.dto.ReferenceInformationTemplateDto;

public interface InitDatabaseService {
	
	/**
	 * 初始化权限表
	 * 
	 * @return
	 */
	@Transactional
	List<Permission> initPermission();
	
	/**
	 * 初始化角色表
	 * 
	 * @return
	 */
	@Transactional
	List<Role> initRole();
	
	/**
	 * 初始化角色权限表
	 * 
	 * @return
	 */
	@Transactional
	List<RolePermission> initRolePermission();

	/**
	 * 初始化人员表
	 * 
	 * @return
	 */
	@Transactional
	List<User> initUser();

	/**
	 * 初始化人员角色表
	 * 
	 * @return
	 */
	@Transactional
	List<UserRole> initUserRole();

	/**
	 * 初始化人员权限表
	 * 
	 * @return
	 */
	@Transactional
	List<UserPermission> initUserPermission();
	
	/**
	 * 初始化基准信息模板表
	 * 
	 * @return
	 */
	@Transactional
	List<ReferenceInformationTemplateDto> initReferenceInformationTemplate();

}
