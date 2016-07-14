<b>Build application:</b>
gradle build

<b>Create config file :</b>
Create your own config.properties file or copy from project /doc folder to "file:${user.home}/.loanapplication/config.properties"

<b>Start server:</b>
java -jar build/libs/loanapplication-1.0.0.jar

<b>To execute on Linux:</b>  
GET: curl -i -H "Content-Type: application/json" -X GET $URL  
POST: curl -i -H "Content-Type: application/json" -X POST -d '{$JSON}' $URL  

<b>To execute on Windows:</b>  
Install FireFox plugin Poster

<b>API reference:</b>  

**Apply for loan :**
URL: POST http://localhost:8080/loan
Content Type = application/json
Sample of request :
{  "clientId":"131",
   "firstName":"John",
   "lastName":"Boskin",
   "amount":22.33
}

params in JSON: 
clientId : String (Mandatory)
firstName : String (Mandatory)
lastName : String (Mandatory)
amount : Numeric in format xxxx.xx (min=1, max=1000) (Mandatory)   

According the task description there are following requirements validation rules are applied:
  - Application comes from blacklisted personal id and client already exist in the database
  - N application (number is configurable via com.loanapp.number.session.second, value initialy set to 2) / second are received from a single country (com.loanapp.default.country=LV)
  - If requested clientId already exist in the database provided firstName and lastName must match ones in the database


 201 CREATED - success response message, loan is created successfully.


**List all approved loans :**
URL: GET http://localhost:8080/loans/all  
Expected success response: 200 OK  
Example of success response body: [{"id":1,"amount":22.33,"client":{"id":"138","firstName":"John","lastName":"Boskin","blacklisted":false},"loanCountry":"LV"}]



**To retrieve single loan by id:**  
URL: GET http://localhost:8080/loan/{id}  
params:  
id : Long (Mandatory)  

Expected success response: 200 OK  
Example of success response body: {"id":1,"amount":22.33,"client":{"id":"138","firstName":"John","lastName":"Boskin","blacklisted":false},"loanCountry":"LV"}


**List all approved loans by client:**
URL: GET http://localhost:8080/loans/user/{clientId}
params:  
clientId : String (Mandatory)

Expected success response: 200 OK  
Example of success response body: [{"id":1,"amount":22.33,"client":{"id":"138","firstName":"John","lastName":"Boskin","blacklisted":false},"loanCountry":"LV"},{"id":2,"amount":222.32,"client":{"id":"138","firstName":"John","lastName":"Boskin","blacklisted":false},"loanCountry":"LV"}]


**To retrieve client details by clientId:**
URL: GET http://localhost:8080/client/{id}
params:  
id : String (Mandatory)  

Expected success response: 200 OK  
Example of success response body: {"id":"138","firstName":"John","lastName":"Boskin","blacklisted":false}


**Put client to blacklist by clientId:**
URL: PUT http://localhost:8080/client/blacklist/add/{id}
params:  
id : String (Mandatory)

Following validation rules are applied: 
  - Client must exist in the database,
  - Client must not be already blacklisted;

Expected success response: 200 OK

Check client state GET http://localhost:8080/client/{id}
Example of success response body: {"id":"138","firstName":"John","lastName":"Boskin","blacklisted":true}

**Remove client from blacklist by clientId:**
URL: PUT http://localhost:8080/client/blacklist/remove/{id}
params:
id : String (Mandatory)

Following validation rules are applied:
  - Client must exist in the database,
  - Client must be already blacklisted;

Expected success response: 200 OK

Check client state GET http://localhost:8080/client/{id}
Example of success response body: {"id":"138","firstName":"John","lastName":"Boskin","blacklisted":false}
