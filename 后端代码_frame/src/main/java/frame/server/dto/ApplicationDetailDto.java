package frame.server.dto;

import frame.server.domain.ApplicationDetail;
import lombok.Getter;
import lombok.Setter;

/**
 * 消耗品申请详细Dto
 * 
 * @author Administrator
 *
 */
@Getter
@Setter
public class ApplicationDetailDto extends ApplicationDetail {
	
	private String code;

	private String consumableName;
	
	private String typeName;
	
}
