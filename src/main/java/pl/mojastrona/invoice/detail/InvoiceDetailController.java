package pl.mojastrona.invoice.detail;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoices/details")
public class InvoiceDetailController {

    private final InvoiceDetailService detailService;

    @PostMapping
    public void create(@Valid @RequestBody CreateInvoiceDetailRequest invoiceDetailRequest) {

        detailService.create(invoiceDetailRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadInvoiceDetailResponse> read(@PathVariable("id") Long id) {
        ReadInvoiceDetailResponse details = detailService.findById(id);
        return ResponseEntity.ok(details);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long id, @Valid @RequestBody UpdateInvoiceDetailRequest updateInvoiceRequest) {

        detailService.update(id, updateInvoiceRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReadInvoiceDetailResponse>> find(@RequestParam(value = "inid") Long invoiceId,
                                                          Pageable pageable){
        return ResponseEntity.ok(detailService.find(invoiceId, pageable));
    }
}
