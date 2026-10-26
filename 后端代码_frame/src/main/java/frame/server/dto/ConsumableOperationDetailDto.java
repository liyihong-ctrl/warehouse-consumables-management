package frame.server.dto;

import frame.server.constant.ConsumableOperateTypeConstant;
import frame.server.domain.ConsumableOperationDetail;
import lombok.Getter;
import lombok.Setter;

/**
 * 操作履历详情Dto
 * 
 * @author Administrator
 *
 */
@Getter
@Setter
public class ConsumableOperationDetailDto extends ConsumableOperationDetail{
	
	// 消耗品Code
	private String consumableCode;
	
	// 消耗品名称
	private String consumableName;
	
	// 消耗品简介
	private String consumableSummary;
	
	// 消耗品类型名
	private String consumableTypeName;
	
	// 操作类型名称
	@SuppressWarnings("unused")
	private String operateName;
	
	public String getOperateName() {
		return ConsumableOperateTypeConstant.getOperationName(super.getOperationType());
	}
	
	public void setOperateName(String operateName) {
		this.operateName = operateName;
	}
	
}
