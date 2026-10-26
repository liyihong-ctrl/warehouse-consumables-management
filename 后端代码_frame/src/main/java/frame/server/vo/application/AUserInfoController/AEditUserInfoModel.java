package frame.server.vo.application.AUserInfoController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import frame.server.constant.GenderConstant;
import frame.server.constant.Messages;
import frame.server.utils.StringUtil;
import frame.server.utils.ValidatorUtil;
import lombok.Data;

/**
 * 消耗品申请系统：用户更新信息Model
 * 
 * @author Administrator
 *
 */
@Data
public class AEditUserInfoModel {
	
	// ID
	@NotBlank(message = Messages.badRequest)
	private String id;

	// 名称
	@NotBlank(message = "姓名不能为空")
	@Size(min = 1, max = 20, message = "姓名长度范围为1~20")
	private String name;

	// 性别
	@NotBlank(message = "性别不能为空")
	private String gender;

	private String avatar;

	private String email;

	private String phone;
	
	// 部门
	private String department;

	public boolean validate() {
		return validateGender() && validateEmail() && validatePhone();
	}

	// 校验性别
	private boolean validateGender() {
		return !StringUtil.isBlank(gender)
				&& (gender.equals(GenderConstant.MALE.getName()) || gender.equals(GenderConstant.FEMALE.getName()));
	}

	// 校验邮箱
	private boolean validateEmail() {
		return StringUtil.isBlank(email) || ValidatorUtil.isEmail(email);
	}

	// 校验电话
	private boolean validatePhone() {
		return StringUtil.isBlank(phone) || ValidatorUtil.isMobile(phone);
	}
	
	// 校验部门
	public boolean validateDepartment() {
		if(StringUtil.isBlank(department)) {
			return false;
		} else if(department.length() > 99) {
			return false;
		} else {
			return true;
		}
	}

}
