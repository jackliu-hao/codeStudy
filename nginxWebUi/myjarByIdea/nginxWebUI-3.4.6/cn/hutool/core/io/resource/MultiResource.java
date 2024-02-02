package cn.hutool.core.io.resource;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IORuntimeException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class MultiResource implements Resource, Iterable<Resource>, Iterator<Resource>, Serializable {
   private static final long serialVersionUID = 1L;
   private final List<Resource> resources;
   private int cursor;

   public MultiResource(Resource... resources) {
      this((Collection)CollUtil.newArrayList((Object[])resources));
   }

   public MultiResource(Collection<Resource> resources) {
      if (resources instanceof List) {
         this.resources = (List)resources;
      } else {
         this.resources = CollUtil.newArrayList(resources);
      }

   }

   public String getName() {
      return ((Resource)this.resources.get(this.cursor)).getName();
   }

   public URL getUrl() {
      return ((Resource)this.resources.get(this.cursor)).getUrl();
   }

   public InputStream getStream() {
      return ((Resource)this.resources.get(this.cursor)).getStream();
   }

   public boolean isModified() {
      return ((Resource)this.resources.get(this.cursor)).isModified();
   }

   public BufferedReader getReader(Charset charset) {
      return ((Resource)this.resources.get(this.cursor)).getReader(charset);
   }

   public String readStr(Charset charset) throws IORuntimeException {
      return ((Resource)this.resources.get(this.cursor)).readStr(charset);
   }

   public String readUtf8Str() throws IORuntimeException {
      return ((Resource)this.resources.get(this.cursor)).readUtf8Str();
   }

   public byte[] readBytes() throws IORuntimeException {
      return ((Resource)this.resources.get(this.cursor)).readBytes();
   }

   public Iterator<Resource> iterator() {
      return this.resources.iterator();
   }

   public boolean hasNext() {
      return this.cursor < this.resources.size();
   }

   public synchronized Resource next() {
      if (this.cursor >= this.resources.size()) {
         throw new ConcurrentModificationException();
      } else {
         ++this.cursor;
         return this;
      }
   }

   public void remove() {
      this.resources.remove(this.cursor);
   }

   public synchronized void reset() {
      this.cursor = 0;
   }

   public MultiResource add(Resource resource) {
      this.resources.add(resource);
      return this;
   }
}
