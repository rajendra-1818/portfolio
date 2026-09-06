# DocuFlow AI

DocuFlow is a local document retrieval demo for text knowledge bases. It ingests text into SQLite and ranks search results using TF-IDF vectors and cosine similarity, returning a matching excerpt. TF-IDF is lexical retrieval; this project does not use a neural embedding model or generate LLM answers.

## What it demonstrates

- FastAPI route design with Pydantic validation and OpenAPI documentation.
- A repository boundary around SQLite persistence.
- A retrieval component that can be replaced by a hosted embedding model later.
- Search results with scores and excerpts instead of an opaque answer.
- Testable application construction with an injected temporary store.

## Run locally

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn docuflow.main:app --reload
```

Open `http://localhost:8000/docs`, or try:

```bash
curl -X POST http://localhost:8000/documents -H 'Content-Type: application/json' \
  -d '{"title":"Runbook","text":"Deployments require health checks, rollback plans, and observable logs."}'
curl 'http://localhost:8000/search?q=rollback+health'
```

## Stack

Python 3.12 · FastAPI · Pydantic · SQLite · scikit-learn · pytest · Docker

## Tests and persistence

Run `python -m pytest -q` from this directory. Tests cover ingestion, request validation, ranking, no-match results, punctuation-only input, and SQLite persistence. `DOCUFLOW_DB` selects the database file; the default is `docuflow.db` in the working directory.

```bash
docker build -t docuflow-ai .
docker run --rm -p 127.0.0.1:8000:8000 -v docuflow-data:/data -e DOCUFLOW_DB=/data/docuflow.db docuflow-ai
```

## Limits

This demo has no authentication or tenant isolation. Search rebuilds the TF-IDF index for each request and is intended for small local collections. Documents must be submitted as text through the API; there is no upload parser, React frontend, or LLM integration. Scores indicate lexical similarity, not answer confidence.

The project intentionally keeps retrieval local and deterministic. A production version could add chunking, embeddings, access control, background indexing, and an LLM answer layer while preserving the same search contract.
