package frame.server.service;

import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageInfo;

import frame.server.domain.Operation;
import frame.server.dto.OperationDto;
import frame.server.vo.Page;

public interface OperationService {

	/**
	 * 根据用户账号获取用户ID
	 * 
	 * @param account
	 * @return
	 */
	String getUserId(String account);
	
	/**
	 * 保存操作履历
	 * 
	 * @param operationHistory
	 */
	@Transactional
	void saveOperation(Operation operation);
	
	/**
	 * 获取操作履历数据(分页)
	 * 
	 * @param page
	 * @return
	 */
	PageInfo<OperationDto> getLogs(Page page);

}
