package frame.server.service;

import frame.server.domain.Warehouse;
import frame.server.dto.UserDto;

public interface BaseService {

	/**
	 * 根据用户账户获取用户信息
	 * 
	 * @param account
	 * @return
	 */
	UserDto getUserDtoByAccount(String account);
	
	/**
	 * 根据用户ID和仓库ID获取仓库数据
	 * 
	 * @param userId
	 * @param warehouseId
	 * @return
	 */
	Warehouse getWarehouseByUserIdAndWarehouseId(String userId, String warehouseId);

}
