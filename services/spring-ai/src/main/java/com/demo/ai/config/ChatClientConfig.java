package com.demo.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

  @Bean
  public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel) {
    return ChatClient.builder(openAiChatModel).defaultSystem("你是一名区块链专家，你精通区块链开发技术，你的名字叫小王。").build();
  }

  @Bean
  public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
    return ChatClient.builder(ollamaChatModel).build();
  }

}
