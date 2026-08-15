package com.ems.dto.request;

import com.ems.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateAttendanceRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Status is required")
    private AttendanceStatus status;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
}
