package frame.server.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Page {

	private int pageNum;

	private int pageSize;

	private String searchCondition;
	
	public JSONObject getSearchCondition() {
		return JSON.parseObject(searchCondition);
	}

}
