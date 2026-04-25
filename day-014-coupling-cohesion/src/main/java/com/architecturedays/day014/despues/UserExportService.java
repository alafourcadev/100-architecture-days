package com.architecturedays.day014.despues;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Cohesion alta: todo lo relacionado con exportar y reportar
 * datos de usuarios vive en una sola clase.
 */
@Service
public class UserExportService {

    public byte[] exportToCsv(List<UserReportData> users) {
        throw new UnsupportedOperationException();
    }

    public Report generateMonthlyReport() {
        throw new UnsupportedOperationException();
    }
}
