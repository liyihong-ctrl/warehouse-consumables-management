package frame.server.vo.warehouse.WWarehouseController;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 分配人员Model
 * 
 * @author Administrator
 *
 */
@Data
public class UpdateAssignUserModel {

	// 仓库ID
	@NotBlank(message = "仓库ID不能为空")
	private String warehouseId;
	
	// 用户IDs
	private List<String> userIds;

}
