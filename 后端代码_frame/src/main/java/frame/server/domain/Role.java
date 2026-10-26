package frame.server.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import frame.server.utils.DateUtil;

/**
 * 角色表
 * 
 * @author Administrator
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
	
	// ID
	private String id;
	
	// 名称
	private String name;
	
	// 编号
	private String str;
	
	// 类型
	private int type;
	
	// 备注
	private String remarks;
	
	// 创建时间
	private Date createTime;
	
	// 是否停用
	private boolean isStop;
	
	// 是否删除
	// 角色数据删除为假删除(目的是为保存相应用户的数据)
	// 例如：已删除用户，再删除角色，在用户操作历史中还需要查看相应数据
	//       或者是用户想撤回删除操作等等
	private boolean isDelete;
	
	public String getCreateTime() {
		if(createTime != null) {
			return DateUtil.convertDateToString(createTime, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI);
		}
		return "";
	}
	
	public boolean getIsStop() {
		return isStop;
	}
	
	public void setIsStop(boolean isStop) {
		this.isStop = isStop;
	}
	
	public boolean getIsDelete() {
		return isDelete;
	}
	
	public void setIsDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

}
