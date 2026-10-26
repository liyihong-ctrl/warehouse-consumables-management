package frame.server.service.warehouse;

public interface WHomeService {

	/**
	 * 获取系统管理员和仓库管理员总数
	 * 
	 * @return
	 */
	int getUserCount();
	
	/**
	 * 获取仓库总数
	 * 
	 * @return
	 */
	int getWarehouseCount();
	
}
