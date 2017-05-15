package wechat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import com.qianbao.util.HttpClentSSL;

public class WechatServlet extends HttpServlet {
	private static String PB_DOMAIN = "https://ipcrs.pbccrc.org.cn/";
	private static String PB_DOMAIN_LOGIN = PB_DOMAIN.concat("page/login/loginreg.jsp");
	private static String PB_DOMAIN_CHECKCODE = PB_DOMAIN.concat("imgrc.do");
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		CloseableHttpClient httpclient = HttpClentSSL.createHttpclient();
		HttpGet httpGet = new HttpGet(PB_DOMAIN_LOGIN);
		HttpResponse response  = httpclient.execute(httpGet);
		String cookie = response.getFirstHeader("Set-Cookie").getValue();
		
		HttpGet imgGet = new HttpGet(PB_DOMAIN_CHECKCODE.concat("?a="+System.nanoTime()));
		
		imgGet.setHeader("Cookie", cookie);// 带上第一次请求的Cookie
		imgGet.setHeader("Referer", PB_DOMAIN_LOGIN);
		imgGet.setHeader("Accept", "image/webp,image/*,*/*;q=0.8");
		imgGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
		imgGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4");
		imgGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36");
		imgGet.setHeader("Pragma", "no-cache");
		
		response = httpclient.execute(imgGet);
		if(response != null&&response.getStatusLine().getStatusCode()==200){
			HttpEntity resEntity = response.getEntity();
			byte[] bb = IOUtils.toByteArray(resEntity.getContent());
			resp.getOutputStream().write(bb);
		}
	}
	

	

}
