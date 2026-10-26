package frame.server.config.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;

/**
 * 设置shiro角色管理为：多角色or关系
 * 说明：
 *    The logical operation for the permission check in case multiple roles are specified. AND is the default
 *    shiro自带的角色管理是AND关系，也就是说同一时间仅能满足一个角色，与我们使用习惯不符。
 *    所以重写它的过滤方法
 *    
 * @author Administrator
 *
 */
public class RoleFilter extends RolesAuthorizationFilter {

    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
            throws IOException {

        final Subject subject = getSubject(request, response);
        final String[] rolesArray = (String[]) mappedValue;

        if (rolesArray == null || rolesArray.length == 0) {
            // 无指定角色时，无需检查，允许访问
            return true;
        }

        for (String roleName : rolesArray) {
            if (subject.hasRole(roleName)) {
                return true;
            }
        }

        return false;
    }
    
}