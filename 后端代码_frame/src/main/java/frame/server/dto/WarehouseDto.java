package frame.server.dto;

import java.util.List;

import frame.server.domain.Warehouse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseDto extends Warehouse {
	
	private List<UserInfoDto> users;

}
