package frame.server.utils;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件工具包
 * 
 * @author Administrator
 *
 */
public class FileUtils {

	/**
	 * 将文件保存至本地指定目录下
	 * 
	 * @param file
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static boolean upload(MultipartFile file, String path, String fileName) {
		// 生成新的文件名
		String realPath = path + "/" + fileName;

		File dest = new File(realPath);

		// 判断文件父目录是否存在
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdir();
		}

		try {
			// 保存文件
			file.transferTo(dest);
			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
