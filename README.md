# Alight Motion Premium Generator - Android App

Aplikasi Android untuk mengenerate premium Alight Motion menggunakan API RafaelXD.

## Fitur

- ✅ Kirim Magic Link ke email
- ✅ Verifikasi akun menggunakan raw link dari email
- ✅ Menampilkan hasil dalam format JSON (oobCode, idToken, dll)
- ✅ Copy hasil ke clipboard
- ✅ UI yang bersih dan mudah digunakan
- ✅ Support Android 7.0+ (API 24)

## Teknologi yang digunakan

- Kotlin
- Retrofit + OkHttp
- Coroutines
- Material Design 3
- ViewBinding

## Cara Build

### 1. Buka di Android Studio
1. Buka Android Studio
2. Pilih **Open an existing project**
3. Pilih folder `alight_motion_android`
4. Tunggu Gradle sync selesai

### 2. Build APK
- **Debug APK**: `Build > Build Bundle(s) / APK(s) > Build APK(s)`
- **Release APK**: `Build > Generate Signed Bundle / APK`

### 3. Jalankan di Emulator / Device
- Connect device atau buka emulator
- Klik tombol **Run** (atau Shift+F10)

## Cara Penggunaan

1. Buka aplikasi
2. Masukkan email di kolom pertama
3. Tekan tombol **Kirim Magic Link**
4. Cek email Anda (termasuk folder Spam)
5. Buka email dari Alight Motion
6. Copy link (tekan-tahan tombol Login → Salin URL)
7. Paste link tersebut di kolom **Raw Link**
8. Tekan tombol **Verifikasi Akun**
9. Hasil akan muncul di bawah (termasuk idToken & oobCode)

## API yang digunakan

Base URL: `https://am.rafaelxd.my.id`

Endpoints:
- `POST /api/send` → Mengirim magic link
- `POST /api/verify` → Verifikasi akun

## Catatan

- Aplikasi ini hanya untuk keperluan edukasi dan testing.
- Pastikan email yang digunakan valid.
- Durasi premium yang dihasilkan: **1 Tahun**

## Developer Asli

Original Node.js scraper oleh: **RafaelXD** (t.me/hazeloffc)

## Lisensi

Free to use & modify for personal use.

---

**Made with ❤️ for Android**