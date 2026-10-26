package frame.server.domain;

import java.util.Date;

import frame.server.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基准信息模板
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceInformationTemplate {

	// ID
	private String id;
	
	// 仓库ID
	private String warehouseId;
	
	// 名称
	private String name;
	
	// 备注
	private String remarks;
	
	// 创建日期
	private Date createTime;
	
	// 是否为系统内置
	private boolean isSystem;
	
	// 是否删除
	private boolean isDelete;
	
	public String getCreateTime() {
		if(createTime != null) {
			return DateUtil.convertDateToString(createTime, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI);
		}
		return "";
	}
	
	public boolean getIsSystem() {
		return isSystem;
	}
	
	public void setIsSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	
	public boolean getIsDelete() {
		return isDelete;
	}
	
	public void setIsDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	
}
