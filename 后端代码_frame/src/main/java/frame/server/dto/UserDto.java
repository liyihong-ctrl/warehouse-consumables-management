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
public class UserDto extends User {

	// 角色
	private List<RoleDto> roles;
	
	// 权限
	private List<Permission> permissions;
	
	// 仓库
	private List<Warehouse> warehouses;
	
	// 附属信息
	private UserExtension extension;

}
