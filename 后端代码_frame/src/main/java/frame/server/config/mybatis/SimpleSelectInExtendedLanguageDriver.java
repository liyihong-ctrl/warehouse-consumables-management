package frame.server.config.mybatis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * Mybatis注解增强：select in
 * 
 * @author Administrator
 *
 */
public class SimpleSelectInExtendedLanguageDriver extends XMLLanguageDriver implements LanguageDriver {
	
	private final Pattern inPattern = Pattern.compile("\\(#\\{(\\w+)\\}\\)");

	/**
	 * Usage:
	 *   @Lang(SimpleSelectInExtendedLanguageDriver.class)
     *   @Select("SELECT * FROM users WHERE id IN (#{userIds})")
     *   List<User> selectUsers(@Param("userIds") List<String> userIds);
	 */
	/**
	 * Compiled grammar:
	 *   @Select({"<script>",
	 *            "SELECT *", 
     *            "FROM user",
     *            "WHERE id IN", 
     *            "<foreach item='item' index='index' collection='list'",
     *              "open='(' separator=',' close=')'>",
     *              "#{item}",
     *            "</foreach>",
     *            "</script>"}) 
	 *   List<User> selectUsers(@Param("userIds") List<Intger> userIds);
	 */
    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        
        Matcher matcher = inPattern.matcher(script);
        if (matcher.find()) {
            script = matcher.replaceAll("(<foreach collection=\"$1\" item=\"__item\" separator=\",\" >#{__item}</foreach>)");
        }
        
        script = "<script>" + script + "</script>";
        return super.createSqlSource(configuration, script, parameterType);
    }
    
}
