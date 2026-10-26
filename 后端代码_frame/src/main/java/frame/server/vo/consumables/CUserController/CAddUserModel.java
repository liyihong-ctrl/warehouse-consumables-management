package frame.server.vo.consumables.CUserController;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import frame.server.constant.GenderConstant;
import frame.server.constant.Messages;
import frame.server.utils.StringUtil;
import frame.server.utils.ValidatorUtil;
import lombok.Data;

/**
 * 新增用户Model
 * 
 * @author Administrator
 *
 */
@Data
public class CAddUserModel {
	
	// 仓库ID
	@NotBlank(message = "仓库ID不能为空")
	private String warehouseId;

	// 账号
	@NotBlank(message = Messages.accountCannotBeEmpty)
	@Size(min = 6, max = 50, message = Messages.accountLengthRangeError)
	private String account;

	// 名称
	@NotBlank(message = Messages.nameCannotBeEmpty)
	@Size(min = 1, max = 20, message = Messages.nameLengthRangeError)
	private String name;

	// 性别
	@NotBlank(message = Messages.genderCannotBeEmpty)
	private String gender;

	private String avatar;

	private List<String> roleIds;

	// 邮箱
	private String email;

	// 电话
	private String phone;

	public boolean validate() {
		return validateAccount() && validateGender() && validateRoles();
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

	// 校验角色Ids
	private boolean validateRoles() {
		return !(roleIds == null || roleIds.isEmpty());
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
