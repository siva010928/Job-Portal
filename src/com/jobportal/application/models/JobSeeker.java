package com.jobportal.application.models;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.jobportal.application.App;

public class JobSeeker extends User{
    private Integer id;
    private ArrayList<String> keySkills,languages;
    private ArrayList<Employment> employments;
    private ArrayList<Education> educations;
    private ArrayList<Project> projects;
    private String accompolishments;


    //for seeing this profile by job provier purpose they should not see job_seeker's password,location
    //for this we want to store id to retrieve educations,projects,etc
    //these information enough for job provider
    public JobSeeker(Integer job_seeker_id,String firstName, String lastName,String email) {
        super(firstName, lastName,email);
        this.id=job_seeker_id;
    }

    //only for login
    public JobSeeker(String firstName, String lastName, String gender,Date DOB,String email,String location,String phone,UserType userType, ArrayList<String> keySkills, ArrayList<String> languages, ArrayList<Employment> employments, ArrayList<Education> educations, ArrayList<Project> projects, String accompolishments) {
        super(firstName, lastName, gender,DOB,email, location,phone,userType);
        this.keySkills = keySkills;
        this.languages = languages;
        this.employments = employments;
        this.educations = educations;
        this.projects = projects;
        this.accompolishments = accompolishments;
        //for sake
        this.id=App.id;
    }
    

    //this method is used for the scenario [job provider comes get list of applicants of job but he not check all job_seeker_extra profiles 
    //but check maybe one or two in that case 
    //we only load this extra profile if he select to view full details of particular applicant
    //otherwise it won't load unnecessary
    //view candidate full profile their education etc from applications page
    //during login also this method invoke once
    @Override
    public void generateProfile()throws SQLException{
        PreparedStatement stmt=App.conn.prepareStatement("SELECT accomplishments FROM job_seekers WHERE job_seeker_id=?");
        stmt.setInt(1,this.getId());
        ResultSet rS1=stmt.executeQuery();
        rS1.next();
        String accomplishments=rS1.getString("accomplishments");
        //getting Projects
        stmt=App.conn.prepareStatement("SELECT * FROM projects WHERE job_seeker_id=?");
        stmt.setInt(1, this.getId());
        ResultSet rProjects=stmt.executeQuery();

        ArrayList<Project> projects=new ArrayList<>();

        while(rProjects.next()){
            projects.add(new Project(rProjects.getInt("project_id"),rProjects.getString("title"), rProjects.getString("client"), rProjects.getString("status"), rProjects.getString("link"), rProjects.getString("details"), rProjects.getDate("start_date"), rProjects.getDate("end_date")==null?App.nullDate:rProjects.getDate("end_date")));
        }

        //getting educations
        stmt=App.conn.prepareStatement("SELECT * FROM educations WHERE job_seeker_id=?");
        stmt.setInt(1, this.id);
        ResultSet rEducations=stmt.executeQuery();

        ArrayList<Education> educations=new ArrayList<>();

        while(rEducations.next()){
            educations.add(new Education(rEducations.getInt("education_id"),rEducations.getString("education_level"), rEducations.getString("course"), rEducations.getString("institution"), rEducations.getString("course_type"), rEducations.getInt("passout"),rEducations.getFloat("grade")));
        }

        //getting employments
        stmt=App.conn.prepareStatement("SELECT * FROM employments WHERE job_seeker_id=?");
        stmt.setInt(1, this.id);
        ResultSet rEmployments=stmt.executeQuery();

        ArrayList<Employment> employments=new ArrayList<>();

        while(rEmployments.next()){
            employments.add(new Employment(rEmployments.getInt("employment_id"),rEmployments.getString("organization"), rEmployments.getString("designation"), rEmployments.getDate("start_date"), rEmployments.getDate("end_date")==null?App.nullDate:rEmployments.getDate("end_date")));
        }
        

        //getting key_skills
        stmt=App.conn.prepareStatement("SELECT * FROM seeker_skills JOIN key_skills USING(key_skill_id) WHERE job_seeker_id=?");
        stmt.setInt(1, this.id);
        ResultSet rSkills=stmt.executeQuery();

        ArrayList<String> keySkills=new ArrayList<>();

        while(rSkills.next()){
            keySkills.add(rSkills.getString("name"));
        }

        //getting languages
        stmt=App.conn.prepareStatement("SELECT * FROM seeker_languages JOIN languages USING(language_id) WHERE job_seeker_id=?");
        stmt.setInt(1, this.id);
        ResultSet rLanguages=stmt.executeQuery();

        ArrayList<String> languages=new ArrayList<>();

        while(rLanguages.next()){
            languages.add(rLanguages.getString("name"));
        }

        //setting all
        this.setAccompolishments(accomplishments);
        this.setProjects(projects);
        this.setEducations(educations);
        this.setEmployments(employments);
        this.setKeySkills(keySkills);
        this.setLanguages(languages);
    }

    public ArrayList<Company> getCompaniesFeed(HashMap<String,String> searchFilter) throws SQLException{
        StringBuilder query=new StringBuilder("SELECT * FROM companies JOIN pays ON revenue_id=pay_id WHERE 1=1");
        
        //filtering for where class of job title,location
        if(!searchFilter.isEmpty()){
            for (Map.Entry<String,String> m : searchFilter.entrySet()) {
                // System.out.println("search Filter....");
                query.append(" AND ");
                query.append(m.getKey());//field name
                query.append(" LIKE ");
                query.append("'%"+m.getValue()+"%' ");//field value
            }
        }

        query.append(" LIMIT 10");

        PreparedStatement stmt=App.conn.prepareStatement(query.toString());
        // System.err.println(query.toString());
        ArrayList<Company> companies=new ArrayList<>();
        ResultSet rS=stmt.executeQuery();
        while(rS.next()){
            
            int company_id=rS.getInt("company_id");
            
            //getting company details of the job posted by this job provider
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS reviews,AVG(ratings) AS ratings FROM reviews WHERE company_id=?");
            stmt.setInt(1, company_id);
            ResultSet rCompany=stmt.executeQuery();
            rCompany.next();
            companies.add(new Company(company_id,rCompany.getInt("reviews"), rCompany.getInt("ratings"), rS.getInt("founded"), rS.getInt("size"), rS.getString("name"), rS.getString("logo"), rS.getString("sector"), rS.getString("industry"), rS.getString("location"), new Pay(rS.getInt("pay_id"),rS.getBigDecimal("from"),rS.getBigDecimal("to"), rS.getString("pay_type"))));
                
        }
        System.err.println("Companies Found: "+companies.size());
        return companies;
    }
    
    //Jobs Feed  job seeker optional Filter
    @Override
    public ArrayList<Job> getJobsFeed(HashMap<String,String> searchFilter,HashMap<String,Integer> sortFilter,Integer daysFilter,Integer salaryFilter) throws SQLException{
        StringBuilder query=new StringBuilder("SELECT * FROM jobs JOIN pays USING(pay_id) JOIN companies USING(company_id) WHERE job_status='OPEN'");
        
        //default sorted by posted date desc and job title asc descending order
        if(sortFilter.isEmpty()){
            sortFilter.put("jobs.postedAt", -1);
            sortFilter.put("jobs.title", 1);
        }

        //filtering by salary
        if(salaryFilter!=-1){
            //(pays.from >= 17000 OR pays.to >= 17000)
            query.append(" AND ");
            query.append("(pays.from >= "+salaryFilter +" OR pays.to >="+salaryFilter+ ") ");
        }

        
        //filterring last 7 days like
        if(daysFilter!=-1){
            query.append(" AND ");
            query.append("jobs.postedAt>(DATE_SUB(CURRENT_DATE,INTERVAL "+daysFilter+" DAY))");
        }

        //filtering for where class of job title,location
        if(!searchFilter.isEmpty()){
            for (Map.Entry<String,String> m : searchFilter.entrySet()) {
                // System.out.println("search Filter....");
                query.append(" AND ");
                query.append(m.getKey());//field name
                query.append(" LIKE ");
                query.append("'%"+m.getValue()+"%' ");//field value
            }
        }

        //filtering for order by class of postedAt,title(sort) it will be always the size of one
        if(!sortFilter.isEmpty()){
            // System.out.println("sort Filter....");
            query.append(" ORDER BY ");
            for(Map.Entry<String,Integer> m:sortFilter.entrySet()){
                query.append(m.getKey());
                query.append(m.getValue()==1?" ASC ":" DESC ");
                query.append(",");
            }
            query.deleteCharAt(query.length()-1);   
        }
        query.append("LIMIT 10");

        PreparedStatement stmt=App.conn.prepareStatement(query.toString());
        // System.err.println(query.toString());
        ArrayList<Job> jobs=new ArrayList<>();
        ResultSet rS=stmt.executeQuery();
        while(rS.next()){
            
            Integer job_id=rS.getInt("job_id");
            int company_id=rS.getInt("company_id");
            int revenue_id=rS.getInt("revenue_id");
            
            //getting pay of the job posted by this job provider
            Pay salaryPay=new Pay(rS.getInt("pay_id"),rS.getBigDecimal("from"),rS.getBigDecimal("to"), rS.getString("pay_type"));

            
            //getting company details of the job posted by this job provider
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS reviews,AVG(ratings) AS ratings FROM reviews WHERE company_id=?");
            stmt.setInt(1, company_id);
            ResultSet rCompany=stmt.executeQuery();

            stmt=App.conn.prepareStatement("SELECT * FROM pays WHERE pay_id=?");
            stmt.setInt(1, revenue_id);
            ResultSet rCompanyRevenue=stmt.executeQuery();
            Pay revenue=null;
            while(rCompanyRevenue.next())
                revenue=new Pay(rCompanyRevenue.getInt("pay_id"),rCompanyRevenue.getBigDecimal("from"),rCompanyRevenue.getBigDecimal("to"), rCompanyRevenue.getString("pay_type"));
            Company company=null;
            while(rCompany.next())    
                company=new Company(company_id,rCompany.getInt("reviews"), rCompany.getInt("ratings"), rS.getInt("founded"), rS.getInt("size"), rS.getString("name"), rS.getString("logo"), rS.getString("sector"), rS.getString("industry"), rS.getString("location"), revenue);
    
            //getting job_types
            stmt=App.conn.prepareStatement("SELECT * FROM job_job_types JOIN job_types USING(job_type_id) WHERE job_id=?");
            stmt.setInt(1,job_id);
            ResultSet rJobtypes=stmt.executeQuery();

            ArrayList<String> job_types=new ArrayList<>();
            while(rJobtypes.next()){
                job_types.add(rJobtypes.getString("name"));
            }

            //getting job_schedules
            stmt=App.conn.prepareStatement("SELECT * FROM job_job_schedules JOIN job_schedules USING(job_schedule_id) WHERE job_id=?");
            stmt.setInt(1,job_id);
            ResultSet rJobschedules=stmt.executeQuery();

            ArrayList<String> job_schedules=new ArrayList<>();

            while(rJobschedules.next()){
                job_schedules.add(rJobschedules.getString("name"));
            }
            // //getting questions id for a particular job
            // stmt=App.conn.prepareStatement("SELECT * FROM questions WHERE job_id=?");
            // stmt.setInt(1,job_id);
            // ResultSet rQuestions=stmt.executeQuery();

            // ArrayList<Integer> questions=new ArrayList<>();

            // while(rQuestions.next()){
            //     questions.add(rQuestions.getInt("question_id"));
            // }
            jobs.add(new Job(job_id,rS.getInt("openings"),rS.getString("title"), rS.getString("description"), rS.getString("location_type"), rS.getString("location"), rS.getString("fullOrPartTime"), rS.getString("job_status"), rS.getString("candidate_profile"), rS.getString("education_level"), salaryPay, rS.getTimestamp("postedAt"), job_types, job_schedules,company));
        }
        return jobs;
    }

    //update their additional profile
    @Override
    public void updateProfile() throws SQLException {
        
    }

    public ArrayList<Application> getMyApplications(HashMap<String,String> searchFilter,HashMap<String,Integer> sortFilter,Integer daysFilter) throws SQLException {
        StringBuilder query=new StringBuilder("SELECT * FROM applications JOIN jobs USING(job_id) JOIN pays USING(pay_id)  JOIN companies USING(company_id) WHERE job_seeker_id=?");
        
        
        //for default showing date will be in descending order(modified first)
        if(sortFilter.isEmpty())
            sortFilter.put("appliedAt", -1);

        // filtering like last 7 days
        if(daysFilter!=-1){
            query.append(" AND ");
            query.append("appliedAt>(DATE_SUB(CURRENT_DATE,INTERVAL "+daysFilter+" DAY))");
        }

        // filter applications based on its status
        for (Map.Entry<String,String> m : searchFilter.entrySet()) {
            query.append(" AND ");
            query.append(m.getKey());//field name
            query.append(" LIKE ");
            query.append("'%"+m.getValue()+"%'");//field value
        }

        //filtering for order by ratings it will be always the size of one
        if(!sortFilter.isEmpty()){
            query.append(" ORDER BY ");
            for(Map.Entry<String,Integer> m:sortFilter.entrySet()){
                query.append(m.getKey());
                query.append(m.getValue()==1?" ASC":" DESC");
                query.append(",");
            }
            query.deleteCharAt(query.length()-1);   
        }

        PreparedStatement stmt=App.conn.prepareStatement(query.toString());
        stmt.setInt(1, App.id);
        // System.err.println("\n"+stmt.toString()+"\n");
        ResultSet rS=stmt.executeQuery();
        ArrayList<Application> applications = new ArrayList<>();
        while(rS.next()){
            // Company company = new Company(rS.getInt("company_id"),null, null, rS.getInt("founded"), rS.getInt("size"), rS.getString("name"), rS.getString("logo"), rS.getString("sector"), rS.getString("industry"), rS.getString("location"), null);


            Integer job_id=rS.getInt("job_id");
            int company_id=rS.getInt("company_id");
            int revenue_id=rS.getInt("revenue_id");
            
            //getting pay of the job posted by this job provider
            Pay salaryPay=new Pay(rS.getInt("pay_id"),rS.getBigDecimal("from"),rS.getBigDecimal("to"), rS.getString("pay_type"));

            
            //getting company details of the job posted by this job provider
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS reviews,AVG(ratings) AS ratings FROM reviews WHERE company_id=?");
            stmt.setInt(1, company_id);
            ResultSet rCompany=stmt.executeQuery();

            stmt=App.conn.prepareStatement("SELECT * FROM pays WHERE pay_id=?");
            stmt.setInt(1, revenue_id);
            ResultSet rCompanyRevenue=stmt.executeQuery();
            Pay revenue=null;
            while(rCompanyRevenue.next())
                revenue=new Pay(rCompanyRevenue.getInt("pay_id"),rCompanyRevenue.getBigDecimal("from"),rCompanyRevenue.getBigDecimal("to"), rCompanyRevenue.getString("pay_type"));
            Company company=null;
            while(rCompany.next())    
                company=new Company(company_id,rCompany.getInt("reviews"), rCompany.getInt("ratings"), rS.getInt("founded"), rS.getInt("size"), rS.getString("name"), rS.getString("logo"), rS.getString("sector"), rS.getString("industry"), rS.getString("location"), revenue);
    
            //getting job_types
            stmt=App.conn.prepareStatement("SELECT * FROM job_job_types JOIN job_types USING(job_type_id) WHERE job_id=?");
            stmt.setInt(1,job_id);
            ResultSet rJobtypes=stmt.executeQuery();

            ArrayList<String> job_types=new ArrayList<>();
            while(rJobtypes.next()){
                job_types.add(rJobtypes.getString("name"));
            }

            //getting job_schedules
            stmt=App.conn.prepareStatement("SELECT * FROM job_job_schedules JOIN job_schedules USING(job_schedule_id) WHERE job_id=?");
            stmt.setInt(1,job_id);
            ResultSet rJobschedules=stmt.executeQuery();

            ArrayList<String> job_schedules=new ArrayList<>();

            while(rJobschedules.next()){
                job_schedules.add(rJobschedules.getString("name"));
            }

            Job job = new Job(job_id,rS.getInt("openings"),rS.getString("title"), rS.getString("description"), rS.getString("location_type"), rS.getString("location"), rS.getString("fullOrPartTime"), rS.getString("job_status"), rS.getString("candidate_profile"), rS.getString("education_level"), salaryPay, rS.getTimestamp("postedAt"), job_types, job_schedules,company);
            applications.add(new Application(rS.getInt("application_id"),this,job,rS.getString("resume"), rS.getString("status"), rS.getTimestamp("appliedAt")));
        }
        return applications;

    }

    public void addEducation(Education education) throws SQLException{
        PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO educations VALUES(DEFAULT,?,?,?,?,?,?,?)");
        stmt.setString(1, education.getEducation());
        stmt.setString(2,education.getCourse());
        stmt.setString(3, education.getInstitute());
        stmt.setString(4, education.getCourseType());
        stmt.setInt(5, education.getPassout());
        stmt.setFloat(6, education.getGrade());
        stmt.setInt(7, App.id);

        int writtenResults=stmt.executeUpdate();
        education.setId(App.getLastInsertId());
        this.educations.add(education);
    }

    public void addEmployment(Employment employment) throws SQLException{
        PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO employments VALUES(DEFAULT,?,?,?,?,?)");
        stmt.setString(1, employment.getOrganization());
        stmt.setString(2,employment.getDesignation());
        stmt.setDate(3, employment.getStart());
        if(employment.getEnd().equals(App.nullDate)){
            stmt.setNull(4, java.sql.Types.DATE);//updating null to education db
        }else{
            stmt.setDate(4, employment.getEnd());
        }
        stmt.setInt(5, App.id);

        int writtenResults=stmt.executeUpdate();
        employment.setId(App.getLastInsertId());
        this.employments.add(employment);
    }

    public void addProject(Project project) throws SQLException{
        PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO projects VALUES(DEFAULT,?,?,?,?,?,?,?,?)");
        stmt.setString(1, project.getTitle());
        stmt.setString(2,project.getStatus());
        stmt.setString(3, project.getClient());
        stmt.setDate(4, project.getStart());
        if(project.getEnd().equals(App.nullDate)){
            stmt.setNull(5, java.sql.Types.DATE);//updating null to education db
        }else{
            stmt.setDate(5, project.getEnd());
        }
        stmt.setString(6, project.getLink());
        stmt.setString(7, project.getDetail());
        stmt.setInt(8, App.id);

        int writtenResults=stmt.executeUpdate();
        project.setId(App.getLastInsertId());
        this.projects.add(project);
    }



    public ArrayList<ArrayList<String>> searchLanguages(String search) throws SQLException{
        ArrayList<ArrayList<String>> searchedResults=new ArrayList<>();

        PreparedStatement stmt=App.conn.prepareStatement("SELECT *  FROM languages WHERE name LIKE ? ORDER BY name");
        stmt.setString(1, "%"+search+"%");
        // System.err.println(stmt.toString());
        ResultSet Rsearches=stmt.executeQuery();
        while(Rsearches.next()){
            searchedResults.add(new ArrayList<>(Arrays.asList(Rsearches.getInt("language_id")+"",Rsearches.getString("name"))));
        }
        System.err.println("searched  languages: "+searchedResults.size());
        return searchedResults;
    }

    public void addLanguage(Integer language_id,String language) throws SQLException{
        PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO seeker_languages VALUES(?,?)");
        stmt.setInt(1, language_id);
        stmt.setInt(2, App.id);
        int writtenResults=stmt.executeUpdate();
        this.languages.add(language);
        System.err.println("Language added: "+writtenResults);
    }

    public void removeLanguage(Integer language_id,String language) throws SQLException{
        PreparedStatement stmt=App.conn.prepareStatement("DELETE FROM seeker_languages WHERE language_id=? AND job_seeker_id=?");
        stmt.setInt(1, language_id);
        stmt.setInt(2, App.id);
        int deleteResults=stmt.executeUpdate();
        this.languages.remove(language);
        System.err.println("Language removed: "+deleteResults);
    }

    public ArrayList<ArrayList<String>> searchSkills(String search) throws SQLException{
        ArrayList<ArrayList<String>> searchedResults=new ArrayList<>();

        PreparedStatement stmt=App.conn.prepareStatement("SELECT *  FROM key_skills WHERE name LIKE ? ORDER BY name LIMIT 25");
        stmt.setString(1, "%"+search+"%");
        ResultSet Rsearches=stmt.executeQuery();
        while(Rsearches.next()){
            searchedResults.add(new ArrayList<>(Arrays.asList(Rsearches.getInt("key_skill_id")+"",Rsearches.getString("name"))));
        }
        return searchedResults;
    }

    public void addSkill(Integer keySkillId,String keySkill) throws SQLException{
        PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO seeker_skills VALUES(?,?)");
        stmt.setInt(1, keySkillId);
        stmt.setInt(2, App.id);
        int writtenResults=stmt.executeUpdate();
        this.keySkills.add(keySkill);
        System.err.println("keyskill added: "+writtenResults);
    }

    public void removeSkill(Integer keySkillId,String keySkill) throws SQLException{
        PreparedStatement stmt=App.conn.prepareStatement("DELETE FROM seeker_skills WHERE key_skill_id=? AND job_seeker_id=?");
        stmt.setInt(1, keySkillId);
        stmt.setInt(2, App.id);
        int deleteResults=stmt.executeUpdate();
        this.keySkills.remove(keySkill);
        System.err.println("keyskill removed: "+deleteResults);
    }

    public void updateAccomplishments(String accompolishments) throws SQLException{
        this.setAccompolishments(accompolishments);
        PreparedStatement stmt=App.conn.prepareStatement("UPDATE job_seekers SET accomplishments=? WHERE job_seeker_id=?");
        stmt.setString(1, accompolishments);
        stmt.setInt(2, this.id);
        int updateResults=stmt.executeUpdate();
    }

    //getMyReviews(jobseeker)(default postedAt descending)
    public ArrayList<Review> getMyReviews(HashMap<String,Integer> sortFilter,Integer daysFilter) throws SQLException{
        //for default showing date will be in descending order(modified first)
        if(sortFilter.isEmpty())
            sortFilter.put("reviewedAt", -1);


        StringBuilder query=new StringBuilder("SELECT * FROM reviews JOIN companies USING(company_id) WHERE job_seeker_id=?");

        //filterring last 7 days like
        if(daysFilter!=-1){
            query.append(" AND ");
            query.append("reviewedAt>(DATE_SUB(CURRENT_DATE,INTERVAL "+daysFilter+" DAY))");
        }

        //filtering for order by ratings it will be always the size of one
        if(!sortFilter.isEmpty()){
            query.append(" ORDER BY ");
            for(Map.Entry<String,Integer> m:sortFilter.entrySet()){
                query.append(m.getKey());
                query.append(m.getValue()==1?" ASC":" DESC");
                query.append(",");
            }
            query.deleteCharAt(query.length()-1);   
        }

        PreparedStatement stmt=App.conn.prepareStatement(query.toString());
        
        stmt.setInt(1,App.id);//job_seeker_id
        ArrayList<Review> reviews=new ArrayList<>();
        ResultSet rS=stmt.executeQuery();
        while(rS.next()){

            reviews.add(new Review(rS.getInt("review_id"),rS.getTimestamp("reviewedAt"),rS.getInt("ratings"),rS.getString("review"), rS.getString("pros"), rS.getString("cons"), rS.getString("job_title"), rS.getString("job_status"), rS.getString("location")));

        }
        return reviews;
    }

    public void deleteEducation(Education education) throws SQLException {
        PreparedStatement stmt=App.conn.prepareStatement("DELETE FROM educations WHERE education_id=?");
        stmt.setInt(1, education.getId());
        int deletedRows=stmt.executeUpdate();
        this.educations.remove(education);
    }

    public void deleteEmployment(Employment employment) throws SQLException {
        PreparedStatement stmt=App.conn.prepareStatement("DELETE FROM employments WHERE employment_id=?");
        stmt.setInt(1, employment.getId());
        int deletedRows=stmt.executeUpdate();
        this.employments.remove(employment);
    }

    public void deleteProject(Project project) throws SQLException {
        PreparedStatement stmt=App.conn.prepareStatement("DELETE FROM projects WHERE project_id=?");
        stmt.setInt(1, project.getId());
        int deletedRows=stmt.executeUpdate();
        this.projects.remove(project);
    }

    public void deleteReview(Review review) throws SQLException {
        PreparedStatement stmt=App.conn.prepareStatement("DELETE FROM reviews WHERE review_id=?");
        stmt.setInt(1, review.getId());
        int deletedRows=stmt.executeUpdate();
        System.err.println("Reviews Deleted: "+deletedRows);
    }

    public void deleteApplication(Application application) throws SQLException {
        PreparedStatement stmt=App.conn.prepareStatement("DELETE FROM applications WHERE application_id=?");
        stmt.setInt(1, application.getId());
        int deletedRows=stmt.executeUpdate();
        System.err.println("Applications Deleted: "+deletedRows);
    }


    public Integer getId(){
        return this.id;
    }
    public ArrayList<String> getKeySkills() {
        return keySkills;
    }

    public void setKeySkills(ArrayList<String> keySkills) {
        this.keySkills = keySkills;
    }

    public ArrayList<String> getLanguages() {
        return languages;
    }

    public void setLanguages(ArrayList<String> languages) {
        this.languages = languages;
    }

    public ArrayList<Employment> getEmployments() {
        return employments;
    }

    public void setEmployments(ArrayList<Employment> employments) {
        this.employments = employments;
    }

    public ArrayList<Education> getEducations() {
        return educations;
    }

    public void setEducations(ArrayList<Education> educations) {
        this.educations = educations;
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }

    public String getAccompolishments() {
        return accompolishments;
    }

    public void setAccompolishments(String accompolishments) {
        this.accompolishments = accompolishments;
    }


    @Override
    public String toString() {
        return "{" +
            " userType='" + getUserType() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", gender='" + getGender() + "'" +
            ", email='" + getEmail() + "'" +
            ", location='" + getLocation() + "'" +
            ", phone='" + getPhone() + "'" +
            ", DOB='" + getDOB() + "'" +
            " id='" + getId() + "'" +
            ", keySkills='" + getKeySkills() + "'" +
            ", languages='" + getLanguages() + "'" +
            ", employments='" + getEmployments() + "'" +
            ", educations='" + getEducations() + "'" +
            ", projects='" + getProjects() + "'" +
            ", accompolishments='" + getAccompolishments() + "'" +
            "}";
    }

}
