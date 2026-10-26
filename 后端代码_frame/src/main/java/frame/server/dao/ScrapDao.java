package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.config.mybatis.SimpleUpdateExtendedLanguageDriver;
import frame.server.domain.Scrap;

@Mapper
public interface ScrapDao {

    @Insert("insert into scrap (#{scrap})")
    @Lang(SimpleInsertExtendedLanguageDriver.class)
    Integer insertScrap(Scrap scrap);

    @Update("update scrap (#{scrap}) where id = #{id} and warehouse_id = #{warehouseId} and is_delete = 0")
    @Lang(SimpleUpdateExtendedLanguageDriver.class)
    Integer updateScrap(Scrap scrap);

    @Delete("update scrap set is_delete = 1 where id = #{id} and warehouse_id = #{warehouseId} and is_delete = 0")
    Integer deleteScrap(@Param("warehouseId") String warehouseId, @Param("id") String id);

    @Select("select * from scrap where warehouse_id = #{warehouseId} and is_delete = 0 order by create_time desc")
    List<Scrap> selectByWarehouseId(String warehouseId);

    @Select("select * from scrap where warehouse_id = #{warehouseId} and (reason like concat('%', #{searchStr}, '%') or operator like concat('%', #{searchStr}, '%')) and is_delete = 0 order by create_time desc")
    List<Scrap> selectByWarehouseIdAndLike(@Param("warehouseId") String warehouseId, @Param("searchStr") String searchStr);

    @Select("select * from scrap where warehouse_id = #{warehouseId} and id = #{id} and is_delete = 0")
    Scrap selectByWarehouseIdAndId(@Param("warehouseId") String warehouseId, @Param("id") String id);

    @Select("select count(id) from scrap where warehouse_id = #{warehouseId} and is_delete = 0")
    Integer selectCountByWarehouseId(String warehouseId);

    @Delete("delete from scrap")
    Integer clear();

    @Update("update scrap set status = #{status} where id = #{id} and warehouse_id = #{warehouseId} and is_delete = 0")
    Integer updateStatus(@Param("warehouseId") String warehouseId, @Param("id") String id, @Param("status") int status);
}
