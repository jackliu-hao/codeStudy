package javax.mail.internet;

public class ContentType {
   private String primaryType;
   private String subType;
   private ParameterList list;

   public ContentType() {
   }

   public ContentType(String primaryType, String subType, ParameterList list) {
      this.primaryType = primaryType;
      this.subType = subType;
      this.list = list;
   }

   public ContentType(String s) throws ParseException {
      HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
      HeaderTokenizer.Token tk = h.next();
      if (tk.getType() != -1) {
         throw new ParseException("Expected MIME type, got " + tk.getValue());
      } else {
         this.primaryType = tk.getValue();
         tk = h.next();
         if ((char)tk.getType() != '/') {
            throw new ParseException("Expected '/', got " + tk.getValue());
         } else {
            tk = h.next();
            if (tk.getType() != -1) {
               throw new ParseException("Expected MIME subtype, got " + tk.getValue());
            } else {
               this.subType = tk.getValue();
               String rem = h.getRemainder();
               if (rem != null) {
                  this.list = new ParameterList(rem);
               }

            }
         }
      }
   }

   public String getPrimaryType() {
      return this.primaryType;
   }

   public String getSubType() {
      return this.subType;
   }

   public String getBaseType() {
      return this.primaryType + '/' + this.subType;
   }

   public String getParameter(String name) {
      return this.list == null ? null : this.list.get(name);
   }

   public ParameterList getParameterList() {
      return this.list;
   }

   public void setPrimaryType(String primaryType) {
      this.primaryType = primaryType;
   }

   public void setSubType(String subType) {
      this.subType = subType;
   }

   public void setParameter(String name, String value) {
      if (this.list == null) {
         this.list = new ParameterList();
      }

      this.list.set(name, value);
   }

   public void setParameterList(ParameterList list) {
      this.list = list;
   }

   public String toString() {
      if (this.primaryType != null && this.subType != null) {
         StringBuffer sb = new StringBuffer();
         sb.append(this.primaryType).append('/').append(this.subType);
         if (this.list != null) {
            sb.append(this.list.toString(sb.length() + 14));
         }

         return sb.toString();
      } else {
         return null;
      }
   }

   public boolean match(ContentType cType) {
      if (!this.primaryType.equalsIgnoreCase(cType.getPrimaryType())) {
         return false;
      } else {
         String sType = cType.getSubType();
         if (this.subType.charAt(0) != '*' && sType.charAt(0) != '*') {
            return this.subType.equalsIgnoreCase(sType);
         } else {
            return true;
         }
      }
   }

   public boolean match(String s) {
      try {
         return this.match(new ContentType(s));
      } catch (ParseException var3) {
         return false;
      }
   }
}
