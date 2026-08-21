# Telegram Bot (Omegamon)

Bot de utilidades para o Telegram desenvolvido em **Java 21 + Spring Boot**, que integra várias APIs externas e permite salvar anotações pessoais direto pelo chat.

## Funcionalidades

- **`/ola`** — mensagem de saudação.
- **`/tempo`** — condições climáticas atuais (temperatura, sensação térmica, umidade, vento) via API de clima (OpenWeather).
- **`/dolar`, `/euro`, `/iene`, `/yuan`** — cotação em tempo real dessas moedas frente ao Real (compra, venda, máxima, mínima e variação percentual).
- **`/noticias`** — as 5 principais notícias do dia via News API.
- **`/notas`** — sistema de notas por usuário, com teclado inline (botões) para **salvar**, **listar** e **deletar** anotações, persistidas em banco de dados.

## Arquitetura

- **Spring Boot 4** com `spring-boot-starter-webmvc` e `spring-boot-starter-data-jpa`.
- **Banco de dados H2** (em memória/arquivo) para persistir as notas dos usuários.
- **[TelegramBots](https://github.com/telegram-bot-and-api-library/telegrambots) (long polling)** para comunicação com a API do Telegram.
- **Lombok** para reduzir boilerplate nas entidades.
- **dotenv-java** para carregar variáveis de ambiente de um arquivo `.env` local.
- **Rate limiting** simples por chat (máx. 10 comandos a cada 10 minutos).
- **Gerenciamento de estado de conversa** (ex.: aguardando texto de nota, aguardando número da nota a deletar) via `ConversationStateService`.

### Estrutura de pacotes

```
com.botTelegram.TelegramBot
├── command/     # Registro dos comandos do bot no Telegram
├── config/      # Configuração do cliente REST e do cliente Telegram
├── controller/  # Consumidor de updates do Telegram (roteamento de comandos)
├── entity/      # Entidade JPA (Note)
├── Enum/        # Enum de moedas suportadas
├── repository/  # Repositório JPA de notas
├── response/    # DTOs de resposta das APIs externas (clima, moeda, notícias)
├── run/         # Inicialização do bot (long polling)
└── service/     # Regras de negócio (clima, preço, notícias, notas, rate limit, estado)
```

## Pré-requisitos

- Java 21
- Maven (ou use o wrapper `./mvnw`)
- Chaves de API: Telegram Bot Token, OpenWeather, e um provedor de notícias (News API)

## Configuração

Crie um arquivo `.env` na raiz do projeto com as variáveis usadas em `application.properties`:

```env
telegram.token=SEU_TOKEN_DO_BOT
latitude=SUA_LATITUDE
longitude=SUA_LONGITUDE
weather_url=https://api.openweathermap.org/data/2.5
weather.token=SUA_CHAVE_OPENWEATHER
price_url=URL_DA_API_DE_COTACAO
news_url=URL_DA_NEWS_API
news.token=SUA_CHAVE_NEWS_API
```

## Executando o projeto

```bash
./mvnw spring-boot:run
```

Ou gerando o pacote e rodando o `.jar`:

```bash
./mvnw clean package
java -jar target/TelegramBotApplication-0.0.1-SNAPSHOT.jar
```

## Tecnologias

Java 21 · Spring Boot 4 · Spring Data JPA · H2 Database · TelegramBots API 10.2.0 · Lombok · dotenv-java