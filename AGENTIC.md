# 🤖 Agentic Strategy & AI Reasoning

This document outlines the **Agentic Architecture** used in the `search-service`. Unlike a standard RAG system that simply "retrieves and summarizes," this engine uses an **Agentic Loop** to reason about the user's intent before taking action.

## 🧠 The "Reasoning Loop"

The system utilizes the **ReAct (Reason + Act)** pattern (implemented via Spring AI's `Call` and `FunctionCallback` APIs).

When a user submits a query, the Agent follows this flow:

1.  **Thought:** Analyze the request. Is it a factual question? A summarization task? Does it require external data?
2.  **Tool Selection:** Choose the appropriate tool from the registered Java beans.
3.  **Observation:** Execute the tool and read the output.
4.  **Synthesis:** Generate the final answer.

### 🛠 Registered Tools (Functions)

The Spring Boot application exposes the following Java methods as "Tools" to the LLM:

| Tool Name | Description | Java Interface |
| :--- | :--- | :--- |
| `knowledgeBaseSearch` | **Primary Tool.** Searches the internal Vector Database for technical documentation and ingested files. | `KnowledgeBaseService.search(String query)` |
| `webSearch` | *(Fallback)* Uses Google/Bing Search API if the internal Knowledge Base returns low confidence scores. | `WebSearchService.search(String query)` |
| `documentSummarizer` | Specialized tool for reading full document content when the user asks for "TL;DR" of a specific file ID. | `DocumentService.getSummary(String docId)` |
| `currentDateTime` | Returns the current server time. | `TimeService.now()` |

## 🏗 Implementation Details

We use **Spring AI Function Calling** to bind Java logic to the LLM.

### 1. Defining a Tool

Tools are defined as standard Java `Function` beans in `AgentConfiguration.java`:

```java
@Bean
@Description("Search the internal knowledge base for technical documents")
public Function<SearchRequest, SearchResponse> knowledgeBaseSearch() {
    return request -> {
        // Logic to query Vector Store
        return vectorStore.similaritySearch(request.query());
    };
}
```

### 2. The System Prompt

The Agent is guided by a system prompt stored in `src/main/resources/prompts/system-agent.st`:

> "You are an intelligent Enterprise Search Assistant.
> You have access to a private knowledge base.
> ALWAYS check the knowledgeBaseSearch tool first before answering.
> If the user asks for current events not in the database, politely decline or use webSearch if enabled.
> Cite your sources by referencing the [Source: filename] provided in the tool output."

## 🔮 Future Capabilities

*   **Multi-Step Reasoning:** Ability to break down complex questions (e.g., "Compare the revenue of Q1 vs Q2") into two separate search steps.
*   **Human-in-the-loop:** If the confidence score is low, the Agent will ask a clarifying question back to the user.
