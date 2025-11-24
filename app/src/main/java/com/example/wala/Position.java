package com.example.wala;

public class Position {
    int idPosition;
    String pseudo,numero,longitude,latitude;

    public Position(int idPosition, String pseudo, String numero, String longitude, String latitude) {
        this.idPosition = idPosition;
        this.pseudo = pseudo;
        this.numero = numero;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    public Position(String pseudo, String numero, String longitude, String latitude) {
        this.pseudo = pseudo;
        this.numero = numero;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return pseudo + " - " + numero + " (" + latitude + ", " + longitude + ")";
    }

}
