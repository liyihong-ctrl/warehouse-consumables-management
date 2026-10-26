package frame.server.vo.consumables.CSupplierController;

import javax.validation.constraints.NotBlank;

import frame.server.constant.Messages;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CEditSupplierModel extends CAddSupplierModel {

	// 供应商ID
	@NotBlank(message = Messages.badRequest)
	private String id;
}
