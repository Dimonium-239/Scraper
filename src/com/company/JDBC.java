package com.company;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class JDBC {

    public static Connection conn;

    public static Statement stmt;

    public JDBC() throws SQLException {
        connect();
    }

    public void connect() throws SQLException {
        conn = DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/PlotsBase",
                "root", "2000");
        stmt = conn.createStatement();

        // Connection is ready to use
        DatabaseMetaData meta = conn.getMetaData();
        System.out.println("Server name: "
                + meta.getDatabaseProductName());
        System.out.println("Server version: "
                + meta.getDatabaseProductVersion());

    }

    public void addToBase(Plot plot) throws SQLException {

        String priceStr, areaStr, kmStr;

        String url = plot.getURL();
        String city = plot.getCity();
        String district = plot.getDistrict();
        String street = plot.getStreet();
        priceStr = String.valueOf(plot.getPrice());
        areaStr = String.valueOf(plot.getArea());
        kmStr = String.valueOf(plot.getKmFromCentre());

        String VALUES = "VALUES ( '" + url + "', '" + city + "', '" + district + "', '" + street + "', " + priceStr + ", " + areaStr + ", " + kmStr + " )";

        stmt.executeUpdate("INSERT INTO new_schema.Plots (Link, City, District, Street, Price, Area, KmFrom) " + VALUES);
    }

    public void deleteFromBase(String url) throws SQLException {
        connect();
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM new_schema.Plots WHERE Link= '" + url + "';");
        conn.close();
    }

    public void editInBase(String URLKey, Plot newPlot) throws SQLException {

        String UPDATE = "UPDATE new_schema.Plots ";
        String SET = "SET " +
                "Link='" + newPlot.getURL() +
                "', City='" + newPlot.getCity() +
                "', District='" + newPlot.getDistrict() +
                "', Street='" + newPlot.getStreet() +
                "', Price=" + newPlot.getPrice() +
                ", Area=" + newPlot.getArea() +
                ", KmFrom=" + newPlot.getKmFromCentre() + " ";
        String WHERE = "WHERE Link = '" + URLKey + "';";
        System.out.println(UPDATE + SET + WHERE);
        stmt.executeUpdate(UPDATE + SET + WHERE);
        conn.close();


    }

    public void refresh() throws SQLException, IOException {
        String link = null;
        try {
            connect();
            String strSelect = "select Link, City from new_schema.Plots";
            ResultSet rset = stmt.executeQuery(strSelect);
            int rowCount = 0;
            Scraper scraper = new Scraper();

            while (rset.next()) {

                String URL = rset.getString("Link");
                String City = rset.getString("City");
                if(City.contains(","))
                    City = City.substring(0, City.indexOf(','));

                String UPDATE = "UPDATE new_schema.Plots ";
                String SET = "SET " +
                        "City='" + City + "' ";
                String WHERE = "WHERE Link = '" + URL + "';";
                stmt.executeUpdate(UPDATE + SET + WHERE);

                link = URL;
                if (!scraper.isActual(URL)) {
                    deleteFromBase(URL);
                    System.out.println("[deleted] " + URL);
                } else
                    System.out.println("[is actual] " + URL);
            }
            ++rowCount;
        }catch (java.lang.IllegalArgumentException e){
            deleteFromBase(link);
        }
    }

    public void addKmFrom() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (Exception ex) {
        }
        try {
            connect();
            String strSelect = "select Link, Price, City, District, Street, Area, KmFrom from new_schema.Plots";
            ResultSet rset = stmt.executeQuery(strSelect);

            int rowCount = 0;
            Scraper scraper = new Scraper();

            while (rset.next()) {

                String URL = rset.getString("Link");
                String city = rset.getString("City");
                String district = rset.getString("District");
                String street = rset.getString("Street");
                Integer price = rset.getInt("Price");
                Integer area = rset.getInt("Area");
                Float kmFrom = rset.getFloat("KmFrom");
                String link1 = "https://www.google.com/search?safe=active&client=firefox-b-d&ei=7tdFXdw1iN-SBaaGhYgE&q=" + city + "+" + district + "+" + street +"+to+%C5%82%C3%B3d%C5%BC+manufaktura&oq=lodz+lodzkie+pabianicka+to+%C5%82%C3%B3d%C5%BC+manufaktura&gs_l=psy-ab.12...1134871.1141390..1147166...0.0..0.234.1583.0j9j1......0....1..gws-wiz.......0i71.49l7_Lw9dD4&ved=0ahUKEwjc5M3Qr-fjAhWIr6QKHSZDAUEQ4dUDCAo";
                scraper.getKmFrom(link1);
                System.out.println("[" + rowCount + "] " + scraper.Km);
                addToBase(new Plot(URL, city, district, street, price, area, scraper.Km));
                ++rowCount;
            }

        } catch (SQLException e) {
        } catch (IOException e) {

        }
    }

    public ObservableList<Plot> getFromBase () {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
            } catch (Exception ex) {
            }

            List<Plot> tempPlots = new ArrayList();

            try {
                connect();
                String strSelect = "select Link, Price, City, District, Area, KmFrom from new_schema.Plots";
                ResultSet rset = stmt.executeQuery(strSelect);

                int rowCount = 0;
                while (rset.next()) {
                    String URL = rset.getString("Link");
                    String city = rset.getString("City");
                    String district = rset.getString("District");
                    String street = rset.getString("Street");
                    Integer price = rset.getInt("Price");
                    Integer area = rset.getInt("Area");
                    Float kmFrom = rset.getFloat("KmFrom");
                    tempPlots.add(new Plot(URL, city, district, street, price, area, kmFrom));
                    ++rowCount;
                }


                conn.close();
                if (conn.isClosed())
                    System.out.println("Connection closed.");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return FXCollections.observableArrayList(tempPlots);
        }
    }




