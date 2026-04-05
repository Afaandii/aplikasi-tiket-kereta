package org.example.model;

import java.sql.Timestamp;

public class Kursi {
    private int id;
    private int gerbongId;
    private int barisKursi;
    private String kodeKursi;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Kursi() {}

    public Kursi(int id, int gerbongId, int barisKursi, String kodeKursi) {
        this.id = id;
        this.gerbongId = gerbongId;
        this.barisKursi = barisKursi;
        this.kodeKursi = kodeKursi;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGerbongId() { return gerbongId; }
    public void setGerbongId(int gerbongId) { this.gerbongId = gerbongId; }

    public int getBarisKursi() { return barisKursi; }
    public void setBarisKursi(int barisKursi) { this.barisKursi = barisKursi; }

    public String getKodeKursi() { return kodeKursi; }
    public void setKodeKursi(String kodeKursi) { this.kodeKursi = kodeKursi; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
