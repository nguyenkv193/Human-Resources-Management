package com.extreme.humanresources;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class HumanResourcesCoreWebTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminCanRenderHrCorePages() throws Exception {
        var admin = user("hradmin").roles("ADMIN");

        mockMvc.perform(get("/employees").with(admin))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/list"));
        mockMvc.perform(get("/departments").with(admin))
                .andExpect(status().isOk())
                .andExpect(view().name("department/list"));
        mockMvc.perform(get("/positions").with(admin))
                .andExpect(status().isOk())
                .andExpect(view().name("position/list"));
        mockMvc.perform(get("/attendance").with(admin))
                .andExpect(status().isOk())
                .andExpect(view().name("attendance/index"));
        mockMvc.perform(get("/leave").with(admin))
                .andExpect(status().isOk())
                .andExpect(view().name("leave/list"));
    }

    @Test
    void linkedEmployeeCanRenderSelfServicePages() throws Exception {
        var employee = user("employee").roles("USER", "EMPLOYEE");

        mockMvc.perform(get("/attendance").with(employee))
                .andExpect(status().isOk())
                .andExpect(view().name("attendance/index"));
        mockMvc.perform(get("/leave").with(employee))
                .andExpect(status().isOk())
                .andExpect(view().name("leave/list"));
    }

    @Test
    void employeeCannotOpenSensitiveAdminDirectories() throws Exception {
        var employee = user("employee").roles("USER", "EMPLOYEE");

        mockMvc.perform(get("/employees").with(employee))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/departments").with(employee))
                .andExpect(status().isForbidden());
    }
}
