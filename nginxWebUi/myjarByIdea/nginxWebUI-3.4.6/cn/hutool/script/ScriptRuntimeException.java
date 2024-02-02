package cn.hutool.script;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import javax.script.ScriptException;

public class ScriptRuntimeException extends RuntimeException {
   private static final long serialVersionUID = 8247610319171014183L;
   private String fileName;
   private int lineNumber = -1;
   private int columnNumber = -1;

   public ScriptRuntimeException(Throwable e) {
      super(ExceptionUtil.getMessage(e), e);
   }

   public ScriptRuntimeException(String message) {
      super(message);
   }

   public ScriptRuntimeException(String messageTemplate, Object... params) {
      super(StrUtil.format(messageTemplate, params));
   }

   public ScriptRuntimeException(String message, Throwable throwable) {
      super(message, throwable);
   }

   public ScriptRuntimeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
      super(message, throwable, enableSuppression, writableStackTrace);
   }

   public ScriptRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
      super(StrUtil.format(messageTemplate, params), throwable);
   }

   public ScriptRuntimeException(String message, String fileName, int lineNumber) {
      super(message);
      this.fileName = fileName;
      this.lineNumber = lineNumber;
   }

   public ScriptRuntimeException(String message, String fileName, int lineNumber, int columnNumber) {
      super(message);
      this.fileName = fileName;
      this.lineNumber = lineNumber;
      this.columnNumber = columnNumber;
   }

   public ScriptRuntimeException(ScriptException e) {
      super(e);
      this.fileName = e.getFileName();
      this.lineNumber = e.getLineNumber();
      this.columnNumber = e.getColumnNumber();
   }

   public String getMessage() {
      StringBuilder ret = (new StringBuilder()).append(super.getMessage());
      if (this.fileName != null) {
         ret.append(" in ").append(this.fileName);
         if (this.lineNumber != -1) {
            ret.append(" at line number ").append(this.lineNumber);
         }

         if (this.columnNumber != -1) {
            ret.append(" at column number ").append(this.columnNumber);
         }
      }

      return ret.toString();
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public int getColumnNumber() {
      return this.columnNumber;
   }

   public String getFileName() {
      return this.fileName;
   }
}
