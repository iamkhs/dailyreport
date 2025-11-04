package me.iamkhs.dailyreport.service;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    @Value("${GEMINI_API_KEY}")
    private String APIKEY;

    public String summarizeCommits(String commits) {

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
                        "- If the commits empty then response no commit found something like this but dont do extra" +
                        "- If something commit like merge with while when merging with another branch just ignore it\n" +
                        "Here are the commits:\n" + commits;

        try {
            Client client = Client.builder().apiKey(APIKEY).build();

            var response =
                    client.models.generateContent(
                            "gemini-2.5-flash",
                            prompt,
                            null);

            return response.text();
        } catch (Exception e) {
            System.out.println(e.getCause() + " " + e.getMessage());
            return e.getMessage();
        }

    }
}
