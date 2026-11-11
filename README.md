# Portal de Estágios

Aplicação Java 17 com Spring Boot, API REST, interface web simples, autenticação por sessão do Spring Security e persistência PostgreSQL via Spring Data JPA.

## Executar

Requisitos: Java 17+ e Maven. PostgreSQL é opcional para persistência dos dados.

Crie o banco e configure a conexão por variáveis de ambiente (nenhuma credencial fica no código):

```sh
createdb portal_estagios
export DATABASE_URL=jdbc:postgresql://localhost:5432/portal_estagios
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD='sua-senha'
psql -h localhost -U postgres -d portal_estagios -f src/scripts/schema.sql
mvn spring-boot:run
```

Por padrão, a aplicação usa um banco H2 em memória; os dados são perdidos ao reiniciar. Para persistência, configure `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `DATABASE_DRIVER_CLASS_NAME=org.postgresql.Driver` e `DATABASE_DIALECT=org.hibernate.dialect.PostgreSQLDialect` para usar PostgreSQL. O Hibernate atualiza o schema sem apagar dados. O script `src/scripts/schema.sql` também migra instalações existentes, adicionando `password_hash`. Contas criadas antes da migração precisam ser recadastradas ou receber uma senha BCrypt definida pelo operador para poderem entrar.

Abra <http://localhost:8081/> para a interface ou <http://localhost:8081/swagger-ui.html> para a documentação interativa OpenAPI. Se quiser usar outra porta, defina `PORT` antes de iniciar a aplicação.

## Autenticação e erros

A sessão é mantida em cookie HTTP; login e logout são `POST`. Para proteção CSRF, obtenha antes das requisições que alteram dados o token em `GET /api/auth/csrf` e envie-o no cabeçalho `X-XSRF-TOKEN`. O navegador mantém o cookie `XSRF-TOKEN`; a interface já faz isso automaticamente. O cookie de sessão não é exposto em respostas JSON.

Os erros seguem o formato:

```json
{
  "timestamp": "2026-11-11T11:03:22",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Informe o curso do estudante.",
  "path": "/api/auth/register/student"
}
```

Senhas não são retornadas; somente hashes BCrypt são persistidos. Falhas internas retornam mensagem genérica e detalhes técnicos não são enviados ao cliente. Cadastro público não recebe perfil/role e só pode criar estudante ou empresa; administradores devem ser provisionados operacionalmente no banco.

## Endpoints

Todos os corpos JSON usam `Content-Type: application/json`. `401` indica sessão ausente/inválida; `403`, perfil sem autorização; `400`, entrada inválida; `404`, recurso não encontrado; `409`, duplicidade ou conflito de regra.

| Método e endpoint | Acesso | Resposta de sucesso |
|---|---|---|
| `GET /api/auth/csrf` | Público | `200` token CSRF |
| `POST /api/auth/register/student` | Público | `201` conta de estudante |
| `POST /api/auth/register/company` | Público | `201` conta de empresa |
| `POST /api/auth/login` | Público | `200` usuário autenticado e cookie de sessão |
| `POST /api/auth/logout` | Autenticado | `204` |
| `GET /api/areas` | Público | `200` lista |
| `POST /api/areas`, `PUT /api/areas/{id}`, `DELETE /api/areas/{id}` | Administrador | `201`, `200`, `204` |
| `GET /api/vagas`, `GET /api/vagas/{id}` | Público | `200` vagas abertas |
| `GET /api/empresa/vagas` | Empresa | `200` vagas próprias |
| `POST /api/empresa/vagas` | Empresa | `201` vaga criada |
| `PUT /api/empresa/vagas/{id}` | Empresa proprietária | `200` vaga atualizada |
| `PATCH /api/empresa/vagas/{id}/encerramento` | Empresa proprietária | `204` |
| `POST /api/estudante/vagas/{vagaId}/candidaturas` | Estudante | `201` candidatura |
| `GET /api/estudante/candidaturas` | Estudante | `200` candidaturas próprias |
| `GET /api/empresa/vagas/{vagaId}/candidaturas` | Empresa proprietária | `200` candidatos da vaga |
| `GET /api/admin/resumo`, `GET /api/admin/usuarios` | Administrador | `200` |
| `DELETE /api/admin/usuarios/{id}` | Administrador | `204` |

Filtros de vagas abertas: `GET /api/vagas?areaId=1&modalidade=REMOTA`. Modalidades aceitas: `PRESENCIAL`, `REMOTA`, `HIBRIDA`.

`GET /api/admin/resumo` retorna os totais de estudantes, empresas, vagas abertas, vagas encerradas e candidaturas.

### Exemplos

Cadastro público — não há campo de perfil ou administrador:

```http
POST /api/auth/register/student
Content-Type: application/json

{"nome":"Ana Silva","email":"ana@example.com","senha":"senha-segura-123","curso":"Sistemas de Informação"}
```

Resposta `201 Created`:

```json
{"id":12,"nome":"Ana Silva","email":"ana@example.com","perfil":"ESTUDANTE","curso":"Sistemas de Informação","nomeEmpresa":null}
```

Login:

```http
POST /api/auth/login
X-XSRF-TOKEN: <token obtido em GET /api/auth/csrf>
Content-Type: application/json

{"email":"ana@example.com","senha":"senha-segura-123"}
```

O `200 OK` retorna o mesmo formato público de usuário e define o cookie de sessão. Envie esse cookie e um token CSRF válido nas chamadas autenticadas.

Publicar vaga como empresa:

```http
POST /api/empresa/vagas
X-XSRF-TOKEN: <token>
Content-Type: application/json

{"titulo":"Estágio Java","descricao":"Desenvolvimento de sistemas.","areaId":1,"localizacao":"São Paulo","modalidade":"HIBRIDA","cargaHorariaSemanal":30}
```

Resposta `201 Created` inclui a vaga, área e empresa responsável. O dono da vaga é obtido exclusivamente da sessão, nunca do corpo da requisição.

## Regras mantidas

- Apenas empresas autenticadas publicam vagas; cada empresa só edita, encerra ou consulta candidatos das próprias vagas.
- Apenas estudantes autenticados podem se candidatar; vaga deve estar aberta e a candidatura é única por estudante e vaga.
- Áreas são geridas por administradores e não podem ser removidas enquanto vinculadas a vagas.
- Encerrar vaga não apaga candidaturas.
