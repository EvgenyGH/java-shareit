# **Java-shareit**

###### The service for sharing items. Backend.
###### The service allows users to give a description of items they have for sharing, allows to find desirable item and rent it. It allows to book items for particular time. Users may send request for items they required and receive sharing offers in return.
###### Spring-Boot, jpa, postgresql, h2, java-core, lombok

## *`Sprint 3`*
#### *- *
#### *- *
#### *- *
#### *- *
#### *- Add endpoints POST /requests, GET /requests, GET /requests/all?from={from}&size={size}, GET /requests/{requestId}*
___

## *`Sprint 2`*
#### *- Add JPA support*
#### *- Add PostgreSql database*
#### *- Refactor User, Item, Booking, ItemRequest models and add Comment model*
#### *- Add schema.sql file*
#### *- Add UserRepository, ItemRepository, BookingRepository, ItemRequestRepository and CommentRepository*
#### *- Refactor UserService, ItemService and add BookingService*
#### *- Add Exceptions for Booking and Item packages*
#### *- Add Dto to Item, Booking, Comment models*
#### *- Add DtoMapper to Item, Bookings and Comment DTOs*
#### *- Add endpoints POST /bookings, PATCH /bookings/{bookingId}?approved={approved}, GET /bookings/{bookingId}, GET /bookings?state={state}, GET /bookings/owner?state={state}, POST /items/{itemId}/comment*
___
*Entity relationship*:  
![Entity relationship](/ER/ER.png)
___

## *`Sprint 1`*
#### *- Add UserController and ItemController*
#### *- Add UserService and ItemService*
#### *- Add UserDao and ItemDao*
#### *- Add UserDao and ItemDao in memory implementations*
#### *- Add User and Item models*
#### *- Add User and Item exceptions (Runtime)*
#### *- Add User and Item exception handlers*
#### *- Add User and Item DTO (data transfer object)*
#### *- Add UserDtoMapper and ItemDtoMapper*
#### *- Add endpoints Post /users, Patch /users/{userId}, Get /users/{userId}, Delete /users/{userId}, Get /users, Post /items, Patch /items/{itemId}, Get /items/{itemId}, Get /items, Get /items/search*
#### *- Add tests for endpoints Post /users, Patch /users/{userId}, Get /users/{userId},Delete /users/{userId}, Get /users, Post /items, Patch /items/{itemId}, Get /items/{itemId}, Get /items, Get /items/search*
___
    