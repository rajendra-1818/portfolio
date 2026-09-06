import os
from fastapi import FastAPI, Query
from pydantic import BaseModel, Field, ConfigDict
from .retrieval import Retriever
from .store import DocumentStore


class DocumentIn(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)
    title: str = Field(min_length=1, max_length=200)
    text: str = Field(min_length=20, max_length=100_000)


class DocumentOut(BaseModel):
    id: int
    title: str
    text: str


class SearchOut(BaseModel):
    id: int
    title: str
    score: float
    excerpt: str


def create_app(store: DocumentStore | None = None) -> FastAPI:
    app = FastAPI(title="DocuFlow AI", version="1.0.0", description="Search a local document collection with explainable lexical retrieval. This demo has no authentication.")
    repository = store or DocumentStore(os.environ.get("DOCUFLOW_DB", "docuflow.db"))
    retriever = Retriever()

    @app.get("/health")
    def health():
        return {"status": "ok", "documents": len(repository.all())}

    @app.post("/documents", response_model=DocumentOut, status_code=201)
    def add_document(document: DocumentIn):
        return repository.add(document.title, document.text)

    @app.get("/documents", response_model=list[DocumentOut])
    def list_documents():
        return repository.all()

    @app.get("/search", response_model=list[SearchOut])
    def search(q: str = Query(min_length=2, max_length=500), limit: int = Query(default=5, ge=1, le=20)):
        return [SearchOut(id=result.document.id, title=result.document.title,
                          score=round(result.score, 4), excerpt=result.excerpt)
                for result in retriever.search(repository.all(), q, limit)]

    return app


app = create_app()
