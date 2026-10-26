package frame.server.service.application;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageInfo;

import frame.server.domain.Warehouse;
import frame.server.dto.ApplicationDto;
import frame.server.dto.ConsumableDto;
import frame.server.vo.Page;
import frame.server.vo.application.AApplicationController.AAddApplicationModel;

public interface AApplicationService {
	
	/**
	 * 获取申请数据
	 * 
	 * @param page
	 * @return
	 */
	PageInfo<ApplicationDto> getApplications(String userId, Page page);
	
	/**
	 * 根据申请ID获取申请详细数据
	 * 
	 * @param applicationId
	 * @return
	 */
	ApplicationDto getApplicationDetailById(String applicationId);
	
	/**
	 * 检测用户是否可以申请
	 * 
	 * @param userId
	 * @param userType
	 * @return
	 */
	boolean canApplication(String userId, String userType);
	
	/**
	 * 获取可申请仓库数据
	 * 
	 * @return
	 */
	List<Warehouse> getWarehouses(String userId, String userType);
	
	/**
	 * 获取消耗品数据
	 * 
	 * @param page
	 * @return
	 */
	PageInfo<ConsumableDto> getConsumables(Page page);
	
	/**
	 * 检查仓库是否存在
	 * 
	 * @param warehouseId
	 * @return
	 */
	boolean isWarehouseExist(String warehouseId);
	
	/**
	 * 检查消耗品是否存在
	 * 
	 * @param model
	 * @return
	 */
	boolean checkConsumables(AAddApplicationModel model);
	
	/**
	 * 新增申请
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	ApplicationDto addApplication(String userId, AAddApplicationModel model);
	
	/**
	 * 取消申请
	 * 
	 * @param applicationId
	 * @return
	 */
	@Transactional
	void cancelApplication(String applicationId);
	
	/**
	 * 根据申请ID获取仓库名
	 * 
	 * @param applicationId
	 * @return
	 */
	String getWarehouseNameByApplicationId(String applicationId);
	
	/**
	 * 根据申请ID获取用户名
	 * 
	 * @param applicationId
	 * @return
	 */
	String getUserNameByApplicationId(String userId);

}
