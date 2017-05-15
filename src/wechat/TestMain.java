package wechat;

//对接口进行测试  
public class TestMain {  
    private String url = "https://ipcrs.pbccrc.org.cn/imgrc.do";  
    private String charset = "utf-8";  
    private HttpClientUtil httpClientUtil = null;  
      
    public TestMain(){  
        httpClientUtil = new HttpClientUtil();  
    }  
      
    public void test(){  
        String httpOrgCreateTest = url;  
        String httpOrgCreateTestRtn = httpClientUtil.doPost(httpOrgCreateTest,charset);  
        
    }  
      
    public static void main(String[] args){  
        TestMain main = new TestMain();  
        main.test();  
    }  
}  
