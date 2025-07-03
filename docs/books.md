
---

# 📚 Book API

API untuk membuat, mencari, dan melihat detail buku. Beberapa endpoint dapat diakses publik, sementara sebagian memerlukan role `USER`.

---

## ➕ Create Book

* **Method**: `POST`
* **URL**: `/books`
* **Authorization**: Required (`USER`)
* **Request Body**:

```json
{
  "isbn": "978-602-03-1234-1",
  "title": "Test",
  "synopsis": "Panduan lengkap belajar Spring Boot.",
  "bookPicture": "https://example.com/springboot.jpg",
  "pages": 320,
  "publishedYear": 2024,
  "language": "Indonesia",
  "publisherName": "Penerbit Gaung",
  "authorNames": ["Fransisca Ellya", "Budi Setiawan"],
  "genreIds": [1, 2]
}
```

* **Response**:

```json
{
  "data": "OK"
}
```

* **Status**: `201 Created`

---

## 📄 Get All Books

* **Method**: `GET`
* **URL**: `/books`
* **Authorization**: Not required (public)
* **Response**:

```json
{
  "data": [
    {
      "id": 1,
      "title": "Belajar Spring Boot",
      "isbn": "978-602-03-1234-5",
      "rating": 0.0,
      "bookPicture": "https://example.com/springboot.jpg",
      "publisherName": "Penerbit Gaung"
    }
  ]
}
```

* **Status**: `200 OK`

---

## 🔍 Get Book by ID

* **Method**: `GET`

* **URL**: `/books/{id}`

* **Authorization**: Not required (public)

* **Path Variable**:

    * `id` (Long): ID dari buku yang ingin dilihat detailnya

* **Response**:

```json
{
  "data": {
    "id": 1,
    "title": "Judul Buku",
    "isbn": "978-1234567890",
    "author": "Nama Penulis",
    "genre": "Fiksi",
    "publisher": {
      "id": "uuid-publisher",
      "name": "Nama Publisher"
    },
    "description": "Deskripsi lengkap",
    "rating": 5,
    "bookPicture": "https://link.com"
  }
}
```

* **Status**: `200 OK`

---

## 🔎 Advanced Search for Books

* **Method**: `GET`

* **URL**: `/books/search`

* **Authorization**: Not required (public)

* **Query Parameters** *(opsional semuanya)*:

    * `title` — Judul buku
    * `isbn` — Nomor ISBN
    * `author` — Nama penulis
    * `genre` — Genre buku
    * `publisher` — Nama penerbit

* **Contoh Request**:

```
GET /books/search?title=java&genre=programming
```

* **Response**:

```json
{
  "data": [
    {
      "id": 5,
      "title": "Java Programming 101",
      "description": "Belajar Java dari dasar",
      "rating": 4,
      "bookPicture": "https://gambar.com/java.jpg",
      "publisherName": "Penerbit Gaung"
    }
  ]
}
```

* **Status**: `200 OK`
* **Catatan**: Kosongkan string (misal: `title=`) akan diperlakukan sebagai `null` alias tidak dipakai dalam filter pencarian.

---

## 🛡️ Akses Endpoint

| Endpoint        | Method | Akses  |
| --------------- | ------ | ------ |
| `/books`        | POST   | `USER` |
| `/books`        | GET    | Publik |
| `/books/{id}`   | GET    | Publik |
| `/books/search` | GET    | Publik |

---