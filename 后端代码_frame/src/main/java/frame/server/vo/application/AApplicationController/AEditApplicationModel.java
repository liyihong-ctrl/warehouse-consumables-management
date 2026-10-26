package frame.server.vo.application.AApplicationController;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * 更新申请Model
 * 
 * @author Administrator
 *
 */
@Getter
@Setter
public class AEditApplicationModel extends AAddApplicationModel {

	// ID
	@NotBlank(message = "申请ID不能为空")
	private String id;
	
}
