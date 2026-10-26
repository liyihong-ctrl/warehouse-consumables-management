package frame.server.constant;

/**
 * 消耗品操作类型
 * 
 * 仅保存与数量相关的操作
 * 
 * @author Administrator
 *
 */
public class ConsumableOperateTypeConstant {

	// 添加消耗品
	public static final int ADD_CONSUMABLE = 100;

	// 更新消耗品
	public static final int UPDATE_CONSUMABLE = 200;

	// 删除消耗品
	public static final int DELETE_CONSUMABLE = 300;

	// 出库
	public static final int NORMAL_DELIVERY = 0;

	// 在工减少
	public static final int REDUCED_WORK = 1;

	// 入库：A类型
	public static final int INBOUND_A = 2;

	// 入库：B类型
	public static final int INBOUND_B = 3;

	public static String getOperationName(int operateState) {
		switch (operateState) {
		case NORMAL_DELIVERY:
			return "出库";
		case REDUCED_WORK:
			return "在工减少";
		case INBOUND_A:
			return "入库：A类型";
		case INBOUND_B:
			return "入库：B类型";
		case ADD_CONSUMABLE:
			return "添加消耗品";
		case UPDATE_CONSUMABLE:
			return "更新消耗品";
		case DELETE_CONSUMABLE:
			return "删除消耗品";
		}
		return "";
	}

}
