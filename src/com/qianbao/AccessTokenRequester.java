package com.qianbao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;

public class AccessTokenRequester {

	private static final String APPID  = "17050809645171";
	private static final String SECRET = "E99B83F5-36CF-4E82-B3CC-9B45019CBEA9";
	private static final String URL    = "http://api.dataduoduo.com/cgi/token";

	private AccessTokenRequester () {

	}

	private String generate(String appid, String secret) {
		String params = "appid=" + appid + "&secret=" + secret;
		String result = doGet(URL, params);
		String token = null;
		if (code == 200) {
			System.out.println("�����������:" + result);
			// �����л�result json string
			// ������Լ�����&���̵�ʵ���������...
			// ʾ��������ʹ�õ�ΪJackson
//			try {
//				ResponseObj ro = ResponseObj.fromString(result);
//				if (ro.getCode() == 0) {
//					LinkedHashMap map = (LinkedHashMap) ro.getResult();
//					token = (String) map.get("token");
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		return token;
	}

	// HttpURLConnection ResponseCode
	private int code = 0;

	/**
	 * ����һ��http do get ����
	 * 
	 * @param url
	 *            url�ַ���
	 * @param param
	 *            �����ַ���
	 * @return String ���صĽ����json �ַ���
	 */
	private String doGet(String url, String param) {
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		try {
			URL getUrl;
			url = url + "?" + param;
			getUrl = new URL(url);
			connection = (HttpURLConnection) getUrl.openConnection();
			connection.connect();
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			code = connection.getResponseCode();
			StringBuffer sb = new StringBuffer();
			String lines = null;
			while ((lines = reader.readLine()) != null) {
				sb.append(lines);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (connection != null)
					connection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ����һ�� access token
	 */
	public static String generateToken() {
		return new AccessTokenRequester().generate(APPID, SECRET);
	}

	public static void main(String args[]) {

		System.out.println("Access Token:" + generateToken());

	}

}