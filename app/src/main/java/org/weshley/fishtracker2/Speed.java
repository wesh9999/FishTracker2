package org.weshley.fishtracker2;

public class Speed
{
   public enum Units { mph, kph }

   private float _speed;
   private Speed.Units _units;

   public Speed(Speed other)
   {
      _speed = other._speed;
      _units = other._units;
   }

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

   public String valueString() { return "" + _speed; }

   public String valueWithUnits() { return "" + _speed + _units; }

}
