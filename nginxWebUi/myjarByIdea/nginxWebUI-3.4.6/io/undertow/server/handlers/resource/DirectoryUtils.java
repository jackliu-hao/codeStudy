package io.undertow.server.handlers.resource;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.DateUtils;
import io.undertow.util.ETag;
import io.undertow.util.ETagUtils;
import io.undertow.util.FlexBase64;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.RedirectBuilder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import org.xnio.channels.Channels;

public class DirectoryUtils {
   public static boolean sendRequestedBlobs(HttpServerExchange exchange) {
      ByteBuffer buffer = null;
      String type = null;
      String etag = null;
      String quotedEtag = null;
      if ("css".equals(exchange.getQueryString())) {
         buffer = DirectoryUtils.Blobs.FILE_CSS_BUFFER.duplicate();
         type = "text/css";
         etag = DirectoryUtils.Blobs.FILE_CSS_ETAG;
         quotedEtag = DirectoryUtils.Blobs.FILE_CSS_ETAG_QUOTED;
      } else if ("js".equals(exchange.getQueryString())) {
         buffer = DirectoryUtils.Blobs.FILE_JS_BUFFER.duplicate();
         type = "application/javascript";
         etag = DirectoryUtils.Blobs.FILE_JS_ETAG;
         quotedEtag = DirectoryUtils.Blobs.FILE_JS_ETAG_QUOTED;
      }

      if (buffer != null) {
         if (!ETagUtils.handleIfNoneMatch(exchange, new ETag(false, etag), false)) {
            exchange.setStatusCode(304);
            return true;
         } else {
            exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, String.valueOf(buffer.limit()));
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, type);
            exchange.getResponseHeaders().put(Headers.ETAG, quotedEtag);
            if (Methods.HEAD.equals(exchange.getRequestMethod())) {
               exchange.endExchange();
               return true;
            } else {
               exchange.getResponseSender().send(buffer);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static StringBuilder renderDirectoryListing(String path, Resource resource) {
      if (!path.endsWith("/")) {
         path = path + "/";
      }

      StringBuilder builder = new StringBuilder();
      builder.append("<html>\n<head>\n<script src='").append(path).append("?js'></script>\n").append("<link rel='stylesheet' type='text/css' href='").append(path).append("?css' />\n</head>\n");
      builder.append("<body onresize='growit()' onload='growit()'>\n<table id='thetable'>\n<thead>\n");
      builder.append("<tr><th class='loc' colspan='3'>Directory Listing - ").append(path).append("</th></tr>\n").append("<tr><th class='label offset'>Name</th><th class='label'>Last Modified</th><th class='label'>Size</th></tr>\n</thead>\n").append("<tfoot>\n<tr><th class=\"loc footer\" colspan=\"3\">Powered by Undertow</th></tr>\n</tfoot>\n<tbody>\n");
      int state = 0;
      String parent = null;
      if (path.length() > 1) {
         for(int i = path.length() - 1; i >= 0; --i) {
            if (state == 1) {
               if (path.charAt(i) == '/') {
                  state = 2;
               }
            } else if (path.charAt(i) != '/') {
               if (state == 2) {
                  parent = path.substring(0, i + 1);
                  break;
               }

               state = 1;
            }
         }

         if (parent == null) {
            parent = "/";
         }
      }

      SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.US);
      int i = 0;
      if (parent != null) {
         ++i;
         builder.append("<tr class='odd'><td><a class='icon up' href='").append(parent).append("'>[..]</a></td><td>");
         builder.append(format.format(resource.getLastModified() == null ? new Date(0L) : resource.getLastModified())).append("</td><td>--</td></tr>\n");
      }

      for(Iterator var7 = resource.list().iterator(); var7.hasNext(); builder.append("</td></tr>\n")) {
         Resource entry = (Resource)var7.next();
         StringBuilder var10000 = builder.append("<tr class='");
         ++i;
         var10000.append((i & 1) == 1 ? "odd" : "even").append("'><td><a class='icon ");
         builder.append(entry.isDirectory() ? "dir" : "file");
         builder.append("' href='").append(path).append(entry.getName()).append("'>").append(entry.getName()).append("</a></td><td>");
         builder.append(format.format(entry.getLastModified() == null ? new Date(0L) : entry.getLastModified())).append("</td><td>");
         if (entry.isDirectory()) {
            builder.append("--");
         } else {
            formatSize(builder, entry.getContentLength());
         }
      }

      builder.append("</tbody>\n</table>\n</body>\n</html>");
      return builder;
   }

   public static void renderDirectoryListing(HttpServerExchange exchange, Resource resource) {
      String requestPath = exchange.getRequestPath();
      if (!requestPath.endsWith("/")) {
         exchange.setStatusCode(302);
         exchange.getResponseHeaders().put(Headers.LOCATION, RedirectBuilder.redirect(exchange, exchange.getRelativePath() + "/", true));
         exchange.endExchange();
      } else {
         StringBuilder builder = renderDirectoryListing(requestPath, resource);

         try {
            ByteBuffer output = ByteBuffer.wrap(builder.toString().getBytes(StandardCharsets.UTF_8));
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
            exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, String.valueOf(output.limit()));
            exchange.getResponseHeaders().put(Headers.LAST_MODIFIED, DateUtils.toDateString(new Date()));
            exchange.getResponseHeaders().put(Headers.CACHE_CONTROL, "must-revalidate");
            Channels.writeBlocking(exchange.getResponseChannel(), output);
         } catch (UnsupportedEncodingException var5) {
            throw new IllegalStateException(var5);
         } catch (IOException var6) {
            UndertowLogger.REQUEST_IO_LOGGER.ioException(var6);
            exchange.setStatusCode(500);
         }

         exchange.endExchange();
      }
   }

   private static StringBuilder formatSize(StringBuilder builder, Long size) {
      if (size == null) {
         builder.append("???");
         return builder;
      } else {
         int n = 1073741824;

         int type;
         for(type = 0; size < (long)n && n >= 1024; ++type) {
            n /= 1024;
         }

         long top = size * 100L / (long)n;
         long bottom = top % 100L;
         top /= 100L;
         builder.append(top);
         if (bottom > 0L) {
            builder.append(".").append(bottom / 10L);
            bottom %= 10L;
            if (bottom > 0L) {
               builder.append(bottom);
            }
         }

         switch (type) {
            case 0:
               builder.append(" GB");
               break;
            case 1:
               builder.append(" MB");
               break;
            case 2:
               builder.append(" KB");
         }

         return builder;
      }
   }

   private DirectoryUtils() {
   }

   private static String md5(byte[] buffer) {
      try {
         MessageDigest md = MessageDigest.getInstance("MD5");
         md.update(buffer);
         byte[] digest = md.digest();
         return new String(FlexBase64.encodeBytes(digest, 0, digest.length, false), StandardCharsets.US_ASCII);
      } catch (NoSuchAlgorithmException var3) {
         throw new InternalError("MD5 not supported on this platform");
      }
   }

   public static class Blobs {
      public static final String FILE_JS = "function growit() {\n    var table = document.getElementById(\"thetable\");\n\n    var i = table.rows.length - 1;\n    while (i-- > 0) {\n        if (table.rows[i].id == \"eraseme\") {\n            table.deleteRow(i);\n        } else {\n            break;\n        }\n    }\n    table.style.height=\"\";\n    var i = 0;\n    while (table.offsetHeight < window.innerHeight - 24) {\n        i++;\n        var tbody = table.tBodies[0];\n        var row = tbody.insertRow(tbody.rows.length);\n        row.id=\"eraseme\";\n        var cell = row.insertCell(0);\n        if (table.rows.length % 2 != 0) {\n            row.className=\"even eveninvis\";\n        } else {\n            row.className=\"odd oddinvis\";\n        }\n\n        cell.colSpan=3;\n        cell.appendChild(document.createTextNode(\"i\"));\n    }\n    table.style.height=\"100%\";\n    if (i > 0) {\n        document.documentElement.style.overflowY=\"hidden\";\n    } else {\n        document.documentElement.style.overflowY=\"auto\";\n    }\n}";
      public static final String FILE_JS_ETAG;
      public static final String FILE_JS_ETAG_QUOTED;
      public static final String FILE_CSS = "body {\n    font-family: \"Lucida Grande\", \"Lucida Sans Unicode\", \"Trebuchet MS\", Helvetica, Arial, Verdana, sans-serif;\n    margin: 5px;\n}\n\nth.loc {\n    background-image: linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -o-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -moz-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -webkit-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -ms-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    \n    background-image: -webkit-gradient(\n        linear,\n        left bottom,\n        left top,\n        color-stop(0.08, rgb(153,151,153)),\n        color-stop(0.54, rgb(199,199,199))\n    );\n    color: black;\n    padding: 2px;\n    font-weight: normal;\n    border: solid 1px;\n    font-size: 150%;\n    text-align: left;\n}\n\nth.label {\n    border: solid  1px;\n    text-align: left;\n    padding: 4px;\n    padding-left: 8px;\n    font-weight: normal;\n    font-size: small;\n    background-color: #e8e8e8;\n}\n\nth.offset {\n    padding-left: 32px;\n}\n\nth.footer {\n    font-size: 75%;\n    text-align: right;\n}\n\na.icon {\n    padding-left: 24px;\n    text-decoration: none;\n    color: black;\n}\n\na.icon:hover {\n    text-decoration: underline;\n}\n\ntable {\n    border: 1px solid;\n    border-spacing: 0px;\n    width: 100%;\n    border-collapse: collapse;\n}\n\ntr.odd {\n    background-color: #f3f6fa;\n}\n\ntr.odd td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.even {\n    background-color: #ffffff;\n}\n\ntr.even td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.eveninvis td {\n    color: #ffffff;\n}\n\ntr.oddinvis td {\n    color: #f3f6fa\n}\n\na.up {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABI0lEQVQ4y2P4//8/Ay7sM4nhPwjjUwMm0ua//Y+M0+e//QrSGDAfgvEZAjdgydHXcAzTXLjWDoxhhqBbhGLA1N0vwBhdM7ohMHVwA8yrzn4zLj/936j8FE7N6IaA1IL0gPQy2DVc+rnp3FeCmtENAekB6WXw7Lz1tWD5x/+wEIdhdI3o8iA9IL0MYZMfvq9a9+V/w+avcIzLAGQ1ID0gvQxJc56/aNn29X/vnm9wjMsAZDWtQD0gvQwFy94+6N37/f/Moz/gGJcByGpAekB6GarXf7427ciP/0vP/YRjdP/CMLIakB6QXobKDd9PN+769b91P2kYpAekl2HJhb8r11/583/9ZRIxUM+8U783MQCBGBDXAHEbibgGrBdfTiMGU2wAAPz+nxp+TnhDAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.dir {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAAA+UlEQVQ4jWP4//8/AyUYTKTNf/sfGafPf/s1be47G5IMWHL0NRxP2f3mbcaCtz/RDUbHKAZM3f2CJAw3wLzq7Dfj8tP/jcpPkYRBekB6GewaLv3cdO7r/y0XSMMgPSC9DJ6dt74WLP/4v3TVZ5IwSA9IL0PY5Ifvq9Z9+d+w+StJGKQHpJchac7zFy3bvv7v3fONJNwK1APSy5C/7O2D3r3f/888+oMkDNID0stQvf7ztWlHfvxfeu4nSRikB6SXoXLD99ONu379b91PGgbpAellWHLh38r1V/78X3+ZRAzUM/fUr00MQCAGxDVA3EYirgHrpUpupAQDAPs+7c1tGDnPAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.file {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAABM0lEQVQ4y5WSTW6DMBCF3xvzc4wuOEIO0kVAuUB7vJ4g3KBdoHSRROomEpusUaoAcaYLfmKoqVRLIxnJ7/M3YwJVBcknACv8b+1U9SvoP1bXa/3WNDVIAQmQBLsNOEsGQYAwDNcARgDqusbl+wIRA2NkBEyqP0s+kCOAQhhjICJdkaDIJDwEvQAhH+G+SHagWTsi4jHoAWYIOxYDZDjnb8Fn4Akvz6AHcAbx3Tp5ETwI3RwckyVtv4Fr4VEe9qq6bDB5tlnYWou2bWGtRRRF6jdwAm5Za1FVFc7nM0QERVG8A9hPDRaGpapomgZlWSJJEuR5ftpsNq8ADr9amC+SuN/vuN1uIIntdnvKsuwZwKf2wxgBxpjpX+dA4jjW4/H4kabpixt2AbvAmDX+XnsAB509ww+A8mAar+XXgQAAAABJRU5ErkJggg==') left center no-repeat;\n}";
      public static final String FILE_CSS_ETAG;
      public static final String FILE_CSS_ETAG_QUOTED;
      public static final ByteBuffer FILE_CSS_BUFFER;
      public static final ByteBuffer FILE_JS_BUFFER;

      static {
         FILE_JS_ETAG = DirectoryUtils.md5("function growit() {\n    var table = document.getElementById(\"thetable\");\n\n    var i = table.rows.length - 1;\n    while (i-- > 0) {\n        if (table.rows[i].id == \"eraseme\") {\n            table.deleteRow(i);\n        } else {\n            break;\n        }\n    }\n    table.style.height=\"\";\n    var i = 0;\n    while (table.offsetHeight < window.innerHeight - 24) {\n        i++;\n        var tbody = table.tBodies[0];\n        var row = tbody.insertRow(tbody.rows.length);\n        row.id=\"eraseme\";\n        var cell = row.insertCell(0);\n        if (table.rows.length % 2 != 0) {\n            row.className=\"even eveninvis\";\n        } else {\n            row.className=\"odd oddinvis\";\n        }\n\n        cell.colSpan=3;\n        cell.appendChild(document.createTextNode(\"i\"));\n    }\n    table.style.height=\"100%\";\n    if (i > 0) {\n        document.documentElement.style.overflowY=\"hidden\";\n    } else {\n        document.documentElement.style.overflowY=\"auto\";\n    }\n}".getBytes(StandardCharsets.US_ASCII));
         FILE_JS_ETAG_QUOTED = '"' + FILE_JS_ETAG + '"';
         FILE_CSS_ETAG = DirectoryUtils.md5("body {\n    font-family: \"Lucida Grande\", \"Lucida Sans Unicode\", \"Trebuchet MS\", Helvetica, Arial, Verdana, sans-serif;\n    margin: 5px;\n}\n\nth.loc {\n    background-image: linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -o-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -moz-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -webkit-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -ms-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    \n    background-image: -webkit-gradient(\n        linear,\n        left bottom,\n        left top,\n        color-stop(0.08, rgb(153,151,153)),\n        color-stop(0.54, rgb(199,199,199))\n    );\n    color: black;\n    padding: 2px;\n    font-weight: normal;\n    border: solid 1px;\n    font-size: 150%;\n    text-align: left;\n}\n\nth.label {\n    border: solid  1px;\n    text-align: left;\n    padding: 4px;\n    padding-left: 8px;\n    font-weight: normal;\n    font-size: small;\n    background-color: #e8e8e8;\n}\n\nth.offset {\n    padding-left: 32px;\n}\n\nth.footer {\n    font-size: 75%;\n    text-align: right;\n}\n\na.icon {\n    padding-left: 24px;\n    text-decoration: none;\n    color: black;\n}\n\na.icon:hover {\n    text-decoration: underline;\n}\n\ntable {\n    border: 1px solid;\n    border-spacing: 0px;\n    width: 100%;\n    border-collapse: collapse;\n}\n\ntr.odd {\n    background-color: #f3f6fa;\n}\n\ntr.odd td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.even {\n    background-color: #ffffff;\n}\n\ntr.even td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.eveninvis td {\n    color: #ffffff;\n}\n\ntr.oddinvis td {\n    color: #f3f6fa\n}\n\na.up {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABI0lEQVQ4y2P4//8/Ay7sM4nhPwjjUwMm0ua//Y+M0+e//QrSGDAfgvEZAjdgydHXcAzTXLjWDoxhhqBbhGLA1N0vwBhdM7ohMHVwA8yrzn4zLj/936j8FE7N6IaA1IL0gPQy2DVc+rnp3FeCmtENAekB6WXw7Lz1tWD5x/+wEIdhdI3o8iA9IL0MYZMfvq9a9+V/w+avcIzLAGQ1ID0gvQxJc56/aNn29X/vnm9wjMsAZDWtQD0gvQwFy94+6N37/f/Moz/gGJcByGpAekB6GarXf7427ciP/0vP/YRjdP/CMLIakB6QXobKDd9PN+769b91P2kYpAekl2HJhb8r11/583/9ZRIxUM+8U783MQCBGBDXAHEbibgGrBdfTiMGU2wAAPz+nxp+TnhDAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.dir {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAAA+UlEQVQ4jWP4//8/AyUYTKTNf/sfGafPf/s1be47G5IMWHL0NRxP2f3mbcaCtz/RDUbHKAZM3f2CJAw3wLzq7Dfj8tP/jcpPkYRBekB6GewaLv3cdO7r/y0XSMMgPSC9DJ6dt74WLP/4v3TVZ5IwSA9IL0PY5Ifvq9Z9+d+w+StJGKQHpJchac7zFy3bvv7v3fONJNwK1APSy5C/7O2D3r3f/888+oMkDNID0stQvf7ztWlHfvxfeu4nSRikB6SXoXLD99ONu379b91PGgbpAellWHLh38r1V/78X3+ZRAzUM/fUr00MQCAGxDVA3EYirgHrpUpupAQDAPs+7c1tGDnPAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.file {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAABM0lEQVQ4y5WSTW6DMBCF3xvzc4wuOEIO0kVAuUB7vJ4g3KBdoHSRROomEpusUaoAcaYLfmKoqVRLIxnJ7/M3YwJVBcknACv8b+1U9SvoP1bXa/3WNDVIAQmQBLsNOEsGQYAwDNcARgDqusbl+wIRA2NkBEyqP0s+kCOAQhhjICJdkaDIJDwEvQAhH+G+SHagWTsi4jHoAWYIOxYDZDjnb8Fn4Akvz6AHcAbx3Tp5ETwI3RwckyVtv4Fr4VEe9qq6bDB5tlnYWou2bWGtRRRF6jdwAm5Za1FVFc7nM0QERVG8A9hPDRaGpapomgZlWSJJEuR5ftpsNq8ADr9amC+SuN/vuN1uIIntdnvKsuwZwKf2wxgBxpjpX+dA4jjW4/H4kabpixt2AbvAmDX+XnsAB509ww+A8mAar+XXgQAAAABJRU5ErkJggg==') left center no-repeat;\n}".getBytes(StandardCharsets.US_ASCII));
         FILE_CSS_ETAG_QUOTED = '"' + FILE_CSS_ETAG + '"';

         try {
            byte[] bytes = "body {\n    font-family: \"Lucida Grande\", \"Lucida Sans Unicode\", \"Trebuchet MS\", Helvetica, Arial, Verdana, sans-serif;\n    margin: 5px;\n}\n\nth.loc {\n    background-image: linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -o-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -moz-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -webkit-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -ms-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    \n    background-image: -webkit-gradient(\n        linear,\n        left bottom,\n        left top,\n        color-stop(0.08, rgb(153,151,153)),\n        color-stop(0.54, rgb(199,199,199))\n    );\n    color: black;\n    padding: 2px;\n    font-weight: normal;\n    border: solid 1px;\n    font-size: 150%;\n    text-align: left;\n}\n\nth.label {\n    border: solid  1px;\n    text-align: left;\n    padding: 4px;\n    padding-left: 8px;\n    font-weight: normal;\n    font-size: small;\n    background-color: #e8e8e8;\n}\n\nth.offset {\n    padding-left: 32px;\n}\n\nth.footer {\n    font-size: 75%;\n    text-align: right;\n}\n\na.icon {\n    padding-left: 24px;\n    text-decoration: none;\n    color: black;\n}\n\na.icon:hover {\n    text-decoration: underline;\n}\n\ntable {\n    border: 1px solid;\n    border-spacing: 0px;\n    width: 100%;\n    border-collapse: collapse;\n}\n\ntr.odd {\n    background-color: #f3f6fa;\n}\n\ntr.odd td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.even {\n    background-color: #ffffff;\n}\n\ntr.even td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.eveninvis td {\n    color: #ffffff;\n}\n\ntr.oddinvis td {\n    color: #f3f6fa\n}\n\na.up {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABI0lEQVQ4y2P4//8/Ay7sM4nhPwjjUwMm0ua//Y+M0+e//QrSGDAfgvEZAjdgydHXcAzTXLjWDoxhhqBbhGLA1N0vwBhdM7ohMHVwA8yrzn4zLj/936j8FE7N6IaA1IL0gPQy2DVc+rnp3FeCmtENAekB6WXw7Lz1tWD5x/+wEIdhdI3o8iA9IL0MYZMfvq9a9+V/w+avcIzLAGQ1ID0gvQxJc56/aNn29X/vnm9wjMsAZDWtQD0gvQwFy94+6N37/f/Moz/gGJcByGpAekB6GarXf7427ciP/0vP/YRjdP/CMLIakB6QXobKDd9PN+769b91P2kYpAekl2HJhb8r11/583/9ZRIxUM+8U783MQCBGBDXAHEbibgGrBdfTiMGU2wAAPz+nxp+TnhDAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.dir {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAAA+UlEQVQ4jWP4//8/AyUYTKTNf/sfGafPf/s1be47G5IMWHL0NRxP2f3mbcaCtz/RDUbHKAZM3f2CJAw3wLzq7Dfj8tP/jcpPkYRBekB6GewaLv3cdO7r/y0XSMMgPSC9DJ6dt74WLP/4v3TVZ5IwSA9IL0PY5Ifvq9Z9+d+w+StJGKQHpJchac7zFy3bvv7v3fONJNwK1APSy5C/7O2D3r3f/888+oMkDNID0stQvf7ztWlHfvxfeu4nSRikB6SXoXLD99ONu379b91PGgbpAellWHLh38r1V/78X3+ZRAzUM/fUr00MQCAGxDVA3EYirgHrpUpupAQDAPs+7c1tGDnPAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.file {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAABM0lEQVQ4y5WSTW6DMBCF3xvzc4wuOEIO0kVAuUB7vJ4g3KBdoHSRROomEpusUaoAcaYLfmKoqVRLIxnJ7/M3YwJVBcknACv8b+1U9SvoP1bXa/3WNDVIAQmQBLsNOEsGQYAwDNcARgDqusbl+wIRA2NkBEyqP0s+kCOAQhhjICJdkaDIJDwEvQAhH+G+SHagWTsi4jHoAWYIOxYDZDjnb8Fn4Akvz6AHcAbx3Tp5ETwI3RwckyVtv4Fr4VEe9qq6bDB5tlnYWou2bWGtRRRF6jdwAm5Za1FVFc7nM0QERVG8A9hPDRaGpapomgZlWSJJEuR5ftpsNq8ADr9amC+SuN/vuN1uIIntdnvKsuwZwKf2wxgBxpjpX+dA4jjW4/H4kabpixt2AbvAmDX+XnsAB509ww+A8mAar+XXgQAAAABJRU5ErkJggg==') left center no-repeat;\n}".getBytes(StandardCharsets.US_ASCII);
            FILE_CSS_BUFFER = ByteBuffer.allocateDirect(bytes.length);
            FILE_CSS_BUFFER.put(bytes);
            FILE_CSS_BUFFER.flip();
            bytes = "function growit() {\n    var table = document.getElementById(\"thetable\");\n\n    var i = table.rows.length - 1;\n    while (i-- > 0) {\n        if (table.rows[i].id == \"eraseme\") {\n            table.deleteRow(i);\n        } else {\n            break;\n        }\n    }\n    table.style.height=\"\";\n    var i = 0;\n    while (table.offsetHeight < window.innerHeight - 24) {\n        i++;\n        var tbody = table.tBodies[0];\n        var row = tbody.insertRow(tbody.rows.length);\n        row.id=\"eraseme\";\n        var cell = row.insertCell(0);\n        if (table.rows.length % 2 != 0) {\n            row.className=\"even eveninvis\";\n        } else {\n            row.className=\"odd oddinvis\";\n        }\n\n        cell.colSpan=3;\n        cell.appendChild(document.createTextNode(\"i\"));\n    }\n    table.style.height=\"100%\";\n    if (i > 0) {\n        document.documentElement.style.overflowY=\"hidden\";\n    } else {\n        document.documentElement.style.overflowY=\"auto\";\n    }\n}".getBytes(StandardCharsets.US_ASCII);
            FILE_JS_BUFFER = ByteBuffer.allocateDirect(bytes.length);
            FILE_JS_BUFFER.put(bytes);
            FILE_JS_BUFFER.flip();
         } catch (Exception var1) {
            throw new IllegalStateException(var1);
         }
      }
   }
}
