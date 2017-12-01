package club.sigapp.purduecorecmonitor.Utils;



public class Properties {
    private static String[] daysOfWeek = {"Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat"};
    private static String[] monthsOfYear = {"   ","Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun.", "Jul.", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};

    public static String[] getDaysOfWeek() {
        return daysOfWeek;
    }
    public static String[] getMonthsOfYear() {return monthsOfYear;}
}
