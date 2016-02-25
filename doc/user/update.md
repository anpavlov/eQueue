#User.update
Edit user details

## Supported request methods 
* POST

##Supported formats
* json

##Arguments


###Requried
* token

   ```str``` user token


###Optional
* username

   ```str``` user name
* email

   ```str``` user email

Requesting http://some.host.ru/api/user/update/ with *{"token": "a52479f5-323f-45c8-908d-dea511344e0c", "username": "alex", "email": "alex@mail.ru"}*:

Response:
```json
{
    "code": 0,
    "body": {
        "username": "alex",
        "email": "alex@mail.ru"
    }
}
```
