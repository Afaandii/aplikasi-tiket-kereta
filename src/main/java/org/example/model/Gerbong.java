package org.example.model;

import java.sql.Timestamp;

public class Gerbong {
    private int id;
    private int keretaId;
    private int kelasKeretaId;
    private String namaGerbong;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Display helpers (JOIN results)
    private String namaKereta;
    private String namaKelas;

    public Gerbong() {}

    public Gerbong(int id, int keretaId, int kelasKeretaId, String namaGerbong) {
        this.id = id;
        this.keretaId = keretaId;
        this.kelasKeretaId = kelasKeretaId;
        this.namaGerbong = namaGerbong;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getKeretaId() { return keretaId; }
    public void setKeretaId(int keretaId) { this.keretaId = keretaId; }

    public int getKelasKeretaId() { return kelasKeretaId; }
    public void setKelasKeretaId(int kelasKeretaId) { this.kelasKeretaId = kelasKeretaId; }

    public String getNamaGerbong() { return namaGerbong; }
    public void setNamaGerbong(String namaGerbong) { this.namaGerbong = namaGerbong; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getNamaKereta() { return namaKereta; }
    public void setNamaKereta(String namaKereta) { this.namaKereta = namaKereta; }

    public String getNamaKelas() { return namaKelas; }
    public void setNamaKelas(String namaKelas) { this.namaKelas = namaKelas; }
}
