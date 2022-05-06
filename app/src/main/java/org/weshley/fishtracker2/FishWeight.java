package org.weshley.fishtracker2;

public class FishWeight
{
   public enum Units { lbs, kg }

   private float _weight;
   private FishWeight.Units _units;

   public FishWeight(float weight)
   {
      _weight = weight;
      _units = Config.getDefaultFishWeightUnits();
   }

   public FishWeight(float weight, FishWeight.Units units)
   {
      _weight = weight;
      _units = units;
   }

   public float getWeight()
   {
      return _weight;
   }

   public FishWeight.Units getUnits()
   {
      return _units;
   }

   public String valueString()
   {
      return "" + _weight;
   }
}
