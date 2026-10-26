package frame.server.vo;

import java.util.List;

import lombok.Data;

/**
 * Excel Model
 * 
 * @author Administrator
 *
 */
@Data
public class ExcelDataModel {

    // 表头
    private List<String> titles;

    // 数据
    private List<List<Object>> rows;
    
    private String name;

}
