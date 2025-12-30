# 🌱 TanamUbah 
![Logo](app/src/main/res/drawable/logo_utama.png)
**TanamUbah** adalah aplikasi mobile berbasis Android yang berfokus pada pengelolaan dan partisipasi kegiatan lingkungan, khususnya event penanaman dan aksi hijau. Aplikasi ini memudahkan pengguna untuk menemukan event, melihat detail kegiatan, serta mendaftar sebagai relawan secara langsung melalui aplikasi.


---

## 🚀 Fitur Utama

- 🔐 **Autentikasi Pengguna**
  - Login dan registrasi menggunakan Firebase Authentication
- 📅 **Manajemen Event**
  - Menampilkan daftar event lingkungan
  - Melihat detail event (deskripsi, lokasi, penyelenggara, target relawan)
- 🙋‍♀️ **Pendaftaran Relawan**
  - Pengguna dapat mendaftar ke event
  - Sistem mencegah pendaftaran ganda
- 📊 **Progress Relawan**
  - Menampilkan jumlah relawan saat ini
  - Progress bar berdasarkan target relawan
- ⚡ **Realtime Update**
  - Data event dan pendaftaran tersinkronisasi secara realtime menggunakan Cloud Firestore

---

## 🛠️ Teknologi yang Digunakan

- **Android (Java)**
- **Firebase Authentication**
- **Cloud Firestore**
- **Glide** (image loading)
- **XML Layout** (UI Android)

---

## 🧩 Arsitektur & Struktur Data

### Struktur Firestore (sederhana)
- events (collection)
- eventId (document)
- nama
- penyelenggara
- lokasi
- targetRelawan
- currentRelawan
- registrations (sub-collection)
- userId (document)
-  userId
- registeredAt
  
![file link](firebase.txt)

Pendekatan ini memastikan:
- Setiap user hanya bisa mendaftar satu kali per event
- Jumlah relawan terupdate secara konsisten menggunakan transaction

---

## 📱 Alur Penggunaan Aplikasi

1. User melakukan login / registrasi
2. User melihat daftar event yang tersedia
3. User memilih event untuk melihat detail
4. User menekan tombol **Daftar**
5. Sistem:
   - Mengecek apakah user sudah terdaftar
   - Menyimpan data pendaftaran
   - Mengupdate jumlah relawan
6. Progress event diperbarui secara otomatis

---

## 🎯 Tujuan Pengembangan

Aplikasi TanamUbah dikembangkan sebagai proyek pembelajaran untuk:
- Menerapkan konsep **mobile development Android**
- Mengintegrasikan **Firebase sebagai backend**
- Membangun sistem pendaftaran event yang aman dan konsisten
- Mendukung gerakan peduli lingkungan melalui teknologi

---

## 👩‍💻 Developer

**Baysarah & Cindy Novitri**  
Mahasiswa Manajemen Informatika, Universitas Syiah Kuala

---

## 📌 Catatan

Proyek ini masih dapat dikembangkan lebih lanjut, seperti:
- Fitur riwayat event yang diikuti user
- Dashboard statistik untuk admin
- Notifikasi event
- Integrasi peta lokasi event

---

> 🌱 *“Aksi lokal Dampak Global”*
