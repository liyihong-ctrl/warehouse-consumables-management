package frame.server.domain;

import java.util.Date;

import frame.server.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消耗品表
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Consumable {

	// ID
	private String id;

	// 仓库ID
	private String warehouseId;

	//供应商 Id
	private String supplierId;

	// 类型ID
	private String typeId;

	// 编码：根据code值生成条形码
	private String code;

	// 名称
	private String name;

	// 数量
	private int count;

	// 安全在工数
	private int safetyCount;

	// 价格
	private double price;

	// 简介
	private String summary;

	// 状态
	private int state;

	// 创建日期
	private Date createTime;

	// 是否锁定：锁定后无法进行出库入库、在工减少、无法申请等操作
	private boolean isLock;



	// 是否删除
	private boolean isDelete;

	public String getCreateTime() {
		if (createTime != null) {
			return DateUtil.convertDateToString(createTime, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI);
		}
		return "";
	}
	
	public boolean getIsLock() {
		return isLock;
	}
	
	public void setIsLock(boolean isLock) {
		this.isLock = isLock;
	}
	
	public boolean getIsDelete() {
		return isDelete;
	}
	
	public void setIsDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

}
