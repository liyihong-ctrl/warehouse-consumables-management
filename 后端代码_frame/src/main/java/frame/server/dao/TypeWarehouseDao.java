package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.domain.TypeWarehouse;

/**
 * 消耗品类型
 * 
 * @author Administrator
 *
 */
@Mapper
public interface TypeWarehouseDao {

	/**
	 * 消耗品管理系统：批量添加消耗品类型和仓库关联关系
	 * 
	 * @param typeWarehouses
	 * @return
	 */
	@Insert("<script>insert into type_warehouse (type_id, warehouse_id) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "    (#{item.typeId}, #{item.warehouseId}) "
			+ "</foreach></script>")
	Integer insertTypeWarehouses(List<TypeWarehouse> typeWarehouses);
	
	/**
	 * 消耗品管理系统：添加消耗品类型和仓库关联关系
	 * 
	 * @param typeWarehouse
	 * @return
	 */
	@Insert("insert into type_warehouse (#{typeWarehouse})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertTypeWarehouse(TypeWarehouse typeWarehouse);
	
	/**
	 * 消耗品管理系统：根据仓库ID查询类型总数
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select count(t3.id) from type_warehouse as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.type_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0")
	Integer selectCountByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据类型ID和仓库ID查询记录数
	 * 
	 * @param typeWarehouse
	 * @return
	 */
	@Select("select count(t3.id) from type_warehouse as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.type_id = #{typeId} and "
			+ "t1.warehouse_id = t2.id and t1.type_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0")
	Integer c_selectCountByTypeIdAndWarehouseId(TypeWarehouse typeWarehouse);
	
}
