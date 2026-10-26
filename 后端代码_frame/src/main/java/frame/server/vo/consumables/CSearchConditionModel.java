package frame.server.vo.consumables;

import javax.validation.constraints.NotBlank;

import frame.server.constant.Messages;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消耗品检索Model
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CSearchConditionModel {

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
	
	// 查看报警数据
	private boolean isViewAlarmData;
	
	public boolean getIsViewAlarmData() {
		return isViewAlarmData;
	}
	
	public void setIsViewAlarmData(boolean isViewAlarmData) {
		this.isViewAlarmData = isViewAlarmData;
	}
	
}
