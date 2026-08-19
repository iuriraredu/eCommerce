Considerações iniciais:

1) Os arquivos/pastas não foram olhados:
- .github
- Dockerfile
- qodana.yaml

2) Algumas classes não possuem comentário pois sofrem mais do mesmo. Por exemplo, a classe OrderController possui os mesmos problemas da ClientController, então para economizar tempo, nenhum comentário foi adicionado em relação aos mesmos problemas.

---

1) Pessoalmente prefiro o swagger feito fora do código, acredito que deixa a API mais limpa, mas isso é apenas algo pessoal.

2) Normalmente não se commita a pasta .idea.

3) O código está bilíngue, tem coisas em português e outras em inglês. Mantenha apenas uma língua, de preferência em inglês.

4) O código usa várias padrões. Por exemplo, em alguns lugares o nome de um parâmetro é apenas service em outra é mais definido etc. Precisa manter um padrão (não apenas no nome das coisas, mas no código inteiro).

5) Precisa melhorar o nome de variáveis, parâmetros etc.

6) O que não for mutável, declarar com o modificador "final".

7) Se está usando env para a secret do token, por que não usar para demais parâmetros no application.properties?

8) Argon2id precisa ser parametrizado em vez de usar os parâmetros padrão.

9) Os mapeadores dentro dos records podem ser colocados em classes Mappers/Converters, mas isso fica a seu critério.

10) Alguns DTOs possuem campos desnecessários.
