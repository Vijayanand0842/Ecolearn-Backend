package com.ecolearn.backend;

import com.ecolearn.backend.model.*;
import com.ecolearn.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private ModuleRepository moduleRepository;
    @Autowired private PageRepository pageRepository;
    @Autowired private QuizRepository quizRepository;
    @Autowired private ProjectRepository projectRepository;

    private final Random random = new Random();

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
        System.out.println("DataSeeder completed. Ensured Users, Projects, Modules, and Quizzes exist with deep sample data.");
    }

    private void seedUsers() {
        // Admin
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setRole("admin");
        admin.setVerified(1);
        admin.setPoints(0);
        userRepository.save(admin);

        // Sample Students
        String[] students = {"vijay", "eco_warrior", "green_tech", "sustain_expert"};
        for (String name : students) {
            User u = new User();
            u.setUsername(name);
            u.setPassword("pass123");
            u.setRole("user");
            u.setVerified(name.equals("vijay") ? 1 : 0);
            u.setPoints(random.nextInt(500));
            userRepository.save(u);
        }
    }

    private void seedProjects() {
        String[] categories = {"Energy", "Waste", "Water", "Food", "Community", "Transport"};
        String[] difficulties = {"Easy", "Medium", "Hard"};
        String[] actionVerbs = {"Install", "Setup", "Audit", "Organize", "Implement", "Design"};
        String[] targets = {"Solar Panels", "Compost Bin", "Rainwater Tank", "Local Garden", "Bike Path", "LED Lighting"};

        for (int i = 1; i <= 50; i++) {
            Project project = new Project();
            String cat = categories[i % categories.length];
            String diff = difficulties[i % 3];
            project.setTitle(cat + " - " + actionVerbs[i % actionVerbs.length] + " " + targets[i % targets.length] + " (" + i + ")");
            project.setDescription("This project focuses on " + cat.toLowerCase() + " sustainability. By completing this " + diff.toLowerCase() + " task, you will reduce local carbon emissions and inspire others in your community. " +
                "Requirements: Take a photo of the completed installation and provide a brief write-up of the benefits observed.");
            project.setDifficulty(diff);
            projectRepository.save(project);
        }
    }

    private void seedLessons() {
        String[][] lessonThemes = {
            {"Energy", "Solar Power Fundamentals", "Learn how to harness the sun's power for a cleaner future."},
            {"Energy", "Wind Turbines & Microgrids", "Deep dive into kinetic energy and localized power distribution."},
            {"Waste", "Zero Waste Living Handbook", "Practical steps to eliminate trash from your daily life."},
            {"Waste", "Advanced Composting Techniques", "Master the science of decomposition and soil enrichment."},
            {"Water", "Water Conservation & Harvesting", "Save every drop with greywater recycling and rain capture."},
            {"Food", "Urban Permaculture 101", "Turn tiny balconies into productive food forests."},
            {"Food", "Plant-Based Diets for Climate", "The impact of nutrition on global ecological health."},
            {"Lifestyle", "Sustainable Fashion Ethics", "Understanding the true cost of fast fashion and circularity."},
            {"Lifestyle", "Minimalism & Eco-Footprint", "Less is more: reducing consumption for a healthier planet."},
            {"Policy", "Climate Activism & Policy", "How to influence local government and drive systemic change."},
            {"Tech", "Smart Home Automation", "Using IoT to optimize energy use in modern households."},
            {"Nature", "Biodiversity Restoration", "Repairing local ecosystems and protecting native species."}
        };

        for (int l = 0; l < lessonThemes.length; l++) {
            String category = lessonThemes[l][0];
            String title = lessonThemes[l][1];
            String desc = lessonThemes[l][2];

            Lesson lesson = new Lesson();
            lesson.setTitle(title);
            lesson.setCategory(category);
            lesson.setDescription(desc);
            lesson = lessonRepository.save(lesson);

            String[] moduleNames = {
                "Historical Foundations",
                "Scientific Principles",
                "Global Case Studies",
                "Practical Workshop",
                "Economic Feasibility",
                "Social Equity & Justice",
                "Implementation Roadmap",
                "Future Outlook"
            };

            for (int m = 1; m <= moduleNames.length; m++) {
                ModuleEntity mod = new ModuleEntity();
                mod.setLessonId(lesson.getId());
                mod.setTitle("Module " + m + ": " + moduleNames[m-1]);
                mod.setOrderIndex(m);
                mod = moduleRepository.save(mod);

                for (int p = 1; p <= 3; p++) {
                    Page page = new Page();
                    page.setModuleId(mod.getId());
                    String content = "### " + moduleNames[m-1] + " - Part " + p + "\n\n" +
                        "In this section of our " + title + " course, we examine the critical role of " + moduleNames[m-1].toLowerCase() + ".\n\n" +
                        "**Key Takeaways:**\n" +
                        "1. Understanding the system dynamics of " + category + ".\n" +
                        "2. Identifying leverage points for sustainable intervention.\n" +
                        "3. Measuring the impact of individual and collective action.\n\n" +
                        "As we move forward, consider the data points presented in the accompanying charts. The correlation between resource management and ecological stability is undeniable. We must act with urgency and precision.";
                    page.setContent(content);
                    page.setPageNumber(p);
                    pageRepository.save(page);
                }

                Quiz quiz = new Quiz();
                quiz.setModuleId(mod.getId());
                StringBuilder questionsJson = new StringBuilder("[");
                for (int q = 1; q <= 10; q++) {
                    int correctIdx = q % 4;
                    String qText = "Regarding " + moduleNames[m-1] + ", which statement best describes the " + category + " impact of factor " + q + "?";
                    questionsJson.append("{\"question\": \"").append(qText).append("\", \"options\": [\"Significant improvement\", \"Marginal impact\", \"Systemic risk factor\", \"Neutral outcome\"], \"correct_answer\": ").append(correctIdx).append("}");
                    if (q < 10) questionsJson.append(",");
                }
                questionsJson.append("]");
                quiz.setQuestionsJson(questionsJson.toString());
                quizRepository.save(quiz);
            }
        }
    }
}
