package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import frame.server.domain.Permission;
import frame.server.domain.UserPermission;

/**
 * 用户权限
 * 
 * @author Administrator
 *
 */
@Mapper
public interface UserPermissionDao {
	
	/**
	 * 批量添加用户角色
	 * 
	 * @param roles
	 * @return
	 */
	@Insert("<script>insert into user_permission (user_id, permission_id) values <foreach collection = 'list' item = 'item' separator = ','>(#{item.userId}, #{item.permissionId})</foreach></script>")
	Integer insertUserPermissions(List<UserPermission> userPermissions);
	
	/**
	 * 清空用户角色
	 * 
	 * @return
	 */
	@Delete("delete from user_permission")
	Integer clear();
	
	/**
	 * 根据用户ID删除用户权限对应信息
	 * 
	 * @param userId
	 * @return
	 */
	@Delete("delete from user_permission where user_id = #{userId}")
	Integer deleteByUserId(String userId);
	
	/**
	 * 获取全部的用户权限对应信息
	 * 
	 * @return
	 */
	@Select("select * from user_permission")
	List<UserPermission> selectAll();

	/**
	 * 根据用户ID查询用户所有的权限
	 * 
	 * @param userId
	 * @return
	 */
	@Select("select t1.* from permission as t1, user_permission as t2 where t2.user_id = #{userId} and t2.permission_id = t1.id")
	List<Permission> selectPermissionsByUserId(String userId);

}
