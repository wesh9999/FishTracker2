package org.weshley.fishtracker2;

import java.util.Date;

public class Catch
{
   private Date _timestamp = null;
   private LatLon _gpsLocation = null;
   private String _species = null;
   private FishLength _length = null;
   private FishWeight _weight = null;
   private Lure _lure = null;
   private WaterDepth _depth = null;
   private Temperature _airTemp = null;
   private Temperature _waterTemp = null;
   private Config.Clarity _waterClarity = null;
   private WaterDepth _secchi = null;
   private String _structure = null;
   private String _cover = null;
   private Config.Precipitation _precip = null;
   private Wind _wind = null;
   private String _notes = null;

   public Catch(Trip trip, Catch lastCatch)
   {
      updateFieldsFrom(lastCatch);
      fillFieldsFromTrip(trip);
   }

   public void dumpData(StringBuilder sb)
   {
      sb.append(toXml());
   }

   public Date getTimestamp()
   {
      return _timestamp;
   }

   public void setTimestamp(Date dt)
   {
      _timestamp = dt;
   }

   public String getSpecies()
   {
      return _species;
   }

   public void setSpecies(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _species = null;
      }
      else
      {
         _species = s;
         Config.addSpecies(s);
      }
   }

   public String getStructure()
   {
      return _structure;
   }

   public void setStructure(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _structure = null;
      }
      else
      {
         _structure = s;
         Config.addStructure(s);
      }
   }

   public String getCover()
   {
      return _cover;
   }

   public void setCover(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _cover = null;
      }
      else
      {
         _cover = s;
         Config.addCover(s);
      }
   }

   public FishLength getLength() { return _length; }
   public void setLength(FishLength l) { _length = l; }

   public FishWeight getWeight() { return _weight; }
   public void setWeight(FishWeight w) { _weight = w; }

   public void setWaterTemp(Temperature t) { _waterTemp = t; }
   public Temperature getWaterTemp() { return _waterTemp; }

   public void setAirTemp(Temperature t) { _airTemp = t; }
   public Temperature getAirTemp() { return _airTemp; }

   public void setDepth(WaterDepth d) { _depth = d; }
   public WaterDepth getDepth() { return _depth; }

   public Wind getWind()
   {
      return _wind;
   }

   public void setWind(Wind w)
   {
      _wind = w;
   }

   public Speed getWindSpeed()
   {
      if(null == _wind)
         return null;
      else
         return _wind.getSpeed();
   }
   public void setWindSpeed(Speed s)
   {
      if((null == _wind) && (null != s))
         _wind = new Wind();
      if(null != _wind)
         _wind.setSpeed(s);
   }

   public Config.Direction getWindDirection()
   {
      if(null == _wind)
         return null;
      else
         return _wind.getDirection();
   }

   public void setWindDirection(Config.Direction d)
   {
      if((null == _wind) && (null != d))
         _wind = new Wind();
      if(null != _wind)
         _wind.setDirection(d);
   }

   public Config.WindStrength getWindStrength()
   {
      if(null == _wind)
         return null;
      return _wind.getStrength();
   }

   public void setWindStrength(Config.WindStrength s)
   {
      if((null == _wind) && (null != s))
         _wind = new Wind();
      if(null != _wind)
         _wind.setStrength(s);
   }

   public Config.Precipitation getPrecip() { return _precip; }
   public void setPrecip(Config.Precipitation p) { _precip = p; }

   public Config.Clarity getWaterClarity() { return _waterClarity; }
   public void setWaterClarity(Config.Clarity c) { _waterClarity = c; }

   public WaterDepth getSecchi() { return _secchi; }
   public void setSecchi(WaterDepth d) { _secchi = d; }

   public String getNotes() { return _notes; }
   public void setNotes(String s)
   {
      if((null == s) || s.isEmpty())
         _notes = null;
      else
         _notes = s;
   }

   public LatLon getGpsLocation() { return _gpsLocation; }
   public void setGpsLocation(LatLon loc) { _gpsLocation = loc; }

   public Lure getLure() { return _lure; }

   public void setLure(Lure l)
   {
      _lure = l.copy();
      Config.addLure(_lure);
   }

   public void setLureType(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setType(s);
   }

   public String getLureType()
   {
      if(null == _lure)
         return null;
      else
         return _lure.getType();
   }

   public void setLureBrand(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setBrand(s);
   }

   public String getLureBrand()
   {
      if(null == _lure)
         return null;
      else
         return _lure.getBrand();
   }

   public void setLureSize(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setSize(s);
   }

   public String getLureSize()
   {
      if(null == _lure)
         return null;
      else
         return _lure.getSize();
   }

   public void setLureColor(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setColor(s);
   }

   public String getLureColor()
   {
      if(null == _lure)
         return null;
      else
         return _lure.getColor();
   }

   public void setTrailerType(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setTrailerType(s);
   }

   public String getTrailerType()
   {
      if(null == _lure)
         return null;
      else
         return _lure.getTrailer();
   }

   public void setTrailerSize(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setTrailerSize(s);
   }

   public String getTrailerSize()
   {
      if(null == _lure)
         return null;
      else
         return _lure.getTrailerSize();
   }

   public void setTrailerColor(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setTrailerColor(s);
   }

   public String getTrailerColor()
   {
      if(null == _lure)
         return null;
      else
         return _lure.getTrailerColor();
   }

   public String toXml()
   {
      return toXml("");
   }

   public String toXml(String indent)
   {
      // NOTE:  Tried a few ways to use std java libraries to format the xml nicely, but nothing seemed to
      // work very well so doing it myself.
      StringBuilder sb = new StringBuilder();
      sb.append(indent).append("<catch\n");
      if(null != _timestamp)
         sb.append(indent).append("   time=\"").append(Utils.toXmlValue(Config.formatTimestamp(_timestamp))).append("\"\n");
      if(null != _gpsLocation)
         sb.append(indent)
           .append("   lat=\"").append(_gpsLocation.getLat()).append("\"\n")
           .append("   lon=\"").append(_gpsLocation.getLon()).append("\"\n");
      if(null != _species)
         sb.append(indent).append("   species=\"").append(Utils.toXmlValue(_species)).append("\"\n");
      if(null != _length)
         sb.append(indent).append("   length=\"").append(_length.toXmlValue()).append("\"\n");
      if(null != _weight)
         sb.append(indent).append("   weight=\"").append(_weight.toXmlValue()).append("\"\n");
      if(null != _lure)
      {
         if(null != _lure.getType())
            sb.append(indent).append("   lureType=\"").append(Utils.toXmlValue(_lure.getType())).append("\"\n");
         if(null != _lure.getBrand())
            sb.append(indent).append("   lureBrand=\"").append(Utils.toXmlValue(_lure.getBrand())).append("\"\n");
         if(null != _lure.getColor())
            sb.append(indent).append("   lureColor=\"").append(Utils.toXmlValue(_lure.getColor())).append("\"\n");
         if(null != _lure.getSize())
            sb.append(indent).append("   lureSize=\"").append(Utils.toXmlValue(_lure.getSize())).append("\"\n");
         if(null != _lure.getTrailer())
            sb.append(indent).append("   lureTrailer=\"").append(Utils.toXmlValue(_lure.getTrailer())).append("\"\n");
         if(null != _lure.getTrailerColor())
            sb.append(indent).append("   trailerColor=\"").append(Utils.toXmlValue(_lure.getTrailerColor())).append("\"\n");
         if(null != _lure.getTrailerSize())
            sb.append(indent).append("   trailerSize=\"").append(Utils.toXmlValue(_lure.getTrailerSize())).append("\"\n");
      }
      if(null != _depth)
         sb.append(indent).append("   depth=\"").append(_depth.toXmlValue()).append("\"\n");
      if(null != _airTemp)
         sb.append(indent).append("   airTemp=\"").append(_airTemp.toXmlValue()).append("\"\n");
      if(null != _waterTemp)
         sb.append(indent).append("   waterTemp=\"").append(_waterTemp.toXmlValue()).append("\"\n");
      if(null != _waterClarity)
         sb.append(indent).append("   waterClarity=\"").append(Utils.toXmlValue(_waterClarity.toString())).append("\"\n");
      if(null != _secchi)
         sb.append(indent).append("   secchi=\"").append(_secchi.toXmlValue()).append("\"\n");
      if(null != _structure)
         sb.append(indent).append("   structure=\"").append(Utils.toXmlValue(_structure)).append("\"\n");
      if(null != _cover)
         sb.append(indent).append("   cover=\"").append(Utils.toXmlValue(_cover)).append("\"\n");
      if(null != _precip)
         sb.append(indent).append("   precipitation=\"").append(Utils.toXmlValue(_precip.toString())).append("\"\n");
      if(null != _wind)
         sb.append(indent).append("   wind=\"").append(_wind.toXmlValue()).append("\"\n");
      if(null != _notes)
         sb.append(indent).append("   notes=\"").append(Utils.toXmlValue(_notes)).append("\"\n");
      sb.append(indent).append("/>\n");
      return sb.toString();
   }

   public static Catch fromXml(String s)
   {
      // TODO - implement Catch.fromXml
      return null;
   }

   private void fillFieldsFromTrip(Trip t)
   {
      // default any null fields to values from the trip

      if(null == t)
         return; // shouldn't happen, but just in case...

      if(null == _waterTemp)
         _waterTemp = t.getWaterTemp();
      if(null == _airTemp)
         _airTemp = t.getAirTempStart();
      if((null == _wind) && (null != t.getWindStart()))
         _wind = new Wind(t.getWindStart());
      if(null == _precip)
         _precip = t.getPrecip();
      if(null == _waterClarity)
         _waterClarity = t.getWaterClarity();
      if(null == _secchi)
         _secchi = t.getSecchi();
   }

   private void updateFieldsFrom(Catch lastCatch)
   {
      if(null == lastCatch)
         return;
      _species = lastCatch.getSpecies();
      _length = lastCatch.getLength();
      _weight = lastCatch.getWeight();
      _waterTemp = lastCatch.getWaterTemp();
      _airTemp = lastCatch.getAirTemp();
      _depth = lastCatch.getDepth();
      if(null == lastCatch._wind)
         _wind = null;
      else
         _wind = new Wind(lastCatch.getWind());
      _precip = lastCatch.getPrecip();
      _waterClarity = lastCatch.getWaterClarity();
      _secchi = lastCatch.getSecchi();
      _notes = lastCatch.getNotes();
      _gpsLocation = lastCatch.getGpsLocation();
      _structure = lastCatch.getStructure();
      _cover = lastCatch.getCover();
      if(null == lastCatch.getLure())
         _lure = null;
      else
         _lure = lastCatch.getLure().copy();
   }
}
