package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.config.mybatis.SimpleUpdateExtendedLanguageDriver;
import frame.server.domain.Supplier;

@Mapper
public interface SupplierDao {

	// 插入一条新的供应商记录
	@Insert("insert into supplier (#{supplier})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertSupplier(Supplier supplier);

	// 根据ID和仓库ID修改供应商信息
	@Update("update supplier (#{supplier}) where id = #{id} and warehouse_id = #{warehouseId} and is_delete = 0")
	@Lang(SimpleUpdateExtendedLanguageDriver.class)
	Integer updateSupplier(Supplier supplier);

	// 根据ID删除供应商信息，软删除，不物理删除数据库数据
	@Delete("update supplier set is_delete = 1 where id = #{supplierId} and warehouse_id = #{warehouseId} and is_delete = 0")
	Integer deleteSupplier(@Param("warehouseId") String warehouseId, @Param("supplierId") String supplierId);

	// 查询指定仓库下的供应商信息
	@Select("select * from supplier where warehouse_id = #{warehouseId} and is_delete = 0 order by create_time desc")
	List<Supplier> selectByWarehouseId(String warehouseId);

	// 根据供应商名称模糊查询，用于列表搜索框
	@Select("select * from supplier where warehouse_id = #{warehouseId} and name like concat('%', #{name}, '%') and is_delete = 0 order by create_time desc")
	List<Supplier> selectSupplierAndLikeName(@Param("warehouseId") String warehouseId, @Param("name") String name);

	// 根据仓库ID和供应商ID查一条供应商，用于修改前校验和详情查询
	@Select("select * from supplier where warehouse_id = #{warehouseId} and id = #{supplierId} and is_delete = 0")
	Supplier selectByWarehouseIdAndSupplierId(@Param("warehouseId") String warehouseId,
			@Param("supplierId") String supplierId);

	// 判断供应商是否存在，用于删除、修改之前确认供应商状态
	@Select("select count(id) from supplier where warehouse_id = #{warehouseId} and id = #{supplierId} and is_delete = 0")
	Integer selectCountByWarehouseIdAndSupplierId(@Param("warehouseId") String warehouseId,
			@Param("supplierId") String supplierId);

	// 判断同一仓库下供应商名称是否重复
	@Select("select count(id) from supplier where warehouse_id = #{warehouseId} and name = #{name} and is_delete = 0")
	Integer selectCountByWarehouseIdAndName(@Param("warehouseId") String warehouseId, @Param("name") String name);
}
