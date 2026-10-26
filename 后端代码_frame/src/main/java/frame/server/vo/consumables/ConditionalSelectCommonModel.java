package frame.server.vo.consumables;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 条件检索通用Model
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionalSelectCommonModel {
	
	// 仓库ID
	private String warehouseId;
	
	// 检索条件
	private String searchStr;

}
