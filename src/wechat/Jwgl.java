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
   * 登录功能
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
    // 获取验证码
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
    // 获取返回的Cookie
    Cookie = responseSecret.getFirstHeader("Set-Cookie").getValue();
    String viewState = IOUtils.getViewState(indexUrl, "", "");
    // 将验证码下载到C盘
    IOUtils.getSecret(responseSecret.getEntity().getContent(),
        "secretCode.png", "d://");
    Scanner sc = new Scanner(System.in);
    System.out.println("请输入验证码：");
    // 手动填充刚才获取的验证码的值
    String secret = sc.next().trim();
    HttpPost loginPost = new HttpPost(loginUrl);// 创建登录的Post请求
    loginPost.setHeader("Cookie", Cookie);// 带上第一次请求的Cookie
    List<NameValuePair> nameValuePairLogin = new ArrayList<NameValuePair>();// 封装Post提交参数
    nameValuePairLogin
        .add(new BasicNameValuePair("__VIEWSTATE", viewState));// 隐藏表单值
    nameValuePairLogin
        .add(new BasicNameValuePair("loginname", stuNumber));// 学号
    nameValuePairLogin.add(new BasicNameValuePair("password", password));// 密码
    nameValuePairLogin.add(new BasicNameValuePair("_@IMGRC@_", secret));// 验证码
    nameValuePairLogin.add(new BasicNameValuePair("method",
        "login"));// 身份,默认学生
    nameValuePairLogin.add(new BasicNameValuePair("date", "1493804031088"));
    
    
    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
        nameValuePairLogin, "GB2312");
    loginPost.setEntity(entity);
    HttpResponse responseLogin = client.execute(loginPost);
    // client1.close();
    // 第三步:判断提交数据是否成功，成功返回302
    if (responseLogin.getStatusLine().getStatusCode() == 302) {
      // 如果提交成功，带着Cookie请求重定向的main页面，并获取学生姓名
      HttpGet mainGet = new HttpGet(mainUrl + stuNumber);
      mainGet.setHeader("Cookie", Cookie);
      mainGet.setHeader("Referer", loginUrl);
      HttpResponse responseMain = client.execute(mainGet);
      InputStream is = responseMain.getEntity().getContent();
      String html = "";
      try {
        html = IOUtils.getHtml(is, "GB2312");
      } catch (Exception e) {
        System.out.println("解析html失败！");
        e.printStackTrace();
      }
     // stuName = Jsoup.parse(html).getElementById("xhxm").text();
      System.out.println("登录成功！欢迎您：" + stuName);
      client.close();
      return true;
    } else {
      System.out.println("登录失败！");
      client.close();
      return false;
    }
  
  }
  
  /**
   * 查询空教室
   * 
   * @throws Exception
   * 
   * @throws Exception
   */
  public void queryClassroom(String xiaoqu, String xqj, String sjd)
      throws Exception {
  
    CloseableHttpClient client = HttpClients.createDefault();
    String newQueryClassrommUrl = queryClassroomUrl + stuNumber + "&xm="
        + stuName + queryClassroomGnmkdm;// 拼接请求的Url
    String parseSjd = ParseUtils.parseWeek(sjd);// 解析当前节次对应的字符串
    String nowWeek = DateUtils.getWeek() + "";// 获取当前时间是第几周
    String viewState = IOUtils.getViewState(newQueryClassrommUrl, Cookie,
        mainUrl + stuNumber);
    // 封装查询空教室请求参数
    List<NameValuePair> queryClassroomPair = new ArrayList<NameValuePair>();
    queryClassroomPair.add(new BasicNameValuePair("__EVENTTARGET", ""));
    queryClassroomPair.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
    queryClassroomPair
        .add(new BasicNameValuePair("__VIEWSTATE", viewState));
    queryClassroomPair.add(new BasicNameValuePair("xiaoq", xiaoqu));// 校区类型，默认西校区
    queryClassroomPair.add(new BasicNameValuePair("jslb", ""));// 教室类别，默认为空
    queryClassroomPair.add(new BasicNameValuePair("min_zws", "0"));// 最小座位数，默认为0
    queryClassroomPair.add(new BasicNameValuePair("max_zws", ""));// 最大座位数，默认为空
    queryClassroomPair.add(new BasicNameValuePair("ddlKsz", nowWeek));// 起始周，默认当前周
    queryClassroomPair.add(new BasicNameValuePair("ddlJsz", nowWeek));// 结束周，默认当前周
    queryClassroomPair.add(new BasicNameValuePair("xqj", xqj));// 星期几,默认当天
    queryClassroomPair.add(new BasicNameValuePair("ddlDsz", ""));// 单双周，默认
    queryClassroomPair.add(new BasicNameValuePair("sjd", parseSjd));// 第几节
    queryClassroomPair.add(new BasicNameValuePair("Button2", "空教室查询"));
    queryClassroomPair.add(new BasicNameValuePair("xn", "2015-2016"));
    queryClassroomPair.add(new BasicNameValuePair("xq", "2"));
    queryClassroomPair.add(new BasicNameValuePair("ddlSyXn", "2015-2016"));
    queryClassroomPair.add(new BasicNameValuePair("ddlSyxq", "2"));
    UrlEncodedFormEntity entityClassroom = new UrlEncodedFormEntity(
        queryClassroomPair);
  
    HttpPost queryClassroomPost = new HttpPost(newQueryClassrommUrl);
    // newQueryClassrommUrl示例:http://jwgl2.ujn.edu.cn/xxjsjy.aspx?xh=20121214104&xm=XXX&gnmkdm=N121611
    queryClassroomPost.setEntity(entityClassroom);
    queryClassroomPost.setHeader("Referer", mainUrl + stuNumber);// 设置头信息
    queryClassroomPost.setHeader("Cookie", Cookie);
    HttpResponse responseClassroom = client.execute(queryClassroomPost);
    InputStream is = responseClassroom.getEntity().getContent();
    String html = IOUtils.getHtml(is, "GB2312");
    
    client.close();
  }
  
  /**
   * 重载查询空教室方法，默认时间，课程节次的无参数查询方法
   * 
   * @throws IOException
   * @throws ClientProtocolException
   */
  public void queryClassroom() throws ClientProtocolException, IOException,
      Exception {
    String weekDay = DateUtils.getWeekDay() + "";// 获取当前时间是星期几
    String sdj = DateUtils.getNowCourse() + "";// 获取当前时间是第几节课
    new Jwgl().queryClassroom(xixiaoqu, weekDay, sdj);
  }
  
  /**
   * 查询个人成绩方法
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
    // 封装请求参数
    List<NameValuePair> queryGradePair = new ArrayList<NameValuePair>();
    queryGradePair.add(new BasicNameValuePair("__EVENTTARGET", ""));
    queryGradePair.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
    queryGradePair.add(new BasicNameValuePair("__VIEWSTATE", viewState));
    queryGradePair.add(new BasicNameValuePair("hidLanguage", ""));
    queryGradePair.add(new BasicNameValuePair("ddlXN", xn));// 学年
    queryGradePair.add(new BasicNameValuePair("ddlXQ", xq));// 学期
    queryGradePair.add(new BasicNameValuePair("ddl_kcxz", ""));
    queryGradePair.add(new BasicNameValuePair("btn_xq", "学期成绩"));
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
   * 查询个人课表方法
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
      System.out.println("查询成绩测试-------");
      jw.queryStuGrade("2015-2016", "1");
      // 查询西校区，周一，第12节空教室测试。
      // jw.queryClassroom("1", "1", "2");
      System.out.println("查询空教室测试------");
      jw.queryClassroom();
      System.out.println("查询个人课表测试-------");
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