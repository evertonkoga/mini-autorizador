# Mini Autorizador

## Ferramentas utilizadas
- JDK 22
- IDE Intellij Community Edition
- Spring Boot 3.3.2
  - Lib Starter Test

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