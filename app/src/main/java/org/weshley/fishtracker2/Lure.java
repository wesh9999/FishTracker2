package org.weshley.fishtracker2;

public class Lure
{
   private String _type = null;
   private String _brand = null;
   private String _color = null;
   private String _size = null;
   private String _trailer = null;
   private String _trailerColor = null;
   private String _trailerSize = null;

   public Lure()
   {
   }

   public Lure copy()
   {
      Lure l = new Lure();
      l._type = _type;
      l._brand = _brand;
      l._color = _color;
      l._size = _size;
      l._trailer = _trailer;
      l._trailerColor = _trailerColor;
      l._trailerSize = _trailerSize;
      return l;
   }

   public String getType() { return _type; }
   public String getBrand() { return _brand; }
   public String getColor() { return _color; }
   public String getSize() { return _size; }
   public String getTrailer() { return _trailer; }
   public String getTrailerColor() { return _trailerColor; }
   public String getTrailerSize() { return _trailerSize; }

   public void setType(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _type = null;
      }
      else
      {
         _type = s;
         Config.addLureType(s);
      }
   }

   public void setBrand(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _brand = null;
      }
      else
      {
         _brand = s;
         Config.addLureBrand(s);
      }
   }

   public String description()
   {
      // FIXME - clean this up add all fields (?) to deal with optional fields not being populated...
      return _type;
   }

   public void setSize(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _size = null;
      }
      else
      {
         _size = s;
         Config.addLureSize(s);
      }
   }

   public void setColor(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _color = null;
      }
      else
      {
         _color = s;
         Config.addLureColor(s);
      }
   }

   public void setTrailerColor(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _trailerColor = null;
      }
      else
      {
         _trailerColor = s;
         Config.addLureColor(s);
      }
   }

   public void setTrailerSize(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _trailerSize = null;
      }
      else
      {
         _trailerSize = s;
         Config.addTrailerSize(s);
      }
   }

   public void setTrailerType(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _trailer = null;
      }
      else
      {
         _trailer = s;
         Config.addTrailerType(s);
      }
   }

}
