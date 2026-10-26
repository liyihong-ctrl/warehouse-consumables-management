package frame.server.config.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSONObject;

import frame.server.constant.JwtClaimConstant;
import frame.server.dao.UserDao;
import frame.server.dto.UserDto;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.JwtUtil;
import frame.server.utils.StringUtil;

@Component
public class JwtFilter extends BasicHttpAuthenticationFilter {

	@Autowired
	private UserDao userDao;

	/**
	 * 解释为什么最终返回的都是true，即允许访问
	 * 返回false请求会直接被拦截，用户看不到任何东西
	 * 而返回true，控制器中可以通过其他方式来判断用户是否登入
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		if (isLoginAttempt(request, response)) {
			try {
				executeLogin(request, response);
				return true;
			} catch (Exception e) {
				if (e.getMessage().equals("token即将过期")) {
					return refreshToken(request, response);
				} else {
					this.response401(request, response, e.getMessage());
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 判断用户是否想要是否想要登入 
	 * 检测header里面是否包含Authorization字段即可
	 */
	@Override
	protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
		String token = this.getAuthzHeader(request);
		return token != null;
	}

	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String token = httpServletRequest.getHeader("Authorization");
		if(StringUtil.isBlank(token)) {
			Cookie[] cookies = httpServletRequest.getCookies();
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("Authorization")) {
					token = cookie.getName();
					break;
				}
			}
		}
		
		JwtToken jwtToken = new JwtToken(token);
		getSubject(request, response).login(jwtToken);
		return true;
	}

	/**
	 * 对跨域提供支持
	 */
	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
		httpServletResponse.setHeader("Access-Control-Allow-Headers",
				httpServletRequest.getHeader("Access-Control-Request-Headers"));
		if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
			httpServletResponse.setStatus(HttpStatus.OK.value());
			return false;
		}
		return super.preHandle(request, response);
	}

	/**
	 * 刷新token
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean refreshToken(ServletRequest request, ServletResponse response) {
		String token = this.getAuthzHeader(request);
		String account = JwtUtil.getClaim(token, JwtClaimConstant.ACCOUNT);
		if (!JwtUtil.isExpiration(token)) {
			// filter使用dao教程https://www.cnblogs.com/4king/p/11722235.html
			// Web容器初始化顺序是按照Listener-filter-servlet的顺序进行，
			// 因为dispatcherServlet是在filter之后才进行初始化，
			// 也就是这个时候我们要自动注入的bean才被初始化。
			// 所以此时在filter中自动注入的时候还没有bean，所以会注入失败。
			ServletContext context = request.getServletContext();
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
			userDao = ctx.getBean(UserDao.class);

			UserDto user = userDao.selectUserDtoByAccount(account);
			token = JwtUtil.sign(user.getAccount(), user.getPassword());
			JwtToken jwtToken = new JwtToken(token);
			this.getSubject(request, response).login(jwtToken);
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			httpServletResponse.setHeader("Authorization", token);
			httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
			return true;
		}

		return false;
	}

	/**
	 * 非法请求返回401
	 * @param req
	 * @param resp
	 * @param msg
	 */
	private void response401(ServletRequest req, ServletResponse resp, String msg) {
		HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
		httpServletResponse.setStatus(401);
		httpServletResponse.setCharacterEncoding("UTF-8");
		httpServletResponse.setContentType("application/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = httpServletResponse.getWriter();
			String data = JSONObject.toJSONString(ApiResultUtil.unauthorized());
			out.append(data);
		} catch (IOException e) {

		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

}
