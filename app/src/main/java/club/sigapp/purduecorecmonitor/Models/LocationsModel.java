package club.sigapp.purduecorecmonitor.Models;

public class LocationsModel implements Comparable<LocationsModel>{
    public String LocationId;
    public String LocationName;
    public String ZoneId;
    public String ZoneName;
    public int Capacity;
    public int Headcount;
    public String EntryDate;


    public int compareTo(LocationsModel t){
        if(this.ZoneName.compareTo(t.ZoneName) != 0){  // order by zone
            return this.ZoneName.compareTo(t.ZoneName);
        }else{  // if in same zone then order by location name
            return this.LocationName.compareTo(t.LocationName);
        }
    }
}
