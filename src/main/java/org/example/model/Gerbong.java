package org.example.model;

import java.sql.Timestamp;

public class Gerbong {
    private int id;
    private int keretaId;
    private int kelasId;
    private String nomorGerbong;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int stokGerbong;

    // Display helpers (JOIN results)
    private String namaKereta;
    private String namaKelas;

    public Gerbong() {}

    public Gerbong(int id, int keretaId, int kelasId, String nomorGerbong, int stokGerbong) {
        this.id = id;
        this.keretaId = keretaId;
        this.kelasId = kelasId;
        this.nomorGerbong = nomorGerbong;
        this.stokGerbong = stokGerbong;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getKeretaId() { return keretaId; }
    public void setKeretaId(int keretaId) { this.keretaId = keretaId; }

    public int getKelasId() { return kelasId; }
    public void setKelasId(int kelasId) { this.kelasId = kelasId; }

    public String getNomorGerbong() { return nomorGerbong; }
    public void setNomorGerbong(String nomorGerbong) { this.nomorGerbong = nomorGerbong; }

    public int getStokGerbong() { return stokGerbong; }
    public void setStokGerbong(int stokGerbong) { this.stokGerbong = stokGerbong; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getNamaKereta() { return namaKereta; }
    public void setNamaKereta(String namaKereta) { this.namaKereta = namaKereta; }

    public String getNamaKelas() { return namaKelas; }
    public void setNamaKelas(String namaKelas) { this.namaKelas = namaKelas; }
}
