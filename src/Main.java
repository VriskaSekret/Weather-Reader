import java.util.*;
import java.awt.*;

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
        this.climateType = climateType;
        this.columnNames = climateType.getColumnNames();
    }
    public void process(String regionDataFileName){}
}

public class Main {
    public static void main(String[] args) {

    }
}