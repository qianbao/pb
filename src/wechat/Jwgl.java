package wechat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.lang.model.util.Elements;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
  
public class Jwgl {
  private static String stuNumber = "";
  private static String stuName = "";
  private static String Cookie = "";
  private String indexUrl = GlobalConstant.INDEX_URL;
  private String secretCodeUrl = GlobalConstant.SECRETCODE_URL;
  private String loginUrl = GlobalConstant.LOGIN_URL;
  private String mainUrl = GlobalConstant.MAIN_URL;
  private String queryClassroomUrl = GlobalConstant.QUERY_CLASSROOM_URL;
  private String queryClassroomGnmkdm = GlobalConstant.QUERY_CLASSROOM_GNMKDM;
  private String queryStuGradeUrl = GlobalConstant.QUERY_STU_GRADE_URL;
  private String queryStuGradeGnmkd = GlobalConstant.QUERY_STU_GRADE_GNMKDM;
  private String queryStuCourseUrl = GlobalConstant.QUERY_STU_COURSE_URL;
  private String queryStuCourseGnmkd = GlobalConstant.QUERY_STU_COURSE_GNMKDM;
  private String xixiaoqu = GlobalConstant.XIXIAOQU;
  private String identityStu = GlobalConstant.IDENTITY_STU;
  
  /**
   * ��¼����
   * 
   * @param stuNumber
   * @param password
   * @return
   * @throws Exception
   * @throws UnsupportedOperationException
   */
  public boolean login(String stuNumber, String password)
      throws UnsupportedOperationException, Exception {
    this.stuNumber = stuNumber;
    // ��ȡ��֤��
    HttpGet secretCodeGet = new HttpGet(secretCodeUrl);
    CloseableHttpClient client = HttpClients.createDefault();
   
    KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
    FileInputStream instream = new FileInputStream(new File("D:/cpdg/my.truststore"));
	trustStore.load(instream, "123123".toCharArray());
	SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
	socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	Scheme sch =new Scheme(  "https", socketFactory, 443);
	client.getConnectionManager().getSchemeRegistry().register(sch);
    
    CloseableHttpResponse responseSecret = client.execute(secretCodeGet);
    // ��ȡ���ص�Cookie
    Cookie = responseSecret.getFirstHeader("Set-Cookie").getValue();
    String viewState = IOUtils.getViewState(indexUrl, "", "");
    // ����֤�����ص�C��
    IOUtils.getSecret(responseSecret.getEntity().getContent(),
        "secretCode.png", "d://");
    Scanner sc = new Scanner(System.in);
    System.out.println("��������֤�룺");
    // �ֶ����ղŻ�ȡ����֤���ֵ
    String secret = sc.next().trim();
    HttpPost loginPost = new HttpPost(loginUrl);// ������¼��Post����
    loginPost.setHeader("Cookie", Cookie);// ���ϵ�һ�������Cookie
    List<NameValuePair> nameValuePairLogin = new ArrayList<NameValuePair>();// ��װPost�ύ����
    nameValuePairLogin
        .add(new BasicNameValuePair("__VIEWSTATE", viewState));// ���ر�ֵ
    nameValuePairLogin
        .add(new BasicNameValuePair("loginname", stuNumber));// ѧ��
    nameValuePairLogin.add(new BasicNameValuePair("password", password));// ����
    nameValuePairLogin.add(new BasicNameValuePair("_@IMGRC@_", secret));// ��֤��
    nameValuePairLogin.add(new BasicNameValuePair("method",
        "login"));// ���,Ĭ��ѧ��
    nameValuePairLogin.add(new BasicNameValuePair("date", "1493804031088"));
    
    
    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
        nameValuePairLogin, "GB2312");
    loginPost.setEntity(entity);
    HttpResponse responseLogin = client.execute(loginPost);
    // client1.close();
    // ������:�ж��ύ�����Ƿ�ɹ����ɹ�����302
    if (responseLogin.getStatusLine().getStatusCode() == 302) {
      // ����ύ�ɹ�������Cookie�����ض����mainҳ�棬����ȡѧ������
      HttpGet mainGet = new HttpGet(mainUrl + stuNumber);
      mainGet.setHeader("Cookie", Cookie);
      mainGet.setHeader("Referer", loginUrl);
      HttpResponse responseMain = client.execute(mainGet);
      InputStream is = responseMain.getEntity().getContent();
      String html = "";
      try {
        html = IOUtils.getHtml(is, "GB2312");
      } catch (Exception e) {
        System.out.println("����htmlʧ�ܣ�");
        e.printStackTrace();
      }
     // stuName = Jsoup.parse(html).getElementById("xhxm").text();
      System.out.println("��¼�ɹ�����ӭ����" + stuName);
      client.close();
      return true;
    } else {
      System.out.println("��¼ʧ�ܣ�");
      client.close();
      return false;
    }
  
  }
  
  /**
   * ��ѯ�ս���
   * 
   * @throws Exception
   * 
   * @throws Exception
   */
  public void queryClassroom(String xiaoqu, String xqj, String sjd)
      throws Exception {
  
    CloseableHttpClient client = HttpClients.createDefault();
    String newQueryClassrommUrl = queryClassroomUrl + stuNumber + "&xm="
        + stuName + queryClassroomGnmkdm;// ƴ�������Url
    String parseSjd = ParseUtils.parseWeek(sjd);// ������ǰ�ڴζ�Ӧ���ַ���
    String nowWeek = DateUtils.getWeek() + "";// ��ȡ��ǰʱ���ǵڼ���
    String viewState = IOUtils.getViewState(newQueryClassrommUrl, Cookie,
        mainUrl + stuNumber);
    // ��װ��ѯ�ս����������
    List<NameValuePair> queryClassroomPair = new ArrayList<NameValuePair>();
    queryClassroomPair.add(new BasicNameValuePair("__EVENTTARGET", ""));
    queryClassroomPair.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
    queryClassroomPair
        .add(new BasicNameValuePair("__VIEWSTATE", viewState));
    queryClassroomPair.add(new BasicNameValuePair("xiaoq", xiaoqu));// У�����ͣ�Ĭ����У��
    queryClassroomPair.add(new BasicNameValuePair("jslb", ""));// �������Ĭ��Ϊ��
    queryClassroomPair.add(new BasicNameValuePair("min_zws", "0"));// ��С��λ����Ĭ��Ϊ0
    queryClassroomPair.add(new BasicNameValuePair("max_zws", ""));// �����λ����Ĭ��Ϊ��
    queryClassroomPair.add(new BasicNameValuePair("ddlKsz", nowWeek));// ��ʼ�ܣ�Ĭ�ϵ�ǰ��
    queryClassroomPair.add(new BasicNameValuePair("ddlJsz", nowWeek));// �����ܣ�Ĭ�ϵ�ǰ��
    queryClassroomPair.add(new BasicNameValuePair("xqj", xqj));// ���ڼ�,Ĭ�ϵ���
    queryClassroomPair.add(new BasicNameValuePair("ddlDsz", ""));// ��˫�ܣ�Ĭ��
    queryClassroomPair.add(new BasicNameValuePair("sjd", parseSjd));// �ڼ���
    queryClassroomPair.add(new BasicNameValuePair("Button2", "�ս��Ҳ�ѯ"));
    queryClassroomPair.add(new BasicNameValuePair("xn", "2015-2016"));
    queryClassroomPair.add(new BasicNameValuePair("xq", "2"));
    queryClassroomPair.add(new BasicNameValuePair("ddlSyXn", "2015-2016"));
    queryClassroomPair.add(new BasicNameValuePair("ddlSyxq", "2"));
    UrlEncodedFormEntity entityClassroom = new UrlEncodedFormEntity(
        queryClassroomPair);
  
    HttpPost queryClassroomPost = new HttpPost(newQueryClassrommUrl);
    // newQueryClassrommUrlʾ��:http://jwgl2.ujn.edu.cn/xxjsjy.aspx?xh=20121214104&xm=XXX&gnmkdm=N121611
    queryClassroomPost.setEntity(entityClassroom);
    queryClassroomPost.setHeader("Referer", mainUrl + stuNumber);// ����ͷ��Ϣ
    queryClassroomPost.setHeader("Cookie", Cookie);
    HttpResponse responseClassroom = client.execute(queryClassroomPost);
    InputStream is = responseClassroom.getEntity().getContent();
    String html = IOUtils.getHtml(is, "GB2312");
    
    client.close();
  }
  
  /**
   * ���ز�ѯ�ս��ҷ�����Ĭ��ʱ�䣬�γ̽ڴε��޲�����ѯ����
   * 
   * @throws IOException
   * @throws ClientProtocolException
   */
  public void queryClassroom() throws ClientProtocolException, IOException,
      Exception {
    String weekDay = DateUtils.getWeekDay() + "";// ��ȡ��ǰʱ�������ڼ�
    String sdj = DateUtils.getNowCourse() + "";// ��ȡ��ǰʱ���ǵڼ��ڿ�
    new Jwgl().queryClassroom(xixiaoqu, weekDay, sdj);
  }
  
  /**
   * ��ѯ���˳ɼ�����
   * 
   * @throws ClientProtocolException
   * @throws IOException
   */
  public void queryStuGrade(String xn, String xq)
      throws ClientProtocolException, IOException {
    CloseableHttpClient client = HttpClients.createDefault();
    String newQueryStuGradeUrl = queryStuGradeUrl + stuNumber + "&xm="
        + stuName + queryStuGradeGnmkd;
    HttpPost queryGradePost = new HttpPost(newQueryStuGradeUrl);
    String viewState = IOUtils.getViewState(newQueryStuGradeUrl, Cookie,
        mainUrl + stuNumber);
    // ��װ�������
    List<NameValuePair> queryGradePair = new ArrayList<NameValuePair>();
    queryGradePair.add(new BasicNameValuePair("__EVENTTARGET", ""));
    queryGradePair.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
    queryGradePair.add(new BasicNameValuePair("__VIEWSTATE", viewState));
    queryGradePair.add(new BasicNameValuePair("hidLanguage", ""));
    queryGradePair.add(new BasicNameValuePair("ddlXN", xn));// ѧ��
    queryGradePair.add(new BasicNameValuePair("ddlXQ", xq));// ѧ��
    queryGradePair.add(new BasicNameValuePair("ddl_kcxz", ""));
    queryGradePair.add(new BasicNameValuePair("btn_xq", "ѧ�ڳɼ�"));
    queryGradePost.setHeader("Cookie", Cookie);
    queryGradePost.setHeader("Referer", mainUrl + stuNumber);
    UrlEncodedFormEntity entityGrade = new UrlEncodedFormEntity(
        queryGradePair);
    queryGradePost.setEntity(entityGrade);
    HttpResponse responQueryGradePost = client.execute(queryGradePost);
  
    String gradeHtml = IOUtils.getHtml(responQueryGradePost.getEntity()
        .getContent(), "GB2312");
    System.out.println(gradeHtml);
  
  }
  
  /**
   * ��ѯ���˿α���
   * 
   * @param xnd
   * @param xqd
   * @throws ClientProtocolException
   * @throws IOException
   */
  public void queryStuCourse(String xnd, String xqd)
      throws ClientProtocolException, IOException {
    CloseableHttpClient client = HttpClients.createDefault();
    String newQueryStuCourseUrl = queryStuCourseUrl + stuNumber + "&xm="
        + stuName + queryStuCourseGnmkd;
    String viewState = IOUtils.getViewState(newQueryStuCourseUrl, Cookie,
        mainUrl + stuNumber);
    HttpPost queryStuCoursePost = new HttpPost(newQueryStuCourseUrl);
    List<NameValuePair> stuCoursePair = new ArrayList<NameValuePair>();
    stuCoursePair.add(new BasicNameValuePair("__EVENTTARGET", "xqd"));
    stuCoursePair.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
    stuCoursePair.add(new BasicNameValuePair("__VIEWSTATE", viewState));
    stuCoursePair.add(new BasicNameValuePair("xnd", xnd));
    stuCoursePair.add(new BasicNameValuePair("xqd", xqd));
    UrlEncodedFormEntity entitySource = new UrlEncodedFormEntity(
        stuCoursePair);
    queryStuCoursePost.setEntity(entitySource);
    queryStuCoursePost.setHeader("Cookie", Cookie);
    queryStuCoursePost.setHeader("Referer", mainUrl + stuNumber);
    HttpResponse responseStuCourse = client.execute(queryStuCoursePost);
    String html = IOUtils.getHtml(responseStuCourse.getEntity()
        .getContent(), "GB2312");
    System.out.println(html);
  }
  
  public static void main(String[] args) {
    Jwgl jw = new Jwgl();
    try {
      jw.login("qq773152", "ericsson");
      System.out.println("��ѯ�ɼ�����-------");
      jw.queryStuGrade("2015-2016", "1");
      // ��ѯ��У������һ����12�ڿս��Ҳ��ԡ�
      // jw.queryClassroom("1", "1", "2");
      System.out.println("��ѯ�ս��Ҳ���------");
      jw.queryClassroom();
      System.out.println("��ѯ���˿α����-------");
      jw.queryStuCourse("2014-2015", "1");
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    //QQ:451209214
  }
  
}