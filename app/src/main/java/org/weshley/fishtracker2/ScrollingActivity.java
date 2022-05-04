package org.weshley.fishtracker2;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.snackbar.Snackbar;
import org.weshley.fishtracker2.databinding.ActivityScrollingBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScrollingActivity
   extends AppCompatActivity
{
   Trip _trip = null;
   HashMap<String,Integer> _speciesMap;

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

      initEditors();
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
      else
      {
         return super.onOptionsItemSelected(item);
      }
   }

   private void startTrip()
   {
      if(null == _trip)
      {
         _trip = Trip.createNewTrip();
         _trip.setLocation("Lake Hartwell");
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
      if(null == _trip)
      {
         showMessage("No active trip");
         return;
      }

      _trip.endTrip();
      clearCatchFields();
      enableCatchControls(false);
      enableTripControls(false);
      _trip = null;
      updateTripInfo();
      updateMenuState();
   }

   private void updateTripInfo()
   {
      if(null == _trip)
         getTripInfoField().setText(R.string.no_trip_message);
      else
         getTripInfoField().setText(_trip.getMultilineLabel());
   }

   private void newCatch()
   {
      startTrip();
      updateCatchFields(_trip.newCatch());
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
      getTimeField().setText(Config.formatTime(c.getTimestamp()));
      getDateField().setText(Config.formatDate(c.getTimestamp()));
      setSpeciesSelection(c.getSpecies());
// TODO - add other catch fields
   }

   private void clearCatchFields()
   {
      getTimeField().setText("");
      getDateField().setText("");
      getSpeciesField().setSelection(0);
// TODO - add other catch fields
   }

   private void initEditors()
   {
      initSpeciesEditor();
   }

   private void enableCatchControls(boolean enabled)
   {
      getSpeciesField().setEnabled(enabled);
      // TODO - add other fields here....
   }

   private void enableTripControls(boolean enabled)
   {
      // TODO - add controls here when trip info is added to UI
   }

   private void initSpeciesEditorItems()
   {
      // TODO - add most recent species (maybe last 5 from all trips?) plus a separator at top of list
      Spinner editor = getSpeciesField();
      List<String> list = new ArrayList<>();
      list.add(Config.BLANK_LABEL);
      list.add(Config.OTHER_LABEL);
      for(String s : Config.getAllSpecies())
         list.add(s);
      _speciesMap = new HashMap<>();
      for(int i = 0; i < list.size(); ++i)
         _speciesMap.put(list.get(i), i);
      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
            getContext(), R.layout.support_simple_spinner_dropdown_item, list);
      editor.setAdapter(spinnerArrayAdapter);
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
               final EditText input = new EditText(getContext());
               new AlertDialog.Builder(getContext())
                     .setTitle("Enter New Species")
                     .setView(input)
                     .setIcon(android.R.drawable.ic_dialog_alert)
                     .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        String newSpecies = input.getText().toString();
                        getCurrentCatch().setSpecies(newSpecies);
                        initSpeciesEditorItems();
                        setSpeciesSelection(newSpecies);
                     })
                     .setNegativeButton(android.R.string.no, null).show();
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

   void setSpeciesSelection(String species)
   {
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

   private Catch getCurrentCatch()
   {
      if(null == _trip)
         return null;
      else
         return _trip.getLastCatch();
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

   private EditText getDateField() { return (EditText) findViewById(R.id.dateField); }
   private EditText getTimeField() { return (EditText) findViewById(R.id.timeField); }
   private EditText getTripInfoField() { return (EditText) findViewById(R.id.tripInfoField); }
   private Spinner getSpeciesField() { return (Spinner) findViewById(R.id.speciesField); }

}

