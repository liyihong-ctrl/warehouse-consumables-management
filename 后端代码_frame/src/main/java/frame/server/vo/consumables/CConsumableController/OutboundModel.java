package frame.server.vo.consumables.CConsumableController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import frame.server.constant.Messages;
import frame.server.constant.ConsumableOperateTypeConstant;
import frame.server.constant.ConsumableMaxCountConstant;
import lombok.Data;

/**
 * 出库Model
 * 
 * @author Administrator
 *
 */
@Data
public class OutboundModel {
	
	// ID
	@NotBlank(message = Messages.badRequest)
	private String id;

	// 仓库ID
	@NotBlank(message = Messages.badRequest)
	private String warehouseId;
	
	// 出库类型
	private short type;

	// 出库数
	private int outboundCount;
	
	// 备注
	@Size(min = 0, max = 50, message = "备注长度范围为0~50")
	private String remarks;

	public boolean validate() {
		return validateCount() && validateType();
	}
	
	// 校验出库类型
	private boolean validateType() {
		return type == ConsumableOperateTypeConstant.NORMAL_DELIVERY || type == ConsumableOperateTypeConstant.REDUCED_WORK;
	}

	// 校验数量
	private boolean validateCount() {
		return 0 < outboundCount && outboundCount < ConsumableMaxCountConstant.MAXINVENTORYCOUNT;
	}

}
