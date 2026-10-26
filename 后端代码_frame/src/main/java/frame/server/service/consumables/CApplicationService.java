package frame.server.service.consumables;

import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageInfo;

import frame.server.domain.Application;
import frame.server.dto.ApplicationDto;
import frame.server.vo.Page;
import frame.server.vo.consumables.CApplicationController.ApprovalModel;

public interface CApplicationService {
	
	/**
	 * 获取全部申请(分页)
	 * 
	 * @return
	 */
	PageInfo<ApplicationDto> getApplications(Page page);
	
	/**
	 * 根据申请ID获取申请
	 * 
	 * @param applicationId
	 * @return
	 */
	Application getApplication(String applicationId);
	
	/**
	 * 设置申请状态为审核中
	 * 
	 * @param applicationId
	 */
	@Transactional
	void setApplicationApprovalUnderReview(String applicationId);
	
	/**
	 * 审批申请
	 * 
	 * @param model
	 */
	@Transactional
	void doApproval(ApprovalModel model);

}
