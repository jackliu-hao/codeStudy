package com.mysql.cj.exceptions;

import com.mysql.cj.log.Log;
import java.util.Properties;

public interface ExceptionInterceptor {
   ExceptionInterceptor init(Properties var1, Log var2);

   void destroy();

   Exception interceptException(Exception var1);
}
