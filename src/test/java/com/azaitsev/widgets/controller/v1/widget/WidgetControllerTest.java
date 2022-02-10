package com.azaitsev.widgets.controller.v1.widget;

import com.azaitsev.widgets.controller.v1.widget.dto.CreateWidgetRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WidgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void post_shouldCreateWidgetWithNoZSpecified() throws Exception {
        CreateWidgetRequest request = new CreateWidgetRequest();

        request.setX(10);
        request.setY(20);
        request.setWidth(100);
        request.setHeight(200);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/widgets")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(request.getX()))
                .andExpect(jsonPath("$.y").value(request.getY()))
                .andExpect(jsonPath("$.z").value(1))
                .andExpect(jsonPath("$.width").value(request.getWidth()))
                .andExpect(jsonPath("$.height").value(request.getHeight()))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.lastModificationDate").exists());
    }

    @Test
    public void post_shouldCreateWidgetWithSpecifiedZ() throws Exception {
        CreateWidgetRequest request = new CreateWidgetRequest();

        request.setX(10);
        request.setY(20);
        request.setZ(30);
        request.setWidth(100);
        request.setHeight(200);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/widgets")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(request.getX()))
                .andExpect(jsonPath("$.y").value(request.getY()))
                .andExpect(jsonPath("$.z").value(request.getZ()))
                .andExpect(jsonPath("$.width").value(request.getWidth()))
                .andExpect(jsonPath("$.height").value(request.getHeight()))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.lastModificationDate").exists());
    }

    @Test
    public void post_shouldReturnBadRequestDueToInvalidWidth() throws Exception {
        CreateWidgetRequest request = new CreateWidgetRequest();

        request.setX(10);
        request.setY(20);
        request.setWidth(-100);
        request.setHeight(200);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/widgets")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}
