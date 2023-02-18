package com.jetsup.ussdracharge.models;

public class ISPContent {
    String ussdCodeName;
    String ussdCode;

    public ISPContent(String ussdCodeName, String ussdCode) {
        this.ussdCodeName = ussdCodeName;
        this.ussdCode = ussdCode;
    }

    public String getUssdCodeName() {
        return ussdCodeName;
    }

    public String getUssdCode() {
        return ussdCode;
    }
}
