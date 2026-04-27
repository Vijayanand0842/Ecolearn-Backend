const express = require('express');
const cors = require('cors');
const sqlite3 = require('sqlite3').verbose();
const path = require('path');

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

const dbPath = path.resolve(__dirname, 'database.sqlite');
const db = new sqlite3.Database(dbPath, (err) => {
  if (err) console.error('Error opening db', err.message);
  else console.log('Connected to SQLite database.');
});

// --- AUTH ---
app.post('/api/users/login', (req, res) => {
    const { username, password } = req.body;
    db.get('SELECT id, username, role, points, verified FROM users WHERE username = ? AND password = ?', [username, password], (err, row) => {
        if (err) return res.status(500).json({ error: err.message });
        if (row) res.json(row);
        else res.status(401).json({ error: 'Invalid username or password' });
    });
});
app.post('/api/users/register', (req, res) => {
    const { username, password } = req.body;
    let role = username === 'admin' ? 'admin' : 'user';
    db.run('INSERT INTO users (username, password, role) VALUES (?, ?, ?)', [username, password, role], function(err) {
        if (err) return res.status(400).json({ error: 'Username taken' });
        res.json({ id: this.lastID, username, role, points: 0, verified: 0 });
    });
});

// --- ADMIN / USERS ---
app.get('/api/users', (req, res) => {
    db.all('SELECT id, username, role, points, verified FROM users', [], (err, rows) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(rows);
    });
});
app.get('/api/users/:id/details', (req, res) => {
    const userId = req.params.id;
    db.all('SELECT m.title, m.lesson_id FROM user_progress up JOIN modules m ON up.module_id = m.id WHERE up.user_id = ?', [userId], (err, mods) => {
        db.all('SELECT p.title, p.difficulty FROM project_progress pp JOIN projects p ON pp.project_id = p.id WHERE pp.user_id = ?', [userId], (err, projs) => {
           res.json({ modules: mods || [], projects: projs || [] });
        });
    });
});
app.post('/api/users/:id/verify', (req, res) => {
    db.run('UPDATE users SET verified = 1 WHERE id = ?', [req.params.id], function(err) {
        if (err) return res.status(500).json({ error: err.message });
        res.json({ success: true });
    });
});

app.get('/api/stats', (req, res) => {
    db.get('SELECT COUNT(*) as u FROM users', (err, u) => {
        db.get('SELECT COUNT(*) as l FROM lessons', (err, l) => {
            db.get('SELECT COUNT(*) as p FROM projects', (err, p) => {
                res.json({ users: u?.u, lessons: l?.l, projects: p?.p });
            });
        });
    });
});

// --- LESSONS & MODULES ---
app.get('/api/lessons', (req, res) => {
    db.all('SELECT * FROM lessons', [], (err, rows) => res.json(rows));
});
app.get('/api/lessons/:id/modules', (req, res) => {
    const { id } = req.params; const userId = req.query.userId;
    const sql = `SELECT m.*, CASE WHEN up.module_id IS NOT NULL THEN 1 ELSE 0 END as completed
                 FROM modules m LEFT JOIN user_progress up ON m.id = up.module_id AND up.user_id = ? WHERE m.lesson_id = ? ORDER BY m.order_index ASC`;
    db.all(sql, [userId, id], (err, rows) => res.json(rows||[]));
});
app.get('/api/modules/:id/pages', (req, res) => {
    db.all('SELECT * FROM pages WHERE module_id = ? ORDER BY page_number ASC', [req.params.id], (err, rows) => res.json(rows||[]));
});
app.get('/api/modules/:id/quiz', (req, res) => {
    db.get('SELECT * FROM quizzes WHERE module_id = ?', [req.params.id], (err, row) => {
        if (row) {
            row.questions = JSON.parse(row.questions_json);
            delete row.questions_json;
            res.json(row);
        } else res.status(404).json({ error: 'Quiz not found' });
    });
});
app.post('/api/modules/:id/complete', (req, res) => {
    const { userId } = req.body; const moduleId = req.params.id;
    db.run('INSERT OR IGNORE INTO user_progress (user_id, module_id) VALUES (?, ?)', [userId, moduleId], function(err) {
        if (this.changes > 0) {
            db.run('UPDATE users SET points = points + 50 WHERE id = ?', [userId], () => res.json({ success: true }));
        } else res.json({ success: true });
    });
});

// --- PROJECTS ---
app.get('/api/projects', (req, res) => {
    const userId = req.query.userId;
    const sql = `SELECT p.*, CASE WHEN pp.project_id IS NOT NULL THEN 1 ELSE 0 END as completed
                 FROM projects p LEFT JOIN project_progress pp ON p.id = pp.project_id AND pp.user_id = ?`;
    db.all(sql, [userId], (err, rows) => res.json(rows||[]));
});
app.post('/api/projects', (req, res) => {
    const { title, description, difficulty } = req.body;
    db.run('INSERT INTO projects (title, description, difficulty) VALUES (?, ?, ?)', [title, description, difficulty], function() {
        res.json({ success: true, id: this.lastID });
    });
});
app.post('/api/projects/:id/complete', (req, res) => {
    const { userId } = req.body; const projectId = req.params.id;
    db.run('INSERT OR IGNORE INTO project_progress (user_id, project_id) VALUES (?, ?)', [userId, projectId], function(err) {
        if (this.changes > 0) {
            db.run('UPDATE users SET points = points + 100 WHERE id = ?', [userId], () => res.json({ success: true }));
        } else res.json({ success: true });
    });
});

// --- Serve Frontend ---
app.use(express.static(path.join(__dirname, '../frontend/dist')));
app.use((req, res) => {
    res.sendFile(path.join(__dirname, '../frontend/dist/index.html'));
});

app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
