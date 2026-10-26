package frame.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仓库更新Dto
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseUpdateDto {
	
	// ID
	private String id;
	
	// Code
	private String code;
	
	// 名称
	private String name;
	
	// 照片
	private String pictureUrl;
	
	// 简介
	private String summary;

}
