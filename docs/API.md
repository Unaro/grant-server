# API спецификация проекта GrantServer

Документ содержит все публичные REST-эндпоинты, их структуру входных/выходных данных, и подробные образцы DTO-сущностей.

***

## Эндпоинты

### 1. Регистрация и авторизация

| HTTP | URL | Описание | Тело запроса | Пример ответа |
| :-- | :-- | :-- | :-- | :-- |
| POST | /api/participants/register | Регистрация фирмы | ParticipantRegisterDTO | ServerResponseDTO |
| POST | /api/experts/register | Регистрация эксперта | ExpertRegisterDTO | ServerResponseDTO |
| POST | /api/participants/login | Вход участника | AuthRequestDTO | AuthResponseDTO |
| POST | /api/experts/login | Вход эксперта | AuthRequestDTO | AuthResponseDTO |
| POST | /api/participants/logout | Выход участника | — | ServerResponseDTO |
| POST | /api/experts/logout | Выход эксперта | — | ServerResponseDTO |
| DELETE | /api/participants/{id} | Удалить участника | — | ServerResponseDTO |
| DELETE | /api/experts/{id} | Удалить эксперта | — | ServerResponseDTO |


***

### 2. Работа с заявками

| HTTP | URL | Описание | Тело запроса | Пример ответа |
| :-- | :-- | :-- | :-- | :-- |
| POST | /api/applications | Добавить заявку на грант | GrantApplicationDTO | ServerResponseDTO |
| GET | /api/applications | Получить список заявок | GrantApplicationFilterDTO (query params/JSON) | ServerResponseDTO (массив заявок) |
| DELETE | /api/applications/{id} | Отменить заявку | — | ServerResponseDTO |


***

### 3. Оценка заявок экспертами

| HTTP | URL | Описание | Тело запроса | Пример ответа |
| :-- | :-- | :-- | :-- | :-- |
| POST | /api/evaluations | Поставить оценку | EvaluationDTO | ServerResponseDTO |
| PUT | /api/evaluations/{id} | Изменить оценку | EvaluationDTO | ServerResponseDTO |
| DELETE | /api/evaluations/{id} | Удалить оценку | — | ServerResponseDTO |
| GET | /api/applications/by-fields | Заявки по направлениям | { "fields": ["математика"] } | ServerResponseDTO (массив заявок) |
| GET | /api/applications/by-expert/{expertId} | Заявки, оценённые экспертом | — | ServerResponseDTO (массив заявок) |


***

### 4. Итоговый расчёт и распределение грантов

| HTTP | URL | Описание | Тело запроса | Пример ответа |
| :-- | :-- | :-- | :-- | :-- |
| POST | /api/grants/compute | Подвести итоги конкурса | GrantFundRequestDTO | ServerResponseDTO (результаты распределения) |


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
  "token": "jwt-token-value"
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
  "status": "ACTIVE" // ACTIVE, CANCELLED
}
```


### GrantApplicationFilterDTO

```json
{
  "ownerId": 1,
  "fields": ["информатика"],
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
  "threshold": 3
}
```


### ServerResponseDTO

**Успех:**

```json
{
  "responseCode": 200,
  "responseData": { /* специфичный DTO, массив, информация */ }
}
```

**Ошибка:**

```json
{
  "responseCode": 400,
  "responseData": "Invalid request data"
}
```


***

## Примеры

### Регистрация участника

**POST /api/participants/register**
_Тело запроса:_

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

_Ответ:_

```json
{
  "responseCode": 200,
  "responseData": {
    "id": 1,
    "firmName": "ООО Идея",
    "manager": {
      "firstName": "Иван",
      "lastName": "Петров"
    },
    "login": "firm_idea"
  }
}
```


***

### Добавление заявки

**POST /api/applications**
_Тело запроса:_

```json
{
  "title": "Нейронная сеть для анализа данных",
  "description": "ML и аналитика",
  "fields": ["математика", "информатика"],
  "requestedSum": 700000
}
```

_Ответ:_

```json
{
  "responseCode": 200,
  "responseData": {
    "id": 101,
    "title": "Нейронная сеть для анализа данных",
    "fields": ["математика", "информатика"],
    "requestedSum": 700000,
    "ownerId": 1,
    "status": "ACTIVE"
  }
}
```


***

### Оценка заявки

**POST /api/evaluations**
_Тело запроса:_

```json
{
  "applicationId": 101,
  "expertId": 2,
  "score": 4
}
```

_Ответ:_

```json
{
  "responseCode": 200,
  "responseData": {
    "id": 201,
    "applicationId": 101,
    "expertId": 2,
    "score": 4
  }
}
```


***

## Примечания

- Все логины уникальны, двойная регистрация недопустима
- Удаление участника или эксперта полностью уничтожает связанные заявки/оценки
- Оценки можно изменять или удалять, заявки после создания изменению не подлежат (можно только отменить)
- Итоговое подведение результатов автоматизируется отдельной сервисной функцией
- Каждому эндпоинту сопоставлены соответствующие тесты (JUnit), что отражено в структуре проекта
