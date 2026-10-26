package frame.server.dto;

import java.math.BigDecimal;
import java.util.List;

import frame.server.domain.Consumable;
import frame.server.domain.ConsumableReferenceInformation;
import lombok.Getter;
import lombok.Setter;

/**
 * 消耗品Dto
 * 
 * @author Administrator
 *
 */
@Getter
@Setter
public class ConsumableDto extends Consumable {

	// 仓库名称
	private String warehouseName;

	// 类型名称
	private String typeName;

	// 总价格
	private double totalPrice;

	// 基准信息
	private List<ConsumableReferenceInformation> referenceInformations;

	// 入库批次
	private List<ConsumableInboundBatchDto> inboundBatchs;

	public double getTotalPrice() {
		totalPrice = 0;
		for (ConsumableInboundBatchDto consumableInboundBatch : inboundBatchs) {
			BigDecimal count = new BigDecimal(Integer.toString(consumableInboundBatch.getCount()));
			BigDecimal price = new BigDecimal(Double.toString(consumableInboundBatch.getPrice()));
			totalPrice += count.multiply(price).doubleValue();
		}
		return totalPrice;
	}

}
