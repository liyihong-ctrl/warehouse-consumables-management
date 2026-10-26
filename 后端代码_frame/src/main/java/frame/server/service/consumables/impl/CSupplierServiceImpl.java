package frame.server.service.consumables.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import frame.server.dao.ConsumableDao;
import frame.server.dao.SupplierDao;
import frame.server.domain.Supplier;
import frame.server.service.consumables.CSupplierService;
import frame.server.utils.StringUtil;
import frame.server.vo.Page;
import frame.server.vo.consumables.CSupplierController.CAddSupplierModel;
import frame.server.vo.consumables.CSupplierController.CEditSupplierModel;

@Service("cSupplierService")
public class CSupplierServiceImpl implements CSupplierService {

	@Autowired
	private SupplierDao supplierDao;

	@Autowired
	private ConsumableDao consumableDao;

	@Override
	public PageInfo<Supplier> getSuppliers(Page page) {
		PageHelper.startPage(page.getPageNum(), page.getPageSize());

		JSONObject searchCondition = page.getSearchCondition();
		String warehouseId = searchCondition.getString("warehouseId");
		String searchStr = searchCondition.getString("searchStr");

		if (StringUtil.isBlank(searchStr)) {
			return new PageInfo<Supplier>(supplierDao.selectByWarehouseId(warehouseId));
		}

		return new PageInfo<Supplier>(supplierDao.selectSupplierAndLikeName(warehouseId, searchStr));
	}

	@Override
	public List<Supplier> getAllSuppliers(String warehouseId) {
		return supplierDao.selectByWarehouseId(warehouseId);
	}

	@Override
	public Supplier getSupplier(String warehouseId, String supplierId) {
		return supplierDao.selectByWarehouseIdAndSupplierId(warehouseId, supplierId);
	}

	@Override
	public boolean isSupplierExist(String warehouseId, String supplierId) {
		return supplierDao.selectCountByWarehouseIdAndSupplierId(warehouseId, supplierId) > 0;
	}

	@Override
	public boolean isSupplierNameRepeat(String warehouseId, String name) {
		return supplierDao.selectCountByWarehouseIdAndName(warehouseId, name) > 0;
	}

	@Override
	public boolean isConsumableBoundSupplier(String supplierId) {
		return consumableDao.c_selectConsumableCountBySupplierId(supplierId) > 0;
	}

	@Override
	public void addSupplier(CAddSupplierModel model) {
		supplierDao.insertSupplier(model.createSupplier());
	}

	@Override
	public void updateSupplier(CEditSupplierModel model) {
		Supplier supplier = new Supplier();
		supplier.setId(model.getId());
		supplier.setWarehouseId(model.getWarehouseId());
		supplier.setName(model.getName());
		supplier.setContactPerson(model.getContactPerson());
		supplier.setContactPhone(model.getContactPhone());
		supplier.setAddress(model.getAddress());
		supplier.setRemarks(model.getRemarks());

		supplierDao.updateSupplier(supplier);
	}

	@Override
	public void deleteSupplier(String warehouseId, String supplierId) {
		supplierDao.deleteSupplier(warehouseId, supplierId);
	}
}
