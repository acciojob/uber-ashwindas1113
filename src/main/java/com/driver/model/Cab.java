package com.driver.model;

import com.zaxxer.hikari.util.DriverDataSource;

import javax.persistence.*;

@Entity
@Table(name = "cab")

public class Cab{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cabId;

    private Integer perKmRate;

    private boolean available;

    public Cab() {
    }

    public Integer getCabId() {
        return cabId;
    }

    public void setCabId(Integer cabId) {
        this.cabId = cabId;
    }

    public Integer getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(Integer perKmRate) {
        this.perKmRate = perKmRate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        available = available;
    }

    //Mapping
     @OneToOne
     @JoinColumn
     private Driver driver;
}