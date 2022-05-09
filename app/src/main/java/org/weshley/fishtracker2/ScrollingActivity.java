package org.weshley.fishtracker2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.core.app.ActivityCompat;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.snackbar.Snackbar;
import org.weshley.fishtracker2.databinding.ActivityScrollingBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ScrollingActivity
   extends AppCompatActivity
{
   private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;

   private List<Trip> _trips = new ArrayList<>();
   private HashMap<String,Integer> _speciesMap;
   private HashMap<String,Integer> _lureMap;
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

   private ActivityScrollingBinding binding;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      binding = ActivityScrollingBinding.inflate(getLayoutInflater());
      setContentView(binding.getRoot());

      Toolbar toolbar = binding.toolbar;
      setSupportActionBar(toolbar);
      CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
      toolBarLayout.setTitle(getTitle());

      FloatingActionButton fab = binding.fab;
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
         return true;
      }
      else if(id == R.id.action_end_trip)
      {
         endTrip();
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
         Trip t = Trip.createNewTrip();
         _trips.add(t);
         t.setLocation("Lake Hartwell");
         // FIXME - prompt for all trip data (location, transport, etc), defaulting to last trip values
         updateTripInfo();
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
      enableCatchControls(false);
      enableTripControls(false);
      updateTripInfo();
      updateMenuState();
   }

   private void updateTripInfo()
   {
      Trip t = getCurrentTrip();
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
      updateTripInfo();
   }

   private void updateCatchFields(Catch c)
   {
      if(null == c)
      {
         clearCatchFields();
         return;
      }

      setDateField(c);
      setTimeField(c);
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
      getTripInfoField().setText("");
      getDateField().setText("");
      getTimeField().setText("");
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
   }

   private void initUnitsLabels()
   {
      getLengthUnitsField().setText(Config.getDefaultFishLengthUnits().toString());
      getWeightUnitsField().setText(Config.getDefaultFishLengthUnits().toString());
      getDepthUnitsField().setText(Config.getDefaultWaterDepthUnits().toString());
      getAirTempUnitsField().setText(Config.getDefaultTempUnits().toString());
      getWaterTempUnitsField().setText(Config.getDefaultTempUnits().toString());
      getSecchiUnitsField().setText(Config.getDefaultWaterDepthUnits().toString());
      getWindSpeedUnitsField().setText(Config.getDefaultWindSpeedUnits().toString());
   }

   private void initEditors()
   {
      initSpeciesEditor();
      initLureEditor();
      initLureTypeEditor();
      initLureBrandEditor();
      initLureColorEditor();
      initLureSizeEditor();
      initTrailerEditor();
      initTrailerColorEditor();
      initTrailerSizeEditor();
      initWaterClarityEditor();
      initStructureEditor();
      initCoverEditor();
      initPrecipEditor();
      initWindDirectionEditor();
      initWindStrengthEditor();
   }

   private void enableCatchControls(boolean enabled)
   {
      // NOTE:  tripInfo, date, time, and gps fields are always not editable

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
      // TODO - add controls here when trip info is added to UI
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

   public void initLureEditor()
   {
      // TODO - figure out how to populate this.  maybe with the last 5 or 10 used lures?
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
            getContext(), R.layout.support_simple_spinner_dropdown_item, list);
      spinner.setAdapter(spinnerArrayAdapter);
   }

   private void initFixedSpinnerItems(Spinner spinner, List<String> items)
   {
      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
            getContext(), R.layout.support_simple_spinner_dropdown_item, items);
      spinner.setAdapter(spinnerArrayAdapter);
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
                     getCurrentCatch().setLureType(newItem);
                     initLureTypeEditorItems();
                     setLureFields(getCurrentCatch());
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
                  c.setLureType(selectedItem);
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
                     String newItem = getNewItemInputField().getText().toString();
                     getCurrentCatch().setLureBrand(newItem);
                     initLureBrandEditorItems();
                     setLureFields(getCurrentCatch());
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
                  c.setLureBrand(selectedItem);
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
                     String newItem = getNewItemInputField().getText().toString();
                     getCurrentCatch().setLureColor(newItem);
                     initColorEditorItems();
                     setLureFields(getCurrentCatch());
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
                  c.setLureColor(selectedItem);
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
                     String newItem = getNewItemInputField().getText().toString();
                     getCurrentCatch().setLureSize(newItem);
                     initLureSizeEditorItems();
                     setLureFields(getCurrentCatch());
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
                  c.setLureSize(selectedItem);
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
                     String newItem = getNewItemInputField().getText().toString();
                     getCurrentCatch().setTrailerType(newItem);
                     initTrailerEditorItems();
                     setLureFields(getCurrentCatch());
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
                  c.setTrailerType(selectedItem);
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
                     String newItem = getNewItemInputField().getText().toString();
                     getCurrentCatch().setTrailerColor(newItem);
                     initColorEditorItems();
                     setLureFields(getCurrentCatch());
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
                  c.setTrailerColor(selectedItem);
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
                     String newItem = getNewItemInputField().getText().toString();
                     getCurrentCatch().setTrailerSize(newItem);
                     initTrailerSizeEditorItems();
                     setLureFields(getCurrentCatch());
                  }
               });
            }
            else
            {
               Catch c = getCurrentCatch();
               if(null != c)
                  c.setTrailerSize(selectedItem);
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
               c.setWaterClarity(Config.Clarity.valueOf(selectedItem));
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
               c.setPrecip(Config.Precipitation.valueOf(selectedItem));
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
               c.setWindDirection(Config.Direction.valueOf(selectedItem));
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
               c.setWindStrength(Config.WindStrength.valueOf(selectedItem));
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

   private void setDateField(Catch c)
   {
      if((null == c) || (null == c.getTimestamp()))
         getDateField().setText("");
      else
         getDateField().setText(Config.formatDate(c.getTimestamp()));
   }

   private void setTimeField(Catch c)
   {
      if((null == c) || (null == c.getTimestamp()))
         getTimeField().setText("");
      else
         getTimeField().setText(Config.formatTime(c.getTimestamp()));
   }

   private void setGpsField(Catch c)
   {
      if((null == c) || (null == c.getGpsLocation()))
         getGpsField().setText("");
      else
         getGpsField().setText(c.getGpsLocation().getLat() + ", " + c.getGpsLocation().getLon());
   }


   void setSpeciesSelection(Catch c)
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
         getLengthUnitsField().setText(Config.getDefaultFishLengthUnits().toString());
      }
      else
      {
         getLengthField().setText(c.getLength().valueString());
         getLengthUnitsField().setText(c.getLength().getUnits().toString());
      }
   }

   private void setWeightField(Catch c)
   {
      if((null == c) || (null == c.getWeight()))
      {
         getWeightField().setText("");
         getWeightUnitsField().setText(Config.getDefaultFishWeightUnits().toString());
      }
      else
      {
         getWeightField().setText(c.getWeight().valueString());
         getWeightUnitsField().setText(c.getWeight().getUnits().toString());
      }
   }

   void setLureFields(Catch c)
   {
      Lure lure = null;
      if(null != c)
         lure = c.getLure();
      if((null == lure))
      {
         // NOTE:  0 is blank element added first to the list
         getLureField().setSelection(0);
         getLureTypeField().setSelection(0);
         getLureBrandField().setSelection(0);
         getLureColorField().setSelection(0);
         getLureSizeField().setSelection(0);
         getTrailerField().setSelection(0);
         getTrailerColorField().setSelection(0);
         getTrailerSizeField().setSelection(0);
      }
      else
      {
/*
TODO - lure UI not done yet....
         if(null != lure.description() && _lureMap.containsKey(lure.description()))
            getLureField().setSelection(_lureMap.get(lure.description()));
 */
         if(null != lure.getType())
            getLureTypeField().setSelection(_lureTypeMap.get(lure.getType()));

         if(null != lure.getBrand())
            getLureBrandField().setSelection(_lureBrandMap.get(lure.getBrand()));

         if(null != lure.getColor())
            getLureColorField().setSelection(_colorMap.get(lure.getColor()));

         if(null != lure.getSize())
            getLureSizeField().setSelection(_lureSizeMap.get(lure.getSize()));

         if(null != lure.getTrailer())
            getTrailerField().setSelection(_trailerMap.get(lure.getTrailer()));

         if(null != lure.getTrailerColor())
            getTrailerColorField().setSelection(_colorMap.get(lure.getTrailerColor()));

         if(null != lure.getTrailerSize())
            getTrailerSizeField().setSelection(_trailerSizeMap.get(lure.getTrailerSize()));
       }
   }

   private void setDepthField(Catch c)
   {
      if((null == c) || (null == c.getDepth()))
      {
         getDepthField().setText("");
         getDepthUnitsField().setText(Config.getDefaultWaterDepthUnits().toString());
      } else
      {
         getDepthField().setText(c.getDepth().valueString());
         getDepthUnitsField().setText(c.getDepth().getUnits().toString());
      }
   }

   private void setAirTempField(Catch c)
   {
      if((null == c) || (null == c.getAirTemp()))
      {
         getAirTempField().setText("");
         getAirTempUnitsField().setText(Config.getDefaultTempUnits().toString());
      }
      else
      {
         getAirTempField().setText(c.getAirTemp().valueString());
         getAirTempUnitsField().setText(c.getAirTemp().getUnits().toString());
      }
   }

   private void setWaterTempField(Catch c)
   {
      if((null == c) || (null == c.getWaterTemp()))
      {
         getWaterTempField().setText("");
         getWaterTempUnitsField().setText(Config.getDefaultTempUnits().toString());
      }
      else
      {
         getWaterTempField().setText(c.getWaterTemp().valueString());
         getWaterTempUnitsField().setText(c.getWaterTemp().getUnits().toString());
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
         getSecchiUnitsField().setText(Config.getDefaultWaterDepthUnits().toString());
      }
      else
      {
         getSecchiField().setText(c.getSecchi().valueString());
         getSecchiUnitsField().setText(c.getSecchi().getUnits().toString());
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
         getWindSpeedUnitsField().setText(Config.getDefaultWindSpeedUnits().toString());
      }
      else
      {
         getWindSpeedField().setText(c.getWindSpeed().valueString());
         getWindSpeedUnitsField().setText(c.getWindSpeed().getUnits().toString());
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
      return binding.getRoot().getContext();
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

   private EditText getTripInfoField() { return (EditText) findViewById(R.id.tripInfoField); }
   private EditText getDateField() { return (EditText) findViewById(R.id.dateField); }
   private EditText getTimeField() { return (EditText) findViewById(R.id.timeField); }
   private EditText getGpsField() { return (EditText) findViewById(R.id.gpsField); }
   private Spinner getSpeciesField() { return (Spinner) findViewById(R.id.speciesField); }
   private EditText getLengthField() { return (EditText) findViewById(R.id.lengthField); }
   private TextView getLengthUnitsField() { return (TextView) findViewById(R.id.lengthUnitsLabel); }
   private EditText getWeightField() { return (EditText) findViewById(R.id.weightField); }
   private TextView getWeightUnitsField() { return (TextView) findViewById(R.id.weightUnitsLabel); }
   private Spinner getLureField() { return (Spinner) findViewById(R.id.lureField); }
   private Spinner getLureTypeField() { return (Spinner) findViewById(R.id.lureTypeField); }
   private Spinner getLureBrandField() { return (Spinner) findViewById(R.id.lureBrandField); }
   private Spinner getLureColorField() { return (Spinner) findViewById(R.id.lureColorField); }
   private Spinner getLureSizeField() { return (Spinner) findViewById(R.id.lureSizeField); }
   private Spinner getTrailerField() { return (Spinner) findViewById(R.id.trailerField); }
   private Spinner getTrailerColorField() { return (Spinner) findViewById(R.id.trailerColorField); }
   private Spinner getTrailerSizeField() { return (Spinner) findViewById(R.id.trailerSizeField); }
   private EditText getDepthField() { return (EditText) findViewById(R.id.depthField); }
   private TextView getDepthUnitsField() { return (TextView) findViewById(R.id.depthUnitsLabel); }
   private EditText getAirTempField() { return (EditText) findViewById(R.id.airTempField); }
   private TextView getAirTempUnitsField() { return (TextView) findViewById(R.id.airTempUnitsLabel); }
   private EditText getWaterTempField() { return (EditText) findViewById(R.id.waterTempField); }
   private TextView getWaterTempUnitsField() { return (TextView) findViewById(R.id.waterTempUnitsLabel); }
   private Spinner getWaterClarityField() { return (Spinner) findViewById(R.id.waterClarityField); }
   private EditText getSecchiField() { return (EditText) findViewById(R.id.secchiField); }
   private TextView getSecchiUnitsField() { return (TextView) findViewById(R.id.secchiUnitsLabel); }
   private Spinner getStructureField() { return (Spinner) findViewById(R.id.structureField); }
   private Spinner getCoverField() { return (Spinner) findViewById(R.id.coverField); }
   private Spinner getPrecipField() { return (Spinner) findViewById(R.id.precipField); }
   private EditText getWindSpeedField() { return (EditText) findViewById(R.id.windSpeedField); }
   private TextView getWindSpeedUnitsField() { return (TextView) findViewById(R.id.windSpeedUnitsLabel); }
   private Spinner getWindDirectionField() { return (Spinner) findViewById(R.id.windDirectionField); }
   private Spinner getWindStrengthField() { return (Spinner) findViewById(R.id.windStrengthField); }
   private EditText getNotesField() { return (EditText) findViewById(R.id.notesField); }

}

