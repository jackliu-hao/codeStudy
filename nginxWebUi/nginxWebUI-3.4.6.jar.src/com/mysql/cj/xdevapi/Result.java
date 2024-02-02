package com.mysql.cj.xdevapi;

import com.mysql.cj.QueryResult;
import java.util.Iterator;

public interface Result extends QueryResult {
  long getAffectedItemsCount();
  
  int getWarningsCount();
  
  Iterator<Warning> getWarnings();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\Result.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */