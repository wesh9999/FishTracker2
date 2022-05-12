package org.weshley.fishtracker2;

public class Wind
{
   private Speed _speed = null;
   private Config.Direction _dir = null;
   private Config.WindStrength _strength = null;

   public Wind()
   {
   }

   public Wind(Speed sp, Config.Direction dir, Config.WindStrength str)
   {
      _speed = sp;
      _dir = dir;
      _strength = str;
   }

   public Wind(Wind other)
   {
      _speed = other._speed;
      _dir = other._dir;
      _strength = other._strength;
   }

   public void setSpeed(Speed sp) { _speed = sp; }
   public Speed getSpeed() { return _speed; }

   public Config.Direction getDirection() { return _dir; }
   public void setDirection(Config.Direction dir) { _dir = dir; }

   public Config.WindStrength getStrength() { return _strength; }
   public void setStrength(Config.WindStrength str) { _strength = str; }

   public String toXmlValue()
   {
      StringBuilder sb = new StringBuilder();
      if(null != _speed)
         sb.append("-" + _speed.valueWithUnits());
      sb.append(",");
      if(null != _dir)
         sb.append(_dir);
      sb.append(",");
      if(null != _strength)
         sb.append(_strength);
      return Utils.toXmlValue(sb.toString());
   }

   public static Wind fromXmlString(String xmlValue)
   {
      // string looks like:
      //    ""
      //    ",,"
      //    "2mph,N,steady"
      //    etc
      String s = Utils.fromXmlValue(xmlValue);
      Wind w = new Wind();
      String[] pieces = Utils.split(s, ',');
      if(pieces.length != 3)
         return null;  // TODO - report error somewhere...

      // speed
      if(!pieces[0].isEmpty())
      {
         int idx = Utils.firstNonDigitIndex(pieces[0]);
         if(idx == -1)
         {
            w._speed = new Speed(Float.parseFloat(pieces[0]));
         } else
         {
            float spd = Float.parseFloat(pieces[0].substring(0, idx));
            w._speed = new Speed(spd, Speed.Units.valueOf(pieces[0].substring(idx)));
         }
      }

      // dir
      if(!pieces[1].isEmpty())
         w._dir = Config.Direction.valueOf(pieces[1]);

      // strength
      if(!pieces[2].isEmpty())
         w._strength = Config.WindStrength.valueOf(pieces[2]);

      return w;
   }

}
