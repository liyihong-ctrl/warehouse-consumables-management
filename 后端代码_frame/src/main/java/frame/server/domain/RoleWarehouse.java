package frame.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色仓库表
 * 消耗品管理系统中：角色仅属于某一仓库
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleWarehouse {

	private String roleId;
	
	private String warehouseId;
	
}
