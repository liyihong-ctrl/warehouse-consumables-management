package frame.server.dto;

import lombok.Data;

/**
 * 用户简单信息Dto
 * 
 * @author Administrator
 *
 */
@Data
public class UserSimpleInfo {
	
	// id
	private String id;
	
	// 名称
	private String name;
	
	// 性别
	private String gender;
	
	// 头像
	private String avatar;

}
