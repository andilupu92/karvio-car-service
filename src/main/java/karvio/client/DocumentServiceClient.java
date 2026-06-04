package karvio.client;

import karvio.dto.client.ExpenseRequest;
import karvio.dto.client.ExpenseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "karvio-document-service")
public interface DocumentServiceClient {

    @PostMapping("/expenses/byCars")
    List<ExpenseResponse> getExpensesForCars(@RequestBody List<Long> carIds);

    @DeleteMapping("/documents/byCar/{carId}")
    void deleteAllDocumentsByCar(@PathVariable Long carId);

    @PostMapping("/expenses/monthly-average/{carId}")
    BigDecimal getMonthlyAverage(@PathVariable Long carId, @RequestBody ExpenseRequest expenseRequest);
}