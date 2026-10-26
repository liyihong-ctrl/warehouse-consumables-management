package frame.server.vo.consumables.CConsumableController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 根据仓库ID和消耗品ID检索Model
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CSearchApplicationModel {
	
	private String warehouseId;
	
	private String consumableId;

}
