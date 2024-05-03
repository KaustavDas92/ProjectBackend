package com.example.springbootlibrary.service;

import com.example.springbootlibrary.dao.BookRepository;
import com.example.springbootlibrary.dao.CheckoutRepository;
import com.example.springbootlibrary.dao.HistoryRepository;
import com.example.springbootlibrary.dao.PaymentRepository;
import com.example.springbootlibrary.entity.Book;
import com.example.springbootlibrary.entity.Checkout;
import com.example.springbootlibrary.entity.History;
import com.example.springbootlibrary.entity.Payment;
import com.example.springbootlibrary.responseModels.ShelfCurrentLoansResponse;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class BookService {

    private BookRepository bookRepository;
    private CheckoutRepository checkoutRepository;
    private HistoryRepository historyRepository;

    private PaymentRepository paymentRepository;
    @Autowired
    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository,
                       HistoryRepository historyRepository, PaymentRepository paymentRepository) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.historyRepository=historyRepository;
        this.paymentRepository=paymentRepository;
    }

    public Book checkoutBook (String userEmail,Long bookId) throws Exception {
        Optional<Book> book=bookRepository.findById(bookId);
        System.out.println("book="+book.get().getId());
        Checkout validateCheckout=checkoutRepository.findByUserEmailAndBookId(userEmail,bookId);
        System.out.println("validate checkout="+validateCheckout);

        if(book.isEmpty() || validateCheckout != null || book.get().getCopies_available() <=0){
            throw new Exception("book not found or book already checked out");
        }

        //Start- check if book needs returning before checkout .

        List<Checkout> currentBooksCheckedOut= checkoutRepository.findBooksByUserEmail(userEmail);

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        boolean bookNeedsReturning=false;

        for(Checkout checkout:currentBooksCheckedOut){
            Date d1= sdf.parse(checkout.getReturnDate());
            Date d2= sdf.parse(LocalDate.now().toString());

            TimeUnit time=TimeUnit.DAYS;

            double differenceInTime=time.convert(d1.getTime()-d2.getTime(),TimeUnit.MILLISECONDS);

            if(differenceInTime<0){
                bookNeedsReturning= true;
                break;
            }
        }
        Payment payment=paymentRepository.findByUserEmail(userEmail);
        if((payment!= null && payment.getAmount()>0) || (payment!= null && bookNeedsReturning)){
            throw new Exception("Outstanding fees");
        }

        if(payment == null){
            Payment userPayment= new Payment();
            userPayment.setAmount(0.00);
            userPayment.setUserEmail(userEmail);
            paymentRepository.save(userPayment);
        }
        //End- check if book needs returning before checkout .


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

    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws Exception {
        List<ShelfCurrentLoansResponse> shelfCurrentLoansResponses=new ArrayList<>();

        List<Checkout> checkoutList=checkoutRepository.findBooksByUserEmail(userEmail);
        List<Long> bookIds=new ArrayList<>();

        for(Checkout i:checkoutList){
            bookIds.add(i.getBookId());
        }

        List<Book> books = bookRepository.findBooksByBookIds(bookIds);

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");

        for(Book book:books){
            Optional<Checkout> checkout=checkoutList.stream()
                    .filter(x -> x.getBookId() == book.getId())
                    .findFirst();

            if(checkout.isPresent()){
                Date returnDate= sdf.parse(checkout.get().getReturnDate());
                Date now=sdf.parse(LocalDate.now().toString());
                TimeUnit time = TimeUnit.DAYS;
                long difference_in_time=time.convert(returnDate.getTime() - now.getTime(),TimeUnit.MILLISECONDS);

                ShelfCurrentLoansResponse sclr= new ShelfCurrentLoansResponse(book,(int)difference_in_time);

                shelfCurrentLoansResponses.add(sclr);
            }
        }

        return shelfCurrentLoansResponses;
    }

    public void returnBook(String userEmail,long bookId) throws Exception {
        Optional<Book> book=bookRepository.findById(bookId);
        Checkout checkout=checkoutRepository.findByUserEmailAndBookId(userEmail,bookId);
        if(book.isEmpty() || checkout == null){
            throw new Exception("Book does not exist or not checked out by the user");
        }
        book.get().setCopies_available(book.get().getCopies_available()+1);
        bookRepository.save(book.get());

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");

        Date d1= sdf.parse(checkout.getReturnDate());
        Date d2= sdf.parse(LocalDate.now().toString());

        TimeUnit time=TimeUnit.DAYS;

        double differenceinDate= time.convert(d1.getTime()-d2.getTime(),TimeUnit.MILLISECONDS);

        if(differenceinDate<0){
            Payment payment=paymentRepository.findByUserEmail(userEmail);
            payment.setAmount(payment.getAmount() + Math.abs(differenceinDate));
        }


        checkoutRepository.deleteById(checkout.getId());

        History history=new History(
                userEmail,
                checkout.getCheckoutDate(),
                LocalDate.now().toString(),
                book.get().getTitle(),
                book.get().getAuthor(),
                book.get().getDescription(),
                book.get().getImg()
        );

        historyRepository.save(history);
    }

    public void renewLoan(String userEmail,Long bookId) throws Exception{
        Checkout validateCheckout=checkoutRepository.findByUserEmailAndBookId(userEmail,bookId);

        if( validateCheckout == null){
            throw new Exception("Book does not exist or not checked out by the user");
        }

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date d1=sdf.parse(validateCheckout.getReturnDate());
        Date d2=sdf.parse(LocalDate.now().toString());

        if(d1.compareTo(d2)>=0 ){
//                validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString());
                validateCheckout.setReturnDate(LocalDate.parse(validateCheckout.getReturnDate()).plusDays(7).toString());
                checkoutRepository.save(validateCheckout);
        }
    }
}
