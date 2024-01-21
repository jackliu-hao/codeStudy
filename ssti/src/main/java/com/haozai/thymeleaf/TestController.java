package com.haozai.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author jackliu  Email:
 * @description:
 * @Version
 * @create 2024-01-04 19:01
 */
@Controller
public class TestController {

    @GetMapping("/path")
    public String path(@RequestParam String lang) {
        return  lang ; //template path is tainted
    }
}
