package com.email.writer_ai;

import static io.micrometer.common.util.StringUtils.isNotBlank;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailGeneratorService {

	private final WebClient webClient;

	@Value("${api.gemini.url}")
	private String geminiApiUrl;

	@Value("${api.gemini.key}")
	private String geminiApiKey;

	public String generateEmailReply(EmailRequest emailRequest) {
		final String prompt = buildPrompt(emailRequest);
		Map<String, Object> requestBody = buildRequestBody(prompt);

		log.info("Sending request to gemini");
		String url = geminiApiUrl.concat("?key=").concat(geminiApiKey);
		String response = webClient.post()
				.uri(url)
				.bodyValue(requestBody).retrieve().bodyToMono(String.class)
				.block();

		return extractResponseContent(response);
	}

	private String extractResponseContent(final String response) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text")
					.asText();
		} catch (Exception e) {
			return "Error processing request: ".concat(e.getMessage());
		}
	}

	private static Map<String, Object> buildRequestBody(final String prompt) {
		return Map.of("contents", new Object[]{Map.of("parts", new Object[]{Map.of("text", prompt)})});
	}

	private String buildPrompt(EmailRequest emailRequest) {
		log.info("building prompt");
		StringBuilder prompt = new StringBuilder();
		prompt.append(
				"Generate a professional email reply for the following email content. Please don't generate a subject line. ");

		if (isNotBlank(emailRequest.getTone())) {
			prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
		}

		prompt.append("\n Original Email: \n").append(emailRequest.getEmailContent());
		return prompt.toString();
	}
}
