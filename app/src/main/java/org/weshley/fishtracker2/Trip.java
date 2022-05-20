package org.weshley.fishtracker2;

import java.util.*;

public class Trip
{
   // TODO - add code to store and retrieve trips from disk
   private static final List<Trip> _allTrips = new ArrayList<>();

   // TODO - add fields to the UI
   private String _transport = null;
   private Distance _distanceTraveled = null;
   private String _location = "";
   private Date _startTime = null;
   private Date _endTime = null;
   private Temperature _airTempStart = null;
   private Temperature _airTempEnd = null;

   private Temperature _waterTemp = null;
   private String _waterLevel = null;
   // NOTE - using string since this could be a number like ft above sea level, or something like "high"
   private Config.Clarity _waterClarity = null;
   private WaterDepth _secchi = null;

   private Config.Precipitation _precip = null;
   private WindRange _windRange = null;

   private LatLon[] _track = null;  // TODO - Trip._track needs implementation

   private String _notes = null;

   private List<Catch> _catches = new ArrayList<>();

   public static List<Trip> getAllTrips()
   {
      return _allTrips;
   }

   public static Trip getMostRecentTrip()
   {
      // TODO - should probably sort _allTrips by start time, unless i sort them this way when loading them....
      if(_allTrips.isEmpty())
         return null;
      else
         return _allTrips.get(_allTrips.size() - 1);
   }

   public static Trip createNewTrip()
   {
      return createNewTrip(null);
   }

   public static Trip createNewTrip(Trip lastTrip)
   {
      Trip t = new Trip();
      t.setFieldsFrom(lastTrip);
      _allTrips.add(t);
      return t;
   }

   public static List<Lure> getAllLures()
   {
      // using Lure.toString to collect only one instance of a lure that was found in multiple catches
      Map<String,Lure> lureMap = new TreeMap<>();
      for(Trip t : Trip.getAllTrips())
      {
         for(Catch c : t.getCatches())
         {
            if((null != c.getLure()) && !Lure.NULL_LURE.equals(c.getLure()))
               lureMap.put(c.getLure().toString(), c.getLure());
         }
      }

      List<Lure> lures = new ArrayList<>();
      lures.addAll(lureMap.values());
      return lures;
   }

   public void setFieldsFrom(Trip lastTrip)
   {
      if(null == lastTrip)
         return;
      _transport = lastTrip.getTransport();
      _notes = lastTrip.getNotes();
      _precip = lastTrip.getPrecip();
      _secchi = lastTrip.getSecchi();
      _distanceTraveled = lastTrip.getDistanceTraveled();
      _airTempEnd = lastTrip.getAirTempEnd();
      _airTempStart = lastTrip.getAirTempStart();
      _endTime = lastTrip.getEndTime();
      _startTime = lastTrip.getStartTime();
      _waterClarity = lastTrip.getWaterClarity();
      _waterLevel = lastTrip.getWaterLevel();
      _waterTemp = lastTrip.getWaterTemp();
      if(null != lastTrip.getWindRange())
         _windRange = new WindRange(lastTrip.getWindRange());
      else
         _windRange = null;
      // TODO - deal with _track once i start logging gps data
   }

   private Trip()
   {
      _startTime = new Date();
   }

   public void dumpData(StringBuilder sb)
   {
      sb.append(toXml());
   }

   public String toXml()
   {
      // NOTE:  Tried a few ways to use std java libraries to format the xml nicely, but nothing seemed to
      // work very well so doing it myself.
      StringBuilder sb = new StringBuilder();
      sb.append("<trip\n");
      if(null != _location)
         sb.append("   location=\"").append(Utils.toXmlValue(_location)).append("\"\n");
      if(null != _startTime)
         sb.append("   startTime=\"").append(Utils.toXmlValue(Config.formatTimestamp(_startTime))).append("\"\n");
      if(null != _endTime)
         sb.append("   endTime=\"").append(Utils.toXmlValue(Config.formatTimestamp(_endTime))).append("\"\n");
      if(null != _transport)
         sb.append("   transport=\"").append(Utils.toXmlValue(_transport)).append("\"\n");
      if(null != _distanceTraveled)
         sb.append("   distance=\"").append(_distanceTraveled.toXmlValue()).append("\"\n");
      if(null != _airTempStart)
         sb.append("   airTempStart=\"").append(_airTempStart.toXmlValue()).append("\"\n");
      if(null != _airTempEnd)
         sb.append("   airTempEnd=\"").append(_airTempEnd.toXmlValue()).append("\"\n");
      if(null != _waterTemp)
         sb.append("   waterTemp=\"").append(_waterTemp.toXmlValue()).append("\"\n");
      if(null != _waterLevel)
         sb.append("   waterLevel=\"").append(Utils.toXmlValue(_waterLevel)).append("\"\n");
      if(null != _waterClarity)
         sb.append("   waterClarity=\"").append(Utils.toXmlValue(_waterClarity.toString())).append("\"\n");
      if(null != _secchi)
         sb.append("   secchi=\"").append(_secchi.toXmlValue()).append("\"\n");
      if(null != _windRange)
         sb.append("   wind=\"").append(_windRange.toXmlValue()).append("\"\n");
      if(null != _precip)
         sb.append("   precipitation=\"").append(Utils.toXmlValue(_precip.toString())).append("\"\n");
      if(null != _notes)
         sb.append("   notes=\"").append(Utils.toXmlValue(_notes)).append("\"\n");
      sb.append("   >\n");

      for(Catch c : _catches)
         sb.append(c.toXml("   ")).append("\n\n");

      // TODO - add track info
      if(null != _track)
         sb.append("   <track />").append("\n");

      sb.append("</trip>\n");
      return sb.toString();
   }

   // TODO - add fromXml()

   public String getNotes() { return _notes; }
   public void setNotes(String s)
   {
      if((null != s) && s.isEmpty())
         _notes = null;
      else
        _notes = s;
   }

   public Temperature getAirTempStart() { return _airTempStart; }
   public void setAirTempStart(Temperature t) { _airTempStart = t; }
   public Temperature getAirTempEnd() { return _airTempEnd; }
   public void setAirTempEnd(Temperature t) { _airTempEnd = t; }

   public Temperature getWaterTemp() { return _waterTemp; }
   public void setWaterTemp(Temperature t) { _waterTemp = t; }
   public String getWaterLevel() { return _waterLevel; }
   public void setWaterLevel(String s)
   {
      if((null == s) || s.isEmpty())
         _waterLevel = null;
      else
         _waterLevel = s;
   }
   public Config.Clarity getWaterClarity() { return _waterClarity; }
   public void setWaterClarity(Config.Clarity c) { _waterClarity = c; }
   public WaterDepth getSecchi() { return _secchi; }
   public void setSecchi(WaterDepth d) { _secchi = d; }

   public Distance getDistanceTraveled() { return _distanceTraveled; }
   public void setDistanceTraveled(Distance d) { _distanceTraveled = d; }

   public WindRange getWindRange() { return _windRange; }
   public void setWindRange(WindRange w) { _windRange = w; }

   public Wind getWindStart()
   {
      if(null == _windRange)
         return null;
      else
         return _windRange.getStart();
   }

   public void setWindStart(Wind w)
   {
      if((null == _windRange) && (null != w))
         _windRange = new WindRange();
      if(null != _windRange)
         _windRange.setStart(w);
   }

   public Wind getWindEnd()
   {
      if(null == _windRange)
         return null;
      else
         return _windRange.getEnd();
   }

   public void setWindEnd(Wind w)
   {
      if((null == _windRange) && (null != w))
         _windRange = new WindRange();
      if(null != _windRange)
         _windRange.setEnd(w);
   }

   public void setWindStrengthStart(Config.WindStrength s)
   {
      if((null == _windRange) && (null != s))
         _windRange = new WindRange();
      if(null != _windRange)
         _windRange.setStrengthStart(s);
   }

   public void setWindStrengthEnd(Config.WindStrength s)
   {
      if((null == _windRange) && (null != s))
         _windRange = new WindRange();
      if(null != _windRange)
         _windRange.setStrengthEnd(s);
   }

   public void setWindStartDir(Config.Direction d)
   {
      if((null == _windRange) && (null != d))
         _windRange = new WindRange();
      if(null != _windRange)
         _windRange.setDirectionStart(d);
   }

   public void setWindStartSpeed(Speed s)
   {
      if((null == _windRange) && (null != s))
         _windRange = new WindRange();
      if(null != _windRange)
         _windRange.setSpeedStart(s);
   }

   public void setWindEndDir(Config.Direction d)
   {
      if((null == _windRange) && (null != d))
         _windRange = new WindRange();
      if(null != _windRange)
         _windRange.setDirectionEnd(d);
   }

   public void setWindEndSpeed(Speed s)
   {
      if((null == _windRange) && (null != s))
         _windRange = new WindRange();
      if(null != _windRange)
         _windRange.setSpeedEnd(s);
   }

   public Config.Precipitation getPrecip() { return _precip; }
   public void setPrecip(Config.Precipitation p) { _precip = p; }

   public void setLocation(String loc)
   {
      if((null == loc) || loc.isEmpty())
      {
         _location = null;
      }
      else
      {
         _location = loc;
         Config.addLocation(loc);
      }
   }

   public String getLocation()
   {
      return _location;
   }

   public void setTransport(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _transport = null;
      }
      else
      {
         _transport = s;
         Config.addTransport(s);
      }
   }

   public String getTransport()
   {
      return _transport;
   }

   public Date getStartTime()
   {
      return _startTime;
   }

   public Date getEndTime()
   {
      return _endTime;
   }

   public void endTrip()
   {
      if(null == _endTime)
         _endTime = new Date();
   }

   public boolean isEnded()
   {
      return null != _endTime;
   }

   public List<Catch> getCatches()
   {
      return _catches;
   }

   public Catch getLastCatch()
   {
      if(_catches.isEmpty())
         return null;
      return _catches.get(_catches.size() - 1);
   }

   public Catch newCatch()
   {
      Catch c = new Catch(this, getLastCatch());
      c.setTimestamp(new Date());
      _catches.add(c);
      return c;
   }

   public String getLabel()
   {
      return _location + " - " + Config.formatTimestamp(_startTime) + " (" + _catches.size() + ")";
   }

   public String getMultilineLabel()
   {
      String catchLabel = _catches.size() == 1 ? " catch" : " catches";
      return (_location == null ? "" : _location + "\n")
         + Config.formatTimestamp(_startTime) + "\n"
         + _catches.size() + catchLabel;
   }
}
