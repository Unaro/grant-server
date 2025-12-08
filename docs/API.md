# API спецификация проекта GrantServer

Документ содержит все публичные REST-эндпоинты, их структуру входных/выходных данных, и подробные образцы DTO-сущностей.

***

## Аутентификация

Большинство эндпоинтов (кроме регистрации и входа) требуют наличия заголовка авторизации.
Сервер использует механизм **Bearer Token**.

**Заголовок:**
`Authorization: Bearer <your_token_uuid>`

Если заголовок отсутствует или токен невалиден, сервер вернет ошибку `401 Unauthorized`.

***

## Эндпоинты

### 1. Регистрация и авторизация

| HTTP | URL | Описание | Заголовки | Тело запроса | Пример ответа |
| :-- | :-- | :-- | :-- | :-- | :-- |
| POST | /api/participants/register | Регистрация фирмы | — | **ParticipantRegisterDTO** | ServerResponseDTO |
| POST | /api/experts/register | Регистрация эксперта | — | **ExpertRegisterDTO** | ServerResponseDTO |
| POST | /api/participants/login | Вход участника | — | **AuthRequestDTO** | AuthResponseDTO |
| POST | /api/experts/login | Вход эксперта | — | **AuthRequestDTO** | AuthResponseDTO |


***

### 2. Работа с заявками

| HTTP | URL | Описание | Заголовки | Тело запроса | Пример ответа |
| :-- | :-- | :-- | :-- | :-- | :-- |
| POST | /api/applications | Добавить заявку | `Authorization` | **GrantApplicationCreateDTO** | ServerResponseDTO |
| GET | /api/applications | Получить список | `Authorization` | — | ServerResponseDTO (массив) |

*Примечание: Поле `ownerId` берется автоматически из токена участника.*

***

### 3. Оценка заявок экспертами

| HTTP | URL | Описание | Заголовки | Тело запроса | Пример ответа |
| :-- | :-- | :-- | :-- | :-- | :-- |
| POST | /api/evaluations | Поставить оценку | `Authorization` | **EvaluationCreateDTO** | ServerResponseDTO |
| PUT | /api/evaluations/{id} | Изменить оценку | `Authorization` | **EvaluationCreateDTO** | ServerResponseDTO |
| DELETE | /api/evaluations/{id} | Удалить оценку | `Authorization` | — | ServerResponseDTO |

*Примечание: Поле `expertId` берется автоматически из токена эксперта.*

***

### 4. Итоговый расчёт и распределение грантов

| HTTP | URL | Описание | Заголовки | Тело запроса | Пример ответа |
| :-- | :-- | :-- | :-- | :-- | :-- |
| POST | /api/grants/compute | Подвести итоги | `Authorization` | **GrantFundRequestDTO** | ServerResponseDTO (результаты) |


***

## DTO (Data Transfer Object) структуры

### ParticipantRegisterDTO

```json
{
  "firmName": "ООО Идея",
  "manager": {
    "firstName": "Иван",
    "lastName": "Петров"
  },
  "login": "firm_idea",
  "password": "secure_password"
}
```

### ExpertRegisterDTO

```json
{
  "firstName": "Павел",
  "lastName": "Сидоров",
  "fields": ["математика", "информатика"],
  "login": "expert_pavel",
  "password": "secure_password"
}
```

### AuthRequestDTO

```json
{
  "login": "firm_idea",
  "password": "secure_password"
}
```

### AuthResponseDTO

```json
{
  "token": "d290f1ee-6c54-4b01-90e6-d701748f0851"
}
```

### GrantApplicationDTO

```json
{
  "id": 101,
  "title": "Разработка модели искусственного интеллекта",
  "description": "Масштабируемый проект на основе ML",
  "fields": ["математика", "информатика"],
  "requestedSum": 500000,
  "ownerId": 1,
  "status": "ACTIVE"
}
```

### EvaluationDTO

```json
{
  "id": 201,
  "applicationId": 101,
  "expertId": 2,
  "score": 5
}
```

### GrantFundRequestDTO

```json 
{
  "fund": 1500000,
  "threshold": 3.5
}
```

### ServerResponseDTO

Успех:

```json 
{
  "responseCode": 200,
  "responseData": { /* специфичный DTO, массив, информация */ }
}
```

Ошибка:

```json
{
  "responseCode": 400,
  "responseData": "Error message description"
}
```
