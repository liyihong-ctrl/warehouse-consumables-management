package frame.server.dto;

import java.util.List;

import frame.server.domain.Permission;
import frame.server.domain.User;
import frame.server.domain.UserExtension;
import frame.server.domain.Warehouse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationUserDto extends User {

	// 角色
	private List<RoleDto> roles;
	
	// 权限
	private List<Permission> permissions;
	
	// 仓库
	private List<Warehouse> warehouses;
	
	// 附属信息
	private UserExtension extension;
	
	// 可申请仓库Ids
	private List<String> canApplicationWarehouseIds;
	
	// 是否可申请当前仓库
	private boolean canApplication;
	
	public boolean getCanApplication() {
		return canApplication;
	}
	
	public void setCanApplication(boolean canApplication) {
		this.canApplication = canApplication;
	}

}
