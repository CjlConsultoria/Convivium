# Convivium - Sistema de Gestão para Condomínios

## Descrição

O Convivium é um sistema completo para gestão de condomínios, oferecendo funcionalidades para administradores, porteiros, síndicos e moradores.

## Funcionalidades

- **Gestão de Usuários**: Cadastro e gerenciamento de moradores, porteiros, síndicos e administradores
- **Controle de Encomendas**: Registro e controle de entrega de encomendas
- **Sistema de Reclamações**: Abertura e acompanhamento de reclamações
- **Chat**: Sistema de comunicação interna
- **Relatórios**: Geração de relatórios diversos
- **Gestão de Licenças**: Controle de licenças do sistema
- **Gestão de Empresas**: Cadastro e gerenciamento de empresas/condomínios

## Tecnologias Utilizadas

- **Backend**: Spring Boot 2.7.0
- **Banco de Dados**: H2 (local) / PostgreSQL (produção)
- **Segurança**: Spring Security + JWT
- **Documentação**: Swagger
- **Build**: Maven

## Como Executar Localmente

### Pré-requisitos

- Java 11+
- Maven 3.6+

### Passos para execução

1. Clone o repositório:
```bash
git clone <url-do-repositorio>
cd convivium/backend
```

2. Execute o projeto com perfil local (H2):
```bash
mvn spring-boot:run -Plocal
```

Ou use o comando direto:
```bash
mvn spring-boot:run -Dspring.profiles.active=local
```

3. Acesse a aplicação:
   - **API**: http://localhost:8080
   - **H2 Console**: http://localhost:8080/h2-console
   - **Swagger**: http://localhost:8080/swagger-ui/

### Configurações do H2

Para acessar o console do H2:
- **JDBC URL**: `jdbc:h2:mem:convivium`
- **Username**: `sa`
- **Password**: `password`

## Usuários Padrão (Ambiente Local)

O sistema cria automaticamente os seguintes usuários para teste:

| Tipo | Email | Senha | Descrição |
|------|-------|-------|----------|
| Admin | admin@convivium.com | admin123 | Administrador do sistema |
| Porteiro | porteiro@convivium.com | porteiro123 | Porteiro do condomínio |
| Morador | morador@convivium.com | morador123 | Morador exemplo |

## Estrutura do Projeto

```
backend/
├── src/main/java/br/com/convivium/
│   ├── config/          # Configurações do Spring
│   ├── controller/      # Controllers REST
│   ├── dto/            # Data Transfer Objects
│   ├── entity/         # Entidades JPA
│   ├── exception/      # Tratamento de exceções
│   ├── repository/     # Repositórios JPA
│   ├── security/       # Configurações de segurança
│   └── service/        # Serviços de negócio
├── src/main/resources/
│   ├── application-local.properties  # Configurações locais
│   └── data-local.sql               # Dados iniciais
└── pom.xml            # Configurações Maven
```

## Perfis de Execução

### Local (H2)
```bash
mvn spring-boot:run -Plocal
```
- Banco H2 em memória
- Dados de teste pré-carregados
- Console H2 habilitado
- Logs detalhados

### Produção
```bash
mvn spring-boot:run
```
- Banco PostgreSQL
- Configurações de produção
- Segurança aprimorada

## API Endpoints Principais

- **POST** `/api/auth/login` - Autenticação
- **POST** `/api/auth/register` - Registro de usuário
- **GET** `/api/users` - Listar usuários
- **GET** `/api/empresas` - Listar empresas
- **GET** `/api/encomendas` - Listar encomendas
- **GET** `/api/reclamacoes` - Listar reclamações

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.