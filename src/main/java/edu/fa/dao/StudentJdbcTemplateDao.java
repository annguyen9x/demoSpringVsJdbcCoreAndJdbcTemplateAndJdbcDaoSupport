package edu.fa.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.swing.tree.TreePath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import edu.fa.model.Student;

@Component//context:component-scan quét đến package "edu.fa.dao" có class này, Bỏ "@Component" không được do không load được Bean. 
//@Componet tương đương (@Repository, @Server,..) nhưng nó dùng chung được khi không đặt là @Repository,...
//Nếu đặt @Component(value = "ABC") -> thì ở hàm Main phải getBean(ABC), còn mặc định thì tên Bean = tenClass( viết thường chữ đầu tiên)
public class StudentJdbcTemplateDao {

	//@Autowired -> Nếu không đặt ở đây mà đặt ở hàm setDataSource() thì jdbcTemplate không cần khởi tạo new JdbcTemplate(); 
    //và gán load DataSource:jdbcTemplate.setDataSource(dataSource); nhiều lần mỗi khi cần thực thi câu truy vấn
   
    //Khi đặt @Autowired thì spring sẽ tự động Inject Bean( tiêm Bean- tức là khởi tạo và gán đối tượng đã khởi tạo)
    //vào thuộc tính (hoặc hàm ???) được đánh dấu @Autowire, với điều kiện là class phải được  đánh dấu là: @Component, hoặc 
    //@Repository, @Service, @Controller
    private DataSource dataSource;//Tương đương load Driver và load URL trong jdbc thuần 
    //-> khi cần tạo kết nối chỉ cần: connection = dataSource.getConnection(); ->không cần truyền vào URL như code thuần
    
    //private JdbcTemplate jdbcTemplate = new JdbcTemplate();//Tương đương tạo connection và tạo statement trong jdbc thuần, nhưng 
    //nó nhanh hơn do chỉ một bước: jdbcTemplate.setDataSource(dataSource); tương đương 2 bước: tạo connection và statement của jdbc thuần
    //Ở đây không cần: connection = dataSource.getConnection() như Statement + DataSource
    
    
    private JdbcTemplate jdbcTemplate;//Sẽ khởi tạo ở hàm setDataSource() để không phải setDataSource() nhiều lần
    //Ở đây không cần: connection = dataSource.getConnection() như Statement + DataSource
    		
    public DataSource getDataSource() {
		return dataSource;
	}

    @Autowired// Do có @Autowired nên hàm này sẽ luôn được gọi, và được (inject Bean) truyền vào Object khi app chạy ???
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		
		jdbcTemplate = new JdbcTemplate(dataSource);//Do có  @Autowired nên khởi tạo ở đây thay vì khỏi tạo ở thuộc tính
		//-> để không phải jdbcTemplate.setDataSource(dataSource);  nhiều lần mỗi khi thực thi câu truy vấn
	}

    //Ứng dụng class kế thừa RowMapper lấy ra tất cả Student
    public List<Student> getAllStudents()
    {
    	List<Student> students = new ArrayList<>();
    	String query = "Select * From Student";
    	students = jdbcTemplate.query(query, new MyStudentMapper());
        return students;
    }
    
    //Ứng dụng class kế thừa RowMapper lấy ra một Student theo id
    public Student getStudentById(int id) {
    	Student student = null;
    	String query = "Select * From Student Where id= ?";
    	student = jdbcTemplate.queryForObject(query, new Object[] {1}, new MyStudentMapper());//"new MyStudentMapper()" là một "Instant" (thể hiện) của MyStudentMapper
    	//queryForObject(query, new Object[] {tham số 1, tham số 2,...}, new class kế thừa RowMapper)
    	return student;
    }
    
    //Insert một Student vào CSDL
    public void insertStudent(Student student)
    {
    	//jdbcTemplate.setDataSource(dataSource);//Bỏ cái này vì đã có  @Autowired ở hàm setDataSource() nên nó đã được tự khởi tạo: jdbcTemplate = new JdbcTemplate(dataSource);
    	//Tương đương: createConnection();//Tạo kết nối DB do @Autowired ở thuộc tính dataSource nên nó tự tạo và gán giá trị cho dataSource
    	//và statement = connection.createStatement(); trong jdbc thuần
    	
    	String query = "insert into Student values (" +  student.getId() + ",'" + student.getName() + "','" + 
    					student.getLocation() +"')";
    	jdbcTemplate.execute(query);//tương đương  statement.execute(query) trong jdbc thuần
    	//không cần statement.close(); ???
    }
    
    //delete tất cả dứ liệu trong table Student 
    public void deleteAllStudent()
    {
    	//jdbcTemplate.setDataSource(dataSource);
    	//Tương đương: createConnection();//Tạo kết nối DB do @Autowired ở thuộc tính dataSource nên nó tự tạo và gán giá trị cho dataSource
    	//và statement = connection.createStatement(); trong jdbc thuần
    	
    	String query = "Delete from Student";
    	jdbcTemplate.execute(query);//tương đương:  statement.execute(query) trong jdbc thuần
    	//không cần statement.close(); ???
    }
    
    //Lấy ra số sv trong table Student=> Không dùng RowMapper nên chỉ lấy được Object có sẵn như: Integer.class, String.class
    public int countStudent()
    {
    	//jdbcTemplate.setDataSource(dataSource);
    	//Tương đương: createConnection();//Tạo kết nối DB do @Autowired ở thuộc tính dataSource nên nó tự tạo và gán giá trị cho dataSource
    	//và statement = connection.createStatement(); trong jdbc thuần
    	
    	String query = "Select count(*) from Student";
    	return jdbcTemplate.queryForObject(query, Integer.class);//queryForObject(query, <Kiểu Object trả về>.class) -> trả về kiểu đối tượng mong muốn
    	//không cần statement.close(); ???
    }
    
    //Tạo Inner class dùng cho RowMapper: class final thì class khác không thể kế thừa; p.thức final không thể ghi đè(override);
    //"biến final" là hằng số không thể thay đổi giá trị (nếu đã gán giá trị), giá trị gán lúc k/báo hoặc trong hàm constructor, 
    //"biến static final" là hằng số không thể thay đổi giá trị (nếu đã gán giá trị), giá trị gán lúc k/báo hoặc trong khối lệnh static{}
    public static final class MyStudentMapper implements RowMapper<Student>{

		@Override
		public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("location"));//trả về Object student
		}

    }
}
