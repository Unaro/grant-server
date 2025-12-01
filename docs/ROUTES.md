# API Documentation (GrantServer)

Полный список доступных эндпоинтов, форматов запросов и заголовков.
**Base URL:** `http://localhost:8080`

---

## 1. Участники (Participants)

### Регистрация компании
**POST** `/api/participants/register`

Регистрирует нового участника (фирму).

**Request Body:**
```json
{
  "firmName": "ООО Рога и Копыта",
  "manager": {
    "firstName": "Иван",
    "lastName": "Иванов"
  },
  "login": "firm_user",
  "password": "secure_password"
}
````

**Response (200 OK):**

```json
{
  "responseCode": 200,
  "responseData": {
    "id": 1,
    "firmName": "ООО Рога и Копыта",
    "login": "firm_user",
    "manager": { ... }
  }
}
```

### Вход (Login)

**POST** `/api/participants/login`

**Request Body:**

```json
{
  "login": "firm_user",
  "password": "secure_password"
}
```

**Response (200 OK):**

```json
{
  "responseCode": 200,
  "responseData": {
    "token": "d290f1ee-6c54-4b01-90e6-d701748f0851"
  }
}
```

-----

## 2\. Эксперты (Experts)

### Регистрация эксперта

**POST** `/api/experts/register`

**Request Body:**

```json
{
  "firstName": "Петр",
  "lastName": "Петров",
  "fields": ["IT", "Биология"],
  "login": "expert_petr",
  "password": "secure_password"
}
```

**Response (200 OK):**

```json
{
  "responseCode": 200,
  "responseData": {
    "id": 1,
    "firstName": "Петр",
    "fields": ["IT", "Биология"],
    "login": "expert_petr"
  }
}
```

### Вход (Login)

**POST** `/api/experts/login`

**Request Body:**

```json
{
  "login": "expert_petr",
  "password": "secure_password"
}
```

**Response (200 OK):** (Возвращает токен)

-----

## 3\. Заявки на гранты (Applications)

### Создать заявку

**POST** `/api/applications`

**Headers:**

  * `X-User-Id`: ID участника (обязательно для тестов, имитация токена)

**Request Body:**

```json
{
  "title": "Разработка ИИ",
  "description": "Нейросеть для анализа данных",
  "fields": ["IT", "Математика"],
  "requestedSum": 500000
}
```

**Response (200 OK):**

```json
{
  "responseCode": 200,
  "responseData": {
    "id": 10,
    "title": "Разработка ИИ",
    "status": "ACTIVE",
    "ownerId": 1
  }
}
```

### Получить список всех заявок

**GET** `/api/applications`

**Response (200 OK):**
Массив объектов заявок.

-----

## 4\. Оценки (Evaluations)

### Поставить оценку

**POST** `/api/evaluations`

**Request Body:**

```json
{
  "applicationId": 10,
  "expertId": 5,
  "score": 5
}
```

### Изменить оценку

**PUT** `/api/evaluations/{id}`
*(где {id} — ID существующей оценки)*

**Request Body:**

```json
{
  "score": 3,
  "applicationId": 10,
  "expertId": 5
}
```

### Удалить оценку

**DELETE** `/api/evaluations/{id}`

-----

## 5\. Распределение фонда (Grant Fund)

### Рассчитать победителей

**POST** `/api/grants/compute`

Запускает алгоритм: фильтрует по порогу, сортирует по стратегии, распределяет бюджет.

**Request Body:**

```json
{
  "fund": 1000000,
  "threshold": 3.5
}
```

**Response (200 OK):**

```json
{
  "responseCode": 200,
  "responseData": {
    "winners": [
      {
        "applicationId": 10,
        "title": "Разработка ИИ",
        "averageScore": 5.0,
        "givenAmount": 500000
      }
    ],
    "remainingFund": 500000
  }
}
```

-----

## 6\. Служебные (System)

### Проверка статуса сервера

**GET** `/api/health`

**Response:**

```json
{
  "status": "OK",
  "server": "GrantServer v1.0"
}
```