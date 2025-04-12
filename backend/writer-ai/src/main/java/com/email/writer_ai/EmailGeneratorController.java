package com.email.writer_ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class EmailGeneratorController {

	private final EmailGeneratorService emailGeneratorService;

	@PostMapping("/reply")
	public ResponseEntity<String> generateEmailReply(@RequestBody EmailRequest emailRequest) {
		log.info("Request received: {}", emailRequest);
		final String response = emailGeneratorService.generateEmailReply(emailRequest);
		log.info("Sending back generated response: {}", response);
		return ResponseEntity.ok(response);
	}
}
