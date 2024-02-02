package org.apache.http.message;

import java.io.Serializable;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class BasicHeader implements Header, Cloneable, Serializable {
   private static final HeaderElement[] EMPTY_HEADER_ELEMENTS = new HeaderElement[0];
   private static final long serialVersionUID = -5427236326487562174L;
   private final String name;
   private final String value;

   public BasicHeader(String name, String value) {
      this.name = (String)Args.notNull(name, "Name");
      this.value = value;
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   public HeaderElement[] getElements() throws ParseException {
      return this.getValue() != null ? BasicHeaderValueParser.parseElements((String)this.getValue(), (HeaderValueParser)null) : EMPTY_HEADER_ELEMENTS;
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public String toString() {
      return BasicLineFormatter.INSTANCE.formatHeader((CharArrayBuffer)null, (Header)this).toString();
   }
}
