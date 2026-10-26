package frame.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消耗品类型与仓库关联关系表
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeWarehouse {
	
	private String warehouseId;

	private String typeId;
	
}
