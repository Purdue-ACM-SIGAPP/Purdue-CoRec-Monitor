package club.sigapp.purduecorecmonitor.Utils;

public class Properties {
    private static String[] daysOfWeek = {"Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat"};
    private static String[] monthsOfYear = {"Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun.", "Jul.", "Aug.", "Sept.", "Oct.", "Nov.", "Dec.", " "};
    private static String[] hoursOfDay = {"12am", "1am", "2am", "3am", "4am", "5am", "6am", "7am", "8am", "9am", "10am", "11am", "12pm", "1pm", "2pm", "3pm", "4pm", "5pm", "6pm", "7pm", "8pm", "9pm", "10pm", "11pm"};

    public static String[] getDaysOfWeek() {
        return daysOfWeek;
    }
    public static String[] getMonthsOfYear() {return monthsOfYear;}
    public static String[] getHoursOfDay() {
        return hoursOfDay;
    }
}
