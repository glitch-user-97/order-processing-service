package com.inteview.orderprocessingservice.model.request;

import com.inteview.orderprocessingservice.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest (

        @NotNull(message = "Status is required")
        OrderStatus status
) {
}
