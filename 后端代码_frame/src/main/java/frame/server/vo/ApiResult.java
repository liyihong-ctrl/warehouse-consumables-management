package frame.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult {

	private int code;

	private boolean success;

	private Object data;

	private String msg;
	
}
