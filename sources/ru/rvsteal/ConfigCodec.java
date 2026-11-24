package ru.rvsteal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

final class ConfigCodec {
   private static final String PREFIX = "KILLERKC2:1:";

   static String encode(Properties settings) throws IOException {
      Properties copy = new Properties();
      copy.putAll(settings);
      copy.remove("access.key");
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      copy.store(output, "KillerKC2");
      return "KILLERKC2:1:" + Base64.getEncoder().encodeToString(output.toByteArray());
   }

   static Properties decode(String input) throws IOException {
      String value = input.trim();
      if (value.startsWith("KILLERKC2:1:") && value.length() <= 65536) {
         byte[] bytes = Base64.getDecoder().decode(value.substring("KILLERKC2:1:".length()));
         Properties p = new Properties();
         p.load(new ByteArrayInputStream(bytes));
         p.remove("access.key");
         if (p.isEmpty()) {
            throw new IllegalArgumentException("Empty config");
         } else {
            return p;
         }
      } else {
         throw new IllegalArgumentException("Invalid config");
      }
   }
}
