package frame.server.service.consumables;

import java.util.List;

import com.github.pagehelper.PageInfo;

import frame.server.domain.Scrap;
import frame.server.vo.Page;

public interface CScrapService {

    PageInfo<Scrap> getScraps(Page page);

    List<Scrap> getAllScraps(String warehouseId);

    Scrap getScrap(String warehouseId, String id);

    void addScrap(Scrap scrap);

    void updateScrap(Scrap scrap);

    void deleteScrap(String warehouseId, String id);

    void approveScrap(String warehouseId, String id);

    void rejectScrap(String warehouseId, String id);

    int getScrapCount(String warehouseId);
}
