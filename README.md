# wci-be

## build

- [Docker](https://docs.docker.com/get-docker/)

```shell
./build.sh
```

or

```shell
docker build -t janobono/wci-be .
```

## local run

- start

```shell
docker-compose up
```

- stop

```shell
docker-compose down
```

### endpoints

- [/api/health](http://localhost:8080/api/health)
- [mail dev](http://localhost:8081)

## environment variables

| Name               | Default                              |
|--------------------|--------------------------------------|
| PORT               | 8080                                 |
| CONTEXT_PATH       | /api                                 |
| LOG_LEVEL          | debug                                |
| DB_URL             | jdbc:postgresql://localhost:5432/app |
| DB_USER            | app                                  |
| DB_PASSWORD        | app                                  |
| MAIL_HOST          | localhost                            |
| MAIL_PORT          | 2525                                 |
| MAIL_USER_NAME     |                                      |
| MAIL_USER_PASSWORD |                                      |
| MAIL_TLS_ENABLE    | false                                |
| MAIL_TLS_REQUIRED  | false                                |
| MAIL_AUTH          | false                                |
| TOKEN_ISSUER       | wci                                  |
| TOKEN_EXPIRES_IN   | 3600                                 |
| TOKEN_PRIVATE_KEY  |                                      |
| TOKEN_PUBLIC_KEY   |                                      |
| APPLICATION_NAME   | WCI                                  |
| APPLICATION_NAME   | WCI                                  |
| APP_LANGUAGES_*    | * - index, value sk, en              |

## run just local services

- start

```shell
docker-compose -f docker-compose-local-dev.yaml up
```

- stop

```shell
docker-compose -f docker-compose-local-dev.yaml down
```
