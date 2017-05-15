package com.qianbao.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

import com.qianbao.util.HttpClentSSL;
import static com.qianbao.servlet.PBConstant.*;

public class PBServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String m = req.getParameter("m");
		try {
			Method method = this.getClass().getDeclaredMethod(m, HttpServletRequest.class, HttpServletResponse.class);
			method.invoke(this, req, resp);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	private CloseableHttpClient httpclient = null;
	public void checkcode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		httpclient = HttpClentSSL.createHttpclient();
		HttpGet httpGet = new HttpGet(PB_DOMAIN_LOGREG);
		HttpResponse response = httpclient.execute(httpGet);
		httpGet = new HttpGet(PB_DOMAIN_CHECKCODE.concat("?a=" + System.nanoTime()));
		httpGet.setHeader("Origin", "https://ipcrs.pbccrc.org.cn");
		httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
		httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4");
		httpGet.setHeader("Connection", "keep-alive");
		httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpGet.setHeader("Host", "ipcrs.pbccrc.org.cn");// 带上第一次请求的Cookie
		httpGet.setHeader("Referer", PB_DOMAIN_LOGREG);
		httpGet.setHeader("Upgrade-Insecure-Requests", "1");
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Trident/7.0; rv:11.0) like Gecko");
		httpGet.setHeader("Pragma", "no-cache");
		response = httpclient.execute(httpGet);
		if (response != null && response.getStatusLine().getStatusCode() == 200) {
			HttpEntity resEntity = response.getEntity();
			byte[] bb = IOUtils.toByteArray(resEntity.getContent());
			resp.getOutputStream().write(bb);
			resp.getOutputStream().close();
		}
	}

	public void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String secret = req.getParameter("secret");
				
		HttpPost loginPost = new HttpPost(PB_DOMAIN_LOGIN);// 创建登录的Post请求
		loginPost.setHeader("Origin", "https://ipcrs.pbccrc.org.cn");
		loginPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		loginPost.setHeader("Accept-Encoding", "gzip, deflate, br");
		loginPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4");
		loginPost.setHeader("Connection", "keep-alive");
		loginPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		// loginPost.setHeader("Cookie", cookie);// 带上第一次请求的Cookie
		loginPost.setHeader("Host", "ipcrs.pbccrc.org.cn");// 带上第一次请求的Cookie
		loginPost.setHeader("Referer", PB_DOMAIN_LOGREG);
		loginPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Trident/7.0; rv:11.0) like Gecko");

		List<NameValuePair> nameValuePairLogin = new ArrayList<NameValuePair>();// 封装Post提交参数
		nameValuePairLogin.add(new BasicNameValuePair("loginname", username));// 学号
		nameValuePairLogin.add(new BasicNameValuePair("password", password));// 密码
		nameValuePairLogin.add(new BasicNameValuePair("_@IMGRC@_", secret));// 验证码
		nameValuePairLogin.add(new BasicNameValuePair("method", "login"));
		nameValuePairLogin.add(new BasicNameValuePair("date", String.valueOf(System.nanoTime())));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairLogin, "GB2312");
		loginPost.setEntity(entity);
		HttpResponse response = httpclient.execute(loginPost);
		int statuscode = response.getStatusLine().getStatusCode();
		if (statuscode == 200) {
			String html = getHtml(response.getEntity().getContent(), "GB2312");
			System.out.println("html:" + html);
			HttpGet WelHttpGet = new HttpGet(PB_DOMAIN_WELCOME);
			response = httpclient.execute(WelHttpGet);
			if (statuscode == 200) {
				String ret = getHtml(response.getEntity().getContent(), "GB2312");
				System.out.println("html:" + ret);
				setCookieStore(response);
			}
		}
	}

	public static void setCookieStore(HttpResponse httpResponse) {

		BasicCookieStore cookieStore = new BasicCookieStore();
		// JSESSIONID
		String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
		String JSESSIONID = setCookie.substring("JSESSIONID=".length(), setCookie.indexOf(";"));
		// 新建一个Cookie
		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", JSESSIONID);
		cookie.setVersion(0);
		cookie.setDomain("ipcrs.pbccrc.org.cn");
		cookie.setPath("/");
		cookieStore.addCookie(cookie);
	}

	public static String getHtml(InputStream is, String encoding) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		is.close();
		return new String(bos.toByteArray(), encoding);
	}
}
