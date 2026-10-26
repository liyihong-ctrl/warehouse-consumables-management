package frame.server.vo.consumables.CUserController;

import lombok.Data;

/**
 * 批量导入用户Model
 * 
 * @author Administrator
 *
 */
@Data
public class CImportUserDetailModel {

	// 名称
	private String name;

	// 账号
	private String account;

	// 性别
	private String gender;

	// 邮箱
	private String email;

	// 电话
	private String phone;
	
	// 角色名称
	private String roleNames;

	// 是否启用
	private String isEnable;

	public String getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(String isEnable) {
		this.isEnable = isEnable;
	}

}
