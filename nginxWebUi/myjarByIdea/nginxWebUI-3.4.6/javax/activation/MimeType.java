package javax.activation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class MimeType implements Externalizable {
   private String primaryType;
   private String subType;
   private MimeTypeParameterList parameters;
   private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";

   public MimeType() {
      this.primaryType = "application";
      this.subType = "*";
      this.parameters = new MimeTypeParameterList();
   }

   public MimeType(String rawdata) throws MimeTypeParseException {
      this.parse(rawdata);
   }

   public MimeType(String primary, String sub) throws MimeTypeParseException {
      if (this.isValidToken(primary)) {
         this.primaryType = primary.toLowerCase();
         if (this.isValidToken(sub)) {
            this.subType = sub.toLowerCase();
            this.parameters = new MimeTypeParameterList();
         } else {
            throw new MimeTypeParseException("Sub type is invalid.");
         }
      } else {
         throw new MimeTypeParseException("Primary type is invalid.");
      }
   }

   private void parse(String rawdata) throws MimeTypeParseException {
      int slashIndex = rawdata.indexOf(47);
      int semIndex = rawdata.indexOf(59);
      if (slashIndex < 0 && semIndex < 0) {
         throw new MimeTypeParseException("Unable to find a sub type.");
      } else if (slashIndex < 0 && semIndex >= 0) {
         throw new MimeTypeParseException("Unable to find a sub type.");
      } else {
         if (slashIndex >= 0 && semIndex < 0) {
            this.primaryType = rawdata.substring(0, slashIndex).trim().toLowerCase();
            this.subType = rawdata.substring(slashIndex + 1).trim().toLowerCase();
            this.parameters = new MimeTypeParameterList();
         } else {
            if (slashIndex >= semIndex) {
               throw new MimeTypeParseException("Unable to find a sub type.");
            }

            this.primaryType = rawdata.substring(0, slashIndex).trim().toLowerCase();
            this.subType = rawdata.substring(slashIndex + 1, semIndex).trim().toLowerCase();
            this.parameters = new MimeTypeParameterList(rawdata.substring(semIndex));
         }

         if (!this.isValidToken(this.primaryType)) {
            throw new MimeTypeParseException("Primary type is invalid.");
         } else if (!this.isValidToken(this.subType)) {
            throw new MimeTypeParseException("Sub type is invalid.");
         }
      }
   }

   public String getPrimaryType() {
      return this.primaryType;
   }

   public void setPrimaryType(String primary) throws MimeTypeParseException {
      if (!this.isValidToken(this.primaryType)) {
         throw new MimeTypeParseException("Primary type is invalid.");
      } else {
         this.primaryType = primary.toLowerCase();
      }
   }

   public String getSubType() {
      return this.subType;
   }

   public void setSubType(String sub) throws MimeTypeParseException {
      if (!this.isValidToken(this.subType)) {
         throw new MimeTypeParseException("Sub type is invalid.");
      } else {
         this.subType = sub.toLowerCase();
      }
   }

   public MimeTypeParameterList getParameters() {
      return this.parameters;
   }

   public String getParameter(String name) {
      return this.parameters.get(name);
   }

   public void setParameter(String name, String value) {
      this.parameters.set(name, value);
   }

   public void removeParameter(String name) {
      this.parameters.remove(name);
   }

   public String toString() {
      return this.getBaseType() + this.parameters.toString();
   }

   public String getBaseType() {
      return this.primaryType + "/" + this.subType;
   }

   public boolean match(MimeType type) {
      return this.primaryType.equals(type.getPrimaryType()) && (this.subType.equals("*") || type.getSubType().equals("*") || this.subType.equals(type.getSubType()));
   }

   public boolean match(String rawdata) throws MimeTypeParseException {
      return this.match(new MimeType(rawdata));
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeUTF(this.toString());
      out.flush();
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      try {
         this.parse(in.readUTF());
      } catch (MimeTypeParseException var3) {
         throw new IOException(var3.toString());
      }
   }

   private static boolean isTokenChar(char c) {
      return c > ' ' && c < 127 && "()<>@,;:/[]?=\\\"".indexOf(c) < 0;
   }

   private boolean isValidToken(String s) {
      int len = s.length();
      if (len > 0) {
         for(int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (!isTokenChar(c)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
