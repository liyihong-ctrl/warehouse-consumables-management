package frame.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导入文件错误Model
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportErrorDataModel {

	// 行数
	private int row;
	
	// 字段名
	private String fieldName;
	
	// 错误信息
	private String errorMsg;
	
}
