package frame.server.vo.consumables.CApplicationController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 审批Model
 * 
 * @author Administrator
 *
 */
@Data
public class ApprovalModel {
	
	// 仓库ID
	@NotBlank(message = "仓库ID不能为空")
	private String warehouseId;
	
	// 申请ID
	@NotBlank(message = "申请ID不能为空")
	private String applicationId;
	
	// 审批状态
	private int approvalState;
	
	// 审批说明
	@Size(min = 0, max = 50, message = "审批说明长度范围为0~50")
	private String approvalInstructions;

}
