package grpc.server;

import java.io.IOException;
import model.PaymentServiceGrpc.PaymentServiceImplBase;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

public class PaymentService extends PaymentServiceImplBase {

  private static final ServiceRepository repository = new ServiceRepository();

  public static void main(String[] args) throws IOException, InterruptedException {
    Server server = ServerBuilder
        .forPort(8000)
        .addService(new PaymentService())
        .addService(ProtoReflectionService.newInstance())
        .build();

    server.start();
    server.awaitTermination();
  }

}