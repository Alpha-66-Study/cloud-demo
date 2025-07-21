# Spring AI 模型（Models）详细说明

## 一、Spring AI 简介
Spring AI 是一个面向 Java/Spring 生态的 AI 应用开发框架，致力于简化 AI 能力（如大模型、嵌入、图片、音频等）在企业应用中的集成。其核心理念是：用统一、可移植的 API 连接企业数据/API 与主流 AI 模型。

---

## 二、模型类型（Models）
Spring AI 支持多种主流 AI 模型，按输入/输出类型主要分为：

- **聊天模型（Chat Models）**：输入/输出均为文本，典型如 OpenAI GPT、Anthropic Claude、Azure OpenAI、Ollama、Google Gemini 等。
- **嵌入模型（Embedding Models）**：输入为文本，输出为向量（数字数组），用于语义检索、RAG 等场景。
- **图片模型（Image Models）**：输入为文本，输出为图片，如文生图（Stable Diffusion、DALL·E 等）。
- **音频模型（Audio Models）**：如语音转文本（ASR）、文本转语音（TTS）。
- **内容审核模型（Moderation Models）**：用于文本内容安全检测。

Spring AI 通过统一的接口屏蔽了不同厂商、不同模型的差异，便于灵活切换。

---

## 三、核心概念

### 1. Prompt（提示词）
Prompt 是与 AI 模型交互的输入，既可以是简单字符串，也可以是多角色消息（如 system、user、assistant）。Spring AI 支持模板化 prompt，便于动态填充内容。

### 2. Embedding（嵌入）
将文本/图片等内容转为高维向量，常用于语义检索、RAG（检索增强生成）等。

### 3. Token（令牌）
AI 模型处理的最小单位。计费和上下文长度通常以 token 为单位。

### 4. Structured Output（结构化输出）
通过特殊 prompt 或工具调用，将模型输出转为结构化数据（如 JSON）。

### 5. Tool Calling（工具调用）
支持模型调用外部 API/函数，扩展模型能力，实现“智能体”场景。

---

## 四、主要 API 及用法

### 1. ChatModel（聊天模型接口）
统一的聊天模型接口，支持多厂商切换。

```java
public interface ChatModel extends Model<Prompt, ChatResponse> {
    default String call(String message);
    ChatResponse call(Prompt prompt);
}
```

- `call(String message)`：快速调用，适合简单场景。
- `call(Prompt prompt)`：支持多角色、复杂参数。

#### 典型用法
```java
@Autowired
private ChatClient chatClient;

public String askAI(String question) {
    ChatResponse response = chatClient.call(new ChatRequest(question));
    return response.getResult();
}
```

### 2. StreamingChatModel（流式聊天模型）
支持响应流式返回，适合长文本、实时对话。

### 3. EmbeddingModel（嵌入模型接口）
将文本转为向量，常用于语义检索、RAG。

---

## 五、模型配置与切换

Spring AI 支持多种主流模型，常见配置方式如下：

### 1. 添加依赖
以 OpenAI 为例：
```xml
<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

### 2. 配置密钥
在 `application.yml` 中配置：
```yaml
spring:
  ai:
    openai:
      api-key: sk-xxxxxx
```

### 3. 切换模型
只需更换依赖和配置即可切换到如 Azure OpenAI、Anthropic、Ollama、Bedrock 等。

---

## 六、常见场景
- 智能问答/对话
- 文档问答（RAG）
- 文本生成/总结/改写
- 语义检索/向量数据库
- 多模态（文生图、语音识别等）
- 内容审核

---

## 七、参考链接
- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Spring AI 项目主页](https://spring.io/projects/spring-ai/)

---

> 本文档基于 Spring AI 官方文档整理，适合 Java/Spring 开发者快速了解和集成主流 AI 模型能力。 