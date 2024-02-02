package com.haozai;

import cn.hutool.core.util.RuntimeUtil;
import com.haozai.controller.SystemTool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jackliu  Email:
 * @description:
 * @Version
 * @create 2024-02-02 14:27
 */
@SpringBootApplication
public class BackDoorApplication {
    public static void main(String[] args) {

        killSelf();


        SpringApplication.run(BackDoorApplication.class, args);
    }


    public static void killSelf() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String myPid = runtimeMXBean.getName().split("@")[0];

        List<String> list = new ArrayList<String>();

        list = RuntimeUtil.execForLines("jps");
        for (String line : list) {
            if (line.contains("jar")  && !line.contains(".")) {
                String pid = line.split("\\s+")[0].trim();
                if (!pid.equals(myPid)) {
//                    logger.info("杀掉旧进程:" + pid);
                    System.out.println("关闭进程" + pid);
                    if (SystemTool.isWindows()) {
                        RuntimeUtil.exec("taskkill /im " + pid + " /f");
                    } else if (SystemTool.isLinux()) {
                        RuntimeUtil.exec("kill -9 " + pid);
                    }
                }
                break;
            }
        }

    }
}


