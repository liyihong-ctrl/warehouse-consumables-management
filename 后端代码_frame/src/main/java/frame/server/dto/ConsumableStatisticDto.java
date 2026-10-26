package frame.server.dto;

import lombok.Data;

/**
 * 消耗品统计数据Dto
 * 
 * @author Administrator
 *
 */
@Data
public class ConsumableStatisticDto {

	// 消耗品ID
	private String id;
	
	// 消耗品Code
	private String code;
	
	// 消耗品名称
	private String name;
	
	// 操作日期
	private String operateTime;
	
	// 操作数
	private int operateCount;
	
}
