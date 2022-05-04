package org.weshley.fishtracker2;

public class LatLon
{
   private float _lat;
   private float _lon;

   public LatLon()
   {
      // TODO - read gps to get _lat and _lon...
   }

   public LatLon(float lat, float lon)
   {
      _lat = lat;
      _lon = lon;
   }

   public float getLat() { return _lat; }
   public float getLon() { return _lon; }
}
