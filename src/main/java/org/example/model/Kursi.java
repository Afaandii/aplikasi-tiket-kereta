package org.example.model;

import java.sql.Timestamp;

public class Kursi {
    private int id;
    private int gerbongId;
    private int nomorKursi;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Kursi() {}

    public Kursi(int id, int gerbongId, int nomorKursi) {
        this.id = id;
        this.gerbongId = gerbongId;
        this.nomorKursi = nomorKursi;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGerbongId() { return gerbongId; }
    public void setGerbongId(int gerbongId) { this.gerbongId = gerbongId; }

    public int getNomorKursi() { return nomorKursi; }
    public void setNomorKursi(int nomorKursi) { this.nomorKursi = nomorKursi; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
