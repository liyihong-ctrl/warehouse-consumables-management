package frame.server.vo.application.AApplicationController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.alibaba.fastjson.JSONObject;

import frame.server.constant.Messages;
import lombok.Data;

/**
 * 添加申请Model
 * 
 * @author Administrator
 *
 */
@Data
public class AAddApplicationModel {

	// 申请名
	@NotBlank(message = "申请名不能为空")
	@Size(min = 1, max = 20, message = "申请名长度范围为1~20")
	private String name;

	// 申请备注
	@Size(min = 0, max = 50, message = "申请备注长度范围为0~50")
	private String remarks;

	// 仓库ID
	@NotBlank(message = Messages.badRequest)
	private String warehouseId;

	// 消耗品
	private JSONObject consumables;
	
	public boolean validate() {
		return validateConsumables();
	}
	
	private boolean validateConsumables() {
		if(consumables == null || consumables.isEmpty()) {
			return false;
		}
		return true;
	}

}
