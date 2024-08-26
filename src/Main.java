/*

Geoff Ribu
926605515
grib784

WEATHER FILE READER

This program takes user input to process yearly weather data stored in .txt files.

The program uses an enum to figure out which data to display in its output and its respective column name.
The program then uses 3 classes (Rainfall, Sunshine, and Temperature) to process the data stored in a string.
These 3 classes are all extended from an abstract class "Measurement".

A class named Region uses the 3 data processing classes to store regional weather averages.

Then the Statistics class stores the regional data to output in a readable way for users.

*/




import java.util.*;
import java.io.*;

enum ClimateType{
    RAINFALL(new String[]{"Rainfall"}),
    RAINFALL_SUNSHINE(new String[]{"Rainfall", "Sunshine"}),
    RAINFALL_SUNSHINE_TEMPERATURE(new String[]{"Rainfall", "Sunshine", "Min.Temperature"}),
    ALL(new String[]{"Rainfall", "Sunshine", "Min.Temperature", "Max.Temperature"});

    private String[] names = new String[]{"Rainfall", "Sunshine", "Min.Temperature", "Max.Temperature"};

    ClimateType(String[] names) {
        this.names = names;
    }

    public String[] getColumnNames(){
        return names;
    }

    public static ClimateType fromInt(int value){
        return switch (value) {
            case 0 -> RAINFALL;
            case 1 -> RAINFALL_SUNSHINE;
            case 2 -> RAINFALL_SUNSHINE_TEMPERATURE;
            case 3 -> ALL;
            default -> null;
        };
    }
}

abstract class Measurement{
    protected ArrayList<Double> data;
    protected String measurementUnit;

    public Measurement(){
        this.data = new ArrayList<>();
        this.measurementUnit = "";
    }

    public double getAverage(){
        if (data.isEmpty()) {
            return 0.0;
        }
        else{
            double sum = 0.0;
            for (Double d : data) {
                sum += d;
            }
            return sum/data.size();
        }
    }

    public abstract void process(String line);

    public String toString(){
        return String.format("%.2f%s", getAverage(), measurementUnit);
    }
}

class Rainfall extends Measurement{
    public Rainfall(String measuredRainfall){
        super();
        measurementUnit = "mm";
        process(measuredRainfall);
    }

    @Override
    public void process(String line) {
        String[] dataList = line.split(",");
        for (String d : dataList) {
             double convertedData = (Double.parseDouble(d));
             if (convertedData > 0.0) data.add(convertedData);
             else data.add(0.0);
        }
    }
}

class Sunshine extends Measurement{
    public Sunshine(String measuredSunshine){
        super();
        measurementUnit = "hr";
        process(measuredSunshine);
    }

    @Override
    public void process(String line) {
        String[] dataList = line.split(",");
        for (String d : dataList) {
            data.add(Double.parseDouble(d));
        }
    }
}

class Temperature extends Measurement{
    public Temperature(String measuredTemperature){
        super();
        measurementUnit = "° C";
        process(measuredTemperature);
    }

    @Override
    public void process(String line) {
        String[] dataList = line.split("\t");
        for (String d : dataList) {
            data.add(Double.parseDouble(d));
        }
    }
}

class Region{
    private String regionName = "UNKNOWN";
    private ArrayList<Measurement> regionData = new ArrayList<>();

    public Region(){}

    public Region(String regionName){
        this.regionName = regionName;
    }

    public void process(String rainfall){
        Rainfall rainfallObj = new Rainfall(rainfall);
        regionData.add(rainfallObj);
    }

    public void process(String rainfall, String sunshine) {
        Rainfall rainfallObj = new Rainfall(rainfall);
        Sunshine sunshineObj = new Sunshine(sunshine);
        regionData.add(rainfallObj);
        regionData.add(sunshineObj);
    }

    public void process(String rainfall, String sunshine, String minTemperature) {
        Rainfall rainfallObj = new Rainfall(rainfall);
        Sunshine sunshineObj = new Sunshine(sunshine);
        Temperature temperatureObj = new Temperature(minTemperature);
        regionData.add(rainfallObj);
        regionData.add(sunshineObj);
        regionData.add(temperatureObj);
    }

    public void process(String rainfall, String sunshine, String minTemperature, String maxTemperature) {
        Rainfall rainfallObj = new Rainfall(rainfall);
        Sunshine sunshineObj = new Sunshine(sunshine);
        Temperature temperatureObj = new Temperature(minTemperature);
        Temperature temperatureObj2 = new Temperature(maxTemperature);
        regionData.add(rainfallObj);
        regionData.add(sunshineObj);
        regionData.add(temperatureObj);
        regionData.add(temperatureObj2);
    }

    public double getAverage(int colIndex){
        return regionData.get(colIndex).getAverage();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%16s|", regionName));
        for (Measurement m : regionData) {
            sb.append(String.format("%16s|", m.toString()));
        }
        return sb.toString();
    }
}

class Statistics {
    private ArrayList<Region> regions;
    private String[] columnNames;
    private ClimateType climateType;

    public Statistics(ClimateType climateType){
        this.regions = new ArrayList<>();
        this.climateType = climateType;
        this.columnNames = climateType.getColumnNames();
    }

    public void process(String regionDataFileName){
        Scanner input = null;
        try {
            input = new Scanner(new File(regionDataFileName));
            ArrayList<String> lines = new ArrayList<>();
            while (input.hasNextLine()) {
                lines.add(input.nextLine());
            }
            Region regionObj = new Region(regionDataFileName.substring(0, regionDataFileName.length()-4));
            if (climateType == ClimateType.ALL) {
                regionObj.process(lines.get(0), lines.get(1), lines.get(2), lines.get(3));
            } else if (climateType == ClimateType.RAINFALL_SUNSHINE_TEMPERATURE) {
                regionObj.process(lines.get(0), lines.get(1), lines.get(2));
            } else if (climateType == ClimateType.RAINFALL_SUNSHINE) {
                regionObj.process(lines.get(0), lines.get(1));
            } else {
                regionObj.process(lines.get(0));
            }

            regions.add(regionObj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            input.close();
        }
    }

    public void displayTable(){
        System.out.println("Average climatological data for selected locations throughout NZ\n" +
                "================================================================\n" +
                "================================================================\n");
        System.out.print("          Region|");
        for (String columnName : columnNames) {
            System.out.printf("%16s|", columnName);
        }
        System.out.println();
        System.out.print("=================");
        for (String columnName : columnNames) {
            System.out.print("=================");
        }
        System.out.println();
        for (Region r : regions) {
            System.out.println(r.toString());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Choose the types of data to analysis:\n" +
                "0 - Rainfall only\n" +
                "1 - Rainfall and Sunshine\n" +
                "2 - Rainfall, Sunshine and Minimum Temperature\n" +
                "3 - All data\n" +
                "Enter your selection:");
        int userSelection = input.nextInt();
        Statistics statistics;
        switch (userSelection) {
            case 0 -> statistics = new Statistics(ClimateType.RAINFALL);
            case 1 -> statistics = new Statistics(ClimateType.RAINFALL_SUNSHINE);
            case 2 -> statistics = new Statistics(ClimateType.RAINFALL_SUNSHINE_TEMPERATURE);
            case 3 -> statistics = new Statistics(ClimateType.ALL);
            default -> statistics = new Statistics(ClimateType.ALL);
        }
        System.out.println("Enter data file name (Enter 'EXIT' to finish):");
        String fileNameInput = input.next();
        while (!fileNameInput.equals("EXIT")){
            statistics.process(fileNameInput);
            System.out.println("Enter data file name (Enter 'EXIT' to finish):");
            fileNameInput = input.next();
        }
        System.out.println();
        statistics.displayTable();
    }
}