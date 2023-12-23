package com.driver.services.impl;

import com.driver.Exception.NoCabAvailableException;
import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer deleted = customerRepository2.findById(customerId).get();
		customerRepository2.delete(deleted);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		//find all the list of drivers present in our database
		List<Driver> availableDriverList = driverRepository2.findByCabAvailableIsTrueOrderByDriverId();

		//2. get the driver with lowest driver id
			if(!availableDriverList.isEmpty()){
				Driver selectedDriver = availableDriverList.get(0);

				//create a new TicketBooking Instance
				TripBooking tripBooking = new TripBooking();

				tripBooking.setFromLocation(fromLocation);
				tripBooking.setToLocation(toLocation);
				tripBooking.setDistanceInKm(distanceInKm);
				Customer customer = customerRepository2.findById(customerId).get();
				tripBooking.setCustomer(customer);
				tripBooking.setDriver(selectedDriver);

				selectedDriver.getCab().setAvailable(false);

				tripBookingRepository2.save(tripBooking);
				driverRepository2.save(selectedDriver);

				return tripBooking;
			} else {
		// No available drivers
		throw new NoCabAvailableException("No cab available!");
	  }
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking bookedTrip = tripBookingRepository2.findById(tripId).get();
		bookedTrip.setStatus(TripStatus.CANCELED);
		bookedTrip.setBill(0);
		bookedTrip.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(bookedTrip);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.COMPLETED);

		Driver driver = tripBooking.getDriver();
		if(driver!=null){
			driver.getCab().setAvailable(true);
			driverRepository2.save(driver);
		}
		tripBookingRepository2.save(tripBooking);
	}
}
