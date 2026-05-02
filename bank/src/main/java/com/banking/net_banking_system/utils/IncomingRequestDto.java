package com.banking.net_banking_system.utils;

import java.util.UUID;

public record IncomingRequestDto<T>(
        String message,

        UUID correlationId,

        T data
) {
}
