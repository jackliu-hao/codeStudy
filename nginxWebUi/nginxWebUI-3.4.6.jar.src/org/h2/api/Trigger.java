package org.h2.api;

import java.sql.Connection;
import java.sql.SQLException;

public interface Trigger {
  public static final int INSERT = 1;
  
  public static final int UPDATE = 2;
  
  public static final int DELETE = 4;
  
  public static final int SELECT = 8;
  
  default void init(Connection paramConnection, String paramString1, String paramString2, String paramString3, boolean paramBoolean, int paramInt) throws SQLException {}
  
  void fire(Connection paramConnection, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) throws SQLException;
  
  default void close() throws SQLException {}
  
  default void remove() throws SQLException {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\api\Trigger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */