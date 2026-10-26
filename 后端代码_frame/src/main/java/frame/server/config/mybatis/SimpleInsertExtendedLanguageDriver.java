package frame.server.config.mybatis;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import com.google.common.base.CaseFormat;

/**
 * Mybatis注解增强：insert
 * 
 * @author Administrator
 *
 */
public class SimpleInsertExtendedLanguageDriver extends XMLLanguageDriver implements LanguageDriver {

	private final Pattern inPattern = Pattern.compile("\\(#\\{(\\w+)\\}\\)");

	/**
	 * Usage:
	 *   @Insert("INSERT INTO users (#{user})")
     *   @Lang(SimpleInsertLangDriver.class)
     *   int insertUserDAO(User user);
	 */
	@Override
	public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {

		Matcher matcher = inPattern.matcher(script);
		if (matcher.find()) {
			StringBuilder sb = new StringBuilder();
			StringBuilder tmp = new StringBuilder();
			sb.append("(");

			for (Field field : parameterType.getDeclaredFields()) {
				// Exclude variables modified by Invisble
				if(!field.isAnnotationPresent(Invisible.class)) {
					sb.append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()) + ",");
					tmp.append("#{" + field.getName() + "},");
				}
			}

			sb.deleteCharAt(sb.lastIndexOf(","));
			tmp.deleteCharAt(tmp.lastIndexOf(","));
			sb.append(") values (" + tmp.toString() + ")");

			script = matcher.replaceAll(sb.toString());
			script = "<script>" + script + "</script>";
		}

		return super.createSqlSource(configuration, script, parameterType);
	}
}
