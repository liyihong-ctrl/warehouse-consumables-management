package frame.server.dto;

import java.util.List;

import frame.server.domain.Type;
import lombok.Getter;
import lombok.Setter;

/**
 * 消耗品类型Dto
 * 
 * @author Administrator
 *
 */
@Getter
@Setter
public class TypeDto extends Type {

	// 子类型
	private List<TypeDto> children;

	// 当前类型下消耗品数量
	private int curTypeConsumableCount;

	private int totalAmount;

	public int getTotalAmount() {
		totalAmount = curTypeConsumableCount;

		if (children == null || children.isEmpty()) {
			return totalAmount;
		}

		for (TypeDto type : children) {
			totalAmount += type.getTotalAmount();
		}

		return totalAmount;
	}

}
