package cn.hutool.core.exceptions;

import cn.hutool.core.util.StrUtil;

public class StatefulException extends RuntimeException {
   private static final long serialVersionUID = 6057602589533840889L;
   private int status;

   public StatefulException() {
   }

   public StatefulException(String msg) {
      super(msg);
   }

   public StatefulException(String messageTemplate, Object... params) {
      super(StrUtil.format(messageTemplate, params));
   }

   public StatefulException(Throwable throwable) {
      super(throwable);
   }

   public StatefulException(String msg, Throwable throwable) {
      super(msg, throwable);
   }

   public StatefulException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
      super(message, throwable, enableSuppression, writableStackTrace);
   }

   public StatefulException(int status, String msg) {
      super(msg);
      this.status = status;
   }

   public StatefulException(int status, Throwable throwable) {
      super(throwable);
      this.status = status;
   }

   public StatefulException(int status, String msg, Throwable throwable) {
      super(msg, throwable);
      this.status = status;
   }

   public int getStatus() {
      return this.status;
   }
}
