package frame.server.vo.consumables.CStatisticsController;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.github.pagehelper.StringUtil;

import frame.server.constant.StatisticConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取消耗品统计信息Model
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CGetConsumableStatisticsSearchModel {

	// 仓库ID
	@NotBlank(message = "仓库ID不能为空")
	private String warehouseId;

	// 统计类型
	// 1：出库
	// 2：在工减少
	// 3. 入库
	// 4. 综合数量
	private short staticType;

	// 消耗品IDs
	private List<String> consumableIds;

	// 开始日期
	@NotBlank(message = "开始日期不能为空")
	private String startTime;

	// 结束日期
	@NotBlank(message = "结束日期不能为空")
	private String endTime;

	public boolean validate() {
		return validateConsumableIds() && validateStatisticType() && validateStartTime() && validateEndTime();
	}

	private boolean validateConsumableIds() {
		if (consumableIds == null || consumableIds.isEmpty()) {
			return false;
		}
		return true;
	}

	private boolean validateStatisticType() {
		return staticType == StatisticConstant.OUTBOUND_QUANTITY || staticType == StatisticConstant.REDUCED_WORK
				|| staticType == StatisticConstant.INBOUND_QUANTITY;
	}

	private boolean validateStartTime() {
		return !StringUtil.isEmpty(startTime);
	}

	private boolean validateEndTime() {
		return !StringUtil.isEmpty(endTime);
	}

}
