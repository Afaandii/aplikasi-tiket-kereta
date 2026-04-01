package org.example.model;

import java.sql.Timestamp;

public class Stasiun {
    private int id;
    private String kodeStasiun;
    private String namaStasiun;
    private String kota;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Stasiun() {
    }

    public Stasiun(int id, String kodeStasiun, String namaStasiun, String kota) {
        this.id = id;
        this.kodeStasiun = kodeStasiun;
        this.namaStasiun = namaStasiun;
        this.kota = kota;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKodeStasiun() {
        return kodeStasiun;
    }

    public void setKodeStasiun(String kodeStasiun) {
        this.kodeStasiun = kodeStasiun;
    }

    public String getNamaStasiun() {
        return namaStasiun;
    }

    public void setNamaStasiun(String namaStasiun) {
        this.namaStasiun = namaStasiun;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
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
