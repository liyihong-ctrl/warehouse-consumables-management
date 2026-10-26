package frame.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户权限表
 * 说明：用户可具备多角色，而多角色之间权限是相交的，会存在数据冗余。
 *       所以在此表中根据用户角色配置用户所具备的权限。
 * 
 * @author Administrator
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPermission {
	
	// 用户ID
	private String userId;
	
	// 权限ID
	private String permissionId;

}
