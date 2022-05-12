package org.weshley.fishtracker2;

public class WindRange
{
   private Speed _speedStart = null;
   private Speed _speedEnd = null;
   private Config.Direction _dirStart = null;
   private Config.Direction _dirEnd = null;
   private Config.WindStrength _strength = null;

   public WindRange()
   {
   }

   public WindRange(Speed sp, Config.Direction dir, Config.WindStrength str)
   {
      _speedStart = sp;
      _dirStart = dir;
      _strength = str;
   }

   public WindRange(Speed speedStart, Config.Direction dirStart,
                    Speed speedEnd, Config.Direction dirEnd,
                    Config.WindStrength str)
   {
      _speedStart = speedStart;
      _speedEnd = speedEnd;
      _dirStart = dirStart;
      _dirEnd = dirEnd;
      _strength = str;
   }

   public void setSpeedStart(Speed sp) { _speedStart = sp; }
   public Speed getSpeedStart() { return _speedStart; }

   public void setSpeedEnd(Speed sp) { _speedEnd = sp; }
   public Speed getSpeedEnd() { return _speedEnd; }

   public Config.Direction getDirectionStart() { return _dirStart; }
   public void setDirectionStart(Config.Direction dir) { _dirStart = dir; }

   public Config.Direction getDirectionEnd() { return _dirEnd; }
   public void setDirectionEnd(Config.Direction dir) { _dirEnd = dir; }

   public Config.WindStrength getStrength() { return _strength; }
   public void setStrength(Config.WindStrength str) { _strength = str; }

   public String toXmlValue()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(getSpeedString());
      sb.append(",");
      sb.append(getDirectionString());
      sb.append(",");
      if(null != _strength)
         sb.append(_strength);
      return Utils.toXmlValue(sb.toString());
   }

   public static WindRange fromXmlString(String xmlValue)
   {
      // string looks like:
      //    ""
      //    ",,"
      //    "2mph,N,steady"
      //    "2mph,N,"
      //    "2-5mph,NE-NNE,intermittent"
      //    etc
      String s = Utils.fromXmlValue(xmlValue);
      WindRange w = new WindRange();
      String[] pieces = Utils.split(s, ',');
      if(pieces.length != 3)
         return null;  // TODO - report error somewhere...

      // speed
      String[] speedPieces = Utils.split(pieces[0], '-');
      if(speedPieces.length == 1)
      {
         int idx = Utils.firstNonDigitIndex(speedPieces[1]);
         if(idx == -1)
         {
            w._speedStart = new Speed(Float.parseFloat(speedPieces[1]));
         }
         else
         {
            float spd = Float.parseFloat(speedPieces[1].substring(0, idx));
            w._speedStart = new Speed(spd, Speed.Units.valueOf(speedPieces[1].substring(idx)));
         }
      }
      else if(speedPieces.length == 2)
      {
         int idx = Utils.firstNonDigitIndex(speedPieces[1]);
         float spd = Float.parseFloat(speedPieces[1].substring(0, idx));
         w._speedEnd = new Speed(spd, Speed.Units.valueOf(speedPieces[1].substring(idx)));
         Speed.Units u = w._speedEnd.getUnits();
         idx = Utils.firstNonDigitIndex(speedPieces[0]);
         if(-1 != idx)
         {
            u = Speed.Units.valueOf(speedPieces[0].substring(idx));
            spd = Float.parseFloat(speedPieces[0].substring(0, idx));
         }
         else
         {
            spd = Float.parseFloat(speedPieces[0]);
         }
         w._speedStart = new Speed(spd, u);
      }
      else
      {
         // TODO - report error?
      }

      // dir
      String[] dirPieces = Utils.split(pieces[1], '-');
      if(dirPieces.length == 1)
      {
         w._dirStart = Config.Direction.valueOf(dirPieces[0]);
      }
      else if(dirPieces.length == 2)
      {
         w._dirStart = Config.Direction.valueOf(dirPieces[0]);
         w._dirEnd = Config.Direction.valueOf(dirPieces[1]);
      }
      else
      {
         // TODO - report error?
      }

      // strength
      if(!pieces[2].isEmpty())
         w._strength = Config.WindStrength.valueOf(pieces[2]);
      return w;
   }

   private String getDirectionString()
   {
      StringBuilder sb = new StringBuilder();
      if(null != _dirStart)
         sb.append(_dirStart);
      sb.append("-");
      if(null != _dirEnd)
         sb.append(_dirEnd);
      return sb.toString();
   }

   private String getSpeedString()
   {
      StringBuilder sb = new StringBuilder();
      if(null != _speedStart)
      {
         if(null != _speedEnd)
         {
            if(_speedStart.getUnits() == _speedEnd.getUnits())
               sb.append(_speedStart.valueString() + "-" + _speedEnd.valueString() + _speedStart.getUnits());
            else
               sb.append(_speedStart.valueWithUnits() + "-" + _speedEnd.valueWithUnits());
         }
         else
         {
            sb.append(_speedStart.valueWithUnits() + "-");
         }
      }
      else
      {
         if(null != _speedEnd)
            sb.append("-" + _speedEnd.valueWithUnits());
      }
      return sb.toString();
   }
}
