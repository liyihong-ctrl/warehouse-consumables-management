package frame.server.service.warehouse;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageInfo;

import frame.server.domain.User;
import frame.server.domain.Warehouse;
import frame.server.dto.WarehouseDto;
import frame.server.vo.Page;
import frame.server.vo.warehouse.WWarehouseController.AddWarehouseModel;
import frame.server.vo.warehouse.WWarehouseController.EditWarehouseModel;
import frame.server.vo.warehouse.WWarehouseController.UpdateAssignUserModel;

public interface WWarehouseService {

	/**
	 * 获取仓库信息
	 * 
	 * @param page
	 * @return
	 */
	List<Warehouse> getAllWarehouses();

	/**
	 * 获取仓库信息(分页)
	 * 
	 * @param page
	 * @return
	 */
	PageInfo<WarehouseDto> getWarehouses(Page page);

	/**
	 * 根据仓库ID获取仓库信息
	 * 
	 * @param warehouseId
	 * @return
	 */
	WarehouseDto getWarehouseByWarehouseId(String warehouseId);

	/**
	 * 查询仓库名称是否重复
	 * 
	 * @param warehouseName
	 * @return
	 */
	boolean isWarehouseNameRepeat(String warehouseName);

	/**
	 * 查询仓库Code是否重复
	 * 
	 * @param warehouseCode
	 * @return
	 */
	boolean isWarehouseCodeRepeat(String warehouseCode);

	/**
	 * 新增仓库
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	WarehouseDto addWarehouse(AddWarehouseModel model);

	/**
	 * 更新仓库信息
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	WarehouseDto updateWarehouse(EditWarehouseModel model);

	/**
	 * 检查仓库是否可以被删除或停用
	 * 
	 * @param warehouseId
	 * @return
	 */
	boolean isWarehouseCanDeleteOrDisable(String warehouseId);

	/**
	 * 更新仓库状态
	 * 
	 * @param warehouseId
	 */
	@Transactional
	void updateWarehouseState(String warehouseId);

	/**
	 * 删除仓库 仅更新当前删除仓库的状态
	 * 
	 * @param warehouseId
	 */
	@Transactional
	void deleteWarehouse(String warehouseId);
	
	/**
	 * 获取仓库管理员信息
	 * 
	 * @return
	 */
	List<User> getAllWarehouseManagers();
	
	/**
	 * 检查仓库管理员Ids
	 * 
	 * @param userIds
	 * @return
	 */
	boolean checkWarehouseManagerIds(List<String> userIds);
	
	/**
	 * 分配用户
	 * 
	 * @param model
	 */
	@Transactional
	WarehouseDto updateAssignUsers(UpdateAssignUserModel model);

}
