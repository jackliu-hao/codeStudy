package javax.mail;

import java.io.Serializable;

public abstract class Address implements Serializable {
   private static final long serialVersionUID = -5822459626751992278L;

   public abstract String getType();

   public abstract String toString();

   public abstract boolean equals(Object var1);
}
