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
import frame.server.domain.ConsumableOperationHistory;
import frame.server.dto.ConsumableOperationHistoryDto;
import frame.server.dto.ConsumableStatisticDto;
import frame.server.vo.consumables.CSearchConditionModel;
import frame.server.vo.consumables.CStatisticsController.CGetConsumableStatisticsSearchSingleModel;

/**
 * 消耗品操作履历
 * 
 * @author Administrator
 *
 */
@Mapper
public interface ConsumableOperationHistoryDao {

	/**
	 * 批量添加消耗品操作履历
	 * 
	 * @param consumableOperationHistorys
	 * @return
	 */
	@Insert("<script>insert into consumable_operation_history (id, warehouse_id, user_id, create_time) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "(#{item.id}, #{item.warehouseId}, #{item.userId}, #{item.createTime}) " + "</foreach></script>")
	Integer insertConsumableOperationHistorys(List<ConsumableOperationHistory> consumableOperationHistorys);

	/**
	 * 添加消耗品操作履历
	 * 
	 * @param consumableOperationHistory
	 * @return
	 */
	@Insert("insert into consumable_operation_history (#{consumableOperationHistory})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertConsumableOperationHistory(ConsumableOperationHistory consumableOperationHistory);

	/**
	 * 清空消耗品操作履历表
	 * 
	 * @return
	 */
	@Delete("delete from consumable_operation_history")
	Integer clear();

	/**
	 * 消耗品管理系统：根据仓库ID检索操作履历数据
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t1.*, t3.name as user_name, t3.account as user_account "
			+ "from consumable_operation_history as t1, warehouse as t2, user as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.user_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ConsumableOperationDetailDao.c_selectByOperationHistoryId")) })
	List<ConsumableOperationHistoryDto> c_selectByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID和用户名称模糊检索操作履历数据
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t3.name as user_name, t3.account as user_account "
			+ "from consumable_operation_history as t1, warehouse as t2, user as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.user_id = t3.id and "
			+ "t3.name like '%${searchStr}%' and " + "t2.is_enable = 1 and t2.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ConsumableOperationDetailDao.c_selectByOperationHistoryId")) })
	List<ConsumableOperationHistoryDto> c_selectByWarehouseIdAndLikeUserName(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品名称模糊检索操作履历数据
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t3.name as user_name, t3.account as user_account "
			+ "from consumable_operation_history as t1, warehouse as t2, user as t3, consumable_operation_detail as t4, consumable as t5 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.user_id = t3.id and "
			+ "t1.id = t4.consumable_operation_history_id and t4.consumable_id = t5.id and "
			+ "t5.name like '%${searchStr}%' and " + "t2.is_enable = 1 and t2.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ConsumableOperationDetailDao.c_selectByOperationHistoryId")) })
	List<ConsumableOperationHistoryDto> c_selectByWarehouseIdAndLikeConsumableName(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和日期范围检索操作履历数据
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t1.*, t3.name as user_name, t3.account as user_account "
			+ "from consumable_operation_history as t1, warehouse as t2, user as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.user_id = t3.id and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} "
			+ "order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ConsumableOperationDetailDao.c_selectByOperationHistoryId")) })
	List<ConsumableOperationHistoryDto> c_selectByWarehouseIdAndInTime(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和用户名称和日期范围模糊检索操作履历数据
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t3.name as user_name, t3.account as user_account "
			+ "from consumable_operation_history as t1, warehouse as t2, user as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.user_id = t3.id and "
			+ "t3.name like '%${searchStr}%' and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ConsumableOperationDetailDao.c_selectByOperationHistoryId")) })
	List<ConsumableOperationHistoryDto> c_selectByWarehouseIdAndLikeUserNameAndInTime(CSearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品名称模糊和日期范围检索操作履历数据
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t1.*, t3.name as user_name, t3.account as user_account "
			+ "from consumable_operation_history as t1, warehouse as t2, user as t3, consumable_operation_detail as t4, consumable as t5 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.warehouse_id = t2.id and t1.user_id = t3.id and "
			+ "t1.id = t4.consumable_operation_history_id and t4.consumable_id = t5.id and "
			+ "t5.name like '%${searchStr}%' and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t2.is_enable = 1 and t2.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ConsumableOperationDetailDao.c_selectByOperationHistoryId")) })
	List<ConsumableOperationHistoryDto> c_selectByWarehouseIdAndLikeConsumableNameAndInTime(
			CSearchConditionModel model);

	/**
	 * 消耗品管理系统：获取消耗品出库统计数据
	 * 
	 * 出库
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t3.id, t3.name, t1.operation_type, sum(t1.operation_count) as operate_count, date_format(t2.create_time, '%Y-%m-%d') as operate_time "
			+ "from consumable_operation_detail as t1, consumable_operation_history as t2, consumable as t3 "
			+ "where t1.consumable_id = #{consumableId} and t1.operation_type = 0 and t1.consumable_operation_history_id = t2.id and "
			+ "#{startTime} <= date_format(t2.create_time,'%Y-%m-%d') and date_format(t2.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.consumable_id = t3.id and t3.warehouse_id = #{warehouseId} "
			+ "group by id, operate_time order by operate_time ")
	List<ConsumableStatisticDto> c_selectOutboundStaticDataBySearchModel(CGetConsumableStatisticsSearchSingleModel model);

	/**
	 * 消耗品管理系统：获取消耗品在工减少统计数据
	 * 
	 * 在工减少
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t3.id, t3.name, t1.operation_type, sum(t1.operation_count) as operate_count, date_format(t2.create_time, '%Y-%m-%d') as operate_time "
			+ "from consumable_operation_detail as t1, consumable_operation_history as t2, consumable as t3 "
			+ "where t1.consumable_id = #{consumableId} and t1.operation_type = 1 and t1.consumable_operation_history_id = t2.id and "
			+ "#{startTime} <= date_format(t2.create_time,'%Y-%m-%d') and date_format(t2.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.consumable_id = t3.id and t3.warehouse_id = #{warehouseId} "
			+ "group by id, operate_time order by operate_time ")
	List<ConsumableStatisticDto> c_selectReducedWorkStaticDataBySearchModel(CGetConsumableStatisticsSearchSingleModel model);
	/**
	 * 消耗品管理系统：获取消耗品入库统计数据
	 * 
	 * 入库(包含新增)
	 * 
	 * @param model
	 * @return
	 */
	@Select("select t3.id, t3.name, t1.operation_type, sum(t1.operation_count) as operate_count, date_format(t2.create_time, '%Y-%m-%d') as operate_time "
			+ "from consumable_operation_detail as t1, consumable_operation_history as t2, consumable as t3 "
			+ "where t1.consumable_id = #{consumableId} and (t1.operation_type = 2 or t1.operation_type = 3 or t1.operation_type = 100) and t1.consumable_operation_history_id = t2.id and "
			+ "#{startTime} <= date_format(t2.create_time,'%Y-%m-%d') and date_format(t2.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.consumable_id = t3.id and t3.warehouse_id = #{warehouseId} "
			+ "group by id, operate_time order by operate_time ")
	List<ConsumableStatisticDto> c_selectInboundStaticDataBySearchModel(CGetConsumableStatisticsSearchSingleModel model);
}
