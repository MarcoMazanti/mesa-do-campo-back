# Protocolo híbrido v1

O backend usa RSA-OAEP com SHA-256 para transportar uma chave AES de uso único e AES-256-GCM para proteger o JSON. GCM autentica o conteúdo; portanto, não substitua esse formato por AES-CBC.

## Rotas protegidas

Todos os controllers de negócio estão anotados com `@HybridEncrypted`. A exceção é o endpoint público de bootstrap `GET /api/crypto/public-key`. Para futuras rotas, anote o método ou a classe do controller:

```java
@HybridEncrypted
@PostMapping("/create")
public ResponseEntity<?> create(@RequestBody MeuDto dto) { ... }
```

O endpoint de bootstrap é `GET /api/crypto/public-key`. Ele é público e devolve a chave pública RSA como `publicKey` em Base64 no formato X.509/SPKI. A chave privada continua exclusivamente no backend, em `CHAVE_PRIVADA`, no formato PKCS#8 Base64 ou PEM.

## Requisição com corpo

O cliente gera uma nova chave AES de 32 bytes e um nonce aleatório de 12 bytes para cada requisição. Criptografa a chave AES com RSA-OAEP/SHA-256 e o JSON com AES-GCM. O AAD obrigatório é a string UTF-8 `METODO:/caminho`, por exemplo `POST:/api/produto/create`.

```json
{
  "version": "v1",
  "algorithm": "RSA-OAEP-256/A256GCM",
  "encryptedKey": "Base64(RSA-OAEP(AES-256))",
  "iv": "Base64(nonce de 12 bytes)",
  "encryptedData": "Base64(ciphertext e tag GCM)"
}
```

## Requisição sem corpo

Em rotas anotadas sem corpo, como `GET` e `DELETE`, envie a chave AES embrulhada no cabeçalho `X-Hybrid-Encrypted-Key`. Ela usa o mesmo RSA-OAEP/SHA-256. A resposta é cifrada com essa chave.

## Resposta

Após normalizar a resposta normal da API, o backend devolve:

```json
{
  "version": "v1",
  "algorithm": "RSA-OAEP-256/A256GCM",
  "iv": "Base64(nonce de 12 bytes)",
  "encryptedData": "Base64(ciphertext e tag GCM)"
}
```

Para abrir a resposta, o frontend usa o AAD UTF-8 `RESPONSE:METODO:/caminho`, por exemplo `RESPONSE:POST:/api/produto/create`. Se a chave RSA ou o envelope de entrada for inválido, a API responde um erro 400 sem cifrar — pois ainda não há chave de sessão confiável para proteger a resposta.

## Ordem dos advice

`DecryptRequestInterceptor` possui a maior precedência e executa antes do mapeamento para o DTO. Na saída, `ModelResponseAdivice` possui precedência alta e cria o modelo padrão; `EncryptResponseInterceptor` possui a menor precedência e cifra esse modelo por último. O interceptor MVC registra a política da rota antes do controller, inclusive para que um `@ExceptionHandler` saiba se deve cifrar um erro que ocorreu depois de a chave AES ter sido aberta.
