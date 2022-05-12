package org.weshley.fishtracker2;

public class WaterDepth
{
   public enum Units { ft, m }

   private float _depth;
   private WaterDepth.Units _units;

   public WaterDepth(float depth)
   {
      _depth = depth;
      _units = Config.getDefaultWaterDepthUnits();
   }

   public WaterDepth(float depth, WaterDepth.Units units)
   {
      _depth = depth;
      _units = units;
   }

   public float getDepth()
   {
      return _depth;
   }

   public WaterDepth.Units getUnits()
   {
      return _units;
   }

   public String valueString() { return "" + _depth; }

   public String toXmlValue() { return Utils.toXmlValue(valueString() + _units); }

}
