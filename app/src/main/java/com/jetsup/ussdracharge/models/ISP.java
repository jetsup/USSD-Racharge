package com.jetsup.ussdracharge.models;

public class ISP {
    String ISPName;
    String ISPSlogan;
    int ISPLogoIcon;

    public ISP(String ISPName, String ISPSlogan, int ISPLogoIcon) {
        this.ISPName = ISPName;
        this.ISPSlogan = ISPSlogan;
        this.ISPLogoIcon = ISPLogoIcon;
    }

    public String getISPName() {
        return ISPName;
    }

    public String getISPSlogan() {
        return ISPSlogan;
    }

    public int getISPLogoIcon() {
        return ISPLogoIcon;
    }
}
