package com.nutrinest.controller;

import com.nutrinest.entity.Address;
import com.nutrinest.service.AddressService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public String addressPage(
            Model model,
            Principal principal,
            @RequestParam(required = false, defaultValue = "address") String returnTo) {

        model.addAttribute(
                "addresses",
                addressService.getAllAddresses(principal.getName())
        );

        model.addAttribute("returnTo", returnTo);

        return "address";
    }

    @GetMapping("/add")
    public String addAddressPage(
            Model model,
            @RequestParam(required = false, defaultValue = "address") String returnTo) {

        model.addAttribute("address", new Address());
        model.addAttribute("returnTo", returnTo);

        return "add-address";
    }

    @PostMapping("/save")
    public String saveAddress(
            @ModelAttribute Address address,
            Principal principal,
            @RequestParam(required = false, defaultValue = "address") String returnTo) {

        addressService.saveAddress(address, principal.getName());

        if ("checkout".equalsIgnoreCase(returnTo)) {
            return "redirect:/checkout";
        }

        return "redirect:/address";
    }

    @GetMapping("/edit/{id}")
    public String editAddress(
            @PathVariable Long id,
            Model model,
            Principal principal,
            @RequestParam(required = false, defaultValue = "address") String returnTo) {

        model.addAttribute(
                "address",
                addressService.getAddress(id, principal.getName())
        );

        model.addAttribute("returnTo", returnTo);

        return "edit-address";
    }

    @PostMapping("/update/{id}")
    public String updateAddress(
            @PathVariable Long id,
            @ModelAttribute Address address,
            Principal principal,
            @RequestParam(required = false, defaultValue = "address") String returnTo) {

        addressService.updateAddress(
                id,
                address,
                principal.getName()
        );

        if ("checkout".equalsIgnoreCase(returnTo)) {
            return "redirect:/checkout";
        }

        return "redirect:/address";
    }

    @GetMapping("/delete/{id}")
    public String deleteAddress(
            @PathVariable Long id,
            Principal principal,
            @RequestParam(required = false, defaultValue = "address") String returnTo) {

        addressService.deleteAddress(id, principal.getName());

        if ("checkout".equalsIgnoreCase(returnTo)) {
            return "redirect:/checkout";
        }

        return "redirect:/address";
    }

    @GetMapping("/default/{id}")
    public String setDefaultAddress(
            @PathVariable Long id,
            Principal principal,
            @RequestParam(required = false, defaultValue = "address") String returnTo) {

        addressService.setDefaultAddress(id, principal.getName());

        if ("checkout".equalsIgnoreCase(returnTo)) {
            return "redirect:/checkout";
        }

        return "redirect:/address";
    }
}