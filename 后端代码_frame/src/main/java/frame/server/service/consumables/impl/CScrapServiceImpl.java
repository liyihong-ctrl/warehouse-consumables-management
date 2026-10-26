package frame.server.service.consumables.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import frame.server.dao.ConsumableDao;
import frame.server.dao.ScrapDao;
import frame.server.domain.Scrap;
import frame.server.service.consumables.CScrapService;
import frame.server.utils.RandomUtil;
import frame.server.utils.StringUtil;
import frame.server.vo.Page;

@Service("cScrapService")
public class CScrapServiceImpl implements CScrapService {

    @Autowired
    private ScrapDao scrapDao;

    @Autowired
    private ConsumableDao consumableDao;

    @Override
    public PageInfo<Scrap> getScraps(Page page) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());

        JSONObject searchCondition = page.getSearchCondition();
        String warehouseId = searchCondition.getString("warehouseId");
        String searchStr = searchCondition.getString("searchStr");

        if (StringUtil.isBlank(searchStr)) {
            return new PageInfo<Scrap>(scrapDao.selectByWarehouseId(warehouseId));
        }
        return new PageInfo<Scrap>(scrapDao.selectByWarehouseIdAndLike(warehouseId, searchStr));
    }

    @Override
    public List<Scrap> getAllScraps(String warehouseId) {
        return scrapDao.selectByWarehouseId(warehouseId);
    }

    @Override
    public Scrap getScrap(String warehouseId, String id) {
        return scrapDao.selectByWarehouseIdAndId(warehouseId, id);
    }

    @Override
    public void addScrap(Scrap scrap) {
        scrap.setId(RandomUtil.genarateId());
        scrap.setCreateTime(new Date());
        scrap.setStatus(0); // 待审批
        scrap.setIsDelete(false);
        scrapDao.insertScrap(scrap);
    }

    @Override
    public void updateScrap(Scrap scrap) {
        scrapDao.updateScrap(scrap);
    }

    @Override
    public void deleteScrap(String warehouseId, String id) {
        scrapDao.deleteScrap(warehouseId, id);
    }

    @Override
    public void approveScrap(String warehouseId, String id) {
        scrapDao.updateStatus(warehouseId, id, 1);
    }

    @Override
    public void rejectScrap(String warehouseId, String id) {
        scrapDao.updateStatus(warehouseId, id, 2);
    }

    @Override
    public int getScrapCount(String warehouseId) {
        return scrapDao.selectCountByWarehouseId(warehouseId);
    }
}
