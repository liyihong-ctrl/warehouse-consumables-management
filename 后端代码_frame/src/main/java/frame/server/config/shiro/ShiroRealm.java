package frame.server.config.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import frame.server.config.jwt.JwtToken;
import frame.server.constant.JwtClaimConstant;
import frame.server.dao.UserDao;
import frame.server.domain.Permission;
import frame.server.dto.RoleDto;
import frame.server.dto.UserDto;
import frame.server.utils.JwtUtil;
import frame.server.utils.StringUtil;

public class ShiroRealm extends AuthorizingRealm {

	@Autowired
	private UserDao userDao;

	/**
	 * 必须重写此方法，不然Shiro会报错
	 */
	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof JwtToken;
	}

	/**
	 * 只有当需要检测用户权限的时候才会调用此方法
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String account = JwtUtil.getClaim(principals.toString(), JwtClaimConstant.ACCOUNT);
		
		UserDto user = userDao.selectEnabledUserDtoByAccount(account);
		
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		
		if (user != null) {
			for (RoleDto role : user.getRoles()) {
				simpleAuthorizationInfo.addRole(role.getName());
			}
			for (Permission permission : user.getPermissions()) {
				simpleAuthorizationInfo.addStringPermission(permission.getStr());
			}
		}
		
		return simpleAuthorizationInfo;
	}

	/**
	 * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
		String token = (String) auth.getCredentials();

		String account = JwtUtil.getClaim(token, JwtClaimConstant.ACCOUNT);

		if (StringUtil.isBlank(account)) {
			throw new AuthenticationException("token无效");
		}

		UserDto user = userDao.selectUserDtoByAccount(account);
		if (user == null) {
			throw new AuthenticationException("用户不存在");
		}

		if (!JwtUtil.verify(token, account, user.getPassword())) {
			throw new AuthenticationException("用户名或密码错误");
		}

		if (JwtUtil.isExpiration(token)) {
			throw new AuthenticationException("token过期");
		}

		if (JwtUtil.isAfterExpiration(token)) {
			throw new AuthenticationException("token即将过期");
		}

		SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(token, token, getName());
		return simpleAuthenticationInfo;
	}

}
