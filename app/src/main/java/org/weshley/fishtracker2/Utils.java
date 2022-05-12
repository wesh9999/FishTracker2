package org.weshley.fishtracker2;

import java.util.ArrayList;
import java.util.Collection;

public class Utils
{
   public static String toXmlValue(String str)
   {
      if(str == null)
         return null;
      int len = str.length();
      if(len == 0)
         return str;

      StringBuffer encoded = new StringBuffer();
      for(int i = 0; i < len; i++)
      {
         char c = str.charAt(i);
         if(c == '<')
            encoded.append("&lt;");
         else if(c == '\"')
            encoded.append("&quot;");
         else if(c == '>')
            encoded.append("&gt;");
         else if(c == '\'')
            encoded.append("&apos;");
         else if(c == '&')
            encoded.append("&amp;");
         else
            encoded.append(c);
      }
      return encoded.toString();
   }

   public static String fromXmlValue(String s)
   {
      // TODO - Utils.fromXmlValue needs implementation
      throw new RuntimeException("Utils.fromXmlValue not implemeted");
   }

   public static String[] split(String source, char delim)
   {
      if((null == source) || source.isEmpty())
         return new String[0];
      ArrayList<String> pieces = new ArrayList<String>();
      Utils.split(source, delim, pieces);
      String[] strings = new String[pieces.size()];
      for(int i = 0; i < pieces.size(); ++i)
         strings[i] = (String) pieces.get(i);
      return strings;
   }

   public static void split(String source, char delim, Collection<String> dest)
   {
      dest.clear();
      if((null == source) || source.isEmpty())
         return;

      int start = 0;
      int end = 0;
      while(start < source.length())
      {
         end = source.indexOf(delim, start);
         if(end == -1)
         {
            dest.add(source.substring(start));
            break;
         }

         if(end == start)
            dest.add("");
         else
            dest.add(source.substring(start, end));
         start = end + 1;

         if(start >= source.length())
         {
            if(end == (source.length() - 1))
               dest.add("");
            break;
         }
      }
   }

   public static int firstNonDigitIndex(String s)
   {
      for(int i = 0; i < s.length(); ++i)
      {
         if(!Character.isDigit(s.charAt(i)))
            return i;
      }
      return -1;
   }

}
