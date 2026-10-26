package frame.server.utils;

import java.io.IOException;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import frame.server.dto.ApplicationDetailDto;
import frame.server.dto.ApplicationDto;

/**
 * Pdf工具包
 * 
 * @author Administrator
 *
 */
public class PdfUtil {

	/**
	 * 生成pdf
	 * 
	 * @param document
	 * @param writer
	 * @param products
	 * @throws IOException
	 */
	public void createApplicationPDF(Document document, PdfWriter writer, ApplicationDto application)
			throws IOException {
		document.setPageSize(PageSize.A4);// A4纸张
		try {
			document.addTitle("申请信息_" + application.getCreateTime());// 标题

			document.add(new Chunk("\n"));// 换行

			PdfPTable table = new PdfPTable(2);// 表格，2列

			PdfPCell cell = null;// 单元格

			Font font = new Font(BaseFont.createFont("/字体/simsun.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED));

			// 标题：仓库名称
			cell = new PdfPCell(new Paragraph("仓库名称", font));
			cell.setHorizontalAlignment(1);// 居中对齐
			table.addCell(cell);// 将单元格加入表格

			// 填入仓库名称
			cell = new PdfPCell(new Paragraph(application.getWarehouseName(), font));
			cell.setHorizontalAlignment(1);// 居中对齐
			table.addCell(cell);

			document.add(new Chunk("\n"));

			// 标题：用户名称
			cell = new PdfPCell(new Paragraph("用户名称", font));// 标题
			cell.setHorizontalAlignment(1);// 居中对齐
			table.addCell(cell);// 将单元格加入表格

			// 填入用户名称
			cell = new PdfPCell(new Paragraph(application.getUserName(), font));
			cell.setHorizontalAlignment(1);// 居中对齐
			table.addCell(cell);

			document.add(new Chunk("\n"));

			// 标题：申请名称
			cell = new PdfPCell(new Paragraph("申请名称", font));// 标题
			cell.setHorizontalAlignment(1);// 居中对齐
			table.addCell(cell);// 将单元格加入表格

			// 填入申请名
			cell = new PdfPCell(new Paragraph(application.getName(), font));
			cell.setHorizontalAlignment(1);// 居中对齐
			table.addCell(cell);

			document.add(new Chunk("\n"));

			// 标题：申请日期
			cell = new PdfPCell(new Paragraph("申请日期", font));// 标题
			cell.setHorizontalAlignment(1);// 居中对齐
			table.addCell(cell);// 将单元格加入表格

			// 填入申请日期
			cell = new PdfPCell(new Paragraph(application.getCreateTime()));
			cell.setHorizontalAlignment(1);// 居中对齐
			table.addCell(cell);

			// 标题：备注
			cell = new PdfPCell(new Paragraph("备注", font));// 标题
			cell.setHorizontalAlignment(1);// 居中对齐
			table.addCell(cell);// 将单元格加入表格

			// 填入申请日期
			cell = new PdfPCell(new Paragraph(application.getRemarks(), font));
			cell.setHorizontalAlignment(1);// 居中对齐
			table.addCell(cell);

			document.add(new Chunk("\n"));

			cell = new PdfPCell(new Paragraph("", font));
			cell.setBorder(0);
			cell.setPadding(20);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph("", font));
			cell.setBorder(0);
			cell.setPadding(20);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph("", font));
			cell.setBorder(0);
			cell.setPadding(20);
			table.addCell(cell);
			
			document.add(new Chunk("\n"));

			document.add(table);

			PdfPTable table2 = new PdfPTable(3);

			cell = new PdfPCell(new Paragraph("消耗品Code", font));
			cell.setHorizontalAlignment(1);
			table2.addCell(cell);

			cell = new PdfPCell(new Paragraph("消耗品名称", font));
			cell.setHorizontalAlignment(1);
			table2.addCell(cell);

			// 条形码展示出错；暂时隐藏起来
			//cell = new PdfPCell(new Paragraph("条形码", font));
			//cell.setHorizontalAlignment(1);
			//table2.addCell(cell);

			cell = new PdfPCell(new Paragraph("申请数量", font));
			cell.setHorizontalAlignment(1);
			table2.addCell(cell);
			
			document.add(new Chunk("\n"));

			for (ApplicationDetailDto detail : application.getDetails()) {
				// Code
				cell = new PdfPCell(new Paragraph(detail.getCode(), font));
				cell.setHorizontalAlignment(1);
				table2.addCell(cell);
				
				// 消耗品名称
				cell = new PdfPCell(new Paragraph(detail.getConsumableName(), font));
				cell.setHorizontalAlignment(1);
				table2.addCell(cell);

				//cell = new PdfPCell(new Paragraph(""));
				//cell.setHorizontalAlignment(1);
				//table2.addCell(cell);

				// 申请数
				cell = new PdfPCell(new Paragraph(Integer.toString(detail.getApplicateCount()), font));
				cell.setHorizontalAlignment(1);
				table2.addCell(cell);
				
				document.add(new Chunk("\n"));
			}

			document.add(table2);
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
	}

}
