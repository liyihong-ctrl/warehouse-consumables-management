package frame.server.vo.consumables.CRoleController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取全部角色信息(分页)检索Model
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRolesSelectModel {

	private String warehouseId;
	
	private String roleName;
	
}
