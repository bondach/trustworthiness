1. Запуск
```sh
docker compose up --build -d --force-recreate
```
P.S. Первый запуск может быть долгим, так как docker будет выгружать образы

P.S.2. При изменении кода в проекте trustworthiness также будет происходить пересборка образа, что может
увеличить время запуска окружения

2. Остановка
```sh
docker compose down
```

3. Отправить событие в kafka:
Команда ниже откроет шелл, который на вход будет принимать текст сообщений. По Enter будет происходить отправка сообщений.
Чтобы выйти, нужно нажать Ctrl+C
```sh
docker compose exec kafka kafka-console-producer.sh --topic trustworthiness --bootstrap-server kafka:9092
```

4. Прочитать события из kafka:
Откроет шелл, который покажет события из топика. Чтобы выйти из него, нужно нажать Ctrl+C
```sh
docker compose exec kafka kafka-console-consumer.sh --topic trustworthiness --from-beginning --bootstrap-server kafka:9092
```
