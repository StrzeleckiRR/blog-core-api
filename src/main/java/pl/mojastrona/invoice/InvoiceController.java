package pl.mojastrona.invoice;


import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public void create(@Valid @RequestBody CreateInvoiceRequest invoiceRequest) {
        invoiceService.create(invoiceRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadInvoiceResponse> read(@PathVariable("id") Long id) {
       return ResponseEntity.ok(invoiceService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long id, @Valid @RequestBody UpdateInvoiceRequest updateInvoiceRequest) {

        invoiceService.update(id, updateInvoiceRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {

        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archive(@PathVariable("id") Long id) {

        invoiceService.archive(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<FindInvoiceResponse>> find(@RequestParam(value = "s", defaultValue = "") String sellerContaining,
                                                          @RequestParam(value = "b", defaultValue = "") String buyerContaining,
                                                          @RequestParam int page,
                                                          @RequestParam int size){
        return ResponseEntity.ok(invoiceService.find(sellerContaining, buyerContaining, page, size));
    }

    @PostMapping("/find")
    public ResponseEntity<Page<FindInvoiceResponse>> find(@RequestBody FindInvoiceRequest findInvoiceRequest, Pageable pageable) {

        Page<FindInvoiceResponse> body = invoiceService.find(findInvoiceRequest,pageable);
        return ResponseEntity.ok(body);
    }
}
