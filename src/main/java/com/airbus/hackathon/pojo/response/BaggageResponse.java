package com.airbus.hackathon.pojo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaggageResponse implements Serializable {

    private static final long serialVersionUID = 5960272648644059474L;

    @JsonProperty("booking_id")
    private Integer bookingId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("weight")
    private Float weight;

    @JsonProperty("no_of_items")
    private Integer noOfItems;

    @JsonProperty("error")
    private String error;

    public BaggageResponse() {
    }

    public BaggageResponse(Integer bookingId, String status, Float weight, Integer noOfItems, String error) {
        this.bookingId = bookingId;
        this.status = status;
        this.weight = weight;
        this.noOfItems = noOfItems;
        this.error = error;
    }
    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Integer getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(Integer noOfItems) {
        this.noOfItems = noOfItems;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "BaggageResponse{" +
                "bookingId=" + bookingId +
                ", status='" + status + '\'' +
                ", weight=" + weight +
                ", noOfItems=" + noOfItems +
                ", error='" + error + '\'' +
                '}';
    }

}
