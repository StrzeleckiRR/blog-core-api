package pl.mojastrona.accountant;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class FindAccountantResponseTest {

    @Test
    void givenAccountant_whenFrom_thenCorrectResponse() {

        long expectedId = 100L;
        String expectedName = "Ksiegowy Paweł";

        Accountant accountant = Accountant.builder()
                .id(expectedId)
                .name(expectedName)
                .build();

        FindAccountantResponse response = FindAccountantResponse.from(accountant);


//        assertNotNull(response);
//        assertEquals(expectedId, response.getId());
//        assertEquals(expectedName, response.getName());

          assertThat(response).isNotNull();
          assertThat(response.getId()).isEqualTo(expectedId);
          assertThat(response.getName()).isEqualTo(expectedName);



    }
}