# 🎯 Resume Tailor — Currículo Inteligente com IA

Aplicação Spring Boot que utiliza a API do Claude (Anthropic) para adaptar currículos automaticamente de acordo com a vaga desejada.

---

## 🚀 Como rodar o projeto

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Chave de API da Anthropic ([console.anthropic.com](https://console.anthropic.com))

### 1. Clone o projeto

```bash
git clone <url-do-repositorio>
cd resume-tailor
```

### 2. Configure a chave da API

**Opção A — Variável de ambiente (recomendado):**
```bash
export ANTHROPIC_API_KEY=sk-ant-xxxxxxxxxxxx
```

**Opção B — application.properties:**
```properties
anthropic.api.key=sk-ant-xxxxxxxxxxxx
```

### 3. Execute a aplicação

```bash
mvn spring-boot:run
```

Acesse: [http://localhost:8080](http://localhost:8080)

---

## 📁 Estrutura do Projeto

```
resume-tailor/
├── src/
│   ├── main/
│   │   ├── java/com/resumetailor/
│   │   │   ├── ResumeTailorApplication.java       # Classe principal
│   │   │   ├── controller/
│   │   │   │   ├── ResumeTailorController.java    # Rotas HTTP
│   │   │   │   └── GlobalExceptionHandler.java    # Tratamento de erros
│   │   │   ├── service/
│   │   │   │   ├── ResumeTailorService.java       # Orquestrador principal
│   │   │   │   ├── PdfExtractorService.java       # Lê texto do PDF enviado
│   │   │   │   ├── AnthropicService.java          # Chama a API do Claude
│   │   │   │   ├── PdfGeneratorService.java       # Gera PDF do resultado
│   │   │   │   └── DocxGeneratorService.java      # Gera DOCX do resultado
│   │   │   └── dto/
│   │   │       ├── TailorRequest.java
│   │   │       ├── TailorResponse.java
│   │   │       └── AnthropicDtos.java
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── index.html                     # Página principal
│   │       │   └── result.html                    # Página de resultado
│   │       └── application.properties
└── pom.xml
```

---

## ⚙️ Fluxo da Aplicação

```
Usuário faz upload do PDF
        ↓
PdfExtractorService extrai o texto (Apache PDFBox)
        ↓
AnthropicService envia para a API do Claude
        ↓
Claude retorna o currículo reescrito para a vaga
        ↓
PdfGeneratorService ou DocxGeneratorService gera o arquivo
        ↓
Usuário baixa o currículo adaptado
```

---

## 🔌 Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/` | Página principal |
| `POST` | `/tailor` | Processa o currículo (formulário web) |
| `GET` | `/download/{filename}` | Baixa o arquivo gerado |
| `POST` | `/api/tailor` | API REST (retorna JSON) |

---

## 🛠️ Configurações (application.properties)

| Propriedade | Padrão | Descrição |
|-------------|--------|-----------|
| `anthropic.api.key` | — | Sua chave de API |
| `anthropic.api.model` | `claude-opus-4-5` | Modelo do Claude |
| `anthropic.api.max-tokens` | `4096` | Tamanho máximo da resposta |
| `spring.servlet.multipart.max-file-size` | `10MB` | Tamanho máximo do upload |
| `server.port` | `8080` | Porta do servidor |

---

## 🔮 Próximos passos (evoluções sugeridas)

- [ ] Suporte a DOCX como entrada (além de PDF)
- [ ] Histórico de currículos gerados (banco de dados)
- [ ] Autenticação de usuários
- [ ] Score de compatibilidade currículo × vaga
- [ ] Múltiplos idiomas (EN, ES)
- [ ] Deploy no Railway / Render / AWS
# CVAdjuster
