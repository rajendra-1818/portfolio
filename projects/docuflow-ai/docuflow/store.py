from dataclasses import dataclass
import sqlite3
from pathlib import Path


@dataclass(frozen=True)
class Document:
    id: int
    title: str
    text: str


class DocumentStore:
    def __init__(self, path: str = "docuflow.db"):
        self.path = path
        Path(path).parent.mkdir(parents=True, exist_ok=True)
        with self._connect() as db:
            db.execute("CREATE TABLE IF NOT EXISTS documents (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, text TEXT NOT NULL)")

    def _connect(self):
        return sqlite3.connect(self.path)

    def add(self, title: str, text: str) -> Document:
        with self._connect() as db:
            cursor = db.execute("INSERT INTO documents(title, text) VALUES (?, ?)", (title, text))
            return Document(cursor.lastrowid, title, text)

    def all(self) -> list[Document]:
        with self._connect() as db:
            rows = db.execute("SELECT id, title, text FROM documents ORDER BY id DESC").fetchall()
        return [Document(*row) for row in rows]
