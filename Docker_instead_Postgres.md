

# Использование PostgreSQL через Docker

Настройка PostgreSQL с использованием Docker позволяет избежать установки базы данных на ваш ПК. Следуйте этим шагам для настройки и подключения PostgreSQL к вашему приложению.

---

## Шаг 1. Запуск PostgreSQL в Docker

### Убедитесь, что Docker установлен и работает:
```bash
docker --version
```

Если Docker установлен, выполните следующую команду для создания и запуска контейнера PostgreSQL:

```bash
docker run ^
  --name my-postgres ^
  -e POSTGRES_USER=myuser ^
  -e POSTGRES_PASSWORD=mysecretpassword ^
  -e POSTGRES_DB=spring_hiber ^
  -p 5434:5432 ^
  -d postgres

```
Правильный формат команды в **PowerShell** _(Я использовал это)_:

```bash
docker run --name my-postgres `
  -e POSTGRES_USER=myuser `
  -e POSTGRES_PASSWORD=mysecretpassword `
  -e POSTGRES_DB=spring_hiber `
  -p 5434:5432 `
  -d postgres
```

**Описание параметров команды:**
- `--name my-postgres` — имя контейнера.
- `-e POSTGRES_USER=myuser` — задаёт имя пользователя базы данных.
- `-e POSTGRES_PASSWORD=mysecretpassword` — задаёт пароль пользователя.
- `-e POSTGRES_DB=spring_hiber` — создаёт базу данных с указанным именем.
- `-p 5434:5432` — перенаправляет порт PostgreSQL (5432) на порт вашего ПК (5434).
- `-d` — запускает контейнер в фоновом режиме.

### Проверьте, что контейнер работает:
```bash
docker ps
```

Вы должны увидеть активный контейнер с именем `my-postgres`.

---

## Шаг 2. Настройка `db.properties`

Измените файл `db.properties` следующим образом:

```properties
# PostgreSQL properties
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://localhost:5434/spring_hiber
db.username=myuser
db.password=mysecretpassword

# Hibernate properties
hibernate.show_sql=true
hibernate.hbm2ddl.auto=update
```

Эти настройки должны соответствовать параметрам запуска контейнера.

---

## Шаг 3. Проверка подключения

Убедитесь, что в вашем проекте указана зависимость для PostgreSQL. Если вы используете Maven, добавьте в файл `pom.xml` следующую зависимость:

```xml
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>42.6.0</version>
</dependency>
```

### Запустите приложение
Проверьте логи вашего приложения. Если всё настроено правильно, Hibernate подключится к PostgreSQL в контейнере.

---

## Шаг 4. Управление контейнером

### Остановка контейнера:
```bash
docker stop my-postgres
```

### Запуск контейнера:
```bash
docker start my-postgres
```

### Удаление контейнера:
```bash
docker rm -f my-postgres
```

---

## Шаг 5. Тестирование подключения

Для проверки работы базы данных подключитесь к ней через PostgreSQL-клиент (например, **DBeaver** или **psql**):

```bash
psql -h localhost -p 5434 -U myuser -d spring_hiber
```

Введите пароль `mysecretpassword`.

---

После выполнения всех шагов ваше приложение должно успешно работать с PostgreSQL, запущенной в Docker-контейнере. Если возникнут проблемы, напишите, и я помогу!
```
---

---

---

 

# Управление контейнерами PostgreSQL в Docker

---

## 1. Проверить существующие контейнеры

Для просмотра всех контейнеров (включая остановленные) выполните команду:

```cmd
docker ps -a
```

Найдите контейнер с именем `my-postgres`.

---

## 2. Удалить старый контейнер (если он больше не нужен)

Если контейнер больше не используется, удалите его с помощью команды:

```cmd
docker rm -f my-postgres
```

- Флаг `-f` принудительно удаляет контейнер, даже если он работает.

**Важно:** Убедитесь, что контейнер не содержит данных, которые вам нужны. Данные PostgreSQL в контейнере хранятся только внутри него, если не настроены тома (volumes). Для настройки долговременного хранения данных обратитесь за помощью.

---

## 3. Использовать другое имя для нового контейнера

Если вы хотите сохранить старый контейнер, создайте новый с другим именем. Например:

```powershell
docker run `
  --name my-postgres-new `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=1234 `
  -e POSTGRES_DB=itm_task021_spring_security_task_3_1_2_db `
  -p 5432:5432 `
  -d postgres
```

- Новый контейнер будет называться `my-postgres-new`.

---

## 4. Проверить, запущен ли контейнер

После запуска нового контейнера убедитесь, что он работает, с помощью команды:

```cmd
docker ps
```

Если контейнер отображается в списке, он активен и PostgreSQL готов к работе.

---

## Примечание

Если вы удаляете старый контейнер, убедитесь, что данные, которые он содержит, больше не нужны. Чтобы сохранить данные из контейнера, используйте **тома (volumes)**:

- Том позволяет сохранять данные вне контейнера для долговременного хранения.
- Для настройки тома напишите, и я помогу вам это сделать.
```