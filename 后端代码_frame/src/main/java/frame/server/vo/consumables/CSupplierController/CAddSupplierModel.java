package frame.server.vo.consumables.CSupplierController;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import frame.server.constant.Messages;
import frame.server.domain.Supplier;
import frame.server.utils.RandomUtil;
import lombok.Data;

@Data
public class CAddSupplierModel {

	// 仓库ID
	@NotBlank(message = Messages.badRequest)
	private String warehouseId;

	// 供应商名称
	@NotBlank(message = "供应商名称不能为空")
	@Size(min = 1, max = 50, message = "供应商名称长度范围为1~50")
	private String name;

	// 联系人
	@Size(min = 0, max = 20, message = "联系人长度范围为0~20")
	private String contactPerson;

	// 联系电话
	@Size(min = 0, max = 20, message = "联系电话长度范围为0~20")
	private String contactPhone;

	// 地址
	@Size(min = 0, max = 255, message = "地址长度范围为0~255")
	private String address;

	// 备注
	@Size(min = 0, max = 255, message = "备注长度范围为0~255")
	private String remarks;

	public Supplier createSupplier() {
		return new Supplier(RandomUtil.genarateId(), warehouseId, name, contactPerson, contactPhone, address, remarks,
				new Date(), false);
	}
}
