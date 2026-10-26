package frame.server.utils;

/**
 * 文件名工具包
 * 
 * @author Administrator
 *
 */
public class FileNameUtils {
	
	/**
	 * 获取文件名后缀
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getSuffix(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}

	/**
	 * 根据UUID生成新文件名
	 * 
	 * @param fileOriginName
	 * @return
	 */
	public static String getFileName(String fileOriginName) {
		return RandomUtil.genarateId() + FileNameUtils.getSuffix(fileOriginName);
	}

}
