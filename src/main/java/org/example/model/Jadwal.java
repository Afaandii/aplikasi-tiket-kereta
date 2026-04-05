package org.example.model;

import java.sql.Timestamp;

public class Jadwal {
    private int id;
    private int keretaId;
    private int stasiunAsalId;
    private int stasiunTujuanId;
    private Timestamp waktuBerangkat;
    private Timestamp waktuTiba;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Display helpers (JOIN results)
    private String namaKereta;
    private String namaStasiunAsal;
    private String namaStasiunTujuan;

    public Jadwal() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getKeretaId() { return keretaId; }
    public void setKeretaId(int keretaId) { this.keretaId = keretaId; }

    public int getStasiunAsalId() { return stasiunAsalId; }
    public void setStasiunAsalId(int stasiunAsalId) { this.stasiunAsalId = stasiunAsalId; }

    public int getStasiunTujuanId() { return stasiunTujuanId; }
    public void setStasiunTujuanId(int stasiunTujuanId) { this.stasiunTujuanId = stasiunTujuanId; }

    public Timestamp getWaktuBerangkat() { return waktuBerangkat; }
    public void setWaktuBerangkat(Timestamp waktuBerangkat) { this.waktuBerangkat = waktuBerangkat; }

    public Timestamp getWaktuTiba() { return waktuTiba; }
    public void setWaktuTiba(Timestamp waktuTiba) { this.waktuTiba = waktuTiba; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getNamaKereta() { return namaKereta; }
    public void setNamaKereta(String namaKereta) { this.namaKereta = namaKereta; }

    public String getNamaStasiunAsal() { return namaStasiunAsal; }
    public void setNamaStasiunAsal(String namaStasiunAsal) { this.namaStasiunAsal = namaStasiunAsal; }

    public String getNamaStasiunTujuan() { return namaStasiunTujuan; }
    public void setNamaStasiunTujuan(String namaStasiunTujuan) { this.namaStasiunTujuan = namaStasiunTujuan; }
}
