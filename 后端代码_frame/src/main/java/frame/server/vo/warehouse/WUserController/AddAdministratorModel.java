package frame.server.vo.warehouse.WUserController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import frame.server.constant.GenderConstant;
import frame.server.constant.UserTypeEnum;
import frame.server.utils.StringUtil;
import frame.server.utils.ValidatorUtil;
import lombok.Data;

/**
 * 添加管理员Model
 * 
 * @author Administrator
 *
 */
@Data
public class AddAdministratorModel {

	// 管理员类型
	// 与前端对应: 1: 系统管理员 2: 仓库管理员 8：申请人员
	private String type;

	// 申请人员：部门
	private String department;

	// 账号
	@NotBlank(message = "账号不能为空")
	@Size(min = 6, max = 50, message = "账号长度范围为6~50")
	private String account;

	// 名称
	@NotBlank(message = "姓名不能为空")
	@Size(min = 1, max = 20, message = "姓名长度范围为1~20")
	private String name;

	// 性别
	@NotBlank(message = "性别不能为空")
	private String gender;

	// 头像
	private String avatar;

	// 邮箱
	private String email;

	// 电话
	private String phone;

	public boolean validate() {
		return validateType() && validateDepartment() && validateAccount() && validateGender() && validateEmail() && validatePhone();
	}

	// 校验用户类型
	public boolean validateType() {
		return !StringUtil.isBlank(type) && (type.equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue()))
				|| type.equals(Integer.toString(UserTypeEnum.WAREHOUSEMANAGER.getValue()))
				|| type.equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue())));
	}

	public boolean validateDepartment() {
		if (!validateType()) {
			return false;
		}

		if (type.equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue())) && StringUtil.isBlank(department)) {
			return false;
		}

		return true;
	}

	// 校验账号
	public boolean validateAccount() {
		return !StringUtil.isBlank(account) && (ValidatorUtil.isMobile(account) || ValidatorUtil.isEmail(account));
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

}
