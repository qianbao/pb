package wechat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
/*
 * 利用HttpClient进行post请求的工具类
 */
public class HttpClientUtil {
	public String doPost(String url,String charset){
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		String result = null;
		try{
			
			httpClient = new SSLClient();
			httpGet = new HttpGet(url);
			httpGet.setHeader("connection", "close");
			httpGet.setHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			//设置参数
			HttpResponse response = httpClient.execute(httpGet);
			if(response != null&&response.getStatusLine().getStatusCode()==200){
				HttpEntity resEntity = response.getEntity();
				OutputStream os = new FileOutputStream(new File("d://","secretCode.png"), true); 
				byte[] bb = IOUtils.toByteArray(resEntity.getContent());
				System.out.println(bb.length);
				IOUtils.write(bb, os);
				if(resEntity != null){
					
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;
	}
	

}