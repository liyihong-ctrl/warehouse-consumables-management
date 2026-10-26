package frame.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户扩展信息表
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExtension {

	private String userId;
	
	// 初始化密码
	private String initializedPassword;
	
	// 消耗品申请系统：所属部门
	private String department;
	
}
