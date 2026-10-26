package frame.server.domain;

import java.util.Date;

import frame.server.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 采购申请表
 * 说明：一次申请仅对应一个仓库
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application {
	
	// ID
	private String id;
	
	// 申请人ID
	private String userId;
	
	// 仓库ID
	private String warehouseId;
	
	// 申请名
	private String name;
	
	// 备注
	private String remarks;
	
	// 创建日期
	private Date createTime;
	
	// 申请人：是否取消申请
	private boolean isCancelApplication;
	
	// 申请人：是否删除
	private boolean isDelete;
	
	// 管理者：审批状态
	private short approvalStatus;
	
	// 管理者：审批说明
	private String approvalInstructions;
	
	public String getCreateTime() {
		if(createTime != null) {
			return DateUtil.convertDateToString(createTime, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI);
		}
		return "";
	}

	public boolean getIsCancelApplication() {
		return isCancelApplication;
	}

	public void setIsCancelApplication(boolean isCancelApplication) {
		this.isCancelApplication = isCancelApplication;
	}
	
	public boolean getIsDelete() {
		return isDelete;
	}
	
	public void setIsDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

}
