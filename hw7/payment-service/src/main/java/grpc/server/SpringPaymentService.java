package grpc.server;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import model.PaymentCreateRequest;
import model.PaymentServiceGrpc.PaymentServiceImplBase;

import model.Payments;
import model.PaymentsResponse;
import io.grpc.stub.StreamObserver;
import model.Cards;
import model.CardsServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.devh.boot.grpc.server.service.GrpcService;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Map;


@GrpcService
@SpringBootApplication
public class SpringPaymentService extends PaymentServiceImplBase {

    @Autowired
    private ServiceRepository repository;

    @Override
    public void getPayments(
            Empty request,
            StreamObserver<Payments> responseObserver)
    {
        Payments response = Payments.newBuilder()
                .addAllPayments(repository.getPayments())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createPayment(
            PaymentCreateRequest request,
            StreamObserver<PaymentsResponse> responseObserver)
    {
        String code;
        String message;
        ManagedChannel channel = ManagedChannelBuilder.forAddress("ni-am.fit.cvut.cz", 9090)
                .usePlaintext()
                .build();
        CardsServiceGrpc.CardsServiceBlockingStub stub = CardsServiceGrpc.newBlockingStub(channel);

        Cards.Card card = Cards.Card.newBuilder()
                .setCardNumber(request.getCardNumber())
                .setCardOwner(request.getCardOwner())
                .build();

        BoolValue res = stub.validateCard(card);
        if(!res.getValue()){
            code = "1";
            message = "Wrong card credentials!";
        }
        else {
            Map<Integer, String> result = repository.createPayment(request);
            code = result.get(0);
            message = result.get(1);
        }
        channel.shutdown();

        PaymentsResponse response = PaymentsResponse.newBuilder()
                .setCode(code)
                .setMessage(message)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringPaymentService.class, args);
    }
}
