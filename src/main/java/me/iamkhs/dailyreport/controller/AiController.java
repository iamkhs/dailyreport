package me.iamkhs.dailyreport.controller;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiController {

    @Value("${GEMINI_API_KEY}")
    private String APIKEY;

    @PostMapping("/summarize")
    public String summarize(@RequestBody String text) {
        String prompt =
                "You are a professional software engineer who writes concise, clear, and professional daily work reports. \n" +
                        "I will provide you with a list of Git commit messages from today. \n" +
                        "\n" +
                        "Your task:\n" +
                        "- Rephrase them into short, bullet-point statements.\n" +
                        "- Make them understandable to a non-technical manager.\n" +
                        "- Keep each point one line, simple, and professional.\n" +
                        "- Include the action performed and the module or feature affected if possible.\n" +
                        "- Do not add extra commentary, just the summarized tasks.\n" +
                        "Here are the commits:\n" + text;

        Client client = Client.builder().apiKey(APIKEY).build();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null);

        return response.text();
    }

}
