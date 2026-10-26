package frame.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import frame.server.constant.JwtClaimConstant;
import frame.server.dto.UserDto;
import frame.server.service.BaseService;
import frame.server.utils.JwtUtil;
import frame.server.utils.StringUtil;

/**
 * 全局父类Controller
 * 
 * @author Administrator
 *
 */
@Component
public class BaseController {
	
	@Autowired
	private BaseService baseService;
	
	/**
	 * 获取当前登录用户的账号
	 * 
	 * @return
	 */
	public String getAccount() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		
		String token = request.getHeader("Authorization");
		if (StringUtil.isBlank(token)) {
			return null;
		}

		String account = JwtUtil.getClaim(token, JwtClaimConstant.ACCOUNT);
		return account;
	}
	
	/**
	 * 获取当前登录用户的信息
	 * 
	 * @return
	 */
	public UserDto getLoginUserInfo() {
		return baseService.getUserDtoByAccount(getAccount());
	}
	
	/**
	 * 获取当前登录用户的ID
	 * 
	 * @return
	 */
	public String getUserId() {
		return getLoginUserInfo().getId();
	}
	
	/**
	 * 获取当前登录用户的类型
	 * 
	 * @return
	 */
	public String getUserType() {
		return getLoginUserInfo().getType();
	}
	
	/**
	 * 仓库管理系统：校验当前登录用户是否有权限操作指定仓库
	 * 
	 * @return
	 */
	public boolean validateUserWarehouse(String warehouseId) {
		return baseService.getWarehouseByUserIdAndWarehouseId(getUserId(), warehouseId) != null;
	}

}
