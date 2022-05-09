package org.weshley.fishtracker2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trip
{
   private static final List<Trip> _allTrips = new ArrayList<>();

   // TODO - add details from Trips sheet....
   private String _transport = null;
   private String _location = "";
   private Date _startTime = null;
   private Date _endTime = null;
   private List<Catch> _catches = new ArrayList<>();

   public static Trip createNewTrip()
   {
      Trip t = new Trip();
      _allTrips.add(t);
      return t;
   }

   private Trip()
   {
      _startTime = new Date();
   }

   public void dumpData(StringBuilder sb)
   {
      // TODO - just dumping to stdout for now.  need to dump some structured data (xml?) to a file or email or something?
      sb.append("--------- Trip ----------\n");
      sb.append("   start: " + Config.formatTimestamp(_startTime) + "\n");
      sb.append("   end: " + Config.formatTimestamp(_endTime) + "\n");
      sb.append("   location: " + _location + "\n");
      sb.append("   transport: " + _transport + "\n");
      for(Catch c : _catches)
         c.dumpData(sb);
   }

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
      Catch c = new Catch(getLastCatch());
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
      return _location + "\n"
         + Config.formatTimestamp(_startTime) + "\n"
         + _catches.size() + catchLabel;
   }
}
