package cn.hutool.http.server.filter;

import com.sun.net.httpserver.Filter;

public abstract class SimpleFilter extends Filter {
   public String description() {
      return "Anonymous Filter";
   }
}
