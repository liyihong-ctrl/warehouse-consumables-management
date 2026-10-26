package frame.server.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
	
	private DateUtil() {

	}

	public static final String DATE_FORMAT_YYYY = "yyyy";

	public static final String DATE_FORMAT_YYYYMM = "yyyyMM";

	public static final String DATE_FORMAT_YYYY_MM = "yyyy-MM";

	public static final String DATE_FORMAT_YYMMDD = "yyMMdd";

	public static final String DATE_FORMAT_YY_MM_DD = "yy-MM-dd";

	public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";

	public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

	public static final String DATE_FORMAT_POINTYYYYMMDD = "yyyy.MM.dd";

	public static final String DATE_TIME_FORMAT_YYYY年MM月DD日 = "yyyy年MM月dd日";

	public static final String DATE_FORMAT_YYYYMMDDHHmm = "yyyyMMddHHmm";

	public static final String DATE_TIME_FORMAT_YYYYMMDD_HH_MI = "yyyyMMdd HH:mm";

	public static final String DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI = "yyyy-MM-dd HH:mm";

	public static final String DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_1 = "yyyy/MM/dd HH:mm";

	public static final String DATE_TIME_FORMAT_YYYY_MM_DD_1 = "yyyy/MM/dd";

	public static final String DATE_TIME_FORMAT_YYYYMMDDHHMISS = "yyyyMMddHHmmss";

	public static final String DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS = "yyyy-MM-dd HH:mm:ss";

	public static final String DATE_TIME_FORMAT_YYYYMMDDHHMISSSSS = "yyyyMMddHHmmssSSS";

	public static final String DATE_FORMAT_MMDDHHMI = "MM-dd HH:mm";

	/**
	 * String转Date
	 * 
	 * @param dateStr
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static Date convertStringToDate(String dateStr, String format) throws ParseException {
		SimpleDateFormat simple = new SimpleDateFormat(format);
		simple.setLenient(false);
		return simple.parse(dateStr);
	}
	
	/**
	 * Date转String
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String convertDateToString(Date date, String format) {
		SimpleDateFormat formater = new SimpleDateFormat(format);
		return formater.format(date);
	}
	
	/**
	 * 获取两个日期中间的日期集合
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<String> getDays(String startTime, String endTime) {
        // 返回的日期集合
        List<String> days = new ArrayList<String>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }
	
}
