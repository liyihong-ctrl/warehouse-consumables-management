package frame.server.vo.application.AUserInfoController;

import javax.validation.constraints.NotBlank;

import frame.server.constant.Messages;
import frame.server.utils.StringUtil;
import frame.server.utils.ValidatorUtil;
import lombok.Data;

/**
 * 消耗品申请系统：更新用户密码Model
 * 
 * @author Administrator
 *
 */
@Data
public class AEditUserPasswordModel {

	// 旧密码
	@NotBlank(message = Messages.badRequest)
	private String oldPass;

	// 新密码
	@NotBlank(message = Messages.badRequest)
	private String newPass;

	// 确认密码
	@NotBlank(message = Messages.badRequest)
	private String confirmPass;

	public boolean validate() {
		return validateOldPass() && validateNewPass() && validateConfirmPass();
	}

	private boolean validateOldPass() {
		return !StringUtil.isBlank(oldPass) && ValidatorUtil.isPassword(oldPass);
	}

	private boolean validateNewPass() {
		return !StringUtil.isBlank(newPass) && ValidatorUtil.isPassword(newPass);
	}

	private boolean validateConfirmPass() {
		return !StringUtil.isBlank(confirmPass) && ValidatorUtil.isPassword(confirmPass) && newPass.equals(confirmPass);
	}

}