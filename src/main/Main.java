package main;

import java.util.Scanner;

import model.*;

/** Console menu for the University Event Management System. */
public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static EventController controller = new EventController();
    private static User[] users = new User[20];
    private static int userCount = 0;
    private static Guild guild;
    private static GuildPresidentsCouncil council;

    public static void main(String[] args) {
        seedData();
        System.out.println("==== University Event Management System ====");
        User current = null;
        boolean running = true;

        while (running) {
            if (current == null) {
                current = loginScreen();
                if (current == null) {
                    break;                       // exit from login screen
                }
            }
            printMenu(current);
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewEvents(current);
                    break;
                case 2:
                    if (allowed(current, "createEvent")) createEventFlow((EventOrganizer) current);
                    break;
                case 3:
                    if (allowed(current, "registerForEvent")) registerFlow((Student) current);
                    break;
                case 4:
                    if (allowed(current, "cancelRegistration")) cancelRegistrationFlow((Student) current);
                    break;
                case 5:
                    if (allowed(current, "recordAttendance")) attendanceFlow((EventOrganizer) current);
                    break;
                case 6:
                    if (allowed(current, "updateEvent")) cancelEventFlow((EventOrganizer) current);
                    break;
                case 7:
                    if (allowed(current, "recordBudget")) expenseFlow((EventOrganizer) current);
                    break;
                case 8:
                    if (allowed(current, "approveBudget")) approveBudgetFlow((UniversityAdministrator) current);
                    break;
                case 9:
                    if (allowed(current, "generateReport")) reportFlow(current);
                    break;
                case 10:
                    if (allowed(current, "manageAccounts")) createAccountFlow((SystemAdministrator) current);
                    break;
                case 11:
                    showPermissions(current);
                    break;
                case 12:
                    polymorphismDemo();
                    break;
                case 13:
                    System.out.println(guild.summary());
                    System.out.println(council.summary());
                    break;
                case 14:
                    System.out.println("Logged out.\n");
                    current = null;
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    controller.showError("Invalid choice. Please select a number from the menu.");
            }
        }
        System.out.println("Goodbye!");
    }

    // ----------------------------------------------------------- login & menu
    private static User loginScreen() {
        System.out.println("\n--- Login (UC-01) --- (type 'exit' as email to quit)");
        System.out.println("Demo accounts (password 1234 for all):");
        System.out.println("  ali@uni.edu (Student) | ayesha@uni.edu (Student) | sara@uni.edu (Organizer)");
        System.out.println("  bilal@uni.edu (University Admin) | admin@uni.edu (System Admin)");

        for (int attempt = 1; attempt <= 3; attempt++) {
            String email = readLine("Email: ");
            if (email.equalsIgnoreCase("exit")) {
                return null;
            }
            String password = readLine("Password: ");
            for (int i = 0; i < userCount; i++) {
                if (users[i].getEmail().equalsIgnoreCase(email) && users[i].login(password)) {
                    controller.showConfirmation("Welcome, " + users[i].getName() + "!");
                    return users[i];
                }
            }
            controller.showError("Wrong email or password (attempt " + attempt + " of 3).");
        }
        return null;
    }

    private static void printMenu(User user) {
        System.out.println("\n========== MENU (" + user.getName() + " - " + user.getClass().getSimpleName() + ") ==========");
        System.out.println(" 1. View / search events            (UC-02)");
        System.out.println(" 2. Create event                    (UC-06, UC-08)");
        System.out.println(" 3. Register for event              (UC-03, UC-04)");
        System.out.println(" 4. Cancel my registration          (UC-05)");
        System.out.println(" 5. Record attendance               (UC-13)");
        System.out.println(" 6. Cancel an event                 (UC-07)");
        System.out.println(" 7. Record actual expense           (UC-09)");
        System.out.println(" 8. Approve event budget            (UC-09)");
        System.out.println(" 9. Generate report                 (UC-14)");
        System.out.println("10. Create user account             (UC-15)");
        System.out.println("11. Show my permissions");
        System.out.println("12. Polymorphism demo (all users)");
        System.out.println("13. View guild & council            (UC-11, UC-12)");
        System.out.println("14. Logout");
        System.out.println(" 0. Exit");
    }

    private static boolean allowed(User user, String permission) {
        if (user.hasPermission(permission)) {
            return true;
        }
        controller.showError("Access denied: your role (" + user.getClass().getSimpleName()
                + ") does not have the '" + permission + "' permission.");
        return false;
    }

    // ------------------------------------------------------------- use cases
    private static void viewEvents(User user) {
        System.out.println("--- View / Search Events (UC-02) ---");
        String query = readLine("Search by title (press Enter to list all): ");
        Event[] found = controller.searchEvents(user, query);
        if (found.length == 0) {
            System.out.println("No events found.");
            return;
        }
        for (int i = 0; i < found.length; i++) {
            System.out.println(controller.showEventDetails(found[i]));
        }
        System.out.println("Published events: "
                + controller.countByStatus(controller.getEvents(), controller.getEventCount(), "PUBLISHED"));
    }

    private static void createEventFlow(EventOrganizer organizer) {
        System.out.println("--- Create Event (UC-06) ---");
        String title = readLine("Event title: ");
        String schedule = readLine("Date and time (yyyy-mm-dd hh:mm): ");
        if (schedule.length() != 16) {
            controller.showError("Invalid date/time format. Example: 2026-12-20 10:00");
            return;
        }
        int expected = readInt("Expected participants: ");

        Venue[] free = controller.listFreeVenues(schedule, expected);   // UC-08 include
        if (free.length == 0) {
            controller.showError("No venue is free at that time for " + expected
                    + " participants. Change date, time or requirements.");
            return;
        }
        System.out.println("Free venues:");
        for (int i = 0; i < free.length; i++) {
            System.out.println("   " + free[i].getVenueId() + ". " + free[i].getName()
                    + " (capacity " + free[i].getCapacity() + ", " + free[i].getLocation() + ")");
        }
        int venueId = readInt("Select venue id: ");
        Venue selected = controller.findVenueInList(free, venueId);
        if (selected == null) {
            controller.showError("Please select one of the free venues listed above.");
            return;
        }
        double allocated = readDouble("Allocated budget: ");
        double planned = readDouble("Planned expenses: ");

        Event event = controller.createEvent(organizer, title, schedule, selected, allocated, planned);
        if (event == null) {
            controller.showError(controller.getLastError());
            return;
        }
        String publish = readLine("Publish now so students can register? (y/n): ");
        if (publish.equalsIgnoreCase("y")) {
            event.publish();
        }
        controller.showConfirmation("Event created! ID " + event.getEventId() + ", status " + event.getStatus()
                + ", venue " + selected.getName());
    }

    private static void registerFlow(Student student) {
        System.out.println("--- Register for Event (UC-03) ---");
        viewEvents(student);
        int id = readInt("Enter event id to register: ");
        Event event = controller.findEvent(id);
        if (controller.registerStudent(student, event)) {
            controller.showConfirmation("Registration confirmed! Your registration id is "
                    + controller.getLastRegistrationId() + ". Event: " + event.getTitle());
        } else {
            controller.showError(controller.getLastError());
        }
    }

    private static void cancelRegistrationFlow(Student student) {
        System.out.println("--- Cancel Registration (UC-05) ---");
        Registration[] mine = controller.listConfirmedForStudent(student.getUserId());
        if (mine.length == 0) {
            System.out.println("You have no active registrations.");
            return;
        }
        for (int i = 0; i < mine.length; i++) {
            System.out.println("   " + mine[i]);
        }
        int regId = readInt("Enter registration id to cancel: ");
        if (controller.cancelRegistration(student, regId)) {
            controller.showConfirmation("Registration " + regId + " cancelled. Your seat is free again.");
        } else {
            controller.showError(controller.getLastError());
        }
    }

    private static void attendanceFlow(EventOrganizer organizer) {
        System.out.println("--- Record Attendance (UC-13) ---");
        int eventId = readInt("Event id: ");
        Registration[] regs = controller.listConfirmedForEvent(eventId);
        if (regs.length == 0) {
            System.out.println("No confirmed registrations for this event.");
            return;
        }
        for (int i = 0; i < regs.length; i++) {
            String answer = readLine("Is student " + regs[i].getStudentId() + " (reg #"
                    + regs[i].getRegistrationId() + ") present? (y/n): ");
            controller.recordAttendance(organizer, regs[i].getRegistrationId(), answer.equalsIgnoreCase("y"));
        }
        controller.showConfirmation("Attendance saved. Total marked present so far: " + controller.countPresent());
    }

    private static void cancelEventFlow(EventOrganizer organizer) {
        System.out.println("--- Cancel Event (UC-07) ---");
        int id = readInt("Event id to cancel: ");
        String reason = readLine("Reason: ");
        if (controller.cancelEvent(organizer, id, reason)) {
            controller.showConfirmation("Event " + id + " cancelled; venue released; students notified.");
        } else {
            controller.showError(controller.getLastError());
        }
    }

    private static void expenseFlow(EventOrganizer organizer) {
        System.out.println("--- Record Actual Expense (UC-09) ---");
        int id = readInt("Event id: ");
        double amount = readDouble("Expense amount: ");
        if (controller.recordExpense(organizer, id, amount)) {
            controller.showConfirmation("Expense recorded. " + controller.findEvent(id).getBudget());
        } else {
            controller.showError(controller.getLastError());
        }
    }

    private static void approveBudgetFlow(UniversityAdministrator admin) {
        System.out.println("--- Approve Event Budget (UC-09) ---");
        int id = readInt("Event id: ");
        Event event = controller.findEvent(id);
        if (event == null) {
            controller.showError("Event not found.");
        } else if (admin.approveBudget(event.getBudget())) {
            controller.showConfirmation("Budget approved (within limit " + admin.getApprovalLimit() + "): "
                    + event.getBudget());
        } else {
            controller.showError("Budget rejected: outside the approval limit or invalid figures.");
        }
    }

    private static void reportFlow(User user) {
        System.out.println("--- Generate Report (UC-14) ---");
        System.out.println("1. Events   2. Budget vs actual   3. Participation");
        int choice = readInt("Choose report: ");
        String type;
        if (choice == 1) {
            type = "events";
        } else if (choice == 2) {
            type = "budgets";
        } else if (choice == 3) {
            type = "participation";
        } else {
            controller.showError("Invalid report choice.");
            return;
        }
        Report report = controller.generateReport(user, type);
        if (report == null) {
            controller.showError(controller.getLastError());
        } else {
            System.out.println("Report #" + report.getReportId() + " generated at " + report.getGeneratedAt());
            System.out.println(report.getContent());
        }
    }

    private static void createAccountFlow(SystemAdministrator admin) {
        System.out.println("--- Create User Account (UC-15) ---");
        String name = readLine("Name: ");
        String email = readLine("Email: ");
        String password = readLine("Password: ");
        String role = readLine("Role (student/organizer): ").toLowerCase();
        User created = admin.createAccount(100 + userCount, name, email, password, role);
        if (created != null && admin.assignRole(created, role) && userCount < users.length) {
            users[userCount++] = created;
            controller.showConfirmation("Account created: " + created.describe());
        } else {
            controller.showError("Could not create account (invalid role or storage full).");
        }
    }

    private static void showPermissions(User user) {
        String[] perms = user.getPermissions();
        String line = "";
        for (int i = 0; i < perms.length; i++) {
            line += perms[i] + (i < perms.length - 1 ? ", " : "");
        }
        System.out.println(user.describe());
        System.out.println("Permissions: " + line);
    }

    // Runtime polymorphism: one User[] array, same call, different behaviour per object
    private static void polymorphismDemo() {
        System.out.println("--- Polymorphism: user.getPermissions() on a User[] array ---");
        for (int i = 0; i < userCount; i++) {
            User u = users[i];                                   // parent reference
            System.out.println(u.getClass().getSimpleName() + " " + u.getName() + " -> "
                    + u.getPermissions().length + " permissions: " + String.join(", ", u.getPermissions()));
        }
    }

    // ---------------------------------------------------------------- seed data
    private static void seedData() {
        users[userCount++] = new Student(1, "Ali Khan", "ali@uni.edu", "1234", "24P-0001", "BS(CS)", 5);
        users[userCount++] = new Student(2, "Ayesha Noor", "ayesha@uni.edu", "1234", "24P-0002");
        EventOrganizer sara = new EventOrganizer(3, "Sara Ahmed", "sara@uni.edu", "1234", "EMP-101", 1, 1);
        users[userCount++] = sara;
        users[userCount++] = new UniversityAdministrator(4, "Dr. Bilal", "bilal@uni.edu", "1234",
                "EMP-201", "Administration", "Dean", 1, 500000.0, "Dean Office", "Member");
        users[userCount++] = new SystemAdministrator(5, "Admin Hamza", "admin@uni.edu", "1234",
                "EMP-301", "IT", "System Admin", 1, 3, "2026-11-01", "All accounts");

        controller.addVenue(new Venue(1, "Main Auditorium", 300, "Block A"));
        controller.addVenue(new Venue(2, "Seminar Hall", 80, "Block B"));
        controller.addVenue(new Venue(3, "Mini Hall", 2));        // tiny capacity: easy to show "event full"

        Event e1 = controller.createEvent(sara, "Tech Fest 2026", "2026-12-05 10:00",
                controller.findVenueInList(controller.listFreeVenues("2026-12-05 10:00"), 1), 50000, 30000);
        e1.publish();
        Event e2 = controller.createEvent(sara, "Career Fair", "2026-12-10 09:00",
                controller.findVenueInList(controller.listFreeVenues("2026-12-10 09:00"), 3), 20000, 10000);
        e2.publish();

        guild = new Guild(1, "Computing Society", 3);
        guild.addMember((Student) users[0]);
        guild.addActivity("Tech Fest 2026");
        council = new GuildPresidentsCouncil(1, "Fall 2026", 3);
        council.addPresident(sara);
        council.recordMeeting("2026-11-15");
        council.recordDecision("Approve Tech Fest budget");
    }

    // ------------------------------------------------------------ input helpers
    private static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.print("Please enter a whole number: ");
        }
        int value = sc.nextInt();
        sc.nextLine();
        return value;
    }

    private static double readDouble(String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextDouble()) {
            sc.next();
            System.out.print("Please enter a number: ");
        }
        double value = sc.nextDouble();
        sc.nextLine();
        return value;
    }
}
