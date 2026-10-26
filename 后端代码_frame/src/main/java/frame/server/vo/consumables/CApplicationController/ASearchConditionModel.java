package frame.server.vo.consumables.CApplicationController;

import javax.validation.constraints.NotBlank;

import frame.server.constant.Messages;
import lombok.Data;

/**
 * 申请检索Model
 * 
 * @author Administrator
 *
 */
@Data
public class ASearchConditionModel {
	
	// 仓库ID
	@NotBlank(message = Messages.badRequest)
	private String warehouseId;

	// 检索类型
	private String searchType;

	// 检索数据
	private String searchStr;

	// 开始日期
	private String startTime;

	// 结束日期
	private String endTime;

}
