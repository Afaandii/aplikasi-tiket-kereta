package org.example.model;

import java.sql.Timestamp;

public class KelasKereta {
    private int id;
    private String namaKelasKereta;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int stokGerbong;

    public KelasKereta() {
    }

    public KelasKereta(int id, String namaKelasKereta) {
        this.id = id;
        this.namaKelasKereta = namaKelasKereta;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaKelasKereta() {
        return namaKelasKereta;
    }

    public void setNamaKelasKereta(String namaKelasKereta) {
        this.namaKelasKereta = namaKelasKereta;
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

    public int getStokGerbong() {
        return stokGerbong;
    }

    public void setStokGerbong(int stokGerbong) {
        this.stokGerbong = stokGerbong;
    }

    @Override
    public String toString() {
        return namaKelasKereta;
    }
}
