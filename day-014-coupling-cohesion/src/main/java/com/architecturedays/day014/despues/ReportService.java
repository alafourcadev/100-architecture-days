package com.architecturedays.day014.despues;

import org.springframework.stereotype.Service;

/**
 * Recibe los datos pre-procesados que necesita para el reporte.
 * No depende del shape del request HTTP.
 */
@Service
public class ReportService {

    public Report generateUserReport(UserReportData data) {
        throw new UnsupportedOperationException();
    }
}
