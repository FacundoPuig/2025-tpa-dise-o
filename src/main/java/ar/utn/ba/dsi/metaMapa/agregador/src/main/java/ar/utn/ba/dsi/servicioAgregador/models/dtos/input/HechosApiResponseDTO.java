package ar.utn.ba.dsi.servicioAgregador.models.dtos.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class HechosApiResponseDTO {

	@JsonProperty("current_page")
	private int currentPage;

	private List<HechoAgregadorInputDTO> data;

	@JsonProperty("first_page_url")
	private String firstPageUrl;

	@JsonProperty("last_page")
	private int lastPage;

	@JsonProperty("last_page_url")
	private String lastPageUrl;

	@JsonProperty("next_page_url")
	private String nextPageUrl;

	@JsonProperty("prev_page_url")
	private String prevPageUrl;

	private int total;
}

