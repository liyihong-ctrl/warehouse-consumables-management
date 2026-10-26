package frame.server.vo.consumables.CTypeController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import frame.server.constant.Messages;
import lombok.Data;

/**
 * 新增消耗品类型Model
 * 
 * @author Administrator
 *
 */
@Data
public class CAddConsumableTypeModel {

	// 仓库ID
	@NotBlank(message = Messages.badRequest)
	private String warehouseId;

	// 父类型ID
	private String parentId;

	// 类型名称
	@NotBlank(message = "类型名称不能为空")
	@Size(min = 1, max = 20, message = "类型名称长度范围为1~20")
	private String name;

	// 备注
	@Size(min = 0, max = 50, message = "角色备注长度范围为0~50")
	private String remarks;

}
