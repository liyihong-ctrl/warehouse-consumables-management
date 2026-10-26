package frame.server.aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.JwtClaimConstant;
import frame.server.domain.Operation;
import frame.server.service.OperationService;
import frame.server.utils.IpUtil;
import frame.server.utils.JwtUtil;
import frame.server.utils.RandomUtil;
import frame.server.utils.StringUtil;

/**
 * 切面处理类
 * 系统日志
 * 
 * @author Administrator
 *
 */
@Aspect
@Component
public class OperationAspect {
	
	@Autowired
	private OperationService operationService;

	/**
	 * 定义切点 @Pointcut
	 * 在注解的位置切入代码
	 */
	@Pointcut("@annotation(frame.server.annotation.OperationAnnotation)")
	public void OperationPoinCut() {

	}

	/**
	 * 切面 配置通知
	 * @param joinPoint
	 */
	@AfterReturning("OperationPoinCut()")
	public void saveSipLog(JoinPoint joinPoint) {
		Operation operation = new Operation();
		
		// 设置操作履历ID
		operation.setId(RandomUtil.genarateId());

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

		// 保存用户ID
		String token = request.getHeader("Authorization");
		if (StringUtil.isBlank(token)) {
			operation.setUserId("");
		} else {
			operation.setUserId(operationService.getUserId(JwtUtil.getClaim(token, JwtClaimConstant.ACCOUNT)));
		}

		// 从切面织入点处通过反射机制获取织入点处的方法
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		
		// 获取切入点所在的方法
		Method method = signature.getMethod();
		
		OperationAnnotation operationAnnotation = method.getAnnotation(OperationAnnotation.class);
		if (operationAnnotation != null) {
			// 保存操作
			operation.setOperation(operationAnnotation.value());
		}

		// 获取请求的类名
		String className = joinPoint.getTarget().getClass().getName();
		
		// 获取请求的方法名
		String methodName = method.getName();
		
		// 保存请求路径
		operation.setMethod(className + "." + methodName);

		// 请求的参数
		Object[] args = joinPoint.getArgs();
		if (args != null && args.length > 0) {
			List<Object> list = new ArrayList<Object>();
			for(int i = 0; i < args.length; i++) {
				if(args[i].getClass() != BeanPropertyBindingResult.class) {
					list.add(args[i]);
				}
			}
			
			// 保存请求参数
			operation.setParams(JSON.toJSONString(list));
		} else {
			operation.setParams("");
		}

		// 请求的IP地址
		String ip = IpUtil.getIpAddr(request);
		
		// 保存请求IP地址
		operation.setIp(ip);

		operation.setCreateTime(new Date());

		operationService.saveOperation(operation);
	}

}
