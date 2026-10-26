package frame.server.vo.consumables.CTypeController;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 接收前端传来的Excel字符串Model
 * 
 * @author Administrator
 *
 */
@Data
public class CImportTypeModel {
	
	// 仓库ID
	@NotBlank(message = "仓库ID不能为空")
	private String warehouseId;
	
	// 导入数据
	@NotBlank(message = "导入数据不能为空")
	private String excelData;

}
