package frame.server.constant;

/**
 * 角色类型
 * 
 * @author Administrator
 *
 */
public enum UserTypeEnum {
	
	// 根管理员
	ROOTADMIN(0),

	// 系统管理员
	SYSTEMADMIN(1),
	
	// 仓库管理员
	WAREHOUSEMANAGER(2),
	
	// 普通员工
	GENERALEMPLOYEE(4),
	
	// 其他员工
	OTHEREMPLOYEES(8),
	
	// 临时员工
	TEMPORARYEMPLOYEE(1024)
	
	;
	
	private final int value;
	
	UserTypeEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

}
