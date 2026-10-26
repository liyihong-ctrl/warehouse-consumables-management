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
import frame.server.domain.Application;
import frame.server.dto.ApplicationDto;
import frame.server.vo.consumables.CApplicationController.ASearchConditionModel;
import frame.server.vo.consumables.CApplicationController.ApprovalModel;
import frame.server.vo.consumables.CConsumableController.CSearchApplicationModel;

/**
 * 消耗品申请
 * 
 * @author Administrator
 *
 */
@Mapper
public interface ApplicationDao {

	/**
	 * 批量添加申请
	 * 
	 * @param applications
	 * @return
	 */
	@Insert("<script>insert into application "
			+ "(id, warehouse_id, user_id, name, remarks, create_time, is_cancel_application, is_delete, approval_status, approval_instructions) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "(#{item.id}, #{item.warehouseId}, #{item.userId}, #{name}, #{item.remarks}, #{item.createTime}, #{item.isCancelApplication}, #{item.isDelete}, #{item.approvalStatus}, #{item.ApprovalInstructions}) "
			+ "</foreach> " + "</script>")
	Integer insertApplications(List<Application> applications);

	/**
	 * 添加申请
	 * 
	 * @param user
	 * @return
	 */
	@Insert("insert into application (#{application})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertApplication(Application application);

	/**
	 * 清空申请表
	 * 
	 * @return
	 */
	@Delete("delete from application")
	Integer clear();

	/**
	 * 根据申请ID删除申请
	 * 
	 * @param userId
	 * @return
	 */
	@Update("update application set is_delete = 1 where id = #{id} and is_delete = 0")
	Integer delete(Application application);

	/**
	 * 更新申请
	 * 
	 * @param application
	 * @return
	 */
	@Update("update application set name = #{name} , remarks = #{remarks} where id = #{id} and is_delete = 0")
	Integer update(Application application);

	/**
	 * 更新审批状态
	 * 
	 * @param application
	 * @return
	 */
	@Update("update application set approval_status = #{approvalStatus} where id = #{id} and is_delete = 0")
	Integer updateApplicationState(Application application);

	/**
	 * 管理员设置审批状态
	 * 
	 * @param application
	 * @return
	 */
	@Update("update application set approval_status = #{approvalState}, approval_instructions = #{approvalInstructions} "
			+ "where id = #{applicationId} and is_delete = 0")
	Integer c_updateApplicationState(ApprovalModel model);

	/**
	 * 更新是否取消申请
	 * 
	 * @param application
	 * @return
	 */
	@Update("update application set is_cancel_application = !is_cancel_application where id = #{applicationId} and is_delete = 0")
	Integer updateIsCancelApplication(String applicationId);
	
	/**
	 * 仓库管理系统：根据用户ID检索未完成的申请记录数
	 * 
	 * @param userId
	 * @return
	 */
	@Select("select count(*) from application "
			+ "where user_id = #{userId} and (approval_status = 0 or approval_status = 1 or approval_status = 3) and is_delete = 0")
	Integer w_selectUndoneApplicationByUserId(String userId);

	/**
	 * 消耗品管理系统：根据消耗品申请ID检索申请数据
	 * 
	 * 1.未审核 2.审核中 3.审核通过
	 * 
	 * @param applicationId
	 * @return
	 */
	@Select("select t1.* from application as t1, warehouse as t2, user as t3 "
			+ "where t1.id = #{applicationId} and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t1.user_id = t3.id and " + "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0")
	Application c_selectByApplicationId(String applicationId);

	/**
	 * 消耗品管理系统：根据仓库ID查询申请总数
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select count(t1.id) " + "from application as t1, warehouse as t2, user as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t1.user_id = t3.id and " + "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0")
	Integer selectCountByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID检索申请数据
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as user_name "
			+ "from application as t1, warehouse as t2, user as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t1.user_id = t3.id and " + "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ApplicationDetailDao.selectByWarehouseId")) })
	List<ApplicationDto> c_selectByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：根据仓库ID检索申请数据
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as user_name "
			+ "from application as t1, warehouse as t2, user as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.warehouse_id = t2.id and t1.user_id = t3.id and " + "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ApplicationDetailDao.selectByWarehouseId")) })
	List<ApplicationDto> c_selectByWarehouseIdAndInTime(ASearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和申请名称模糊检索申请数据
	 * 
	 * @param application
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as user_name "
			+ "from application as t1, warehouse as t2, user as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.name like '%${name}%' and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t1.user_id = t3.id and " + "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ApplicationDetailDao.selectByWarehouseId")) })
	List<ApplicationDto> c_selectByWarehouseIdAndLikeName(Application application);

	/**
	 * 消耗品管理系统：根据仓库ID和申请名称和日期模糊检索申请数据
	 * 
	 * @param application
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as user_name "
			+ "from application as t1, warehouse as t2, user as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.name like '%${searchStr}%' and t1.is_delete = 0 and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.warehouse_id = t2.id and t1.user_id = t3.id and " + "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ApplicationDetailDao.selectByWarehouseId")) })
	List<ApplicationDto> c_selectByWarehouseIdAndLikeNameAndInTime(ASearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和用户名称模糊检索申请数据
	 * 
	 * @param application
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as user_name "
			+ "from application as t1, warehouse as t2, user as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t1.user_id = t3.id and " + "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.name like '%${name}%' and t3.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ApplicationDetailDao.selectByWarehouseId")) })
	List<ApplicationDto> c_selectByWarehouseIdAndLikeUserName(Application application);

	/**
	 * 消耗品管理系统：根据仓库ID和用户名称模糊检索申请数据
	 * 
	 * @param application
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as user_name "
			+ "from application as t1, warehouse as t2, user as t3 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "#{startTime} <= date_format(t1.create_time,'%Y-%m-%d') and date_format(t1.create_time,'%Y-%m-%d') <= #{endTime} and "
			+ "t1.warehouse_id = t2.id and t1.user_id = t3.id and " + "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.name like '%${searchStr}%' and t3.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ApplicationDetailDao.selectByWarehouseId")) })
	List<ApplicationDto> c_selectByWarehouseIdAndLikeUserNameAndInTime(ASearchConditionModel model);

	/**
	 * 消耗品管理系统：根据仓库ID和消耗品ID检索申请，且处于未审核、审核中、审核通过状态
	 * 
	 * @param model
	 * @return
	 */
	@Select("select count(t1.id) " + "from application as t1, application_detail as t2, warehouse as t3, user as t4 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.is_delete = 0 and "
			+ "(t1.approval_status = 0 or t1.approval_status = 1 or t1.approval_status = 3) and "
			+ "t1.id = t2.application_id and t1.warehouse_id = t3.id and t1.user_id = t4.id and "
			+ "t2.consumable_id = #{consumableId} and " + "t3.is_enable = 1 and t3.is_delete = 0 and "
			+ "t4.is_delete = 0")
	Integer c_selectCountByWarehouseIdAndConsumableIdAndIsIncompleteStatus(CSearchApplicationModel model);

	/**
	 * 根据仓库ID检索审核中、审核通过的申请
	 * 
	 * @param application
	 * @return
	 */
	@Select("select count(*) from application where warehouse_id = #{warehouseId} and (approval_status = 0 or approval_status = 1) and "
			+ "is_cancel_application = 0 and is_delete = 0")
	Integer selectNotCompletedByWarehouseId(String warehouseId);

	/**
	 * 消耗品申请系统：根据用户ID检索申请数据
	 * 
	 * @param userId
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as user_name "
			+ "from application as t1, warehouse as t2, user as t3 "
			+ "where t1.user_id = #{userId} and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t1.user_id = t3.id and " + "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ApplicationDetailDao.selectByWarehouseId")) })
	List<ApplicationDto> a_selectByUserId(String userId);

	/**
	 * 消耗品申请系统：根据用户ID和申请名称模糊检索申请数据
	 * 
	 * @param application
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as user_name "
			+ "from application as t1, warehouse as t2, user as t3 "
			+ "where t1.user_id = #{userId} and t1.name like '%${name}%' and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t1.user_id = t3.id and " + "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0 order by create_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ApplicationDetailDao.selectByWarehouseId")) })
	List<ApplicationDto> a_selectByUserIdAndLikeName(Application application);

	/**
	 * 消耗品申请系统：根据消耗品申请ID检索申请数据
	 * 
	 * @param applicationId
	 * @return
	 */
	@Select("select t1.*, t2.name as warehouse_name, t3.name as user_name "
			+ "from application as t1, warehouse as t2, user as t3 "
			+ "where t1.id = #{applicationId} and t1.is_delete = 0 and "
			+ "t1.warehouse_id = t2.id and t1.user_id = t3.id and " + "t2.is_enable = 1 and t2.is_delete = 0 and "
			+ "t3.is_delete = 0")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "details", column = "id", many = @Many(select = "frame.server.dao.ApplicationDetailDao.selectByWarehouseId")) })
	ApplicationDto a_selectApplicationDtoById(String applicationId);

}
