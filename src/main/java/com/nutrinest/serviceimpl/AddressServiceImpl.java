package com.nutrinest.serviceimpl;

import com.nutrinest.entity.Address;
import com.nutrinest.entity.User;
import com.nutrinest.repository.AddressRepository;
import com.nutrinest.repository.UserRepository;
import com.nutrinest.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository,
                              UserRepository userRepository) {

        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Address> getAllAddresses(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return addressRepository.findByUser(user);
    }

    @Override
    public Address getAddress(Long id, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address Not Found"));
    }

    @Override
    public void saveAddress(Address address, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        long totalAddress = addressRepository.countByUser(user);

        if (totalAddress >= 4) {
            throw new RuntimeException("Maximum 4 addresses allowed.");
        }

        address.setUser(user);

        if (totalAddress == 0) {
            address.setDefaultAddress(true);
        }

        addressRepository.save(address);
    }

    @Override
    public void updateAddress(Long id,
                              Address formAddress,
                              String email) {

        Address address = getAddress(id, email);

        address.setFullName(formAddress.getFullName());
        address.setPhone(formAddress.getPhone());
        address.setAddressLine1(formAddress.getAddressLine1());
        address.setAddressLine2(formAddress.getAddressLine2());
        address.setLandmark(formAddress.getLandmark());
        address.setCity(formAddress.getCity());
        address.setState(formAddress.getState());
        address.setPincode(formAddress.getPincode());
        address.setAddressType(formAddress.getAddressType());

        addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Long id, String email) {

        Address address = getAddress(id, email);

        boolean wasDefault = address.isDefaultAddress();

        User user = address.getUser();

        addressRepository.delete(address);

        if (wasDefault) {

            List<Address> addresses = addressRepository.findByUser(user);

            if (!addresses.isEmpty()) {

                Address first = addresses.get(0);

                first.setDefaultAddress(true);

                addressRepository.save(first);
            }
        }
    }

    @Override
    public void setDefaultAddress(Long id,
                                  String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        List<Address> addresses = addressRepository.findByUser(user);

        for (Address address : addresses) {

            address.setDefaultAddress(false);

            addressRepository.save(address);
        }

        Address selected = getAddress(id, email);

        selected.setDefaultAddress(true);

        addressRepository.save(selected);
    }
}