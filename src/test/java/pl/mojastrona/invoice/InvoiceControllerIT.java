package pl.mojastrona.invoice;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.mojastrona.invoice.detail.InvoiceDetail;
import pl.mojastrona.invoice.helper.DetailsCreator;
import pl.mojastrona.invoice.helper.InvoiceCreator;
import pl.mojastrona.post.*;
import pl.mojastrona.util.BaseIT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

class InvoiceControllerIT extends BaseIT {

    private static final String API_INVOICES_URL = "/api/invoices";

    @Autowired
    private InvoiceCreator invoiceCreator;

    @Autowired
    private DetailsCreator detailsCreator;

    @Test
    void givenWrongRequest_whenCreate_thenBadRequest() throws Exception {

        createUserAndAuthenticate();

        CreatePostRequest request = new CreatePostRequest(null, null, null);

        ResultActions resultActions = performPost(API_INVOICES_URL, request);

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buyer").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seller").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.paymentDate").value("must not be null"));

    }

    @Test
    void givenNotAuthenticatedUser_whenCreate_then401() throws Exception {
        //given

        String expectedBuyer = "Marcin Strzelecki";
        String expectedSeller = "Seller1";
        LocalDate expectedPaymentDate = LocalDate.now();
        CreateInvoiceRequest request = new CreateInvoiceRequest(
                expectedBuyer,
                expectedSeller,
                expectedPaymentDate
        );

        //when
        ResultActions resultActions = performPost(API_INVOICES_URL, request);


        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void givenCorrectRequest_whenCreate_thenCreateInvoice() throws Exception {
        //given
        createUserAndAuthenticate();

        String expectedBuyer = "Marcin Strzelecki";
        String expectedSeller = "Seller1";
        LocalDate expectedPaymentDate = LocalDate.now();
        CreateInvoiceRequest request = new CreateInvoiceRequest(
                expectedBuyer,
                expectedSeller,
                expectedPaymentDate
        );

        //when
        ResultActions resultActions = performPost(API_INVOICES_URL, request);


        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());

        List<Invoice> invoiceList = entityManager.createQuery("select i from Invoice i left join fetch i.details").getResultList();
        assertThat(invoiceList).hasSize(1);
        Invoice invoice = invoiceList.get(0);

        assertThat(invoice).extracting(
                Invoice::getId,
                Invoice::getCreatedDate,
                Invoice::getLastModifiedDateTime
        ).isNotNull();

        assertThat(invoice).extracting(
                Invoice::getVersion,
                Invoice::getBuyer,
                Invoice::getSeller,
                Invoice::getPaymentDate,
                Invoice::getStatus
        ).containsExactly(
                0,
                expectedBuyer,
                expectedSeller,
                expectedPaymentDate,
                InvoiceStatus.ACTIVE
        );

        assertThat(invoice.getDetails()).hasSize(2);
        assertThat(invoice.getDetails())
                .extracting(InvoiceDetail::getProductName).containsExactlyInAnyOrder("product1", "product2");

    }

    @Test
    void givenNotAuthenticatedUser_whenUpdate_then401() throws Exception {
        //given
        UpdateInvoiceRequest request = new UpdateInvoiceRequest(null, null, null, null);

        Long id = 100L;

        ResultActions resultActions = performPut(API_INVOICES_URL + "/{id}", id, request);

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }

    @Test
    void givenWrongRequest_whenUpdate_thenBadRequest() throws Exception {
        //given
        createUserAndAuthenticate();

        UpdateInvoiceRequest request = new UpdateInvoiceRequest(null, null, null, null);

        Long id = 100L;

        ResultActions resultActions = performPut(API_INVOICES_URL + "/{id}", id, request);

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest()) // Can you use MockMvcResultMatchers method static
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", Matchers.hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buyer").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seller").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.paymentDate").value("must not be null"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").value("must not be null"));

    }

    @Test
    void givenNotExistingInvoice_whenUpdate_thenNotFound() throws Exception {
        //given
        createUserAndAuthenticate();

        String expectedBuyer = "Marcin Strzelecki";
        String expectedSeller = "Seller1";
        LocalDate expectedPaymentDate = LocalDate.now();
        int expectedVersion = 0;
        UpdateInvoiceRequest request = new UpdateInvoiceRequest(expectedBuyer, expectedSeller, expectedPaymentDate, expectedVersion);

        Long id = 100L;


        ResultActions resultActions = performPut(API_INVOICES_URL + "/{id}", id, request);

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(is(emptyString())));

    }

    @Test
    void givenCorrectRequest_whenUpdate_thenUpdateInvoice() throws Exception {
        //given
        createUserAndAuthenticate();

        Invoice invoice = invoiceCreator.createInvoiceWithDetails();//


        String expectedBuyer = "Marcin Strzelecki";
        String expectedNewSeller = "New Seller";
        LocalDate expectedPaymentDate = LocalDate.now();
        int expectedVersion = 0;

        UpdateInvoiceRequest request = new UpdateInvoiceRequest(
                expectedBuyer,
                expectedNewSeller,
                expectedPaymentDate,
                expectedVersion);

        Long id = invoice.getId();


        ResultActions resultActions = performPut(API_INVOICES_URL + "/{id}", id, request);

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(is(emptyString())));

        Invoice updatedInvoice = entityManager.createQuery("select i from Invoice i left join fetch i.details where i.id=:id", Invoice.class)
                .setParameter("id", id)
                .getSingleResult(); // pojedynczy wynik

        assertThat(updatedInvoice.getLastModifiedDateTime()).isAfter(invoice.getCreatedDate());
        assertThat(updatedInvoice.getCreatedDate()).isEqualToIgnoringNanos(invoice.getCreatedDate());

        assertThat(updatedInvoice).extracting(
                Invoice::getVersion,
                Invoice::getPaymentDate,
                Invoice::getBuyer,
                Invoice::getSeller,
                Invoice::getStatus
        ).containsExactly(
                invoice.getVersion() + 1,
                invoice.getPaymentDate(),
                expectedBuyer,
                expectedNewSeller,
                invoice.getStatus()
        );

        assertThat(updatedInvoice.getDetails()).hasSize(1);
        InvoiceDetail invoiceDetail = updatedInvoice.getDetails().iterator().next();
        assertThat(invoiceDetail.getLastModifiedDateTime()).isEqualToIgnoringNanos(invoiceDetail.getCreatedDate());
        assertThat(invoiceDetail.getVersion()).isEqualTo(0);

    }

    @Test
    void givenWrongVersion_whenUpdate_thenConflict() throws Exception {
        //given
        createUserAndAuthenticate();

        Invoice invoice = invoiceCreator.createInvoice();

        int wrongVersion = invoice.getVersion() + 1;

        UpdateInvoiceRequest request = new UpdateInvoiceRequest("Marcin Strzelecki", "New Seller", LocalDate.now(), wrongVersion);

        Long id = invoice.getId();


        ResultActions resultActions = performPut(API_INVOICES_URL + "/{id}", id, request);

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string(is(emptyString())));

        Invoice shouldntBeUpdatedInvoice = entityManager.find(Invoice.class, id);

        assertThat(shouldntBeUpdatedInvoice.getLastModifiedDateTime()).isEqualToIgnoringNanos(invoice.getCreatedDate());
        assertThat(shouldntBeUpdatedInvoice.getCreatedDate()).isEqualToIgnoringNanos(invoice.getCreatedDate());

        assertThat(shouldntBeUpdatedInvoice).extracting(
                Invoice::getVersion,
                Invoice::getPaymentDate,
                Invoice::getBuyer,
                Invoice::getSeller,
                Invoice::getStatus
        ).containsExactly(
                invoice.getVersion(),
                invoice.getPaymentDate(),
                invoice.getBuyer(),
                invoice.getSeller(),
                invoice.getStatus()
        );

    }
    @Test
    void givenNotAuthenticatedUser_whenRead_then401() throws Exception {
        //given
        Long id = 100L;

        ResultActions resultActions = performGet(API_INVOICES_URL + "/{id}", id);

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }

    @Test
    void givenNotExistingInvoice_whenRead_thenNotFound() throws Exception {
        //given
        createUserAndAuthenticate();
        Long id = 100L;

        ResultActions resultActions = performGet(API_INVOICES_URL + "/{id}", id);

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(is(emptyString())));

    }

    @Test
    void givenExistingInvoice_whenRead_thenReturnResponse() throws Exception {
        //given
        createUserAndAuthenticate();

        Invoice invoice = invoiceCreator.createInvoice();
        Long invoiceId = invoice.getId();

        InvoiceDetail detail1 = detailsCreator.createDetails(invoice, 1);
        InvoiceDetail detail2 = detailsCreator.createDetails(invoice, 2);
        InvoiceDetail detail3 = detailsCreator.createDetails(invoice, 3);


        List<InvoiceDetail> detailsList = Stream.of(detail1, detail2, detail3)
                .sorted(Comparator.comparing(InvoiceDetail::getCreatedDate).reversed())
                .toList();


        ResultActions resultActions = performGet(API_INVOICES_URL + "/{id}", invoiceId);

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(invoice.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").value(invoice.getVersion()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.paymentDate").value(invoice.getPaymentDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buyer").value(invoice.getBuyer()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seller").value(invoice.getSeller()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(invoice.getStatus().toString()))

                .andExpect(MockMvcResultMatchers.jsonPath("$.detailResponses[*]", hasSize(detailsList.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detailResponses[*].id").value(contains(detailsList.get(0).getId().intValue(), detailsList.get(1).getId().intValue(), detailsList.get(2).getId().intValue()))

                );


        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        String invoiceCreatedDateTimeStr = JsonPath.compile("$.createdDateTime").read(contentAsString);
        LocalDateTime invoiceCreatedDateTime = LocalDateTime.parse(invoiceCreatedDateTimeStr);
        assertThat(invoiceCreatedDateTime).isEqualToIgnoringNanos(invoice.getCreatedDate());

        int i = 0;
        for (InvoiceDetail detail : detailsList) {

            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.detailResponses[" + i + "].id").value(detail.getId().intValue()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.detailResponses[" + i + "].version").value(detail.getVersion()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.detailResponses[" + i + "].productName").value(detail.getProductName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.detailResponses[" + i + "].price").value(detail.getPrice().doubleValue()));

            LocalDateTime detailCreatedDate = parseDateTime(contentAsString, "$.detailResponses[" + i + "].createdDate");
            assertThat(detailCreatedDate).isEqualToIgnoringNanos(detail.getCreatedDate());
            i++;
        }
    }

    @Test
    void givenNoInvoiceInDb_whenGetFind_thenEmptyList() throws Exception {
        //given
        createUserAndAuthenticate();

        ResultActions resultActions = performGet(API_INVOICES_URL,
                Map.of(
                        "s", "seller",
                        "b", "Marcin Strzelecki",
                        "page", "0",
                        "size", "3"
                )
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", is(empty())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(0));

    }

    @Test
    void givenInvoices_whenGetFind_thenCorrectResponse() throws Exception {
        //given
        createUserAndAuthenticate();

        Invoice publishedAndActive1 = invoiceCreator.createInvoice(invoice -> invoice.setPaymentDate(LocalDate.now().minusDays(6)));
        Invoice publishedAndActive2 = invoiceCreator.createInvoice(invoice -> invoice.setPaymentDate(LocalDate.now().minusDays(5)));
        Invoice publishedAndActive3 = invoiceCreator.createInvoice(invoice -> invoice.setPaymentDate(LocalDate.now().minusDays(4)));

        //not matching by deleted
        invoiceCreator.createInvoice(invoice -> invoice.setStatus(InvoiceStatus.DELETED));

        Invoice publishedAndActive4 = invoiceCreator.createInvoice(invoice -> invoice.setPaymentDate(LocalDate.now().minusDays(3)));

        // not matching by seller
        invoiceCreator.createInvoice(invoice -> invoice.setSeller("nie pasuje sprzedawca"));

        Invoice publishedAndActive5 = invoiceCreator.createInvoice(invoice -> invoice.setPaymentDate(LocalDate.now().minusDays(2)));

        Invoice publishedAndActive6 = invoiceCreator.createInvoice(invoice -> invoice.setPaymentDate(LocalDate.now()));

        ResultActions resultActions = performGet(API_INVOICES_URL,
                Map.of(
                        "s", "Seller",
                        "b", "Marcin Strzelecki",
                        "page", "0",
                        "size", "3"
                )
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(6))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*]", hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].id").value(contains(
                        publishedAndActive1.getId().intValue(),
                        publishedAndActive2.getId().intValue(),
                        publishedAndActive3.getId().intValue()))

                );

    }
}
