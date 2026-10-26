package frame.server.vo.consumables.CRoleController;

import frame.server.utils.StringUtil;
import lombok.Data;

/**
 * 批量导入角色Model
 * 
 * @author Administrator
 *
 */
@Data
public class CImportRoleDetailModel {
	
	private String name;
	
	private String str;
	
	private String remarks;
	
	private String c_login;
	
	private String c_role_add;
	
	private String c_role_delete;
	
	private String c_role_update;
	
	private String c_role_select;
	
	private String c_role_import;
	
	private String c_role_export;
	
	private String c_user_add;
	
	private String c_user_delete;
	
	private String c_user_update;
	
	private String c_user_select;
	
	private String c_user_state_management;
	
	private String c_user_import;
	
	private String c_user_export;
	
	private String c_consumablesType_add;
	
	private String c_consumablesType_delete;
	
	private String c_consumablesType_update;
	
	private String c_consumablesType_select;
	
	private String c_consumablesType_state_management;
	
	private String c_consumablesType_import;
	
	private String c_consumablesType_export;
	
	private String c_referenceInformationTemplate_add;
	
	private String c_referenceInformationTemplate_delete;
	
	private String c_referenceInformationTemplate_update;
	
	private String c_referenceInformationTemplate_select;
	
	private String c_consumables_add;
	
	private String c_consumables_delete;
	
	private String c_consumables_update;
	
	private String c_consumables_select;
	
	private String c_consumables_state_management;
	
	private String c_consumables_inbound;
	
	private String c_consumables_outbound;
	
	private String c_consumables_import;
	
	private String c_consumables_export;
	
	private String c_application_select;
	
	private String c_application_state_management;
	
	private String c_userOperate_select;
	
	private String c_userOperate_export;
	
	private String c_statistics_select;
	
	private String c_statistics_export;
	
	public static String validatePermission(String keyWord) {
		if(StringUtil.isBlank(keyWord)) {
			return "权限内容不能为空";
		}
		
		keyWord = keyWord.trim();
		if(!(keyWord.equals("有") || keyWord.equals("无"))) {
			return "权限关键词为\"有\"或者\"无\"";
		}
		
		return null;
	}
	
}
