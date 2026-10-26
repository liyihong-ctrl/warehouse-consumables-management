package frame.server.constant;

/**
 * 登录目标
 * 
 * @author Administrator
 *
 */
public class LoginTargetConstant {

	// 消耗品管理系统
	public static final String CONSUMABLES = "1";

	// 申请系统
	public static final String APPLICATION = "2";

	// 仓库管理系统
	public static final String WAREHOUSE = "3";
	
	public static String getTarget(String target) {
		String targetStr = "";
		switch(target) {
			case "1":
				targetStr = "consumables";
				break;
			case "2":
				targetStr = "application";
				break;
			case "3":
				targetStr = "warehouse";
				break;
		}
		return targetStr;
	}
	
}
