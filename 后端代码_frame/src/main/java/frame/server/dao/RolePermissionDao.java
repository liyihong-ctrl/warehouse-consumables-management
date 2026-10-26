package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import frame.server.domain.Permission;
import frame.server.domain.RolePermission;

/**
 * 角色权限
 * 
 * @author Administrator
 *
 */
@Mapper
public interface RolePermissionDao {
	
	/**
	 * 批量添加角色权限
	 * 
	 * @param roles
	 * @return
	 */
	@Insert("<script>insert into role_permission (role_id, permission_id) values <foreach collection = 'list' item = 'item' separator = ','> (#{item.roleId}, #{item.permissionId})</foreach></script>")
	Integer insertRolePermissions(List<RolePermission> rolePermissions);
	
	/**
	 * 添加加角色权限
	 * 
	 * @param rolePermission
	 * @return
	 */
	@Insert("insert into role_permission (#{rolePermission})")
	Integer insertRolePermission(RolePermission rolePermission);
	
	/**
	 * 清空角色权限表
	 * 
	 * @return
	 */
	@Delete("delete from role_permission")
	Integer clear();
	
	/**
	 * 根据角色ID删除角色权限对应关系
	 * 
	 * @param roleId
	 * @return
	 */
	@Delete("delete from role_permission where role_id = #{roleId}")
	Integer deleteByRoleId(String roleId);
	
	/**
	 * 获取全部的角色权限对应关系
	 * 
	 * @return
	 */
	@Select("select * from role_permission")
	List<RolePermission> selectAll();
	
	/**
	 * 根据RoleId查询所对应的权限ID
	 * 
	 * @param roleId
	 * @return
	 */
	@Select("select permission_id from role_permission where role_id = #{roleId}")
	List<String> selectPermissionIdsByRoleId(String roleId);
	
	/**
	 * 根据角色ID查询权限信息
	 * 
	 * @param roleId
	 * @return
	 */
	@Select("select t1.* from permission as t1, role_permission as t2 where t2.role_id = #{roleId} and t2.permission_id = t1.id")
	List<Permission> selectPermissionsByRoleId(String roleId);

}
