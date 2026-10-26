package frame.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 申请人员可申请仓库配置表
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWarehouseApplicationSetting {
	
	// 用户ID
	private String userId;
	
	// 仓库ID
	private String warehouseId;

}
