package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.domain.ConsumableInboundBatch;
import frame.server.dto.ConsumableInboundBatchDto;

/**
 * 消耗品入库批次
 * 
 * @author Administrator
 *
 */
@Mapper
public interface ConsumableInboundBatchDao {
	
	/**
	 * 批量添加消耗品入库批次
	 * 
	 * @param consumableInboundBatchs
	 * @return
	 */
	@Insert("<script>insert into consumable_inbound_batch "
			+ "(id, consumable_id, count, price, create_time) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "(#{item.id}, #{item.consumableId}, #{item.count},  #{item.price}, #{item.createTime}) "
			+ "</foreach> " + "</script>")
	Integer insertConsumableInboundBatchs(List<ConsumableInboundBatch> consumableInboundBatchs);

	/**
	 * 添加消耗品入库批次
	 * 
	 * @param consumableInboundBatch
	 * @return
	 */
	@Insert("insert into consumable_inbound_batch (#{consumableInboundBatch})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertConsumableInboundBatch(ConsumableInboundBatch consumableInboundBatch);

	/**
	 * 更新消耗品入库批次价格
	 * 
	 * @param consumableInboundBatch
	 * @return
	 */
	@Update("update consumable_inbound_batch set price = #{price} where id = #{id}")
	Integer updatePrice(ConsumableInboundBatch consumableInboundBatch);

	/**
	 * 更新消耗品入库批次数量
	 * 
	 * @param consumableInboundBatch
	 * @return
	 */
	@Update("update consumable_inbound_batch set count = #{count} where id = #{id}")
	Integer updateCount(ConsumableInboundBatch consumableInboundBatch);
	
	/**
	 * 根据消耗品ID检索批次
	 * 
	 * @param consumableId
	 * @return
	 */
	@Select("select * from consumable_inbound_batch where consumable_id = #{consumableId} order by create_time")
	List<ConsumableInboundBatch> selectByConsumableId(String consumableId);
	
	/**
	 * 根据消耗品ID检索批次
	 * 
	 * @param consumableId
	 * @return
	 */
	@Select("select * from consumable_inbound_batch where consumable_id = #{consumableId} order by create_time")
	List<ConsumableInboundBatchDto> selectConsumableInboundBatchDtoByConsumableId(String consumableId);

	/**
	 * 根据消耗品ID检索批次
	 * 
	 * 最后一次入库
	 * 
	 * @param consumableId
	 * @return
	 */
	@Select("select * from consumable_inbound_batch where consumable_id = #{consumableId} order by create_time desc limit 1")
	ConsumableInboundBatch selectByConsumableIdLastTime(String consumableId);

}
