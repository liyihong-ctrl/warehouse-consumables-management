package frame.server.vo.consumables.CUserController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取全部用户信息(分页)检索Model
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUsersSelectModel {

	private String warehouseId;
	
	private String searchStr;

}
