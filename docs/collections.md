
---

# 📚 User Book Collection API

API ini memungkinkan user untuk mengelola dan melihat koleksi buku mereka sendiri maupun koleksi user lain (khususnya admin di masa depan). Semua endpoint membutuhkan role `USER`.

---

## ✅ Add Book to Authenticated User Collection

* **Method**: `POST`

* **URL**: `/users/me/books/{bookId}`

* **Authorization**: Required (`USER`)

* **Path Variable**:

  * `bookId` (Long): ID buku yang ingin ditambahkan ke koleksi user saat ini

* **Response**:

```json
{
  "data": "Buku berhasil ditambahkan ke koleksi"
}
```

* **Status**: `200 OK`

---

## ❌ Remove Book from Authenticated User Collection

* **Method**: `DELETE`

* **URL**: `/users/me/books/{bookId}`

* **Authorization**: Required (`USER`)

* **Path Variable**:

  * `bookId` (Long): ID buku yang ingin dihapus dari koleksi user saat ini

* **Response**:

```json
{
  "data": "Buku berhasil dihapus dari koleksi"
}
```

* **Status**: `200 OK`

---

## 📖 Get All Books for Specific User

* **Method**: `GET`

* **URL**: `/users/{userId}/books`

* **Authorization**: Required (`USER`)

* **Path Variable**:

  * `userId` (UUID): ID user yang ingin dilihat koleksi bukunya

* **Response**:

```json
{
  "data": [
    {
      "id": 1,
      "title": "Judul Buku",
      "description": "Deskripsi singkat",
      "rating": 4,
      "bookPicture": "https://link.gambar.com",
      "publisherName": "Penerbit Gaung"
    }
  ]
}
```

* **Status**: `200 OK`

---

## 🔢 Get Total Book Count for Specific User

* **Method**: `GET`

* **URL**: `/users/{userId}/books/count`

* **Authorization**: Required (`USER`)

* **Path Variable**:

  * `userId` (UUID): ID user yang ingin dihitung jumlah koleksi bukunya

* **Response**:

```json
{
  "data": 5
}
```

* **Status**: `200 OK`

---