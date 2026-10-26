package frame.server.vo.consumables.CReferenceInformationController;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * 更新基准信息模板Model
 * 
 * @author Administrator
 *
 */
@Getter
@Setter
public class CEditReferenceInformationTemplateModel extends CAddReferenceInformationTemplateModel {

	// ID
	@NotBlank(message = "基准信息模板ID不能为空")
	private String id;
	
}
