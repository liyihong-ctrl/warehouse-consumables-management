package frame.server.dto;

import java.util.List;

import frame.server.domain.Permission;
import frame.server.domain.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto extends Role {

	private List<Permission> permissions;

}
