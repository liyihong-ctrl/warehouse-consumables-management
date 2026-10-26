package frame.server.vo.consumables.CConsumableController;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 批量导入消耗品Model
 * 
 * @author Administrator
 *
 */
@Data
public class CImportConsumableModel {
	
	// 仓库ID
	@NotBlank(message = "仓库ID不能为空")
	private String warehouseId;
	
	// 导入数据
	@NotBlank(message = "导入数据不能为空")
	private String excelData;

}
