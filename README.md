# _Java-shareit_

### _Descripton_

The service for sharing items. Backend.  

The service allows users to give a description of items they have for sharing, allows to find 
desirable item and rent it. It allows to book items for particular time. Users may send request 
for items they required and receive sharing offers in return.  

### _Technologies_

Spring-Boot (2.7.1), jpa, postgresql (43.3.6), h2 (2.1.214), java (sdk 11), lombok (1.18.24),
docker(20.10.17), docker-compose(2.10.2).

### _Structure_   

Project consists of two modules (microservices):
1. _Shareit-gateway_ - the gateway filters incorrect requests and then address shareit-main server.  
The module depends on shareit-main server.
Runs on port 8080.


2. _Shareit-server_ - the service with main logic. Shareit-server works with postgres database.  
Runs on port 9090.

### _Entity relationship_  
![Entity relationship](./ER/ER.png)

### _Starting the service_
CLI start command: `docker-compose -p shareit up`

Manual run:
1. Set environmental variables.
2. Run database.
3. Run shareit-server.
4. Run shareit-gateway.

Environmental variables set by default in docker-compose:

1. Shareit-server:
   - DB_PASSWORD=psw
   - DB_USER=SI
   - DB_NAME=shareit
   - DB_HOST=db
   - DB_PORT=5432
   - SERVER_PORT=9090

2. Shareit-gateway:
   - SERVER_URL=`http://server:9090`
___

### _Update history_

## *`Sprint 4`*
#### *- Implement microservice architecture (gateway/server/database)*
#### *- Move incoming data validation to module Gateway*
#### *- Gateway: add BookingController, ItemController, ItemRequestController and UserController*
#### *- Gateway: add ExceptionHandler*
#### *- Gateway: add BasicClient class*
#### *- Gateway: add BookingClient, ItemClient, ItemRequestClient and UserClient*
#### *- Add Dockerfiles*
#### *- Add docker-compose.yml*
#### *- Add unit tests for controllers and clients*
___

### *`Sprint 3`*
##### *- Add ItemRequestController, ItemRequestService, ItemRequestRepository, ItemRequestExceptions, ItemRequestDto, ItemRequestDtoMapper*
##### *- Add endpoints POST /requests, GET /requests, GET /requests/all?from={from}&size={size}, GET /requests/{requestId}*
##### *- Add pagination to GET /requests/all, GET /items, GET /items/search, GET /bookings и GET /bookings/owner endpoints*
##### *- Add unit tests (controller tests, repository tests, service tests, json dto tests) and integration tests (service + database)*
___

### *`Sprint 2`*
##### *- Add JPA support*
##### *- Add PostgreSql database*
##### *- Refactor User, Item, Booking, ItemRequest models and add Comment model*
##### *- Add schema.sql file*
##### *- Add UserRepository, ItemRepository, BookingRepository, ItemRequestRepository and CommentRepository*
##### *- Refactor UserService, ItemService and add BookingService*
##### *- Add Exceptions for Booking and Item packages*
##### *- Add Dto to Item, Booking, Comment models*
##### *- Add DtoMapper to Item, Bookings and Comment DTOs*
##### *- Add endpoints POST /bookings, PATCH /bookings/{bookingId}?approved={approved}, GET /bookings/{bookingId}, GET /bookings?state={state}, GET /bookings/owner?state={state}, POST /items/{itemId}/comment*
___
### *`Sprint 1`*
##### *- Add UserController and ItemController*
##### *- Add UserService and ItemService*
##### *- Add UserDao and ItemDao*
##### *- Add UserDao and ItemDao in memory implementations*
##### *- Add User and Item models*
##### *- Add User and Item exceptions (Runtime)*
##### *- Add User and Item exception handlers*
##### *- Add User and Item DTO (data transfer object)*
##### *- Add UserDtoMapper and ItemDtoMapper*
##### *- Add endpoints Post /users, Patch /users/{userId}, Get /users/{userId}, Delete /users/{userId}, Get /users, Post /items, Patch /items/{itemId}, Get /items/{itemId}, Get /items, Get /items/search*
##### *- Add tests for endpoints Post /users, Patch /users/{userId}, Get /users/{userId},Delete /users/{userId}, Get /users, Post /items, Patch /items/{itemId}, Get /items/{itemId}, Get /items, Get /items/search*
___
    