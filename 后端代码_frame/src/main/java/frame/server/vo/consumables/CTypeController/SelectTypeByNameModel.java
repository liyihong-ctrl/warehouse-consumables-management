package frame.server.vo.consumables.CTypeController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 根据类型名称检索类型Model
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectTypeByNameModel {

	private String warehouseId;
	
	private String typeName;
	
}
