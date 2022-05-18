package org.weshley.fishtracker2;

import androidx.core.view.WindowInsetsCompat;

public class WindRange
{
   private Wind _start = null;
   private Wind _end = null;

   public WindRange()
   {
   }

   public WindRange(WindRange other)
   {
      if(null != other)
      {
         _start = new Wind(other._start);
         _end = new Wind(other._end);
      }
   }

   public WindRange(Wind start, Wind end)
   {
      _start = new Wind(start);
      _end = new Wind(end);
   }

   public void setStart(Wind w)
   {
      if(null == w)
         _start = w;
      else
         _start = new Wind(w);
   }

   public Wind getStart() { return _start; }

   public void setEnd(Wind w)
   {
      if(null == w)
         _end = w;
      else
         _end = new Wind(w);
   }

   public Wind getEnd() { return _end; }

   public void setSpeedStart(Speed sp)
   {
      if((null == _start) && (null != sp))
         _start = new Wind();
      if(null != _start)
         _start.setSpeed(sp);
   }

   public Speed getSpeedStart()
   {
      if(null == _start)
         return null;
      else
         return _start.getSpeed();
   }

   public void setSpeedEnd(Speed sp)
   {
      if((null == _end) && (null != sp))
         _end = new Wind();
      if(null != _end)
         _end.setSpeed(sp);
   }

   public Speed getSpeedEnd()
   {
      if(null == _end)
         return null;
      else
         return _end.getSpeed();
   }

   public void setDirectionStart(Config.Direction dir)
   {
      if((null == _start) && (null != dir))
         _start = new Wind();
      if(null != _start)
         _start.setDirection(dir);
   }

   public Config.Direction getDirectionStart()
   {
      if(null == _start)
         return null;
      else
         return _start.getDirection();
   }

   public void setDirectionEnd(Config.Direction dir)
   {
      if((null == _end) && (null != dir))
         _end = new Wind();
      if(null != _end)
         _end.setDirection(dir);
   }

   public Config.Direction getDirectionEnd()
   {
      if(null == _end)
         return null;
      else
         return _end.getDirection();
   }

   public void setStrengthStart(Config.WindStrength str)
   {
      if((null == _start) && (null != str))
         _start = new Wind();
      if(null != _start)
         _start.setStrength(str);
   }

   public Config.WindStrength getStrengthStart()
   {
      if(null == _start)
         return null;
      else
         return _start.getStrength();
   }

   public void setStrengthEnd(Config.WindStrength str)
   {
      if((null == _end) && (null != str))
         _end = new Wind();
      if(null != _end)
         _end.setStrength(str);
   }

   public Config.WindStrength getStrengthEnd()
   {
      if(null == _end)
         return null;
      else
         return _end.getStrength();
   }

   public String toXmlValue()
   {
      StringBuilder sb = new StringBuilder();
      if(null != _start)
         sb.append(_start.toXmlValue());
      sb.append(";");
      if(null != _end)
         sb.append(_end.toXmlValue());
      return Utils.toXmlValue(sb.toString());
   }

   public static WindRange fromXmlString(String xmlValue)
   {
      // string looks like:
      //    ""
      //    ";"
      //    "2mph,N,steady"
      //    "2mph,N,steady;"
      //    ";2mph,N,steady"
      //    "2mph,N,"
      //    "2mph,NE,intermittent;5mph,E,steady"
      //    etc
      String s = Utils.fromXmlValue(xmlValue);
      if((null == s) || s.isEmpty())
         return null;

      String[] pieces = Utils.split(s, ';');
      if(pieces.length == 1)
      {
         WindRange w = new WindRange();
         w._start = Wind.fromXmlString(pieces[0]);
         return w;
      }
      else if(pieces.length == 2)
      {
         WindRange w = new WindRange();
         w._start = Wind.fromXmlString(pieces[0]);
         w._end = Wind.fromXmlString(pieces[1]);
         return w;
      }
      else
      {
         return null;  // TODO - report error somewhere...
      }
   }

}
