package frame.server.vo.consumables.CConsumableController;

import javax.validation.constraints.NotBlank;

import frame.server.constant.Messages;
import lombok.Getter;
import lombok.Setter;

/**
 * 更新消耗品Model
 * 
 * @author Administrator
 *
 */
@Getter
@Setter
public class CEditConsumableModel extends CAddConsumableModel {

	// ID
	@NotBlank(message = Messages.badRequest)
	private String id;
	
}
