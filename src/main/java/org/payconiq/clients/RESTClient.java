package org.payconiq.clients;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.payconiq.models.Booking;
import org.payconiq.models.BookingResource;
import org.payconiq.models.Credentials;
import org.payconiq.models.Secret;

public class RESTClient {

    private static final Gson gson = new Gson();

    private final String URL;

    private final Credentials credentials;
    private String token;


    public RESTClient(String URL, Credentials credentials) {
        this.URL = URL;
        this.credentials = credentials;
        Unirest.setTimeouts(0, 0);
        Unirest.setObjectMapper(new GsonObjectMapper());
    }

    public void authenticate() throws UnirestException {
        HttpResponse<Secret> response = Unirest.post("https://restful-booker.herokuapp.com/auth")
                .header("Content-Type", "application/json")
                .body(credentials).asObject(Secret.class);
        this.token = response.getBody().getToken();
    }

    public BookingResource createBooking(Booking booking) throws UnirestException {
        HttpResponse<BookingResource> response = Unirest.post(this.URL + "/booking")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + this.token)
                .body(gson.toJson(booking))
                .asObject(BookingResource.class);
        if (response.getStatus() == 200) {
            return response.getBody();
        } else {
            throw new UnirestException("Status Code: " + response.getStatus());
        }
    }

    public Booking getBooking(Integer bookingId) throws UnirestException {
        HttpResponse<Booking> response = Unirest.get(this.URL + "/booking/{BookingID}")
                .routeParam("BookingID", String.valueOf(bookingId))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + this.token)
                .asObject(Booking.class);
        if (response.getStatus() == 200) {
            return response.getBody();
        } else {
            throw new UnirestException("Status Code: " + response.getStatus());
        }
    }

    public BookingResource[] getBookingIds() throws UnirestException {
        HttpResponse<BookingResource[]> response = Unirest.get(this.URL + "/booking")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + this.token)
                .asObject(BookingResource[].class);
        if (response.getStatus() == 200) {
            return response.getBody();
        } else {
            throw new UnirestException("Status Code: " + response.getStatus());
        }
    }

    public BookingResource[] getBookingIds(Booking booking) throws UnirestException {
        GetRequest request = Unirest.get(this.URL + "/booking");
        if (booking.getFirstname() != null) request.queryString("firstname", booking.getFirstname());
        if (booking.getLastname() != null) request.queryString("lastname", booking.getLastname());
        if (booking.getBookingdates() != null) {
            if (booking.getBookingdates().getCheckin() != null)
                request.queryString("checkin", booking.getBookingdates().getCheckin());
            if (booking.getBookingdates().getCheckout() != null)
                request.queryString("checkout", booking.getBookingdates().getCheckin());
        }
        HttpResponse<BookingResource[]> response = request
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + this.token)
                .asObject(BookingResource[].class);
        if (response.getStatus() == 200) {
            return response.getBody();
        } else {
            throw new UnirestException("Status Code: " + response.getStatus());
        }
    }

    public Booking updateBooking(Integer bookingId, Booking booking) throws UnirestException {
        HttpResponse<Booking> response = Unirest.patch(this.URL + "/booking/{BookingID}")
                .routeParam("BookingID", String.valueOf(bookingId))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + this.token)
                .body(booking)
                .asObject(Booking.class);
        if (response.getStatus() == 200) {
            return response.getBody();
        } else {
            throw new UnirestException("Status Code: " + response.getStatus());
        }
    }

    public String deleteBooking(Integer bookingId) throws UnirestException {
        HttpResponse<String> response = Unirest.delete(this.URL + "/booking/{BookingID}")
                .routeParam("BookingID", String.valueOf(bookingId))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + this.token)
                .asString();
        if (response.getStatus() == 201) {
            return response.getBody();
        } else {
            throw new UnirestException("Status Code: " + response.getStatus());
        }
    }

    class GsonObjectMapper implements ObjectMapper {
        private final Gson gson = new Gson();

        @Override
        public <T> T readValue(String s, Class<T> aClass) {
            try {
                return gson.fromJson(s, aClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String writeValue(Object o) {
            try {
                return gson.toJson(o);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
