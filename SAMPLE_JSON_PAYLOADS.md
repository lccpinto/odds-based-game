# Sample JSON Payloads for Testing API

Below are sample JSON requests and expected responses for various endpoints of the Odds Based Game application.
You can also find and import a Bruno collection here: [bruno-collection/odds-based-game](bruno-collection/odds-based-game)
---

## 1. Player Registration

**Endpoint:** `POST /players/register`

**Request:**

```json
{
  "name": "John",
  "surname": "Doe",
  "username": "johnDoe"
}
```

**Expected Response:**

```json
{
  "id": 1,
  "name": "John",
  "surname": "Doe",
  "username": "johnDoe",
  "walletBalance": 1000
}
```

---

## 2. Place a Bet (using username)

**Endpoint:** `POST /bets`

**Request:**

```json
{
  "username": "johnDoe",
  "betNumber": 5,
  "betAmount": 100
}
```

**Expected Response (Example – WIN):**

```json
{
  "id": 1,
  "player": {
    "id": 1,
    "name": "John",
    "surname": "Doe",
    "username": "johnDoe",
    "walletBalance": 1900
  },
  "betNumber": 5,
  "betAmount": 100,
  "generatedNumber": 5,
  "winnings": 1000,
  "result": "WIN",
  "timestamp": "2025-03-03T10:15:30"
}
```

---

## 3. Retrieve a Bet

**Endpoint:** `GET /bets/1`

**Expected Response:**

```json
{
  "id": 1,
  "player": {
    "id": 1,
    "name": "John",
    "surname": "Doe",
    "username": "johnDoe",
    "walletBalance": 1900
  },
  "betNumber": 5,
  "betAmount": 100,
  "generatedNumber": 5,
  "winnings": 1000,
  "result": "WIN",
  "timestamp": "2025-03-03T10:15:30"
}
```

---

## 4. Retrieve Wallet Transactions for a Player (Paginated)

**Endpoint:** `GET /transactions/player/johnDoe?size=2&page=0`

**Expected Response:**

```json
{
  "content": [
    {
      "id": 1,
      "player": {
        "id": 1,
        "name": "John",
        "surname": "Doe",
        "username": "johnDoe",
        "walletBalance": 1900
      },
      "amount": -100,
      "transactionType": "BET_DEDUCTION",
      "timestamp": "2025-03-03T10:15:00"
    },
    {
      "id": 2,
      "player": {
        "id": 1,
        "name": "John",
        "surname": "Doe",
        "username": "johnDoe",
        "walletBalance": 1900
      },
      "amount": 1000,
      "transactionType": "WINNING_CREDIT",
      "timestamp": "2025-03-03T10:16:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 2,
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 2,
  "totalPages": 1,
  "size": 2,
  "number": 0,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "first": true,
  "numberOfElements": 2,
  "empty": false
}
```

---

## 5. Retrieve Bets for a Player (Paginated)

**Endpoint:** `GET /bets/player/johnDoe?page=0&size=2`

**Expected Response:**

```json
{
  "content": [
    {
      "id": 1,
      "player": {
        "id": 1,
        "name": "John",
        "surname": "Doe",
        "username": "johnDoe",
        "walletBalance": 1900
      },
      "betNumber": 5,
      "betAmount": 100,
      "generatedNumber": 5,
      "winnings": 1000,
      "result": "WIN",
      "timestamp": "2025-03-03T10:15:30"
    },
    {
      "id": 2,
      "player": {
        "id": 1,
        "name": "John",
        "surname": "Doe",
        "username": "johnDoe",
        "walletBalance": 1900
      },
      "betNumber": 3,
      "betAmount": 50,
      "generatedNumber": 7,
      "winnings": 0,
      "result": "LOSE",
      "timestamp": "2025-03-03T10:20:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 2,
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 2,
  "totalPages": 1,
  "size": 2,
  "number": 0,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "first": true,
  "numberOfElements": 2,
  "empty": false
}
```

---

## 6. Retrieve Leaderboard

**Endpoint:** `GET /leaderboard?limit=10`

**Expected Response:**

```json
[
  {
    "playerId": 1,
    "username": "johnDoe",
    "totalWinnings": 1000
  },
  {
    "playerId": 2,
    "username": "janeDoe",
    "totalWinnings": 800
  }
]
```

---

## 7. Retrieve Game Properties

**Endpoint:** `GET /game-properties`

**Expected Response:**

```json
{
  "player": {
    "initialBalance": 1000
  },
  "bet": {
    "minNumber": 1,
    "maxNumber": 10
  },
  "winningRules": {
    "exactMatch": 10,
    "oneOff": 5,
    "twoOff": 0.5
  },
  "leaderboard": {
    "maxLimit": 100
  }
}
```
