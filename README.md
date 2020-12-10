## Grpc con Spring Boot y Quarkus

En este ejemplo vemos como usar grpc con spring boot y quarkus:

- Spring boot 
- Quarkus
- Grpc

Lo siguiente es generar los binarios de productor y consumidor

```shell script
mvn clean install
```

Una vez generado los binarios podemos atacar el api de grpc usando la herramienta **grpcurl**:


### Grpc Spring Boot

 - Arrancar el servicio de grpc con el siguiente commando 

```shell script
mvn spring-boot:run
```

 - Con el siguiente comando vamos a listar los servicios que tiene 

```shell script
grpcurl --plaintext localhost:6565 list
com.xabe.CarEndPoint
grpc.health.v1.Health
grpc.reflection.v1alpha.ServerReflection
```

- Ahora vamos a listar los endpoints que tiene nuestro servicio de **CarEnpoint**

```shell script
grpcurl --plaintext localhost:6565 list com.xabe.CarEndPoint
com.xabe.CarEndPoint.GetCar
com.xabe.CarEndPoint.SearchCar
com.xabe.CarEndPoint.UploadCarImage
```

- Tambien obtener la definición de los servicios

```shell script
grpcurl --plaintext localhost:6565 describe com.xabe.CarEndPoint
com.xabe.CarEndPoint is a service:
service CarEndPoint {
  rpc GetCar ( .com.xabe.car.GetCarRequest ) returns ( .com.xabe.car.GetCarResponse );
  rpc SearchCar ( .com.xabe.car.SearchCarRequest ) returns ( stream .com.xabe.car.SearchCarResponse );
  rpc UploadCarImage ( stream .com.xabe.car.UploadCarImageRequest ) returns ( .com.xabe.car.UploadCarImageResponse );
}
```

```shell script
grpcurl --plaintext localhost:6565 describe com.xabe.car.GetCarRequest
com.xabe.car.GetCarRequest is a message:
message GetCarRequest {
  string id = 1;
}
```

- Con los siguientes comandos vamos a invocar a cada uno de los endpoints:

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.CarEndPoint/GetCar <<EOM
{
 "id":"1"
 }
EOM
```

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.CarEndPoint/SearchCar <<EOM
{
 "brand":"mazda"
}
EOM
```

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.CarEndPoint/UploadCarImage <<EOM
{"car_info":{"id" : "1"}}
{"car_data":{"chunk_data":[]}}
EOM
```

### Grpc Quarkus

 - Arrancar el servicio de grpc con el siguiente commando 

```shell script
mvn quarkus:dev
```

 - Con el siguiente comando vamos a listar los servicios que tiene 

```shell script
grpcurl --plaintext localhost:6565 list
com.xabe.CarEndPoint
grpc.health.v1.Health
```

- Ahora vamos a listar los endpoints que tiene nuestro servicio de **CarEnpoint**

```shell script
grpcurl --plaintext localhost:6565 list com.xabe.CarEndPoint
com.xabe.CarEndPoint.GetCar
com.xabe.CarEndPoint.GetNavigateCar
```

- Tambien obtener la definición de los servicios

```shell script
grpcurl --plaintext localhost:6565 describe com.xabe.CarEndPoint
com.xabe.CarEndPoint is a service:
service CarEndPoint {
  rpc GetCar ( .com.xabe.car.GetCarRequest ) returns ( .com.xabe.car.GetCarResponse );
  rpc GetNavigateCar ( stream .com.xabe.car.GetNavigateCarRequest ) returns ( stream .com.xabe.car.GetNavigateCarResponse );
}
```

```shell script
grpcurl --plaintext localhost:6565 describe com.xabe.car.GetCarRequest
com.xabe.car.GetCarRequest is a message:
message GetCarRequest {
  string id = 1;
}
```

- Con los siguientes comandos vamos a invocar a cada uno de los endpoints:

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.CarEndPoint/GetCar <<EOM
{
 "id":"1"
 }
EOM
```

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.CarEndPoint/GetNavigateCar <<EOM
{"id":"1","distance_travelled":5}
{"id":"1","distance_travelled":15}
{"id":"1","distance_travelled":30}
{"id":"1","distance_travelled":25}
{"id":"1","distance_travelled":25}
EOM
