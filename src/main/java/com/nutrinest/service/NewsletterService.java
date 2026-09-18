package com.nutrinest.service;

public interface NewsletterService {

    boolean subscribe(String email);

    boolean isSubscribed(String email);
}