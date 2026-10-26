package frame.server.vo.consumables.CTypeController;

import javax.validation.constraints.NotBlank;

import frame.server.constant.Messages;
import lombok.Getter;
import lombok.Setter;

/**
 * 更新类型Model
 * 
 * @author Administrator
 *
 */
@Getter
@Setter
public class CEditConsumableTypeModel extends CAddConsumableTypeModel {
	
	// 类型ID
	@NotBlank(message = Messages.badRequest)
	private String id;

}
