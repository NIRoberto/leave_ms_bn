package com.robert.leave_ms_bn.exceptions;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class LeaveBalanceNotFoundException extends RuntimeException {

    public LeaveBalanceNotFoundException() {
        super("Leave balance not found.");
    }

    public LeaveBalanceNotFoundException(String message) {
        super(message);
    }

    public LeaveBalanceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LeaveBalanceNotFoundException(Throwable cause) {
        super(cause);
    }
}
