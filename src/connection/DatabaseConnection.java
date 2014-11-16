package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.tomcat.jdbc.pool.DataSource;

import vo.CategoryVO;
import vo.ElementVO;
import vo.ReviewVO;
import vo.StatusVO;
import vo.UserVO;
import exception.YelpException;


public class DatabaseConnection {
	
	Connection con = null;
	static ResultSet rs;
	Statement stmt = null;
	DataSource ds = null;
//	
	DatabaseConnection(){		
		try {			
				//Class.forName("com.mysql.jdbc.Driver").newInstance();
				//con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Yelp","root","");
				//stmt = con.createStatement();
				//if(!con.isClosed())
				//	System.out.println("Successfully Connected!!!");
			ConnectionPoolManager ConnectionPoolManager = new ConnectionPoolManager();
			
			ds = ConnectionPoolManager.getDS();
			con =  ds.getConnection();
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public UserVO login(String email, String password) throws YelpException {
		java.sql.PreparedStatement pStatement = null;
		ResultSet rs  = null;
		String sql = null;
		UserVO userVO = null;
		int rowcount=0;
		try{
			sql = "Select * from users where email = ?";
			pStatement = con.prepareStatement(sql);
			pStatement.setString(1, email);
			rs = pStatement.executeQuery();
			//rs.close();
			//pStatement.close();
			if (!rs.next() ) {
			   throw new YelpException("Invalid username/password");
			} else {
				if(rs.getString("password").equals(password)){
					userVO = new UserVO();
					userVO.setId(rs.getInt("id"));
					userVO.setEmail(rs.getString("email"));
					userVO.setLastName(rs.getString("lastname"));
					userVO.setFirstName(rs.getString("firstname"));
					userVO.setLastLogin((rs.getString("lastlogin")==null||rs.getString("lastlogin")=="")?"First Login":rs.getString("lastlogin"));
					userVO.setIsAdmin(rs.getString("isAdmin"));
					//set up login time
					java.util.Date date= new java.util.Date();
					sql = "UPDATE users SET lastlogin= ? where email= ?";
					pStatement = con.prepareStatement(sql);
					pStatement.setTimestamp(1, new Timestamp(date.getTime()));
					pStatement.setString(2, email);
					rowcount=pStatement.executeUpdate();
					rs.close();
					pStatement.close();
					if(rowcount > 0){
						System.out.println("Last login updated!");
					}
					else{
						System.out.println("The data could not be inserted in the database");
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return userVO;
	}

	public StatusVO signUp(UserVO userVO){
		String result = "";
		int rowcount;
		StatusVO statusVO = new StatusVO();
		try {
			String sql = "insert into users(firstname, lastname, email, password, isAdmin) values (?,?,?,?,?)";
			java.sql.PreparedStatement pStatement = con.prepareStatement(sql);
			pStatement.setString(1, userVO.getFirstName());
			pStatement.setString(2, userVO.getLastName());
			pStatement.setString(3, userVO.getEmail());
			pStatement.setString(4, userVO.getPassword());
			pStatement.setString(5, "N");
			
			//pStatement.executeUpdate();
			rowcount=pStatement.executeUpdate();;
			rs.close();
			pStatement.close();
			
			if(rowcount > 0){
				statusVO.setIsSuccessful(true);
				statusVO.setMessgge("You are registered!");
				//System.out.println("You are registered!");
			}
			else{
				statusVO.setIsSuccessful(false);
				statusVO.setMessgge("The data could not be inserted in the database");
			}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			statusVO.setIsSuccessful(false);
			statusVO.setMessgge("The data could not be inserted in the database");
			e.printStackTrace();
		} 
		finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


		return statusVO;
	}
	
	public String getPassword(String username){
		String result="";
		try{
			String query = "Select password from Customer where username='"+username+"'";
			rs = stmt.executeQuery(query);
			rs.next();
			result = rs.getString("password");
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public Boolean checkUser(String email){
		Boolean flag= false;
		try{
			String sql = "Select firstname from users where email = ?";
			java.sql.PreparedStatement pStatement = con.prepareStatement(sql);
			pStatement.setString(1,email);
			ResultSet rs = pStatement.executeQuery();
			System.out.println(rs);
			if(rs!=null){
				rs.next();
				flag = (rs.getString("firstname")==null)?true:false;
			}
			else{
				flag = true;
			}
			rs.close();
			pStatement.close();
			
		}
		catch(SQLException sqlException){
			sqlException.printStackTrace();
		}
		finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return flag;
	}
	
	public CategoryVO[] getCategories(){
		CategoryVO[] categories = null;
		int count = 0;
		java.sql.PreparedStatement pStatement = null;
		ResultSet rs  = null;
		String sql = null;
		try{
			sql = "Select count(id) as rowcount from category";
			pStatement = con.prepareStatement(sql);
			rs = pStatement.executeQuery();
			rs.next();
			int rowcount = rs.getInt("rowcount");
			rs.close();
			categories = new CategoryVO[rowcount];
			sql = "Select * from category";
			pStatement = con.prepareStatement(sql);
			rs = pStatement.executeQuery();
			while(rs.next()){
				CategoryVO categoryVO = new CategoryVO();
				categoryVO.setId(Long.valueOf(rs.getInt("id")));
				categoryVO.setName(rs.getString("name"));
				categoryVO.setDescription(rs.getString("description"));
				categoryVO.setCount(rs.getInt("count"));
				categories[count] = categoryVO;
				count++;
				//categories.add(categoryVO);
			}
			rs.close();
			pStatement.close();
		}
		catch(SQLException exception){
			exception.printStackTrace();
		}
		finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return categories;
	}
	
	public String addCategory(String name, String description){
		String msg=null;
		int rowcount;
		try{
			String sql = "INSERT into category (name, description) VALUES (?,?)";
			java.sql.PreparedStatement pStatement = con.prepareStatement(sql);
			pStatement.setString(1, name);
			pStatement.setString(2, description);
			rowcount=pStatement.executeUpdate();;
			rs.close();
			pStatement.close();
			
			if(rowcount > 0){
				msg="Category Added Successfully!";
				System.out.println("Category Added Successfully!");
			}
			else{
				msg="Category already exists!";
			}	
		}
		catch(SQLException exception){
			msg = "Category already exists!";
			exception.printStackTrace();
		}
		finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return msg;
	}
	
	public String updateCategory(String name, String description, String CategoryName) throws YelpException{
		String msg=null;
		int rowcount;
		java.sql.PreparedStatement pStatement = null;
		ResultSet rs = null;
		try{
			//categories = new ArrayList();
			String sqlQuery = "SELECT id from category WHERE lower(name) = ?";
			pStatement = con.prepareStatement(sqlQuery);
			pStatement.setString(1, CategoryName.toLowerCase());
			rs = pStatement.executeQuery();
			if(rs==null){
				msg = "No such category found";
				throw new YelpException("No such category found");
			}
			rs.next();
			int id = rs.getInt("id");
			
			String sql = "UPDATE category  SET name = ?, description = ? WHERE id = ?";
			pStatement = con.prepareStatement(sql);
			pStatement.setString(1, name);
			pStatement.setString(2, description);
			pStatement.setInt(3, id);
			
			rowcount=pStatement.executeUpdate();;
			rs.close();
			pStatement.close();
			
			if(rowcount > 0){
				msg="Category Updated Successfully!";
				System.out.println("Category Updated Successfully!");
			}
			else{
				msg="Category already exists!";
			}	
		}
		catch(SQLException exception){
			msg = exception.getMessage();
			exception.printStackTrace();
		}
		finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return msg;
	}
	
	public String addElement(String name, String description, String address, String categoryName) throws YelpException{
		String msg=null;
		int rowcount;
		java.sql.PreparedStatement pStatement = null;
		ResultSet rs = null;
		try{
			//categories = new ArrayList();
			String sqlQuery = "SELECT id from category WHERE lower(name) = ?";
			pStatement = con.prepareStatement(sqlQuery);
			pStatement.setString(1, categoryName.toLowerCase());
			rs = pStatement.executeQuery();
			if(rs==null){
				msg = "No such category found";
				throw new YelpException("No such category found");
			}
			rs.next();
			int id = rs.getInt("id");
			
			String sql = "INSERT into element(name, description, address, category_fk) values (?,?,?,?)";
			
			pStatement = con.prepareStatement(sql);
			pStatement.setString(1, name);
			pStatement.setString(2, description);
			pStatement.setString(3, address);
			pStatement.setInt(4, id);
			
			rowcount=pStatement.executeUpdate();;
			rs.close();
			pStatement.close();
			
			if(rowcount > 0){
				msg="Element Added Successfully!";
				System.out.println("Element Added Successfully!");
			}
			else{
				msg="Element already exists!";
			}	
		}
		catch(SQLException exception){
			msg = exception.getMessage();
			msg = "Element Already ecists!";
			exception.printStackTrace();
		}
		finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return msg;
	}
	
	
	public String updateElement(String name, String description, String address, String oldName) throws YelpException{
		String msg=null;
		int rowcount;
		java.sql.PreparedStatement pStatement = null;
		ResultSet rs = null;
		try{
			//categories = new ArrayList();
			String sqlQuery = "SELECT id from element WHERE lower(name) = ?";
			pStatement = con.prepareStatement(sqlQuery);
			pStatement.setString(1, oldName.toLowerCase());
			rs = pStatement.executeQuery();
			if(rs==null){
				msg = "No such element found";
				throw new YelpException("No such element found");
			}
			rs.next();
			int id = rs.getInt("id");
			
			String sql = "UPDATE element SET name = ?, description = ?, address = ? WHERE id = ?";
			pStatement = con.prepareStatement(sql);
			pStatement.setString(1, name);
			pStatement.setString(2, description);
			pStatement.setString(3, address);
			pStatement.setInt(4, id);
			
			rowcount=pStatement.executeUpdate();;
			rs.close();
			pStatement.close();
			
			if(rowcount > 0){
				msg="Element Updated Successfully!";
				System.out.println("Element Updated Successfully!");
			}
			else{
				msg="Element already exists!";
			}	
		}
		catch(SQLException exception){
			msg = exception.getMessage();
			exception.printStackTrace();
		}
		finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return msg;
	}
	
	public String deleteCategory(String name) throws YelpException{

		String msg=null;
		int rowcount;
		java.sql.PreparedStatement pStatement = null;
		ResultSet rs = null;
		try{
			//categories = new ArrayList();
			String sqlQuery = "SELECT id from category WHERE lower(name) = ?";
			pStatement = con.prepareStatement(sqlQuery);
			pStatement.setString(1, name.toLowerCase());
			rs = pStatement.executeQuery();
			if(rs==null){
				msg = "No such category found";
				throw new YelpException("No such category found");
			}
			rs.next();
			int id = rs.getInt("id");
			
			String sql = "Delete from category WHERE id = ?";
			pStatement = con.prepareStatement(sql);
			pStatement.setInt(1, id);
			rowcount=pStatement.executeUpdate();;
			rs.close();
			pStatement.close();
			
			if(rowcount > 0){
				msg="Category deleted Successfully!";
				System.out.println("Category deleted Successfully!");
			}
			else{
				msg="Category cannot be deleted!";
			}	
		}
		catch(SQLException exception){
			msg = exception.getMessage();
			exception.printStackTrace();
		}
		finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return msg;
	
	}
	
	public String deleteElement(String name) throws YelpException{

		String msg=null;
		int rowcount;
		java.sql.PreparedStatement pStatement = null;
		ResultSet rs = null;
		try{
			//categories = new ArrayList();
			String sqlQuery = "SELECT id from element WHERE lower(name) = ?";
			pStatement = con.prepareStatement(sqlQuery);
			pStatement.setString(1, name.toLowerCase());
			rs = pStatement.executeQuery();
			if(rs==null){
				msg = "No such element found";
				throw new YelpException("No such element found");
			}
			rs.next();
			int id = rs.getInt("id");
			
			String sql = "Delete from element WHERE id = ?";
			pStatement = con.prepareStatement(sql);
			pStatement.setInt(1, id);
			rowcount=pStatement.executeUpdate();;
			rs.close();
			pStatement.close();
			
			if(rowcount > 0){
				msg="Element deleted Successfully!";
				System.out.println("Element deleted Successfully!");
			}
			else{
				msg="Element cannot be deleted!";
			}	
		}
		catch(SQLException exception){
			msg = exception.getMessage();
			exception.printStackTrace();
		}
		finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return msg;
	}
	
	public ElementVO[] getElements(String categoryName){
		ElementVO[] elements = null;
		java.sql.PreparedStatement pStatement = null;
		ResultSet rs = null;
		int count = 0;
		String sql = null;
		try{
			sql = "Select count(e.id) as rowcount from element e join category c on c.id = e.category_fk WHERE LOWER(c.name) = ?";
			pStatement = con.prepareStatement(sql);
			pStatement.setString(1, categoryName.toLowerCase());
			rs = pStatement.executeQuery();
			rs.next();
			int rowcount = rs.getInt("rowcount");
			rs.close();
			elements = new ElementVO[rowcount];

			sql = "Select * from element e join category c on c.id = e.category_fk WHERE LOWER(c.name) = ?";
			pStatement = con.prepareStatement(sql);
			pStatement.setString(1, categoryName.toLowerCase());
			rs = pStatement.executeQuery();
			while(rs.next()){
				ElementVO elementVO = new ElementVO();
				elementVO.setId(Long.valueOf(rs.getInt("id")));
				elementVO.setName(rs.getString("name"));
				elementVO.setDescription(rs.getString("description"));
				elementVO.setAddress(rs.getString("address"));
				elementVO.setCount(rs.getLong("total_Reviews"));
				elementVO.setRating(rs.getInt("avg_rating"));;
				elements[count] = elementVO;
				count++;
				//categories.add(categoryVO);
			}
			rs.close();
			pStatement.close();
		}
		catch(SQLException exception){
			exception.printStackTrace();
		}
		finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return elements;
	}
	
	public StatusVO addReviews(String elementName, String email, int rating, String comment) throws YelpException {
		String msg=null;
		int rowcount;
		java.sql.PreparedStatement pStatement = null;
		ResultSet rs = null;
		int userId = 0;
		int id = 0;
		StatusVO statusVO = new StatusVO();
		int categoryId = 0;
		try{
			//categories = new ArrayList();
			String sqlQuery = "SELECT id, category_fk from element WHERE lower(name) = ?";
			try{
				pStatement = con.prepareStatement(sqlQuery);
				pStatement.setString(1, elementName.toLowerCase());
				rs = pStatement.executeQuery();
				if(rs==null){
					msg = "No such element found";
					statusVO.setIsSuccessful(false);
					statusVO.setMessgge(msg);
					throw new YelpException("No such element found");
				}
				rs.next();
				id = rs.getInt("id");
				categoryId = rs.getInt("category_fk");
				rs.close();
			}
			catch(SQLException exception){
				exception.printStackTrace();
				msg = "No such element found";
				statusVO.setIsSuccessful(false);
				statusVO.setMessgge(msg);
				new YelpException("No Element found");
			}
			try{
				sqlQuery = "SELECT id from users WHERE LOWER(email) = ?";
				pStatement = con.prepareStatement(sqlQuery);
				pStatement.setString(1, email.toLowerCase());
				rs = pStatement.executeQuery();
				if(rs==null){
					msg = "No such user found";
					statusVO.setIsSuccessful(false);
					statusVO.setMessgge(msg);
					throw new YelpException("No such user found");
				}
				rs.next();
				userId = rs.getInt("id");
				rs.close();
			}
			catch(SQLException exception){
				msg = "No such user found";
				statusVO.setIsSuccessful(false);
				statusVO.setMessgge(msg);
				new YelpException("No User found");
			}
			
			String sql = "INSERT into reviews(rating, comment, submitted_by, submitted_on, emlement_id) values (?,?,?,?,?)";
			java.util.Date date= new java.util.Date();
			
			pStatement = con.prepareStatement(sql);
			pStatement.setInt(1, rating);
			pStatement.setString(2, comment);
			pStatement.setInt(3, userId);
			pStatement.setTimestamp(4, new Timestamp(date.getTime()));
			pStatement.setInt(5, id);
			
			rowcount=pStatement.executeUpdate();
			
			if(rowcount > 0){
				msg="Review submitted Successfully!";
				statusVO.setIsSuccessful(true);
				statusVO.setMessgge(msg);
				System.out.println("Review submitted Successfully!");
			}
			else{
				msg="Review cannot be submitted!";
				statusVO.setIsSuccessful(false);
				statusVO.setMessgge(msg);
			}
			
			sql = "select floor(sum(rating)/count(id)) as avg, count(id) as count from reviews where emlement_id = ?";
			pStatement = con.prepareStatement(sql);
			pStatement.setInt(1, id);
			rs = pStatement.executeQuery();
			rs.next();
			int avgRating  = rs.getInt("avg");
			int count = rs.getInt("count");
			rs.close();
			
			sql = "UPDATE element SET total_reviews=?, avg_rating=? WHERE id=?";
			pStatement = con.prepareStatement(sql);
			pStatement.setInt(1, count);
			pStatement.setInt(2, avgRating);
			pStatement.setInt(3, id);
			rowcount = pStatement.executeUpdate();
			
			sql = "select count(r.id) as count from reviews r join element e on e.id = r.emlement_id join category c on c.id = e.category_fk Where c.id = ?";
			pStatement = con.prepareStatement(sql);
			pStatement.setInt(1, categoryId);
			rs = pStatement.executeQuery();
			rs.next();
			count  = rs.getInt("count");
			rs.close();
			
			sql = "UPDATE category SET count=? WHERE id=?";
			pStatement = con.prepareStatement(sql);
			pStatement.setInt(1, count);
			pStatement.setInt(2, categoryId);
			rowcount = pStatement.executeUpdate();
			
			rs.close();
			pStatement.close();
			
				
		}
		catch(SQLException exception){
			msg = exception.getMessage();
			statusVO.setIsSuccessful(false);
			statusVO.setMessgge(msg);
			exception.printStackTrace();
		}
		finally{
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return statusVO;
	}
	
	public ReviewVO[] getReviews(String elementName) throws YelpException{

		ReviewVO[] reviews = null;
		java.sql.PreparedStatement pStatement = null;
		String sql = null;
		ResultSet rs = null;
		int count = 0;
		try{
			sql = "Select count(r.id) as rowcount from reviews r join element e on e.id = r.emlement_id and LOWER(e.name) = ?";
			pStatement = con.prepareStatement(sql);
			pStatement.setString(1, elementName.toLowerCase());
			rs = pStatement.executeQuery();
			rs.next();
			int rowcount = rs.getInt("rowcount");
			rs.close();
			reviews = new ReviewVO[rowcount];
			
			sql = "Select r.id as id, r.comment as comment, r.rating as rating, u.email as email, r.submitted_on as submittedOn, "
					+ "e.name as elementName, e.id as element_id"
					+ " from reviews r join element e on e.id = r.emlement_id join users u on u.id = r.submitted_by"
					+ " WHERE LOWER(e.name) = ?";
			pStatement = con.prepareStatement(sql);
			pStatement.setString(1, elementName.toLowerCase());
			rs = pStatement.executeQuery();
			while(rs.next()){
				ReviewVO reviewVO = new ReviewVO();
				reviewVO.setId(Long.valueOf(rs.getInt("id")));
				reviewVO.setComment(rs.getString("comment"));
				reviewVO.setRating(rs.getLong("rating"));
				reviewVO.setSubmittedOn(rs.getString("submittedOn"));
				reviewVO.setEmail(rs.getString("email"));
				System.out.println(rs.getString("elementName"));
				System.out.println(rs.getInt("element_id"));
				reviewVO.setElementName(rs.getString("elementName"));
				reviewVO.setElementId(rs.getInt("element_id"));
				reviews[count] = reviewVO;
				count++;
				//categories.add(categoryVO);
			}
			rs.close();
			pStatement.close();
		}
		catch(SQLException exception){
			exception.printStackTrace();
		}
		finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return reviews;
	
		
	}
}

