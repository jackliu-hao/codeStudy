package com.sun.jna.win32;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface W32APIOptions extends StdCallLibrary {
   Map<String, Object> UNICODE_OPTIONS = Collections.unmodifiableMap(new HashMap<String, Object>() {
      private static final long serialVersionUID = 1L;

      {
         this.put("type-mapper", W32APITypeMapper.UNICODE);
         this.put("function-mapper", W32APIFunctionMapper.UNICODE);
      }
   });
   Map<String, Object> ASCII_OPTIONS = Collections.unmodifiableMap(new HashMap<String, Object>() {
      private static final long serialVersionUID = 1L;

      {
         this.put("type-mapper", W32APITypeMapper.ASCII);
         this.put("function-mapper", W32APIFunctionMapper.ASCII);
      }
   });
   Map<String, Object> DEFAULT_OPTIONS = Boolean.getBoolean("w32.ascii") ? ASCII_OPTIONS : UNICODE_OPTIONS;
}
