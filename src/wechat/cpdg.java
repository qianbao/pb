package wechat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.KeyStore;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
/**
 * 利用HttpClient，模拟https连接
 * 使用4.1版本
 * @since 2011.7.7
 */
public class cpdg{
	private static String loginUrl = "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp";
	private static HttpClient httpclient;
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
	
		httpclient = new DefaultHttpClient();
//		获得密匙库
		KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());

		FileInputStream instream = new FileInputStream(new File("D:/cpdg/my.truststore"));
		trustStore.load(instream, "123123".toCharArray());
		SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
		socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		Scheme sch =new Scheme(  "https", socketFactory, 443);
		httpclient.getConnectionManager().getSchemeRegistry().register(sch);
		//获得HttpGet对象
		HttpGet httpGet = null;
		httpGet = new HttpGet(loginUrl);
		
		
		//发送请求
		HttpResponse response = httpclient.execute(httpGet);
		String cookie = response.getFirstHeader("Set-Cookie").getValue();
		
		httpclient = null;
		httpclient  = new DefaultHttpClient();
		httpclient.getConnectionManager().getSchemeRegistry().register(sch);
		
		HttpGet imgGet = new HttpGet("https://ipcrs.pbccrc.org.cn/imgrc.do");
		
		imgGet.setHeader("Cookie", cookie);// 带上第一次请求的Cookie
		imgGet.setHeader("Referer", loginUrl);
		imgGet.setHeader("Accept", "image/webp,image/*,*/*;q=0.8");
		imgGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
		imgGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4");
		imgGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36");
		imgGet.setHeader("Pragma", "no-cache");
		
		response = httpclient.execute(imgGet);
		if(response != null&&response.getStatusLine().getStatusCode()==200){
			HttpEntity resEntity = response.getEntity();
			OutputStream os = new FileOutputStream(new File("d://cpdg//","secretCode.png"), true); 
			byte[] bb = IOUtils.toByteArray(resEntity.getContent());
			System.out.println(bb.length);
			IOUtils.write(bb, os);
			if(resEntity != null){
				
			}
		}
		
	
    }
}

