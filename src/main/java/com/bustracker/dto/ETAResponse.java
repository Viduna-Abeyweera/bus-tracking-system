package com.bustracker.dto;

public class ETAResponse {

    private String busId;
    private String stopName;
    private double distanceKm;
    private int etaMinutes;
    private double busLatitude;
    private double busLongitude;
    private double stopLatitude;
    private double stopLongitude;

    public ETAResponse() {
    }

    public ETAResponse(String busId, String stopName, double distanceKm,
                       int etaMinutes, double busLatitude, double busLongitude,
                       double stopLatitude, double stopLongitude) {
        this.busId = busId;
        this.stopName = stopName;
        this.distanceKm = distanceKm;
        this.etaMinutes = etaMinutes;
        this.busLatitude = busLatitude;
        this.busLongitude = busLongitude;
        this.stopLatitude = stopLatitude;
        this.stopLongitude = stopLongitude;
    }

    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }
    public String getStopName() { return stopName; }
    public void setStopName(String stopName) { this.stopName = stopName; }
    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    public int getEtaMinutes() { return etaMinutes; }
    public void setEtaMinutes(int etaMinutes) { this.etaMinutes = etaMinutes; }
    public double getBusLatitude() { return busLatitude; }
    public void setBusLatitude(double busLatitude) { this.busLatitude = busLatitude; }
    public double getBusLongitude() { return busLongitude; }
    public void setBusLongitude(double busLongitude) { this.busLongitude = busLongitude; }
    public double getStopLatitude() { return stopLatitude; }
    public void setStopLatitude(double stopLatitude) { this.stopLatitude = stopLatitude; }
    public double getStopLongitude() { return stopLongitude; }
    public void setStopLongitude(double stopLongitude) { this.stopLongitude = stopLongitude; }
}