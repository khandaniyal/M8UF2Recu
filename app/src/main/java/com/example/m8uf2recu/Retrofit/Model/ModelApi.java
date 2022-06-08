package com.example.m8uf2recu.Retrofit.Model;

import java.util.ArrayList;

public class ModelApi {

    String name;
    ArrayList<Weather> weather;
    Coord coord;
    Temp main;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public ArrayList<Weather> getWeather() { return weather; }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public Temp getMain() {
        return main;
    }

    public void setMain(Temp main) {
        this.main = main;
    }
}
