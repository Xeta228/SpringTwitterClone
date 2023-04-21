package com.baron.webapp;

import com.baron.webapp.controller.Controller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void test() throws Exception{
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
                content().string(containsString("Hello, guest")));
    }
    @Test
    public void loginTest() throws Exception{
        this.mockMvc.perform(get("/main")).andDo(print()).andExpect(status().is3xxRedirection()).andExpect(
                redirectedUrl("http://localhost/login"));
    }
    @Test
    public void correctLoginTest() throws Exception{
        this.mockMvc.perform(formLogin().user("admin").password("0000"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main"));
    }
    @Test
    public void badCredentials() throws Exception{
        this.mockMvc.perform(post("/login").param("user","Alfred"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
