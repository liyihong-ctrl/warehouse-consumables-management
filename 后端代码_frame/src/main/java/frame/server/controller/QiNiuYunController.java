package frame.server.controller;

import java.io.FileInputStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import frame.server.utils.ApiResultUtil;
import frame.server.utils.RandomUtil;
import frame.server.vo.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 七牛云操作Api
 * 该测试的七牛云配置会不定时关闭，所以请配置自己的参数。
 * 1. 设置:
 *      QINIU_DOMAIN
 *      ACCESS_KEY
 *      SECRET_KEY
 *      BUCKET
 *      PATH
 * 2. 重新设置对应权限
 *      当前权限: 登录用户即可
 * 
 * @author Administrator
 *
 */
@Api(tags = "七牛云控制器")
@RestController
@RequestMapping("/qny")
@SuppressWarnings("deprecation")
public class QiNiuYunController {
	
	private final String QINIU_DOMAIN = System.getenv().getOrDefault("QINIU_DOMAIN", "");

	private final String ACCESS_KEY = System.getenv().getOrDefault("QINIU_ACCESS_KEY", "");

    private final String SECRET_KEY = System.getenv().getOrDefault("QINIU_SECRET_KEY", "");

    private final String BUCKET = System.getenv().getOrDefault("QINIU_BUCKET", "");

    private final String PATH = System.getenv().getOrDefault("QINIU_PATH", "");
    
    /**
     * 通过服务器直接上传图片至七牛云
     * 
     * @param multipartFile
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "上传图片至七牛云")
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ApiResult uploadImg(@RequestParam("file") MultipartFile multipartFile) throws Exception {
		FileInputStream file = (FileInputStream) multipartFile.getInputStream();
		Configuration cfg = new Configuration(Zone.zone0());
		UploadManager uploadManager = new UploadManager(cfg);
		Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String upToken = auth.uploadToken(BUCKET);
        String key = PATH + "/images/" + System.currentTimeMillis() + "/" + RandomUtil.genarateId();
        Response response = uploadManager.put(file, key, upToken, null, null);
        DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
		String return_path = QINIU_DOMAIN + "/" + putRet.key;
		JSONObject res = new JSONObject();
		res.put("imgPath", return_path);
        return ApiResultUtil.success(res);
	}
    
    /**
     * 通过获取七牛云token直接上传至七牛云服务器
     * 
     * @return
     */
    @ApiOperation(value = "获取七牛云上传图片Token")
	@RequestMapping(value = "/uploadToken", method = RequestMethod.GET)
    public ApiResult uploadToken() {
    	Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY); 
    	String upToken = auth.uploadToken(BUCKET,null, 1000 * 60 * 6, null);
        return ApiResultUtil.success(upToken);
    }

}
