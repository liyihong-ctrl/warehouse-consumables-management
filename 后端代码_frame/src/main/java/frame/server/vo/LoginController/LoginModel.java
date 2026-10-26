package frame.server.vo.LoginController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import frame.server.constant.LoginTargetConstant;
import frame.server.constant.LoginTypeConstant;
import frame.server.constant.Messages;
import frame.server.utils.StringUtil;
import frame.server.utils.ValidatorUtil;
import lombok.Data;

@Data
public class LoginModel {

	// 登录系统
	private String target;

	// 账号
	@NotBlank(message = Messages.accountOrPasswordError)
	private String account;

	// 密码
	@NotBlank(message = Messages.accountOrPasswordError)
	@Size(min = 6, max = 20, message = Messages.accountOrPasswordError)
	private String password;

	// 记住我
	private boolean rememberMe;

	// 登录类型
	private String type;

	// 校验登录目标
	public boolean validateTarget() {
		return !StringUtil.isBlank(target) && (target.equals(LoginTargetConstant.CONSUMABLES)
				|| target.equals(LoginTargetConstant.APPLICATION) || target.equals(LoginTargetConstant.WAREHOUSE));
	}
	
	// 校验账号
	public boolean validateAccount() {
		return ValidatorUtil.isMobile(account) || ValidatorUtil.isEmail(account);
	}
	
	// 校验密码
	public boolean validatePassword() {
		return ValidatorUtil.isPassword(password);
	}

	// 校验登录类型
	public boolean validateType() {
		return !StringUtil.isBlank(type) && (type.equals(LoginTypeConstant.WEB) || type.equals(LoginTypeConstant.APPLET)
				|| type.equals(LoginTypeConstant.APP));
	}

}
