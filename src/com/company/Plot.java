package com.company;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleFloatProperty;

public class Plot {
    private SimpleStringProperty URL;
    private SimpleStringProperty city;
    private SimpleStringProperty district;
    private SimpleStringProperty street;
    private SimpleIntegerProperty price;
    private SimpleIntegerProperty area;
    private SimpleFloatProperty kmFromCentre;

    public String getURL(){ return URL.get(); }
    public String getCity(){ return city.get(); }
    public String getDistrict(){ return district.get(); }
    public String getStreet(){ return street.get(); }
    public int getPrice(){ return price.get(); }
    public int getArea(){ return area.get(); }
    public float getKmFromCentre(){ return  kmFromCentre.get(); }

    public void setURL(String URL){ this.URL = new SimpleStringProperty(URL); }
    public void setCity(String city) { this.city = new SimpleStringProperty(city); }
    public void setDistrict(String district) { this.district = new SimpleStringProperty(district); }
    public void setStreet(String street) { this.street = new SimpleStringProperty(street); }
    public void setPrice(int price){ this.price = new SimpleIntegerProperty(price); }
    public void setArea(int area){ this.area = new SimpleIntegerProperty(area); }
    public void setKmFromCentre(float kmFromCentre){ this.kmFromCentre = new SimpleFloatProperty(kmFromCentre); }

    public Plot(String URL, String city, String district, String street, int price, int area, float kmFromCentre) {
        this.URL = new SimpleStringProperty(URL);
        this.city = new SimpleStringProperty(city);
        this.district = new SimpleStringProperty(district);
        this.street = new SimpleStringProperty(street);
        this.price = new SimpleIntegerProperty(price);
        this.area = new SimpleIntegerProperty(area);
        this.kmFromCentre = new SimpleFloatProperty(kmFromCentre);
    }

    public Plot(String URL, String city, String district, int price, int area) {
        this.URL = new SimpleStringProperty(URL);
        this.city = new SimpleStringProperty(city);
        this.district = new SimpleStringProperty(district);
        this.price = new SimpleIntegerProperty(price);
        this.area = new SimpleIntegerProperty(area);
    }

    public Plot() {
    }
}
