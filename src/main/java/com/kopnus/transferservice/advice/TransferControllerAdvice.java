package com.kopnus.transferservice.advice;

import com.kopnus.transferservice.exception.CheckBalanceException;
import com.kopnus.transferservice.exception.InquiryException;
import com.kopnus.transferservice.exception.TransferException;
import com.kopnus.transferservice.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class TransferControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(new ApiResponse<>("0019", "An unexpected error occurred. Please try again later."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CheckBalanceException.class)
    public ResponseEntity<?> checkBalanceException(CheckBalanceException e) {
        return new ResponseEntity<>(new ApiResponse<>("0001", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InquiryException.class)
    public ResponseEntity<?> inquiryException(InquiryException e) {
        return new ResponseEntity<>(new ApiResponse<>("0002", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransferException.class)
    public ResponseEntity<?> transferException(TransferException e) {
        return new ResponseEntity<>(new ApiResponse<>("0003", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}