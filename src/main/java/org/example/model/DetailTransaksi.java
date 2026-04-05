package org.example.model;

import java.sql.Timestamp;

public class DetailTransaksi {
    private int id;
    private int transaksiId;
    private int jadwalKursiId;
    private int harga;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public DetailTransaksi() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransaksiId() { return transaksiId; }
    public void setTransaksiId(int transaksiId) { this.transaksiId = transaksiId; }

    public int getJadwalKursiId() { return jadwalKursiId; }
    public void setJadwalKursiId(int jadwalKursiId) { this.jadwalKursiId = jadwalKursiId; }

    public int getHarga() { return harga; }
    public void setHarga(int harga) { this.harga = harga; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
