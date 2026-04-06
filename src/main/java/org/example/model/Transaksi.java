package org.example.model;

import java.sql.Timestamp;

public class Transaksi {
    private int id;
    private int userId;
    private String kodeTransaksi;
    private String kodeBooking;
    private String namaCustomer;
    private long totalBayar;
    private int jumlahTiket;
    private String status;
    private String metodePembayaran;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String username;

    public Transaksi() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getKodeTransaksi() { return kodeTransaksi; }
    public void setKodeTransaksi(String kodeTransaksi) { this.kodeTransaksi = kodeTransaksi; }

    public String getKodeBooking() { return kodeBooking; }
    public void setKodeBooking(String kodeBooking) { this.kodeBooking = kodeBooking; }

    public String getNamaCustomer() { return namaCustomer; }
    public void setNamaCustomer(String namaCustomer) { this.namaCustomer = namaCustomer; }

    public long getTotalBayar() { return totalBayar; }
    public void setTotalBayar(long totalBayar) { this.totalBayar = totalBayar; }

    public int getJumlahTiket() { return jumlahTiket; }
    public void setJumlahTiket(int jumlahTiket) { this.jumlahTiket = jumlahTiket; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
