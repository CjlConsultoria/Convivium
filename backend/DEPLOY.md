# Deploy no Render (e ambientes similares)

## Variáveis de ambiente no Render

Configure no painel do Render (Service → Environment):

| Variável | Obrigatório | Descrição |
|----------|-------------|-----------|
| `SPRING_PROFILES_ACTIVE` | Sim | Defina como `prod` |
| `SPRING_DATASOURCE_URL` | Sim | URL JDBC do PostgreSQL (ex: `jdbc:postgresql://host:5432/convivium`) |
| `SPRING_DATASOURCE_USERNAME` | Sim | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Sim | Senha do banco |
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
