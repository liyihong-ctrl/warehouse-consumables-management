package frame.server.vo.consumables.CConsumableController;

import java.util.List;

import javax.validation.constraints.NotBlank;

import frame.server.constant.Messages;
import frame.server.utils.StringUtil;
import lombok.Data;

/**
 * 批量删除消耗品Model
 * 
 * @author Administrator
 *
 */
@Data
public class CBatchDeleteConsumableModel {

	// 仓库ID
	@NotBlank(message = Messages.badRequest)
	private String warehouseId;

	// 批量删除消耗品Ids
	private List<String> ids;

	public boolean validate() {
		return validateIds();
	}

	private boolean validateIds() {
		if (ids == null) {
			return false;
		}

		if (ids.size() == 0) {
			return false;
		}

		for (String id : ids) {
			if (StringUtil.isBlank(id)) {
				return false;
			}
		}

		return true;
	}

}
