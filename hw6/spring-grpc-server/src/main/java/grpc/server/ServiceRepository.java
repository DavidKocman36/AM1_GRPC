package grpc.server;

import java.util.*;

import javax.annotation.PostConstruct;

import cz.cvut.fit.niam1.grpc.model.*;
import org.springframework.stereotype.Component;


@Component
public class ServiceRepository {

    private static final List<Booking> bookings = new ArrayList<>();
    private static final List<Airport> airports = new ArrayList<>();
    private static final List<Passenger> passengers = new ArrayList<>();

    /*
    *  Error codes:
    *       0 - success
    *       1 - a non/already-existing record
    *       2 - Incorrect input parameters
    */

    @PostConstruct
    public void initRepo(){
        /*
            Departure: name is the name of the airport the plane is flying to
            Arrival: name is the name of the airport the plane is coming from
         */
        List<Dates> departureDates = Arrays.asList(createDate("Hong Kong", "20/02/2022T05:20:"),
                createDate("Hong Kong", "12/02/2022T14:15"),
                createDate("Hong Kong", "05/03/2022T14:15"),
                createDate("Chicago", "10/03/2022T18:15"),
                createDate("Chicago", "25/04/2022T09:45"));

        List<Dates> arrivalDates = Arrays.asList(createDate("Hong Kong", "21/02/2022T17:30"),
                createDate("Hong Kong", "13/02/2022T20:50"),
                createDate("Chicago", "11/03/2022T14:15"),
                createDate("Chicago", "26/04/2022T23:15"));

        airports.add(Airport.newBuilder()
                .setName("Paris")
                .addAllArrivalTimes(arrivalDates)
                .addAllDepartureTimes(departureDates)
                .build());

        departureDates = Arrays.asList(createDate("Paris", "21/02/2022T12:30"),
                createDate("Paris", "13/02/2022T15:50"),
                createDate("Chicago", "27/04/2022T01:00"));

        arrivalDates = Arrays.asList(createDate("Paris", "20/02/2022T10:20"),
                createDate("Paris", "12/02/2022T19:15"),
                createDate("Paris", "05/03/2022T19:15"),
                createDate("Chicago", "26/04/2022T18:15"));
        airports.add(Airport.newBuilder()
                .setName("Hong Kong")
                .addAllArrivalTimes(arrivalDates)
                .addAllDepartureTimes(departureDates)
                .build());

        departureDates = Arrays.asList(createDate("Hong Kong", "26/04/2022T14:15"),
                createDate("Paris", "11/03/2022T19:15"),
                createDate("Paris", "26/04/2022T12:45"));

        arrivalDates = Arrays.asList(createDate("Paris", "10/03/2022T22:15"),
                createDate("Paris", "25/04/2022T13:15"),
                createDate("Hong Kong", "27/04/2022T05:00"));
        airports.add(Airport.newBuilder()
                .setName("Chicago")
                .addAllArrivalTimes(arrivalDates)
                .addAllDepartureTimes(departureDates)
                .build());

        passengers.add(Passenger.newBuilder()
                .setName("PAvel NOvak")
                .build());

        passengers.add(Passenger.newBuilder()
                .setName("Ondra Veselý")
                .build());
        // Create two entries
        // Paris to HK
        bookings.add(Booking.newBuilder()
                .setId("1")
                .setPassenger(passengers.get(0))
                .setDepartureDate(airports.get(0).getDepartureTimes(0).getDate())
                .setDepartureAirport(airports.get(0).getName())
                .setArrivalDate(airports.get(1).getArrivalTimes(0).getDate())
                .setArrivalAirport(airports.get(1).getName())
                .build());

        //Chicago to HK
        bookings.add(Booking.newBuilder()
                .setId("2")
                .setPassenger(passengers.get(1))
                .setDepartureDate(airports.get(2).getDepartureTimes(0).getDate())
                .setDepartureAirport(airports.get(2).getName())
                .setArrivalDate(airports.get(1).getArrivalTimes(3).getDate())
                .setArrivalAirport(airports.get(1).getName())
                .build());
    }

    public List<Booking> getBookings(){
        return bookings;
    }

    // This method deletes a booking with the given id
    public Map<Integer, String> deleteBooking(String id){
        Map<Integer, String> result = new HashMap<>();

        // Check whether the booking exists
        Booking aux = bookings.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);

        if(aux == null) {
            result.put(0, "1");
            result.put(1, "Record not found!");
            return result;
        }

        bookings.removeIf(t -> t.getId().equals(id));

        result.put(0, "0");
        result.put(1, "Delete: Successfully deleted a booking!");
        return result;
    }

    // This function adds a booking with the given data
    public Map<Integer, String> addBooking(BookingsCreateRequest request){
        Map<Integer, String> result = new HashMap<>();
        Passenger p = createOrGetPassenger(request.getPassenger());

        // Check for duplicates
        Booking aux = bookings.stream()
                .filter(s -> s.getPassenger().getName().replaceAll("\\s", "").equalsIgnoreCase(request.getPassenger().replaceAll("\\s", "")))
                .findFirst()
                .orElse(null);

        if (aux != null)
        {
            result.put(0, "1");
            result.put(1, "Create: Record already exists!");
            return result;
        }

        // Check, whether the airports exist
        Airport depAir = airports.stream()
                .filter(s -> s.getName().replaceAll("\\s", "").equalsIgnoreCase(request.getDepartureAirport().replaceAll("\\s", "")))
                .findFirst()
                .orElse(null);

        Airport arrAir = airports.stream()
                .filter(s -> s.getName().replaceAll("\\s", "").equalsIgnoreCase(request.getArrivalAirport().replaceAll("\\s", "")))
                .findFirst()
                .orElse(null);

        if(depAir == null || arrAir == null){
            result.put(0, "2");
            result.put(1, "Create: Non existing airport(s)!");
            return result;
        }

        // Check whether the flights exist
        if(!checkDates(request.getDepartureAirport(),
                request.getArrivalAirport(),
                request.getDepartureDate(),
                request.getArrivalDate()))
        {
            result.put(0, "2");
            result.put(1, "Create: Non existing flights!");
            return result;
        }

        // Create a new id and the new booking
        int max = bookings.stream().map(t-> Integer.valueOf(t.getId())).max(Integer::compare).get();
        int id = max + 1;
        Booking b = Booking.newBuilder()
                .setId(Integer.toString(id))
                .setPassenger(p)
                .setArrivalAirport(arrAir.getName())
                .setDepartureAirport(depAir.getName())
                .setArrivalDate(request.getArrivalDate())
                .setDepartureDate(request.getDepartureDate())
                .build();

        bookings.add(b);

        result.put(0, "0");
        result.put(1, "Create: Successfully created a record!");
        return result;
    }

    // This function updates a booking wih the given data
    public Map<Integer, String> updateBooking(BookingsUpdateRequest request){
        Map<Integer, String> result = new HashMap<>();

        // Check, whether the record exists
        Booking b = bookings.stream()
                .filter(s -> s.getId().equals(request.getId()))
                .findFirst()
                .orElse(null);

        if(b == null){
            result.put(0, "1");
            result.put(1, "Update: Record not found!");
            return result;
        }

        int index = bookings.indexOf(b);
        Booking.Builder bb = b.toBuilder();
        // Because the client does not have to change the whole entry, all attributes in the
        // request are optional.
        if(request.hasPassenger()){
            Passenger p = createOrGetPassenger(request.getPassenger());
            bb.setPassenger(p);
        }
        // When the client wishes to change the departure airport or time, then he has to
        // also add the complementary attribute - airport or date.
        // Same goes with arrival airport and date.
        if(request.hasDepartureAirport() && request.hasDepartureDate()){
            Airport depAir = airports.stream()
                    .filter(s -> s.getName().replaceAll("\\s", "").equalsIgnoreCase(request.getDepartureAirport().replaceAll("\\s", "")))
                    .findFirst()
                    .orElse(null);

            if(depAir == null){
                result.put(0, "2");
                result.put(1, "Update: Non existing airport!");
                return result;
            }

            bb.setDepartureDate(request.getDepartureDate());

            bb.setDepartureAirport(depAir.getName());
        }
        if(request.hasArrivalAirport() && request.hasArrivalDate()){
            Airport arrAir = airports.stream()
                    .filter(s -> s.getName().replaceAll("\\s", "").equalsIgnoreCase(request.getArrivalAirport().replaceAll("\\s", "")))
                    .findFirst()
                    .orElse(null);

            if(arrAir == null){
                result.put(0, "2");
                result.put(1, "Update: Non existing airport");
                return result;
            }
            bb.setArrivalAirport(arrAir.getName());

            bb.setArrivalDate(request.getArrivalDate());
        }

        // Last step is o check, whether the flights the client provided exist.
        if(!checkDates(bb.getDepartureAirport(),
                bb.getArrivalAirport(),
                bb.getDepartureDate(),
                bb.getArrivalDate()))
        {
            result.put(0, "2");
            result.put(1, "Update: Non existing flights!");
            return result;
        }
        b = bb.build();
        bookings.set(index, b);

        result.put(0, "0");
        result.put(1, "Update: Successfully updated a record!");
        return result;
    }

    // This function creates a new passenger if it no exists or returns an existing one.
    private Passenger createOrGetPassenger(String name){
        Passenger p = passengers.stream()
                .filter(s -> s.getName().replaceAll("\\s", "").equalsIgnoreCase(name.replaceAll("\\s", "")))
                .findFirst()
                .orElse(null);

        if(p == null){
            p = Passenger.newBuilder().setName(name).build();
            passengers.add(p);
        }

        return p;
    }

    // Create a new date.
    private Dates createDate(String name, String date){
        return Dates.newBuilder().setName(name).setDate(date).build();
    }

    // This function checks the existence of flights.
    private Boolean checkDates(String depAirp, String arrAirp, String depDate, String arrDate){
        // Get the departure dates of the exact departure airport and search the date
        // and arrival airport inside the array.
        Dates aux =  airports.stream()
                .filter(a -> a.getName().equals(depAirp))
                .findFirst()
                .get()
                .getDepartureTimesList()
                .stream()
                .filter(t -> t.getName().equals(arrAirp)&&t.getDate().equals(depDate))
                .findFirst().orElse(null);

        // This means the date does not exist.
        if(aux == null){
            return false;
        }

        aux = airports.stream()
                .filter(a -> a.getName().equals(arrAirp))
                .findFirst()
                .get()
                .getArrivalTimesList()
                .stream()
                .filter(t -> t.getName().equals(depAirp)&&t.getDate().equals(arrDate))
                .findFirst().orElse(null);

        return aux != null;
    }
}
