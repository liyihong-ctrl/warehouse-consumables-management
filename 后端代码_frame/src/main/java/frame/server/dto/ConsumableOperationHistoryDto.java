package frame.server.dto;

import java.util.List;

import frame.server.domain.ConsumableOperationHistory;
import lombok.Getter;
import lombok.Setter;

/**
 * 操作履历Dto
 * 
 * @author Administrator
 *
 */
@Getter
@Setter
public class ConsumableOperationHistoryDto extends ConsumableOperationHistory {
	
	private String userName;
	
	private String userAccount;

	List<ConsumableOperationDetailDto> details;
	
}
