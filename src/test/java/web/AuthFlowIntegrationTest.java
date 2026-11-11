package web;

import app.PortalEstagiosApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(classes = PortalEstagiosApplication.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:portal;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void cadastroLoginSessaoEAutorizacaoNaoPermitemElevarPerfil() throws Exception {
        mockMvc.perform(post("/api/auth/register/student")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Ana","email":"ana@example.com","senha":"senha-segura-123",
                                 "curso":"Sistemas","perfil":"ADMINISTRADOR"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfil").value("ESTUDANTE"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.senha").doesNotExist());

        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana@example.com","senha":"senha-segura-123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil").value("ESTUDANTE"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        mockMvc.perform(get("/api/estudante/candidaturas").session(session))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/admin/resumo").session(session))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/auth/logout").session(session).with(csrf()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/estudante/candidaturas").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void chamadasProtegidasSemSessaoRetornam401() throws Exception {
        mockMvc.perform(get("/api/estudante/candidaturas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void requisicoesQueAlteramDadosExigemCsrf() throws Exception {
        mockMvc.perform(post("/api/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Bia","email":"bia@example.com","senha":"senha-segura-123",
                                 "curso":"Sistemas"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void endpointCsrfEmiteTokenAceitoNoCabecalhoDaApi() throws Exception {
        MvcResult csrfResult = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(cookie().exists("XSRF-TOKEN"))
                .andReturn();
        String token = com.jayway.jsonpath.JsonPath.read(
                csrfResult.getResponse().getContentAsString(), "$.token");
        MockCookie csrfCookie = new MockCookie("XSRF-TOKEN",
                csrfResult.getResponse().getCookie("XSRF-TOKEN").getValue());

        mockMvc.perform(post("/api/auth/register/student")
                        .cookie(csrfCookie)
                        .header("X-XSRF-TOKEN", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Caio","email":"caio@example.com","senha":"senha-segura-123",
                                 "curso":"Sistemas"}
                                """))
                .andExpect(status().isCreated());
    }
}
