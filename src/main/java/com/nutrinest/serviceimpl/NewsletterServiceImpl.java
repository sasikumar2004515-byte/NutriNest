package com.nutrinest.serviceimpl;

import com.nutrinest.entity.NewsletterSubscription;
import com.nutrinest.repository.NewsletterSubscriptionRepository;
import com.nutrinest.service.NewsletterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NewsletterServiceImpl implements NewsletterService {

    private final NewsletterSubscriptionRepository newsletterRepository;

    public NewsletterServiceImpl(
            NewsletterSubscriptionRepository newsletterRepository) {

        this.newsletterRepository = newsletterRepository;
    }

    @Override
    public boolean subscribe(String email) {

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        email = email.trim().toLowerCase();

        Optional<NewsletterSubscription> existing =
                newsletterRepository.findByEmail(email);

        if (existing.isPresent()) {

            NewsletterSubscription subscription = existing.get();

            if (!subscription.isSubscribed()) {
                subscription.setSubscribed(true);
                subscription.setSubscribedAt(LocalDateTime.now());
                newsletterRepository.save(subscription);
            }

            return true;
        }

        NewsletterSubscription subscription =
                new NewsletterSubscription();

        subscription.setEmail(email);
        subscription.setSubscribed(true);
        subscription.setSubscribedAt(LocalDateTime.now());

        newsletterRepository.save(subscription);

        return true;
    }

    @Override
    public boolean isSubscribed(String email) {

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return newsletterRepository
                .existsByEmailAndSubscribedTrue(
                        email.trim().toLowerCase()
                );
    }
}