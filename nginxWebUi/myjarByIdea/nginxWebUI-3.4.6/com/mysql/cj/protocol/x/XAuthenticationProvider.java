package com.mysql.cj.protocol.x;

import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.AuthenticationProvider;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.xdevapi.XDevAPIError;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class XAuthenticationProvider implements AuthenticationProvider<XMessage> {
   XProtocol protocol;
   private PropertyDefinitions.AuthMech authMech = null;
   private XMessageBuilder messageBuilder = new XMessageBuilder();

   public void init(Protocol<XMessage> prot, PropertySet propertySet, ExceptionInterceptor exceptionInterceptor) {
      this.protocol = (XProtocol)prot;
   }

   public void connect(String userName, String password, String database) {
      this.changeUser(userName, password, database);
   }

   public void changeUser(String userName, String password, String database) {
      boolean overTLS = ((XServerCapabilities)this.protocol.getServerSession().getCapabilities()).getTls();
      RuntimeProperty<PropertyDefinitions.AuthMech> authMechProp = this.protocol.getPropertySet().getEnumProperty(PropertyKey.xdevapiAuth);
      List tryAuthMech;
      if (!overTLS && !authMechProp.isExplicitlySet()) {
         tryAuthMech = Arrays.asList(PropertyDefinitions.AuthMech.MYSQL41, PropertyDefinitions.AuthMech.SHA256_MEMORY);
      } else {
         tryAuthMech = Arrays.asList((PropertyDefinitions.AuthMech)authMechProp.getValue());
      }

      XProtocolError capturedAuthErr = null;
      Iterator var8 = tryAuthMech.iterator();

      while(var8.hasNext()) {
         PropertyDefinitions.AuthMech am = (PropertyDefinitions.AuthMech)var8.next();
         this.authMech = am;

         try {
            switch (this.authMech) {
               case SHA256_MEMORY:
                  this.protocol.send(this.messageBuilder.buildSha256MemoryAuthStart(), 0);
                  byte[] nonce = this.protocol.readAuthenticateContinue();
                  this.protocol.send(this.messageBuilder.buildSha256MemoryAuthContinue(userName, password, nonce, database), 0);
                  break;
               case MYSQL41:
                  this.protocol.send(this.messageBuilder.buildMysql41AuthStart(), 0);
                  byte[] salt = this.protocol.readAuthenticateContinue();
                  this.protocol.send(this.messageBuilder.buildMysql41AuthContinue(userName, password, salt, database), 0);
                  break;
               case PLAIN:
                  if (!overTLS) {
                     throw new XProtocolError("PLAIN authentication is not allowed via unencrypted connection.");
                  }

                  this.protocol.send(this.messageBuilder.buildPlainAuthStart(userName, password, database), 0);
                  break;
               case EXTERNAL:
                  this.protocol.send(this.messageBuilder.buildExternalAuthStart(database), 0);
                  break;
               default:
                  throw new WrongArgumentException("Unknown authentication mechanism '" + this.authMech + "'.");
            }
         } catch (CJCommunicationsException var12) {
            if (capturedAuthErr != null && var12.getCause() instanceof ClosedChannelException) {
               throw capturedAuthErr;
            }

            throw var12;
         }

         try {
            this.protocol.readAuthenticateOk();
            capturedAuthErr = null;
            break;
         } catch (XProtocolError var13) {
            if (var13.getErrorCode() != 1045) {
               throw var13;
            }

            capturedAuthErr = var13;
         }
      }

      if (capturedAuthErr != null) {
         if (tryAuthMech.size() == 1) {
            throw capturedAuthErr;
         } else {
            String errMsg = "Authentication failed using " + StringUtils.joinWithSerialComma(tryAuthMech) + ", check username and password or try a secure connection";
            XDevAPIError ex = new XDevAPIError(errMsg, capturedAuthErr);
            ex.setVendorCode(capturedAuthErr.getErrorCode());
            ex.setSQLState(capturedAuthErr.getSQLState());
            throw ex;
         }
      } else {
         this.protocol.afterHandshake();
      }
   }
}
