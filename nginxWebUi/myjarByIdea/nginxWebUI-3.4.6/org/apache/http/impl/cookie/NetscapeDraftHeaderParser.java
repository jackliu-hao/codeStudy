package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.ParserCursor;
import org.apache.http.message.TokenParser;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class NetscapeDraftHeaderParser {
   public static final NetscapeDraftHeaderParser DEFAULT = new NetscapeDraftHeaderParser();
   private static final char PARAM_DELIMITER = ';';
   private static final BitSet TOKEN_DELIMS = TokenParser.INIT_BITSET(61, 59);
   private static final BitSet VALUE_DELIMS = TokenParser.INIT_BITSET(59);
   private final TokenParser tokenParser;

   public NetscapeDraftHeaderParser() {
      this.tokenParser = TokenParser.INSTANCE;
   }

   public HeaderElement parseHeader(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
      Args.notNull(buffer, "Char array buffer");
      Args.notNull(cursor, "Parser cursor");
      NameValuePair nvp = this.parseNameValuePair(buffer, cursor);
      List<NameValuePair> params = new ArrayList();

      while(!cursor.atEnd()) {
         NameValuePair param = this.parseNameValuePair(buffer, cursor);
         params.add(param);
      }

      return new BasicHeaderElement(nvp.getName(), nvp.getValue(), (NameValuePair[])params.toArray(new NameValuePair[params.size()]));
   }

   private NameValuePair parseNameValuePair(CharArrayBuffer buffer, ParserCursor cursor) {
      String name = this.tokenParser.parseToken(buffer, cursor, TOKEN_DELIMS);
      if (cursor.atEnd()) {
         return new BasicNameValuePair(name, (String)null);
      } else {
         int delim = buffer.charAt(cursor.getPos());
         cursor.updatePos(cursor.getPos() + 1);
         if (delim != '=') {
            return new BasicNameValuePair(name, (String)null);
         } else {
            String value = this.tokenParser.parseToken(buffer, cursor, VALUE_DELIMS);
            if (!cursor.atEnd()) {
               cursor.updatePos(cursor.getPos() + 1);
            }

            return new BasicNameValuePair(name, value);
         }
      }
   }
}
