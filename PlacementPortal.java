import java.util.*;

public class PlacementPortal {
    static Scanner sc = new Scanner(System.in);

    // ---- Data Models (small) ----
    static class Student {
        int id; String name, branch; double cgpa; String skills;
        Student(int id, String name, String branch, double cgpa, String skills){
            this.id=id; this.name=name; this.branch=branch; this.cgpa=cgpa; this.skills=skills.toUpperCase();
        }
    }
    static class Company {
        int id; String name;
        Company(int id, String name){ this.id=id; this.name=name; }
    }
    static class Job {
        int id, companyId; String role, branchAllowed, reqSkills; double minCgpa; boolean approved;
        Job(int id,int companyId,String role,double minCgpa,String branchAllowed,String reqSkills){
            this.id=id; this.companyId=companyId; this.role=role;
            this.minCgpa=minCgpa; this.branchAllowed=branchAllowed.toUpperCase();
            this.reqSkills=reqSkills.toUpperCase(); this.approved=false;
        }
    }
    static class Application {
        int id, studentId, jobId; String status;
        Application(int id,int studentId,int jobId){ this.id=id; this.studentId=studentId; this.jobId=jobId; this.status="APPLIED"; }
    }

    // ---- Storage ----
    static ArrayList<Student> students = new ArrayList<>();
    static ArrayList<Company> companies = new ArrayList<>();
    static ArrayList<Job> jobs = new ArrayList<>();
    static ArrayList<Application> apps = new ArrayList<>();

    static int studentSeq=1000, companySeq=2000, jobSeq=3000, appSeq=4000;

    // ---- Helpers ----
    static Student findStudent(int id){ for(Student s:students) if(s.id==id) return s; return null; }
    static Company findCompany(int id){ for(Company c:companies) if(c.id==id) return c; return null; }
    static Job findJob(int id){ for(Job j:jobs) if(j.id==id) return j; return null; }

    static boolean alreadyApplied(int studentId,int jobId){
        for(Application a:apps) if(a.studentId==studentId && a.jobId==jobId) return true;
        return false;
    }

    static boolean eligible(Student s, Job j){
        boolean cgpaOk = s.cgpa >= j.minCgpa;
        boolean branchOk = j.branchAllowed.equals("ALL") || s.branch.equalsIgnoreCase(j.branchAllowed);
        boolean skillOk = j.reqSkills.equals("ANY") || s.skills.contains(j.reqSkills); // simple matching
        return cgpaOk && branchOk && skillOk;
    }

    // ---- Menus ----
    public static void main(String[] args) {
        seed(); // demo job so it doesn't look empty

        while(true){
            System.out.println("\n=== PLACEMENT PORTAL ===");
            System.out.println("1) Student  2) Company  3) Admin  0) Exit");
            int ch = readInt("Choose: ");
            if(ch==0) break;
            if(ch==1) studentMenu();
            else if(ch==2) companyMenu();
            else if(ch==3) adminMenu();
            else System.out.println("Invalid!");
        }
        System.out.println("Bye!");
    }

    static void studentMenu(){
        System.out.println("\n--- Student ---");
        System.out.println("1) Register  2) Login");
        int ch = readInt("Choose: ");
        if(ch==1){
            String name = readStr("Name: ");
            String branch = readStr("Branch (CSE/IT/ECE): ").toUpperCase();
            double cgpa = readDouble("CGPA: ");
            String skills = readStr("Skills (e.g., JAVA,SQL): ");
            Student s = new Student(++studentSeq, name, branch, cgpa, skills);
            students.add(s);
            System.out.println("Registered! Student ID: " + s.id);
        } else if(ch==2){
            int id = readInt("Student ID: ");
            Student s = findStudent(id);
            if(s==null){ System.out.println("Not found!"); return; }
            studentDash(s);
        }
    }

    static void studentDash(Student s){
        while(true){
            System.out.println("\n--- Student Dashboard ("+s.name+") ---");
            System.out.println("1) View Jobs  2) Apply  3) My Applications  0) Logout");
            int ch = readInt("Choose: ");
            if(ch==0) return;

            if(ch==1){
                System.out.println("\nApproved Jobs:");
                boolean any=false;
                for(Job j:jobs){
                    if(!j.approved) continue;
                    any=true;
                    System.out.println("JobID:"+j.id+" Role:"+j.role+" MinCGPA:"+j.minCgpa+
                            " Branch:"+j.branchAllowed+" Skill:"+j.reqSkills);
                }
                if(!any) System.out.println("No approved jobs yet.");
            }
            else if(ch==2){
                int jobId = readInt("Enter JobID: ");
                Job j = findJob(jobId);
                if(j==null || !j.approved){ System.out.println("Invalid job / not approved."); continue; }
                if(alreadyApplied(s.id, jobId)){ System.out.println("Already applied!"); continue; }

                Application a = new Application(++appSeq, s.id, jobId);
                apps.add(a);
                System.out.println("Applied! ApplicationID: " + a.id);
            }
            else if(ch==3){
                System.out.println("\nMy Applications:");
                boolean any=false;
                for(Application a:apps){
                    if(a.studentId!=s.id) continue;
                    any=true;
                    Job j = findJob(a.jobId);
                    System.out.println("AppID:"+a.id+" Job:"+(j==null?"N/A":j.role)+" Status:"+a.status);
                }
                if(!any) System.out.println("No applications yet.");
            }
        }
    }

    static void companyMenu(){
        System.out.println("\n--- Company ---");
        System.out.println("1) Register  2) Login");
        int ch = readInt("Choose: ");
        if(ch==1){
            String name = readStr("Company Name: ");
            Company c = new Company(++companySeq, name);
            companies.add(c);
            System.out.println("Registered! Company ID: " + c.id);
        } else if(ch==2){
            int id = readInt("Company ID: ");
            Company c = findCompany(id);
            if(c==null){ System.out.println("Not found!"); return; }
            companyDash(c);
        }
    }

    static void companyDash(Company c){
        while(true){
            System.out.println("\n--- Company Dashboard ("+c.name+") ---");
            System.out.println("1) Post Job  2) My Jobs  0) Logout");
            int ch = readInt("Choose: ");
            if(ch==0) return;

            if(ch==1){
                String role = readStr("Role: ");
                double minCgpa = readDouble("Min CGPA: ");
                String branch = readStr("Allowed Branch (CSE/IT/ECE or ALL): ").toUpperCase();
                String skill = readStr("Required Skill (e.g., JAVA or ANY): ").toUpperCase();
                Job j = new Job(++jobSeq, c.id, role, minCgpa, branch, skill);
                jobs.add(j);
                System.out.println("Job Posted! JobID:"+j.id+" (Pending Admin Approval)");
            } else if(ch==2){
                System.out.println("\nMy Jobs:");
                boolean any=false;
                for(Job j:jobs){
                    if(j.companyId!=c.id) continue;
                    any=true;
                    System.out.println("JobID:"+j.id+" Role:"+j.role+" Approved:"+ (j.approved?"YES":"NO"));
                }
                if(!any) System.out.println("No jobs yet.");
            }
        }
    }

    static void adminMenu(){
        String u = readStr("Admin user: ");
        String p = readStr("Admin pass: ");
        if(!(u.equals("admin") && p.equals("admin123"))){
            System.out.println("Wrong admin credentials!");
            return;
        }

        while(true){
            System.out.println("\n--- Admin Dashboard ---");
            System.out.println("1) Pending Jobs  2) Approve Job  3) Auto Shortlist  0) Logout");
            int ch = readInt("Choose: ");
            if(ch==0) return;

            if(ch==1){
                System.out.println("\nPending Jobs:");
                boolean any=false;
                for(Job j:jobs){
                    if(j.approved) continue;
                    any=true;
                    System.out.println("JobID:"+j.id+" Role:"+j.role+" CompanyID:"+j.companyId+
                            " MinCGPA:"+j.minCgpa+" Branch:"+j.branchAllowed+" Skill:"+j.reqSkills);
                }
                if(!any) System.out.println("No pending jobs.");
            }
            else if(ch==2){
                int jobId = readInt("JobID to approve: ");
                Job j = findJob(jobId);
                if(j==null){ System.out.println("Invalid JobID"); continue; }
                j.approved = true;
                System.out.println("Approved!");
            }
            else if(ch==3){
                int jobId = readInt("JobID for shortlist: ");
                Job j = findJob(jobId);
                if(j==null){ System.out.println("Invalid JobID"); continue; }

                int count=0;
                for(Application a:apps){
                    if(a.jobId!=jobId) continue;
                    if(!a.status.equals("APPLIED")) continue;
                    Student s = findStudent(a.studentId);
                    if(s!=null && eligible(s,j)){
                        a.status="SHORTLISTED";
                        count++;
                    }
                }
                System.out.println("Shortlisted: " + count);
            }
        }
    }

    // ---- IO helpers ----
    static int readInt(String msg){
        while(true){
            System.out.print(msg);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch(Exception e){ System.out.println("Enter a valid number!"); }
        }
    }
    static double readDouble(String msg){
        while(true){
            System.out.print(msg);
            try { return Double.parseDouble(sc.nextLine().trim()); }
            catch(Exception e){ System.out.println("Enter a valid decimal!"); }
        }
    }
    static String readStr(String msg){
        while(true){
            System.out.print(msg);
            String s = sc.nextLine().trim();
            if(!s.isEmpty()) return s;
            System.out.println("Cannot be empty!");
        }
    }

    static void seed(){
        Company c = new Company(++companySeq, "DemoTech");
        companies.add(c);
        Job j = new Job(++jobSeq, c.id, "Java Developer", 6.5, "CSE", "JAVA");
        j.approved = true;
        jobs.add(j);
    }
}
