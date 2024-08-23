# Mini Autorizador

## Ferramentas utilizadas
- JDK 22
- IDE Intellij Community Edition
- Spring Boot 3.3.2
  - Lib Starter Test

## Breve explicação por commit
### Commit 1
* Em CartaoTest foi aplicado TDD com teste de unidade para criação de cartão.
* Na classe Cartao foram abordados:
  * Modelo não anêmico (DDD) + o princípio SRP do SOLID
  * Adicionado **Factory Method** + construtor privado, para criação e instanciação do cartão com a regra ``"todo cartão deverá ser criado com um saldo inicial de R$500,00"``, garantindo que não irá existir um novo cartão sem estra regra;
  * Adicionado método de validação do negocio, mas centralizada em CartaoValidador abordando:
    * Aplicado os princípios SRP, OCP e DIP do SOLID
    * Adicionado **Notification Pattern** para agrupar e gerenciar as notificações de validações.
    * Em NoStacktraceException foi configurado para não incluir a stack trace nas exceções lançadas.
    * Incluido lib Apache Commons Lang 3 para ajudar nas validações.
