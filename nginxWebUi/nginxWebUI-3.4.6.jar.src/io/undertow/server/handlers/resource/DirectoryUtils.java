/*     */ package io.undertow.server.handlers.resource;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.ETag;
/*     */ import io.undertow.util.ETagUtils;
/*     */ import io.undertow.util.FlexBase64;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.RedirectBuilder;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import org.xnio.channels.Channels;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DirectoryUtils
/*     */ {
/*     */   public static boolean sendRequestedBlobs(HttpServerExchange exchange) {
/*  55 */     ByteBuffer buffer = null;
/*  56 */     String type = null;
/*  57 */     String etag = null;
/*  58 */     String quotedEtag = null;
/*  59 */     if ("css".equals(exchange.getQueryString())) {
/*  60 */       buffer = Blobs.FILE_CSS_BUFFER.duplicate();
/*  61 */       type = "text/css";
/*  62 */       etag = Blobs.FILE_CSS_ETAG;
/*  63 */       quotedEtag = Blobs.FILE_CSS_ETAG_QUOTED;
/*  64 */     } else if ("js".equals(exchange.getQueryString())) {
/*  65 */       buffer = Blobs.FILE_JS_BUFFER.duplicate();
/*  66 */       type = "application/javascript";
/*  67 */       etag = Blobs.FILE_JS_ETAG;
/*  68 */       quotedEtag = Blobs.FILE_JS_ETAG_QUOTED;
/*     */     } 
/*     */     
/*  71 */     if (buffer != null) {
/*     */       
/*  73 */       if (!ETagUtils.handleIfNoneMatch(exchange, new ETag(false, etag), false)) {
/*  74 */         exchange.setStatusCode(304);
/*  75 */         return true;
/*     */       } 
/*     */       
/*  78 */       exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, String.valueOf(buffer.limit()));
/*  79 */       exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, type);
/*  80 */       exchange.getResponseHeaders().put(Headers.ETAG, quotedEtag);
/*  81 */       if (Methods.HEAD.equals(exchange.getRequestMethod())) {
/*  82 */         exchange.endExchange();
/*  83 */         return true;
/*     */       } 
/*  85 */       exchange.getResponseSender().send(buffer);
/*     */       
/*  87 */       return true;
/*     */     } 
/*     */     
/*  90 */     return false;
/*     */   }
/*     */   
/*     */   public static StringBuilder renderDirectoryListing(String path, Resource resource) {
/*  94 */     if (!path.endsWith("/")) {
/*  95 */       path = path + "/";
/*     */     }
/*  97 */     StringBuilder builder = new StringBuilder();
/*  98 */     builder.append("<html>\n<head>\n<script src='").append(path).append("?js'></script>\n")
/*  99 */       .append("<link rel='stylesheet' type='text/css' href='").append(path).append("?css' />\n</head>\n");
/* 100 */     builder.append("<body onresize='growit()' onload='growit()'>\n<table id='thetable'>\n<thead>\n");
/* 101 */     builder.append("<tr><th class='loc' colspan='3'>Directory Listing - ").append(path).append("</th></tr>\n")
/* 102 */       .append("<tr><th class='label offset'>Name</th><th class='label'>Last Modified</th><th class='label'>Size</th></tr>\n</thead>\n")
/* 103 */       .append("<tfoot>\n<tr><th class=\"loc footer\" colspan=\"3\">Powered by Undertow</th></tr>\n</tfoot>\n<tbody>\n");
/*     */     
/* 105 */     int state = 0;
/* 106 */     String parent = null;
/* 107 */     if (path.length() > 1) {
/* 108 */       for (int j = path.length() - 1; j >= 0; j--) {
/* 109 */         if (state == 1) {
/* 110 */           if (path.charAt(j) == '/') {
/* 111 */             state = 2;
/*     */           }
/* 113 */         } else if (path.charAt(j) != '/') {
/* 114 */           if (state == 2) {
/* 115 */             parent = path.substring(0, j + 1);
/*     */             break;
/*     */           } 
/* 118 */           state = 1;
/*     */         } 
/*     */       } 
/* 121 */       if (parent == null) {
/* 122 */         parent = "/";
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 127 */     SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.US);
/* 128 */     int i = 0;
/* 129 */     if (parent != null) {
/* 130 */       i++;
/* 131 */       builder.append("<tr class='odd'><td><a class='icon up' href='").append(parent).append("'>[..]</a></td><td>");
/* 132 */       builder.append(format.format((resource.getLastModified() == null) ? new Date(0L) : resource.getLastModified()))
/* 133 */         .append("</td><td>--</td></tr>\n");
/*     */     } 
/*     */     
/* 136 */     for (Resource entry : resource.list()) {
/* 137 */       builder.append("<tr class='").append(((++i & 0x1) == 1) ? "odd" : "even").append("'><td><a class='icon ");
/* 138 */       builder.append(entry.isDirectory() ? "dir" : "file");
/* 139 */       builder.append("' href='").append(path).append(entry.getName()).append("'>").append(entry.getName()).append("</a></td><td>");
/* 140 */       builder.append(format.format((entry.getLastModified() == null) ? new Date(0L) : entry.getLastModified()))
/* 141 */         .append("</td><td>");
/* 142 */       if (entry.isDirectory()) {
/* 143 */         builder.append("--");
/*     */       } else {
/* 145 */         formatSize(builder, entry.getContentLength());
/*     */       } 
/* 147 */       builder.append("</td></tr>\n");
/*     */     } 
/* 149 */     builder.append("</tbody>\n</table>\n</body>\n</html>");
/*     */     
/* 151 */     return builder;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void renderDirectoryListing(HttpServerExchange exchange, Resource resource) {
/* 156 */     String requestPath = exchange.getRequestPath();
/* 157 */     if (!requestPath.endsWith("/")) {
/* 158 */       exchange.setStatusCode(302);
/* 159 */       exchange.getResponseHeaders().put(Headers.LOCATION, RedirectBuilder.redirect(exchange, exchange.getRelativePath() + "/", true));
/* 160 */       exchange.endExchange();
/*     */       
/*     */       return;
/*     */     } 
/* 164 */     StringBuilder builder = renderDirectoryListing(requestPath, resource);
/*     */     
/*     */     try {
/* 167 */       ByteBuffer output = ByteBuffer.wrap(builder.toString().getBytes(StandardCharsets.UTF_8));
/* 168 */       exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
/* 169 */       exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, String.valueOf(output.limit()));
/* 170 */       exchange.getResponseHeaders().put(Headers.LAST_MODIFIED, DateUtils.toDateString(new Date()));
/* 171 */       exchange.getResponseHeaders().put(Headers.CACHE_CONTROL, "must-revalidate");
/* 172 */       Channels.writeBlocking((WritableByteChannel)exchange.getResponseChannel(), output);
/* 173 */     } catch (UnsupportedEncodingException e) {
/* 174 */       throw new IllegalStateException(e);
/* 175 */     } catch (IOException e) {
/* 176 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 177 */       exchange.setStatusCode(500);
/*     */     } 
/*     */     
/* 180 */     exchange.endExchange();
/*     */   }
/*     */ 
/*     */   
/*     */   private static StringBuilder formatSize(StringBuilder builder, Long size) {
/* 185 */     if (size == null) {
/* 186 */       builder.append("???");
/* 187 */       return builder;
/*     */     } 
/* 189 */     int n = 1073741824;
/* 190 */     int type = 0;
/* 191 */     while (size.longValue() < n && n >= 1024) {
/* 192 */       n /= 1024;
/* 193 */       type++;
/*     */     } 
/*     */     
/* 196 */     long top = size.longValue() * 100L / n;
/* 197 */     long bottom = top % 100L;
/* 198 */     top /= 100L;
/*     */     
/* 200 */     builder.append(top);
/* 201 */     if (bottom > 0L) {
/* 202 */       builder.append(".").append(bottom / 10L);
/* 203 */       bottom %= 10L;
/* 204 */       if (bottom > 0L) {
/* 205 */         builder.append(bottom);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 210 */     switch (type) { case 0:
/* 211 */         builder.append(" GB"); break;
/* 212 */       case 1: builder.append(" MB"); break;
/* 213 */       case 2: builder.append(" KB");
/*     */         break; }
/*     */     
/* 216 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Blobs
/*     */   {
/*     */     public static final String FILE_JS = "function growit() {\n    var table = document.getElementById(\"thetable\");\n\n    var i = table.rows.length - 1;\n    while (i-- > 0) {\n        if (table.rows[i].id == \"eraseme\") {\n            table.deleteRow(i);\n        } else {\n            break;\n        }\n    }\n    table.style.height=\"\";\n    var i = 0;\n    while (table.offsetHeight < window.innerHeight - 24) {\n        i++;\n        var tbody = table.tBodies[0];\n        var row = tbody.insertRow(tbody.rows.length);\n        row.id=\"eraseme\";\n        var cell = row.insertCell(0);\n        if (table.rows.length % 2 != 0) {\n            row.className=\"even eveninvis\";\n        } else {\n            row.className=\"odd oddinvis\";\n        }\n\n        cell.colSpan=3;\n        cell.appendChild(document.createTextNode(\"i\"));\n    }\n    table.style.height=\"100%\";\n    if (i > 0) {\n        document.documentElement.style.overflowY=\"hidden\";\n    } else {\n        document.documentElement.style.overflowY=\"auto\";\n    }\n}";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 266 */     public static final String FILE_JS_ETAG = DirectoryUtils.md5("function growit() {\n    var table = document.getElementById(\"thetable\");\n\n    var i = table.rows.length - 1;\n    while (i-- > 0) {\n        if (table.rows[i].id == \"eraseme\") {\n            table.deleteRow(i);\n        } else {\n            break;\n        }\n    }\n    table.style.height=\"\";\n    var i = 0;\n    while (table.offsetHeight < window.innerHeight - 24) {\n        i++;\n        var tbody = table.tBodies[0];\n        var row = tbody.insertRow(tbody.rows.length);\n        row.id=\"eraseme\";\n        var cell = row.insertCell(0);\n        if (table.rows.length % 2 != 0) {\n            row.className=\"even eveninvis\";\n        } else {\n            row.className=\"odd oddinvis\";\n        }\n\n        cell.colSpan=3;\n        cell.appendChild(document.createTextNode(\"i\"));\n    }\n    table.style.height=\"100%\";\n    if (i > 0) {\n        document.documentElement.style.overflowY=\"hidden\";\n    } else {\n        document.documentElement.style.overflowY=\"auto\";\n    }\n}".getBytes(StandardCharsets.US_ASCII));
/* 267 */     public static final String FILE_JS_ETAG_QUOTED = '"' + FILE_JS_ETAG + '"';
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String FILE_CSS = "body {\n    font-family: \"Lucida Grande\", \"Lucida Sans Unicode\", \"Trebuchet MS\", Helvetica, Arial, Verdana, sans-serif;\n    margin: 5px;\n}\n\nth.loc {\n    background-image: linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -o-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -moz-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -webkit-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -ms-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    \n    background-image: -webkit-gradient(\n        linear,\n        left bottom,\n        left top,\n        color-stop(0.08, rgb(153,151,153)),\n        color-stop(0.54, rgb(199,199,199))\n    );\n    color: black;\n    padding: 2px;\n    font-weight: normal;\n    border: solid 1px;\n    font-size: 150%;\n    text-align: left;\n}\n\nth.label {\n    border: solid  1px;\n    text-align: left;\n    padding: 4px;\n    padding-left: 8px;\n    font-weight: normal;\n    font-size: small;\n    background-color: #e8e8e8;\n}\n\nth.offset {\n    padding-left: 32px;\n}\n\nth.footer {\n    font-size: 75%;\n    text-align: right;\n}\n\na.icon {\n    padding-left: 24px;\n    text-decoration: none;\n    color: black;\n}\n\na.icon:hover {\n    text-decoration: underline;\n}\n\ntable {\n    border: 1px solid;\n    border-spacing: 0px;\n    width: 100%;\n    border-collapse: collapse;\n}\n\ntr.odd {\n    background-color: #f3f6fa;\n}\n\ntr.odd td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.even {\n    background-color: #ffffff;\n}\n\ntr.even td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.eveninvis td {\n    color: #ffffff;\n}\n\ntr.oddinvis td {\n    color: #f3f6fa\n}\n\na.up {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABI0lEQVQ4y2P4//8/Ay7sM4nhPwjjUwMm0ua//Y+M0+e//QrSGDAfgvEZAjdgydHXcAzTXLjWDoxhhqBbhGLA1N0vwBhdM7ohMHVwA8yrzn4zLj/936j8FE7N6IaA1IL0gPQy2DVc+rnp3FeCmtENAekB6WXw7Lz1tWD5x/+wEIdhdI3o8iA9IL0MYZMfvq9a9+V/w+avcIzLAGQ1ID0gvQxJc56/aNn29X/vnm9wjMsAZDWtQD0gvQwFy94+6N37/f/Moz/gGJcByGpAekB6GarXf7427ciP/0vP/YRjdP/CMLIakB6QXobKDd9PN+769b91P2kYpAekl2HJhb8r11/583/9ZRIxUM+8U783MQCBGBDXAHEbibgGrBdfTiMGU2wAAPz+nxp+TnhDAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.dir {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAAA+UlEQVQ4jWP4//8/AyUYTKTNf/sfGafPf/s1be47G5IMWHL0NRxP2f3mbcaCtz/RDUbHKAZM3f2CJAw3wLzq7Dfj8tP/jcpPkYRBekB6GewaLv3cdO7r/y0XSMMgPSC9DJ6dt74WLP/4v3TVZ5IwSA9IL0PY5Ifvq9Z9+d+w+StJGKQHpJchac7zFy3bvv7v3fONJNwK1APSy5C/7O2D3r3f/888+oMkDNID0stQvf7ztWlHfvxfeu4nSRikB6SXoXLD99ONu379b91PGgbpAellWHLh38r1V/78X3+ZRAzUM/fUr00MQCAGxDVA3EYirgHrpUpupAQDAPs+7c1tGDnPAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.file {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAABM0lEQVQ4y5WSTW6DMBCF3xvzc4wuOEIO0kVAuUB7vJ4g3KBdoHSRROomEpusUaoAcaYLfmKoqVRLIxnJ7/M3YwJVBcknACv8b+1U9SvoP1bXa/3WNDVIAQmQBLsNOEsGQYAwDNcARgDqusbl+wIRA2NkBEyqP0s+kCOAQhhjICJdkaDIJDwEvQAhH+G+SHagWTsi4jHoAWYIOxYDZDjnb8Fn4Akvz6AHcAbx3Tp5ETwI3RwckyVtv4Fr4VEe9qq6bDB5tlnYWou2bWGtRRRF6jdwAm5Za1FVFc7nM0QERVG8A9hPDRaGpapomgZlWSJJEuR5ftpsNq8ADr9amC+SuN/vuN1uIIntdnvKsuwZwKf2wxgBxpjpX+dA4jjW4/H4kabpixt2AbvAmDX+XnsAB509ww+A8mAar+XXgQAAAABJRU5ErkJggg==') left center no-repeat;\n}";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 371 */     public static final String FILE_CSS_ETAG = DirectoryUtils.md5("body {\n    font-family: \"Lucida Grande\", \"Lucida Sans Unicode\", \"Trebuchet MS\", Helvetica, Arial, Verdana, sans-serif;\n    margin: 5px;\n}\n\nth.loc {\n    background-image: linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -o-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -moz-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -webkit-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -ms-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    \n    background-image: -webkit-gradient(\n        linear,\n        left bottom,\n        left top,\n        color-stop(0.08, rgb(153,151,153)),\n        color-stop(0.54, rgb(199,199,199))\n    );\n    color: black;\n    padding: 2px;\n    font-weight: normal;\n    border: solid 1px;\n    font-size: 150%;\n    text-align: left;\n}\n\nth.label {\n    border: solid  1px;\n    text-align: left;\n    padding: 4px;\n    padding-left: 8px;\n    font-weight: normal;\n    font-size: small;\n    background-color: #e8e8e8;\n}\n\nth.offset {\n    padding-left: 32px;\n}\n\nth.footer {\n    font-size: 75%;\n    text-align: right;\n}\n\na.icon {\n    padding-left: 24px;\n    text-decoration: none;\n    color: black;\n}\n\na.icon:hover {\n    text-decoration: underline;\n}\n\ntable {\n    border: 1px solid;\n    border-spacing: 0px;\n    width: 100%;\n    border-collapse: collapse;\n}\n\ntr.odd {\n    background-color: #f3f6fa;\n}\n\ntr.odd td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.even {\n    background-color: #ffffff;\n}\n\ntr.even td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.eveninvis td {\n    color: #ffffff;\n}\n\ntr.oddinvis td {\n    color: #f3f6fa\n}\n\na.up {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABI0lEQVQ4y2P4//8/Ay7sM4nhPwjjUwMm0ua//Y+M0+e//QrSGDAfgvEZAjdgydHXcAzTXLjWDoxhhqBbhGLA1N0vwBhdM7ohMHVwA8yrzn4zLj/936j8FE7N6IaA1IL0gPQy2DVc+rnp3FeCmtENAekB6WXw7Lz1tWD5x/+wEIdhdI3o8iA9IL0MYZMfvq9a9+V/w+avcIzLAGQ1ID0gvQxJc56/aNn29X/vnm9wjMsAZDWtQD0gvQwFy94+6N37/f/Moz/gGJcByGpAekB6GarXf7427ciP/0vP/YRjdP/CMLIakB6QXobKDd9PN+769b91P2kYpAekl2HJhb8r11/583/9ZRIxUM+8U783MQCBGBDXAHEbibgGrBdfTiMGU2wAAPz+nxp+TnhDAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.dir {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAAA+UlEQVQ4jWP4//8/AyUYTKTNf/sfGafPf/s1be47G5IMWHL0NRxP2f3mbcaCtz/RDUbHKAZM3f2CJAw3wLzq7Dfj8tP/jcpPkYRBekB6GewaLv3cdO7r/y0XSMMgPSC9DJ6dt74WLP/4v3TVZ5IwSA9IL0PY5Ifvq9Z9+d+w+StJGKQHpJchac7zFy3bvv7v3fONJNwK1APSy5C/7O2D3r3f/888+oMkDNID0stQvf7ztWlHfvxfeu4nSRikB6SXoXLD99ONu379b91PGgbpAellWHLh38r1V/78X3+ZRAzUM/fUr00MQCAGxDVA3EYirgHrpUpupAQDAPs+7c1tGDnPAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.file {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAABM0lEQVQ4y5WSTW6DMBCF3xvzc4wuOEIO0kVAuUB7vJ4g3KBdoHSRROomEpusUaoAcaYLfmKoqVRLIxnJ7/M3YwJVBcknACv8b+1U9SvoP1bXa/3WNDVIAQmQBLsNOEsGQYAwDNcARgDqusbl+wIRA2NkBEyqP0s+kCOAQhhjICJdkaDIJDwEvQAhH+G+SHagWTsi4jHoAWYIOxYDZDjnb8Fn4Akvz6AHcAbx3Tp5ETwI3RwckyVtv4Fr4VEe9qq6bDB5tlnYWou2bWGtRRRF6jdwAm5Za1FVFc7nM0QERVG8A9hPDRaGpapomgZlWSJJEuR5ftpsNq8ADr9amC+SuN/vuN1uIIntdnvKsuwZwKf2wxgBxpjpX+dA4jjW4/H4kabpixt2AbvAmDX+XnsAB509ww+A8mAar+XXgQAAAABJRU5ErkJggg==') left center no-repeat;\n}".getBytes(StandardCharsets.US_ASCII));
/* 372 */     public static final String FILE_CSS_ETAG_QUOTED = '"' + FILE_CSS_ETAG + '"';
/*     */     
/*     */     public static final ByteBuffer FILE_CSS_BUFFER;
/*     */     
/*     */     public static final ByteBuffer FILE_JS_BUFFER;
/*     */     
/*     */     static {
/*     */       try {
/* 380 */         byte[] bytes = "body {\n    font-family: \"Lucida Grande\", \"Lucida Sans Unicode\", \"Trebuchet MS\", Helvetica, Arial, Verdana, sans-serif;\n    margin: 5px;\n}\n\nth.loc {\n    background-image: linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -o-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -moz-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -webkit-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -ms-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    \n    background-image: -webkit-gradient(\n        linear,\n        left bottom,\n        left top,\n        color-stop(0.08, rgb(153,151,153)),\n        color-stop(0.54, rgb(199,199,199))\n    );\n    color: black;\n    padding: 2px;\n    font-weight: normal;\n    border: solid 1px;\n    font-size: 150%;\n    text-align: left;\n}\n\nth.label {\n    border: solid  1px;\n    text-align: left;\n    padding: 4px;\n    padding-left: 8px;\n    font-weight: normal;\n    font-size: small;\n    background-color: #e8e8e8;\n}\n\nth.offset {\n    padding-left: 32px;\n}\n\nth.footer {\n    font-size: 75%;\n    text-align: right;\n}\n\na.icon {\n    padding-left: 24px;\n    text-decoration: none;\n    color: black;\n}\n\na.icon:hover {\n    text-decoration: underline;\n}\n\ntable {\n    border: 1px solid;\n    border-spacing: 0px;\n    width: 100%;\n    border-collapse: collapse;\n}\n\ntr.odd {\n    background-color: #f3f6fa;\n}\n\ntr.odd td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.even {\n    background-color: #ffffff;\n}\n\ntr.even td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.eveninvis td {\n    color: #ffffff;\n}\n\ntr.oddinvis td {\n    color: #f3f6fa\n}\n\na.up {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABI0lEQVQ4y2P4//8/Ay7sM4nhPwjjUwMm0ua//Y+M0+e//QrSGDAfgvEZAjdgydHXcAzTXLjWDoxhhqBbhGLA1N0vwBhdM7ohMHVwA8yrzn4zLj/936j8FE7N6IaA1IL0gPQy2DVc+rnp3FeCmtENAekB6WXw7Lz1tWD5x/+wEIdhdI3o8iA9IL0MYZMfvq9a9+V/w+avcIzLAGQ1ID0gvQxJc56/aNn29X/vnm9wjMsAZDWtQD0gvQwFy94+6N37/f/Moz/gGJcByGpAekB6GarXf7427ciP/0vP/YRjdP/CMLIakB6QXobKDd9PN+769b91P2kYpAekl2HJhb8r11/583/9ZRIxUM+8U783MQCBGBDXAHEbibgGrBdfTiMGU2wAAPz+nxp+TnhDAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.dir {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAAA+UlEQVQ4jWP4//8/AyUYTKTNf/sfGafPf/s1be47G5IMWHL0NRxP2f3mbcaCtz/RDUbHKAZM3f2CJAw3wLzq7Dfj8tP/jcpPkYRBekB6GewaLv3cdO7r/y0XSMMgPSC9DJ6dt74WLP/4v3TVZ5IwSA9IL0PY5Ifvq9Z9+d+w+StJGKQHpJchac7zFy3bvv7v3fONJNwK1APSy5C/7O2D3r3f/888+oMkDNID0stQvf7ztWlHfvxfeu4nSRikB6SXoXLD99ONu379b91PGgbpAellWHLh38r1V/78X3+ZRAzUM/fUr00MQCAGxDVA3EYirgHrpUpupAQDAPs+7c1tGDnPAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.file {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAABM0lEQVQ4y5WSTW6DMBCF3xvzc4wuOEIO0kVAuUB7vJ4g3KBdoHSRROomEpusUaoAcaYLfmKoqVRLIxnJ7/M3YwJVBcknACv8b+1U9SvoP1bXa/3WNDVIAQmQBLsNOEsGQYAwDNcARgDqusbl+wIRA2NkBEyqP0s+kCOAQhhjICJdkaDIJDwEvQAhH+G+SHagWTsi4jHoAWYIOxYDZDjnb8Fn4Akvz6AHcAbx3Tp5ETwI3RwckyVtv4Fr4VEe9qq6bDB5tlnYWou2bWGtRRRF6jdwAm5Za1FVFc7nM0QERVG8A9hPDRaGpapomgZlWSJJEuR5ftpsNq8ADr9amC+SuN/vuN1uIIntdnvKsuwZwKf2wxgBxpjpX+dA4jjW4/H4kabpixt2AbvAmDX+XnsAB509ww+A8mAar+XXgQAAAABJRU5ErkJggg==') left center no-repeat;\n}".getBytes(StandardCharsets.US_ASCII);
/* 381 */         FILE_CSS_BUFFER = ByteBuffer.allocateDirect(bytes.length);
/* 382 */         FILE_CSS_BUFFER.put(bytes);
/* 383 */         FILE_CSS_BUFFER.flip();
/*     */         
/* 385 */         bytes = "function growit() {\n    var table = document.getElementById(\"thetable\");\n\n    var i = table.rows.length - 1;\n    while (i-- > 0) {\n        if (table.rows[i].id == \"eraseme\") {\n            table.deleteRow(i);\n        } else {\n            break;\n        }\n    }\n    table.style.height=\"\";\n    var i = 0;\n    while (table.offsetHeight < window.innerHeight - 24) {\n        i++;\n        var tbody = table.tBodies[0];\n        var row = tbody.insertRow(tbody.rows.length);\n        row.id=\"eraseme\";\n        var cell = row.insertCell(0);\n        if (table.rows.length % 2 != 0) {\n            row.className=\"even eveninvis\";\n        } else {\n            row.className=\"odd oddinvis\";\n        }\n\n        cell.colSpan=3;\n        cell.appendChild(document.createTextNode(\"i\"));\n    }\n    table.style.height=\"100%\";\n    if (i > 0) {\n        document.documentElement.style.overflowY=\"hidden\";\n    } else {\n        document.documentElement.style.overflowY=\"auto\";\n    }\n}".getBytes(StandardCharsets.US_ASCII);
/* 386 */         FILE_JS_BUFFER = ByteBuffer.allocateDirect(bytes.length);
/* 387 */         FILE_JS_BUFFER.put(bytes);
/* 388 */         FILE_JS_BUFFER.flip();
/* 389 */       } catch (Exception e) {
/* 390 */         throw new IllegalStateException(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String md5(byte[] buffer) {
/*     */     try {
/* 402 */       MessageDigest md = MessageDigest.getInstance("MD5");
/* 403 */       md.update(buffer);
/* 404 */       byte[] digest = md.digest();
/* 405 */       return new String(FlexBase64.encodeBytes(digest, 0, digest.length, false), StandardCharsets.US_ASCII);
/* 406 */     } catch (NoSuchAlgorithmException e) {
/*     */       
/* 408 */       throw new InternalError("MD5 not supported on this platform");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\DirectoryUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */