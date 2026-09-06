from fastapi.testclient import TestClient
from docuflow.main import create_app
from docuflow.store import DocumentStore


def client(tmp_path):
    return TestClient(create_app(DocumentStore(str(tmp_path / "test.db"))))


def test_ingest_and_search(tmp_path):
    api = client(tmp_path)
    response = api.post("/documents", json={"title": "Deployments", "text": "Kubernetes deployments should expose health checks and rollback plans."})
    assert response.status_code == 201
    response = api.get("/search", params={"q": "health checks"})
    assert response.status_code == 200
    assert response.json()[0]["title"] == "Deployments"


def test_rejects_short_documents(tmp_path):
    assert client(tmp_path).post("/documents", json={"title": "Too short", "text": "tiny"}).status_code == 422
