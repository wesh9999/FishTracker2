package org.weshley.fishtracker2;

public class Distance
{
   public enum Units { m, km }

   private float _dist;
   private Distance.Units _units;

   public Distance(float dist)
   {
      _dist = dist;
      _units = Config.getDefaultDistanceUnits();
   }

   public Distance(float dist, Distance.Units units)
   {
      _dist = dist;
      _units = units;
   }

   public float getDistance()
   {
      return _dist;
   }

   public Distance.Units getUnits()
   {
      return _units;
   }

   public String valueString() { return "" + _dist; }

   public String toXmlValue() { return Utils.toXmlValue(valueString() + _units); }
}
