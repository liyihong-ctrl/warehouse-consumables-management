package frame.server.dto;

import java.util.List;

import frame.server.domain.ReferenceInformationTemplate;
import frame.server.domain.ReferenceInformationTemplateDetail;
import lombok.Getter;
import lombok.Setter;

/**
 * 基准信息模板Dto
 * 
 * @author Administrator
 *
 */
@Getter
@Setter
public class ReferenceInformationTemplateDto extends ReferenceInformationTemplate {

	private List<ReferenceInformationTemplateDetail> details;
	
}
