package frame.server.domain;

import java.util.Date;

import frame.server.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仓库表
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse {
	
	// ID
	private String id;
	
	// Code
	private String code;
	
	// 名称
	private String name;
	
	// 照片
	private String pictureUrl;
	
	// 简介
	private String summary;
	
	// 创建日期
	private Date createTime;
	
	// 显示顺序
	private int displayOrder;
	
	// 是否启用
	private boolean isEnable;
	
	// 是否删除
	private boolean isDelete;
	
	public String getCreateTime() {
		if(createTime != null) {
			return DateUtil.convertDateToString(createTime, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI);
		}
		return "";
	}
	
	public boolean getIsEnable() {
		return isEnable;
	}
	
	public void setIsEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
	
	public boolean getIsDelete() {
		return isDelete;
	}
	
	public void setIsDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

}
