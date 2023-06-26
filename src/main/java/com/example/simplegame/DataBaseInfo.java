package com.example.simplegame;

// this class is used to contain info about our database
public class DataBaseInfo {
    private String dataBaseConnectionURL = "jdbc:mysql://localhost:3306/management?user=bestuser&password=bestuser";

    public String getDataBaseConnectionURL() {
        return dataBaseConnectionURL;
    }
}
