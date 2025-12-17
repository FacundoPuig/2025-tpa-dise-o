/*
package ar.utn.ba.dsi.fuenteDinamica.controller;

import ar.utn.ba.dsi.fuenteDinamica.SecurityConfig;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.input.HechoDinamicaInputDTO;
import ar.utn.ba.dsi.fuenteDinamica.services.IDinamicaService;
import ar.utn.ba.dsi.fuenteDinamica.services.IEdicionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DinamicaController.class)
@Import(SecurityConfig.class)
public class DinamicaControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private IDinamicaService dinamicaService;
	@MockBean
	private IEdicionService edicionService;
	@MockBean
	private UserDetailsServiceImpl userDetailsService;

	@Test
	@WithMockUser(username = "user-1")
	void crearHecho_conUsuarioValido_deberiaRetornarCreated() throws Exception {
		HechoDinamicaInputDTO hechoInput = new HechoDinamicaInputDTO();
		hechoInput.setTitulo("Nuevo Hecho");
		MockMultipartFile hechoData = new MockMultipartFile("hechoData", "", "application/json", objectMapper.writeValueAsBytes(hechoInput));

		mockMvc.perform(multipart("/dinamica/hechos").file(hechoData).with(csrf()))
				.andExpect(status().isCreated());
	}

	@Test
	void crearHecho_comoUsuarioAnonimo_deberiaRetornarCreated() throws Exception {
		HechoDinamicaInputDTO hechoInput = new HechoDinamicaInputDTO();
		hechoInput.setTitulo("Hecho Anónimo");
		MockMultipartFile hechoData = new MockMultipartFile("hechoData", "", "application/json", objectMapper.writeValueAsBytes(hechoInput));

		mockMvc.perform(multipart("/dinamica/hechos").file(hechoData).with(csrf()))
				.andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "admin-1", roles = "ADMIN")
	void eliminarHecho_conPermisosDeAdmin_deberiaRetornarNoContent() throws Exception {
		mockMvc.perform(delete("/dinamica/hechos/Hecho a eliminar").with(csrf()))
				.andExpect(status().isNoContent());

		verify(dinamicaService, times(1)).eliminar("Hecho a eliminar", "admin-1");
	}
}*/
