package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import frame.server.domain.Permission;

/**
 * 权限
 * 
 * @author Administrator
 *
 */
@Mapper
public interface PermissionDao {

	/**
	 * 批量添加权限信息
	 * 
	 * @param permissions
	 * @return
	 */
	@Insert("<script>insert into permission (id, name, str, display_order) values <foreach collection = 'list' item = 'item' separator = ','>(#{item.id}, #{item.name}, #{item.str}, #{item.displayOrder})</foreach></script>")
	Integer insertPermissions(List<Permission> permissions);
	
	/**
	 * 清空权限表
	 * 
	 * @return
	 */
	@Delete("delete from permission")
	Integer clear();
	
	/**
	 * 获取所有权限信息
	 * 
	 * @return
	 */
	@Select("select * from permission order by display_order")
	List<Permission> selectAll();
	
	/**
	 * 根据权限ID获取权限信息
	 * 
	 * @return
	 */
	@Select("select * from permission where id = #{permissionId}")
	Permission selectPermissionByPermissionId(String permissionId);
	
	/**
	 * 根据权限名称获取权限信息
	 * 
	 * @return
	 */
	@Select("select * from permission where name = #{permissionName}")
	Permission selectPermissionByPermissionName(String permissionName);
	
	/**
	 * 根据权限标识获取权限信息
	 * 
	 * @return
	 */
	@Select("select * from permission where str = #{permissionStr}")
	Permission selectPermissionByPermissionStr(String permissionStr);
	
}
