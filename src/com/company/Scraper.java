package com.company;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class Scraper {

    protected String City = null;

    protected String District = null;

    protected String Street = null;

    protected int Price = 0;

    protected int Area = 0;

    protected float Km = 0.0f;

    public void scraping() throws IOException, SQLException {
        String url = "https://www.otodom.pl/sprzedaz/dzialka/lodz/?search%5Bregion_id%5D=5&search%5Bcity_id%5D=1004&nrAdsPerPage=72";
        JDBC jdbc = new JDBC();
        for (int i = 0; i < 5; i++) {
            if (i > 1) {
                url = url + "&page=" + i;
            }
            Document document = Jsoup.connect(url).get();
            Elements links = document.select("a[href]");

            for (Element link : links) {
                String linkTemp = link.attr("href");
                String textTemp = link.text();
                if (!textTemp.isEmpty() && !linkTemp.contains("search") && linkTemp.contains("oferta")) {
                    System.out.println("link : " + link.attr("href"));
                    formatDescription(getDescription(linkTemp));
                    getStreetName(linkTemp);
                    getKmFrom(linkTemp);
                    jdbc.addToBase(new Plot(linkTemp, City, District, Street, Price, Area, Km));
                }
            }
        }

    }

    public boolean isActual(String url) throws IOException {
        Connection.Response response = Jsoup.connect(url).followRedirects(true).execute();
        if(response.url().toString().equals("https://www.otodom.pl/sprzedaz/dzialka/lodz/#from404"))
            return false;
        return true;
    }

    public void getKmFrom(String url) throws IOException {
        try {
            Document doc = Jsoup.connect(url).get();

            String km = null;
            int num = 0;
            Elements kmAndTime = doc.select("span.UdvAnf");
            String[] temp = kmAndTime.text().split(" ", 13);
            num = Arrays.asList(temp).indexOf("min");
            km = temp[num+1];
            km = km.substring(1);
            km = km.replaceAll(",", "\\.");

            Km = Float.valueOf(km);
        }catch (java.lang.StringIndexOutOfBoundsException e){
            Km = 0.0f;
        }

    }

    private void getStreetName(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String head = null;
        int num = 0;
        Elements section = doc.select("section.section-description");
        Elements str = doc.select("a.css-1sulocs-baseStyle-Address-contentStyle");

        head = str.text();
        head = head.replaceAll("\\.", " ");
        head = head.replaceAll(",", " ");
        String[] header = head.split(" ", 10);
        String[] workingHeader = Arrays.stream(header)
                .filter(value -> value != null && value.length() > 0).toArray(size -> new String[size]);

        if(head.contains("ul")){
            num = Arrays.asList(workingHeader).indexOf("ul");
            setStreet(workingHeader, num);
            return;
        }

        String tempText = section.text().replaceAll("\\.", " ");
        tempText = tempText.replaceAll(",", " ");
        String[] withNulls = tempText.split(" ", 1000);
        String[] words = Arrays.stream(withNulls)
                .filter(value -> value != null && value.length() > 0).toArray(size -> new String[size]);
        num = Arrays.asList(words).indexOf("ul");
        if (((int) words[num + 1].charAt(0) >= 65 && (int) words[num + 1].charAt(0) <= 90) && !words[num + 1].equals("Opis")) {
            setStreet(words, num);
            return;
        }

        num = Arrays.asList(words).indexOf("ulicy");
        if (((int) words[num + 1].charAt(0) >= 65 && (int) words[num + 1].charAt(0) <= 90) && !words[num + 1].equals("Opis")) {
            setStreet(words, num);
            return;
        }

        num = Arrays.asList(words).indexOf("ulic");
        if (((int) words[num + 1].charAt(0) >= 65 && (int) words[num + 1].charAt(0) <= 90) && !words[num + 1].equals("Opis")) {
            setStreet(words, num);
            return;
        }

        num = Arrays.asList(words).indexOf("alei");
        if (((int) words[num + 1].charAt(0) >= 65 && (int) words[num + 1].charAt(0) <= 90) && !words[num + 1].equals("Opis")) {
            setStreet(words, num);
            return;
        }
        System.out.println("[-]");
        //System.out.println(tempText);
        }

            private void setStreet(String[] words, int num){
                Street = words[num + 1];
                if(Street.endsWith("iej"))
                    Street = Street.substring(0, Street.length() - 3) + "a";
                else if(Street.endsWith("ej"))
                    Street = Street.substring(0, Street.length() - 2) + "a";
                System.out.println(Street);
            }


            private String getDescription (String url) throws IOException {
                Document document = Jsoup.connect(url).get();
                String description =
                        document.select("meta[name=description]").get(0)
                                .attr("content");
                return description;
            }

            private void formatDescription (String description){
                try {
                    description = description.replaceAll("Odkryj tę działkę na sprzedaż w miejscowości ", "");
                    description = description.replaceAll("za cenę ", "");
                    description = description.replaceAll(". Ta działka na sprzedaż ma ", "");
                    description = description.replaceAll(" ", "");
                    description = description.substring(0, description.lastIndexOf("m²"));

                    String location = description.substring(0, description.lastIndexOf(","));
                    String city = location.substring(0, location.lastIndexOf(","));
                    String district = location.substring(location.lastIndexOf(",") + 1);
                    String numbers = description.substring(description.lastIndexOf(",") + 1);
                    String price = numbers.substring(0, numbers.lastIndexOf("z"));
                    String area = numbers.substring(numbers.lastIndexOf("ł") + 1);

                    City = city;
                    District = district;
                    Price = Integer.parseInt(price);
                    Area = Integer.parseInt(area);

                    //System.out.println(description);

                } catch (java.lang.StringIndexOutOfBoundsException e) {
                    City = "<edit>";
                    District = "<edit>";
                    Price = 0;
                    Area = 0;
                }
            }
        }
