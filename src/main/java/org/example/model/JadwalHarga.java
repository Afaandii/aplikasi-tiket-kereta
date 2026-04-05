package org.example.model;

import java.sql.Timestamp;

public class JadwalHarga {
    private int id;
    private int jadwalId;
    private int kelasId;
    private int hargaTiket;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Display helpers
    private String namaKelas;

    public JadwalHarga() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getJadwalId() { return jadwalId; }
    public void setJadwalId(int jadwalId) { this.jadwalId = jadwalId; }

    public int getKelasId() { return kelasId; }
    public void setKelasId(int kelasId) { this.kelasId = kelasId; }

    public int getHargaTiket() { return hargaTiket; }
    public void setHargaTiket(int hargaTiket) { this.hargaTiket = hargaTiket; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getNamaKelas() { return namaKelas; }
    public void setNamaKelas(String namaKelas) { this.namaKelas = namaKelas; }
}
