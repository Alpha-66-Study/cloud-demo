package com.demo.ai.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

@RestController("/ai")
@RequiredArgsConstructor
public class AiController {

  private final OpenAiChatModel openAiChatModel;
  private final ChatClient openAiChatClient;
  private final OpenAiImageModel openAiImageModel;
  private final OpenAiAudioSpeechModel openAiAudioSpeechModel;
  private final ChatClient ollamaChatClient;

  @GetMapping("/generate")
  public String generate(@RequestParam(value = "message", defaultValue = "你是谁") String message) {
    return openAiChatModel.call(message);
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "你是谁") String message) {
    return openAiChatModel.stream(message);
  }

  @GetMapping("/generateSystem")
  public String generateSystem(@RequestParam(value = "message", defaultValue = "你是谁") String message) {
    return openAiChatClient.prompt().user(message).call().content();
  }

  @GetMapping("/generateImage")
  public void generateImage(@RequestParam(value = "prompt", defaultValue = "小猫打篮球") String prompt,
                            HttpServletResponse response) throws IOException {
    ImageResponse imageResponse = openAiImageModel.call(new ImagePrompt(prompt));
    String imageUrl = imageResponse.getResult().getOutput().getUrl();
    try (InputStream imageInputStream = new URL(imageUrl).openStream();
         OutputStream out = response.getOutputStream()) {
      response.setContentType("image/png");
      response.setHeader("Content-Disposition", "inline; filename=generated.png");
      imageInputStream.transferTo(out);
      out.flush();
    }
  }

  @GetMapping("/generateAudio")
  public void generateAudio(@RequestParam(value = "prompt", defaultValue = "床前明月光，疑是地上霜") String prompt,
                            HttpServletResponse response) throws IOException {
    response.setContentType("audio/mpeg");
    response.setHeader("Content-Disposition", "inline; filename=audio.mp3");
    SpeechResponse aiResponse = openAiAudioSpeechModel.call(new SpeechPrompt(prompt));
    byte[] output = aiResponse.getResult().getOutput();
    OutputStream os = response.getOutputStream();
    os.write(output);
    os.flush();
    os.close();
  }

  @GetMapping("/generateWithOllama")
  public String generateWithOllama(@RequestParam(value = "prompt", defaultValue = "你是谁") String message) {
    return ollamaChatClient.prompt().user(message).call().content();
  }

}
