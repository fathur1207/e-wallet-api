# E-Wallet REST API (Spring Boot)

REST API sederhana untuk sistem E-Wallet dengan dua fitur utama:

1. **Cek Saldo** — `GET /api/wallet/balance/{userId}`
2. **Transfer Saldo** — `POST /api/wallet/transfer`

Dibangun menggunakan **Spring Boot 3**, **Spring Data JPA**, dan **MySQL**.

---

## 1. Prasyarat

| Tool          | Versi          | Cek versi              |
|---------------|----------------|-------------------------|
| Java (JDK)    | 17             | `java -version`         |
| Maven         | 3.8+           | `mvn -version` |
| MySQL Server  | 8.0            | `mysql --version`       |

---

## 2. Struktur Project

```
e-wallet-api/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/example/ewallet/
    │   │   ├── EWalletApiApplication.java     # Entry point aplikasi
    │   │   ├── controller/
    │   │   │   └── WalletController.java      # Endpoint REST (cek saldo & transfer)
    │   │   ├── service/
    │   │   │   └── WalletService.java         # Business logic (transaksi & locking)
    │   │   ├── repository/
    │   │   │   ├── UserRepository.java
    │   │   │   └── TransactionLogRepository.java
    │   │   ├── model/
    │   │   │   ├── User.java                  # Entity user/wallet
    │   │   │   └── TransactionLog.java        # Entity riwayat transaksi
    │   │   ├── dto/
    │   │   │   ├── TransferRequest.java
    │   │   │   ├── BalanceResponse.java
    │   │   │   ├── TransferResponse.java
    │   │   │   └── ErrorResponse.java
    │   │   └── exception/
    │   │       ├── UserNotFoundException.java
    │   │       ├── InsufficientBalanceException.java
    │   │       ├── InvalidTransferException.java
    │   │       └── GlobalExceptionHandler.java
    │   └── resources/
    │       ├── application.properties         # Konfigurasi datasource & JPA
    │       └── data.sql                       # Seeding data user awal
    └── test/
```

## 3. Setup Database (MySQL)

Hibernate (`ddl-auto=update`) akan otomatis membuat tabel `users` dan `transaction_logs` saat aplikasi pertama kali dijalankan. Data awal (seed user) otomatis diisi lewat `data.sql`.

 **Create database** (opsional, `createDatabaseIfNotExist=true` sudah diset):

```sql
CREATE DATABASE IF NOT EXISTS ewallet_db;
```

Jalankan lewat MySQL CLI:

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ewallet_db;"
```

### Konfigurasi Koneksi

Edit file `src/main/resources/application.properties` sesuai kredensial MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ewallet_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=pwd

#Load data.sql
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always

```

Ganti `root` / `pwd` dengan username dan password MySQL.

---

## 4. Data Awal (Seeding)

Aplikasi ini **tidak memiliki endpoint registrasi/pembuatan user**. Data user dianggap sudah tersedia di sistem dan otomatis diisi melalui `data.sql` saat aplikasi start:

| id  | name           | email             | balance      |
|-----|----------------|-------------------|--------------|
| 1   | Andi Wijaya    | andi@example.com  | 1.000.000,00 |
| 2   | Budi Santoso   | budi@example.com  | 500.000,00   |
| 3   | Citra Lestari  | citra@example.com | 750.000,00   |

Script seeding memakai pengecekan `NOT EXISTS`, sehingga aman dijalankan berulang kali (restart aplikasi) tanpa menyebabkan data duplikat atau saldo ter-reset ulang jika baris sudah ada.

---

## 5. Menjalankan Aplikasi

### Opsi A — Menggunakan Maven Wrapper

Untuk meng-generate Maven Wrapper di project, **Jalankan:** 
```bash
cd e-wallet-api
mvn -N io.takari:maven:wrapper
```

**Lalu:**
```bash
./mvnw spring-boot:run
```

Windows:
```cmd
cd e-wallet-api
mvnw.cmd spring-boot:run
```

### Opsi B — Menggunakan Maven yang terinstall global

```bash
cd e-wallet-api
mvn spring-boot:run
```

### Opsi C — Build JAR lalu jalankan

```bash
mvn clean package
java -jar target/e-wallet-api.jar
```

Jika berhasil, aplikasi akan berjalan di:

```
http://localhost:8080
```
---

## 6. Dokumentasi Endpoint

### 6.1 Cek Saldo

```
GET /api/wallet/balance/{userId}
```

**Contoh request:**

```bash
curl -X GET http://localhost:8080/api/wallet/balance/1
```

**Contoh response sukses (200 OK):**

```json
{
  "userId": 1,
  "name": "Andi Wijaya",
  "balance": 1000000.00
}
```

**Contoh response gagal — user tidak ditemukan (404 Not Found):**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "User dengan id 99 tidak ditemukan",
  "timestamp": "2026-09-15T10:00:00"
}
```

---

### 6.2 Transfer Saldo

```
POST /api/wallet/transfer
Content-Type: application/json
```

**Request body:**

```json
{
  "senderId": 1,
  "receiverId": 2,
  "amount": 50000
}
```

**Contoh request (curl):**

```bash
curl -X POST http://localhost:8080/api/wallet/transfer \
  -H "Content-Type: application/json" \
  -d '{"senderId": 1, "receiverId": 2, "amount": 50000}'
```

**Contoh response sukses (200 OK):**

```json
{
  "transactionId": 1,
  "senderId": 1,
  "receiverId": 2,
  "amount": 50000,
  "timestamp": "2026-09-15T10:05:00",
  "message": "Transfer berhasil"
}
```

**Contoh response gagal — saldo tidak cukup (422 Unprocessable Entity):**

```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Saldo user dengan id 1 tidak mencukupi untuk melakukan transfer",
  "timestamp": "2026-09-15T10:05:00"
}
```

**Contoh response gagal — validasi input (400 Bad Request):**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "amount: amount harus lebih besar dari 0",
  "timestamp": "2026-09-15T10:05:00"
}
```

**Kemungkinan status error lain:**

| Kondisi                                   | HTTP Status | 
|--------------------------------------------|-------------|
| `senderId` / `receiverId` tidak ditemukan   | 404 Not Found |
| `senderId` = `receiverId`                   | 400 Bad Request |
| `amount` <= 0 atau kosong                   | 400 Bad Request |
| Saldo pengirim tidak mencukupi              | 422 Unprocessable Entity |

---

## 7. Detail Implementasi

- **Konsistensi data (ACID):** Proses transfer dibungkus dalam satu `@Transactional`, sehingga pengurangan saldo pengirim dan penambahan saldo penerima terjadi secara atomic (all-or-nothing).
- **Concurrency safety:** Baris `users` yang terlibat transfer dikunci menggunakan `SELECT ... FOR UPDATE` (`PESSIMISTIC_WRITE`) dengan urutan `id` yang konsisten (id lebih kecil dikunci lebih dulu) untuk mencegah race condition maupun deadlock saat ada beberapa transfer berjalan bersamaan.
- **Audit trail:** Setiap transfer yang berhasil dicatat ke tabel `transaction_logs` (senderId, receiverId, amount, waktu transaksi).
- **Validasi input:** Menggunakan Bean Validation (`@NotNull`, `@DecimalMin`) pada `TransferRequest`.
- **Error handling terpusat:** `GlobalExceptionHandler` (`@RestControllerAdvice`) memastikan semua error dikembalikan dalam format JSON yang konsisten beserta HTTP status code yang sesuai.

---
