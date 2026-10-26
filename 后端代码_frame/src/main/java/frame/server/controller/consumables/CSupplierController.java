package frame.server.controller.consumables;

import com.alibaba.fastjson.JSONObject;
import frame.server.annotation.OperationAnnotation;
import frame.server.constant.Messages;
import frame.server.constant.PermissionsConstant;
import frame.server.controller.BaseController;
import frame.server.domain.Supplier;
import frame.server.service.consumables.CSupplierService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.StringUtil;
import frame.server.vo.ApiResult;
import frame.server.vo.Page;
import frame.server.vo.consumables.CSupplierController.CAddSupplierModel;
import frame.server.vo.consumables.CSupplierController.CEditSupplierModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//api 用于 swagger 生成接口文档  requestMapping 用于指定访问路径
@Api(tags="消耗品管理系统：供应商管理控制器")
@RequestMapping("/cSupplier")
@RestController
public class CSupplierController extends BaseController {
    @Autowired
    private CSupplierService cSupplierService;

    //分页查询数据的接口 使用get 方法即可
    @ApiOperation(value = "消耗品管理系统：获取供应商管理列表")
    @OperationAnnotation(value = "消耗品管理系统：获取供应商列表")
    @RequiresPermissions(PermissionsConstant.C_APPLICATION_SELECT_STR)
    @RequestMapping(value = "/getSuppliers",method = RequestMethod.GET)
    public  ApiResult getSuppliers(Page page) {
        JSONObject searchCondition = page.getSearchCondition();

        //如果查询条件或者仓库 id 任意缺失就返回请求错误
        if (searchCondition == null || StringUtil.isBlank((searchCondition.getString("warehouseId")))){
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        String warehouseId=searchCondition.getString("warehouseId");
        if (!super.validateUserWarehouse(warehouseId)){
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        return ApiResultUtil.success(cSupplierService.getSuppliers(page));
    };
    @ApiOperation(value = "消耗品管理系统：获取某一仓库的全部供应商数据")
    @OperationAnnotation(value = "消耗品管理系统：获取某一仓库的全部供应商数据")
    @RequiresPermissions(PermissionsConstant.C_APPLICATION_SELECT_STR)
    @RequestMapping(value = "/allSuppliers",method = RequestMethod.GET)
    public ApiResult aliSuppliers(String warehouseId){
       if (StringUtil.isBlank(warehouseId)){
           return ApiResultUtil.badRequest(Messages.badRequest);
       }
       if (!super.validateUserWarehouse(warehouseId)){
           return ApiResultUtil.badRequest(Messages.badRequest);
       }
       return  ApiResultUtil.success(cSupplierService.getAllSuppliers(warehouseId));
    }
    @ApiOperation(value="消耗品管理系统：新增供应商")
    @OperationAnnotation(value = "消耗品管理系统：新增供应商")
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @RequiresPermissions(PermissionsConstant.C_APPLICATION_SELECT_STR)
    public ApiResult addSupplier(@Validated CAddSupplierModel model){
        if (!super.validateUserWarehouse(model.getWarehouseId())){
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        if (cSupplierService.isSupplierNameRepeat(model.getWarehouseId(), model.getName())){
            return ApiResultUtil.error("新增失败：供应商名称重复");
        }
        //前端数据模板无误，返回数据模型
        cSupplierService.addSupplier(model);
        return ApiResultUtil.success();
    }
    @ApiOperation("消耗品管理系统：修改供应商信息")
    @OperationAnnotation("消耗品管理系统：修改供应商信息")
    @RequiresPermissions(PermissionsConstant.C_CONSUMABLES_UPDATE_STR)
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public ApiResult updateSupplier(@Validated  CEditSupplierModel model){
        if (!super.validateUserWarehouse(model.getWarehouseId())){
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        Supplier supplier = cSupplierService.getSupplier(model.getWarehouseId(), model.getId());
        if (supplier == null){
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        if (!model.getName().equals(supplier.getName())&&cSupplierService.isSupplierNameRepeat(model.getName(), supplier.getWarehouseId())){
            return ApiResultUtil.error("修改失败：供应商名称重复");
        }
        cSupplierService.updateSupplier(model);
        return ApiResultUtil.success();
    }


    @ApiOperation("消耗品管理系统：删除供应商")
    @OperationAnnotation("消耗品管理系统：删除供应商")
    @RequiresPermissions(PermissionsConstant.C_CONSUMABLES_DELETE_STR)
    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public ApiResult deleteSupplier(String warehouseId,String id){
        if (StringUtil.isBlank(id)||StringUtil.isBlank(warehouseId)){
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        if (!super.validateUserWarehouse(warehouseId)){
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        if (!cSupplierService.isConsumableBoundSupplier(id)){
            return ApiResultUtil.error("请求失败，此供应商已经绑定消耗品");
        }
        cSupplierService.deleteSupplier(warehouseId,id);
        return ApiResultUtil.success();
    }


}
