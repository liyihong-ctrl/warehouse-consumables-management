package frame.server.domain;

import java.util.Date;

import lombok.Data;
import frame.server.utils.DateUtil;

/**
 * 操作履历
 * 
 * @author Administrator
 *
 */
@Data
public class Operation {
	
	private String id;
	
	private String userId;
	
	// 操作
	private String operation;
	
	private String ip;
	
	private Date createTime;
	
	// 请求控制器
	private String method;
	
	// 参数
	private String params;
	
	public String getCreateTime() {
		if(createTime != null) {
			return DateUtil.convertDateToString(createTime, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI);
		}
		return "";
	}

}
