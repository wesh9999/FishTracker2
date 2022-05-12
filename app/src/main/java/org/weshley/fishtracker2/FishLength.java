package org.weshley.fishtracker2;

public class FishLength
{
   public enum Units { in, cm }

   private float _length;
   private Units _units;

   public FishLength(float length)
   {
      _length = length;
      _units = Config.getDefaultFishLengthUnits();
   }

   public FishLength(float length, Units units)
   {
      _length = length;
      _units = units;
   }

   public float getLength()
   {
      return _length;
   }

   public Units getUnits()
   {
      return _units;
   }

   public String valueString()
   {
      return "" + _length;
   }

   public String toXmlValue() { return Utils.toXmlValue(valueString() + _units); }
}
