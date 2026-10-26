package frame.server.domain;

import java.util.Date;

import frame.server.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

	// 供应商ID
	private String id;

	// 仓库ID
	private String warehouseId;

	// 供应商名称
	private String name;

	// 联系人
	private String contactPerson;

	// 联系电话
	private String contactPhone;

	// 供应商地址
	private String address;

	// 备注
	private String remarks;

	// 创建时间
	private Date createTime;

	// 是否删除
	private boolean isDelete;

	public String getCreateTime() {
		if (createTime != null) {
			return DateUtil.convertDateToString(createTime, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI);
		}
		return "";
	}

	public boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
}
