package frame.server.vo.LoginController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import frame.server.constant.GenderConstant;
import frame.server.utils.StringUtil;
import frame.server.utils.ValidatorUtil;
import lombok.Data;

/**
 * 注册Model
 * 
 * @author Administrator
 *
 */
@Data
public class RegisterModel {

	// 名称
	@NotBlank(message = "姓名不能为空")
	@Size(min = 2, max = 6, message = "姓名长度范围为2~6")
	private String name;

	// 性别
	@NotBlank(message = "性别不能为空")
	private String gender;

	// 邮箱
	private String email;

	// 电话
	private String phone;

	// 账号
	@NotBlank(message = "账号不能为空")
	@Size(min = 6, max = 50, message = "账号长度范围为6~50")
	private String account;

	// 密码
	@NotBlank(message = "密码不能为空")
	@Size(min = 6, max = 20, message = "账号长度范围为6~20")
	private String password;

	// 部门
	@NotBlank(message = "所属部门不能为空")
	@Size(min = 1, max = 99, message = "部门长度范围为1~99")
	private String department;

	public boolean validate() {
		return validateGender() && validateEmail() && validatePhone() && validateAccount() && validatePassword();
	}

	// 校验性别
	public boolean validateGender() {
		return !StringUtil.isBlank(gender)
				&& (gender.equals(GenderConstant.MALE.getName()) || gender.equals(GenderConstant.FEMALE.getName()));
	}

	// 校验邮箱
	public boolean validateEmail() {
		return StringUtil.isBlank(email) || ValidatorUtil.isEmail(email);
	}

	// 校验电话
	public boolean validatePhone() {
		return StringUtil.isBlank(phone) || ValidatorUtil.isMobile(phone);
	}

	// 校验账号
	public boolean validateAccount() {
		return !StringUtil.isBlank(account) && (ValidatorUtil.isMobile(account) || ValidatorUtil.isEmail(account));
	}

	// 校验密码
	public boolean validatePassword() {
		return !StringUtil.isBlank(password) && ValidatorUtil.isPassword(password);
	}

}
