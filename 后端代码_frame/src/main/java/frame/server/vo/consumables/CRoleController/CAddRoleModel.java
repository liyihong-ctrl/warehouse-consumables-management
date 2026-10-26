package frame.server.vo.consumables.CRoleController;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 添加角色Model
 * 
 * @author Administrator
 *
 */
@Data
public class CAddRoleModel {
	
	// 仓库ID
	@NotBlank(message = "仓库ID不能为空")
	private String warehouseId;
	
	// 名称
	@NotBlank(message = "角色名称不能为空")
	@Size(min = 1, max = 20, message = "角色名称长度范围为1~20")
	private String name;
	
	// 标识
	@NotBlank(message = "角色标识不能为空")
	@Size(min = 1, max = 20, message = "角色标识长度范围为1~20")
	private String str;
	
	// 备注
	@Size(min = 0, max = 50, message = "角色备注长度范围为0~50")
	private String remarks;
	
	// 权限
	List<String> permissionStrs;
	
	public boolean validate() {
		return validatePermissionIds();
	}
	
	private boolean validatePermissionIds() {
		return permissionStrs != null && !permissionStrs.isEmpty();
	}

}
