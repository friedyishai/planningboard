package com.whiteboard.enums;

public enum FontEnum {
    ARIAL("Arial"),
    TIMES_NEW_ROMAN("Times New Roman"),
    COURIER_NEW("Courier New"),
    VERDANA("Verdana"),
    GEORGIA("Georgia");

    public String value;

    FontEnum(String value) {
        this.value = value;
    }
}
