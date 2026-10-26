package frame.server.constant;

/***
 * 服务器IP地址全局变量 说明：因存在图片上传至服务器功能，所以需要配置tomcat虚拟映射。
 * 在图片上传后需返回完整路径给前端，此处需要根据实际服务器地址设置返回的路径。
 * 
 * 示例：物理地址：UPLOAD_DRIVE + UPLOAD_FOLDER_NAME + WAREHOUSE_AVATAR
 * 网络地址：SERVER_IP_ADDRESS + WAREHOUSE_AVATAR + 名称
 * 
 * @author Administrator
 *
 */
public class FileUploadSettingConstant {

	// 服务器IP地址
	public static final String SERVER_IP_ADDRESS = "http://127.0.0.1/EchoCrushResources/";

	// 上传至服务器指定盘
	public static final String UPLOAD_DRIVE = "C:\\\\";

	// 上传至服务器指定文件夹
	public static final String UPLOAD_FOLDER_NAME = "EchoCrushResources\\";

	// 仓库管理系统
	public static final String WAREHOUSE = "warehouse\\";
	// 仓库管理系统-用户头像
	public static final String WAREHOUSE_AVATAR = WAREHOUSE + "avatar\\";
	// 仓库管理系统-仓库图片
	public static final String WAREHOUSE_WAREHOUSE = WAREHOUSE + "warehouse\\";

	// 消耗品管理系统
	public static final String CONSUMABLES = "consumables\\";
	// 仓库管理系统-用户头像
	public static final String CONSUMABLES_AVATAR = CONSUMABLES + "avatar\\";
	// 仓库管理系统-仓库图片
	public static final String CONSUMABLES_CONSUMABLES = CONSUMABLES + "consumables\\";

	// 消耗品申请系统
	public static final String APPLICATION = "application\\";
	// 消耗品申请系统-用户头像
	public static final String APPLICATION_AVATAR = APPLICATION + "avatar\\";

}
