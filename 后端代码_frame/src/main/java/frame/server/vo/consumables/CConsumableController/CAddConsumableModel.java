package frame.server.vo.consumables.CConsumableController;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.alibaba.fastjson.JSONObject;

import frame.server.constant.ConsumableMaxCountConstant;
import frame.server.constant.Messages;
import frame.server.domain.Consumable;
import frame.server.utils.RandomUtil;
import frame.server.utils.StringUtil;
import frame.server.utils.ValidatorUtil;
import frame.server.vo.consumables.ReferenceInformationModel;
import lombok.Data;

/**
 * 鏂板娑堣€楀搧Model
 * 
 * @author Administrator
 *
 */
@Data
public class CAddConsumableModel {

	// 浠撳簱ID
	@NotBlank(message = Messages.badRequest)
	private String warehouseId;

	// 绫诲瀷ID
	@NotBlank(message = Messages.badRequest)
	private String typeId;

	// 缂栫爜锛氭牴鎹甤ode鍊肩敓鎴愭潯褰㈢爜
	@NotBlank(message = "娑堣€楀搧code涓嶈兘涓虹┖")
	@Size(min = 1, max = 20, message = "娑堣€楀搧code闀垮害鑼冨洿涓?~20")
	private String code;

	// 鍚嶇О
	@NotBlank(message = "娑堣€楀搧鍚嶇О涓嶈兘涓虹┖")
	@Size(min = 1, max = 50, message = "娑堣€楀搧鍚嶇О闀垮害鑼冨洿涓?~50")
	private String name;

	// 鏁伴噺
	private int count;

	// 瀹夊叏鍦ㄥ伐鏁?
	private int safetyCount;

	// 浠锋牸
	private double price;

	// 绠€浠?
	@Size(min = 0, max = 255, message = "娑堣€楀搧绠€浠嬮暱搴﹁寖鍥翠负0~255")
	private String summary;

	// 鍩哄噯淇℃伅
	private JSONObject referenceInformations;

	public Consumable createConsumable() {
		return new Consumable(RandomUtil.genarateId(), warehouseId, null, typeId, code, name, count, safetyCount, price,
				summary, 1, new Date(), false, false);
	}

	public boolean validate() {
		return validateCode() && validateCount() && validateSafetyCount() && validatePrice() && validateReferenceInformations();
	}
	
	private boolean validateCode() {
		return ValidatorUtil.isConsumableCode(code);
	}

	// 鏍￠獙鏁伴噺
	private boolean validateCount() {
		return 0 <= count && count <= ConsumableMaxCountConstant.MAXINVENTORYCOUNT;
	}

	// 鏍￠獙瀹夊叏鍦ㄥ伐鏁?
	private boolean validateSafetyCount() {
		return 0 <= safetyCount && safetyCount <= ConsumableMaxCountConstant.MAXINVENTORYCOUNT;
	}

	// 鏍￠獙浠锋牸
	private boolean validatePrice() {
		return 0 <= price && price <= ConsumableMaxCountConstant.MAXPRICECOUNT;
	}
	
	// 鏍￠獙鍩哄噯淇℃伅
	private boolean validateReferenceInformations() {
		if(referenceInformations == null || referenceInformations.isEmpty()) {
			return true;
		}
		
		List<ReferenceInformationModel> referenceInformationList = JSONObject.parseArray(
				referenceInformations.getString("referenceInformations"), ReferenceInformationModel.class);
		
		for(ReferenceInformationModel referenceInformationModel : referenceInformationList) {
			String key = referenceInformationModel.getKey();
			String value = referenceInformationModel.getValue();
			
			if(!StringUtil.isBlank(key) && key.length() > 255) {
				return false;
			}
			
			if(!StringUtil.isBlank(value) && value.length() > 255) {
				return false;
			}
		}
		
		return true;
	}

}
