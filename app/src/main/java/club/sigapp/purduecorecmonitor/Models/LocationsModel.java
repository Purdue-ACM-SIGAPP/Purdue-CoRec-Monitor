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
        if(this.ZoneId == t.ZoneId){
            return 1;
        }
        return 0;
    }
}
