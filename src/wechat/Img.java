package wechat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Img {

	public static void main(String[] args) {
		
		try {
			HttpGet secretCodeGet = new HttpGet("https://ipcrs.pbccrc.org.cn/imgrc.do?0.3949698929306751");
			SSLClient client = new SSLClient();
			CloseableHttpResponse responseSecret = client.execute(secretCodeGet);
			String cookie = responseSecret.getFirstHeader("Set-Cookie").getValue();
			OutputStream os = new FileOutputStream(new File("d://","secretCode.png"), true); 
			byte[] bb = IOUtils.toByteArray(responseSecret.getEntity().getContent());
			IOUtils.write(bb, os);
			
			
	        Scanner sc = new Scanner(System.in);
	        System.out.println("«Î ‰»Î—È÷§¬Î£∫");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	  
}
