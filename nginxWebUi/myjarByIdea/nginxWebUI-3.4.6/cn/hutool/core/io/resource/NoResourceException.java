package cn.hutool.core.io.resource;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;

public class NoResourceException extends IORuntimeException {
   private static final long serialVersionUID = -623254467603299129L;

   public NoResourceException(Throwable e) {
      super(ExceptionUtil.getMessage(e), e);
   }

   public NoResourceException(String message) {
      super(message);
   }

   public NoResourceException(String messageTemplate, Object... params) {
      super(StrUtil.format(messageTemplate, params));
   }

   public NoResourceException(String message, Throwable throwable) {
      super(message, throwable);
   }

   public NoResourceException(Throwable throwable, String messageTemplate, Object... params) {
      super(StrUtil.format(messageTemplate, params), throwable);
   }

   public boolean causeInstanceOf(Class<? extends Throwable> clazz) {
      Throwable cause = this.getCause();
      return clazz.isInstance(cause);
   }
}
