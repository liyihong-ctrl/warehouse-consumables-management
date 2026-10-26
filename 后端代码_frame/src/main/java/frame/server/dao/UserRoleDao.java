package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import frame.server.domain.UserRole;
import frame.server.dto.RoleDto;

/**
 * 用户角色
 * 
 * @author Administrator
 *
 */
@Mapper
public interface UserRoleDao {
	
	/**
	 * 批量添加用户角色
	 * 
	 * @param roles
	 * @return
	 */
	@Insert("<script>insert into user_role (user_id, role_id) values <foreach collection = 'list' item = 'item' separator = ','>(#{item.userId}, #{item.roleId})</foreach></script>")
	Integer insertUserRoles(List<UserRole> userRoles);
	
	/**
	 * 清空用户角色
	 * 
	 * @return
	 */
	@Delete("delete from user_role")
	Integer clear();
	
	/**
	 * 根据用户ID删除用户角色对应信息
	 * 
	 * @param userId
	 * @return
	 */
	@Delete("delete from user_role where user_id = #{userId}")
	Integer deleteByUserId(String userId);
	
	/**
	 * 根据角色ID删除用户角色对应信息
	 * 
	 * @param roleId
	 * @return
	 */
	@Delete("delete from user_role where role_id = #{roleId}")
	Integer deleteByRoleId(String roleId);
	
	/**
	 * 获取全部的用户角色对应信息
	 * 
	 * @return
	 */
	@Select("select * from user_role")
	List<UserRole> selectAll();
	
	/**
	 * 根据用户ID获取用户全部角色信息
	 * 
	 * @param userId
	 * @return
	 */
	@Select("select t1.* from role as t1, user_role as t2 where t2.user_id = #{userId} and t2.role_id = t1.id")
	@Results({
		@Result(property = "id", column = "id"),
		@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.PermissionDao.selectPermissionByPermissionId"))
	})
	List<RoleDto> selectRoleDtosByUserId(String userId);
	
	/**
	 * 消耗品管理系统：根据角色ID查询用户数
	 * 
	 * @param roleId
	 * @return
	 */
	@Select("select count(t2.id) from user_role as t1, user as t2 where "
			+ "t1.role_id = #{roleId} and t1.user_id = t2.id and "
			+ "t2.is_delete = 0")
	Integer c_selectUserCountByRoleId(String roleId);
	
	/**
	 * 消耗品管理系统：根据角色ID查询已启用用户ID
	 * 
	 * @param roleId
	 * @return
	 */
	@Select("select t2.id from user_role as t1, user as t2 where "
			+ "t1.role_id = #{roleId} and t1.user_id = t2.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0")
	List<String> c_selectUserIdsByRoleId(String roleId);

}
