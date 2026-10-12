package offeria.rfq_service.web.controller;

import offeria.rfq_service.service.RfqService;
import offeria.rfq_service.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RfqControllerTest {

    private RfqService rfqService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        rfqService = mock(RfqService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new RfqController(rfqService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldRejectEmptyRequest() throws Exception {
        mockMvc.perform(post("/api/v1/rfqs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.rfqNumber").value("RFQ number is required"))
                .andExpect(jsonPath("$.title").value("Title is required"))
                .andExpect(jsonPath("$.clientId").value("Client ID is required"))
                .andExpect(jsonPath("$.projectId").value("Project ID is required"));

        verifyNoInteractions(rfqService);
    }

    @Test
    void shouldRejectBlankRfqNumberAndTitle() throws Exception {
        String request = """
                {
                  "rfqNumber": " ",
                  "title": "",
                  "clientId": "11111111-1111-1111-1111-111111111111",
                  "projectId": "22222222-2222-2222-2222-222222222222"
                }
                """;

        mockMvc.perform(post("/api/v1/rfqs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.rfqNumber").value("RFQ number is required"))
                .andExpect(jsonPath("$.title").value("Title is required"));

        verifyNoInteractions(rfqService);
    }

    @Test
    void shouldRejectInvalidUuid() throws Exception {
        String request = """
                {
                  "rfqNumber": "RFQ-001",
                  "title": "Test RFQ",
                  "clientId": "invalid-uuid",
                  "projectId": "22222222-2222-2222-2222-222222222222"
                }
                """;

        mockMvc.perform(post("/api/v1/rfqs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(rfqService);
    }
}
