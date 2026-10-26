package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.domain.ReferenceInformationTemplateDetail;

/**
 * 基准信息模板详细
 * 
 * @author Administrator
 *
 */
@Mapper
public interface ReferenceInformationTemplateDetailDao {

	/**
	 * 批量添加基准信息模板详细
	 * 
	 * @param referenceInformationTemplateDetails
	 * @return
	 */
	@Insert("<script>insert into reference_information_template_detail (id, reference_information_template_id, reference_key, display_order) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "(#{item.id}, #{item.referenceInformationTemplateId}, #{item.referenceKey}, #{item.displayOrder}) "
			+ "</foreach></script>")
	Integer insertReferenceInformationTemplateDetails(List<ReferenceInformationTemplateDetail> referenceInformationTemplateDetails);
	
	/**
	 * 添加基准信息模板详细
	 * 
	 * @param referenceInformationTemplate
	 * @return
	 */
	@Insert("insert into reference_information_template_detail (#{referenceInformationTemplateDetail})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertReferenceInformationTemplateDetail(ReferenceInformationTemplateDetail referenceInformationTemplateDetail);
	
	/**
	 * 清空基准信息模板详细表
	 * 
	 * @return
	 */
	@Delete("delete from reference_information_template_detail")
	Integer clear();
	
	/**
	 * 根据基准信息模板ID删除基准信息模板详细
	 * 
	 * @param referenceInformationTemplateId
	 * @return
	 */
	@Delete("delete from reference_information_template_detail where reference_information_template_id = #{referenceInformationTemplateId}")
	Integer deleteByReferenceInformationTemplateId(String referenceInformationTemplateId);

	/**
	 * 根据基准信息模板ID查询基准信息模板详细
	 * 
	 * @param referenceInformationTemplateId
	 * @return
	 */
	@Select("select * from reference_information_template_detail where reference_information_template_id = #{referenceInformationTemplateId} order by display_order")
	List<ReferenceInformationTemplateDetail> selectByReferenceInformationTemplateId(String referenceInformationTemplateId);
	
}
