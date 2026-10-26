package frame.server.controller.consumables;

import com.alibaba.fastjson.JSONObject;
import frame.server.annotation.OperationAnnotation;
import frame.server.constant.Messages;
import frame.server.constant.PermissionsConstant;
import frame.server.controller.BaseController;
import frame.server.domain.Scrap;
import frame.server.service.consumables.CScrapService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.StringUtil;
import frame.server.vo.ApiResult;
import frame.server.vo.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "消耗品管理系统：报废管理控制器")
@RequestMapping("/cScrap")
@RestController
public class CScrapController extends BaseController {

    @Autowired
    private CScrapService cScrapService;

    @ApiOperation(value = "消耗品管理系统：获取报废列表")
    @OperationAnnotation(value = "消耗品管理系统：获取报废列表")
    @RequiresPermissions(PermissionsConstant.C_SCRAP_SELECT_STR)
    @RequestMapping(value = "/getScraps", method = RequestMethod.POST)
    public ApiResult getScraps(Page page) {
        JSONObject searchCondition = page.getSearchCondition();
        if (searchCondition == null || StringUtil.isBlank(searchCondition.getString("warehouseId"))) {
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        String warehouseId = searchCondition.getString("warehouseId");
        if (!super.validateUserWarehouse(warehouseId)) {
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        return ApiResultUtil.success(cScrapService.getScraps(page));
    }

    @ApiOperation(value = "消耗品管理系统：新增报废记录")
    @OperationAnnotation(value = "消耗品管理系统：新增报废记录")
    @RequiresPermissions(PermissionsConstant.C_SCRAP_ADD_STR)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ApiResult addScrap(Scrap scrap) {
        if (StringUtil.isBlank(scrap.getWarehouseId()) || StringUtil.isBlank(scrap.getConsumableId())) {
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        if (!super.validateUserWarehouse(scrap.getWarehouseId())) {
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        cScrapService.addScrap(scrap);
        return ApiResultUtil.success();
    }

    @ApiOperation(value = "消耗品管理系统：编辑报废记录")
    @OperationAnnotation(value = "消耗品管理系统：编辑报废记录")
    @RequiresPermissions(PermissionsConstant.C_SCRAP_UPDATE_STR)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ApiResult updateScrap(Scrap scrap) {
        if (StringUtil.isBlank(scrap.getId()) || StringUtil.isBlank(scrap.getWarehouseId())) {
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        if (!super.validateUserWarehouse(scrap.getWarehouseId())) {
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        cScrapService.updateScrap(scrap);
        return ApiResultUtil.success();
    }

    @ApiOperation(value = "消耗品管理系统：删除报废记录")
    @OperationAnnotation(value = "消耗品管理系统：删除报废记录")
    @RequiresPermissions(PermissionsConstant.C_SCRAP_DELETE_STR)
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ApiResult deleteScrap(String warehouseId, String id) {
        if (StringUtil.isBlank(id) || StringUtil.isBlank(warehouseId)) {
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        if (!super.validateUserWarehouse(warehouseId)) {
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        cScrapService.deleteScrap(warehouseId, id);
        return ApiResultUtil.success();
    }

    @ApiOperation(value = "消耗品管理系统：审批通过报废")
    @OperationAnnotation(value = "消耗品管理系统：审批通过报废")
    @RequiresPermissions(PermissionsConstant.C_SCRAP_UPDATE_STR)
    @RequestMapping(value = "/approve", method = RequestMethod.GET)
    public ApiResult approveScrap(String warehouseId, String id) {
        if (StringUtil.isBlank(id) || StringUtil.isBlank(warehouseId)) {
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        cScrapService.approveScrap(warehouseId, id);
        return ApiResultUtil.success();
    }

    @ApiOperation(value = "消耗品管理系统：驳回报废")
    @OperationAnnotation(value = "消耗品管理系统：驳回报废")
    @RequiresPermissions(PermissionsConstant.C_SCRAP_UPDATE_STR)
    @RequestMapping(value = "/reject", method = RequestMethod.GET)
    public ApiResult rejectScrap(String warehouseId, String id) {
        if (StringUtil.isBlank(id) || StringUtil.isBlank(warehouseId)) {
            return ApiResultUtil.badRequest(Messages.badRequest);
        }
        cScrapService.rejectScrap(warehouseId, id);
        return ApiResultUtil.success();
    }
}
