package id.ac.unsyiah.android.tanamubah.model;

public class EventModel {

    private String id;
    private String nama;
    private String penyelenggara;
    private String desc;
    private String imageUri;
    private long createdAt;
    private String lokasi;
    private String tglMulai;
    private String tglSelesai;
    private String email;
    private String noHp;
    private int dana;
    private long currentRelawan;
    private long targetRelawan;


    // WAJIB constructor kosong (untuk Firestore nanti)
    public EventModel() {
    }

    public EventModel(String nama, String penyelenggara, String desc, String imageUri, int targetRelawan, long createdAt, String lokasi) {
        this.nama = nama;
        this.penyelenggara = penyelenggara;
        this.desc = desc;
        this.imageUri = imageUri;
        this.targetRelawan = targetRelawan;
        this.createdAt = createdAt;
        this.lokasi = lokasi;
    }

    public String getNama() {
        return nama;
    }

    public String getPenyelenggara() {
        return penyelenggara;
    }

    public String getDesc() {
        return desc;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getLokasi() {
        return lokasi;
    }
    public long getCreatedAt() {
        return createdAt;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setPenyelenggara(String penyelenggara) {
        this.penyelenggara = penyelenggara;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }


    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getTglMulai() {
        return tglMulai;
    }

    public void setTglMulai(String tglMulai) {
        this.tglMulai = tglMulai;
    }

    public String getTglSelesai() {
        return tglSelesai;
    }

    public void setTglSelesai(String tglSelesai) {
        this.tglSelesai = tglSelesai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }


    public int getDana() {
        return dana;
    }

    public void setDana(int dana) {
        this.dana = dana;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCurrentRelawan() {
        return currentRelawan;
    }

    public void setCurrentRelawan(long currentRelawan) {
        this.currentRelawan = currentRelawan;
    }

    public long getTargetRelawan() {
        return targetRelawan;
    }

    public void setTargetRelawan(long targetRelawan) {
        this.targetRelawan = targetRelawan;
    }
}
