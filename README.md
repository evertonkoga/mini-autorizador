# Mini Autorizador

## Ferramentas utilizadas
- JDK 22
- IDE Intellij Community Edition
- Spring Boot 3.3.2
  - Starter Test
  - Starter Web
  - Starter Data Jpa

## Breve explicação por commit
### Commit 1
* EM CartaoTest foi aplicado TDD com teste de unidade para criação de cartão
  * Utilizado @ParameterizedTest, @NullAndEmptySource e @ValueSource, para:
    * Realizar testes parametrizados, com uma variedade de entradas
    * Reduzir a duplicação de códigos de testes
* Na classe Cartao foram abordados:
  * Modelo não anêmico (DDD) + o princípio SRP do SOLID
  * Adicionado **Factory Method** + construtor privado, para criação e instanciação do cartão com a regra ``"todo cartão deverá ser criado com um saldo inicial de R$500,00"``, garantindo que não irá existir um novo cartão sem estra regra;
  * Adicionado método de validação do negocio, mas centralizada em CartaoValidador abordando:
    * Aplicado os princípios SRP, OCP e DIP do SOLID
    * Adicionado **Notification Pattern** para agrupar e gerenciar as notificações de validações.
    * Em NoStacktraceException foi configurado para não incluir a stack trace nas exceções lançadas.
    * Incluido lib Apache Commons Lang 3 para ajudar nas validações.

### Commit 2
* Em CartaoTest foi aplicado TDD com teste de unidade para debitar do cartão.
* Aplicado clean code em CartaoValidator
* Adicionado regras para debitar do Cartão

### Commit 3
* Montado estrutura para o desenvolvimento com **Use Case** na camada de application. 
* Em CreateCartaoUseCaseTest foi aplicado TDD com teste de unidade no Use Case para criação de cartão.
  * Adicionado **Mockito** para mockar o comportamento do Use Case e inteface gateway do dominio.
* Na classe CreateCartaoUseCaseImpl, foi abordado: 
  * Alguns ajustes para utilizar o **Notification Pattern**.
  * Aplicado **Command Pattern** no Use Case, em que o nome da classe diz qual a ação sera executado e a mesma possuirá apenas um método execute.

### Commit 4
* Em GetCartaoByNumeroUseCaseTest foi aplicado TDD com teste de unidade no Use Case para consulta de cartão por numero.
* Foi aplicado o Mockito.reset em união @BeforeEach para garantir que os mocks sejam limpos a cada execução dos teste.

### Commit 5
* Em DebitCartaoUseCaseTest foi aplicado TDD com teste de unidade no Use Case para debitar do cartão.
* **Observação:** Nos testes não fiz uso dos @ParameterizedTest, visto que os testes de unidade na camada de dominio, validam todas as possibilidades possíves.

### Commit 6
* Adicionado os pacotes do Spring Boot (web e data-jpa) e MySQL
* Adicionado docker-compose para o banco de dados MySQL
* Configuração do profile, foi abordado:
  * Boas práticas:
    * Na configuração do datasource, foi aplicado interpolação de variáveis, para que as mesmas sejam aplicadas conforme os dados do profile em execução, mantendo o dinamisdo desses dados.
    * **connection-timeout** - tempo que uma thread espera para receber um conexão do pool.
    * **ddl-auto** - uma boa prática é deixar **none** por padrão e utilizar o **update** em desenvolvimento, uma melhor abordagem a essa configuração seria implementar **migrations**.
    * **max-lifetime** - tempo máximo que uma conexão pode ficar ativa, visando segurança.
  * Para performance:
    * **auto-commit** - desabilitado para que o Spring + Hibernate passe a gerenciar a transação.
    * **maximum-pool-size** - quantidade máxima de conexões no pool para aplicação.
    * **minimum-idle** - quantidade mínima de conexões caso o serviço fique ocioso.
    * **open-in-view** - desabilitado para que não segure a transação desde a controller.
    * **hibernate.connection.provider_disables_autocommit** - habilitado para afirmar que o auto-commit está desabilitado e evitar que o Hibernate tenha que validar antes de cada transação e consumir uma conexão do pool.

### Commit 7
* Em CartaoMySQLGatewayTest foi aplicado TDD com teste de integração no Gateway de dominio.
* Implementado o Gateway de dominio com MySQL, para que a camada de aplication possa se comunicar com a camada de percistencia.
* Configurado o banco H2 para os testes de integração.
* Utilizado a anotação **@DataJpaTest** para que o Spring Boot levante apenas as configurações para testar a repository, deixando os testes de integração mais rápido.
* Foi criado uma anotação **MySQLGatewayTest** para centralizar e organizar todas a anotações necessárias para testar do gateway, inclusive a extension customizada **CleanUpExtension** para garantir que antes de cada teste seja executado a limpeza da base de dados automaticamente e não correr o risco de haver dados sujos de testes anteriores.

### Commit 8
* Aplicado TDD com teste de integração no Use Case com repository.
* Criado anotação **IntegrationTest** para centralizar e organizar todas a anotações necessárias para testar os Use Cases com a repository.
* Utilizada a anotação **@SpyBean** para espionar/validar a chamada ao gateway no Use Case e garantir uma dupla verificação, alem da consulta na repository.

### Commit 9
* Em CartaoRestTest foi aplicado TDD com teste de integração no Controller com Use Case para criação de cartão.
* Criado anotação **ControllerTest** para centralizar e organizar todas a anotações necessárias para testar as controllers.
* Utilizado a anotação **@WebMvcTest** para que o Spring Boot levante somente as configurações para controller, deixando os testes de integração mais rápido.
* Utilizado a anotação **@MockBean** para criar mocks de beans utilizados na controller.
* Criado um interceptador de exceções global em GlobalExceptionHandler com **@RestControllerAdvice**, para centralizar, manipular e transformar exceções lançadas.

### Commit 10
* Em CartaoRestTest foi aplicado TDD com teste de integração para consultar saldo do cartão.
* Ajustado lançamento de exceção do tipo Not Found.

### Commit 11
* Em TransacaoRestTest foi aplicado TDD com teste de integração para debitar do cartão.