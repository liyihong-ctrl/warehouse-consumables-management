package frame.server.domain;

import java.util.Date;

import frame.server.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户表
 * 
 * @author Administrator
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	// id
	private String id;
	
	// 账户
	private String account;
	
	// 密码
	private String password;
	
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
	
	// 注册日期
	private Date registerTime;
	
	// 是否启用
	private boolean isEnable;
	
	// 是否删除
	private boolean isDelete;
	
	public String getRegisterTime() {
		if(registerTime != null) {
			return DateUtil.convertDateToString(registerTime, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI);
		}
		return "";
	}
	
	public boolean getIsEnable() {
		return isEnable;
	}
	
	public void setIsEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
	
	public boolean getIsDelete() {
		return isDelete;
	}
	
	public void setIsDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

}
