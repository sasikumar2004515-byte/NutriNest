package com.nutrinest.repository;

import com.nutrinest.entity.Address;
import com.nutrinest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    // Get all addresses of a user
    List<Address> findByUser(User user);

    // Count addresses (Maximum 4)
    long countByUser(User user);

    // Default Address
    Optional<Address> findByUserAndDefaultAddressTrue(User user);

    // Get Address by User & ID
    Optional<Address> findByIdAndUser(Long id, User user);
}