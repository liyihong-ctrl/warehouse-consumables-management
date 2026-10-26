package frame.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import frame.server.dao.OperationDao;
import frame.server.dao.UserDao;
import frame.server.domain.Operation;
import frame.server.domain.User;
import frame.server.dto.OperationDto;
import frame.server.service.OperationService;
import frame.server.vo.Page;

@Service("operationService")
public class OperationServiceImpl implements OperationService {
	
	@Autowired
	private OperationDao operationDao;
	
	@Autowired
	private UserDao userDao;

	@Override
	public String getUserId(String account) {
		User user = userDao.selectUserByAccount(account);
		if(user == null) {
			return null;
		}
		
		return user.getId();
	}

	@Override
	public void saveOperation(Operation operation) {
		operationDao.insert(operation);
	}

	@Override
	public PageInfo<OperationDto> getLogs(Page page) {
		PageHelper.startPage(page.getPageNum(), page.getPageSize());
		PageInfo<OperationDto> pageInfo = null;
		
		// 提示：根据Page.searchCondition解析出检索条件进行分类筛选
		
		pageInfo = new PageInfo<>(operationDao.selectAll());
		return pageInfo;
	}

}
