package org.weshley.fishtracker2;

import java.util.Date;

public class Catch
{
   private Date _timestamp = null;
   private String _species = null;
   private FishLength _length = null;
   private FishWeight _weight = null;
   private WaterDepth _depth = null;
   private Temperature _airTemp = null;
   private Temperature _waterTemp = null;
   private Speed _windSpeed = null;
   private Config.Direction _windDirection = null;  // TODO - add to UI
   private Config.WindStrength _windStrength = null; // TODO - add to UI
   private Config.Precipitation _precip = null; // TODO - add to UI
   private Config.Clarity _waterClarity = null; // TODO - add to UI
   private String _notes = null; // TODO - add to UI
   private LatLon _gpsLocation = null; // TODO - add to UI
   private String _structure = null;
   private String _cover = null;
   private Lure _lure = null;

   public Catch(Catch lastCatch)
   {
      updateFieldsFrom(lastCatch);
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

   public Speed getWindSpeed() { return _windSpeed; }
   public void setWindSpeed(Speed s) { _windSpeed = s; }

   public Config.Direction getWindDirection() { return _windDirection; }
   public void setWindDirection(Config.Direction d) { _windDirection = d; }

   public Config.WindStrength getWindStrength() { return _windStrength; }
   public void setWindStrength(Config.WindStrength s) { _windStrength = s; }

   public Config.Precipitation getPrecip() { return _precip; }
   public void setPrecip(Config.Precipitation p) { _precip = p; }

   public Config.Clarity getWaterClarity() { return _waterClarity; }
   public void setWaterClarity(Config.Clarity c) { _waterClarity = c; }

   public String getNotes() { return _notes; }
   public void setNotes(String s) { _notes = s; }

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

   public void setLureModel(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setModel(s);
   }

   public void setLureSize(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setSize(s);
   }

   public void setLureColor(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setColor(s);
   }

   public void setTrailerType(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setTrailerType(s);
   }

   public void setTrailerSize(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setTrailerSize(s);
   }

   public void setTrailerColor(String s)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setTrailerColor(s);
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
      _windSpeed = lastCatch.getWindSpeed();
      _windDirection = lastCatch.getWindDirection();
      _windStrength = lastCatch.getWindStrength();
      _precip = lastCatch.getPrecip();
      _waterClarity = lastCatch.getWaterClarity();
      _notes = lastCatch.getNotes();
      _gpsLocation = lastCatch.getGpsLocation();
      _structure = lastCatch.getStructure();
      _cover = lastCatch.getCover();
      if(null == lastCatch.getLure())
         _lure = null;
      else
         _lure = lastCatch.getLure().copy();
// TODO - add all the catch fields....
   }
}
