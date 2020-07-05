package edu.fa;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.fa.dao.StudentJdbcDao;
import edu.fa.dao.StudentJdbcDaoSupport;
import edu.fa.dao.StudentJdbcTemplateDao;
import edu.fa.model.Student;

public class StudentManagement {

	public static void main(String[] args) {
		
		//Load file cấu hình "Spring Beand Configuration File" 
		//=> giống load file cấu hình Hibernate "hibernate.cfg.xml"
		ApplicationContext applicationContext = 
				new ClassPathXmlApplicationContext("context.xml");
		
		//TH 1: Dùng jdbc thuần để kết nối DB trong Spring
		//Lấy ra một Object StudentJdbcDao ánh xạ DB ??? 
		//StudentJdbcDao StudentDao = applicationContext.getBean("studentJdbcDao", StudentJdbcDao.class);
		//truyền vào: applicationContext.getBean("tên Bean chứa kết quả trả về từ DB ???, <tên Bean có trong package <context:component-scan> trong context.xml>.class);
		//=> tương tự Hibernate là truyền vào: Class b = session.get(tên Class.class, id);
	
		//TH2: Dùng DataSource (driverClassName-> thay Class.forName() và url-> thay DriverManager.getConnection(url)) và JdbcTemplate trong Spring
		//Lấy ra một Bean trong ứng dụng, ở trên class "StudentJdbcTemplateDao"phải đặt @Component, hoặc @Controller,... để đánh dấu đây là một bean
		//Nếu @Component(value="tenBean") thì ở đây phải getBean(tenBean), còn mặc định thì tên Bean = tenClass( viết thường chữ đầu tiên)
		//StudentJdbcTemplateDao StudentDao = applicationContext.getBean("studentJdbcTemplateDao", StudentJdbcTemplateDao.class);
		
		//TH3: Dùng JdbcDaoSupport get ra luôn JdbcTemplate mà không cần tạo DataSource và JdbcTemplate
		StudentJdbcDaoSupport StudentDao = applicationContext.getBean("MyDaoSupport", StudentJdbcDaoSupport.class);
		
		//Insert một Student: StudentJdbcDao có phương thức insertStudent(...)
		//Tương tự: session.save(Object) => session.getTransaction().commit(); trong Hibernate để Insert một Object vào DB
		StudentDao.insertStudent(new Student(1, "An Nguyen", "Viet Nam"));
		
		//Lấy ra tất cả Student có trong DB => ứng dụng RowMapper
		System.out.println("Sau khi thêm: " + StudentDao.getAllStudents());
		
		//Lấy ra số sv có trong table Student
		int soSV = StudentDao.countStudent();
		System.out.println("Số record có trong table Student: " + soSV);
		
		//Lấy ra số sv có id =1 => ứng dụng RowMapper
		int id = 1;
		System.out.println("Student có id= " + id + " là: " + StudentDao.getStudentById(id));
		
		//Xóa tất cả Student
		StudentDao.deleteAllStudent();
		
		//Lấy ra tất cả Student có trong DB sau khi xóa => ứng dụng RowMapper
		System.out.println("Sau khi xóa: " + StudentDao.getAllStudents());
		
	}

}
