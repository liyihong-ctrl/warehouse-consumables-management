package frame.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基准信息模板详细表
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceInformationTemplateDetail {
	
	// ID
	private String id;
	
	// 模板ID
	private String referenceInformationTemplateId;

	// 基准信息名
	private String referenceKey;
	
	// 显示顺序
	private int displayOrder;

}
