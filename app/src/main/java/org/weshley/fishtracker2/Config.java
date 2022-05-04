package org.weshley.fishtracker2;

import java.text.SimpleDateFormat;
import java.util.*;

public class Config
{
   public static final String BLANK_LABEL= "";
   public static final String OTHER_LABEL = "-- Other --";

   public enum Direction
   {
      UNDEFINED { public String toString() { return BLANK_LABEL; }},
      Variable, N, NE, E, SE, S, SW, W, NW
   }

   public enum WindStrength
   {
      UNDEFINED { public String toString() { return BLANK_LABEL; }},
      Intermittent, Steady, Growing, Waning
   }

   public enum Precipitation
   {
      UNDEFINED { public String toString() { return BLANK_LABEL; }},
      Clear, PartlyCloudy, MostlyCloudy, SolidClouds, Fog, LightRain, HeavyRain
   }

   public enum Clarity
   {
      UNDEFINED { public String toString() { return BLANK_LABEL; }},
      Low, Moderate, Good
   }

   private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
   private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
   private static final String DEFAULT_TIMESTAMP_FORMAT = DEFAULT_DATE_FORMAT + " " + DEFAULT_TIME_FORMAT;

   private static final String[] DEFAULT_LOCATIONS =
      { "Lake Hartwell", "Oliver Lake" };

   private static final String[] DEFAULT_TRANSPORTS =
      { "Kayak", "Boat", "Walking", "Dock", "Ice" };
   private static final String[] DEFAULT_SPECIES =
      { "Blue Catfish", "Blueback Herring", "Bream", "Carp", "Channel Catfish", "Crappie (Black)", "Flathead Catfish",
        "Hybrid Bass", "Largemouth Bass", "Longnose Gar", "Rainbow Trout", "Rock Bass", "Skipjack Shad",
        "Smallmouth Bass", "Spotted Bass", "Striped Bass", "Walleye", "White Bass" };

   private static final String[] DEFAULT_STRUCTURES =
      { "Bay", "Bluff", "Channel", "Dock", "Flat", "Hole", "Hump", "Open Water", "Pocket", "Point", "Shore"};

   private static final String[] DEFAULT_COVERS =
      { "Blowdown", "Boulder", "Brush", "Chunk Rock", "Grass", "Gravel", "Mud", "Pads", "Sand", "Standing Timber",
        "Weeds"};

   private static final String[] DEFAULT_LURE_TYPES =
      { "Blade", "Bucktail Jig", "Buzzbait", "Carolina (unweighted)", "Carolina (weighted)", "Chatter Bait",
        "Crankbait (deep)", "Crankbait (shallow)", "Chugger", "Dropshot", "Flatside (deep)", "Flatside (shallow)",
        "Herring", "Jerk Bait (shallow)", "Jerk Bait (deep)", "Jigging Spoon", "Lipless Crankbait", "Minnow", "Ned",
        "Powershot", "Scrounger", "Shiner", "Skirted Jig", "Spinnerbait", "Spoon", "Spybait", "Squarebill (deep)",
        "Squarebill (shallow)", "Swim Jig", "Texas (unweighted)", "Texas (weighted)", "Tokyo Rig", "Tube",
        "Underspin Jig", "Vertical Darting Jig", "Walking Stickbait", "Waxworm", "Worm"};

   private static final String[] DEFAULT_LURE_SIZES = {};
   private static final String[] DEFAULT_TRAILER_SIZES = {};
   private static final String[] DEFAULT_LURE_MODELS =
      { "Bandit", "Bobby Garland", "BugZ", "Fritside", "Jitterbug", "Nikko", "Powerbait", "Rattletrap", "Red Fin",
        "Rip Stop", "Road Runner", "Shad Rap", "Spook", "Steel Shad", "Swedish Pimple", "TicklerZ", "Whopper Plopper",
        "X-Rap", "Zoom"};

   private static final String[] DEFAULT_TRAILER_TYPES =
      { "Chunk", "Craw", "Creature", "Curly Tail", "Flatworm", "Fluke (paddle-tail)", "Fluke (split-tail)",
        "Hellgrammite", "Hula Grub", "Straight Worm", "Toad", "Waxworm"};

   private static final String[] DEFAULT_LURE_COLORS =
      { "Bluegill", "Clown", "Crawdad", "Firetiger", "Perch", "Shad",

        "Black", "Blue", "Brown", "Chartreuse", "Green", "Grey", "Gold", "Orange", "Pink", "Purple", "Red", "Silver",
        "White", "Yellow",

        "Black/Gold", "Black/Silver", "Black/White", "Blue/Black", "Blue/Brown", "Blue/Green/Orange", "Blue/Grey",
        "Blue/Orange", "Blue/Silver", "Blue/White", "Green/Grey", "Green/Red Flake", "Green/Silver",
        "Pink/Yelow/Green", "Purple/Silver", "Red/Grey", "Red/White", "White/Blue", "White/Green", "White/Red"};

   // TODO - need to save and restore lists (i.e. _allSpecies) so they persist across app invocations

   private static Set<String> _allLocations = new TreeSet<>();
   private static Set<String> _allTransports = new TreeSet<>();
   private static Set<String> _allSpecies = new TreeSet<>();
   private static Set<String> _allStructures = new TreeSet<>();
   private static Set<String> _allCovers = new TreeSet<>();
   private static Set<String> _allLureTypes = new TreeSet<>();
   private static Set<String> _allLureModels = new TreeSet<>();
   private static Set<String> _allLureSizes = new TreeSet<>();
   private static Set<String> _allTrailerTypes = new TreeSet<>();
   private static Set<String> _allTrailerSizes = new TreeSet<>();
   private static List<String> _allLureColors = new ArrayList<>();

   public static void addLure(Lure l)
   {
      if(null == l)
         return;
      addLureType(l.getType());
      addLureModel(l.getModel());
      addLureSize(l.getSize());
      addLureColor(l.getColor());
      addTrailerType(l.getTrailer());
      addTrailerSize(l.getTrailerSize());
      addLureColor(l.getTrailerColor());
   }
   public static void addLureType(String s)
   {
      if((null != s) && !s.isEmpty() && !_allLureTypes.contains(s))
         _allLureTypes.add(s);
   }

   public static Set<String> getAllLureTypes()
   {
      if(_allLureTypes.isEmpty())
      {
         // TODO - restore list if it was previously saved to disk
         for(String s : DEFAULT_LURE_TYPES)
            _allLureTypes.add(s);
      }
      return _allLureTypes;
   }

   public static void addLureSize(String s)
   {
      if((null != s) && !s.isEmpty() && !_allLureSizes.contains(s))
         _allLureSizes.add(s);
   }

   public static Set<String> getAllLureSizes()
   {
      if(_allLureSizes.isEmpty())
      {
         // TODO - restore list if it was previously saved to disk
         for(String s : DEFAULT_LURE_SIZES)
            _allLureSizes.add(s);
      }
      return _allLureSizes;
   }

   public static void addLureModel(String s)
   {
      if((null != s) && !s.isEmpty() && !_allLureModels.contains(s))
         _allLureModels.add(s);
   }

   public static Set<String> getAllLureModels()
   {
      if(_allLureModels.isEmpty())
      {
         // TODO - restore list if it was previously saved to disk
         for(String s : DEFAULT_LURE_MODELS)
            _allLureModels.add(s);
      }
      return _allLureModels;
   }

   public static void addLureColor(String s)
   {
      if((null != s) && !s.isEmpty() && !_allLureColors.contains(s))
         _allLureColors.add(s);
   }

   public static List<String> getAllLureColors()
   {
      if(_allLureColors.isEmpty())
      {
         // TODO - restore list if it was previously saved to disk
         for(String s : DEFAULT_LURE_COLORS)
            _allLureColors.add(s);
      }
      return _allLureColors;
   }

   public static void addTrailerSize(String s)
   {
      if((null != s) && !s.isEmpty() && !_allTrailerSizes.contains(s))
         _allTrailerSizes.add(s);
   }

   public static Set<String> getAllTrailerSizes()
   {
      if(_allTrailerSizes.isEmpty())
      {
         // TODO - restore list if it was previously saved to disk
         for(String s : DEFAULT_TRAILER_SIZES)
            _allTrailerSizes.add(s);
      }
      return _allTrailerSizes;
   }

   public static void addTrailerType(String s)
   {
      if((null != s) && !s.isEmpty() && !_allTrailerTypes.contains(s))
         _allTrailerTypes.add(s);
   }

   public static Set<String> getAllTrailerTypes()
   {
      if(_allTrailerTypes.isEmpty())
      {
         // TODO - restore list if it was previously saved to disk
         for(String s : DEFAULT_TRAILER_TYPES)
            _allTrailerTypes.add(s);
      }
      return _allTrailerTypes;
   }

   public static void addLocation(String s)
   {
      if((null != s) && !s.isEmpty() && !_allLocations.contains(s))
         _allLocations.add(s);
   }

   public static Set<String> getAllLocations()
   {
      if(_allLocations.isEmpty())
      {
         // TODO - restore list if it was previously saved to disk
         for(String s : DEFAULT_LOCATIONS)
            _allLocations.add(s);
      }
      return _allLocations;
   }

   public static void addTransport(String s)
   {
      if((null != s) && !s.isEmpty() && !_allTransports.contains(s))
         _allTransports.add(s);
   }

   public static Set<String> getAllTransports()
   {
      if(_allTransports.isEmpty())
      {
         // TODO - restore list if it was previously saved to disk
         for(String s : DEFAULT_TRANSPORTS)
            _allTransports.add(s);
      }
      return _allTransports;
   }
   public static void addSpecies(String s)
   {
      if((null != s) && !s.isEmpty() && !_allSpecies.contains(s))
         _allSpecies.add(s);
   }

   public static Set<String> getAllSpecies()
   {
      if(_allSpecies.isEmpty())
      {
          // TODO - restore list if it was previously saved to disk
          for(String s : DEFAULT_SPECIES)
             _allSpecies.add(s);
      }
      return _allSpecies;
   }

   public static void addStructure(String s)
   {
      if((null != s) && !s.isEmpty() && !_allStructures.contains(s))
         _allStructures.add(s);
   }

   public static Set<String> getAllStructures()
   {
      if(_allStructures.isEmpty())
      {
         // TODO - restore list if it was previously saved to disk
         for(String s : DEFAULT_STRUCTURES)
            _allStructures.add(s);
      }
      return _allStructures;
   }

   public static void addCover(String s)
   {
      if((null != s) && !s.isEmpty() && !_allCovers.contains(s))
         _allCovers.add(s);
   }

   public static Set<String> getAllCovers()
   {
      if(_allCovers.isEmpty())
      {
         // TODO - restore list if it was previously saved to disk
         for(String s : DEFAULT_COVERS)
            _allCovers.add(s);
      }
      return _allCovers;
   }

   public static String formatDate(Date dt)
   {
      SimpleDateFormat fmt = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
      return fmt.format(dt);
   }

   public static String formatTime(Date dt)
   {
      SimpleDateFormat fmt = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
      return fmt.format(dt);
   }
   public static String formatTimestamp(Date dt)
   {
      SimpleDateFormat fmt = new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT);
      return fmt.format(dt);
   }

   public static FishLength.Units getDefaultFishLengthUnits()
   {
      return FishLength.Units.in;
   }

   public static FishWeight.Units getDefaultFishWeightUnits()
   {
      return FishWeight.Units.lbs;
   }

   public static WaterDepth.Units getDefaultWaterDepthUnits()
   {
      return WaterDepth.Units.ft;
   }

   public static Speed.Units getDefaultWindSpeedUnits()
   {
      return Speed.Units.mph;
   }

   public static Temperature.Units getDefaultTempUnits()
   {
      return Temperature.Units.F;
   }
}
