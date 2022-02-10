# Widgets REST API

### Description
A web service to work with widgets via HTTP REST API. 
The service stores only widgets, assuming that all clients work with the same board.

A Widget is an object on a plane in a ​Cartesian coordinate system that has coordinates (X, Y), Z-index, width, height, last modification date, and a ​unique identifier​. X, Y, and Z-index are integers (may be negative). Width and height are integers > 0.
Widget attributes should be not null.
A Z-index​ is a unique sequence common to all widgets that determines the order of widgets (regardless of their coordinates). Gaps are allowed.​ The higher the value, the higher the widget lies on the plane.

### Run instance locally
```
./mvnw spring-boot:run
```

### Run tests locally
```
./mvnw test
```

### Endpoints
1. Fetch page of widgets
```
GET /api/v1/widgets?limit=10&offset=0
[
    {
        "id": "375eecb2-2c12-4bc7-abf4-68e2d63b4f57",
        "x": 31,
        "y": 30,
        "z": 1,
        "height": 301,
        "width": 301,
        "lastModificationDate": "2022-02-10T15:44:11.996641Z"
    }
]
```

2) Fetch specific widget 
```
GET /api/v1/widgets/375eecb2-2c12-4bc7-abf4-68e2d63b4f57
{
    "id": "375eecb2-2c12-4bc7-abf4-68e2d63b4f57",
    "x": 31,
    "y": 30,
    "z": 1,
    "height": 301,
    "width": 301,
    "lastModificationDate": "2022-02-10T15:44:11.996641Z"
}
```

3) Create widget
```
POST /api/v1/widgets
{
    "x": 31,
    "y": 30,
    "z": 1,
    "height": 301,
    "width": 301
}
```
4) Update widget
```
PUT /api/v1/widgets/375eecb2-2c12-4bc7-abf4-68e2d63b4f57
{
    "x": 31,
    "y": 30,
    "z": 1,
    "height": 301,
    "width": 301
}
```
5) Delete widget
```
DELETE /api/v1/widgets/375eecb2-2c12-4bc7-abf4-68e2d63b4f57
```

### What was left out of scope & Flaws
1. Request examples above. Open API specification with Swagger would be much better.
2. This README.MD. It lacks description of internal implementation.
3. Pagination. Pagination is not very efficient as it traverses entries until it reaches those that are specified by offset. Having cursor-based pagination would be much better.
5. Logging. Requests and business operations are not logged.
6. Tests. No tests for controller and service.
7. Lack of documentation. 
8. No metrics & healthchecks.
9. Not Cloud ready - no Dockerfile/k8s spec/Helm chart.

Sorry =[

