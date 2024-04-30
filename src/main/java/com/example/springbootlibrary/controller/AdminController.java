package com.example.springbootlibrary.controller;

import com.example.springbootlibrary.requestModels.AddBookRequest;
import com.example.springbootlibrary.service.AdminService;
import com.example.springbootlibrary.utils.ExtractJWTToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("https://localhost:3000")
@RequestMapping("/api/admin")
public class AdminController {

    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/secure/add/book")
    public void postBook(@RequestHeader(value = "Authorization") String token, @RequestBody AddBookRequest addBookRequest) throws Exception{
        String admin= ExtractJWTToken.payloadExtract(token,"\"userType\"");

        if(admin == null  || !admin.equals("admin")){
            throw new Exception("This page is for admin only.");
        }

        adminService.postBook(addBookRequest);

    }
    @PutMapping("/secure/increase/book/quantity")
    public void increaseBook(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception{
        String admin= ExtractJWTToken.payloadExtract(token,"\"userType\"");

        if(admin == null  || !admin.equals("admin")){
            throw new Exception("This page is for admin only.");
        }

        adminService.increaseBook(bookId);

    }
    @PutMapping("/secure/decrease/book/quantity")
    public void decreaseBook(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception{
        String admin= ExtractJWTToken.payloadExtract(token,"\"userType\"");

        if(admin == null  || !admin.equals("admin")){
            throw new Exception("This page is for admin only.");
        }

        adminService.decreaseBook(bookId);

    }
    @DeleteMapping("/secure/delete/book")
    public void deleteBook(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception{
        String admin= ExtractJWTToken.payloadExtract(token,"\"userType\"");

        if(admin == null  || !admin.equals("admin")){
            throw new Exception("This page is for admin only.");
        }

        adminService.deleteBook(bookId);

    }
}
