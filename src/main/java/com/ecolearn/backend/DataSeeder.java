package com.ecolearn.backend;

import com.ecolearn.backend.model.*;
import com.ecolearn.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;



@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private ModuleRepository moduleRepository;
    @Autowired private PageRepository pageRepository;
    @Autowired private QuizRepository quizRepository;
    @Autowired private ProjectRepository projectRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            seedUsers();
        }
        if (projectRepository.count() == 0) {
            seedProjects();
        }
        if (lessonRepository.count() == 0) {
            seedLessons();
        }
        System.out.println("DataSeeder completed. Ensured Users, Projects, Modules, and Quizzes exist.");
    }

    private void seedUsers() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setRole("admin");
        admin.setVerified(1);
        admin.setPoints(0);
        userRepository.save(admin);
    }

    private void seedProjects() {
        String[] difficulties = {"Easy", "Medium", "Hard"};
        for (int i = 1; i <= 20; i++) {
            Project project = new Project();
            project.setTitle("Eco Project #" + i + ": " + (i % 2 == 0 ? "Community Outreach" : "Home Optimization"));
            project.setDescription("Complete this amazing project to greatly improve the sustainability footprint of your local area. Documentation required.");
            project.setDifficulty(difficulties[i % 3]);
            projectRepository.save(project);
        }
    }

    private void seedLessons() {
        String[][] lessonThemes = {
            {"Energy", "Solar Power Fundamentals"}, {"Energy", "Wind Turbines & Microgrids"},
            {"Waste", "Zero Waste Living Handbook"}, {"Waste", "Advanced Composting Techniques"},
            {"Water", "Water Conservation & Harvesting"}, {"Food", "Urban Permaculture 101"},
            {"Food", "Plant-Based Diets for Climate"}, {"Lifestyle", "Sustainable Fashion Ethics"},
            {"Lifestyle", "Minimalism & Eco-Footprint"}, {"Policy", "Climate Activism & Policy"}
        };

        for (int l = 0; l < lessonThemes.length; l++) {
            String category = lessonThemes[l][0];
            String title = lessonThemes[l][1];

            Lesson lesson = new Lesson();
            lesson.setTitle(title);
            lesson.setCategory(category);
            lesson.setDescription("A comprehensive masterclass on " + title + ".");
            lesson = lessonRepository.save(lesson);

            String[] moduleNames = {
                "Introduction and Foundations",
                "Historical Context and Evolution",
                "Core Methodologies and Frameworks",
                "Practical Applications in Daily Life",
                "Economic and Social Impacts",
                "Advanced Strategies for Scaling",
                "Common Challenges and Solutions",
                "Future Trends and Innovations"
            };

            for (int m = 1; m <= 8; m++) {
                ModuleEntity mod = new ModuleEntity();
                mod.setLessonId(lesson.getId());
                mod.setTitle("Chapter " + m + ": " + moduleNames[m-1]);
                mod.setOrderIndex(m);
                mod = moduleRepository.save(mod);

                for (int p = 1; p <= 3; p++) {
                    Page page = new Page();
                    page.setModuleId(mod.getId());
                    String content = "";
                    if (p == 1) {
                        content = "Welcome to Chapter " + m + ", Section 1.\n\n" +
                            "This chapter focuses on " + moduleNames[m-1].toLowerCase() + " in the context of " + title + ". " +
                            "Sustainability is a multifaceted discipline that requires us to understand both the theoretical underpinnings and the practical realities. " +
                            "As we delve into this topic, consider how these concepts might apply to your own environment and community.\n\n" +
                            "The principles we cover here form the bedrock of ecological consciousness. We must critically examine how modern systems interact with natural ecosystems, and what paradigm shifts are required to bring them into harmony.";
                    } else if (p == 2) {
                        content = "Chapter " + m + ", Section 2: Deep Dive.\n\n" +
                            "Building upon the foundations laid in the previous section, we now explore the complex mechanisms that drive " + moduleNames[m-1].toLowerCase() + ". " +
                            "Experts have identified several key indicators of success when implementing " + title + " strategies. These include carbon footprint reduction, resource efficiency, and community engagement metrics.\n\n" +
                            "By analyzing successful case studies, we can extract repeatable patterns. It is crucial to approach these challenges with a systems-thinking mindset, recognizing that an intervention in one area often has cascading effects throughout the entire ecological network.";
                    } else {
                        content = "Chapter " + m + ", Section 3: Synthesis and Next Steps.\n\n" +
                            "In this final section of the chapter, we synthesize our learnings about " + moduleNames[m-1].toLowerCase() + ". " +
                            "The transition from theory to practice is often the most difficult phase. However, by leveraging the frameworks discussed, individuals and organizations can create actionable, step-by-step implementation plans for " + title + ".\n\n" +
                            "As you prepare for the knowledge check, reflect on the core themes. How can you advocate for these changes in your local sphere of influence? The journey towards a sustainable future is paved with millions of small, deliberate actions.";
                    }
                    page.setContent(content);
                    page.setPageNumber(p);
                    pageRepository.save(page);
                }

                Quiz quiz = new Quiz();
                quiz.setModuleId(mod.getId());
                StringBuilder questionsJson = new StringBuilder("[");
                for (int q = 1; q <= 10; q++) {
                    int correctIdx = q % 4;
                    questionsJson.append("{\"question\": \"Question ").append(q).append(" for ").append(title).append("?\", \"options\": [\"Option A\", \"Option B\", \"Option C\", \"Option D\"], \"correct_answer\": ").append(correctIdx).append("}");
                    if (q < 10) questionsJson.append(",");
                }
                questionsJson.append("]");
                quiz.setQuestionsJson(questionsJson.toString());
                quizRepository.save(quiz);
            }
        }
    }
}
