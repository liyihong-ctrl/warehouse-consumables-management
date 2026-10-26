package frame.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户可更新信息实体类
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
	
	// id
	private String id;
	
	// 名称
	private String name;
	
	// 性别
	private String gender;
	
	// 头像
	private String avatar;
	
	// 邮箱
	private String email;
	
	// 电话
	private String phone;
	
	// 用户类型
	private String type;

}
