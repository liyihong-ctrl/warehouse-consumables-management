package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.domain.UserExtension;

/**
 * 用户扩展信息
 * 
 * @author Administrator
 *
 */
@Mapper
public interface UserExtensionDao {

	/**
	 * 批量添加用户扩展信息
	 * 
	 * @param userExtensions
	 * @return
	 */
	@Insert("<script>insert into user_extension "
			+ "(user_id, initialized_password, department) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "(#{item.userId}, #{item.initializedPassword}, #{item.department}) "
			+ "</foreach> "
			+ "</script>")
	Integer insertUserExtensions(List<UserExtension> userExtensions);
	
	/**
	 * 添加用户扩展信息
	 * 
	 * @param userExtension
	 * @return
	 */
	@Insert("insert into user_extension (#{userExtension})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertUserExtension(UserExtension userExtension);
	
	/**
	 * 清空用户扩展信息表
	 * 
	 * @return
	 */
	@Delete("delete from user_extension")
	Integer clear();
	
	/**
	 * 仓库管理系统：更新用户部门信息
	 * 
	 * @param userExtension
	 * @return
	 */
	@Update("update user_extension set department = #{department} where user_id = #{userId}")
	Integer w_updateUserDepartment(UserExtension userExtension);
	
	/**
	 * 消耗品申请系统：更新用户部门信息
	 * 
	 * @param userExtension
	 * @return
	 */
	@Update("update user_extension set department = #{department} where user_id = #{userId}")
	Integer a_updateUserDepartment(UserExtension userExtension);
	
	/**
	 * 根据用户ID查询用户扩展信息
	 * 
	 * @param userId
	 * @return
	 */
	@Select("select * from user_extension where user_id = #{userId}")
	UserExtension selectByUserId(String userId);
	
}
