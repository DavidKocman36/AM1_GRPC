package grpc.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import model.Payment;
import model.PaymentCreateRequest;
import org.springframework.stereotype.Component;

import static java.util.Map.entry;

@Component
public class ServiceRepository {

    private static final List<Payment> payments = new ArrayList<>();

    @PostConstruct
    public void initRepo(){
        payments.add(Payment.newBuilder()
                .setOrderId("1234")
                .setCardNumber("1234-1234-1234-1234")
                .setCardOwner("TojsemJa")
                .build());
    }

    public List<Payment> getPayments()
    {
        return payments;
    }

    public Map<Integer, String> createPayment(PaymentCreateRequest request)
    {
        payments.add(Payment.newBuilder()
                .setOrderId(request.getOrderId())
                .setCardNumber(request.getCardNumber())
                .setCardOwner(request.getCardOwner())
                .build());

        Map<Integer, String> ret = new HashMap<>();
        ret.put(0, "0");
        ret.put(1, "Payment created!");
        return ret;
    }
}
