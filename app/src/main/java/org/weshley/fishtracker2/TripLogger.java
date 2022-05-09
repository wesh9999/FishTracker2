package org.weshley.fishtracker2;

import android.Manifest;
import android.os.Environment;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class TripLogger
{
   private static TripLogger INSTANCE = new TripLogger();

   private File _dest = null;

   public static TripLogger instance()
   {
      return INSTANCE;
   }

   private void TripLogger()
   {
//      checkCapabilities();
   }

   public void setDestinationFolder(File dest)
   {
      _dest= dest;
      if(!_dest.exists())
         _dest.mkdirs();
      System.out.println("+++++++ Set dest folder to [[" + _dest.getAbsolutePath() + "]] - exists=" + _dest.exists());
   }

   public void writeTrip(Trip t)
      throws IOException
   {
      checkCapabilities();
      SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmmss");
      String tripFileName = fmt.format(t.getStartTime()) + ".txt";
      File file = new File(_dest, tripFileName);
      file.createNewFile();

      StringBuilder sb = new StringBuilder();
      t.dumpData(sb);
System.out.println("+++++++++ WRITING TO [[" + file.getAbsolutePath() + "]]");
System.out.println(sb.toString());
System.out.println("-------");
      FileOutputStream os = null;
      try
      {
         os = new FileOutputStream(file);
         os.write(sb.toString().getBytes());
         os.flush();
         os.close();
      }
      finally
      {
         if(null != os)
            os.close();
      }
System.out.println(" +++++++++++ FILE EXISTS=" + file.exists());
FileInputStream is = new FileInputStream(file);
byte[] data = new byte[sb.toString().length()];
is.read(data, 0, sb.toString().length());
is.close();
System.out.println(new String(data));
System.out.println("---------------");
   }

   private void checkCapabilities()
      throws IOException
   {
      String state = Environment.getExternalStorageState();
      if(!Environment.MEDIA_MOUNTED.equals(state))
         throw new IOException("External media not available for writing");
/*
      System.out.println("------- External Storage State ----------");
       if(Environment.MEDIA_MOUNTED.equals(state))
      {
         // Operation possible - Read and Write
         System.out.println("   isAvailable= true;");
         System.out.println("   isWritable= true");
         System.out.println("   isReadable= true");
      }
      else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
      {
         // Operation possible - Read Only
         System.out.println("   isAvailable= true");
         System.out.println("   isWritable= false");
         System.out.println("   isReadable= true");
      }
      else
      {
         // SD card not available
         System.out.println("   isAvailable = false");
         System.out.println("   isWritable= false");
         System.out.println("   isReadable= false");
      }
*/
   }
}
