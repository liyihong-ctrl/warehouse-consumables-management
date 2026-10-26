package frame.server.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import frame.server.vo.ExcelDataModel;

/**
 * Excel导入工具包
 * 
 * @author Administrator
 *
 */
public class ImportExcelUtil {

	/**
	 * 检查文件
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static boolean checkFile(MultipartFile file) {
		// 判断文件是否存在
		if (file == null) {
			return false;
		}

		// 获得文件名
		String fileName = file.getOriginalFilename();

		// 判断文件是否是excel文件
		if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
			return false;
		}

		return true;
	}

	/**
	 * 获取Excel数据
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static ExcelDataModel getExcelData(MultipartFile file) throws IOException {
		// 获得Workbook工作薄对象
		Workbook workbook = getWorkBook(file);

		// 创建返回对象
		ExcelDataModel excelData = new ExcelDataModel();

		if (workbook != null) {
			// 标题
			List<String> titles = new ArrayList<String>();
			
			// 行
			List<List<Object>> rows = new ArrayList<List<Object>>();
			
			for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
				// 获得当前sheet工作表
				Sheet sheet = workbook.getSheetAt(sheetNum);

				if (sheet == null) {
					continue;
				}

				// 获得当前sheet的开始行
				int firstRowNum = sheet.getFirstRowNum();

				// 获得当前sheet的结束行
				int lastRowNum = sheet.getLastRowNum();

				// 循环除了所有行,如果要循环除第一行以外的就firstRowNum+1
				for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
					// 获得当前行
					Row row = sheet.getRow(rowNum);

					// 空行则跳过
					if (row == null) {
						continue;
					}

					// 获得当前行的开始列
					int firstCellNum = row.getFirstCellNum();

					// 获得当前行的列数
					int lastCellNum = row.getLastCellNum();

					List<Object> curRow = new ArrayList<Object>();
					for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
						Cell cell = row.getCell(cellNum);

						// 标题行
						if (rowNum == firstRowNum) {
							titles.add(getCellValue(cell));
						} 
						
						// 数据行
						else {
							curRow.add(getCellValue(cell));
						}
					}
					
					if (rowNum != firstRowNum) {
						rows.add(curRow);
					}
				}
			}

			excelData.setTitles(titles);
			excelData.setRows(rows);
		}
		
		return excelData;
	}

	/**
	 * 获得Workbook工作薄对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Workbook getWorkBook(MultipartFile file) throws IOException {
		// 获得文件名
		String fileName = file.getOriginalFilename();

		// 创建Workbook工作薄对象，表示整个excel
		Workbook workbook = null;

		// 获取excel文件的io流
		InputStream is = file.getInputStream();

		// 根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
		if (fileName.endsWith("xls")) {
			// 2003
			workbook = new HSSFWorkbook(is);
		} else if (fileName.endsWith("xlsx")) {
			// 2007 及2007以上
			workbook = new XSSFWorkbook(is);
		}

		return workbook;
	}

	/**
	 * 获取Excel Cell值
	 * 
	 * @param cell
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getCellValue(Cell cell) {
		String cellValue = "";
		
		if (cell == null) {
			return cellValue;
		}
		
		// 判断数据的类型
		switch (cell.getCellType()) {
		// 数字
		case Cell.CELL_TYPE_NUMERIC:
			cellValue = stringDateProcess(cell);
			break;
		// 字符串
		case Cell.CELL_TYPE_STRING:
			cellValue = String.valueOf(cell.getStringCellValue());
			break;
		// Boolean
		case Cell.CELL_TYPE_BOOLEAN:
			cellValue = String.valueOf(cell.getBooleanCellValue());
			break;
		// 公式
		case Cell.CELL_TYPE_FORMULA:
			cellValue = String.valueOf(cell.getCellFormula());
			break;
		// 空值
		case Cell.CELL_TYPE_BLANK:
			cellValue = "";
			break;
		// 故障
		case Cell.CELL_TYPE_ERROR:
			cellValue = "非法字符";
			break;
		default:
			cellValue = "未知类型";
			break;
		}
		
		return cellValue;
	}

	public static String stringDateProcess(Cell cell) {
		String result = new String();
		// 处理日期格式、时间格式
		if (HSSFDateUtil.isCellDateFormatted(cell)) {
			SimpleDateFormat sdf = null;
			if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
				sdf = new SimpleDateFormat("HH:mm");
			} else {
				// 日期
				sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			}
			Date date = cell.getDateCellValue();
			result = sdf.format(date);
		} else if (cell.getCellStyle().getDataFormat() == 58) {
			// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			double value = cell.getNumericCellValue();
			Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
			result = sdf.format(date);
		} else {
			double value = cell.getNumericCellValue();
			CellStyle style = cell.getCellStyle();
			DecimalFormat format = new DecimalFormat();
			String temp = style.getDataFormatString();
			// 单元格设置成常规
			if (temp.equals("General")) {
				format.applyPattern("#");
			}
			result = format.format(value);
		}

		return result;
	}

}