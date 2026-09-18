package com.nutrinest.service;

import com.nutrinest.entity.Address;

import java.util.List;

public interface AddressService {

    // Get all addresses
    List<Address> getAllAddresses(String email);

    // Get one address
    Address getAddress(Long id, String email);

    // Save address
    void saveAddress(Address address, String email);

    // Update address
    void updateAddress(Long id, Address address, String email);

    // Delete address
    void deleteAddress(Long id, String email);

    // Set Default Address
    void setDefaultAddress(Long id, String email);
}