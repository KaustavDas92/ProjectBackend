package com.example.springbootlibrary.dao;

import com.example.springbootlibrary.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CheckoutRepository extends JpaRepository<Checkout,Long> {
    Checkout findByUserEmailAndBookId( String userEmail,Long bookId);
    List<Checkout> findBooksByUserEmail(String userEmail);
}
