package org.weshley.fishtracker2;

public class Temperature
{
   public enum Units { F, C }

   private float _temp;
   private Temperature.Units _units;

   public Temperature(float temp)
   {
      _temp = temp;
      _units = Config.getDefaultTempUnits();
   }

   public Temperature(float temp, Temperature.Units units)
   {
      _temp = temp;
      _units = units;
   }

   public float getTemp()
   {
      return _temp;
   }

   public Temperature.Units getUnits()
   {
      return _units;
   }

}
