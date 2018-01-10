package club.sigapp.purduecorecmonitor.Models;

public class LocationsModel implements Comparable<LocationsModel>{
    public Location Location;
    public String LocationId;
    public String LocationName;
    public String ZoneId;
    public String DisplayName;
    public int Capacity;
    public int Count;
    public String EntryDate;

    public int compareTo(LocationsModel t){
        if(this.Location.Zone.ZoneName.compareTo(t.Location.Zone.ZoneName) != 0){  // order by zone
            return this.Location.Zone.ZoneName.compareTo(t.Location.Zone.ZoneName);
        }else{  // if in same zone then order by location name
            return this.LocationName.compareTo(t.LocationName);
        }
    }
}
