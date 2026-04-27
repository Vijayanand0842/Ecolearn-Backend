const sqlite3 = require('sqlite3').verbose();
const path = require('path');
const fs = require('fs');

const dbPath = path.resolve(__dirname, 'database.sqlite');
if (fs.existsSync(dbPath)) fs.unlinkSync(dbPath);

const db = new sqlite3.Database(dbPath, (err) => {
  if (err) throw err;
  console.log('Connected to new SQLite database.');
});

db.serialize(() => {
  db.run(`CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, role TEXT DEFAULT 'user', points INTEGER DEFAULT 0, verified INTEGER DEFAULT 0
  )`);
  db.run(`CREATE TABLE IF NOT EXISTS lessons (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, category TEXT, description TEXT)`);
  db.run(`CREATE TABLE IF NOT EXISTS modules (id INTEGER PRIMARY KEY AUTOINCREMENT, lesson_id INTEGER, title TEXT, order_index INTEGER)`);
  db.run(`CREATE TABLE IF NOT EXISTS pages (id INTEGER PRIMARY KEY AUTOINCREMENT, module_id INTEGER, content TEXT, page_number INTEGER)`);
  db.run(`CREATE TABLE IF NOT EXISTS quizzes (id INTEGER PRIMARY KEY AUTOINCREMENT, module_id INTEGER, questions_json TEXT)`);
  db.run(`CREATE TABLE IF NOT EXISTS user_progress (user_id INTEGER, module_id INTEGER, PRIMARY KEY (user_id, module_id))`);
  db.run(`CREATE TABLE IF NOT EXISTS projects (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, difficulty TEXT)`);
  db.run(`CREATE TABLE IF NOT EXISTS project_progress (user_id INTEGER, project_id INTEGER, PRIMARY KEY (user_id, project_id))`);

  // Admins
  db.run("INSERT INTO users (username, password, role, verified) VALUES ('admin', 'admin123', 'admin', 1)");

  // 20 Projects
  const difficulties = ['Easy', 'Medium', 'Hard'];
  const pStmt = db.prepare("INSERT INTO projects (title, description, difficulty) VALUES (?, ?, ?)");
  for (let i = 1; i <= 20; i++) {
    pStmt.run(
      `Eco Project #${i}: ${i%2===0 ? 'Community Outreach' : 'Home Optimization'}`, 
      `Complete this amazing project to greatly improve the sustainability footprint of your local area. Documentation required.`, 
      difficulties[i % 3]
    );
  }
  pStmt.finalize();

  // 10 Lessons -> 8 Modules -> 3 Pages + 1 Multi-Question Quiz
  const lessonThemes = [
    { cat: 'Energy', t: 'Solar Power Fundamentals' }, { cat: 'Energy', t: 'Wind Turbines & Microgrids' },
    { cat: 'Waste', t: 'Zero Waste Living Handbook' }, { cat: 'Waste', t: 'Advanced Composting Techniques' },
    { cat: 'Water', t: 'Water Conservation & Harvesting' }, { cat: 'Food', t: 'Urban Permaculture 101' },
    { cat: 'Food', t: 'Plant-Based Diets for Climate' }, { cat: 'Lifestyle', t: 'Sustainable Fashion Ethics' },
    { cat: 'Lifestyle', t: 'Minimalism & Eco-Footprint' }, { cat: 'Policy', t: 'Climate Activism & Policy' }
  ];

  const stmtLesson = db.prepare("INSERT INTO lessons (title, category, description) VALUES (?, ?, ?)");
  const stmtModule = db.prepare("INSERT INTO modules (lesson_id, title, order_index) VALUES (?, ?, ?)");
  const stmtPage = db.prepare("INSERT INTO pages (module_id, content, page_number) VALUES (?, ?, ?)");
  const stmtQuiz = db.prepare("INSERT INTO quizzes (module_id, questions_json) VALUES (?, ?)");

  let moduleIdCounter = 1;
  for (let l = 0; l < 10; l++) {
    const lessonTitle = lessonThemes[l].t;
    stmtLesson.run(lessonTitle, lessonThemes[l].cat, `A comprehensive masterclass on ${lessonTitle}.`);
    const lessonId = l + 1;

    for (let m = 1; m <= 8; m++) {
      stmtModule.run(lessonId, `Module ${m}: Understanding ${lessonTitle} Core Principles`, m);
      
      for (let p = 1; p <= 3; p++) {
        let content = `Welcome to Page ${p} of Module ${m}. \n\nIn this section, we deeply explore the intricacies of ${lessonTitle}. Sustainability requires continuous effort and learning.` 
        + (p===1 ? "Let's start with definitions." : p===2 ? "Here are some core frameworks to understand." : "Finally, how does this apply to real life?");
        stmtPage.run(moduleIdCounter, content, p);
      }

      // Quiz with 3 questions
      const quizQuestions = [
        { question: `What is the key takeaway for ${lessonTitle}?`, options: ["Eco-friendly routines", "Ignoring carbon", "Consumption", "Non-renewables"], correct_answer: 0 },
        { question: `Which method helps implement Module ${m}?`, options: ["Complacency", "Proactive Planning", "Denial", "Waste"], correct_answer: 1 },
        { question: `Is sustainability a continuous effort?`, options: ["No, it's a phase", "Only for governments", "Yes, it is continuous", "Not important"], correct_answer: 2 }
      ];
      stmtQuiz.run(moduleIdCounter, JSON.stringify(quizQuestions));

      moduleIdCounter++;
    }
  }

  stmtLesson.finalize(); stmtModule.finalize(); stmtPage.finalize(); stmtQuiz.finalize();
  console.log("Seeded DB with 20 Projects, massive module tree, and Multi-Question Quizzes.");
});

db.close();
