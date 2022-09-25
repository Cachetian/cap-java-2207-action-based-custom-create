# CAP action based custom create sample
* Run Commnad
```shell
cds deploy --to sqlite
mvn spring-boot:run 
```
* Environment variables
  * key: destination
  * value:
    ```json
    [
      {
        "type": "HTTP",
        "name": "covid19api",
        "proxyType": "Internet",
        "description": "call restapi sample",
        "authentication": "NoAuthentication",
        "url": "https://api.covid19api.com"
      }
    ]
    ```
* App URL: http://localhost:8080/fiori.html#create-books