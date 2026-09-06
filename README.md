# Sri Rajendra Dabburi — Engineering Portfolio

Three independently runnable projects exploring backend workflows, local document search, and a frontend reliability console.

| Project | What is implemented | Stack |
| --- | --- | --- |
| [Orderflow API](projects/orderflow-api) | Order validation, totals, atomic state transitions, filtering, health endpoint | Java 21, Spring Boot, JUnit, Docker |
| [DocuFlow AI](projects/docuflow-ai) | SQLite document ingestion, TF-IDF retrieval, scored excerpts, API docs | Python, FastAPI, scikit-learn, pytest, Docker |
| [OpsBoard](projects/opsboard-react) | Responsive service dashboard, search, health filters, CSV export | React, TypeScript, Vite |

Start with each project's README for prerequisites, commands, tests, and limitations. All three are demos: Orderflow uses in-memory storage, DocuFlow provides lexical retrieval without an LLM, and OpsBoard uses fictional sample data.

Automated checks live in [.github/workflows/projects-ci.yml](.github/workflows/projects-ci.yml). The existing [engineering essay](index.html) is also included in this repository.

[LinkedIn](https://www.linkedin.com/in/sri-rajendra/) · [GitHub](https://github.com/rajendra-1818)
