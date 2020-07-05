package edu.fa.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import edu.fa.model.Student;

@Component//context:component-scan quét đến ??? => sẽ quét các hàm thao tác với CSDL, Bỏ "@Component" có được không ???
public class StudentJdbcDao {
	private String jdbcURL = "jdbc:derby://localhost:1527/education;create=true";//create=true:là bật kết nối ???
    private Connection connection = null;
    private Statement statement = null;
    
    //Lấy thông tin  Student
    public List<Student> getAllStudents()
    {
    	List<Student> students = new ArrayList<>();
    	createConnection();
        try
        {
        	statement = connection.createStatement();
            ResultSet results = statement.executeQuery("select * from Student");
            ResultSetMetaData rsmd = results.getMetaData();
            while(results.next())
            {
                int id = results.getInt(1);
                String name = results.getString(2);
                String location = results.getString(3);
                students.add(new Student(id, name, location));
            }
            results.close();
            statement.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        
        return students;
    }
    
    //Insert một Student vào CSDL
    public void insertStudent(Student student)
    {
    	createConnection();//Tạo kết nối DB
        try
        {
            statement = connection.createStatement();
            statement.execute("insert into Student values (" +
                    student.getId() + ",'" + student.getName() + "','" + student.getLocation() +"')");
            statement.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
    }
    
    //Tạo kết nối CSDL
    private void createConnection()
    {
    	if( connection == null ) {
    		try
	        {
	            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
	            //Get a connection
	            connection = DriverManager.getConnection(jdbcURL); 
	        }
	        catch (Exception except)
	        {
	            except.printStackTrace();
	        }
    	}
    }
    
    
    //Hàm đóng connection sau khi thao tác( thêm, sửa, xóa, đọc) với CSDL
    private void shutdown()
    {
        try
        {
            if (statement != null)
            {
            	statement.close();
            }
            if (connection != null)
            {
                DriverManager.getConnection(jdbcURL + ";shutdown=true");//shutdown=true => là ngắt kết nối ???
                connection.close();
            }           
        }
        catch (SQLException sqlExcept)
        {
            
        }

    }
}
