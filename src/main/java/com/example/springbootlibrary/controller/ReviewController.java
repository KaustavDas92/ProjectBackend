package com.example.springbootlibrary.controller;

import com.example.springbootlibrary.requestModels.ReviewRequest;
import com.example.springbootlibrary.service.ReviewService;
import com.example.springbootlibrary.utils.ExtractJWTToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("https://localhost:3000")
@RequestMapping("/api/reviews")
@RestController
public class ReviewController {

    public ReviewService reviewService;


    public ReviewController(ReviewService reviewService) { this.reviewService = reviewService; }

    @PostMapping("/secure")
    public void postReview(@RequestHeader( value="Authorization") String token, @RequestBody ReviewRequest reviewRequest) throws Exception {
        String userEmail= ExtractJWTToken.payloadExtract(token,"\"sub\"");
        if(userEmail == null){
            throw new Exception("user email is missing ");
        }
        reviewService.postReview(userEmail,reviewRequest);
    }

    @GetMapping("/secure/user/book")
    public Boolean reviewBookUser(@RequestHeader(value = "Authorization") String token,@RequestParam long bookId) throws Exception{
        String userEmail= ExtractJWTToken.payloadExtract(token,"\"sub\"");
        if(userEmail == null){
            throw new Exception("user email is missing ");
        }

       return  reviewService.userReviewListed(userEmail,bookId);

    }
}
