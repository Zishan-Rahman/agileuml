import java.util.Date;
import java.util.ArrayList;


class OclDate implements Comparable { 

  static final long YEARMS = 31536000000L; 
  static final long MONTHMS = 2628000000L;
  static final long DAYMS = 86400000L; 
  static final long HOURMS = 3600000L; 
  static final long MINUTEMS = 60000L; 
  static final long SECONDMS = 1000L;  
   
  static OclDate createOclDate() 
  { OclDate result = new OclDate();
    return result; 
  }

  public static long systemTime = 0;
  public long time = 0;

  int year = 0;
  int month = 0;
  int day = 0;
  int weekday = 0; 
  int hour = 0;
  int minute = 0;
  int second = 0;

  public static OclDate newOclDate()
  { Date dd = new Date(); 
    OclDate d = new OclDate();
    d.time = dd.getTime();
    d.year = dd.getYear();
    d.month = dd.getMonth();
    d.day = dd.getDate(); // day of month
    d.weekday = dd.getDay(); 
    d.hour = dd.getHours(); 
    d.minute = dd.getMinutes(); 
    d.second = dd.getSeconds();   
    OclDate.systemTime = d.time; 
    return d; 
  }


  public static OclDate newOclDate_Time(long t)
  { Date dd = new Date(t); 
    
    OclDate d = new OclDate();
    d.time = t;
    d.year = dd.getYear();
    d.month = dd.getMonth();
    d.day = dd.getDate(); // day of month
    d.weekday = dd.getDay(); 
    d.hour = dd.getHours(); 
    d.minute = dd.getMinutes(); 
    d.second = dd.getSeconds();   
    return d; 
  }

  public static OclDate newOclDate_YMD(int yr, int mn, int da)
  { OclDate d = new OclDate();
    int leapyears = (yr - 1968)/4; 
    d.time = (yr - 1970)*YEARMS + (mn-1)*MONTHMS + (da+leapyears-1)*DAYMS;
    d.year = yr;
    d.month = mn;
    d.day = da; // day of month
    Date dd = new Date(d.time); 
    
    d.weekday = dd.getDay(); 
    d.hour = 0; 
    d.minute = 0; 
    d.second = 0;   
    return d; 
  }
  
  public static OclDate newOclDate_YMDHMS(int yr, int mn, int da, int hr, int mi, int sec)
  { OclDate d = new OclDate();
    int leapyears = (yr - 1968)/4; 
    d.time = (yr - 1970)*YEARMS + (mn-1)*MONTHMS + (da + leapyears -1)*DAYMS + hr*HOURMS + mi*MINUTEMS + sec*SECONDMS;
    d.year = yr;
    d.month = mn;
    d.day = da; // day of month
    Date dd = new Date(d.time); 
    
    d.weekday = dd.getDay(); 
    d.hour = hr; 
    d.minute = mi; 
    d.second = sec;   
    return d; 
  }

  public static OclDate newOclDate_String(String s)
  { ArrayList<String> items = Ocl.allMatches(s, "[0-9]+");
  
    // System.out.println(items); 
	
    if (items == null) { return null; }
	 
    int n = items.size(); 
	// year, month, day, hour, mins, secs
	
    if (n == 3) 
    { try {
        int yr = Integer.parseInt("" + items.get(0)); 
        int mn = Integer.parseInt("" + items.get(1)); 
        int dd = Integer.parseInt("" + items.get(2)); 
        OclDate res = OclDate.newOclDate_YMD(yr,mn,dd); 
        return res; 
      } catch (Exception ex) { return null; }
    }

    if (n == 6) 
    { try {
		int yr = Integer.parseInt("" + items.get(0)); 
		int mn = Integer.parseInt("" + items.get(1)); 
		int dd = Integer.parseInt("" + items.get(2)); 
		int hr = Integer.parseInt("" + items.get(3)); 
		int mi = Integer.parseInt("" + items.get(4)); 
		int sec = Integer.parseInt("" + items.get(5)); 
		OclDate res = OclDate.newOclDate_YMDHMS(yr,mn,dd,hr,mi,sec); 
		return res; 
      } catch (Exception ex) { return null; }
    }

    return null; 
  } 
  
  public Object clone()
  { return OclDate.newOclDate_Time(time); } 

  public String toString()
  { // Date dte = new Date(time); 
    // return "" + dte;

    return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;  
  } 

  public void setTime(long t)
  { Date dd = new Date(t); 
    
    time = t;
    year = dd.getYear();
    month = dd.getMonth();
    day = dd.getDate(); // day of month
    weekday = dd.getDay(); 
    hour = dd.getHours(); 
    minute = dd.getMinutes(); 
    second = dd.getSeconds();   
  }


  public long getTime()
  { return time; }

  public int getYear()
  { return year; } 

  public int getMonth()
  { return month; } 

  public int getDate()
  { return day; } 

  public int getDay()
  { return weekday; } 

  public int getHours()
  { return hour; } 

  public int getHour()
  { return hour; } 

  public int getMinutes()
  { return minute; } 

  public int getMinute()
  { return minute; } 

  public int getSeconds()
  { return second; } 

  public int getSecond()
  { return second; } 

  public OclDate addYears(int y)
  { long newtime = time + y*YEARMS;
    return newOclDate_Time(newtime);
  } 

  public OclDate addMonths(int m)
  { long newtime = time + m*MONTHMS;
    return newOclDate_Time(newtime);
  } 

  public OclDate addMonthYMD(int m)
  {
    OclDate result = null;
    if (month + m > 12)
    {
      result = OclDate.newOclDate_YMD(year + 1, (month + m) % 12, day);
    }
    else {
      result = OclDate.newOclDate_YMD(year, month + m, day);
    }
    return result;
  }

  public OclDate subtractMonthYMD(int m)
  {
    OclDate result = null;
    if (month - m <= 0)
    {
      result = OclDate.newOclDate_YMD(year - 1, 12 - (m - month), day);
    }
    else {
      result = OclDate.newOclDate_YMD(year, month - m, day);
    }
    return result;
  }

  public OclDate addDays(int d)
  { long newtime = time + d*DAYMS;
    return newOclDate_Time(newtime);
  } 

  public OclDate addHours(int h)
  { long newtime = time + h*HOURMS;
    return newOclDate_Time(newtime);
  } 

  public OclDate addMinutes(int m)
  { long newtime = time + m*MINUTEMS;
    return newOclDate_Time(newtime);
  } 

  public OclDate addSeconds(int s)
  { long newtime = time + s*SECONDMS;
    return newOclDate_Time(newtime);
  } 

  public long yearDifference(OclDate d)
  { long newtime = time - d.time;
    return newtime/YEARMS;
  } 

  public long monthDifference(OclDate d)
  { long newtime = time - d.time;
    return newtime/MONTHMS;
  } 

  public static int differenceMonths(OclDate d1, OclDate d2)
  {
    int result = 0;
    result = (d1.year - d2.year)*12 + (d1.month - d2.month);
    return result;
  }

  public long dayDifference(OclDate d)
  { long newtime = time - d.time;
    return newtime/DAYMS;
  } 

  public long hourDifference(OclDate d)
  { long newtime = time - d.time;
    return newtime/HOURMS;
  } 

  public long minuteDifference(OclDate d)
  { long newtime = time - d.time;
    return newtime/MINUTEMS;
  } 

  public long secondDifference(OclDate d)
  { long newtime = time - d.time;
    return newtime/SECONDMS;
  } 
  
  public boolean dateBefore(OclDate d)
  {
    boolean result = false;
    if (time < d.time)
    {
      result = true;
    }
    else {
      result = false;
    }
    return result;
  }

  public boolean dateAfter(OclDate d)
  {
    boolean result = false;
    if (time > d.time)
    {
      result = true;
    }
    else {
      result = false;
    }
    return result;
  }
  
  public int compareTo(Object obj)
  { if (obj instanceof OclDate)
    { OclDate dd = (OclDate) obj; 
      if (time < dd.time) { return -1; }
      if (time > dd.time) { return 1; }
      return 0; 
    }
    return 0; 
  }

  public int compareToYMD(OclDate d)
  {
    int result = 0;
    if (year != d.year)
    {
      return (year - d.year) * 365;
    }
    else {
      if (year == d.year && month != d.month)
      {
        return (month - d.month) * 30;
      }
      else {
        if (year == d.year && month == d.month)
        {
          return (day - d.day);
        }
      }
    }

    return result;
  }

  public static OclDate maxDateYMD(OclDate d1, OclDate d2)
  {
    OclDate result = null;
    if (0 < d1.compareToYMD(d2))
    {
      result = d2;
    }
    else {
      result = d1;
    }
    return result;
  }


  public static boolean leapYear(int yr)
  {
    boolean result = false;
    if (yr % 4 == 0 && yr % 100 != 0)
    {
      result = true;
    }
    else {
      if (yr % 400 == 0)
      {
        result = true;
      }
      else {
        result = false;
      }
    }
    return result;
  }


  public boolean isLeapYear()
  {
    boolean result = false;
    result = OclDate.leapYear(year);
    return result;
  }


  public static int monthDays(int mn, int yr)
  {
    int result = 0;
    if (Ocl.initialiseSet(4,6,9,11).contains(mn))
    {
      result = 30;
    }
    else {
      if (mn == 2 && OclDate.leapYear(yr))
      {
        result = 29;
      }
      else {
        if (mn == 2 && !(OclDate.leapYear(yr)))
        {
          result = 28;
        }
        else {
          result = 31;
        }
      }
    }
    return result;
  }


  public int daysInMonth()
  {
    int result = 0;
    result = OclDate.monthDays(month, year);
    return result;
  }


  public boolean isEndOfMonth()
  {
    boolean result = false;
    if (day == OclDate.monthDays(month, year))
    {
      result = true;
    }
    else {
      result = false;
    }
    return result;
  }


  public static int daysBetweenDates(OclDate d1, OclDate d2)
  {
    int result = 0;
    int startDay = d1.day;
    int startMonth = d1.month;
    int startYear = d1.year;
    int endDay = d2.day;
    int endMonth = d2.month;
    int endYear = d2.year;
    int days = 0;
    while (startYear < endYear || (startYear == endYear && startMonth < endMonth))
    {
      int daysinmonth = OclDate.monthDays(startMonth, startYear);
      days = days + daysinmonth - startDay + 1;
      startDay = 1;
      startMonth = startMonth + 1;
      if (startMonth > 12)
      {
        startMonth = 1;
        startYear = startYear + 1;
      }
    }
    days = days + endDay - startDay;
    return days;
  }

  public static long getSystemTime()
  { Date d = new Date(); 
    long result = d.getTime();
    OclDate.systemTime = result;
    return result;
  }

  public static void setSystemTime(long t)
  { OclDate.systemTime = t; }

  public static void main(String[] args)
  { OclDate d1 = OclDate.newOclDate_YMD(2023,7,1); 
    OclDate d2 = OclDate.newOclDate_YMD(2024,11,1); 
    int dd = OclDate.daysBetweenDates(d1,d2); 
    System.out.println(d2.isLeapYear()); 
  } 
  
}

