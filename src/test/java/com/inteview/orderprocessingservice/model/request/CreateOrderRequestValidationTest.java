package com.inteview.orderprocessingservice.model.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateOrderRequestValidationTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setup(){
       factory = Validation.buildDefaultValidatorFactory();
       validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown(){
        factory.close();
    }

    private Set<String> messages(CreateOrderRequest r){
        return validator.validate(r).stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
    }

    @Test
    void validRequest_hasNoViolations(){
        assertTrue(messages(new CreateOrderRequest("1","Laptop",1, new BigDecimal("1200"))).isEmpty());
    }

    @Test
    void missingCustomerId_isRejected(){
        assertTrue(messages(new CreateOrderRequest(null,"Laptop",1,BigDecimal.TEN)).contains("CustomerId is required"));
        assertTrue(messages(new CreateOrderRequest(" ","Laptop",1,BigDecimal.TEN)).contains("CustomerId is required"));
    }

    @Test
    void zeroOrNegativeQuantity_isRejected(){
        assertTrue(messages(new CreateOrderRequest("1","Laptop",0,BigDecimal.TEN)).contains("Quantity must be greater than 0"));
        assertTrue(messages(new CreateOrderRequest("1","Laptop",-3,BigDecimal.TEN)).contains("Quantity must be greater than 0"));
    }

    @Test
    void zeroOrNegativePrice_isRejected(){
        assertTrue(messages(new CreateOrderRequest("1","Laptop",1,BigDecimal.ZERO)).contains("Price must be greater than 0"));
        assertTrue(messages(new CreateOrderRequest("1","Laptop",1,new BigDecimal("-5"))).contains("Price must be greater than 0"));
    }

    @Test
    void missingQuantityAndPrice_areRejected(){
        Set<String> m = messages(new CreateOrderRequest("1", "Laptop", null, null));
        assertTrue(m.contains("Quantity is required"));
        assertTrue(m.contains("Price is required"));
    }
}
