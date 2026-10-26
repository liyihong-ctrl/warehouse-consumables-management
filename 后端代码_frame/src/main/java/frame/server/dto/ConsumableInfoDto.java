package frame.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新消耗品信息Dto
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumableInfoDto {

	// ID
	private String id;

	// 仓库ID
	private String warehouseId;

	// 类型ID
	private String typeId;

	// 编码：根据code值生成条形码
	private String code;

	// 名称
	private String name;

	// 数量
	private int count;

	// 安全在工数
	private int safetyCount;

	// 价格
	private double price;

	// 简介
	private String summary;

}
