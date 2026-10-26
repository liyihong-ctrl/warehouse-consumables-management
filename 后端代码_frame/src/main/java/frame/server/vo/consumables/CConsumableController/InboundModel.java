package frame.server.vo.consumables.CConsumableController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import frame.server.constant.ConsumableMaxCountConstant;
import frame.server.constant.ConsumableOperateTypeConstant;
import frame.server.constant.Messages;
import lombok.Data;

/**
 * 入库Model
 * 
 * @author Administrator
 *
 */
@Data
public class InboundModel {

	// ID
	@NotBlank(message = Messages.badRequest)
	private String id;

	// 仓库ID
	@NotBlank(message = Messages.badRequest)
	private String warehouseId;
	
	// 入库类型
	private short type;

	// 入库数
	private int inboundCount;
	
	// 入库价格
	private double inboundPrice;
	
	// 备注
	@Size(min = 0, max = 50, message = "备注长度范围为0~50")
	private String remarks;

	public boolean validate() {
		return validateType() && validateCount() && validatePrice();
	}
	
	// 校验入库类型
	private boolean validateType() {
		return type == ConsumableOperateTypeConstant.INBOUND_A || type == ConsumableOperateTypeConstant.INBOUND_B;
	}

	// 校验数量
	private boolean validateCount() {
		return 0 < inboundCount && inboundCount < ConsumableMaxCountConstant.MAXINVENTORYCOUNT;
	}

	// 校验价格
	private boolean validatePrice() {
		return 0 <= inboundPrice && inboundPrice < ConsumableMaxCountConstant.MAXPRICECOUNT;
	}

}
