package frame.server.service.consumables;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageInfo;

import frame.server.domain.Supplier;
import frame.server.vo.Page;
import frame.server.vo.consumables.CSupplierController.CAddSupplierModel;
import frame.server.vo.consumables.CSupplierController.CEditSupplierModel;

public interface CSupplierService {

	PageInfo<Supplier> getSuppliers(Page page);

	List<Supplier> getAllSuppliers(String warehouseId);

	Supplier getSupplier(String warehouseId, String supplierId);

	boolean isSupplierExist(String warehouseId, String supplierId);

	boolean isSupplierNameRepeat(String warehouseId, String name);

	boolean isConsumableBoundSupplier(String supplierId);

	@Transactional
	void addSupplier(CAddSupplierModel model);

	@Transactional
	void updateSupplier(CEditSupplierModel model);

	@Transactional
	void deleteSupplier(String warehouseId, String supplierId);
}
