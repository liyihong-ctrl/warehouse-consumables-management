package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.config.mybatis.SimpleUpdateExtendedLanguageDriver;
import frame.server.domain.Role;
import frame.server.dto.RoleDto;

/**
 * 角色
 * 
 * @author Administrator
 *
 */
@Mapper
public interface RoleDao {
	
	/**
	 * 批量添加角色
	 * 
	 * @param roles
	 * @return
	 */
	@Insert("<script>insert into role (id, name, str, type, remarks, create_time, is_stop, is_delete) values <foreach collection = 'list' item = 'item' separator = ','>(#{item.id}, #{item.name}, #{item.str}, #{item.type}, #{item.remarks}, #{item.createTime}, #{item.isStop}, #{item.isDelete})</foreach></script>")
	Integer insertRoles(List<Role> roles);
	
	/**
	 * 添加角色
	 * 
	 * @param role
	 * @return
	 */
	@Insert("insert into role (#{role})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertRole(Role role);
	
	/**
	 * 清空角色表
	 * 
	 * @return
	 */
	@Delete("delete from role")
	Integer clear();
	
	/**
	 * 根据角色ID删除指定角色
	 * 
	 * @param roleId
	 * @return
	 */
	@Delete("delete from role where id = #{roleId}")
	Integer deleteByRoleId(String roleId);
	
	/**
	 * 更新角色
	 * 
	 * @param role
	 * @return
	 */
	@Update("update role (#{role}) where id =#{id}")
	@Lang(SimpleUpdateExtendedLanguageDriver.class)
	Integer update(Role role);
	
	/**
	 * 假删除角色，仅更新isDelete状态
	 * 
	 * @param roleId
	 * @return
	 */
	@Delete("update role set is_delete = 1 where id = #{roleId}")
	Integer updateRoleDeleteState(String roleId);
	
	/**
	 * 查询角色数
	 * 
	 * @return
	 */
	@Select("select count(*) from role where is_delete = 0")
	Integer selectRolesCount();
	
	/**
	 * 获取所有角色信息(不包含权限数据)
	 * 
	 * @return
	 */
	@Select("select * from role where is_delete = 0 order by create_time desc")
	List<Role> selectAll();
	
	/**
	 * 获取所有角色信息(包含权限数据)
	 * 
	 * @return
	 */
	@Select("select * from role where is_delete = 0 order by create_time desc")
	@Results({
		@Result(property = "id", column = "id"),
		@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.RolePermissionDao.selectPermissionsByRoleId"))
	})
	List<RoleDto> selectAll1();
	
	/**
	 * 根据角色ID查询角色信息
	 * 
	 * @param roleId
	 * @return
	 */
	@Select("select * from role where id = #{roleId} and is_delete = 0")
	Role selectRoleByRoleId(String roleId);
	
	/**
	 * 根据角色ID查询角色信息
	 * 
	 * @param roleId
	 * @return
	 */
	@Select("select * from role where id = #{roleId} and is_delete = 0")
	@Results({
		@Result(property = "id", column = "id"),
		@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.RolePermissionDao.selectPermissionsByRoleId"))
	})
	RoleDto selectRoleDtoByRoleId(String roleId);
	
	/**
	 * 根据角色名称查询角色信息
	 * 
	 * @param roleName
	 * @return
	 */
	@Select("select * from role where name = #{roleName} and is_delete = 0")
	Role selectByRoleName(String roleName);
	
	/**
	 * 根据角色标识查询角色信息
	 * 
	 * @param roleStr
	 * @return
	 */
	@Select("select * from role where str = #{roleStr} and is_delete = 0")
	Role selectByRoleStr(String roleStr);

}
