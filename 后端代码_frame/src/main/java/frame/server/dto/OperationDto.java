package frame.server.dto;

import frame.server.domain.Operation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationDto extends Operation {

	private String userName;

}
