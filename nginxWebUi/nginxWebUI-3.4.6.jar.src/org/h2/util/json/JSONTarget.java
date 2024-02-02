package org.h2.util.json;

import java.math.BigDecimal;

public abstract class JSONTarget<R> {
  public abstract void startObject();
  
  public abstract void endObject();
  
  public abstract void startArray();
  
  public abstract void endArray();
  
  public abstract void member(String paramString);
  
  public abstract void valueNull();
  
  public abstract void valueFalse();
  
  public abstract void valueTrue();
  
  public abstract void valueNumber(BigDecimal paramBigDecimal);
  
  public abstract void valueString(String paramString);
  
  public abstract boolean isPropertyExpected();
  
  public abstract boolean isValueSeparatorExpected();
  
  public abstract R getResult();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */