package com.example.springbootlibrary.controller;

import com.example.springbootlibrary.entity.Book;
import com.example.springbootlibrary.responseModels.ShelfCurrentLoansResponse;
import com.example.springbootlibrary.service.BookService;
import com.example.springbootlibrary.utils.ExtractJWTToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("https://localhost:3000")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PutMapping("/secure/checkout")
    public Book checkoutBook(@RequestHeader(value = "Authorization") String token,
                             @RequestParam Long bookId) throws Exception {
        String userEmail= ExtractJWTToken.payloadExtract(token,"\"sub\"");

        return bookService.checkoutBook(userEmail,bookId);
    }

    @GetMapping("/secure/isCheckedOut/byUser")
    public Boolean checkoutBookByUser (@RequestHeader(value = "Authorization") String token,
                                       @RequestParam Long bookId){
        String userEmail= ExtractJWTToken.payloadExtract(token,"\"sub\"");

        return bookService.checkoutBookByUser(userEmail,bookId);
    }

    @GetMapping("/secure/currentloans")
    public List<ShelfCurrentLoansResponse> currentLoans (@RequestHeader(value = "Authorization") String token) throws Exception
    {
        String userEmail= ExtractJWTToken.payloadExtract(token,"\"sub\"");
        return bookService.currentLoans(userEmail);
    }
    @GetMapping("/secure/currentloans/count")
    public int currentLoansCount (@RequestHeader(value = "Authorization") String token){
        String userEmail= ExtractJWTToken.payloadExtract(token,"\"sub\"");
        return bookService.currentLoansCount(userEmail);
    }
    @GetMapping("/secure/{id}")
    public Book getBookById (@RequestHeader(value = "Authorization") String token,@PathVariable long id){
        String userEmail= ExtractJWTToken.payloadExtract(token,"\"sub\"");
        return bookService.getBookById(id);
    }

    @PutMapping("/secure/return")
    public void returnBook(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception {
        String userEmail=ExtractJWTToken.payloadExtract(token,"\"sub\"");
        bookService.returnBook(userEmail,bookId);
    }
    @PutMapping("/secure/renew/loan")
    public void renewLoan(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception {
        String userEmail=ExtractJWTToken.payloadExtract(token,"\"sub\"");
        bookService.renewLoan(userEmail,bookId);
    }
}
