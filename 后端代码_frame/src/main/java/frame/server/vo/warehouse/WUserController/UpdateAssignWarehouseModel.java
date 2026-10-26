package frame.server.vo.warehouse.WUserController;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 更新仓库管理员管理仓库Model
 * 
 * @author Administrator
 *
 */
@Data
public class UpdateAssignWarehouseModel {

	// 用户ID
	@NotBlank(message = "用户ID不能为空")
	private String userId;
	
	// 仓库IDs
	private List<String> warehouseIds;
	
}
