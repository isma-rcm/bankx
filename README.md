1.- Levantar base de datos de mongo con docker:

    docker run -d --name mongo-bankx -p 27017:27017 mongo:6

2.-Clonar el repositorio y ejecutar la aplicación en el puerto 8084:

    mvn spring-boot:run
3.- Pruebas con Postman: 

-Crear Transaccion (OK)
```bash

curl -X POST http://localhost:8084/api/transactions \
-H "Content-Type: application/json" \
-d '{
  "accountNumber": "001-0001",
  "type": "DEBIT",
  "amount": 100
}'
```

<img width="508" height="322" alt="Captura de pantalla_20251218_000021" src="https://github.com/user-attachments/assets/f772dc73-3754-46b4-9294-3b4ef7c6d719" />

- Crear Transacción (rechazo por riesgo) 

```bash
  curl -X POST http://localhost:8084/api/transactions \
-H "Content-Type: application/json" \
-d '{
  "accountNumber": "001-0001",
  "type": "DEBIT",
  "amount": 2000
}'

```
<img width="508" height="322" alt="Captura de pantalla_20251218_000431" src="https://github.com/user-attachments/assets/c12db13c-5c5d-433d-9d9d-4b008bcd5e46" />

- Crear Transacción (rechazo por fondos insuficientes)
  
```bash
curl -X POST http://localhost:8084/api/transactions \
-H "Content-Type: application/json" \
-d '{
  "accountNumber": "001-0002",
  "type": "DEBIT",
  "amount": 900
}'
```

<img width="508" height="322" alt="Captura de pantalla_20251218_000810" src="https://github.com/user-attachments/assets/dfe4ae37-8548-4511-b12a-f253458dcb4b" />

- Listar Transacciones por Cuenta

```bash
curl -X GET http://localhost:8084/api/transactions?accountNumber=001-0001
```
<img width="508" height="322" alt="Captura de pantalla_20251218_000919" src="https://github.com/user-attachments/assets/c9a4c6cf-b26f-4b72-80d6-9b0bf37a4dd8" />

- Stream de transacciones (SSE)

  ```bash
  curl -X GET http://localhost:8084/api/stream/transactions
  ```
<img width="508" height="322" alt="Captura de pantalla_20251218_001351" src="https://github.com/user-attachments/assets/c3cfedc4-80eb-4bb5-91cd-db76815a016c" />

4- mvn test -Dtest=RiskServiceTest

```bash
mvn test -Dtest=RiskServiceTest
```
