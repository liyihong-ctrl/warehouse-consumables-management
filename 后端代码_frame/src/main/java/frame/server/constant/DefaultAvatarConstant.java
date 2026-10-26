package frame.server.constant;

/**
 * 全局默认头像
 * 
 * @author Administrator
 *
 */
public class DefaultAvatarConstant {
	
	public static final String CONSUMABLES_DEFAULT_AVATAR = FileUploadSettingConstant.SERVER_IP_ADDRESS + "defaultLogo/cUserDefaultLogo.png";
	
	public static final String APPLICATION_DEFAULT_AVATAR = FileUploadSettingConstant.SERVER_IP_ADDRESS + "defaultLogo/aUserDefaultLogo.png";
	
	public static final String WAREHOUSE_DEFAULT_AVATAR = FileUploadSettingConstant.SERVER_IP_ADDRESS + "defaultLogo/wUserDefaultLogo.png";
	
	public static String getDefaultLogoUrl(String target) {
		String defaultLogoUrl = "";
		switch(target) {
			case "1":
				defaultLogoUrl = CONSUMABLES_DEFAULT_AVATAR;
				break;
			case "2":
				defaultLogoUrl = APPLICATION_DEFAULT_AVATAR;
				break;
			case "3":
				defaultLogoUrl = WAREHOUSE_DEFAULT_AVATAR;
				break;
		}
		return defaultLogoUrl;
	}

}
