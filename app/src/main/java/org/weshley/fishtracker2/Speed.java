package org.weshley.fishtracker2;

public class Speed
{
   public enum Units { mph, kph }

   private float _speed;
   private Speed.Units _units;

   public Speed(float speed)
   {
      _speed = speed;
      _units = Config.getDefaultWindSpeedUnits();
   }

   public Speed(float speed, Speed.Units units)
   {
      _speed = speed;
      _units = units;
   }

   public float getSpeed()
   {
      return _speed;
   }

   public Speed.Units getUnits()
   {
      return _units;
   }

}
