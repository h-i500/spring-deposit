
# ミニマム銀行アプリ（普通預金 / 定期預金）

Spring Boot / PostgreSQL / Docker Compose で動作する、最小構成の銀行アプリサンプルです。  
**普通預金（Savings Service）** と **定期預金（Time Deposit Service）** をそれぞれ独立した Spring Boot アプリとして実装し、専用の PostgreSQL DB に接続します。

- 各サービスは独立デプロイ可能（マイクロサービス風）
- DB はサービスごとに分離
- REST API で操作可能
  - Savings: 口座作成 / 残高参照 / 入金 / 出金
  - Time Deposit: 申込 / 照会 / 満期解約（単利計算）

---

## 構成

```

spring-deposit/
├── docker-compose.yml
├── savings-service/         # 普通預金サービス
│   ├── src/main/java/...    # Spring Boot コード
│   └── pom.xml
├── time-deposit-service/    # 定期預金サービス
│   ├── src/main/java/...    # Spring Boot コード
│   └── pom.xml

````

- `savings-db` (PostgreSQL, ポート 5433)
- `timedeposit-db` (PostgreSQL, ポート 5432)
- `savings-service` (Spring Boot, ポート 8081)
- `time-deposit-service` (Spring Boot, ポート 8082)

---

## セットアップ

### 1. ビルド
```bash
docker compose build --no-cache
````

### 2. 起動

```bash
docker compose up -d
```

### 3. コンテナ確認

```bash
docker ps
```

---

## API

### 普通預金（Savings Service, :8081）

#### 口座作成

```bash
curl -s -X POST http://localhost:8081/accounts \
  -H "Content-Type: application/json" \
  -d '{"owner":"Taro"}'
```

**レスポンス例**

```json
{
  "id": "a1b2c3d4",
  "owner": "Taro",
  "balance": 0.00
}
```

---

#### 入金

```bash
curl -s -X POST http://localhost:8081/accounts/a1b2c3d4/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount":1000}'
```

**レスポンス例**

```json
{
  "id": "a1b2c3d4",
  "owner": "Taro",
  "balance": 1000.00
}
```

---

#### 出金

```bash
curl -s -X POST http://localhost:8081/accounts/a1b2c3d4/withdraw \
  -H "Content-Type: application/json" \
  -d '{"amount":500}'
```

**レスポンス例**

```json
{
  "id": "a1b2c3d4",
  "owner": "Taro",
  "balance": 500.00
}
```

---

#### 残高確認

```bash
curl -s http://localhost:8081/accounts/a1b2c3d4
```

**レスポンス例**

```json
{
  "id": "a1b2c3d4",
  "owner": "Taro",
  "balance": 500.00
}
```

---

### 定期預金（Time Deposit Service, :8082）

#### 定期預金申込

```bash
curl -s -X POST http://localhost:8082/deposits \
  -H "Content-Type: application/json" \
  -d '{"owner":"Hanako","principal":10000,"annualRate":0.015,"termDays":30}'
```

**レスポンス例**

```json
{
  "id": "fff23561-cbfe-4023-bcda-34b54e2b3dc1",
  "owner": "Hanako",
  "principal": 10000.00,
  "annualRate": 0.015,
  "termDays": 30,
  "startAt": "2025-08-17T09:33:36.677357Z",
  "maturityDate": "2025-09-16T09:33:36.677357Z",
  "status": "OPEN"
}
```

---

#### 照会

```bash
curl -s http://localhost:8082/deposits/fff23561-cbfe-4023-bcda-34b54e2b3dc1
```

**レスポンス例**

```json
{
  "id": "fff23561-cbfe-4023-bcda-34b54e2b3dc1",
  "owner": "Hanako",
  "principal": 10000.00,
  "annualRate": 0.015,
  "termDays": 30,
  "startAt": "2025-08-17T09:33:36.677357Z",
  "maturityDate": "2025-09-16T09:33:36.677357Z",
  "status": "OPEN"
}
```

---

#### 満期解約

```bash
curl -s -X POST http://localhost:8082/deposits/fff23561-cbfe-4023-bcda-34b54e2b3dc1/close
```

**レスポンス例（満期前）**

```json
{
  "error": "not matured yet"
}
```

**レスポンス例（満期後）**

```json
{
  "id": "fff23561-cbfe-4023-bcda-34b54e2b3dc1",
  "owner": "Hanako",
  "principal": 10000.00,
  "annualRate": 0.015,
  "termDays": 30,
  "startAt": "2025-08-17T09:33:36.677357Z",
  "maturityDate": "2025-09-16T09:33:36.677357Z",
  "status": "CLOSED",
  "payout": 10012.33
}
```

---

## 注意点

* 各 DB は **コンテナ破棄時に永続化ボリュームが削除される**ため、テスト用に適しています。
* 本番利用は想定していません。認証・セキュリティ・マイグレーション管理等は未実装です。
* Java 21 + Spring Boot 3.x を前提にしています。

---

