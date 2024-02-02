package javax.mail;

import java.io.Serializable;

public abstract class Address implements Serializable {
  private static final long serialVersionUID = -5822459626751992278L;
  
  public abstract String getType();
  
  public abstract String toString();
  
  public abstract boolean equals(Object paramObject);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\Address.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */