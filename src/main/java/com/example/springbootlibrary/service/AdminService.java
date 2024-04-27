package com.example.springbootlibrary.service;

import com.example.springbootlibrary.dao.BookRepository;
import com.example.springbootlibrary.dao.CheckoutRepository;
import com.example.springbootlibrary.dao.ReviewRepository;
import com.example.springbootlibrary.entity.Book;
import com.example.springbootlibrary.requestModels.AddBookRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AdminService {
    private BookRepository bookRepository;
    private ReviewRepository reviewRepository;
    private CheckoutRepository checkoutRepository;


    @Autowired
    public AdminService(BookRepository bookRepository, ReviewRepository reviewRepository, CheckoutRepository checkoutRepository) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
        this.checkoutRepository = checkoutRepository;
    }



    public void postBook(AddBookRequest addBookRequest){
        Book book= new Book();

        book.setTitle(addBookRequest.getTitle());
        book.setAuthor(addBookRequest.getAuthor());
        book.setDescription(addBookRequest.getDescription());
        book.setCategory(addBookRequest.getCategory());
        book.setImg(addBookRequest.getImg());
        book.setCopies(addBookRequest.getCopies());
        book.setCopies_available(addBookRequest.getCopies());

        bookRepository.save(book);
    }

    public void increaseBook(Long bookId) throws Exception {
        Optional<Book> book=bookRepository.findById(bookId);

        if(book.isEmpty() ){
            throw new Exception("Book Not Found!");
        }

        book.get().setCopies(book.get().getCopies() + 1);
        book.get().setCopies_available(book.get().getCopies_available() + 1);
        bookRepository.save(book.get());
    }
    public void decreaseBook(Long bookId) throws Exception {
        Optional<Book> book=bookRepository.findById(bookId);

        if(book.isEmpty() || book.get().getCopies()<= 0 || book.get().getCopies_available() <= 0){
            throw new Exception("Book Not Found!");
        }

        book.get().setCopies(book.get().getCopies() - 1);
        book.get().setCopies_available(book.get().getCopies_available() - 1);
        bookRepository.save(book.get());
    }

    public void deleteBook(Long bookId) throws Exception{
        Optional<Book> book=bookRepository.findById(bookId);
        if(book.isEmpty()){
            throw new Exception("Book Not Found!");
        }

        reviewRepository.deleteAllByBookId(bookId);
        checkoutRepository.deleteAllByBookId(bookId);
        bookRepository.deleteById(bookId);

    }
}
