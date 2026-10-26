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
import frame.server.domain.Warehouse;
import frame.server.dto.WarehouseDto;
import frame.server.dto.WarehouseUpdateDto;

/**
 * 仓库
 * 
 * @author Administrator
 *
 */
@Mapper
public interface WarehouseDao {
	
	/**
	 * 添加仓库
	 * 
	 * @param warehouse
	 * @return
	 */
	@Insert("insert into warehouse (#{warehouse})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertWarehouse(Warehouse warehouse);
	
	/**
	 * 清空仓库表
	 * 
	 * @return
	 */
	@Delete("delete from warehouse")
	Integer clear();
	
	/**
	 * 根据仓库ID删除仓库(真删除)
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Delete("delete from warehouse where is_delete = 0 and id = #{warehouseId}")
	Integer deleteById(String warehouseId);

	/**
	 * 更新仓库信息
	 * 
	 * @param warehouse
	 * @return
	 */
	@Update("update warehouse (#{warehouse}) where is_delete = 0 and id = #{id}")
	@Lang(SimpleUpdateExtendedLanguageDriver.class)
	Integer updateWarehouse(WarehouseUpdateDto warehouse);
	
	/**
	 * 更新仓库状态
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Update("update warehouse set is_enable = !is_enable where is_delete = 0 and id = #{warehouseId}")
	Integer updateWarehouseState(String warehouseId);
	
	/**
	 * 更新仓库删除状态
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Update("update warehouse set is_delete = !is_delete where is_delete = 0 and id = #{warehouseId}")
	Integer updateWarehouseDeleteState(String warehouseId);
	
	/**
	 * 通用：获取仓库总数
	 * 
	 * @return
	 */
	@Select("select count(*) from warehouse where is_delete = 0")
	Integer selectWarehousesCount();
	
	/**
	 * 通用：获取已启用仓库总数
	 * 
	 * @return
	 */
	@Select("select count(*) from warehouse where is_enable = 1 and is_delete = 0")
	Integer selectEnabledWarehousesCount();
	
	/**
	 * 通用：获取所有仓库的ID
	 * 
	 * @return
	 */
	@Select("select id from warehouse where is_delete = 0")
	List<String> selectAllId();
	
	/**
	 * 通用：获取所有仓库
	 * 
	 * @return
	 */
	@Select("select * from warehouse where is_delete = 0 order by create_time desc")
	List<Warehouse> selectAll();
	
	/**
	 * 通用：获取所有已启用的仓库
	 * 
	 * @return
	 */
	@Select("select * from warehouse where is_delete = 0 and is_enable = 1 order by create_time desc")
	List<Warehouse> selectEnabledAll();
	
	/**
	 * 仓库管理系统：
	 * 获取全部仓库信息
	 * 
	 * @param warehouseName
	 * @return
	 */
	@Select("select * from warehouse where is_delete = 0 order by create_time desc")
	@Results({
		@Result(property = "id", column = "id"),
		@Result(property = "users", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectUsersByWarehouseId2"))
	})
	List<WarehouseDto> selectAll1();
	
	/**
	 * 仓库管理系统：
	 * 获取全部仓库信息
	 * 根据仓库名称模糊减速
	 * 
	 * @param warehouseName
	 * @return
	 */
	@Select("select * from warehouse where is_delete = 0 and name like '%${warehouseName}%' order by create_time desc")
	@Results({
		@Result(property = "id", column = "id"),
		@Result(property = "users", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectUsersByWarehouseId2"))
	})
	List<WarehouseDto> selectLikeName(String warehouseName);
	
	/**
	 * 仓库管理系统：
	 * 根据仓库ID查询仓库信息
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select * from warehouse where is_delete = 0 and id = #{warehouseId}")
	Warehouse selectWarehouseById(String warehouseId);
	
	/**
	 * 仓库管理系统：
	 * 根据仓库ID查询仓库信息
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select * from warehouse where is_delete = 0 and id = #{warehouseId}")
	@Results({
		@Result(property = "id", column = "id"),
		@Result(property = "users", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectSystemManagerAndWarehouseManagerByWarehouseId"))
	})
	WarehouseDto selectWarehouseById1(String warehouseId);
	
	/**
	 * 仓库管理系统：
	 * 根据仓库名称查询仓库数量
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select count(*) from warehouse where is_delete = 0 and name = #{warehouseName}")
	Integer selectWarehouseCountByName(String warehouseName);
	
	/**
	 * 仓库管理系统：
	 * 根据仓库Code查询仓库数量
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select count(*) from warehouse where is_delete = 0 and code = #{warehouseCode}")
	Integer selectWarehouseCountByCode(String warehouseCode);
	
	/**
	 * 仓库管理系统：
	 * 
	 * 获取最大的排序数
	 * 
	 * @return
	 */
	@Select("select max(display_order) from warehouse where is_delete = 0")
	Integer selectMaxDisplayOrder();

	/**
	 * 消耗品申请系统：获取用户可申请仓库
	 * 
	 * @return
	 */
	@Select("select t2.* from user_warehouse_application_setting as t1, warehouse as t2 "
			+ "where t1.user_id = #{userId} and t1.warehouse_id = t2.id and t2.is_enable = 1 and t2.is_delete = 0 order by create_time desc")
	List<Warehouse> a_selectCanApplicationWarehouses(String userId);
	
	/**
	 * 消耗品申请系统：根据仓库ID查询仓库信息
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select count(id) from warehouse where id = #{warehouseId} and is_enable = 1 and is_delete = 0")
	Integer a_selectWarehouseById(String warehouseId);
	
	/**
	 * 消耗品申请系统：根据仓库ID查询仓库名称
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select name from warehouse where id = #{warehouseId} and is_enable = 1 and is_delete = 0")
	String a_selectWarehouseByName(String warehouseId);

}
