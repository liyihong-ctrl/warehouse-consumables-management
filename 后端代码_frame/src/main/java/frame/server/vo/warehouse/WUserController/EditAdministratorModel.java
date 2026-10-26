package frame.server.vo.warehouse.WUserController;

import javax.validation.constraints.NotBlank;

import frame.server.constant.Messages;
import frame.server.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 编辑管理员Model
 * 
 * @author Administrator
 *
 */
@Getter
@Setter
public class EditAdministratorModel extends AddAdministratorModel {

	// 用户ID
	@NotBlank(message = Messages.badRequest)
	private String id;
	
	@Override
	public boolean validate() {
		return !StringUtil.isBlank(id) && super.validate();
	}
	
}
