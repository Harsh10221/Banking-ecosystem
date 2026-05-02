package com.centeral_hub.centeral_hub.dtos;

import java.util.UUID;

public record WithdrawResponseDtoConsumer<T>(

        String message,
        UUID correlationId,
        T data

) {
}


