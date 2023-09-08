package com.example.simplegame;

// this class is used to contain info about our database
public class DataBaseInfo {
    private String dataBaseConnectionURL = "jdbc:mysql://localhost:3306/management?user=bestuser&password=bestuser";
    private String imageURL = "http://localhost:5500/"; //https://172.17.64.55:12321

    public String getDataBaseConnectionURL() {
        return dataBaseConnectionURL;
    }

    public String getImageURL() {
        return imageURL;
    }
}
