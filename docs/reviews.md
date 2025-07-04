# 📚 Review API

## 🔐 Role yang Diizinkan

Semua endpoint pada controller ini hanya bisa diakses oleh user dengan role `USER`.

---

## ➕ Submit Review

* **Method**: `POST`
* **URL**: `/reviews`
* **Authorization**: Required (role: `USER`)
* **Request Body**:

```json
{
  "bookId": 1,
  "rating": 5,
  "message": "Buku yang sangat bagus dan inspiratif!"
}
```

> `userId` akan otomatis di-set dari `@CurrentUser`, tidak perlu disertakan oleh client.

* **Response**:

```json
{
  "data": "OK"
}
```

* **Status**: `201 Created`

---

## ✏️ Update Review

* **Method**: `PATCH`
* **URL**: `/reviews`
* **Authorization**: Required (role: `USER`)
* **Request Body**:

```json
{
  "bookId": 1,
  "rating": 4,
  "message": "Setelah dibaca ulang, ternyata ada bagian yang kurang."
}
```

> Hanya review milik user yang bisa di-update.

* **Response**:

```json
{
  "data": "OK"
}
```

* **Status**: `200 OK`

---

## 📖 Get All Reviews for a Book

* **Method**: `GET`

* **URL**: `/reviews/book/{bookId}`

* **Authorization**: Required (role: `USER`)

* **Response**:

```json
{
  "data": [
    {
      "userId": "fa06d533-19c0-4a20-8a17-eeff93b7aefa",
      "bookId": 3,
      "message": "Setelah dibaca ulang, ternyata ada bagian yang kurang.",
      "rating": 5,
      "createdAt": "2025-07-01T15:30:10.666684Z",
      "updatedAt": "2025-07-01T15:31:55.953311Z"
    }
  ]
}
```

* **Status**: `200 OK`

---

## ❌ Delete Review

* **Method**: `DELETE`

* **URL**: `/reviews/{bookId}`

* **Authorization**: Required (role: `USER`)

* **Response**:

```json
{
  "data": "OK"
}
```

* **Status**: `200 OK`

---