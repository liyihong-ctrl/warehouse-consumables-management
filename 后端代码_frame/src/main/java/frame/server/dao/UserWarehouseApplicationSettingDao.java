package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.domain.UserWarehouseApplicationSetting;

/**
 * 申请人员可申请仓库配置
 * 
 * @author Administrator
 *
 */
@Mapper
public interface UserWarehouseApplicationSettingDao {

	/**
	 * 添加可申请仓库配置
	 * 
	 * @param setting
	 * @return
	 */
	@Insert("insert into user_warehouse_application_setting (#{setting})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertApplication(UserWarehouseApplicationSetting setting);

	/**
	 * 清空可申请仓库配置表
	 * 
	 * @return
	 */
	@Delete("delete from user_warehouse_application_setting")
	Integer clear();
	
	/**
	 * 根据用户ID删除配置
	 * 
	 * @param userId
	 * @return
	 */
	@Delete("delete from user_warehouse_application_setting where user_id = #{userId}")
	Integer deleteByUserId(String userId);
	
	/**
	 * 根据仓库ID删除配置
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Delete("delete from user_warehouse_application_setting where warehouse_id = #{warehouseId}")
	Integer deleteByWarehouseId(String warehouseId);
	
	/**
	 * 根据用户ID和仓库ID删除配置
	 * 
	 * @param setting
	 * @return
	 */
	@Delete("delete from user_warehouse_application_setting where user_id = #{userId} and warehouse_id = #{warehouseId}")
	Integer deleteByUserIdAndWarehouseId(UserWarehouseApplicationSetting setting);
	
	/**
	 * 根据仓库ID检索可申请用户ID
	 * 
	 * @param warehouseId
	 * @return
	 */
	@Select("select user_id from user_warehouse_application_setting where warehouse_id = #{warehouseId}")
	List<String> selectUserIdByWarehouseId(String warehouseId);
	
	/**
	 * 根据用户ID检索可申请仓库ID
	 * 
	 * @param userId
	 * @return
	 */
	@Select("select t1.warehouse_id from user_warehouse_application_setting as t1, warehouse as t2 "
			+ "where t1.user_id = #{userId} and t1.warehouse_id = t2.id and t2.is_enable = 1 and t2.is_delete = 0")
	List<String> selectWarehouseIdByUserId(String userId);
	
	/**
	 * 根据用户ID和仓库ID检索记录数
	 * 
	 * @param setting
	 * @return
	 */
	@Select("select count(*) from user_warehouse_application_setting where user_id = #{userId} and warehouse_id = #{warehouseId}")
	Integer selectCountByUserIdAndWarehouseId(UserWarehouseApplicationSetting setting);
}
