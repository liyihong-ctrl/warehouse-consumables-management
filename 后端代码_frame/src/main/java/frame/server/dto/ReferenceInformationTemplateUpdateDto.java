package frame.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基准信息更新模板Dto
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceInformationTemplateUpdateDto {

	// ID
	private String id;

	// 名称
	private String name;

	// 备注
	private String remarks;

}
