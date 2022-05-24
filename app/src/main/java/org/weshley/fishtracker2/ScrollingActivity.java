package org.weshley.fishtracker2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.core.app.ActivityCompat;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import org.weshley.fishtracker2.databinding.ActivityScrollingBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ScrollingActivity
   extends AppCompatActivity
{
   private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;

   private List<Trip> _trips = new ArrayList<>();
   private HashMap<String,Integer> _tripLocationMap;
   private HashMap<String,Integer> _tripTransportMap;
   private HashMap<String,Integer> _tripWaterClarityMap;

   private HashMap<String,Integer> _speciesMap;
   private HashMap<Lure,Integer> _lureMap;
   private HashMap<String,Integer> _lureTypeMap;
   private HashMap<String,Integer> _lureBrandMap;
   private HashMap<String,Integer> _colorMap;
   private HashMap<String,Integer> _lureSizeMap;
   private HashMap<String,Integer> _trailerMap;
   private HashMap<String,Integer> _trailerSizeMap;
   private HashMap<String,Integer> _structureMap;
   private HashMap<String,Integer> _coverMap;

   private EditText _newItemInputField = null;
   private AlertDialog _createItemDialog = null;

   private ActivityScrollingBinding _binding;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      _binding = ActivityScrollingBinding.inflate(getLayoutInflater());
      setContentView(_binding.getRoot());
      
      Toolbar toolbar = _binding.toolbar;
      setSupportActionBar(toolbar);
      CollapsingToolbarLayout toolBarLayout = _binding.toolbarLayout;
      toolBarLayout.setTitle(getTitle());

      FloatingActionButton fab = _binding.fab;
      fab.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View view)
         {
            //    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //            .setAction("Action", null).show()
            newCatch();
         }
      });

      initUiComponents();
      enableTripControls(false);
      enableCatchControls(false);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_scrolling, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if(id == R.id.action_start_trip)
      {
         startTrip();
         getTripLocationField().performClick();
         return true;
      }
      else if(id == R.id.action_end_trip)
      {
         endTrip();
         return true;
      }
      else if(id == R.id.action_new_catch)
      {
         newCatch();
         return true;
      }
      else if(id == R.id.action_dump_trip)
      {
         dumpTripData();
         return true;
      }
      else
      {
         return super.onOptionsItemSelected(item);
      }
   }

   private void dumpTripData()
   {
      ActivityCompat.requestPermissions(
        this,
         new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
         EXTERNAL_STORAGE_PERMISSION_CODE);

      // NOTE:  These files are ending up in /Android/data/org.weshley.fishtracker2/files/Downloads/
      TripLogger.instance().setDestinationFolder(
         this.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
      try
      {
         for(Trip t : _trips)
            TripLogger.instance().writeTrip(t);
         Toast.makeText(this, "Trips stored successfully", Toast.LENGTH_SHORT).show();
      }
      catch(Exception ex)
      {
         ex.printStackTrace();
         Toast.makeText(this, "FAILURE: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
      }
      // TODO - dump this to a file or email or something?
      // TODO - filter trips to dump
   }

   private Trip getCurrentTrip()
   {
      if(_trips.isEmpty())
         return null;
      Trip lastTrip = _trips.get(_trips.size() - 1);
      if(lastTrip.isEnded())
         return null;
      else
         return lastTrip;
   }

   private void startTrip()
   {
      if(null == getCurrentTrip())
      {
         Trip t = Trip.createNewTrip(Trip.getMostRecentTrip());
         _trips.add(t);
         t.setLocation("Lake Hartwell");  // TODO - remove this
         updateTripFields(t);
         enableTripControls(true);
      }
      else
      {
         showMessage("Trip has already been started");
      }
      updateMenuState();
   }

   private void endTrip()
   {
      Trip t = getCurrentTrip();
      if(null == t)
      {
         showMessage("No active trip");
         return;
      }

      t.endTrip();
      clearCatchFields();
      clearTripFields();
      enableCatchControls(false);
      enableTripControls(false);
      updateTripSummary(null);
      updateMenuState();
   }

   private void updateTripSummary(Trip t)
   {
      if(null == t)
         getTripInfoField().setText(R.string.no_trip_message);
      else
         getTripInfoField().setText(t.getMultilineLabel());
   }

   private void newCatch()
   {
      startTrip();
      updateCatchFields(getCurrentTrip().newCatch());
      enableCatchControls(true);
      updateTripSummary(getCurrentTrip()); // to update the catch counter...
      getSpeciesField().performClick();
   }

   private void updateTripFields(Trip t)
   {
      if(null == t)
      {
         clearTripFields();
         return;
      }

      updateTripSummary(t);
      setTripLocationSelection(t);
      setTripStartField(t);
      setTripEndField(t);
      setTripTransportSelection(t);
      setTripDistanceField(t);
      setTripAirTempStartField(t);
      setTripAirTempEndField(t);
      setTripWaterTempField(t);
      setTripWaterLevelField(t);
      setTripWaterClaritySelection(t);
      setTripSecchiField(t);
      setTripWindStartFields(t);
      setTripWindEndFields(t);
      setTripWindStrengthSelection(t);
      setTripPrecipSelection(t);
      setTripNotesField(t);
   }

   private void clearTripFields()
   {
      getTripInfoField().setText(R.string.no_trip_message);
      getTripLocationField().setSelection(0);
      getTripStartField().setText("");
      getTripEndField().setText("");
      getTripTransportField().setSelection(0);
      getTripDistanceField().setText("");
      getTripAirTempStartField().setText("");
      getTripAirTempEndField().setText("");
      getTripWaterTempField().setText("");
      getTripWaterLevelField().setText("");
      getTripWaterClarityField().setSelection(0);
      getTripSecchiField().setText("");
      getTripStartWindDirField().setSelection(0);
      getTripStartWindSpeedField().setText(0);
      getTripEndWindDirField().setSelection(0);
      getTripEndWindSpeedField().setText(0);
      getTripStartWindStrengthField().setSelection(0);
      getTripPrecipField().setSelection(0);
      getTripNotesField().setText("");
   }

   private void updateCatchFields(Catch c)
   {
      if(null == c)
      {
         clearCatchFields();
         return;
      }

      setWhenField(c);
      setGpsField(c);
      setSpeciesSelection(c);
      setLengthField(c);
      setWeightField(c);
      setLureFields(c);
      setDepthField(c);
      setAirTempField(c);
      setWaterTempField(c);
      setWaterClaritySelection(c);
      setSecchiField(c);
      setStructureSelection(c);
      setCoverSelection(c);
      setPrecipSelection(c);
      setWindSpeedField(c);
      setWindDirectionSelection(c);
      setWindStrengthSelection(c);
      setNotesField(c);
   }

   private void clearCatchFields()
   {
//      getTripInfoField().setText("");
      getWhenField().setText("");
      getGpsField().setText("");
      getSpeciesField().setSelection(0);
      getLengthField().setText("");
      getWeightField().setText("");
      getLureField().setSelection(0);
      getLureTypeField().setSelection(0);
      getLureBrandField().setSelection(0);
      getLureColorField().setSelection(0);
      getLureSizeField().setSelection(0);
      getTrailerField().setSelection(0);
      getTrailerColorField().setSelection(0);
      getTrailerSizeField().setSelection(0);
      getDepthField().setText("");
      getAirTempField().setText("");
      getWaterTempField().setText("");
      getWaterClarityField().setSelection(0);
      getSecchiField().setText("");
      getStructureField().setSelection(0);
      getCoverField().setSelection(0);
      getPrecipField().setSelection(0);
      getWindSpeedField().setText("");
      getWindDirectionField().setSelection(0);
      getWindStrengthField().setSelection(0);
      getNotesField().setText("");
   }

   private void initUiComponents()
   {
      initEditors();
      initUnitsLabels();
      initLabelClickListeners();
   }

   private void initLabelClickListeners()
   {
      // TODO - add catch when and gps fields once those are editable...
      // TODO - add trip fields once that UI is updated

      setLabelFieldClickListener(getLengthLabelField(), getLengthField());
      setLabelFieldClickListener(getWeightLabelField(), getWeightField());

      setSpinnerClickListener(getLureTypeLabelField(), getLureTypeField());
      setSpinnerClickListener(getLureBrandLabelField(), getLureBrandField());
      setSpinnerClickListener(getLureColorLabelField(), getLureColorField());
      setSpinnerClickListener(getLureSizeLabelField(), getLureSizeField());
      setSpinnerClickListener(getTrailerLabelField(), getTrailerField());
      setSpinnerClickListener(getTrailerColorLabelField(), getTrailerColorField());
      setSpinnerClickListener(getTrailerSizeLabelField(), getTrailerSizeField());

      setLabelFieldClickListener(getDepthLabelField(), getDepthField());
      setLabelFieldClickListener(getAirTempLabelField(), getAirTempField());
      setLabelFieldClickListener(getWaterTempLabelField(), getWaterTempField());
      setSpinnerClickListener(getWaterClarityLabelField(), getWaterClarityField());
      setLabelFieldClickListener(getSecchiLabelField(), getSecchiField());
      setSpinnerClickListener(getStructureLabelField(), getStructureField());
      setSpinnerClickListener(getCoverLabelField(), getCoverField());
      setSpinnerClickListener(getPrecipLabelField(), getPrecipField());
      setLabelFieldClickListener(getWindSpeedLabelField(), getWindSpeedField());
      setSpinnerClickListener(getWindDirectionLabelField(), getWindDirectionField());
      setSpinnerClickListener(getWindStrengthLabelField(), getWindStrengthField());
   }

   private void setSpinnerClickListener(TextView labelField, Spinner editField)
   {
      labelField.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View view)
         {
            editField.requestFocus();
            editField.performClick();
         }
      });
   }

   private void setLabelFieldClickListener(TextView labelField, TextView editField)
   {
      labelField.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View view)
         {
            editField.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editField, InputMethodManager.SHOW_IMPLICIT);
         }
      });
   }

   private void initUnitsLabels()
   {
      // trip units labels
      getTripDistanceUnitsField().setText(Config.getDefaultDistanceUnits().toString());
      getTripAirTempUnitsField().setText(Config.getDefaultTempUnits().toString());
      getTripWaterTempUnitsField().setText(Config.getDefaultTempUnits().toString());
      getTripSecchiUnitsField().setText(Config.getDefaultWaterDepthUnits().toString());
      getTripStartWindSpeedUnits().setText(Config.getDefaultWindSpeedUnits().toString());
      getTripEndWindSpeedUnits().setText(Config.getDefaultWindSpeedUnits().toString());

      // catch units labels
//      getLengthUnitsField().setText(Config.getDefaultFishLengthUnits().toString());
//      getWeightUnitsField().setText(Config.getDefaultFishWeightUnits().toString());
      setLengthUnitsLabel();
      setWeightUnitsLabel();
      setDepthUnitsLabel();
      setAirTempUnitsLabel();
      setWaterTempUnitsLabel();
      setSecchiUnitsLabel();
      setWindSpeedUnitsLabel();
   }

   // TODO - "Length", "Weight", "Wind Speed", "Secchi", "Depth, "Air Temp", and "Water Temp"
   //  should be resource strings

   private void setWindSpeedUnitsLabel()
   {
      setWindSpeedUnitsLabel(Config.getDefaultWindSpeedUnits());
   }

   private void setWindSpeedUnitsLabel(Speed.Units  units)
   {
      getWindSpeedLabelField().setText("Wind Speed (" + units.toString() + ")");
   }

   private void setSecchiUnitsLabel()
   {
      setSecchiUnitsLabel(Config.getDefaultWaterDepthUnits());
   }

   private void setSecchiUnitsLabel(WaterDepth.Units units)
   {
      getSecchiLabelField().setText("Secchi (" + units.toString() + ")");
   }

   private void setWeightUnitsLabel()
   {
      setWeightUnitsLabel(Config.getDefaultFishWeightUnits());
   }

   private void setWeightUnitsLabel(FishWeight.Units units)
   {
      getWeightLabelField().setText("Weight (" + units.toString() + ")");
   }

   private void setLengthUnitsLabel()
   {
      setLengthUnitsLabel(Config.getDefaultFishLengthUnits());
   }

   private void setLengthUnitsLabel(FishLength.Units units)
   {
      getLengthLabelField().setText("Length (" + units.toString() + ")");
   }

   private void setDepthUnitsLabel()
   {
      setDepthUnitsLabel(Config.getDefaultWaterDepthUnits());
   }

   private void setDepthUnitsLabel(WaterDepth.Units units)
   {
      getDepthLabelField().setText("Depth (" + units.toString() + ")");
   }

   private void setAirTempUnitsLabel()
   {
      setAirTempUnitsLabel(Config.getDefaultTempUnits());
   }

   private void setAirTempUnitsLabel(Temperature.Units units)
   {
      getAirTempLabelField().setText("Air Temp (" + units.toString() + ")");
   }

   private void setWaterTempUnitsLabel()
   {
      setWaterTempUnitsLabel(Config.getDefaultTempUnits());
   }

   private void setWaterTempUnitsLabel(Temperature.Units units)
   {
      getWaterTempLabelField().setText("Water Temp (" + units.toString() + ")");
   }

   private void initEditors()
   {
      // TODO - configure datetime editors for trip & catch start/end fields

      // trip fields
      initTripLocationEditor();
      initTripTransportEditor();
      initTripDistanceEditor();
      initTripAirTempStartEditor();
      initTripAirTempEndEditor();
      initTripWaterTempEditor();
      initTripWaterLevelEditor();
      initTripWaterClarityEditor();
      initTripSecchiEditor();
      initTripWindStartDirEditor();
      initTripWindStartSpeedEditor();
      initTripWindEndDirEditor();
      initTripWindEndSpeedEditor();
      initTripStartWindStrengthEditor();
      initTripEndWindStrengthEditor();
      initTripPrecipEditor();
      initTripNotesEditor();

      // catch fields
      initSpeciesEditor();
      initLengthEditor();
      initWeightEditor();
      initLureEditor();
      initLureTypeEditor();
      initLureBrandEditor();
      initLureColorEditor();
      initLureSizeEditor();
      initTrailerEditor();
      initTrailerColorEditor();
      initTrailerSizeEditor();
      initDepthEditor();
      initAirTempEditor();
      initWaterTempEditor();
      initWaterClarityEditor();
      initSecchiEditor();
      initStructureEditor();
      initCoverEditor();
      initPrecipEditor();
      initWindSpeedEditor();
      initWindDirectionEditor();
      initWindStrengthEditor();
      initNotesEditor();
   }

   private void enableCatchControls(boolean enabled)
   {
      // NOTE:  tripInfo, date, time, and gps fields are always not editable

      getWhenField().setEnabled(enabled);
      getGpsField().setEnabled(enabled);
      getSpeciesField().setEnabled(enabled);
      getLengthField().setEnabled(enabled);
      getWeightField().setEnabled(enabled);
      getLureField().setEnabled(enabled);
      getLureTypeField().setEnabled(enabled);
      getLureBrandField().setEnabled(enabled);
      getLureColorField().setEnabled(enabled);
      getLureSizeField().setEnabled(enabled);
      getTrailerField().setEnabled(enabled);
      getTrailerColorField().setEnabled(enabled);
      getTrailerSizeField().setEnabled(enabled);
      getDepthField().setEnabled(enabled);
      getAirTempField().setEnabled(enabled);
      getWaterTempField().setEnabled(enabled);
      getWaterClarityField().setEnabled(enabled);
      getSecchiField().setEnabled(enabled);
      getStructureField().setEnabled(enabled);
      getCoverField().setEnabled(enabled);
      getPrecipField().setEnabled(enabled);
      getWindSpeedField().setEnabled(enabled);
      getWindDirectionField().setEnabled(enabled);
      getWindStrengthField().setEnabled(enabled);
      getNotesField().setEnabled(enabled);
   }

   private void enableTripControls(boolean enabled)
   {
      // TODO - add more controls here when trip info is added to UI
      getTripLocationField().setEnabled(enabled);
      getTripStartField().setEnabled(enabled);
      getTripEndField().setEnabled(enabled);
      getTripTransportField().setEnabled(enabled);
      getTripDistanceField().setEnabled(enabled);
      getTripAirTempStartField().setEnabled(enabled);
      getTripAirTempEndField().setEnabled(enabled);
      getTripWaterTempField().setEnabled(enabled);
      getTripWaterLevelField().setEnabled(enabled);
      getTripWaterClarityField().setEnabled(enabled);
      getTripSecchiField().setEnabled(enabled);
      getTripStartWindDirField().setEnabled(enabled);
      getTripStartWindSpeedField().setEnabled(enabled);
      getTripEndWindDirField().setEnabled(enabled);
      getTripEndWindSpeedField().setEnabled(enabled);
      getTripStartWindStrengthField().setEnabled(enabled);
      getTripPrecipField().setEnabled(enabled);
      getTripNotesField().setEnabled(enabled);
   }

   private void initTripLocationEditorItems()
   {
      // TODO - add most recent locations (maybe last 5 from all trips?) plus a separator at top of list
      _tripLocationMap = new HashMap<>();
      initSpinnerItems(getTripLocationField(), Config.getAllLocations(), _tripLocationMap);
   }

   private void initTripLocationEditor()
   {
      initTripLocationEditorItems();
      getTripLocationField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final String selection = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(selection))
            {
               _newItemInputField = null;
               int oldSelection = 0;
               String s = getCurrentTrip().getLocation();
               if(null != s)
                  oldSelection = _tripLocationMap.get(s);
               openCreateItemDialog(
                     "Enter New Location",
                     getTripLocationField(), oldSelection,
                     new DialogInterface.OnClickListener()
                     {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                           String newItem = getNewItemInputField().getText().toString();
                           Trip t = getCurrentTrip();
                           t.setLocation(newItem);
                           initTripLocationEditorItems();
                           setTripLocationSelection(t);
                           updateTripSummary(t);
                        }
                     });
            }
            else
            {
               Trip t = getCurrentTrip();
               if(null != t)
                  t.setLocation(selection);
               updateTripSummary(t);
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initTripWaterClarityEditor()
   {
      List<String> values = new ArrayList<>();
      for(Config.Clarity c : Config.Clarity.values())
         values.add(c.toString());
      initFixedSpinnerItems(getTripWaterClarityField(), values);

      getTripWaterClarityField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            String selectedItem = (String) parent.getItemAtPosition(pos);
            Trip t = getCurrentTrip();
            if(null != t)
            {
               if(selectedItem.isEmpty())
                  t.setWaterClarity(null);
               else
                  t.setWaterClarity(Config.Clarity.valueOf(selectedItem));
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initTripPrecipEditor()
   {
      List<String> values = new ArrayList<>();
      for(Config.Precipitation p : Config.Precipitation.values())
         values.add(p.toString());
      initFixedSpinnerItems(getTripPrecipField(), values);

      getTripPrecipField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            String selectedItem = (String) parent.getItemAtPosition(pos);
            Trip t = getCurrentTrip();
            if(null != t)
            {
               if(selectedItem.isEmpty())
                  t.setPrecip(null);
               else
                  t.setPrecip(Config.Precipitation.valueOf(selectedItem));
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initTripStartWindStrengthEditor()
   {
      List<String> values = new ArrayList<>();
      for(Config.WindStrength s : Config.WindStrength.values())
         values.add(s.toString());
      initFixedSpinnerItems(getTripStartWindStrengthField(), values);

      getTripStartWindStrengthField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            String selectedItem = (String) parent.getItemAtPosition(pos);
            Trip t = getCurrentTrip();
            if(null != t)
            {
               if(selectedItem.isEmpty())
                  t.setWindStrengthStart(null);
               else
                  t.setWindStrengthStart(Config.WindStrength.valueOf(selectedItem));
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initTripEndWindStrengthEditor()
   {
      List<String> values = new ArrayList<>();
      for(Config.WindStrength s : Config.WindStrength.values())
         values.add(s.toString());
      initFixedSpinnerItems(getTripEndWindStrengthField(), values);

      getTripEndWindStrengthField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            String selectedItem = (String) parent.getItemAtPosition(pos);
            Trip t = getCurrentTrip();
            if(null != t)
            {
               if(selectedItem.isEmpty())
                  t.setWindStrengthEnd(null);
               else
                  t.setWindStrengthEnd(Config.WindStrength.valueOf(selectedItem));
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initTripWindStartDirEditor()
   {
      List<String> values = new ArrayList<>();
      for(Config.Direction d : Config.Direction.values())
         values.add(d.toString());
      initFixedSpinnerItems(getTripStartWindDirField(), values);

      getTripStartWindDirField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            String selectedItem = (String) parent.getItemAtPosition(pos);
            Trip t = getCurrentTrip();
            if(null != t)
            {
               if(selectedItem.isEmpty())
                  t.setWindStartDir(null);
               else
                  t.setWindStartDir(Config.Direction.valueOf(selectedItem));
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initTripWindEndDirEditor()
   {
      List<String> values = new ArrayList<>();
      for(Config.Direction d : Config.Direction.values())
         values.add(d.toString());
      initFixedSpinnerItems(getTripEndWindDirField(), values);

      getTripEndWindDirField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            String selectedItem = (String) parent.getItemAtPosition(pos);
            Trip t = getCurrentTrip();
            if(null != t)
            {
               if(selectedItem.isEmpty())
                  t.setWindEndDir(null);
               else
                  t.setWindEndDir(Config.Direction.valueOf(selectedItem));
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initTripDistanceEditor()
   {
      getTripDistanceField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Trip t = getCurrentTrip();
            if(null == t)
               return;

            Distance d = null;
            Distance.Units units = Config.getDefaultDistanceUnits();
            if(null != t.getDistanceTraveled())
               units = t.getDistanceTraveled().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               d = new Distance(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            t.setDistanceTraveled(d);
         }
      });
   }

   private void initTripAirTempStartEditor()
   {
      getTripAirTempStartField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Trip t = getCurrentTrip();
            if(null == t)
               return;

            Temperature temp = null;
            Temperature.Units units = Config.getDefaultTempUnits();
            if(null != t.getAirTempStart())
               units = t.getAirTempStart().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               temp = new Temperature(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            t.setAirTempStart(temp);
         }
      });
   }

   private void initTripAirTempEndEditor()
   {
      getTripAirTempEndField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Trip t = getCurrentTrip();
            if(null == t)
               return;

            Temperature temp = null;
            Temperature.Units units = Config.getDefaultTempUnits();
            if(null != t.getAirTempEnd())
               units = t.getAirTempEnd().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               temp = new Temperature(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            t.setAirTempEnd(temp);
         }
      });
   }

   private void initTripWaterTempEditor()
   {
      getTripWaterTempField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Trip t = getCurrentTrip();
            if(null == t)
               return;

            Temperature temp = null;
            Temperature.Units units = Config.getDefaultTempUnits();
            if(null != t.getWaterTemp())
               units = t.getWaterTemp().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               temp = new Temperature(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            t.setWaterTemp(temp);
         }
      });
   }

   private void initTripSecchiEditor()
   {
      getTripSecchiField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Trip t = getCurrentTrip();
            if(null == t)
               return;

            WaterDepth d = null;
            WaterDepth.Units units = Config.getDefaultWaterDepthUnits();
            if(null != t.getSecchi())
               units = t.getSecchi().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               d = new WaterDepth(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            t.setSecchi(d);
         }
      });
   }

   private void initTripWindStartSpeedEditor()
   {
      getTripStartWindSpeedField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Trip t = getCurrentTrip();
            if(null == t)
               return;

            Speed spd = null;
            Speed.Units units = Config.getDefaultWindSpeedUnits();
            if((null != t.getWindRange()) && (null != t.getWindRange().getSpeedStart()))
               units = t.getWindRange().getSpeedStart().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               spd = new Speed(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            t.setWindStartSpeed(spd);
         }
      });
   }

   private void initTripWindEndSpeedEditor()
   {
      getTripEndWindSpeedField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Trip t = getCurrentTrip();
            if(null == t)
               return;

            Speed spd = null;
            Speed.Units units = Config.getDefaultWindSpeedUnits();
            if((null != t.getWindRange()) && (null != t.getWindRange().getSpeedEnd()))
               units = t.getWindRange().getSpeedEnd().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               spd = new Speed(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            t.setWindEndSpeed(spd);
         }
      });
   }

   private void initTripWaterLevelEditor()
   {
      getTripWaterLevelField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Trip t = getCurrentTrip();
            if(null != t)
               t.setWaterLevel(s.toString());
         }
      });
   }

   private void initTripNotesEditor()
   {
      getTripNotesField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Trip t = getCurrentTrip();
            if(null != t)
               t.setNotes(s.toString());
         }
      });
   }


   private void initTripTransportEditorItems()
   {
      // TODO - add most recent transports (maybe last 5 from all trips?) plus a separator at top of list
      _tripTransportMap = new HashMap<>();
      initSpinnerItems(getTripTransportField(), Config.getAllTransports(), _tripTransportMap);
   }

   private void initTripTransportEditor()
   {
      initTripTransportEditorItems();
      getTripTransportField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final String selection = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(selection))
            {
               _newItemInputField = null;
               int oldSelection = 0;
               String s = getCurrentTrip().getTransport();
               if(null != s)
                  oldSelection = _tripTransportMap.get(s);
               openCreateItemDialog(
                     "Enter New Transport",
                     getTripTransportField(), oldSelection,
                     new DialogInterface.OnClickListener()
                     {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                           String newItem = getNewItemInputField().getText().toString();
                           getCurrentTrip().setTransport(newItem);
                           initTripTransportEditorItems();
                           setTripTransportSelection(getCurrentTrip());
                        }
                     });
            }
            else
            {
               Trip t = getCurrentTrip();
               if(null != t)
                  t.setTransport(selection);
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initAirTempEditor()
   {
      getAirTempField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Catch c = getCurrentCatch();
            if(null == c)
               return;

            Temperature temp = null;
            Temperature.Units units = Config.getDefaultTempUnits();
            if(null != c.getAirTemp())
               units = c.getAirTemp().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               temp = new Temperature(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            c.setAirTemp(temp);
         }
      });
   }

   private void initWaterTempEditor()
   {
      getWaterTempField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Catch c = getCurrentCatch();
            if(null == c)
               return;

            Temperature temp = null;
            Temperature.Units units = Config.getDefaultTempUnits();
            if(null != c.getWaterTemp())
               units = c.getWaterTemp().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               temp = new Temperature(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            c.setWaterTemp(temp);
         }
      });
   }

   private void initSecchiEditor()
   {
      getSecchiField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Catch c = getCurrentCatch();
            if(null == c)
               return;

            WaterDepth d = null;
            WaterDepth.Units units = Config.getDefaultWaterDepthUnits();
            if(null != c.getSecchi())
               units = c.getSecchi().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               d = new WaterDepth(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            c.setSecchi(d);
         }
      });
   }

   private void initNotesEditor()
   {
      getNotesField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Catch c = getCurrentCatch();
            if(null == c)
               return;
            c.setNotes(s.toString());
         }
      });
   }

   private void initWindSpeedEditor()
   {
      getWindSpeedField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Catch c = getCurrentCatch();
            if(null == c)
               return;

            Speed spd = null;
            Speed.Units units = Config.getDefaultWindSpeedUnits();
            if((null != c.getWind()) && (null != c.getWind().getSpeed()))
               units = c.getWind().getSpeed().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               spd = new Speed(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            c.setWindSpeed(spd);
         }
      });
   }

   private void initLengthEditor()
   {
      getLengthField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Catch c = getCurrentCatch();
            if(null == c)
               return;

            FishLength len = null;
            FishLength.Units units = Config.getDefaultFishLengthUnits();
            if(null != c.getLength())
               units = c.getLength().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               len = new FishLength(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            c.setLength(len);
         }
      });
   }

   private void initWeightEditor()
   {
      getWeightField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Catch c = getCurrentCatch();
            if(null == c)
               return;

            FishWeight w = null;
            FishWeight.Units units = Config.getDefaultFishWeightUnits();
            if(null != c.getWeight())
               units = c.getWeight().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               w = new FishWeight(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            c.setWeight(w);
         }
      });
   }

   private void initDepthEditor()
   {
      getDepthField().addTextChangedListener(new TextWatcher()
      {
         public void afterTextChanged(Editable s) {}
         public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

         public void onTextChanged(CharSequence s, int start, int before, int count)
         {
            Catch c = getCurrentCatch();
            if(null == c)
               return;

            WaterDepth d = null;
            WaterDepth.Units units = Config.getDefaultWaterDepthUnits();
            if(null != c.getDepth())
               units = c.getDepth().getUnits();
            try
            {
               float f = Float.parseFloat(s.toString());
               d = new WaterDepth(f, units);
            }
            catch(NumberFormatException ex) { /* ignore */}
            c.setDepth(d);
         }
      });
   }

   private void initSpeciesEditorItems()
   {
      // TODO - add most recent species (maybe last 5 from all trips?) plus a separator at top of list
      _speciesMap = new HashMap<>();
      initSpinnerItems(getSpeciesField(), Config.getAllSpecies(), _speciesMap);
   }

   private void initSpeciesEditor()
   {
      initSpeciesEditorItems();
      getSpeciesField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final String species = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(species))
            {
               _newItemInputField = null;
               int oldSelection = 0;
               String s = getCurrentCatch().getSpecies();
               if(null != s)
                  oldSelection = _speciesMap.get(s);
               openCreateItemDialog(
                  "Enter New Species",
                  getSpeciesField(), oldSelection,
                  new DialogInterface.OnClickListener()
                  {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i)
                     {
                        String newItem = getNewItemInputField().getText().toString();
                        getCurrentCatch().setSpecies(newItem);
                        initSpeciesEditorItems();
                        setSpeciesSelection(getCurrentCatch());
                     }
                  });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
                  c.setSpecies(species);
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initSpinnerItems(Spinner spinner, Collection<String> items, HashMap<String,Integer> positionMap)
   {
      List<String> list = new ArrayList<>();
      list.add(Config.BLANK_LABEL);
      list.add(Config.OTHER_LABEL);
      for(String s : items)
         list.add(s);
      positionMap.clear();
      for(int i = 0; i < list.size(); ++i)
         positionMap.put(list.get(i), i);
      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
            getContext(), R.layout.spinner_dropdown_item_centered, list);
//      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
//            getContext(), R.layout.support_simple_spinner_dropdown_item, list);
      spinner.setAdapter(spinnerArrayAdapter);
   }

   private void initFixedSpinnerItems(Spinner spinner, List<String> items)
   {
      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
            getContext(), R.layout.spinner_dropdown_item_centered, items);
//      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
//            getContext(), R.layout.support_simple_spinner_dropdown_item, items);
      spinner.setAdapter(spinnerArrayAdapter);
   }

   private void initLureEditor()
   {
      initLureEditorItems();
      getLureField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final Lure selectedItem = (Lure) parent.getItemAtPosition(pos);
            Catch c = getCurrentCatch();
            if(null != c)
               c.setLure(selectedItem);
            setLureComponentFields(selectedItem);
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });

   }

   private void initLureEditorItems()
      {
      // TODO - put the last 5 or 10 used lures at the top?

      List<Lure> allLures = Trip.getAllLures();
      if(allLures.isEmpty())
      {
         // TODO - remove this, hardcoded for now for testing
         allLures.add(new Lure("Blade", "Steel Shad", "Shad", "3in"));
         allLures.add(new Lure("Carolina (unweighted)", "", "", "", "Fluke (paddle-tail)", "White", "4in"));
         allLures.add(new Lure("Bucktail Jig", "Zoom", "White", "4in", "Fluke (split-tail)", "Red", "4in"));
      }

      java.util.List<Lure> sortedList = new java.util.ArrayList<>();
      sortedList.add(Lure.NULL_LURE);
      java.util.Map<String,Lure> sortMap = new java.util.TreeMap<>();
      for(Lure lure : allLures)
         sortMap.put(lure.toString(), lure);
      for(String k : sortMap.keySet())
         sortedList.add(sortMap.get(k));

      _lureMap = new HashMap<>();
      for(int i = 0; i < sortedList.size(); ++i)
         _lureMap.put(sortedList.get(i), i);
      ArrayAdapter<Lure> spinnerArrayAdapter = new ArrayAdapter<>(
            getContext(), R.layout.spinner_dropdown_item_centered, sortedList);
//      ArrayAdapter<Lure> spinnerArrayAdapter = new ArrayAdapter<>(
//            getContext(), R.layout.support_simple_spinner_dropdown_item, sortedList);
      getLureField().setAdapter(spinnerArrayAdapter);
   }

   private void initLureTypeEditorItems()
   {
      _lureTypeMap = new HashMap<>();
      initSpinnerItems(getLureTypeField(), Config.getAllLureTypes(), _lureTypeMap);
   }

   private void initLureTypeEditor()
   {
      initLureTypeEditorItems();
      getLureTypeField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final String selectedItem = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(selectedItem))
            {
               _newItemInputField = null;
               int oldSelection = 0;
               String s = getCurrentCatch().getLureType();
               if(null != s)
                  oldSelection = _lureTypeMap.get(s);
               openCreateItemDialog(
               "Enter New Lure Type",
                getLureTypeField(), oldSelection,
                new DialogInterface.OnClickListener()
               {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i)
                  {
                     String newItem = getNewItemInputField().getText().toString();
                     Catch c = getCurrentCatch();
                     if(null != c)
                     {
                        c.setLureType(newItem);
                        initLureTypeEditorItems();
                        initLureEditorItems();
                        setLureFields(c);
                     }
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
               {
                  c.setLureType(selectedItem);
                  initLureEditorItems();
                  setLureFields(c);
               }
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initLureBrandEditorItems()
   {
      _lureBrandMap = new HashMap<>();
      initSpinnerItems(getLureBrandField(), Config.getAllLureBrands(), _lureBrandMap);
   }

   private void initLureBrandEditor()
   {
      initLureBrandEditorItems();
      getLureBrandField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final String selectedItem = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(selectedItem))
            {
               _newItemInputField = null;
               int oldSelection = 0;
               String s = getCurrentCatch().getLureBrand();
               if(null != s)
                  oldSelection = _lureBrandMap.get(s);
               openCreateItemDialog(
             "Enter New Lure Brand",
                  getLureBrandField(), oldSelection,
                  new DialogInterface.OnClickListener()
               {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i)
                  {
                     Catch c = getCurrentCatch();
                     if(null != c)
                     {
                        String newItem = getNewItemInputField().getText().toString();
                        c.setLureBrand(newItem);
                        initLureBrandEditorItems();
                        initLureEditorItems();
                        setLureFields(c);
                        //setLureBrandField(getCurrentCatch().getLure());
                     }
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
               {
                  c.setLureBrand(selectedItem);
                  initLureEditorItems();
                  setLureFields(c);
               }
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initLureColorEditor()
   {
      initColorEditorItems();
      getLureColorField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final String selectedItem = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(selectedItem))
            {
               _newItemInputField = null;
               int oldSelection = 0;
               String s = getCurrentCatch().getLureColor();
               if(null != s)
                  oldSelection = _colorMap.get(s);

               openCreateItemDialog(
                     "Enter New Color",
                     getLureColorField(), oldSelection,
                     new DialogInterface.OnClickListener()
               {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i)
                  {
                     Catch c = getCurrentCatch();
                     if(null != c)
                     {
                        String newItem = getNewItemInputField().getText().toString();
                        c.setLureColor(newItem);
                        initColorEditorItems();
                        initLureEditorItems();
                        setLureFields(c);
                        // setLureColorField(getCurrentCatch().getLure());
                     }
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
               {
                  c.setLureColor(selectedItem);
                  initLureEditorItems();
                  setLureFields(c);
               }
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initLureSizeEditorItems()
   {
      _lureSizeMap = new HashMap<>();
      initSpinnerItems(getLureSizeField(), Config.getAllLureSizes(), _lureSizeMap);
   }

   private void initLureSizeEditor()
   {
      initLureSizeEditorItems();
      getLureSizeField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final String selectedItem = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(selectedItem))
            {
               _newItemInputField = null;
               int oldSelection = 0;
               String s = getCurrentCatch().getLureSize();
               if(null != s)
                  oldSelection = _lureSizeMap.get(s);

               openCreateItemDialog(
                     "Enter New Lure Size",
                     getLureSizeField(), oldSelection,
                     new DialogInterface.OnClickListener()
               {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i)
                  {
                     Catch c = getCurrentCatch();
                     if(null != c)
                     {
                        String newItem = getNewItemInputField().getText().toString();
                        c.setLureSize(newItem);
                        initLureSizeEditorItems();
                        initLureEditorItems();
                        setLureFields(c);
                        // setLureSizeField(getCurrentCatch().getLure());
                     }
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
               {
                  c.setLureSize(selectedItem);
                  initLureEditorItems();
                  setLureFields(c);
               }
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initTrailerEditorItems()
   {
      _trailerMap = new HashMap<>();
      initSpinnerItems(getTrailerField(), Config.getAllTrailerTypes(), _trailerMap);
   }

   private void initTrailerEditor()
   {
      initTrailerEditorItems();
      getTrailerField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final String selectedItem = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(selectedItem))
            {
               _newItemInputField = null;
               int oldSelection = 0;
               String s = getCurrentCatch().getTrailerType();
               if(null != s)
                  oldSelection = _trailerMap.get(s);

               openCreateItemDialog(
                     "Enter New Trailer",
                     getTrailerField(), oldSelection,
                     new DialogInterface.OnClickListener()
               {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i)
                  {
                     Catch c = getCurrentCatch();
                     if(null != c)
                     {
                        String newItem = getNewItemInputField().getText().toString();
                        c.setTrailerType(newItem);
                        initTrailerEditorItems();
                        // setTrailerField(getCurrentCatch().getLure());
                        initLureEditorItems();
                        setLureFields(c);
                     }
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
               {
                  c.setTrailerType(selectedItem);
                  initLureEditorItems();
                  setLureFields(c);
               }
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initColorEditorItems()
   {
      _colorMap = new HashMap<>();
      initSpinnerItems(getTrailerColorField(), Config.getAllLureColors(), _colorMap);
      initSpinnerItems(getLureColorField(), Config.getAllLureColors(), _colorMap);
   }

   private void initTrailerColorEditor()
   {
      initColorEditorItems();
      getTrailerColorField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final String selectedItem = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(selectedItem))
            {
               _newItemInputField = null;
               int oldSelection = 0;
               String s = getCurrentCatch().getTrailerColor();
               if(null != s)
                  oldSelection = _colorMap.get(s);

               openCreateItemDialog(
                     "Enter New Color",
                     getTrailerColorField(), oldSelection,
                     new DialogInterface.OnClickListener()
               {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i)
                  {
                     Catch c = getCurrentCatch();
                     if(null != c)
                     {
                        String newItem = getNewItemInputField().getText().toString();
                        c.setTrailerColor(newItem);
                        initColorEditorItems();
                        initLureEditorItems();
                        setLureFields(c);
                        // setTrailerColorField(getCurrentCatch().getLure());
                     }
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
               {
                  c.setTrailerColor(selectedItem);
                  initLureEditorItems();
                  setLureFields(c);
               }
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initTrailerSizeEditorItems()
   {
      _trailerSizeMap = new HashMap<>();
      initSpinnerItems(getTrailerSizeField(), Config.getAllTrailerSizes(), _trailerSizeMap);
   }

   private void initTrailerSizeEditor()
   {
      initTrailerSizeEditorItems();
      getTrailerSizeField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final String selectedItem = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(selectedItem))
            {
               _newItemInputField = null;
               int oldSelection = 0;
               String s = getCurrentCatch().getTrailerSize();
               if(null != s)
                  oldSelection = _trailerSizeMap.get(s);

               openCreateItemDialog(
                     "Enter New Trailer Size",
                     getTrailerSizeField(), oldSelection,
                     new DialogInterface.OnClickListener()
               {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i)
                  {
                     Catch c = getCurrentCatch();
                     if(null != c)
                     {
                        String newItem = getNewItemInputField().getText().toString();
                        c.setTrailerSize(newItem);
                        initTrailerSizeEditorItems();
                        initLureEditorItems();
                        setLureFields(c);
                        // setTrailerSizeField(getCurrentCatch().getLure());
                     }
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
               {
                  c.setTrailerSize(selectedItem);
                  initLureEditorItems();
                  setLureFields(c);
               }
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initWaterClarityEditor()
   {
      List<String> values = new ArrayList<>();
      for(Config.Clarity c : Config.Clarity.values())
         values.add(c.toString());
      initFixedSpinnerItems(getWaterClarityField(), values);
      getWaterClarityField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            String selectedItem = (String) parent.getItemAtPosition(pos);
            Catch c = getCurrentCatch();
            if(null != c)
            {
               if(selectedItem.isEmpty())
                  c.setWaterClarity(null);
               else
                  c.setWaterClarity(Config.Clarity.valueOf(selectedItem));
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initPrecipEditor()
   {
      List<String> values = new ArrayList<>();
      for(Config.Precipitation p : Config.Precipitation.values())
         values.add(p.toString());
      initFixedSpinnerItems(getPrecipField(), values);

      getPrecipField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            String selectedItem = (String) parent.getItemAtPosition(pos);
            Catch c = getCurrentCatch();
            if(null != c)
            {
               if(selectedItem.isEmpty())
                  c.setPrecip(null);
               else
                  c.setPrecip(Config.Precipitation.valueOf(selectedItem));
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initWindDirectionEditor()
   {
      List<String> values = new ArrayList<>();
      for(Config.Direction d : Config.Direction.values())
         values.add(d.toString());
      initFixedSpinnerItems(getWindDirectionField(), values);

      getWindDirectionField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            String selectedItem = (String) parent.getItemAtPosition(pos);
            Catch c = getCurrentCatch();
            if(null != c)
            {
               if(selectedItem.isEmpty())
                  c.setWindDirection(null);
               else
                  c.setWindDirection(Config.Direction.valueOf(selectedItem));
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initWindStrengthEditor()
   {
      List<String> values = new ArrayList<>();
      for(Config.WindStrength s : Config.WindStrength.values())
         values.add(s.toString());
      initFixedSpinnerItems(getWindStrengthField(), values);

      getWindStrengthField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            String selectedItem = (String) parent.getItemAtPosition(pos);
            Catch c = getCurrentCatch();
            if(null != c)
            {
               if(selectedItem.isEmpty())
                  c.setWindStrength(null);
               else
                  c.setWindStrength(Config.WindStrength.valueOf(selectedItem));
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initStructureEditorItems()
   {
      _structureMap = new HashMap<>();
      initSpinnerItems(getStructureField(), Config.getAllStructures(), _structureMap);
   }

   private void initStructureEditor()
   {
      initStructureEditorItems();
      getStructureField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final String selectedItem = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(selectedItem))
            {
               _newItemInputField = null;
               int oldSelection = 0;
               String s = getCurrentCatch().getStructure();
               if(null != s)
                  oldSelection = _structureMap.get(s);

               openCreateItemDialog(
                     "Enter New Structure",
                     getStructureField(), oldSelection,
                     new DialogInterface.OnClickListener()
               {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i)
                  {
                     String newItem = getNewItemInputField().getText().toString();
                     getCurrentCatch().setStructure(newItem);
                     initStructureEditorItems();
                     setStructureSelection(getCurrentCatch());
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
                  c.setStructure(selectedItem);
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }

   private void initCoverEditorItems()
   {
      _coverMap = new HashMap<>();
      initSpinnerItems(getCoverField(), Config.getAllCovers(), _coverMap);
   }

   private void initCoverEditor()
   {
      initCoverEditorItems();
      getCoverField().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
         {
            final String selectedItem = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(selectedItem))
            {
               _newItemInputField = null;
               int oldSelection = 0;
               String s = getCurrentCatch().getCover();
               if(null != s)
                  oldSelection = _coverMap.get(s);

               openCreateItemDialog(
                     "Enter New Cover",
                     getCoverField(), oldSelection,
                     new DialogInterface.OnClickListener()
               {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i)
                  {
                     String newItem = getNewItemInputField().getText().toString();
                     getCurrentCatch().setCover(newItem);
                     initCoverEditorItems();
                     setCoverSelection(getCurrentCatch());
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
                  c.setCover(selectedItem);
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
      });
   }


   private EditText getNewItemInputField()
   {
      if(null == _newItemInputField)
         _newItemInputField = new EditText(getContext());
      return _newItemInputField;
   }

   private EditText initNewItemInputField(android.content.DialogInterface.OnClickListener listener)
   {
      EditText inputField = getNewItemInputField();
      inputField.setMaxLines(1);
      inputField.setInputType(EditorInfo.TYPE_CLASS_TEXT);
         inputField.setOnKeyListener(new View.OnKeyListener()
         {
            @Override
            public boolean onKey(View view, int code, KeyEvent e)
            {
               if((e.getAction() == KeyEvent.ACTION_DOWN) && (code == KeyEvent.KEYCODE_ENTER))
               {
                  if(null != _createItemDialog)
                  {
                     listener.onClick(_createItemDialog, 0);
                     _createItemDialog.dismiss();
                     _createItemDialog = null;
                  }
                  return true;
               }
               return false;
            }
         });
      return inputField;
   }

   private void openCreateItemDialog(
      String title, Spinner spinner, int oldSelection,
      DialogInterface.OnClickListener okListener)
   {
      DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
       {
         @Override
         public void onClick(DialogInterface dialogInterface, int i)
         {
            spinner.setSelection(oldSelection);
         }
      };

      _createItemDialog = new AlertDialog.Builder(getContext())
            .setTitle(title)
            .setView(initNewItemInputField(okListener))
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, okListener)
            .setNegativeButton(android.R.string.no, cancelListener)
            .create();
      _createItemDialog.show();
   }

   private void setTripLocationSelection(Trip t)
   {
      String loc = null;
      if(null != t)
         loc = t.getLocation();
      if((null == loc) || loc.isEmpty())
      {
         getTripLocationField().setSelection(0); // NOTE:  0 is blank element added first to the list
      }
      else
      {
         int pos = _tripLocationMap.get(loc);
         getTripLocationField().setSelection(pos);
      }
   }

   private void setTripTransportSelection(Trip t)
   {
      String transport = null;
      if(null != t)
         transport = t.getTransport();
      if((null == transport) || transport.isEmpty())
      {
         getTripTransportField().setSelection(0); // NOTE:  0 is blank element added first to the list
      }
      else
      {
         int pos = _tripTransportMap.get(transport);
         getTripTransportField().setSelection(pos);
      }
   }

   private void setTripStartField(Trip t)
   {
      if((null == t) || (null == t.getStartTime()))
         getTripStartField().setText("");
      else
         getTripStartField().setText(Config.formatTimestamp(t.getStartTime()));
   }

   private void setTripEndField(Trip t)
   {
      if((null == t) || (null == t.getLocation()))
         getTripEndField().setText("");
      else
         getTripEndField().setText(Config.formatTimestamp(t.getEndTime()));
   }

   private void setTripDistanceField(Trip t)
   {
      if((null == t) || (null == t.getDistanceTraveled()))
         getTripDistanceField().setText("");
      else
         getTripDistanceField().setText(t.getDistanceTraveled().valueString());
   }

   private void setTripAirTempStartField(Trip t)
   {
      if((null == t) || (null == t.getAirTempStart()))
         getTripAirTempStartField().setText("");
      else
         getTripAirTempStartField().setText(t.getAirTempStart().valueString());
   }

   private void setTripWaterTempField(Trip t)
   {
      if((null == t) || (null == t.getWaterTemp()))
         getTripWaterTempField().setText("");
      else
         getTripWaterTempField().setText(t.getWaterTemp().valueString());
   }

   private void setTripWaterLevelField(Trip t)
   {
      if((null == t) || (null == t.getWaterLevel()))
         getTripWaterLevelField().setText("");
      else
         getTripWaterLevelField().setText(t.getWaterLevel());
   }

   private void setTripSecchiField(Trip t)
   {
      if((null == t) || (null == t.getSecchi()))
         getTripSecchiField().setText("");
      else
         getTripSecchiField().setText(t.getSecchi().valueString());
   }

   private void setTripNotesField(Trip t)
   {
      if((null == t) || (null == t.getNotes()))
         getTripNotesField().setText("");
      else
         getTripNotesField().setText(t.getNotes());
   }

   private void setTripWindStartFields(Trip t)
   {
      if((null != t) && (null != t.getWindRange()))
      {
         Speed spd = t.getWindRange().getSpeedStart();
         if(null == spd)
            getTripStartWindSpeedField().setText("");
         else
            getTripStartWindSpeedField().setText(spd.valueString());

         Config.Direction dir = t.getWindRange().getDirectionStart();
         if(null == dir)
            getTripStartWindDirField().setSelection(0);
         else
            getTripStartWindDirField().setSelection(dir.ordinal());
      }
      else
      {
         getTripStartWindSpeedField().setText("");
         getTripStartWindDirField().setSelection(0);
      }
   }

   private void setTripWindEndFields(Trip t)
   {
      if((null != t) && (null != t.getWindRange()))
      {
         Speed spd = t.getWindRange().getSpeedEnd();
         if(null == spd)
            getTripEndWindSpeedField().setText("");
         else
            getTripEndWindSpeedField().setText(spd.valueString());

         Config.Direction dir = t.getWindRange().getDirectionEnd();
         if(null == dir)
            getTripEndWindDirField().setSelection(0);
         else
            getTripEndWindDirField().setSelection(dir.ordinal());
      }
      else
      {
         getTripStartWindSpeedField().setText("");
         getTripStartWindDirField().setSelection(0);
      }
   }

   private void setTripWaterClaritySelection(Trip t)
   {
      Config.Clarity c = null;
      if(null != t)
         c = t.getWaterClarity();
      if(null == c)
         getTripWaterClarityField().setSelection(0); // NOTE:  0 is blank element added first to the list
      else
         getTripWaterClarityField().setSelection(c.ordinal());
   }

   private void setTripWindStrengthSelection(Trip t)
   {
      Config.WindStrength s = null;
      if((null != t) && (null != t.getWindRange()))
         s = t.getWindRange().getStrengthStart();
      if(null == s)
         getTripStartWindStrengthField().setSelection(0); // NOTE:  0 is blank element added first to the list
      else
         getTripStartWindStrengthField().setSelection(s.ordinal());
   }

   private void setTripPrecipSelection(Trip t)
   {
      Config.Precipitation p = null;
      if(null != t)
         p = t.getPrecip();
      if(null == p)
         getTripPrecipField().setSelection(0); // NOTE:  0 is blank element added first to the list
      else
         getTripPrecipField().setSelection(p.ordinal());
   }

   private void setTripAirTempEndField(Trip t)
   {
      if((null == t) || (null == t.getAirTempEnd()))
         getTripAirTempEndField().setText("");
      else
         getTripAirTempEndField().setText(t.getAirTempEnd().valueString());
   }

   private void setWhenField(Catch c)
   {
      if((null == c) || (null == c.getTimestamp()))
         getWhenField().setText("");
      else
         getWhenField().setText(Config.formatTimestamp(c.getTimestamp()));
   }

   private void setGpsField(Catch c)
   {
      if((null == c) || (null == c.getGpsLocation()))
         getGpsField().setText("");
      else
         getGpsField().setText(c.getGpsLocation().getLat() + ", " + c.getGpsLocation().getLon());
   }


   private void setSpeciesSelection(Catch c)
   {
      String species = null;
      if(null != c)
         species = c.getSpecies();
      if((null == species) || species.isEmpty())
      {
         getSpeciesField().setSelection(0); // NOTE:  0 is blank element added first to the list
      }
      else
      {
         int pos = _speciesMap.get(species);
         getSpeciesField().setSelection(pos);
      }
   }

   private void setLengthField(Catch c)
   {
      if((null == c) || (null == c.getLength()))
      {
         getLengthField().setText("");
         setLengthUnitsLabel();
      }
      else
      {
         getLengthField().setText(c.getLength().valueString());
         setLengthUnitsLabel(c.getLength().getUnits());
      }
   }

   private void setWeightField(Catch c)
   {
      if((null == c) || (null == c.getWeight()))
      {
         getWeightField().setText("");
         setWeightUnitsLabel();
      }
      else
      {
         getWeightField().setText(c.getWeight().valueString());
         setWeightUnitsLabel(c.getWeight().getUnits());
      }
   }

   private void setLureFields(Catch c)
   {
      Lure lure = null;
      if(c != null)
         lure = c.getLure();
      setLureFields(lure);
   }

   private void setLureFields(Lure lure)
   {
      int curSel = getLureField().getSelectedItemPosition();
      int newSel = -1;
      if((null == lure) || (Lure.NULL_LURE == lure))
         newSel = 0;
      else
         newSel = _lureMap.get(lure);
      if(newSel != curSel)
         getLureField().setSelection(newSel);
      setLureComponentFields(lure);
   }

   private void setLureTypeField(Lure lure)
   {
      int curSel = getLureTypeField().getSelectedItemPosition();
      int newSel = -1;
      if((null == lure) || (Lure.NULL_LURE == lure) || (null == lure.getType()))
         newSel = 0;
      else
         newSel = _lureTypeMap.get(lure.getType());
      if(newSel != curSel)
         getLureTypeField().setSelection(newSel);
   }

   private void setLureBrandField(Lure lure)
   {
      int curSel = getLureBrandField().getSelectedItemPosition();
      int newSel = -1;
      if((null == lure) || (Lure.NULL_LURE == lure) || (null == lure.getBrand()))
         newSel = 0;
      else
         newSel = _lureBrandMap.get(lure.getBrand());
      if(newSel != curSel)
         getLureBrandField().setSelection(newSel);
   }

   private void setLureColorField(Lure lure)
   {
      int curSel = getLureColorField().getSelectedItemPosition();
      int newSel = -1;
      if((null == lure) || (Lure.NULL_LURE == lure) || (null == lure.getColor()))
         newSel = 0;
      else
         newSel = _colorMap.get(lure.getColor());
      if(newSel != curSel)
         getLureColorField().setSelection(newSel);
   }

   private void setLureSizeField(Lure lure)
   {
      int curSel = getLureSizeField().getSelectedItemPosition();
      int newSel = -1;
      if((null == lure) || (Lure.NULL_LURE == lure) || (null == lure.getSize()))
         newSel = 0;
      else
         newSel = _lureSizeMap.get(lure.getSize());
      if(newSel != curSel)
         getLureSizeField().setSelection(newSel);
   }

   private void setTrailerField(Lure lure)
   {
      int curSel = getTrailerField().getSelectedItemPosition();
      int newSel = -1;
      if((null == lure) || (Lure.NULL_LURE == lure) || (null == lure.getTrailer()))
         newSel = 0;
      else
         newSel = _trailerMap.get(lure.getTrailer());
      if(newSel != curSel)
         getTrailerField().setSelection(newSel);
   }

   private void setTrailerColorField(Lure lure)
   {
      int curSel = getTrailerColorField().getSelectedItemPosition();
      int newSel = -1;
      if((null == lure) || (Lure.NULL_LURE == lure) || (null == lure.getTrailerColor()))
         newSel = 0;
      else
         newSel = _colorMap.get(lure.getTrailerColor());
      if(newSel != curSel)
         getTrailerColorField().setSelection(newSel);
   }

   private void setTrailerSizeField(Lure lure)
   {
      int curSel = getTrailerSizeField().getSelectedItemPosition();
      int newSel = -1;
      if((null == lure) || (Lure.NULL_LURE == lure) || (null == lure.getTrailerSize()))
         newSel = 0;
      else
         newSel = _trailerSizeMap.get(lure.getTrailerSize());
      if(newSel != curSel)
         getTrailerSizeField().setSelection(newSel);
   }

   private void setLureComponentFields(Lure lure)
   {
      // this sets everything but the main Lure spinner selection (all the lure component fields)
      setLureTypeField(lure);
      setLureBrandField(lure);
      setLureColorField(lure);
      setLureSizeField(lure);
      setTrailerField(lure);
      setTrailerColorField(lure);
      setTrailerSizeField(lure);
   }

   private void setDepthField(Catch c)
   {
      if((null == c) || (null == c.getDepth()))
      {
         getDepthField().setText("");
         setDepthUnitsLabel();
      } else
      {
         getDepthField().setText(c.getDepth().valueString());
         setDepthUnitsLabel(c.getDepth().getUnits());
      }
   }

   private void setAirTempField(Catch c)
   {
      if((null == c) || (null == c.getAirTemp()))
      {
         getAirTempField().setText("");
         setAirTempUnitsLabel();
      }
      else
      {
         getAirTempField().setText(c.getAirTemp().valueString());
         setAirTempUnitsLabel(c.getAirTemp().getUnits());
      }
   }

   private void setWaterTempField(Catch c)
   {
      if((null == c) || (null == c.getWaterTemp()))
      {
         getWaterTempField().setText("");
         setWaterTempUnitsLabel();
      }
      else
      {
         getWaterTempField().setText(c.getWaterTemp().valueString());
         setWaterTempUnitsLabel(c.getWaterTemp().getUnits());
      }
   }

   void setWaterClaritySelection(Catch c)
   {
      Config.Clarity clarity = null;
      if(null != c)
         clarity = c.getWaterClarity();
      if(null == clarity)
         getWaterClarityField().setSelection(0);
      else
         getWaterClarityField().setSelection(clarity.ordinal());
   }

   private void setSecchiField(Catch c)
   {
      if((null == c) || (null == c.getSecchi()))
      {
         getSecchiField().setText("");
         setSecchiUnitsLabel();
      }
      else
      {
         getSecchiField().setText(c.getSecchi().valueString());
         setSecchiUnitsLabel(c.getSecchi().getUnits());
      }
   }

   void setStructureSelection(Catch c)
   {
      String s = null;
      if(null != c)
         s = c.getStructure();
      if(null == s)
      {
         getStructureField().setSelection(0); // NOTE:  0 is blank element added first to the list
      }
      else
      {
         int pos = _structureMap.get(s);
         getStructureField().setSelection(pos);
      }
   }

   void setCoverSelection(Catch c)
   {
      String s = null;
      if(null != c)
         s = c.getCover();
      if(null == s)
      {
         getCoverField().setSelection(0); // NOTE:  0 is blank element added first to the list
      }
      else
      {
         int pos = _coverMap.get(s);
         getCoverField().setSelection(pos);
      }
   }

   void setPrecipSelection(Catch c)
   {
      Config.Precipitation precip = null;
      if(null != c)
         precip = c.getPrecip();
      if(null == precip)
         getPrecipField().setSelection(0);
      else
         getPrecipField().setSelection(precip.ordinal());
   }


   private void setWindSpeedField(Catch c)
   {
      if((null == c) || (null == c.getWindSpeed()))
      {
         getWindSpeedField().setText("");
         setWindSpeedUnitsLabel();
      }
      else
      {
         getWindSpeedField().setText(c.getWindSpeed().valueString());
         setWindSpeedUnitsLabel(c.getWindSpeed().getUnits());
      }
   }

   void setWindDirectionSelection(Catch c)
   {
      Config.Direction dir = null;
      if(null != c)
         dir = c.getWindDirection();
      if(null == dir)
         getWindDirectionField().setSelection(0);
      else
         getWindDirectionField().setSelection(dir.ordinal());
   }

   void setWindStrengthSelection(Catch c)
   {
      Config.WindStrength str = null;
      if(null != c)
         str = c.getWindStrength();
      if(null == str)
         getWindStrengthField().setSelection(0);
      else
         getWindStrengthField().setSelection(str.ordinal());
   }

   private void setNotesField(Catch c)
   {
      if((null == c) || (null == c.getNotes()))
         getNotesField().setText("");
      else
         getNotesField().setText(c.getNotes());
   }

   private Catch getCurrentCatch()
   {
      if(null == getCurrentTrip())
         return null;
      else
         return getCurrentTrip().getLastCatch();
   }

   private Context getContext()
   {
      return _binding.getRoot().getContext();
   }

   private void updateMenuState()
   {
/*
      boolean activeTrip = (null != _trip) && !_trip.isEnded();
      ((MenuItem) findViewById(R.id.action_start_trip)).setEnabled(!activeTrip);
      ((MenuItem) findViewById(R.id.action_end_trip)).setEnabled(activeTrip);
 */
      // FIXME - this isn't working to find menu items....
   }

   private void showMessage(String msg)
   {
      Log.i("APP MESSAGE", msg);
      // FIXME - how do i get the view???
      //Snackbar.make(null, msg, Snackbar.LENGTH_LONG)
      //        .setAction("Action", null).show();
   }

   // trip fields
   private EditText getTripInfoField() { return (EditText) findViewById(R.id.tripInfoField); }
   private Spinner getTripLocationField() { return (Spinner) findViewById(R.id.tripLocationField); }
   private EditText getTripStartField() { return (EditText) findViewById(R.id.tripStartField); }
   private EditText getTripEndField() { return (EditText) findViewById(R.id.tripEndField); }
   private Spinner getTripTransportField() { return (Spinner) findViewById(R.id.tripTransportField); }
   private EditText getTripDistanceField() { return (EditText) findViewById(R.id.tripDistanceField); }
   private TextView getTripDistanceUnitsField() { return (TextView) findViewById(R.id.tripDistanceUnitsLabel); }
   private EditText getTripAirTempStartField() { return (EditText) findViewById(R.id.tripAirTempStartField); }
   private EditText getTripAirTempEndField() { return (EditText) findViewById(R.id.tripAirTempEndField); }
   private TextView getTripAirTempUnitsField() { return (TextView) findViewById(R.id.tripAirTempUnitsLabel); }
   private EditText getTripWaterTempField() { return (EditText) findViewById(R.id.tripWaterTempField); }
   private TextView getTripWaterTempUnitsField() { return (TextView) findViewById(R.id.tripWaterTempUnitsLabel); }
   private EditText getTripWaterLevelField() { return (EditText) findViewById(R.id.tripWaterLevelField); }
   private Spinner getTripWaterClarityField() { return (Spinner) findViewById(R.id.tripWaterClarityField); }
   private EditText getTripSecchiField() { return (EditText) findViewById(R.id.tripWaterSecchiField); }
   private TextView getTripSecchiUnitsField() { return (TextView) findViewById(R.id.tripSecchiUnitsLabel); }
   private Spinner getTripStartWindDirField() { return (Spinner) findViewById(R.id.tripStartWindDirField); }
   private EditText getTripStartWindSpeedField() { return (EditText) findViewById(R.id.tripStartWindSpeedField); }
   private Spinner getTripEndWindDirField() { return (Spinner) findViewById(R.id.tripEndWindDirField); }
   private EditText getTripEndWindSpeedField() { return (EditText) findViewById(R.id.tripEndWindSpeedField); }
   private TextView getTripEndWindSpeedUnits() { return (TextView) findViewById(R.id.tripEndWindSpeedUnitsLabel); }
   private TextView getTripStartWindSpeedUnits() { return (TextView) findViewById(R.id.tripStartWindSpeedUnitsLabel); }
   private Spinner getTripStartWindStrengthField() { return (Spinner) findViewById(R.id.tripStartWindStrengthField); }
   private Spinner getTripEndWindStrengthField() { return (Spinner) findViewById(R.id.tripEndWindStrengthField); }
   private Spinner getTripPrecipField() { return (Spinner) findViewById(R.id.tripPrecipField); }
   private EditText getTripNotesField() { return (EditText) findViewById(R.id.tripNotesField); }

   // catch fields
   private EditText getWhenField() { return (EditText) findViewById(R.id.whenField); }
   private EditText getGpsField() { return (EditText) findViewById(R.id.gpsField); }
   private Spinner getSpeciesField() { return (Spinner) findViewById(R.id.speciesField); }
   private EditText getLengthField() { return (EditText) findViewById(R.id.lengthField); }
   private EditText getLengthLabelField() { return (EditText) findViewById(R.id.lengthLabel); }
   private EditText getWeightField() { return (EditText) findViewById(R.id.weightField); }
   private EditText getWeightLabelField() { return (EditText) findViewById(R.id.weightLabel); }
   private Spinner getLureField() { return (Spinner) findViewById(R.id.lureField); }
   private TextView getLureTypeLabelField() { return (TextView) findViewById(R.id.lureTypeLabel); }
   private TextView getLureBrandLabelField() { return (TextView) findViewById(R.id.lureBrandLabel); }
   private TextView getLureColorLabelField() { return (TextView) findViewById(R.id.lureColorLabel); }
   private TextView getLureSizeLabelField() { return (TextView) findViewById(R.id.lureSizeLabel); }
   private TextView getTrailerLabelField() { return (TextView) findViewById(R.id.trailerLabel); }
   private TextView getTrailerColorLabelField() { return (TextView) findViewById(R.id.trailerColorLabel); }
   private TextView getTrailerSizeLabelField() { return (TextView) findViewById(R.id.trailerSizeLabel); }
   private TextView getWaterClarityLabelField() { return (TextView) findViewById(R.id.waterClarityLabel); }
   private TextView getPrecipLabelField() { return (TextView) findViewById(R.id.precipLabel); }
   private TextView getCoverLabelField() { return (TextView) findViewById(R.id.coverLabel); }
   private TextView getStructureLabelField() { return (TextView) findViewById(R.id.structureLabel); }
   private TextView getWindDirectionLabelField() { return (TextView) findViewById(R.id.windDirectionLabel); }
   private TextView getWindStrengthLabelField() { return (TextView) findViewById(R.id.windStrengthLabel); }
   private Spinner getLureTypeField() { return (Spinner) findViewById(R.id.lureTypeField); }
   private Spinner getLureBrandField() { return (Spinner) findViewById(R.id.lureBrandField); }
   private Spinner getLureColorField() { return (Spinner) findViewById(R.id.lureColorField); }
   private Spinner getLureSizeField() { return (Spinner) findViewById(R.id.lureSizeField); }
   private Spinner getTrailerField() { return (Spinner) findViewById(R.id.trailerField); }
   private Spinner getTrailerColorField() { return (Spinner) findViewById(R.id.trailerColorField); }
   private Spinner getTrailerSizeField() { return (Spinner) findViewById(R.id.trailerSizeField); }
   private EditText getDepthField() { return (EditText) findViewById(R.id.depthField); }
   private TextView getDepthLabelField() { return (TextView) findViewById(R.id.depthLabel); }
   private EditText getAirTempField() { return (EditText) findViewById(R.id.airTempField); }
   private TextView getAirTempLabelField() { return (TextView) findViewById(R.id.airTempLabel); }
   private EditText getWaterTempField() { return (EditText) findViewById(R.id.waterTempField); }
   private TextView getWaterTempLabelField() { return (TextView) findViewById(R.id.waterTempLabel); }
   private Spinner getWaterClarityField() { return (Spinner) findViewById(R.id.waterClarityField); }
   private EditText getSecchiField() { return (EditText) findViewById(R.id.secchiField); }
   private TextView getSecchiLabelField() { return (TextView) findViewById(R.id.secchiLabel); }
   private Spinner getStructureField() { return (Spinner) findViewById(R.id.structureField); }
   private Spinner getCoverField() { return (Spinner) findViewById(R.id.coverField); }
   private Spinner getPrecipField() { return (Spinner) findViewById(R.id.precipField); }
   private EditText getWindSpeedField() { return (EditText) findViewById(R.id.windSpeedField); }
   private TextView getWindSpeedLabelField() { return (TextView) findViewById(R.id.windSpeedLabel); }
   private Spinner getWindDirectionField() { return (Spinner) findViewById(R.id.windDirectionField); }
   private Spinner getWindStrengthField() { return (Spinner) findViewById(R.id.windStrengthField); }
   private EditText getNotesField() { return (EditText) findViewById(R.id.notesField); }

}

