# gRPC Service

Below are three scenarios for gRPC flight bookings service. The service is able to list all flight bookings, add a new booking, remove a booking and delete a booking. 

The booking entry is composed of a passenger info, departure and arrival airport and departure and arrival date. All dates and airports correlate and when creating/updating an entry the client is only able to work with predifined departures and arrivals.

The service also returns a return code and message whether the request is successful or not.

First test scenario is to list all flight bookings.
```
.\grpcurl.exe -plaintext  localhost:8000 describe model.FlightBookingService                                     
model.FlightBookingService is a service:
service FlightBookingService {
  rpc createFlightBookings ( .model.BookingsCreateRequest ) returns ( .model.BookingsResponse );
  rpc deleteFlightBookings ( .model.BookingsDeleteRequest ) returns ( .model.BookingsResponse );
  rpc getFlightBookings ( .google.protobuf.Empty ) returns ( .model.Bookings );
  rpc updateFlightBookings ( .model.BookingsUpdateRequest ) returns ( .model.BookingsResponse );
}


.\grpcurl.exe -plaintext  localhost:8000 model.FlightBookingService.getFlightBookings

{                                          
  "booking": [                             
    {                                      
      "id": "1",                           
      "passenger": {                       
        "name": "PAvel NOvak"              
      },                                   
      "departureAirport": "Paris",         
      "arrivalAirport": "Hong Kong",       
      "departureDate": "20/02/2022T05:20:",
      "arrivalDate": "20/02/2022T10:20"    
    },                                     
    {
      "id": "2",
      "passenger": {
        "name": "Ondra Veselý"
      },
      "departureAirport": "Chicago",
      "arrivalAirport": "Hong Kong",
      "departureDate": "26/04/2022T14:15",
      "arrivalDate": "26/04/2022T18:15"
    }
  ]
}
```

Second scenario adds a and deletes a booking.

```
.\grpcurl.exe -plaintext  localhost:8000 model.FlightBookingService.getFlightBookings
{                                          
  "booking": [                             
    {                                      
      "id": "1",                           
      "passenger": {                       
        "name": "PAvel NOvak"              
      },                                   
      "departureAirport": "Paris",         
      "arrivalAirport": "Hong Kong",       
      "departureDate": "20/02/2022T05:20:",
      "arrivalDate": "20/02/2022T10:20"    
    },                                     
    {
      "id": "2",
      "passenger": {
        "name": "Ondra Veselý"
      },
      "departureAirport": "Chicago",
      "arrivalAirport": "Hong Kong",
      "departureDate": "26/04/2022T14:15",
      "arrivalDate": "26/04/2022T18:15"
    }
  ]
}

 .\grpcurl.exe -plaintext -d '{\"passenger\": \"David Světlý\", \"departureAirport\": \"Paris\", \"arrivalAirport\
": \"Hong Kong\", \"departureDate\": \"12/02/2022T14:15\", \"arrivalDate\": \"12/02/2022T19:15\"}'  localhost:8000 model.FlightBookingService.createFlightBookings

{
  "code": "0",
  "message": "Create: Successfully created a record!"
}

.\grpcurl.exe -plaintext  localhost:8000 model.FlightBookingService.getFlightBookings
{             
  "booking": [
    {
      "id": "1",
      "passenger": {
        "name": "PAvel NOvak"
      },
      "departureAirport": "Paris",
      "arrivalAirport": "Hong Kong",
      "departureDate": "20/02/2022T05:20:",
      "arrivalDate": "20/02/2022T10:20"
    },
    {
      "id": "2",
      "passenger": {
        "name": "Ondra Veselý"
      },
      "departureAirport": "Chicago",
      "arrivalAirport": "Hong Kong",
      "departureDate": "26/04/2022T14:15",
      "arrivalDate": "26/04/2022T18:15"
    },
    {
      "id": "3",
      "passenger": {
        "name": "David Světlý"
      },
      "departureAirport": "Paris",
      "arrivalAirport": "Hong Kong",
      "departureDate": "12/02/2022T14:15",
      "arrivalDate": "12/02/2022T19:15"
    }
  ]
}

.\grpcurl.exe -plaintext -d '{\"id\": \"2\"}'  localhost:8000 model.FlightBookingService.deleteFlightBookings 

{             
  "code": "0",
  "message": "Delete: Successfully deleted a booking!"
}

.\grpcurl.exe -plaintext  localhost:8000 model.FlightBookingService.getFlightBookings                            
{
  "booking": [
    {
      "id": "1",
      "passenger": {
        "name": "PAvel NOvak"
      },
      "departureAirport": "Paris",
      "arrivalAirport": "Hong Kong",
      "departureDate": "20/02/2022T05:20:",
      "arrivalDate": "20/02/2022T10:20"
    },
    {
      "id": "3",
      "passenger": {
        "name": "David Světlý"
      },
      "departureAirport": "Paris",
      "arrivalAirport": "Hong Kong",
      "departureDate": "12/02/2022T14:15",
      "arrivalDate": "12/02/2022T19:15"
    }
  ]
}
```

Third scenario updates and deletes an unknown a booking. The client is able to update only certain aspects of the entry that means that all parametes are optional (except for id).

```
.\grpcurl.exe -plaintext  localhost:8000 model.FlightBookingService.getFlightBookings

{
  "booking": [
    {
      "id": "1",
      "passenger": {
        "name": "PAvel NOvak"
      },
      "departureAirport": "Paris",
      "arrivalAirport": "Hong Kong",
      "departureDate": "20/02/2022T05:20:",
      "arrivalDate": "20/02/2022T10:20"
    },
    {
      "id": "2",
      "passenger": {
        "name": "Ondra Veselý"
      },
      "departureAirport": "Chicago",
      "arrivalAirport": "Hong Kong",
      "departureDate": "26/04/2022T14:15",
      "arrivalDate": "26/04/2022T18:15"
    }
  ]
}

.\grpcurl.exe -plaintext -d '{\"id\": \"1\", \"departureAirport\": \"Paris\", \"departureDate\": \"10/03/2022T18:
15\", \"arrivalAirport\": \"Chicago\", \"arrivalDate\": \"10/03/2022T22:15\"}'  localhost:8000 model.FlightBookingService.updateFlightBookings  

{
  "code": "0",
  "message": "Update: Successfully updated a record!"
}


.\grpcurl.exe -plaintext  localhost:8000 model.FlightBookingService.getFlightBookings

{       
  "booking": [
    {
      "id": "1",
      "passenger": {
        "name": "PAvel NOvak"
      },
      "departureAirport": "Paris",
      "arrivalAirport": "Chicago",
      "departureDate": "10/03/2022T18:15",
      "arrivalDate": "10/03/2022T22:15"
    },
    {
      "id": "2",
      "passenger": {
        "name": "Ondra Veselý"
      },
      "departureAirport": "Chicago",
      "arrivalAirport": "Hong Kong",
      "departureDate": "26/04/2022T14:15",
      "arrivalDate": "26/04/2022T18:15"
    }
  ]
}

.\grpcurl.exe -plaintext -d '{\"id\": \"100\"}'  localhost:8000 model.FlightBookingService.deleteFlightBookings  

{             
  "code": "1",
  "message": "Record not found!"
}
```

Last test case showcases the error message in the form of incorrect date and unknown flight.
```
.\grpcurl.exe -plaintext -d '{\"id\": \"1\", \"departureAirport\": \"Paris\", \"departureDate\": \"10/03/2022T18:
15\", \"arrivalAirport\": \"Chicago\", \"arrivalDate\": \"10/03/T22:15\"}'  localhost:8000 model.FlightBookingService.updateFlightBookings

{                                                                      
  "code":"2",                                                       
  "message": "Create: Incorrect date format! Shall be dd/MM/yyyyTHH:mm"
}

 .\grpcurl.exe -plaintext -d '{\"id\": \"1\", \"departureAirport\": \"Paris\", \"departureDate\": \"10/03/2022T18:
15\", \"arrivalAirport\": \"Chicago\", \"arrivalDate\": \"10/03/2012T22:15\"}'  localhost:8000 model.FlightBookingService.updateFlightBookings   
   
{
  "code": "2",
  "message": "Update: Non existing flights!"
}

```