package org.example.model;

import java.sql.Timestamp;

public class JadwalKursi {
    private int id;
    private int jadwalId;
    private int kursiId;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Display helpers
    private String kodeKursi;
    private int barisKursi;

    public JadwalKursi() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getJadwalId() { return jadwalId; }
    public void setJadwalId(int jadwalId) { this.jadwalId = jadwalId; }

    public int getKursiId() { return kursiId; }
    public void setKursiId(int kursiId) { this.kursiId = kursiId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getKodeKursi() { return kodeKursi; }
    public void setKodeKursi(String kodeKursi) { this.kodeKursi = kodeKursi; }

    public int getBarisKursi() { return barisKursi; }
    public void setBarisKursi(int barisKursi) { this.barisKursi = barisKursi; }
}
