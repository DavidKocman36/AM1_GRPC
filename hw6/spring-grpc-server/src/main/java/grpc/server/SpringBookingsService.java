package grpc.server;

import cz.cvut.fit.niam1.grpc.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.protobuf.Empty;

import cz.cvut.fit.niam1.grpc.model.FlightBookingServiceGrpc.FlightBookingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.HashMap;
import java.util.Map;

@GrpcService
@SpringBootApplication
public class SpringBookingsService extends FlightBookingServiceImplBase {

  @Autowired
  private ServiceRepository repository;
  private Map<Integer, String> result = new HashMap<>();

  @Override
  public void getFlightBookings(
          Empty request,
          StreamObserver<Bookings> responseObserver)
  {
    // Get the bookings and send the in the response
    Bookings response = Bookings.newBuilder()
            .addAllBooking(repository.getBookings())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void deleteFlightBookings(
          BookingsDeleteRequest request,
          StreamObserver<BookingsResponse> responseObserver)
  {
    // Delete a booking
    result = repository.deleteBooking(request.getId());
    String code = result.get(0);
    String message = result.get(1);

    // Send the error code in the resposne
    BookingsResponse response = BookingsResponse.newBuilder()
            .setCode(code)
            .setMessage(message)
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void createFlightBookings(
          BookingsCreateRequest request,
          StreamObserver<BookingsResponse> responseObserver)
  {
    String code;
    String message;

    // Check the corectness of the date format
    if(!request.getArrivalDate().matches("[0-9]{2}/[0-9]{2}/[0-9]{4}T[0-2][0-9]:[0-5][0-9]") || !request.getDepartureDate().matches("[0-9]{2}/[0-9]{2}/[0-9]{4}T[0-2][0-9]:[0-5][0-9]")){
      code = "2";
      message = "Create: Incorrect date format! Shall be dd/MM/yyyyTHH:mm";
    }
    else{
      result = repository.addBooking(request);
      code = result.get(0);
      message = result.get(1);
    }

    // Send the return code back.
    BookingsResponse response = BookingsResponse.newBuilder()
            .setCode(code)
            .setMessage(message)
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void updateFlightBookings(
          BookingsUpdateRequest request,
          StreamObserver<BookingsResponse> responseObserver)
  {
    String code;
    String message;

    // Check the corectness of the date format.
    if((request.hasArrivalDate() && !request.getArrivalDate().matches("[0-9]{2}/[0-9]{2}/[0-9]{4}T[0-2][0-9]:[0-5][0-9]")) ||
            (request.hasDepartureDate() && !request.getDepartureDate().matches("[0-9]{2}/[0-9]{2}/[0-9]{4}T[0-2][0-9]:[0-5][0-9]"))){
      code = "2";
      message = "Create: Incorrect date format! Shall be dd/MM/yyyyTHH:mm";
    }
    else{
      result = repository.updateBooking(request);
      code = result.get(0);
      message = result.get(1);
    }

    // Send the return code.
    BookingsResponse response = BookingsResponse.newBuilder()
            .setCode(code)
            .setMessage(message)
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  public static void main(String[] args) {
    SpringApplication.run(SpringBookingsService.class, args);
  }
}
