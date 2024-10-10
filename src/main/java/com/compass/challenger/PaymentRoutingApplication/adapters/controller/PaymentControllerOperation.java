package com.compass.challenger.PaymentRoutingApplication.adapters.controller;

import com.compass.challenger.PaymentRoutingApplication.application.dto.error.ErrorResponse;
import com.compass.challenger.PaymentRoutingApplication.application.dto.payment.PaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;

@Tag(name = "Payment API", description = "API for processing payments")
public interface PaymentControllerOperation {

    @Operation(summary = "Set a payment", description = "Receives a payment request and processes it by validating and sending it to the appropriate SQS queue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment processed successfully",
                    content = @Content(schema = @Schema(implementation = PaymentRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                     	"error": "VALIDATION_ERROR",
                                     	"message": "Validation failed for some fields.",
                                     	"details": {
                                     		"paymentItems": "Payment items cannot be empty"
                                     	}
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "404", description = "Seller or payment not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                     	"error": "SELLER_NOT_FOUND",
                                     	"message": "Seller Not Found"
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "GENERIC_ERROR",
                                      "message": "An unexpected error occurred while processing the payment."
                                    }
                                    """)
                    ))
    })
    ResponseEntity<PaymentRequest> setPayment(
            @RequestBody(description = "Payment request containing seller code and payment items",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentRequest.class)))
            PaymentRequest paymentRequest
    );
}
