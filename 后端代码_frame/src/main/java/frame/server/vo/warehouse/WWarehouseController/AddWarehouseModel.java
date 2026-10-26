package frame.server.vo.warehouse.WWarehouseController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import frame.server.utils.ValidatorUtil;
import lombok.Data;

/**
 * 添加仓库Model
 * 
 * @author Administrator
 *
 */
@Data
public class AddWarehouseModel {
	
	// Code
	@NotBlank(message = "仓库编号不能为空")
	@Size(min = 1, max = 20, message = "仓库编号长度范围为1~20")
	private String code;
	
	// 名称
	@NotBlank(message = "仓库名称不能为空")
	@Size(min = 1, max = 20, message = "仓库名称长度范围为1~20")
	private String name;
	
	// 照片
	@NotBlank(message = "仓库照片不能为空")
	private String pictureUrl;
	
	// 简介
	@Size(min = 0, max = 200, message = "仓库简介长度范围为0~200")
	private String summary;
	
	public boolean validate() {
		return vaildateCode();
	}
	
	private boolean vaildateCode() {
		return ValidatorUtil.isWarehouseCode(code);
	}

}
