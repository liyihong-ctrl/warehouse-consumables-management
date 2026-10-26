package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.domain.ConsumableOperationDetail;
import frame.server.dto.ConsumableOperationDetailDto;

/**
 * 消耗品操作履历详细
 * 
 * @author Administrator
 *
 */
@Mapper
public interface ConsumableOperationDetailDao {

	/**
	 * 批量添加消耗品操作履历详细
	 * 
	 * @param consumableOperationDetails
	 * @return
	 */
	@Insert("<script>insert into consumable_operation_detail (id, consumable_operation_history_id, consumable_id, operation_type, operation_count, remarks) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "(#{id}, #{item.consumableOperationHistoryId}, #{item.consumableId}, #{item.operationType}, #{item.operationCount}, #{item.remarks}) "
			+ "</foreach></script>")
	Integer insertConsumableOperationDetails(List<ConsumableOperationDetail> consumableOperationDetails);
	
	/**
	 * 添加消耗品操作履历详细
	 * 
	 * @param consumableOperationDetail
	 * @return
	 */
	@Insert("insert into consumable_operation_detail (#{consumableOperationDetail})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertConsumableOperationDetail(ConsumableOperationDetail consumableOperationDetail);
	
	/**
	 * 清空消耗品操作履历详细表
	 * 
	 * @return
	 */
	@Delete("delete from consumable_operation_detail")
	Integer clear();
	
	/**
	 * 消耗品管理系统：根据操作履历ID检索详细
	 * 
	 * @param operationHistoryId
	 * @return
	 */
	@Select("select t1.*, t2.code as consumableCode, t2.name as consumable_name, t2.summary as consumable_summary, t3.name as consumable_type_name "
			+ "from consumable_operation_detail as t1, consumable as t2, type as t3 "
			+ "where t1.consumable_operation_history_id = #{operationHistoryId} and "
			+ "t1.consumable_id = t2.id and t2.type_id = t3.id")
	@Results({
		@Result(property = "id", column = "id"),
	})
	List<ConsumableOperationDetailDto> c_selectByOperationHistoryId(String operationHistoryId);

}
