package org.h2.security.auth;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class H2AuthConfigXml extends DefaultHandler {
   private H2AuthConfig result;
   private HasConfigProperties lastConfigProperties;

   public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
      switch (var3) {
         case "h2Auth":
            this.result = new H2AuthConfig();
            this.result.setAllowUserRegistration("true".equals(getAttributeValueOr("allowUserRegistration", var4, "false")));
            this.result.setCreateMissingRoles("true".equals(getAttributeValueOr("createMissingRoles", var4, "true")));
            break;
         case "realm":
            RealmConfig var7 = new RealmConfig();
            var7.setName(getMandatoryAttributeValue("name", var4));
            var7.setValidatorClass(getMandatoryAttributeValue("validatorClass", var4));
            this.result.getRealms().add(var7);
            this.lastConfigProperties = var7;
            break;
         case "userToRolesMapper":
            UserToRolesMapperConfig var8 = new UserToRolesMapperConfig();
            var8.setClassName(getMandatoryAttributeValue("className", var4));
            this.result.getUserToRolesMappers().add(var8);
            this.lastConfigProperties = var8;
            break;
         case "property":
            if (this.lastConfigProperties == null) {
               throw new SAXException("property element in the wrong place");
            }

            this.lastConfigProperties.getProperties().add(new PropertyConfig(getMandatoryAttributeValue("name", var4), getMandatoryAttributeValue("value", var4)));
            break;
         default:
            throw new SAXException("unexpected element " + var3);
      }

   }

   public void endElement(String var1, String var2, String var3) throws SAXException {
      if (this.lastConfigProperties != null && !var3.equals("property")) {
         this.lastConfigProperties = null;
      }

   }

   private static String getMandatoryAttributeValue(String var0, Attributes var1) throws SAXException {
      String var2 = var1.getValue(var0);
      if (var2 != null && !var2.trim().equals("")) {
         return var2;
      } else {
         throw new SAXException("missing attribute " + var0);
      }
   }

   private static String getAttributeValueOr(String var0, Attributes var1, String var2) {
      String var3 = var1.getValue(var0);
      return var3 != null && !var3.trim().equals("") ? var3 : var2;
   }

   public H2AuthConfig getResult() {
      return this.result;
   }

   public static H2AuthConfig parseFrom(URL var0) throws SAXException, IOException, ParserConfigurationException {
      InputStream var1 = var0.openStream();
      Throwable var2 = null;

      H2AuthConfig var3;
      try {
         var3 = parseFrom(var1);
      } catch (Throwable var12) {
         var2 = var12;
         throw var12;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var11) {
                  var2.addSuppressed(var11);
               }
            } else {
               var1.close();
            }
         }

      }

      return var3;
   }

   public static H2AuthConfig parseFrom(InputStream var0) throws SAXException, IOException, ParserConfigurationException {
      SAXParser var1 = SAXParserFactory.newInstance().newSAXParser();
      H2AuthConfigXml var2 = new H2AuthConfigXml();
      var1.parse(var0, var2);
      return var2.getResult();
   }
}
