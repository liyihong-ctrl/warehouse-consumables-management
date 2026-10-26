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
import frame.server.domain.Type;
import frame.server.domain.TypeWarehouse;
import frame.server.dto.TypeDto;
import frame.server.dto.TypeDtoForWebCascader;
import frame.server.vo.consumables.CTypeController.SelectTypeByNameModel;

/**
 * 消耗品类型
 * 
 * @author Administrator
 *
 */
@Mapper
public interface TypeDao {

	/**
	 * 通用：批量添加消耗品类型
	 * 
	 * @param roles
	 * @return
	 */
	@Insert("<script>insert into type (id, name, remarks, parent_id, create_time, is_delete) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','>"
			+ "(#{item.id}, #{item.name}, #{item.remarks}, #{item.parentId}, #{item.createTime}, #{item.isDelete})</foreach></script>")
	Integer insertTypes(List<Type> types);

	/**
	 * 通用：添加消耗品类型
	 * 
	 * @param role
	 * @return
	 */
	@Insert("insert into type (#{type})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertType(Type type);

	/**
	 * 通用：清空消耗品类型表
	 * 
	 * @return
	 */
	@Delete("delete from type")
	Integer clear();
	
	/**
	 * 通用：删除类型
	 * 
	 * @param typeId
	 * @return
	 */
	@Update("update type set is_delete = !is_delete where (id = #{typeId} or parent_id = #{typeId}) and is_delete = 0")
	Integer delete(String typeId);
	
	/**
	 * 通用：更新类型
	 * 
	 * @param type
	 * @return
	 */
	@Update("update type set name = #{name}, remarks = #{remarks} where id = #{id} and is_delete = 0")
	Integer update(Type type);
	
	/**
	 * 消耗品管理系统：根据仓库ID查询消耗品类型(平级)
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select * from type as t1, type_warehouse as t2, warehouse as t3 "
			+ "where t3.id = #{warehouseId} and t3.is_enable = 1 and t3.is_delete = 0 and "
			+ "t3.id = t2.warehouse_id and t2.type_id = t1.id")
	List<Type> c_selectTypeByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID查询消耗品类型
	 * 
	 * 仅查询父类型，父类型嵌套子类型
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t3.* from type_warehouse as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.type_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.parent_id = '' and t3.is_delete = 0 "
			+ "order by create_time desc")
	@Results({ 
		@Result(property = "id", column = "id"),
		@Result(property = "children", column = "id", many = @Many(select = "frame.server.dao.TypeDao.c_selectTypeDtoByParentId")),
		@Result(property = "curTypeConsumableCount", column = "id", many = @Many(select = "frame.server.dao.ConsumableDao.c_selectCountByTypeId")) })
	List<TypeDto> c_selectTypeDtoByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID和类型名称模糊检索消耗品类型
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t3.* from type_warehouse as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.type_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and t3.name like '%${typeName}%' and "
			+ "t3.parent_id = '' and t3.is_delete = 0 "
			+ "order by create_time desc")
	@Results({ 
		@Result(property = "id", column = "id"),
		@Result(property = "children", column = "id", many = @Many(select = "frame.server.dao.TypeDao.c_selectTypeDtoByParentId")),
		@Result(property = "curTypeConsumableCount", column = "id", many = @Many(select = "frame.server.dao.ConsumableDao.c_selectCountByTypeId")) })
	List<TypeDto> c_selectTypeDtoByWarehouseIdAndLikeName(SelectTypeByNameModel model);

	/**
	 * 消耗品管理系统：根据仓库ID查询消耗品类型
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t3.id as value, t3.name as label, t3.create_time from type_warehouse as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.type_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.parent_id = '' and t3.is_delete = 0 "
			+ "order by create_time desc")
	@Results({ 
		@Result(property = "value", column = "value"),
		@Result(property = "children", column = "value", many = @Many(select = "frame.server.dao.TypeDao.c_selectTypeDtoForWebCascaderByParentId")) })
	List<TypeDtoForWebCascader> c_selectTypeDtoForWebCascaderByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据类型父类ID查询消耗品类型
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select * from type where parent_id = #{parentId} and is_delete = 0 order by create_time desc")
	@Results({ 
		@Result(property = "id", column = "id"),
		@Result(property = "children", column = "id", many = @Many(select = "frame.server.dao.TypeDao.c_selectTypeDtoByParentId")),
		@Result(property = "curTypeConsumableCount", column = "id", many = @Many(select = "frame.server.dao.ConsumableDao.c_selectCountByTypeId")) })
	List<TypeDto> c_selectTypeDtoByParentId(String parentId);
	
	/**
	 * 消耗品管理系统：根据类型父类ID查询消耗品类型
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select id as value, name as label from type where parent_id = #{parentId} and is_delete = 0 order by create_time desc")
	@Results({ 
		@Result(property = "value", column = "value"),
		@Result(property = "children", column = "value", many = @Many(select = "frame.server.dao.TypeDao.c_selectTypeDtoForWebCascaderByParentId")),
		@Result(property = "curTypeConsumableCount", column = "id", many = @Many(select = "frame.server.dao.ConsumableDao.c_selectCountByTypeId")) })
	List<TypeDtoForWebCascader> c_selectTypeDtoForWebCascaderByParentId(String parentId);

	/**
	 * 消耗品管理系统：根据仓库ID和类型名称检索记录数
	 * 
	 * @param model
	 * @return
	 */
	@Select("select count(t3.id) from type_warehouse as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and "
			+ "t1.warehouse_id = t2.id and t1.type_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.name = #{typeName} and t3.is_delete = 0")
	Integer c_selectCountByWarehouseIdAndName(SelectTypeByNameModel model);
	
	/**
	 * 消耗品管理系统：根据父类ID检索子类ID
	 * 
	 * @param typeId
	 * @return
	 */
	@Select("select t3.id from type_warehouse as t1, warehouse as t2, type as t3 "
			+ "where t1.type_id = #{typeId} and t1.warehouse_id = t2.id and t1.type_id = t3.parent_id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0")
	List<String> c_selectChildrenTypeIdByParentId(String typeId);

	/**
	 * 消耗品管理系统：根据类型ID和仓库ID查询记录数
	 * 
	 * @param typeWarehouse
	 * @return
	 */
	@Select("select t3.* from type_warehouse as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.type_id = #{typeId} and "
			+ "t1.warehouse_id = t2.id and t1.type_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0")
	Type c_selectTypeByWarehouseIdAndTypeId(TypeWarehouse typeWarehouse);

}
