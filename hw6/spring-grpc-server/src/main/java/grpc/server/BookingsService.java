package grpc.server;

import java.io.IOException;
import cz.cvut.fit.niam1.grpc.model.FlightBookingServiceGrpc.FlightBookingServiceImplBase;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

public class BookingsService extends FlightBookingServiceImplBase {

  private static final ServiceRepository repository = new ServiceRepository();

  public static void main(String[] args) throws IOException, InterruptedException {
    Server server = ServerBuilder
        .forPort(8000)
        .addService(new BookingsService())
        .addService(ProtoReflectionService.newInstance())
        .build();

    server.start();
    server.awaitTermination();
  }

}