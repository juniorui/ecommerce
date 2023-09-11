# E-commerce

Este projeto tem como objetivo ser utilizado como estrutura inicial para avaliar candidados em um processo seletivo. Trata-se de uma amostra de um possível sistema de e-commerce, ainda em desenvolvimento, com arquitetura em microserviços. O projeto foi concebido com o apache maven e conta com 4 módulos sendo 3 deles microserviços.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	...
    <modules>
        <module>crm</module>
        <module>inventory</module>
        <module>order</module>
        <module>core</module>
    </modules>
	...
</project>
```
As tecnologias utilizadas para concepção do projeto são:

- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html "Java 17")
- [Spring Boot 2.6.1](https://docs.spring.io/spring-boot/docs/2.6.1/reference/html/getting-started.html#getting-started "Spring Boot 2.6.1")
	- spring-boot-starter-data-jpa
	- spring-boot-starter-web
	- spring-boot-starter-validation
	- spring-cloud-starter-openfeign
- [H2 Database](https://www.h2database.com/html/main.html "H2 Database")
- [Flyway](https://flywaydb.org/ "Flyway")
- [Model Mapper](http://modelmapper.org/ "Model Mapper")
- [Lombok](https://projectlombok.org/ "Lombok")


## Módulos

### core

Biblioteca compartilhada entre os microserviços com definições de arquiteturais e classes genéricas de alto nível utilizadas pelos microserviços.

#### com.sigga.ecommerce.core.advice.ApiExceptionHandler

Classe responsável por tratar as exceções do sistema e retornar uma estrutura `JSON` com o código HTTP de erro associado.

```java
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNegocio(ResourceNotFoundException ex, WebRequest request) {

        var status = HttpStatus.NOT_FOUND;

        return handleExceptionInternal(ex, Collections.singletonMap("message", "resource not found"), new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return handleExceptionInternal(ex, Collections.singletonMap("message", "http message not readable because body is empty or invalid"), new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return handleExceptionInternal(ex,

                Collections.singletonMap("validations-error", ex.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList())),

                new HttpHeaders(), status, request);
    }
}
```

#### com.sigga.ecommerce.core.controller.EcommerceController

Classe genérica responsável por definir quais são as operações default para os controladores rest (`@RestController`). Todo controlador do sistema deveria, no caso de trabalhar com um recurso persistente, herdar diretamente ou indiretamente dessa classe. Implementa a interface padrão de recursos do sistema `EcommerceResource`.

```java
@RequiredArgsConstructor
public class EcommerceController<E extends EcommerceEntity, VO> implements EcommerceResource<VO> {

    private final EcommerceService<E, VO> service;

    public VO findById(UUID id) {

        return this.service.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public UUID save(@Valid VO valueObject) {

        return this.service.save(valueObject);
    }

    public void edit(UUID id, VO valueObject) {

        this.service.edit(id, valueObject);
    }

    public void delete(UUID id) {

        this.service.delete(id);
    }

    public Page<VO> search(VO example, Pageable pageable) {

        return this.service.search(example, pageable);
    }
}
```

#### com.sigga.ecommerce.core.entity.EcommerceEntity

Interface que define os principais métodos inerentes a uma entidade (recurso persistente) do sistema.

```java
public interface EcommerceEntity {

    UUID getId();

    void setId(UUID id);
}

```

#### com.sigga.ecommerce.core.exception.ResourceNotFoundException

Classe que mapeia a exceção default para o caso de recurso não encontrado - HTTP 404.

```java
public class ResourceNotFoundException extends RuntimeException {

    public static void throwIf(boolean test) {

        if (test) {

            throw new ResourceNotFoundException();
        }
    }
}
```

#### com.sigga.ecommerce.core.mapper.ModelMapperConfiguration

Classe de configuração para o Spring Boot com o objetivo de criar e disponibilizar o contexto de execução do spring um bean do tipo ModelMapper.

```java
@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        
        return new ModelMapper();
    }
}

```

#### com.sigga.ecommerce.core.repository.EcommerceRepository

Interface que define as principais operações de um repositório de recursos persistentes. É preciso parametrizar, a nível de herança, o recurso persistente, através do tipo genérico, que o repositório fará a manipulação.  Basicamente, herda das definições do `spring-data-jpa`:
- `PagingAndSortingRepository`
- `JpaSpecificationExecutor`
- `QueryByExampleExecutor`

```java
@NoRepositoryBean
public interface EcommerceRepository<T> extends PagingAndSortingRepository<T, UUID>, JpaSpecificationExecutor<T>, QueryByExampleExecutor<T> {

}
```

#### com.sigga.ecommerce.core.resource.EcommerceResource

Interface que define as operações default para recursos do sistema. Todo controlador rest e cliente rest, utilizando o `Spring Cloud OpenFeign`, terá como base essa interface. A idéia é termos o controlador e cliente sincronizados com as mesmas operações. Todas as operações deverão retornar um `ValueObject / DTO`. É preciso parametrizar, a nível de herança, o `ValueObject / DTO`, através do tipo genérico, que o recurso disponibilizará e/ou receberá como entrada para realizar as operações.

```java
public interface EcommerceResource<VO> {

    @ResponseBody
    @GetMapping("/{id}")
    VO findById(@PathVariable UUID id);

    @ResponseBody
    @PostMapping
    UUID save(@RequestBody VO customer);

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void edit(@PathVariable UUID id, @RequestBody VO customer);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id);

    @GetMapping
    Page<VO> search(@SpringQueryMap VO example, @SpringQueryMap Pageable pageable);
}
```

#### com.sigga.ecommerce.core.service.EcommerceService

Classe genérica de serviço no qual receberá dos controladores a operação a ser realizada, mediará o acesso aos repositórios, fará o mapeamento de entidade (recurso persistente) para `ValueObject / DTO` e vice-versa, utilizando, neste caso, o framework `ModelMapper`, e, como de costume, será a classe responsável pela aplicação das regras de negócio.

```java
@RequiredArgsConstructor
public abstract class EcommerceService<T extends EcommerceEntity, VO> {

    private final EcommerceRepository<T> repository;

    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Optional<VO> findById(UUID id) {

        return this.repository.findById(id).map(this::mapEntityToValueObject);
    }

    @Transactional
    public UUID save(VO valueObject) {

        return this.repository.save(this.mapValueObjectToEntity(valueObject)).getId();
    }

    @Transactional
    public void edit(UUID id, VO valueObject) {

        var customerEntity = this.mapValueObjectToEntity(valueObject);

        customerEntity.setId(id);

        this.repository.save(customerEntity);
    }

    public void delete(UUID id) {

        ResourceNotFoundException.throwIf(!this.repository.existsById(id));

        this.repository.deleteById(id);
    }

    public Page<VO> search(VO valueObject, Pageable pageable) {

        var exampleMatcher = ExampleMatcher.matchingAll()

                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)

                .withIgnoreCase();

        var example = Example.of(this.mapValueObjectToEntity(valueObject), exampleMatcher);

        return this.repository.findAll(example, pageable)

                .map(entity -> this.modelMapper.map(entity, getValueObjectClass()));
    }

    protected VO mapEntityToValueObject(T entity) {

        return this.modelMapper.map(entity, this.getValueObjectClass());
    }

    protected T mapValueObjectToEntity(VO valueObject) {

        return this.modelMapper.map(valueObject, this.getEntityClass());
    }

    @SuppressWarnings("unchecked")
    protected Class<VO> getValueObjectClass() {

        return (Class<VO>) Objects.requireNonNull(GenericTypeResolver.resolveTypeArguments(this.getClass(), EcommerceService.class))[1];
    }

    @SuppressWarnings("unchecked")
    protected Class<T> getEntityClass() {

        return (Class<T>) Objects.requireNonNull(GenericTypeResolver.resolveTypeArguments(this.getClass(), EcommerceService.class))[0];
    }
}

```

**Atenção** para os métodos `mapEntityToValueObject` e `mapValueObjectToEntity` responsáveis por realizar o processo de `de/para - from/to` dos objetos de entidade (recurso persistente) e `ValueObject / DTO` e vice-versa.

#### com.sigga.ecommerce.crm.customer.Customer

Classe responsável por definir a estrutura de `ValueObject / DTO` para o recurso de cliente. Note que, no core, não estamos preocupados em como o recurso será persistido, mas sim, como será retornado aos clientes.

```java
@Data
public class Customer {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;

    @Size(max = 100)
    @NotNull(message = "customer name is required")
    private String name;

    @Size(max = 150)
    @NotNull(message = "customer email is required")
    private String email;
}
```

#### com.sigga.ecommerce.crm.customer.CustomerResource

Interface que definir as operações default para o recurso de cliente. Herda de `EcommerceResource<Customer>` parametrizando o `ValueObject / DTO` de cliente. Operações específicas de cliente deverão estar nessa interface.

```java
public interface CustomerResource extends EcommerceResource<Customer> {

}
```

#### com.sigga.ecommerce.inventory.product.Product

Classe responsável por definir a estrutura de `ValueObject / DTO` para o recurso de produto. Note que, no core, não estamos preocupados em como o recurso será persistido, mas sim, como será retornado aos clientes.

```java
@Data
public class Product {

    private UUID id;

    @NotNull(message = "the product name is required")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "the product price must be greater than 0")
    @NotNull(message = "the product price is required")
    private BigDecimal price;
}
```

#### com.sigga.ecommerce.inventory.product.ProductResource

Interface que definir as operações default para o recurso de produto. Herda de `EcommerceResource<Product>` parametrizando o `ValueObject / DTO` de produto. Operações específicas de cliente deverão estar nessa interface.

```java
public interface ProductResource extends EcommerceResource<Product> {

}
```

#### com.sigga.ecommerce.order.purchase.PurchaseOrder

Classe responsável por definir a estrutura de `ValueObject / DTO` para o recurso de ordem de compra. Note que, no core, não estamos preocupados em como o recurso será persistido, mas sim, como será retornado aos clientes.

```java
@Data
public class PurchaseOrder {

    private UUID id;

    @NotNull(message = "the purchase order customer is required")
    private Customer customer;

    @Valid
    private List<PurchaseOrderProduct> products;
}
```

##### com.sigga.ecommerce.order.purchase.PurchaseOrderProduct

Classe responsável por definir a estrutura de `ValueObject / DTO` do produto existente na ordem de compra.

```java
@Data
public class PurchaseOrderProduct {

    private UUID id;

    @NotNull(message = "the purchase order product is required")
    private Product product;

    @NotNull(message = "the product quantity is required")
    private Integer quantity;
}
```

#### com.sigga.ecommerce.order.purchase.PurchaseOrderResource

Interface que definir as operações default para o recurso de ordem de compra. Herda de `EcommerceResource<PurchaseOrder>` parametrizando o `ValueObject / DTO` de ordem de compra. Operações específicas de cliente deverão estar nessa interface.

```java
public interface PurchaseOrderResource extends EcommerceResource<PurchaseOrder> {

}
```

### crm

Microserviço responsável pelas operações do recurso de cliente (Customer). Expõe um conjunto de APIs REST para possibilitar a manutenção das informações de cliente. Roda na **porta 9000**.

#### com.sigga.ecommerce.crm.customer.CustomerController

Controlador REST responsável por expor as operações REST para o recurso de cliente. Herda de `EcommerceController<CustomerEntity, Customer>` parametrizando, via tipagem genérica, a entidade e o `ValueObject / DTO` e implementa a interface `CustomerResource`.

```java
@RestController
@RequestMapping("customer")
public class CustomerController extends EcommerceController<CustomerEntity, Customer> implements CustomerResource {

    public CustomerController(CustomerService service) {

        super(service);
    }
}
```

#### com.sigga.ecommerce.crm.customer.CustomerEntity

Classe responsável por fazer o mapemaneto object-relacional para a entidade de cliente.

```java
@Data
@Entity
@Table(name = "customer")
public class CustomerEntity implements EcommerceEntity {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "hibernate-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotNull
    @Size(max = 100)
    @Column(length = 100)
    private String name;

    @NotNull
    @Size(max = 150)
    @Column(length = 150)
    private String email;
}
```

#### com.sigga.ecommerce.crm.customer.CustomerRepository

Interface que expõe as operações de persistencia das informações no repositório a serem utilizadas pela classe de serviço. Herda da interface `EcommerceRepository<CustomerEntity>` parametrizando a entidade como tipo genérico.

```java
@Repository
public interface CustomerRepository extends EcommerceRepository<CustomerEntity> {

}
```

#### com.sigga.ecommerce.crm.customer.CustomerService

Classe de serviço que disponibiliza operações ao controlador e faz o acesso ao repositório de dados.

```java
@Service
public class CustomerService extends EcommerceService<CustomerEntity, Customer> {

    public CustomerService(CustomerRepository repository, ModelMapper modelMapper) {

        super(repository, modelMapper);
    }
}
```

#### com.sigga.ecommerce.crm.CrmApplication

Classe main responsável por executar o microserviço.

#### crm/src/main/resources/db/migration/V0001__customer.sql

Versão 0001 de migração no banco de dados H2 do microserviço de cliente através do Flyway.

```sql
create table if not exists customer
(
    id    uuid primary key,
    name  varchar(100) not null,
    email varchar(150) not null
);
```

#### crm/src/main/resources/db/testdata/afterMigrate\__0001\__customer.sql

Script executado após a migração do banco de dados com o objetivo de incluir dados de teste.

```sql
insert into customer (id, name, email) values ('7223fda6-4ee6-405e-89ca-64abd9ce8a10', 'AVELINO DA SILVA', 'avelino@gmail.com');
...
```

### inventory

Microserviço responsável pelas operações do recurso de produto (Product). Expõe um conjunto de APIs REST para possibilitar a manutenção das informações de produto. Roda na **porta 9001**.

#### com.sigga.ecommerce.inventory.product.ProductController

Controlador REST responsável por expor as operações REST para o recurso de cliente. Herda de `EcommerceController<ProductEntity, Product>` parametrizando, via tipagem genérica, a entidade e o `ValueObject / DTO` e implementa a interface `ProductResource`.

```java
@RestController
@RequestMapping("product")
public class ProductController extends EcommerceController<ProductEntity, Product> implements ProductResource {

    public ProductController(EcommerceService<ProductEntity, Product> service) {

        super(service);
    }
}
```

#### com.sigga.ecommerce.inventory.product.ProductEntity

Classe responsável por fazer o mapemaneto object-relacional para a entidade de produto.

```java
@Data
@Entity
@Table(name = "product")
public class ProductEntity implements EcommerceEntity {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "hibernate-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotNull
    @Size(max = 100)
    @Column(length = 100)
    private String name;

    @Column
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
}

```

#### com.sigga.ecommerce.inventory.product.ProductRepository

Interface que expõe as operações de persistencia das informações no repositório a serem utilizadas pela classe de serviço. Herda da interface `EcommerceRepository<ProductEntity>` parametrizando a entidade como tipo genérico.

```java
public interface ProductRepository extends EcommerceRepository<ProductEntity> {

}
```

#### com.sigga.ecommerce.inventory.product.ProductService

Classe de serviço que disponibiliza operações ao controlador e faz o acesso ao repositório de dados.

```java
@Service
public class ProductService extends EcommerceService<ProductEntity, Product> {

    public ProductService(ProductRepository repository, ModelMapper modelMapper) {

        super(repository, modelMapper);
    }
}
```

#### com.sigga.ecommerce.inventory.InventoryApplication

Classe main responsável por executar o microserviço.

#### inventory/src/main/resources/db/migration/V0001__product.sql

Versão 0001 de migração no banco de dados H2 do microserviço de cliente através do Flyway.

```sql
create table if not exists product
(
    id    uuid primary key,
    name  varchar(100)   not null,
    price numeric(19, 2) not null
);
```

#### inventory/src/main/resources/db/testdata/afterMigrate\__0001\__product.sql

Script executado após a migração do banco de dados com o objetivo de incluir dados de teste.

```sql
insert into product (id, name, price) values ('d06dff10-be98-436c-b9e8-27c9a0d6689a', 'PRODUCT A', 100);
...
```

### order

Microserviço responsável pelas operações do recurso de ordem de compra (PurchaseOrder). Expõe um conjunto de APIs REST para possibilitar a manutenção das informações de ordem de compra. Roda na **porta 9002**.

#### com.sigga.ecommerce.crm.customer.CustomerResourceClient

Cliente HTTP/Rest do recurso de cliente do microserviço crm. Como dito anteriormente, como centralizamos todas as operações do recurso na interface `CustomerResource`, para criar um cliente, utilizando o `Spring Cloud OpenFeign`, basta criar uma interface, herdar as definições de recurso e utilizar a configuração `@FeignClient` para mapear o nome do `spring-bean` e a url base de acesso ao recurso.

```java
@FeignClient(name = "customer", url = "http://localhost:9000/api/customer")
public interface CustomerResourceClient extends CustomerResource {

}
```

#### com.sigga.ecommerce.order.purchase.PurchaseOrderController

Controlador REST responsável por expor as operações REST para o recurso de ordem de compra. Herda de `EcommerceController<PurchaseOrderEntity, PurchaseOrder>` parametrizando, via tipagem genérica, a entidade e o `ValueObject / DTO` e implementa a interface `PurchaseOrderResource`.

```java
@RestController
@RequestMapping("purchase-order")
public class PurchaseOrderController extends EcommerceController<PurchaseOrderEntity, PurchaseOrder> implements PurchaseOrderResource {

    public PurchaseOrderController(PurchaseOrderService service) {

        super(service);
    }
}
```

#### com.sigga.ecommerce.order.purchase.PurchaseOrderEntity

Classe responsável por fazer o mapemaneto object-relacional para a entidade de ordem de compra.

```java
@Data
@Entity
@Table(name = "purchase_order")
public class PurchaseOrderEntity implements EcommerceEntity {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "hibernate-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotNull
    @Column(name = "customer_id")
    private UUID customerId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "purchaseOrder")
    private List<PurchaseOrderProductEntity> products;

    @PreUpdate
    @PrePersist
    private void preMerge() {

        if (this.products != null) {

            this.products.forEach(p -> p.setPurchaseOrder(this));
        }
    }
}
```

#### com.sigga.ecommerce.order.purchase.PurchaseOrderProductEntity

Classe responsável por fazer o mapemaneto object-relacional para a entidade fraca do(s) produto(s) da ordem de compra.

```java
@Data
@Entity
@Table(name = "purchase_order_product")
public class PurchaseOrderProductEntity implements EcommerceEntity {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "hibernate-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrderEntity purchaseOrder;

    @NotNull
    @Column(name = "product_id")
    private UUID productId;

    @Column
    private Integer quantity;
}
```

#### com.sigga.ecommerce.order.purchase.PurchaseOrderRepository

Interface que expõe as operações de persistencia das informações no repositório a serem utilizadas pela classe de serviço. Herda da interface `EcommerceRepository<PurchaseOrderEntity>` parametrizando a entidade como tipo genérico.

```java
@Repository
public interface PurchaseOrderRepository extends EcommerceRepository<PurchaseOrderEntity> {

}
```

#### com.sigga.ecommerce.order.purchase.PurchaseOrderService

Classe de serviço que disponibiliza operações ao controlador e faz o acesso ao repositório de dados.

```java
@Service
public class PurchaseOrderService extends EcommerceService<PurchaseOrderEntity, PurchaseOrder> {

    private final CustomerResourceClient customerClient;

    public PurchaseOrderService(
            PurchaseOrderRepository repository,
            ModelMapper modelMapper,
            CustomerResourceClient customerClient) {

        super(repository, modelMapper);

        this.customerClient = customerClient;
    }

    @Override
    protected PurchaseOrder mapEntityToValueObject(PurchaseOrderEntity entity) {

        var purchase = super.mapEntityToValueObject(entity);

        purchase.setCustomer(this.customerClient.findById(entity.getCustomerId()));

        return purchase;
    }
}
```

#### com.sigga.ecommerce.order.OrderApplication

Classe main responsável por executar o microserviço.

#### order/src/main/resources/db/migration/V0001__purchase.sql

Versão 0001 de migração no banco de dados H2 do microserviço de cliente através do Flyway.

```sql
create table if not exists purchase_order
(
    id          uuid primary key,
    customer_id uuid not null
);

create table if not exists purchase_order_product
(
    id                uuid primary key,
    purchase_order_id uuid    not null,
    product_id        uuid    not null,
    quantity          integer not null
);
```

## Executando o projeto

### IDEA Intellij Community Edition

- Abra a IDE
- Vá na opção Get from VCS
- Cole o link do repositório: https://github.com/cezaraf/ecommerce.git
- Clique em clone
- Após a IDEA abrir o projeto e carregar todas as dependências:
	- Abra a classe `CrmApplication` e a execute através do ícone de play;
	- Abra a classe `InventoryApplication` e a execute através do ícone de play;
	- Abra a classe `OrderApplication` e a execute através do ícone de play;

### Postman

Para interagir com as APIs expostas, instale o [Postman](https://www.postman.com/downloads/ "Postman"), e importe o [arquivo de coleção](https://raw.githubusercontent.com/cezaraf/ecommerce/master/ecommerce.collection.json "arquivo de coleção").

## Tarefas

- [ ] Ao detalhar uma ordem de compra é possível notar que os campos `name` e `price` do produto não estão sendo retornados. Ajuste o sistema para que esses dados sejam retornados no detalhamento da ordem de compra.

- [ ] Após realizar uma ordem de compra, atualizar o preço do produto e mandar detalhar a ordem de compra, note que o valor do produto na ordem foi alterado, o que não deveria acontecer. Ajuste o sistema para que o mesmo armazene o preço do produto no momento da criação da ordem de compra.
- [ ] Após a compra, é necessário que um e-mail seja enviado ao cliente de forma assíncrona com o detalhamento da compra. Não é necessário fazer a integraçao com um servidor SMPT real, basta criar um mecanismo no qual o e-mail é exibido na saída padrão.
