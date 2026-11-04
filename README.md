# Online Shopping Application – Spring Boot REST API

RESTful backend for an online shop: customers can browse products, manage a shopping cart,
place orders, cancel eligible orders and view order history. Stock and totals are calculated
from database records.

**Tech:** Java 17, Spring Boot, Spring Data JPA, MySQL, Lombok, Bean Validation.
No DTO classes, no JWT / Spring Security (as required).

## Project structure
```
controller/   REST endpoints
service/      business rules (stock, cart, orders)
repository/   Spring Data JPA repositories
model/        Customer, Product, Cart, CartItem, CustomerOrder, OrderItem
exception/    custom exceptions + GlobalExceptionHandler (uniform JSON errors)
docs/         API-TESTS.md (success + failure examples)
```

## Setup
1. Install Java 17+, Maven (or use the included `mvnw`) and MySQL.
2. Start MySQL. The database `shopping_db` is created automatically
   (`createDatabaseIfNotExist=true`) and tables are created by Hibernate (`ddl-auto=update`).
3. Set your MySQL credentials in `src/main/resources/application.properties`
   (currently username `root`, password `abes@123`):
   ```
   spring.datasource.username=root
   spring.datasource.password=abes@123
   ```
4. Run:
   ```
   ./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
   ```
   The API is available at `http://localhost:8080`.

## Endpoints

### Customers
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/customers/register` | Register customer |
| GET | `/api/customers` | Get all customers |
| GET | `/api/customers/{id}` | Get customer by ID |
| PUT | `/api/customers/{id}` | Update customer |
| DELETE | `/api/customers/{id}` | Delete customer |

Rules: email must be unique and valid, mobile must be exactly 10 digits, customer name cannot be blank.
The password is accepted in requests but never returned in responses.

### Products
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Add product |
| GET | `/api/products` | Get all products |
| GET | `/api/products/search?name=` | Search products by name (partial, case-insensitive) |
| GET | `/api/products/{id}` | Get product by ID |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |
| GET | `/api/products/category/{category}` | Get products by category |

Rules: price > 0, quantity ≥ 0, name mandatory. A product that is part of an existing order cannot be deleted.

### Shopping cart
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/cart/add/{customerId}/{productId}?quantity=1` | Add product to cart |
| GET | `/api/cart/{customerId}` | View customer's cart (includes `cartTotal`) |
| PUT | `/api/cart/update/{cartItemId}?quantity=n` | Update cart quantity |
| DELETE | `/api/cart/remove/{cartItemId}` | Remove product from cart |
| DELETE | `/api/cart/clear/{customerId}` | Clear entire cart |

Business rules: one active cart per customer; adding an existing product increases the quantity of
the existing cart item; requested quantity cannot exceed available stock; the cart item price is
always copied from the product record; cart total = Σ (price × quantity).

### Orders
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders/place/{customerId}` | Place order from the cart (status `PLACED`, stock reduced, cart cleared) |
| GET | `/api/orders/customer/{customerId}` | Order history (latest first) |
| PUT | `/api/orders/cancel/{orderId}` | Cancel an order (only `PLACED` orders; stock restored; status `CANCELLED`) |

## Error format
Every failure returns JSON, e.g. `400 Bad Request`:
```json
{ "status": 400, "error": "Mobile number must contain 10 digits", "details": { "mobile": "Mobile number must contain 10 digits" } }
```
`404` – record not found, `400` – validation / business-rule failure.

## Testing
See `docs/API-TESTS.md` for curl examples with success and failure responses.
