package frame.server.vo.consumables.CReferenceInformationController;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.alibaba.fastjson.JSONObject;

import frame.server.utils.StringUtil;
import frame.server.vo.consumables.ReferenceInfomationTemplateDetailModel;
import lombok.Data;

/**
 * 添加基准信息模板Model
 * 
 * @author Administrator
 *
 */
@Data
public class CAddReferenceInformationTemplateModel {

	// 仓库ID
	@NotBlank(message = "仓库ID不能为空")
	private String warehouseId;

	// 名称
	@NotBlank(message = "基准信息模板名称不能为空")
	@Size(min = 1, max = 20, message = "基准信息模板名称长度范围为1~20")
	private String name;

	// 备注
	@Size(min = 0, max = 255, message = "基准信息模板备注长度范围为0~255")
	private String remarks;

	// 基准信息
	private JSONObject details;
	
	public boolean validate() {
		return validateDetails();
	}
	
	private boolean validateDetails() {
		if(details == null || details.isEmpty()) {
			return true;
		}
		
		List<ReferenceInfomationTemplateDetailModel> referenceInformationList = JSONObject
				.parseArray(details.getString("details"), ReferenceInfomationTemplateDetailModel.class);
		
		for(ReferenceInfomationTemplateDetailModel referenceInformation : referenceInformationList) {
			if(StringUtil.isBlank(referenceInformation.getKey())) {
				return true;
			}
			
			String key = referenceInformation.getKey().trim();
			if(key.length() > 255) {
				return false;
			}
		}
		
		return true;
	}

}
