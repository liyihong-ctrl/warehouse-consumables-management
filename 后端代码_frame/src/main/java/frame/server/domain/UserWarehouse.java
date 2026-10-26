package frame.server.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户仓库表
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWarehouse {
	
	// 用户ID
	private String userId;
	
	// 仓库ID
	private String warehouseId;
	
	// 创建日期
	private Date createTime;

}
