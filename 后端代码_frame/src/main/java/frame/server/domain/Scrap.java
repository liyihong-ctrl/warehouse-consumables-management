package frame.server.domain;

import java.util.Date;

import frame.server.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scrap {

    // ID
    private String id;

    // 仓库ID
    private String warehouseId;

    // 消耗品ID
    private String consumableId;

    // 报废数量
    private int quantity;

    // 报废原因
    private String reason;

    // 经办人
    private String operator;

    // 审批状态 0-待审批 1-已通过 2-已驳回
    private int status;

    // 创建日期
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
