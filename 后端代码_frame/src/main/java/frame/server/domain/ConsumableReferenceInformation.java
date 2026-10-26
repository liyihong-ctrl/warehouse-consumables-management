package frame.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消耗品基准信息表
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumableReferenceInformation {
	
	// ID
	private String id;
	
	// 消耗品ID
	private String consumableId;
	
	// 基准信息名
	private String referenceKey;
	
	// 基准信息值
	private String referenceValue;
	
	// 排序
	private int displayOrder;

}
