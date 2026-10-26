package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.domain.ConsumableReferenceInformation;

/**
 * 消耗品基准信息
 * 
 * @author Administrator
 *
 */
@Mapper
public interface ConsumableReferenceInformationDao {
	
	/**
	 * 批量添加消耗品基准信息
	 * 
	 * @param consumableReferenceInformations
	 * @return
	 */
	@Insert("<script>insert into consumable_reference_information "
			+ "(id, consumable_id, reference_key, reference_value, display_order) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "(#{item.id}, #{item.consumableId}, #{item.referenceKey}, #{item.referenceValue}, #{item.displayOrder}) "
			+ "</foreach> "
			+ "</script>")
	Integer insertReferenceInformations(List<ConsumableReferenceInformation> referenceInformations);

	/**
	 * 添加消耗品基准信息
	 * 
	 * @param consumableReferenceInformation
	 * @return
	 */
	@Insert("insert into consumable_reference_information (#{referenceInformation})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertReferenceInformation(ConsumableReferenceInformation referenceInformation);

	/**
	 * 清空消耗品表
	 * 
	 * @return
	 */
	@Delete("delete from consumable_reference_information")
	Integer clear();

	/**
	 * 清空消耗品表
	 * 
	 * @return
	 */
	@Delete("delete from consumable_reference_information where consumable_id = #{consumableId}")
	Integer deleteByConsumableId(String consumableId);
	
	/**
	 * 根据消耗品ID获取基准信息
	 * 
	 * @param consumableId
	 * @return
	 */
	@Select("select * from consumable_reference_information where consumable_id = #{consumableId} order by display_order")
	List<ConsumableReferenceInformation> selectByConsumableId(String consumableId);

}
