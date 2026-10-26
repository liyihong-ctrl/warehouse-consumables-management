package frame.server.dto;

import java.util.List;

import frame.server.domain.Type;
import lombok.Data;

/**
 * 消耗品类型Dto：前端级联格式
 * 
 * @author Administrator
 *
 */
@Data
public class TypeDtoForWebCascader {

	// 类型ID
	private String value;

	// 类型名称
	private String label;
	
	// 子类
	private List<Type> children;

}
