package com.example.springbootlibrary.controller;

import com.example.springbootlibrary.entity.Book;
import com.example.springbootlibrary.service.BookService;
import com.example.springbootlibrary.utils.ExtractJWTToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("Http://localhost:3000")
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
}
