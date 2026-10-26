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
import frame.server.domain.ReferenceInformationTemplate;
import frame.server.dto.ReferenceInformationTemplateDto;
import frame.server.dto.ReferenceInformationTemplateUpdateDto;

/**
 * 基准信息模板
 * 
 * @author Administrator
 *
 */
@Mapper
public interface ReferenceInformationTemplateDao {

	/**
	 * 消耗品管理系统：批量添加基准信息模板
	 * 
	 * @param referenceInformationTemplates
	 * @return
	 */
	@Insert("<script>insert into reference_information_template (id, warehouse_id, name, remarks, create_time, is_system, is_delete) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "(#{item.id}, #{item.warehouseId}, #{item.name}, #{item.remarks}, #{item.createTime}, #{item.isSystem}, #{item.isDelete}) "
			+ "</foreach></script>")
	Integer insertReferenceInformationTemplates(List<ReferenceInformationTemplate> referenceInformationTemplates);

	/**
	 * 消耗品管理系统：添加基准信息模板
	 * 
	 * @param referenceInformationTemplate
	 * @return
	 */
	@Insert("insert into reference_information_template (#{referenceInformationTemplate})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertReferenceInformationTemplate(ReferenceInformationTemplate referenceInformationTemplate);

	/**
	 * 消耗品管理系统：清空基准信息模板表
	 * 
	 * @return
	 */
	@Delete("delete from reference_information_template")
	Integer clear();
	
	/**
	 * 消耗品管理系统：删除系统内置基准信息模板
	 * 
	 * @return
	 */
	@Delete("delete from reference_information_template where is_system = 1")
	Integer deleteSystemTemplate();

	/**
	 * 消耗品管理系统：根据仓库ID删除基准信息模板
	 * 
	 * @return
	 */
	@Delete("update reference_information_template set is_delete = 1 where warehouse_id = #{warehouseId} and is_delete = 0")
	Integer deleteByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID和基准信息模板ID删除基准信息模板
	 * 
	 * @return
	 */
	@Delete("update reference_information_template set is_delete = 1 where warehouse_id = #{warehouseId} and id = #{id} and is_delete = 0")
	Integer deleteByWarehouseIdAndReferenceInformationTemplateId(
			ReferenceInformationTemplate referenceInformationTemplate);

	/**
	 * 消耗品管理系统：更新基准信息模板
	 * 
	 * @param referenceInformationTemplate
	 * @return
	 */
	@Update("update reference_information_template (#{referenceInformationTemplate}) where id =#{id} and is_delete = 0")
	@Lang(SimpleUpdateExtendedLanguageDriver.class)
	Integer update(ReferenceInformationTemplateUpdateDto referenceInformationTemplate);
	
	/**
	 * 消耗品管理系统：获取系统内置基准信息模板ID
	 * 
	 * @return
	 */
	@Select("select id from reference_information_template where is_system = 1")
	String c_selectSystemTemplate();

	/**
	 * 消耗品管理系统：根据仓库ID查询基准信息模板总数
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select count(*) from reference_information_template as t1, warehouse as t2 where "
			+ "t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t2.is_enable = 1 and t2.is_delete = 0")
	Integer c_selectCountByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID查询基准信息模板
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select * from reference_information_template where (warehouse_id = #{warehouseId} or warehouse_id = '') and is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ReferenceInformationTemplateDetailDao.selectByReferenceInformationTemplateId")) })
	List<ReferenceInformationTemplateDto> c_selectReferenceInformationTemplateDtoByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：查询系统内置基准信息模板
	 * 
	 * @return
	 */
	@Select("select * from reference_information_template where warehouse_id = '' and is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ReferenceInformationTemplateDetailDao.selectByReferenceInformationTemplateId")) })
	List<ReferenceInformationTemplateDto> c_selectSystemReferenceInformationTemplateDtoByWarehouseId();

	/**
	 * 消耗品管理系统：根据仓库ID和基准信息模板ID查询基准信息模板
	 * 
	 * @param referenceInformationTemplate
	 * @return
	 */
	@Select("select * from reference_information_template "
			+ "where (warehouse_id = #{warehouseId} or warehouse_id = '') and id = #{id} and is_delete = 0")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ReferenceInformationTemplateDetailDao.selectByReferenceInformationTemplateId")) })
	ReferenceInformationTemplateDto c_selectByWarehouseIdAndReferenceInformationTemplateId(
			ReferenceInformationTemplate referenceInformationTemplate);

	/**
	 * 消耗品管理系统：根据仓库ID和基准信息模板ID查询基准信息模板记录数
	 * 
	 * 不包含系统内置
	 * 
	 * @param referenceInformationTemplate
	 * @return
	 */
	@Select("select count(*) from reference_information_template "
			+ "where warehouse_id = #{warehouseId} and id = #{id} and is_delete = 0")
	Integer c_selectCountByWarehouseIdAndReferenceInformationTemplateIdAndNotIsSystem(
			ReferenceInformationTemplate referenceInformationTemplate);

	/**
	 * 消耗品管理系统：根据仓库ID和基准信息模板名称查询基准信息模板记录数
	 * 
	 * 包含系统内置
	 * 
	 * @param referenceInformationTemplate
	 * @return
	 */
	@Select("select count(*) from reference_information_template "
			+ "where (warehouse_id = #{warehouseId} or warehouse_id = '') and name = #{name} and is_delete = 0")
	Integer c_selectCountByWarehouseIdAndReferenceInformationTemplateName(
			ReferenceInformationTemplate referenceInformationTemplate);

	/**
	 * 消耗品管理系统：根据仓库ID和名称模糊查询基准信息模板
	 * 
	 * @param referenceInformationTemplate
	 * @return
	 */
	@Select("select * from reference_information_template where (warehouse_id = #{warehouseId} or warehouse_id = '') and name like '%${name}%' and is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ReferenceInformationTemplateDetailDao.selectByReferenceInformationTemplateId")) })
	List<ReferenceInformationTemplateDto> c_selectByWarehouseIdAndLikeName(
			ReferenceInformationTemplate referenceInformationTemplate);

}
