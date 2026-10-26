package frame.server.dto;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;

import frame.server.utils.PdfUtil;

/**
 * 继承AbstractPdfView类，实现抽象方法buildPdfDocument
 * 
 */
public class PdfView extends AbstractPdfView {

	/**
	 * 调用接口实现导出
	 */
	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 文件名
		String fileName = new Date().getTime() + "_申请单.pdf";

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "filename=" + new String(fileName.getBytes(), "iso8859-1"));

		ApplicationDto application = (ApplicationDto) model.get("application");

		PdfUtil pdfUtil = new PdfUtil();

		pdfUtil.createApplicationPDF(document, writer, application);
	}

}
