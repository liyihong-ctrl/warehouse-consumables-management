package frame.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局WebSocketModel
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketModel {

	private String msg;
	
	private boolean isForceOffline;

	public boolean getIsForceOffline() {
		return isForceOffline;
	}

	public void setIsForceOffline(boolean isForceOffline) {
		this.isForceOffline = isForceOffline;
	}
	
}
