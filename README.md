承知しました。
README.md に「内部振替（普通→定期）」および「解約時に普通預金へ自動振替」の仕様を追記した内容を提案します。

````markdown
# Spring Deposit Bank App

このプロジェクトは **普通預金サービス (savings-service)** と **定期預金サービス (time-deposit-service)** の 2 つのマイクロサービスで構成されるサンプル銀行アプリです。  
Docker Compose により PostgreSQL とともに起動します。

---

## 機能一覧

### 普通預金サービス (savings-service)
- 口座の作成
- 入金
- 残高照会

### 定期預金サービス (time-deposit-service)
- 定期預金の作成
- 満期日・利息計算
- 解約

### 内部振替機能
- **普通→定期**  
  - 普通預金から定期預金作成時に元本を自動引き落とし  
  - リクエストで `fromAccountId` を指定  
  - 普通口座残高不足時はエラー応答  

- **定期→普通（解約時）**  
  - 満期解約時、解約金額（元本＋利息）を普通預金へ自動振替  
  - リクエストで `toAccountId` を指定  
  - 処理成功後、定期預金のステータスは `CLOSED` に更新される  

---

## API サンプル

### 普通預金の作成
```bash
curl -s -X POST http://localhost:8081/accounts \
  -H "Content-Type: application/json" \
  -d '{"owner":"Taro"}'
````

**レスポンス例**

```json
{
  "createdAt": "2025-08-17T11:53:18.686964Z",
  "balance": 0,
  "owner": "Taro",
  "id": "fc8f799b-2ec9-4ce9-b946-c4fd14f5796c"
}
```

---

### 普通→定期への振替を伴う定期預金作成

```bash
curl -s -X POST http://localhost:8082/deposits \
  -H "Content-Type: application/json" \
  -d '{"owner":"Hanako","principal":10000,"annualRate":0.015,"termDays":30,"fromAccountId":"fc8f799b-2ec9-4ce9-b946-c4fd14f5796c"}'
```

**レスポンス例**

```json
{
  "id": "dc8bee77-72b7-4e2c-baaf-aacb6c0a2234",
  "principal": 10000.00,
  "annualRate": 0.015,
  "maturityDate": "2025-09-16T11:56:00.202255Z",
  "status": "OPEN",
  "owner": "Hanako",
  "termDays": 30,
  "startAt": "2025-08-17T11:56:00.202255Z"
}
```

---

### 定期→普通への振替を伴う解約

```bash
curl -s -X POST \
  "http://localhost:8082/deposits/{depositId}/close?toAccountId={accountId}&at=2025-09-16T11:56:00Z"
```

**レスポンス例**

```json
{
  "payout": 10012.33,
  "status": "CLOSED",
  "id": "dc8bee77-72b7-4e2c-baaf-aacb6c0a2234",
  "toAccountId": "fc8f799b-2ec9-4ce9-b946-c4fd14f5796c"
}
```

---

## 動作確認用スクリプト

### `test-transfer.sh`

* 普通口座作成 → 入金
* 定期作成（普通から元本引落し）
* 満期解約（普通口座に振替）
* 各サービスの状態確認

---

## 起動方法

```bash
docker compose build --no-cache
docker compose up -d
```

---

## 利用ポート

* savings-service: `8081`
* time-deposit-service: `8082`
* DB(PostgreSQL): `5432`

```

---

👉 これで **普通→定期** と **定期→普通** の振替仕様が README に追記されました。  

ご希望であれば、この追記を **実行シーケンス図（Mermaid形式）** でも可視化して README に載せられますが、追加しますか？
```
