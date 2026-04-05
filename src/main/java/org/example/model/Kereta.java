package org.example.model;

import java.sql.Timestamp;

public class Kereta {
    private int id;
    private String kodeKereta;
    private String namaKereta;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Kereta() {
    }

    public Kereta(int id, String kodeKereta, String namaKereta) {
        this.id = id;
        this.kodeKereta = kodeKereta;
        this.namaKereta = namaKereta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKodeKereta() {
        return kodeKereta;
    }

    public void setKodeKereta(String kodeKereta) {
        this.kodeKereta = kodeKereta;
    }

    public String getNamaKereta() {
        return namaKereta;
    }

    public void setNamaKereta(String namaKereta) {
        this.namaKereta = namaKereta;
    }


    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
