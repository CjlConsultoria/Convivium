# Deploy no Render (e ambientes similares)

## Conexão com o banco (obrigatório)

O erro **"Connection to localhost:5432 refused"** significa que as variáveis de banco **não estão definidas** no serviço Web. No Render, o banco fica em outro host; é preciso apontar a aplicação para ele.

### Opção A – Usar DATABASE_URL (recomendado no Render)

1. No Render, abra o seu **PostgreSQL** (Database).
2. Em **Connections**, copie a **Internal Database URL** (formato: `postgresql://usuario:senha@host:5432/nome_do_banco`).
3. No seu **Web Service** (aplicação Java) → **Environment** → adicione:
   - **Key:** `DATABASE_URL`
   - **Value:** cole a Internal Database URL copiada.
4. O sistema converte essa URL automaticamente em `spring.datasource.url`, `username` e `password`. Não precisa criar as variáveis `SPRING_DATASOURCE_*` se usar `DATABASE_URL`.

### Opção B – Usar variáveis Spring

Se preferir não usar `DATABASE_URL`, defina no Web Service:

| Variável | Valor |
|----------|--------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://HOST_DO_POSTGRES:5432/convivium` (troque HOST_DO_POSTGRES pelo host do seu PostgreSQL no Render) |
| `SPRING_DATASOURCE_USERNAME` | usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | senha do banco |

O host do PostgreSQL aparece na Internal Database URL do banco (ex.: `dpg-xxxxx-a.region.railway.app`).

## Demais variáveis de ambiente

Configure no Web Service (Service → Environment):

| Variável | Obrigatório | Descrição |
|----------|-------------|-----------|
| `SPRING_PROFILES_ACTIVE` | Sim | Defina como `prod` |
| `DATABASE_URL` **ou** as 3 `SPRING_DATASOURCE_*` | Sim | Conexão com o PostgreSQL (veja acima) |
| `JWT_SECRET` | Recomendado | Chave secreta para JWT (gere uma string forte) |
| `APP_FRONTEND_URL` | Opcional | URL do frontend para CORS (ex: `https://convivium-front.onrender.com`) |
| `PORT` | Automático | O Render define automaticamente |

## Schema do banco

O sistema usa **`spring.jpa.hibernate.ddl-auto=update`**. Ao subir a aplicação:

- Novas tabelas (ex: `convivium.TB_CONDOMINIO_INFO`) são criadas automaticamente.
- Novas colunas em tabelas existentes são adicionadas.
- **Nenhum dado existente é apagado.**

O schema padrão é `convivium`; todas as tabelas ficam nesse schema.

## Build

- **Render (Java):** use o build command padrão Maven (`mvn clean package -DskipTests` ou o que estiver configurado) e run command `java -jar target/convivium-*.jar`.
- Garanta que o perfil `prod` esteja ativo (variável `SPRING_PROFILES_ACTIVE=prod`).
