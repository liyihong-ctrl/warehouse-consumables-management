package frame.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 采购申请详情表
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDetail {
	
	// ID
	private String id;
	
	// 申请ID
	private String applicationId;
	
	// 消耗品ID
	private String consumableId;
	
	// 申请数
	private int applicateCount;
	
	// 备注
	private String remarks;
	
	// 申请人：是否取消申请
	private boolean isCancelApplication;
	
	// 申请人：是否删除
	private boolean isDelete;
	
	// 管理者：审批状态
	private short approvalStatus;
	
	// 管理者：审批说明
	private String ApprovalInstructions;

	public boolean getIsCancelApplication() {
		return isCancelApplication;
	}

	public void setIsCancelApplication(boolean isCancelApplication) {
		this.isCancelApplication = isCancelApplication;
	}
	
	public boolean getIsDelete() {
		return isDelete;
	}
	
	public void setIsDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

}
