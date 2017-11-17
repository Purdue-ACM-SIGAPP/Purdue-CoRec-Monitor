package club.sigapp.purduecorecmonitor.Models;

import android.location.Location;

public class LocationsModel implements Comparable<LocationsModel>{
    public String LocationId;
    public String LocationName;
    public String ZoneId;
    public int Capacity;
    public int Headcount;
    public String EntryDate;


    public int compareTo(LocationsModel t){
        if(this.ZoneId.compareTo(t.ZoneId) != 0){  // order by zone
            return this.ZoneId.compareTo(t.ZoneId);
        }else{  // if in same zone then order by location name
            return this.LocationName.compareTo(t.LocationName);
        }
    }
}
