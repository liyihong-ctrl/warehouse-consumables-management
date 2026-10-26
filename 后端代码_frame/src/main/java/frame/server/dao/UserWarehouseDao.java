package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.domain.User;
import frame.server.domain.UserWarehouse;
import frame.server.domain.Warehouse;
import frame.server.dto.UserInfoDto;

/**
 * 用户仓库关联关系
 * 
 * @author Administrator
 *
 */
@Mapper
public interface UserWarehouseDao {

	/**
	 * 批量添加用户仓库关联关系
	 * 
	 * @param userWarehouses
	 * @return
	 */
	@Insert("<script>insert into user_warehouse (user_id, warehouse_id, create_time) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "(#{item.userId}, #{item.warehouseId}, #{item.createTime})</foreach></script>")
	Integer insertUserWarehouses(List<UserWarehouse> userWarehouses);

	/**
	 * 添加用户仓库关联关系
	 * 
	 * @param userWarehouse
	 * @return
	 */
	@Insert("insert into user_warehouse (#{userWarehouse})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertUserWarehouse(UserWarehouse userWarehouse);

	/**
	 * 清空用户仓库关联关系表
	 * 
	 * @return
	 */
	@Delete("delete from user_warehouse")
	Integer clear();

	/**
	 * 根据用户ID删除用户仓库关联关系
	 * 
	 * @param userId
	 * @return
	 */
	@Delete("delete from user_warehouse where user_id = #{userId}")
	Integer deleteByUserId(String userId);

	/**
	 * 根据仓库ID删除关联关系
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Delete("delete from user_warehouse where warehouse_id = #{warehouseId}")
	Integer deleteByWarehouseId(String warehouseId);

	/**
	 * 根据仓库ID删除与仓库管理员的关联关系
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Delete("delete t1 from user_warehouse as t1, user as t2 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.user_id = t2.id and t2.type = '2'")
	Integer deleteByWarehouseIdAndUserTypeIsWarehouseManager(String warehouseId);

	/**
	 * 根据用户ID和仓库ID删除关联关系
	 * 
	 * @param userWarehouse
	 * @return
	 */
	@Delete("delete from user_warehouse where user_id = #{userId} and warehouse_id = #{warehouseId}")
	Integer deleteByUserIdAndWarehouseId(UserWarehouse userWarehouse);

	/**
	 * 根据用户ID查询全部仓库数
	 * 
	 * @param userId
	 */
	@Select("select count(t2.id) from user_warehouse as t1, warehouse as t2 "
			+ "where t1.user_id = #{userId} and t1.warehouse_id = t2.id and t2.is_delete = 0")
	int selectWarehousesCountByUserId(String userId);

	/**
	 * 根据用户ID查询已启用仓库数
	 * 
	 * @param userId
	 */
	@Select("select count(t2.id) from user_warehouse as t1, warehouse as t2 "
			+ "where t1.user_id = #{userId} and t1.warehouse_id = t2.id and t2.is_enable = 1 and t2.is_delete = 0")
	int selectEnabledWarehousesCountByUserId(String userId);

	/**
	 * 根据用户ID查询全部仓库ID
	 * 
	 * @param userId
	 */
	@Select("select t1.warehouse_id from user_warehouse as t1, warehouse as t2 "
			+ "where t1.user_id = #{userId} and t1.warehouse_id = t2.id and t2.is_delete = 0")
	List<String> selectWarehouseIdsCountByUserId(String userId);

	/**
	 * 根据用户ID查询已启用仓库ID
	 * 
	 * @param userId
	 */
	@Select("select t1.warehouse_id from user_warehouse as t1, warehouse as t2 "
			+ "where t1.user_id = #{userId} and t1.warehouse_id = t2.id t2.is_enable = 1 and and t2.is_delete = 0")
	List<String> selectEnabledWarehouseIdsCountByUserId(String userId);

	/**
	 * 根据用户ID查询全部仓库
	 * 
	 * @param userId
	 * @return
	 */
	@Select("select t2.* from user_warehouse as t1, warehouse as t2 "
			+ "where user_id = #{userId} and t1.warehouse_id = t2.id and t2.is_delete = 0")
	List<Warehouse> selectWarehousesByUserId(String userId);

	/**
	 * 根据用户ID查询已启用仓库
	 * 
	 * @param userId
	 * @return
	 */
	@Select("select t2.* from user_warehouse as t1, warehouse as t2 "
			+ "where t1.user_id = #{userId} and t1.warehouse_id = t2.id and t2.is_enable = 1 and t2.is_delete = 0")
	List<Warehouse> selectEnabledWarehousesByUserId(String userId);

	/**
	 * 根据仓库ID查询用户数
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select count(t3.id) from user_warehouse as t1, warehouse as t2, user as t3 where "
			+ "t1.warehouse_id = #{warehouseId} and t1.user_id = t3.id and "
			+ "t1.warehouse_id = t2.id and t3.is_delete = 0")
	int selectUsersCountByWarehouseId(String warehouseId);

	/**
	 * 根据仓库ID查询全部用户信息
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t3.* from user_warehouse as t1, warehouse as t2, user as t3 where "
			+ "t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.user_id = t3.id and t3.is_delete = 0")
	List<User> selectUsersByWarehouseId(String warehouseId);

	/**
	 * 根据仓库ID查询全部用户信息
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t3.* from user_warehouse as t1, warehouse as t2, user as t3 where "
			+ "t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.user_id = t3.id and t3.is_delete = 0")
	List<UserInfoDto> selectUsersByWarehouseId2(String warehouseId);

	/**
	 * 根据仓库ID查询系统管理员和仓库管理员信息
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t3.* from user_warehouse as t1, warehouse as t2, user as t3 where "
			+ "t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.user_id = t3.id and "
			+ "(t3.type = '0' or t3.type = '1' or t3.type = '2') and t3.is_delete = 0")
	List<UserInfoDto> selectSystemManagerAndWarehouseManagerByWarehouseId(String warehouseId);

	/**
	 * 根据用户ID和仓库ID查询关联关系数：仓库启用条件下
	 * 
	 * @return
	 */
	@Select("select count(t1.user_id) from user_warehouse as t1, warehouse as t2 "
			+ "where t1.user_id = #{userId} and t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t2.is_enable = 1 and t2.is_delete = 0")
	Integer selectEnabledCountByUserIdAndWarehouseId(UserWarehouse userWarehouse);

	/**
	 * 根据用户ID和仓库ID查询仓库信息：仓库启用条件下
	 * 
	 * @return
	 */
	@Select("select t2.* from user_warehouse as t1, warehouse as t2, user as t3 "
			+ "where t1.user_id = #{userId} and t1.warehouse_id = #{warehouseId} and "
			+ "t1.warehouse_id = t2.id and t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t1.user_id = t3.id and t3.is_enable = 1 and t3.is_delete = 0")
	Warehouse selectEnabledWarehouseByUserIdAndWarehouseId(UserWarehouse userWarehouse);



	/**
	 * 消耗品管理系统：根据仓库ID查询用户数
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select count(t3.id) from user_warehouse as t1, warehouse as t2, user as t3 where "
			+ "t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.user_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and (t3.type = '2' or t3.type = '4') and t3.is_delete = 0")
	int c_selectUsersCountByWarehouseId(String warehouseId);
}
