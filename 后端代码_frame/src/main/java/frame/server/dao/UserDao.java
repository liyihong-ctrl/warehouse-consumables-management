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
import frame.server.domain.User;
import frame.server.dto.ApplicationUserDto;
import frame.server.dto.UserDto;
import frame.server.dto.UserInfoDto;
import frame.server.vo.application.AUserInfoController.AEditUserInfoModel;
import frame.server.vo.consumables.CUserController.GetUsersSelectModel;
import frame.server.vo.consumables.CUserInfoController.CEditUserInfoModel;
import frame.server.vo.warehouse.WUserInfoController.WEditUserInfoModel;

/**
 * 用户
 * 
 * @author Administrator
 *
 */
@Mapper
public interface UserDao {

	/**
	 * 批量添加用户
	 * 
	 * @param users
	 * @return
	 */
	@Insert("<script>insert into user "
			+ "(id, account, password, name, gender, avatar, email, phone, type, register_time, is_enable, is_delete) values "
			+ "<foreach collection = 'list' item = 'item' separator = ','> "
			+ "(#{item.id}, #{item.account}, #{item.password}, #{item.name}, #{item.gender}, #{item.avatar}, #{item.email}, #{item.phone}, #{item.type}, #{item.registerTime}, #{item.isEnable}, #{item.isDelete}) "
			+ "</foreach> " + "</script>")
	Integer insertUsers(List<User> users);

	/**
	 * 添加用户
	 * 
	 * @param user
	 * @return
	 */
	@Insert("insert into user (#{user})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insertUser(User user);

	/**
	 * 清空用户表
	 * 
	 * @return
	 */
	@Delete("delete from user")
	Integer clear();

	/**
	 * 根据用户ID删除用户(真删除)
	 * 
	 * @param userId
	 * @return
	 */
	@Delete("delete from user where id = #{userId} and is_delete = 0")
	Integer deleteByUserId(String userId);

	/**
	 * 更新用户信息
	 * 
	 * @param user
	 * @return
	 */
	@Update("update user (#{user}) where id = #{id} and is_delete = 0")
	@Lang(SimpleUpdateExtendedLanguageDriver.class)
	Integer updateUserInfo(UserInfoDto user);

	/**
	 * 仓库管理系统：更新用户信息
	 * 
	 * @param user
	 * @return
	 */
	@Update("update user (#{user}) where id = #{id} and is_delete = 0")
	@Lang(SimpleUpdateExtendedLanguageDriver.class)
	Integer w_updateUserInfoBySelf(WEditUserInfoModel user);

	/**
	 * 消耗品管理系统：更新用户信息
	 * 
	 * @param user
	 * @return
	 */
	@Update("update user (#{user}) where id = #{id} and is_delete = 0")
	@Lang(SimpleUpdateExtendedLanguageDriver.class)
	Integer c_updateInfoBySelf(CEditUserInfoModel user);

	/**
	 * 消耗品申请系统：更新用户信息
	 * 
	 * @param user
	 * @return
	 */
	@Update("update user set name=#{name}, gender=#{gender}, avatar=#{avatar}, email=#{email}, phone=#{phone} "
			+ "where id = #{id} and is_delete = 0")
	Integer a_updateUserInfoBySelf(AEditUserInfoModel user);

	/**
	 * 更新用户密码
	 * 
	 * @param user
	 * @return
	 */
	@Update("update user set password = #{password} where id = #{id} and is_delete = 0")
	Integer updateUserPassword(User user);

	/**
	 * 更新邮箱
	 * 
	 * @param user
	 * @return
	 */
	@Update("update user set email = #{email} where id = #{id} and is_delete = 0")
	Integer updateUserEmail(User user);

	/**
	 * 通用：修改用户状态
	 * 
	 * @param userId
	 * @return
	 */
	@Update("update user set is_enable = !is_enable where id = #{userId} and is_delete = 0")
	Integer updateUserState(String userId);

	/**
	 * 更新用户删除状态
	 * 
	 * @param userId
	 * @return
	 */
	@Update("update user set is_delete = !is_delete where id = #{id} and is_delete = 0")
	Integer updateUserDeleteState(String userId);

	/**
	 * 获取用户总数
	 * 
	 * @return
	 */
	@Select("select count(*) from user where is_delete = 0")
	Integer selectUsersCount();

	/**
	 * 获取系统管理员和仓库管理员总数
	 * 
	 * @return
	 */
	@Select("select count(*) from user where (type = '0' or type = '1' or type = '2' or type = '8') and is_delete = 0")
	Integer selectSystemAdminAndWarehouseManagerCount();

	/**
	 * 仓库管理系统： 获取全部用户信息
	 * 
	 * @return
	 */
	@Select("select * from user where is_delete = 0 and (type = '0' or type = '1' or type = '2' or type = '8') order by register_time desc")
	List<User> selectWarehouseAllUser();

	/**
	 * 仓库管理系统： 获取仓库管理员信息
	 * 
	 * @return
	 */
	@Select("select * from user where type = '2' and is_delete = 0 order by register_time desc")
	List<User> selectAllWarehouseManager();

	/**
	 * 仓库管理系统： 获取仓库管理员Id
	 * 
	 * @return
	 */
	@Select("select id from user where type = '2' and is_delete = 0 order by register_time desc")
	List<String> selectAllWarehouseManagerIds();

	/**
	 * 仓库管理系统：获取用户信息
	 * 
	 * 1. 系统管理员 2. 仓库管理员 3. 申请人员
	 * 
	 * 
	 * @return
	 */
	@Select("select * from user where (type = '0' or type = '1' or type = '2' or type = '8') and is_delete = 0 "
			+ "order by register_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")) })
	List<UserDto> w_selectUserDtos();

	/**
	 * 仓库管理系统：根据姓名模糊检索用户信息
	 * 
	 * 1. 系统管理员 2. 仓库管理员 3. 申请人员
	 * 
	 * @return
	 */
	@Select("select * from user "
			+ "where (type = '0' or type = '1' or type = '2' or type = '8') and name like '%${name}%' and is_delete = 0 "
			+ "order by register_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")) })
	List<UserDto> w_selectUserDtoLikeUserName(String name);

	/**
	 * 仓库管理系统：根据账号模糊检索用户信息
	 * 
	 * 1. 系统管理员 2. 仓库管理员 3. 申请人员
	 * 
	 * @return
	 */
	@Select("select * from user "
			+ "where (type = '0' or type = '1' or type = '2' or type = '8') and account like '%${account}%' and is_delete = 0 "
			+ "order by register_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")) })
	List<UserDto> w_selectUserDtoLikeUserAccount(String account);

	/**
	 * 仓库管理系统：根据用户类型检索用户信息
	 * 
	 * 1. 系统管理员 2. 仓库管理员 3. 申请人员
	 * 
	 * @return
	 */
	@Select("select * from user where type = #{type} and is_delete = 0 order by register_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")) })
	List<UserDto> w_selectUserDtoByUserType(String type);

	/**
	 * 仓库管理系统： 根据用户ID查询用户信息(不包含角色信息和权限信息)
	 * 
	 * @param userId
	 * @return
	 */
	@Select("select * from user where is_delete = 0 and (type = '0' or type = '1' or type = '2' or type = '8') and id = #{userId}")
	User selectWarehouseUserById(String userId);

	/**
	 * 消耗品管理系统：根据用户ID查询用户信息(不包含角色信息和权限信息)
	 * 
	 * @param userId
	 * @return
	 */
	@Select("select * from user where id = #{userId} and type = '4' and is_delete = 0")
	User c_selectUserById(String userId);

	/**
	 * 消耗品管理系统：根据用户ID查询用户信息(不包含角色信息和权限信息)
	 * 
	 * @param userId
	 * @return
	 */
	@Select("select * from user where id = #{userId} and type = '8' and is_delete = 0")
	User c_selectApplicationUserById(String userId);

	/**
	 * 消耗品管理系统：获取全部用户信息
	 * 
	 * 
	 * @return
	 */
	@Select("select t2.* from user_warehouse as t1, user as t2 where t1.warehouse_id = #{warehouseId} and t1.user_id = t2.id and "
			+ "(t2.type = '2' or t2.type = '4') and t2.is_delete = 0 order by register_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")) })
	List<UserDto> c_selectUserDtoByWarehouseId(String warehouseId);

	/**
	 * 消耗品管理系统：获取申请用户信息
	 * 
	 * 
	 * @return
	 */
	@Select("select * from user where type = '8' and is_enable = 1 and is_delete = 0 order by register_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")),
			@Result(property = "canApplicationWarehouseIds", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseApplicationSettingDao.selectWarehouseIdByUserId"))})
	List<ApplicationUserDto> c_selectApplicationUserDtoByWarehouseId();

	/**
	 * 消耗品管理系统：根据姓名模糊检索全部用户信息
	 * 
	 * @return
	 */
	@Select("select t2.* from user_warehouse as t1, user as t2 where t1.warehouse_id = #{warehouseId} and t1.user_id = t2.id and "
			+ "(t2.type = '2' or t2.type = '4') and t2.name like '%${searchStr}%' and t2.is_delete = 0 order by register_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")) })
	List<UserDto> c_selectUserDtoLikeName(GetUsersSelectModel model);

	/**
	 * 消耗品管理系统：根据姓名模糊检索申请用户信息
	 * 
	 * @return
	 */
	@Select("select * from user where type = '8' and name like '%${searchStr}%' and is_enable = 1 and is_delete = 0 order by register_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")) })
	List<ApplicationUserDto> c_selectApplicationUserDtoLikeName(GetUsersSelectModel model);

	/**
	 * 消耗品管理系统：根据账号模糊检索全部用户信息
	 * 
	 * @return
	 */
	@Select("select t2.* from user_warehouse as t1, user as t2 where t1.warehouse_id = #{warehouseId} and t1.user_id = t2.id and "
			+ "(t2.type = '2' or t2.type = '4') and t2.account like '%${searchStr}%' and t2.is_delete = 0 order by register_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")) })
	List<UserDto> c_selectUserDtoLikeAccount(GetUsersSelectModel model);

	/**
	 * 消耗品管理系统：根据账号模糊检索申请用户信息
	 * 
	 * @return
	 */
	@Select("select * from user where type = '8' and account like '%${searchStr}%' and is_enable = 1 and is_delete = 0 order by register_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")) })
	List<ApplicationUserDto> c_selectApplicationUserDtoLikeAccount(GetUsersSelectModel model);

	/**
	 * 消耗品管理系统：根据用户角色名称检索全部用户信息
	 * 
	 * @return
	 */
	@Select("select t2.* from user_warehouse as t1, user as t2, user_role as t3, role as t4 "
			+ "where t1.warehouse_id = #{warehouseId} and t1.user_id = t2.id and t2.id = t3.user_id and t3.role_id = t4.id and "
			+ "(t2.type = '2' or t2.type = '4') and t4.name like '%${searchStr}%' and t2.is_delete = 0 order by register_time desc")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")) })
	List<UserDto> c_selectUserDtoByRoleName(GetUsersSelectModel model);

	/**
	 * 根据用户账户查询用户信息(不包含角色信息和权限信息)
	 * 
	 * @param account
	 * @return
	 */
	@Select("select * from user where is_delete = 0 and account = #{account}")
	User selectUserByAccount(String account);

	/**
	 * 通用：根据用户账号查询用户数
	 * 
	 * 主要用于判断用户账号是否存在
	 * 
	 * @param account
	 * @return
	 */
	@Select("select count(*) from user where account = #{account} and is_delete = 0")
	int selectUserCountByAccount(String account);

	/**
	 * 根据用户ID查询用户信息
	 * 
	 * @param account
	 * @return
	 */
	@Select("select * from user where id = #{userId} and is_delete = 0")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")) })
	UserDto selectUserDtoByUserId(String userId);

	/**
	 * 根据用户账户查询用户信息
	 * 
	 * @param account
	 * @return
	 */
	@Select("select * from user where account = #{account} and is_delete = 0")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")) })
	UserDto selectUserDtoByAccount(String account);

	/**
	 * 根据用户账户查询已启用的用户信息
	 * 
	 * @param account
	 * @return
	 */
	@Select("select * from user where account = #{account} and is_enable = 1 and is_delete = 0")
	@Results({ @Result(property = "id", column = "id"),
			@Result(property = "roles", column = "id", many = @Many(select = "frame.server.dao.UserRoleDao.selectRoleDtosByUserId")),
			@Result(property = "permissions", column = "id", many = @Many(select = "frame.server.dao.UserPermissionDao.selectPermissionsByUserId")),
			@Result(property = "extension", column = "id", many = @Many(select = "frame.server.dao.UserExtensionDao.selectByUserId")),
			@Result(property = "warehouses", column = "id", many = @Many(select = "frame.server.dao.UserWarehouseDao.selectWarehousesByUserId")) })
	UserDto selectEnabledUserDtoByAccount(String account);

	/**
	 * 根据用户名称查询用户信息(不包含角色信息和权限信息)
	 * 
	 * @param account
	 * @return
	 */
	@Select("select * from user where is_delete = 0 and name = #{userName}")
	User selectUserByUserName(String userName);

	/**
	 * 根据用户邮箱查询用户信息(不包含角色信息和权限信息)
	 * 
	 * @param account
	 * @return
	 */
	@Select("select * from user where is_delete = 0 and email = #{email}")
	User selectUserByEmail(String email);

	/**
	 * 根据用户类型获取所有ID
	 * 
	 * @return
	 */
	@Select("select id from user where type = #{type} and is_delete = 0")
	List<String> selectSystemAdministratorsId(String type);

	/**
	 * 消耗品申请系统：根据用户ID查询用户名称
	 * 
	 * @param account
	 * @return
	 */
	@Select("select name from user where id = #{userId} and is_delete = 0")
	String a_selectUserNameByUserId(String userId);

}
