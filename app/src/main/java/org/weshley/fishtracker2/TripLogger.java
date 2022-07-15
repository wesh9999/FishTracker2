package org.weshley.fishtracker2;

import android.Manifest;
import android.os.Environment;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

// NOTE:  Files are ending up in /Android/data/org.weshley.fishtracker2/files/Downloads/

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
      throws IOException
   {
      _dest= dest;
      if(!_dest.exists())
         _dest.mkdirs();
      else if(!_dest.isDirectory())
         throw new IOException("Destination '" + dest.getAbsolutePath() + "' is an already existing file");
   }

   public void writeConfig()
      throws IOException
   {
      checkCapabilities();
      File f = new File(_dest, "config.txt");
         // FIXME - change extension to xml (txt for now because i can't seem to view xml files in the emulator)
      backupFile(f);
      f.createNewFile();
      StringBuilder sb = new StringBuilder();
      Config.dumpData(sb);
      writeDataToFile(sb, f);
   }

   public void writeTrip(Trip t)
      throws IOException
   {
      checkCapabilities();
      SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmmss");
      String tripFileName = fmt.format(t.getStartTime()) + ".txt";
         // FIXME - change extension to xml (txt for now because i can't seem to view xml files in the emulator)
      File file = new File(_dest, tripFileName);
      backupFile(file);
      file.createNewFile();
      StringBuilder sb = new StringBuilder();
      t.dumpData(sb);
      writeDataToFile(sb, file);
   }

   private void writeDataToFile(StringBuilder sb, File f)
      throws IOException
   {
System.out.println("+++++++++ WRITING TO [[" + f.getAbsolutePath() + "]]");
System.out.println(sb.toString());
System.out.println("-------");
      FileOutputStream os = null;
      try
      {
         os = new FileOutputStream(f);
         os.write(sb.toString().getBytes());
         os.flush();
         os.close();
      }
      finally
      {
         if(null != os)
            os.close();
      }
/*
System.out.println(" +++++++++++ FILE EXISTS=" + file.exists());
FileInputStream is = new FileInputStream(file);
byte[] data = new byte[sb.toString().length()];
is.read(data, 0, sb.toString().length());
is.close();
System.out.println(new String(data));
System.out.println("---------------");
 */
   }

   private void backupFile(File f)
   {
      if(!f.exists() || f.isDirectory())
         return;
      String fullPath = f.getAbsolutePath();
      File backupFile = null;
      int idx = fullPath.lastIndexOf('.');
      if(-1 == idx)
         backupFile = new File(fullPath + ".bak");
      else
         backupFile = new File(fullPath.substring(0, idx) + ".bak");
      if(backupFile.exists())
         backupFile.delete();
      f.renameTo(backupFile);
   }

   private void checkCapabilities()
      throws IOException
   {
      String state = Environment.getExternalStorageState();
      if(!Environment.MEDIA_MOUNTED.equals(state))
         throw new IOException("External media not available for writing");

      if(null == _dest)
         throw new IOException("Destination directory is not set");
      if(!_dest.exists())
         throw new IOException("Destination directory'" + _dest.getAbsolutePath() + "' does not exist");
      if(!_dest.isDirectory())
         throw new IOException("Destination '" + _dest.getAbsolutePath() + "' is not a directory");
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
