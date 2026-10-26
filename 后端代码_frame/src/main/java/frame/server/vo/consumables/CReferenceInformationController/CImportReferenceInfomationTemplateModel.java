package frame.server.vo.consumables.CReferenceInformationController;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 基准信息模板导入Model
 * 
 * @author Administrator
 *
 */
@Data
public class CImportReferenceInfomationTemplateModel {
	
	// 仓库ID
	@NotBlank(message = "仓库ID不能为空")
	private String warehouseId;
	
	// 导入数据
	@NotBlank(message = "导入数据不能为空")
	private String excelData;

}
