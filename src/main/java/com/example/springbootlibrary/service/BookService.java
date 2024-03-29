package com.example.springbootlibrary.service;

import com.example.springbootlibrary.dao.BookRepository;
import com.example.springbootlibrary.dao.CheckoutRepository;
import com.example.springbootlibrary.entity.Book;
import com.example.springbootlibrary.entity.Checkout;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private BookRepository bookRepository;
    private CheckoutRepository checkoutRepository;

    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
    }

    public Book checkoutBook (String userEmail,Long bookId) throws Exception {
        Optional<Book> book=bookRepository.findById(bookId);
        Checkout validateCheckout=checkoutRepository.findByUserEmailAndBookId(userEmail,bookId);

        if(!book.isPresent() || validateCheckout != null || book.get().getCopies_available() <=0){
            throw new Exception("book not found or book already checked out");
        }

        book.get().setCopies_available(book.get().getCopies_available()-1);
        bookRepository.save(book.get());

        Checkout checkout = new Checkout(
                userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                book.get().getId()
        );

        checkoutRepository.save(checkout);
        return book.get();
    }

    public Boolean checkoutBookByUser(String userEmail,Long bookId) {
        Checkout validateCheckout=checkoutRepository.findByUserEmailAndBookId(userEmail,bookId);
        return validateCheckout != null;
    }
    public int currentLoansCount(String userEmail) {
        return checkoutRepository.findBooksByUserEmail(userEmail).size();

    }

    public Book getBookById(long id){
        Optional<Book> book=bookRepository.findById(id);
        if(book.isPresent())
            return  book.get();
        else {
            throw new NoSuchElementException();

        }
    }
}
