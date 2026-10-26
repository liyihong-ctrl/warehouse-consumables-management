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

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.domain.Role;
import frame.server.domain.RoleWarehouse;
import frame.server.domain.Warehouse;
import frame.server.dto.RoleDto;
import frame.server.vo.consumables.CRoleController.GetRolesSelectModel;

@Mapper
public interface RoleWarehouseDao {

	/**
	 * 添加角色权限关联关系
	 * 
	 * @param user
	 * @return
	 */
	@Insert("insert into role_warehouse (#{roleWarehouse})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertRoleWarehouse(RoleWarehouse roleWarehouse);

	/**
	 * 根据角色ID删除角色仓库关联关系
	 * 
	 * @param roleId
	 * @return
	 */
	@Delete("delete from role_warehouse where role_id = #{roleId}")
	Integer deleteByRoleId(String roleId);

	/**
	 * 根据仓库ID删除角色仓库关联关系
	 * 
	 * @param roleId
	 * @return
	 */
	@Delete("delete from role_warehouse where warehouse_id = #{warehouseId}")
	Integer deleteByWarehouseId(String warehouseId);

	/**
	 * 根据仓库ID和角色ID查询关联关系数
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select count(t1.*) from role_warehouse as t1, warehouse as t2 where t1.warehouse_id = #{warehouseId} and t1.role_id = #{roleId} and t1.warehouse_id = t2.id and t2.is_enable = 1 and t2.is_delete = 0")
	Integer selectCountByWarehouseIdAndRoleId(RoleWarehouse roleWarehouse);

	/**
	 * 根据仓库ID和角色ID查询仓库数据
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t2.* from role_warehouse as t1, warehouse as t2 where t1.warehouse_id = #{warehouseId} and t1.role_id = #{roleId} and t1.warehouse_id = t2.id and t2.is_enable = 1 and t2.is_delete = 0")
	Warehouse selectWarehouseByWarehouseIdAndRoleId(RoleWarehouse roleWarehouse);

	/**
	 * 消耗品管理系统：根据仓库ID查询角色总数
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select count(t3.id) from role_warehouse as t1, warehouse as t2, role as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.role_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.type = 4 and t3.is_delete = 0")
	Integer c_selectRoleCountByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID查询全部角色ID
	 * 
	 * 不包含仓库管理员
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t3.id from role_warehouse as t1, warehouse as t2, role as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.role_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.type = 4 and t3.is_delete = 0")
	List<String> c_selectRoleIdsByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID查询全部角色数据
	 * 
	 * 不包含仓库管理员
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t3.* from role_warehouse as t1, warehouse as t2, role as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.role_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.type = 4 and t3.is_delete = 0 "
			+ "order by create_time desc")
	List<Role> c_selectRolesByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID查询全部角色名称数据
	 * 
	 * 不包含仓库管理员
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t3.name from role_warehouse as t1, warehouse as t2, role as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.role_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.type = 4 and t3.is_delete = 0")
	List<String> c_selectRoleNamesByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID查询全部角色标识数据
	 * 
	 * 不包含仓库管理员
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t3.str from role_warehouse as t1, warehouse as t2, role as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.role_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.type = 4 and t3.is_delete = 0")
	List<String> c_selectRoleStrsByWarehouseId(String warehouseId);
	
	/**
	 * 消耗品管理系统：根据仓库ID查询全部角色数据
	 * 
	 * 不包含仓库管理员
	 * 
	 * @return
	 */
	@Select("select t3.* from role_warehouse as t1, warehouse as t2, role as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.role_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.type = 4 and t3.is_delete = 0 "
			+ "order by create_time desc")
	@Results({
		@Result(property = "id", column = "id"),
		@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.RolePermissionDao.selectPermissionsByRoleId"))
	})
	List<RoleDto> c_selectRoleDtosByWarehouseId(String warehouseId);
	
	/**
	 * 消耗品管理系统：根据仓库ID和角色名称模糊查询角色数据
	 * 
	 * 不包含仓库管理员
	 * 
	 * @return
	 */
	@Select("select t3.* from role_warehouse as t1, warehouse as t2, role as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.role_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.type = 4 and t3.name like '%${roleName}%' and t3.is_delete = 0 "
			+ "order by create_time desc")
	@Results({
		@Result(property = "id", column = "id"),
		@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.RolePermissionDao.selectPermissionsByRoleId"))
	})
	List<RoleDto> c_selectRoleDtoByWarehouseIdAndLikeRoleName(GetRolesSelectModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和角色ID查询角色数据
	 * 
	 * @param roleWarehouse
	 * @return
	 */
	@Select("select t3.* from role_warehouse as t1, warehouse as t2, role as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.role_id = #{roleId} and t1.warehouse_id = t2.id and t1.role_id = t3.id and "
			+ "      t2.is_enable = 1 and t2.is_delete = 0")
	Role c_selectRoleByWarehouseIdAndRoleId(RoleWarehouse roleWarehouse);

}
