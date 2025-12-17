package ar.utn.ba.dsi.fuenteEstatica.controller;

import ar.utn.ba.dsi.fuenteEstatica.models.dtos.output.HechoEstaticaOutputDTO;
import ar.utn.ba.dsi.fuenteEstatica.services.IEstaticaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstaticaController.class)
public class EstaticaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IEstaticaService estaticaService;

	@Test
	void getHechos_deberiaRetornarListaDeHechosYStatusOK() throws Exception {
		HechoEstaticaOutputDTO hechoDTO = new HechoEstaticaOutputDTO();
		hechoDTO.setTitulo("Test Hecho");
		List<HechoEstaticaOutputDTO> listaHechos = Collections.singletonList(hechoDTO);
		when(estaticaService.enviarHechosAAgregador()).thenReturn(listaHechos);
		mockMvc.perform(get("/estatica/hechos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].titulo").value("Test Hecho"));
	}

	@Test
	void cargarCSV_deberiaLlamarAlServicioYRetornarStatusOK() throws Exception {
		MockMultipartFile mockFile = new MockMultipartFile("archivoCSV", "hechos.csv", "text/csv", "data".getBytes());
		mockMvc.perform(multipart("/estatica/cargar-csv").file(mockFile))
				.andExpect(status().isOk());
		verify(estaticaService).cargarCSV(mockFile);
	}

	@Test
	void getHechos_cuandoNoHayHechos_deberiaRetornarListaVaciaYStatusOK() throws Exception {
		when(estaticaService.enviarHechosAAgregador()).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/estatica/hechos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	void getHechos_cuandoServicioFalla_deberiaRetornarError500() throws Exception {
		when(estaticaService.enviarHechosAAgregador()).thenThrow(new RuntimeException("Error simulado al leer el archivo"));
		mockMvc.perform(get("/estatica/hechos"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	void cargarCSV_cuandoServicioFalla_deberiaRetornarError500() throws Exception {
		MockMultipartFile mockFile = new MockMultipartFile("archivoCSV", "archivo_fallido.csv", "text/csv", "datos".getBytes());
		doThrow(new RuntimeException("Error simulado al guardar")).when(estaticaService).cargarCSV(any(MultipartFile.class));
		mockMvc.perform(multipart("/estatica/cargar-csv").file(mockFile))
				.andExpect(status().isInternalServerError());
	}

	@Test
	void cargarCSV_conArchivoVacio_deberiaAceptarLaPeticionYLLamarAlServicio() throws Exception {
		// Arrange: Creamos un archivo simulado que está vacío (0 bytes).
		MockMultipartFile archivoVacio = new MockMultipartFile(
				"archivoCSV",
				"archivo_vacio.csv",
				"text/csv",
				new byte[0] // Contenido vacío
		);

		// Act & Assert: Hacemos la petición y esperamos una respuesta exitosa (200 OK).
		mockMvc.perform(multipart("/estatica/cargar-csv").file(archivoVacio))
				.andExpect(status().isOk());

		// Verificamos que el controlador aun así intentó pasarle el archivo al servicio.
		// La lógica de manejar un archivo vacío recae en el servicio/lector, no en el controlador.
		verify(estaticaService).cargarCSV(archivoVacio);
	}
}