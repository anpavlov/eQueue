#User.details
Get user details

## Supported request methods 
* GET

##Arguments

###Requried
* token

   ```str``` user token


Requesting http://some.host.ru/api/user/details/?token=a52479f5-323f-45c8-908d-dea511344e0c

Response:
```json
{
    "code": 0,
    "body": {
        "username": "anonym281",
        "email": "example@mail.ru"
    }
}
```
