# Mesa do Campo — Backend

API do projeto Mesa do Campo, construída com Spring Boot e PostgreSQL. Este README concentra a configuração do ambiente, a integração da criptografia híbrida e o fluxo de versionamento.

## Requisitos

- Java 17
- PostgreSQL
- Maven Wrapper incluído no projeto
- OpenSSL, apenas se for necessário gerar um novo par de chaves RSA

## Como executar

1. Crie o arquivo `.env` na raiz do backend. Ele não é versionado.
2. Configure o banco, a porta e as chaves conforme a seção seguinte.
3. Inicie a aplicação:

```powershell
.\mvnw.cmd spring-boot:run
```

A documentação interativa da API fica disponível em `/swagger-ui/index.html` quando a aplicação estiver em execução.

## Variáveis de ambiente

O projeto carrega o arquivo `.env` e disponibiliza as variáveis para o Spring. Nunca envie esse arquivo, nem chaves, senhas de banco ou token do ngrok para o repositório.

```dotenv
BANCO_URL=jdbc:postgresql://localhost:5432/mesa_do_campo
BANCO_USERNAME=seu_usuario
BANCO_PASSWORD=sua_senha
SERVER_PORT=8080

# Opcional: túnel público do ngrok
NGROK_AUTHTOKEN=seu_token
NGROK_DOMAIN=seu_dominio_ngrok

# Criptografia híbrida
CHAVE_PRIVADA=base64_da_chave_rsa_pkcs8
# Opcional: se omitida, a aplicação a deriva da chave privada.
CHAVE_PUBLICA=base64_da_chave_rsa_x509_spki
```

`CHAVE_PRIVADA` é obrigatória. `CHAVE_PUBLICA` é recomendada para deixar a configuração explícita, porém é opcional porque o backend consegue derivá-la da chave privada RSA.

## Criptografia híbrida

As rotas de negócio anotadas com `@HybridEncrypted` usam RSA-OAEP com SHA-256 para transportar uma chave de sessão AES e AES-256-GCM para proteger e autenticar o JSON. Não substitua AES-GCM por AES-CBC: o GCM também valida a integridade da mensagem.

### Gerar ou substituir as chaves

Use RSA de 3.072 bits ou superior. Os comandos abaixo geram arquivos temporários PEM e exibem os valores Base64 de uma única linha que devem ser colocados no `.env`:

```powershell
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:3072 -out private.pem
openssl pkey -in private.pem -pubout -out public.pem

# Valor para CHAVE_PRIVADA (PKCS#8, Base64)
openssl pkcs8 -topk8 -nocrypt -in private.pem -outform DER | openssl base64 -A

# Valor para CHAVE_PUBLICA (X.509/SPKI, Base64)
openssl pkey -pubin -in public.pem -pubout -outform DER | openssl base64 -A
```

Depois de copiar os valores, remova os arquivos PEM ou guarde-os em um cofre seguro. Ao trocar a chave privada, todos os clientes devem buscar novamente a chave pública. Se uma chave privada for exposta, gere um novo par imediatamente.

O serviço também aceita chaves em PEM, mas o formato Base64 em uma linha evita problemas de quebra de linha no `.env`.

### Chave pública para o frontend

O frontend deve obter a chave pública antes da primeira chamada protegida:

```http
GET /api/crypto/public-key
```

Essa é a única rota pública do protocolo. A resposta possui cache de uma hora e retorna a chave X.509/SPKI em Base64:

```json
{
  "algorithm": "RSA-OAEP-256",
  "publicKey": "Base64(X.509/SPKI)"
}
```

A chave privada nunca sai do backend e nunca deve ser incluída no frontend.

### Requisição com corpo

Para cada requisição protegida com corpo, o cliente deve:

1. Gerar uma nova chave AES de 32 bytes e um IV aleatório de 12 bytes.
2. Cifrar a chave AES com RSA-OAEP/SHA-256 usando a chave pública do endpoint acima.
3. Cifrar o JSON com AES-256-GCM.
4. Usar como AAD a sequência UTF-8 `METODO:/caminho`, por exemplo `POST:/api/produto/create`.

Envie o seguinte envelope JSON:

```json
{
  "version": "v1",
  "algorithm": "RSA-OAEP-256/A256GCM",
  "encryptedKey": "Base64(RSA-OAEP(AES-256))",
  "iv": "Base64(IV de 12 bytes)",
  "encryptedData": "Base64(ciphertext e tag GCM)"
}
```

### Requisição sem corpo

Em rotas protegidas sem corpo, como `GET` e `DELETE`, envie somente a chave AES cifrada no cabeçalho:

```http
X-Hybrid-Encrypted-Key: Base64(RSA-OAEP(AES-256))
```

A resposta usará essa mesma chave AES de sessão.

### Resposta protegida

Depois de montar a resposta padrão da API, o backend devolve um envelope cifrado:

```json
{
  "version": "v1",
  "algorithm": "RSA-OAEP-256/A256GCM",
  "iv": "Base64(IV de 12 bytes)",
  "encryptedData": "Base64(ciphertext e tag GCM)"
}
```

Para decifrá-la, o frontend usa o AAD UTF-8 `RESPONSE:METODO:/caminho`, por exemplo `RESPONSE:POST:/api/produto/create`. Se a chave RSA ou o envelope de entrada for inválido, a API responde `400` sem cifrar, pois não existe uma chave de sessão confiável para proteger a mensagem de erro.

### Adicionar uma rota protegida

Anote o método ou a classe do controller com `@HybridEncrypted`:

```java
@HybridEncrypted
@PostMapping("/create")
public ResponseEntity<?> create(@RequestBody MeuDto dto) {
    // ...
}
```

Na entrada, `DecryptRequestInterceptor` tem a maior precedência e abre o envelope antes do mapeamento para o DTO. Na saída, `ModelResponseAdivice` cria o modelo padrão e `EncryptResponseInterceptor` cifra esse modelo por último. O interceptor MVC registra a política da rota antes do controller, permitindo que um `@ExceptionHandler` também saiba quando deve cifrar um erro.

## Versionamento

| Branch | Finalidade |
| --- | --- |
| `feature` | Desenvolvimento isolado de uma funcionalidade. |
| `dev` | Integração das funcionalidades em desenvolvimento. |
| `homolog` | Testes amplos de homologação, inclusive com banco. |
| `prod` | Produto final após a validação. |

Ordem de merge: `feature` → `dev` → `homolog` → `prod`.
