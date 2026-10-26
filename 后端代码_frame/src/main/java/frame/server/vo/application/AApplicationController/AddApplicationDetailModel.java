package frame.server.vo.application.AApplicationController;

import lombok.Data;

/**
 * 消耗品申请详细Model
 * 
 * @author Administrator
 *
 */
@Data
public class AddApplicationDetailModel {
	
	// 消耗品ID
	private String id;
	
	// 申请数
	private int applicationCount;

}
