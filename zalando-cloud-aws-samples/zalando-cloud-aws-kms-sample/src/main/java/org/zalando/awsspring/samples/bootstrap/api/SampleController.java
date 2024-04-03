package org.zalando.awsspring.samples.bootstrap.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

	@GetMapping("/greeting")
	public Greeting greeting() {
		return new Greeting("Hello");
	}
	
	public static record Greeting(String text) {};
}
