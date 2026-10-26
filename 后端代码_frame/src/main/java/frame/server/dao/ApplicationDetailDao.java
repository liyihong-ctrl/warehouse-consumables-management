package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.domain.ApplicationDetail;
import frame.server.dto.ApplicationDetailDto;

/**
 * 消耗品申请详细
 * 
 * @author Administrator
 *
 */
@Mapper
public interface ApplicationDetailDao {

	/**
	 * 批量添加申请详细
	 * 
	 * @param applicationDetails
	 * @return
	 */
	@Insert("<script>insert into application_detail "
			+ "(id, application_id, consumable_id, applicate_count, remarks, is_cancel_application, is_delete, approval_status, approval_instructions) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "(#{item.id}, #{item.applicationId}, #{item.consumableId}, #{item.applicateCount}, #{item.remarks}, #{item.isCancelApplication}, #{item.isDelete}, #{item.approvalStatus}, #{item.ApprovalInstructions}) "
			+ "</foreach> " + "</script>")
	Integer insertApplicationDetails(List<ApplicationDetail> applicationDetails);

	/**
	 * 添加申请详细
	 * 
	 * @param user
	 * @return
	 */
	@Insert("insert into application_detail (#{applicationDetail})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertApplicationDetail(ApplicationDetail applicationDetail);

	/**
	 * 清空申请详细表
	 * 
	 * @return
	 */
	@Delete("delete from application_detail")
	Integer clear();
	
	/**
	 * 根据申请详细ID删除申请详细
	 * 
	 * @param applicationDetailId
	 * @return
	 */
	@Update("update application_detail set is_delete = 1 where id = #{id} and is_delete = 0")
	Integer delete(String applicationDetailId);
	
	/**
	 * 根据申请ID删除申请详细
	 * 
	 * @param applicationId
	 * @return
	 */
	@Update("update application_detail set is_delete = 1 where application_id = #{applicationId} and is_delete = 0")
	Integer deleteByApplicationId(String applicationId);
	
	/**
	 * 更新备注
	 * 
	 * @param applicationDetail
	 * @return
	 */
	@Update("update application_detail set remarks = #{remarks} where id = #{id} and is_delete = 0")
	Integer updateRemarks(ApplicationDetail applicationDetail);
	
	/**
	 * 更新是否取消申请详细
	 * 
	 * @param applicationDetail
	 * @return
	 */
	@Update("update application_detail set is_cancel_application = #{isCancelApplication} where id = #{id}  and is_delete = 0")
	Integer updateIsCancelApplication(ApplicationDetail applicationDetail);
	
	/**
	 * 根据申请ID检索申请详细数据
	 * 
	 * @param applicationId
	 * @return
	 */
	@Select("select t1.*, t2.code, t2.name as consumable_name, t3.name as type_name "
			+ "from application_detail as t1, consumable as t2, type as t3 "
			+ "where t1.application_id = #{applicationId} and t1.is_delete = 0 and "
			+ "t1.consumable_id = t2.id and t2.type_id = t3.id")
	List<ApplicationDetailDto> selectByWarehouseId(String applicationId);

}
