package wechat;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
  
public class DateUtils {
  private static String startDay = GlobalConstant.START_DAY;// ��ѧ����
  private static String endDay = GlobalConstant.END_DAY;// �ż�����
  
  /**
   * ��ȡ��ǰʱ���ǵڼ��ڿΣ�ֻ��8-16��֮��,����ʱ��Ĭ��1��2�ڿΡ�
   * 
   * @return
   */
  public static int getNowCourse() {
    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");// �������ڸ�ʽ
    String nowDate = df.format(new Date());
    if (nowDate.startsWith("08") || nowDate.startsWith("09")) {
      return 1;// 12�ڿΡ�
    } else if (nowDate.startsWith("10") || nowDate.startsWith("11")) {
      return 2;// 34�ڿΣ��Դ����ơ�
    } else if (nowDate.startsWith("12") || nowDate.startsWith("13")
        || nowDate.startsWith("14")) {
      return 3;
    } else if (nowDate.startsWith("15") || nowDate.startsWith("16")) {
      return 4;
    } else {
      return 1;
    }
  }
  
  /**
   * ��ȡ��ǰʱ���ǵڼ���
   * 
   * @return
   */
  public static int getWeek() {
    int days = 0;
    int nowWeek = 0;
    try {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// �������ڸ�ʽ
      String nowDate = df.format(new Date());
      int nowDaysBetween = daysBetween(startDay, nowDate) + 1;
      days = daysBetween(startDay, endDay);
      int x = nowDaysBetween % 7;
      if (x == 0) {
        nowWeek = nowDaysBetween / 7;
      } else {
        nowWeek = nowDaysBetween / 7 + 1;
      }
  
    } catch (ParseException e) {
      System.out.println("��������ڲ��Ϸ�����������ʧ��");
      e.printStackTrace();
    }
    return nowWeek;
  }
  
  /**
   * ��ȡ��ǰʱ�������ڼ�
   * 
   * @return
   */
  public static int getWeekDay() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    if (cal.get(Calendar.DAY_OF_WEEK) - 1 == 0) {
      return 7;
    }
    return cal.get(Calendar.DAY_OF_WEEK) - 1;
  }
  
  /**
   * ��������String��������֮�������
   * 
   * @param startDay
   * @param endDay
   * @return
   * @throws ParseException
   */
  public static int daysBetween(String startDay, String endDay)
      throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();
    cal.setTime(sdf.parse(startDay));
    long time1 = cal.getTimeInMillis();
    cal.setTime(sdf.parse(endDay));
    long time2 = cal.getTimeInMillis();
    long between_days = (time2 - time1) / (1000 * 3600 * 24);
  
    return Integer.parseInt(String.valueOf(between_days));
  }
  
  /**
   * ��yyyy-MM-dd HH:mm:ss��ʽ����String����ϵͳʱ��
   * 
   * @return
   */
  public static String getNowDate() {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
    return df.format(new Date());
  }
  
}