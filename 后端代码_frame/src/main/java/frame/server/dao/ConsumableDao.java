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
import frame.server.domain.Consumable;
import frame.server.domain.TypeWarehouse;
import frame.server.dto.ConsumableDto;
import frame.server.dto.ConsumableInfoDto;
import frame.server.vo.consumables.CSearchConditionModel;

/**
 * 消耗品
 * 
 * @author Administrator
 *
 */
@Mapper
public interface ConsumableDao {

	/**
	 * 批量添加消耗品
	 * 
	 * @param consumables
	 * @return
	 */
	@Insert("<script>insert into consumable "
			+ "(id, warehouse_id, type_id, code, name, count, safety_count, price, summary, state, create_time, is_lock, is_delete,supplier_id) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "(#{item.id}, #{item.warehouseId}, #{item.typeId}, #{item.code}, #{item.name}, #{item.count}, #{item.safetyCount}, #{item.price}, #{item.summary}, #{item.state}, #{item.createTime}, #{item.isLock}, #{item.isDelete},#{item.supplierId}) "
			+ "</foreach> " + "</script>")
	Integer insertConsumables(List<Consumable> consumables);

	/**
	 * 添加消耗品
	 * 
	 * @param user
	 * @return
	 */
	@Insert("insert into consumable (#{consumable})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertConsumable(Consumable consumable);

	/**
	 * 清空消耗品表
	 * 
	 * @return
	 */
	@Delete("delete from consumable")
	Integer clear();

	/**
	 * 根据仓库ID删除消耗品
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Delete("update consumable set is_delete = 1 where warehouse_id = #{warehouseId} and is_delete = 0")
	Integer deleteByWarehouseId(String warehouseId);

	/**
	 * 根据类型ID删除消耗品
	 * 
	 * @param typeId
	 * @return
	 */
	@Delete("update consumable set is_delete = 1 where type_id = #{typeId} and is_delete = 0")
	Integer deleteBytypeId(String typeId);

	/**
	 * 根据消耗品ID删除消耗品
	 * 
	 * @param consumableId
	 * @return
	 */
	@Delete("update consumable set is_delete = 1 where warehouse_id = #{warehouseId} and id = #{id} and is_delete = 0")
	Integer deleteByConsumableId(ConsumableInfoDto consumable);

	/**
	 * 更新消耗品信息
	 * 
	 * @param consumable
	 * @return
	 */
	@Update("update consumable (#{consumable}) where warehouse_id = #{warehouseId} and id = #{id} and is_delete = 0")
	@Lang(SimpleUpdateExtendedLanguageDriver.class)
	Integer update(ConsumableInfoDto consumable);

	/**
	 * 通用：出库
	 * 
	 * @param consumable
	 * @return
	 */
	@Update("update consumable set count = count - #{count} "
			+ "where warehouse_id = #{warehouseId} and id = #{id} and is_delete = 0")
	Integer outbound(ConsumableInfoDto consumable);

	/**
	 * 通用：入库
	 * 
	 * 更新数量和价格
	 * 
	 * @param consumable
	 * @return
	 */
	@Update("update consumable set count = count + #{count}, price = #{price}"
			+ "where warehouse_id = #{warehouseId} and id = #{id} and is_delete = 0")
	Integer inbound(ConsumableInfoDto consumable);

	/**
	 * 更新消耗品安全在工数量
	 * 
	 * @param consumable
	 * @return
	 */
	@Update("update consumable set safety_count = #{safetyCount} where warehouse_id = #{warehouseId} and id = #{id} and is_delete = 0")
	Integer updateConsumableSafetyCount(ConsumableInfoDto consumable);

	/**
	 * 更新消耗品锁定状态
	 * 
	 * @param consumableId
	 * @return
	 */
	@Update("update consumable set is_lock = !is_lock where id = #{consumableId} and is_delete = 0")
	Integer updateConsumableLockState(String consumableId);

	/**
	 * 消耗品管理系统：根据仓库ID获取消耗品总数
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select count(t1.id) from consumable as t1, warehouse as t2 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t2.is_enable = 1 and t2.is_delete = 0")
	Integer c_selectCountByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品ID获取消耗品数量
	 * 
	 * @param consumable
	 * @return
	 */
	@Select("select t1.count from consumable as t1, warehouse as t2 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.id = #{id}and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t2.is_enable = 1 and t2.is_delete = 0")
	Integer c_selectConsumableCountByWarehouseIdAndConsumableId(Consumable consumable);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品Code获取消耗品记录数
	 * 
	 * @param consumable
	 * @return
	 */
	@Select("select count(t1.id) from consumable as t1, warehouse as t2 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.code = #{code}and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t2.is_enable = 1 and t2.is_delete = 0")
	Integer c_selectConsumableCountByWarehouseIdAndConsumableCode(Consumable consumable);

	/**
	 * 消耗品管理系统：根据仓库ID获取消耗品
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t1.type_id = t3.id " + "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID获取消耗品
	 * 
	 * 查看报警信息
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t1.count <= t1.safety_count and "
			+ "t1.warehouse_id = t2.id and t1.type_id = t3.id " + "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectAlarmByWarehouseId(String warehouseId);

	/**
	 * 根据仓库ID和类型ID获取消耗品
	 * 
	 * @param typeWarehouse
	 * @return
	 */
	@Select("select * from consumable where warehouse_id = #{warehouseId} and type_id = #{typeId} and is_delete = 0")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> selectByWarehouseIdAndTypeId(TypeWarehouse typeWarehouse);

	/**
	 * 根据仓库ID和消耗品ID获取消耗品记录数
	 * 
	 * @param typeWarehouse
	 * @return
	 */
	@Select("select count(*) from consumable where warehouse_id = #{warehouseId} and id = #{id} and is_delete = 0")
	Integer selectCountByWarehouseIdAndConsumableId(ConsumableInfoDto consumable);

	/**
	 * 根据仓库ID和消耗品ID获取消耗品
	 * 
	 * @param typeWarehouse
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.id = #{id} and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t1.type_id = t3.id")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	ConsumableDto selectByWarehouseIdAndConsumableId(ConsumableInfoDto consumable);

	/**
	 * 消耗品管理系统：根据类型ID查询消耗品数
	 * 
	 * @param typeId
	 * @return
	 */
	@Select("select count(t1.id) from consumable as t1, type as t2 "
			+ "where t1.type_id = #{typeId} and t1.is_delete = 0 and " + "t2.is_delete = 0")
	Integer c_selectConsumableCountByTypeId(String typeId);

	/**
	 * 消耗品管理系统：根据仓库ID和日期范围获取消耗品
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.warehouse_id = t2.id and " + "t1.type_id = t3.id " + "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectByWarehouseIdAndInTime(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和日期范围获取消耗品
	 * 
	 * 查看报警数据
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.count <= t1.safety_count and " + "t1.warehouse_id = t2.id and " + "t1.type_id = t3.id "
			+ "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectAlarmDataByWarehouseIdAndInTime(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品名获取消耗品
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t1.name like '%${searchStr}%' and "
			+ "t1.warehouse_id = t2.id and " + "t1.type_id = t3.id " + "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectByWarehouseIdAndLikeName(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品名获取消耗品
	 * 
	 * 查看报警数据
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t1.name like '%${searchStr}%' and "
			+ "t1.count <= t1.safety_count and " + "t1.warehouse_id = t2.id and " + "t1.type_id = t3.id "
			+ "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectAlarmDataByWarehouseIdAndLikeName(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品名和日期范围获取消耗品
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t1.name like '%${searchStr}%' and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.warehouse_id = t2.id and " + "t1.type_id = t3.id " + "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectByWarehouseIdAndLikeNameAndInTime(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品名和日期范围获取消耗品
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t1.name like '%${searchStr}%' and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.count <= t1.safety_count and " + "t1.warehouse_id = t2.id and " + "t1.type_id = t3.id "
			+ "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectAlarmDataByWarehouseIdAndLikeNameAndInTime(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品code获取消耗品
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t1.code = #{searchStr} and "
			+ "t1.warehouse_id = t2.id and " + "t1.type_id = t3.id " + "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectByWarehouseIdAndCode(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品code获取消耗品
	 * 
	 * 查看报警数据
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t1.code = #{searchStr} and "
			+ "t1.count <= t1.safety_count and " + "t1.warehouse_id = t2.id and " + "t1.type_id = t3.id "
			+ "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectAlarmDataByWarehouseIdAndCode(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品code和日期范围获取消耗品
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t1.code = #{searchStr} and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.warehouse_id = t2.id and t1.type_id = t3.id " + "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectByWarehouseIdAndCodeAndInTime(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品code和日期范围获取消耗品
	 * 
	 * 查看报警数据
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t1.code = #{searchStr} and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.count <= t1.safety_count and " + "t1.warehouse_id = t2.id and t1.type_id = t3.id "
			+ "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectAlarmDataByWarehouseIdAndCodeAndInTime(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和类型名和日期范围获取消耗品
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t3.name like '%${searchStr}%' and "
			+ "t1.warehouse_id = t2.id and " + "t1.type_id = t3.id " + "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectByWarehouseIdAndLikeTypeName(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和类型名和日期范围获取消耗品
	 * 
	 * 查看报警数据
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t3.name like '%${searchStr}%' and "
			+ "t1.count <= t1.safety_count and " + "t1.warehouse_id = t2.id and " + "t1.type_id = t3.id "
			+ "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectAlarmDataByWarehouseIdAndLikeTypeName(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和类型名和日期范围获取消耗品
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t3.name like '%${searchStr}%' and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.warehouse_id = t2.id and " + "t1.type_id = t3.id " + "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectByWarehouseIdAndLikeTypeNameAndInTime(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和类型名和日期范围获取消耗品
	 * 
	 * 查看报警信息
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and " + "t3.name like '%${searchStr}%' and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.count <= t1.safety_count and " + "t1.warehouse_id = t2.id and " + "t1.type_id = t3.id "
			+ "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> c_selectAlarmDataByWarehouseIdAndLikeTypeNameAndInTime(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据类型ID查询消耗品数量
	 * 
	 * @param typeId
	 * @return
	 */
	@Select("select count(id) from consumable where type_id = #{typeId} and is_delete = 0")
	Integer c_selectCountByTypeId(String typeId);

	/**
	 * 消耗品申请系统：根据仓库ID获取消耗品
	 * 
	 * 排除库存数为0的消耗品
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and t1.count > 0 and "
			+ "t1.warehouse_id = t2.id and t1.type_id = t3.id " + "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> a_selectByWarehouseId(String warehouseId);

	/**
	 * 消耗品申请系统：根据仓库ID和消耗品名获取消耗品
	 * 
	 * 排除库存数为0的消耗品
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "t1.name like '%${searchStr}%' and t1.count > 0 and " + "t1.warehouse_id = t2.id and "
			+ "t1.type_id = t3.id " + "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> a_selectByWarehouseIdAndLikeName(CSearchConditionModel model);

	/**
	 * 消耗品申请系统：根据仓库ID和消耗品Code获取消耗品
	 * 
	 * 排除库存数为0的消耗品
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name "
			+ "from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "t1.code = #{searchStr} and t1.count > 0 and " + "t1.warehouse_id = t2.id and "
			+ "t1.type_id = t3.id " + "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	List<ConsumableDto> a_selectByWarehouseIdAndCode(CSearchConditionModel model);

	/**
	 * 消耗品申请系统：根据仓库ID和消耗品ID获取消耗品
	 * 
	 * @param typeWarehouse
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as type_name from consumable as t1, warehouse as t2, type as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.id = #{id} and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t1.type_id = t3.id")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "referenceInformations", column = "id", many = @Many(select = "frame.server.dao.ConsumableReferenceInformationDao.selectByConsumableId")),
			@Result(property = "inboundBatchs", column = "id", many = @Many(select = "frame.server.dao.ConsumableInboundBatchDao.selectConsumableInboundBatchDtoByConsumableId")) })
	ConsumableDto a_selectByWarehouseIdAndConsumableId(ConsumableInfoDto consumable);

	@Select("select count(*) from consumable where supplier_id = #{supplierId} and is_delete = 0")
	Integer c_selectConsumableCountBySupplierId(String supplierId);
}
