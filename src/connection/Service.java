package connection;

import java.util.List;

import javax.jws.WebService;

import exception.YelpException;
import vo.CategoryVO;
import vo.ElementVO;
import vo.ReviewVO;
import vo.StatusVO;
import vo.UserVO;

@WebService
public class Service {	
	DatabaseConnection db=new DatabaseConnection();
	
	public UserVO login(String email, String password) throws YelpException {
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		try{
			if(email==null || email.isEmpty()){
				throw new YelpException("First Name is required");
			}
			if(password==null || password.isEmpty()){
				throw new YelpException("Last Name is required");
			}
			UserVO userVO = db.login(email, password);
			System.out.println("UserVO.Id"+userVO.getId());
			System.out.println("UserVO.Name"+userVO.getFirstName() + " "+userVO.getLastName());
			System.out.println("UserVO.Email"+userVO.getEmail());
			System.out.println("UserVO.LastLogin"+userVO.getLastLogin());
			System.out.println("UserVO.isAdmin"+userVO.getIsAdmin());

			return userVO;
			
		}
		
		catch(Exception e){
			e.printStackTrace();
			throw new YelpException("Invalid username/password");
		}
		
	}
	
	public StatusVO signUp(String firstName, String lastName, String email, String pwd) throws YelpException
	{
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		String result;
		StatusVO statusVO = new StatusVO();
		if(firstName==null || firstName.isEmpty()){
			throw new YelpException("First Name is required");
		}
		if(lastName==null || lastName.isEmpty()){
			throw new YelpException("Last Name is required");
		}
		if(email==null || email.isEmpty()){
			throw new YelpException("Email is required");
		}
		if(pwd==null || pwd.isEmpty()){
			throw new YelpException("Password is required");
		}
		
		UserVO userVO = new UserVO();
		userVO.setFirstName(firstName);
		userVO.setLastName(lastName);
		userVO.setEmail(email);
		userVO.setPassword(pwd);
		
		
		//Boolean flag = db.checkUser(userVO.getEmail());
		//if(!flag)
		//	throw new YelpException("User already registered");
		//else
			statusVO = db.signUp(userVO);
			System.out.println(statusVO.getMessgge());
		return statusVO;//this value is returned to the calling servlet
	}
	
	public String getPassword(String username){
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		String result;
		result = db.getPassword(username);
		return result;
	}
	
	public CategoryVO[] getCategoryList(){
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		CategoryVO[] categories = null;
		try{
			categories = db.getCategories();
			System.out.println(categories.length +":ength");
			for(int i=0; i<categories.length; i++){
				System.out.println("CategoryVO.Id"+categories[i].getId());
				System.out.println("CategoryVO.Name"+categories[i].getName());
				System.out.println("CategoryVO.Desc"+categories[i].getDescription());
				System.out.println("CategoryVO.Count"+categories[i].getCount());
			}
		}
		catch(Exception exception){
			exception.printStackTrace();
		}
		
		return categories;
	}
	
	public String addCategory(String name, String description) throws YelpException {
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		if(name== null || name.isEmpty()){
			throw new YelpException("Category name is required!");
		}
		if(description== null || description.isEmpty()){
			throw new YelpException("Category name is required!");
		}
		String msg = db.addCategory(name, description);
		System.out.println(msg);
		return msg;
	}
	
	public String updateCategory(String name, String description, String categoryName) throws YelpException {
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		if(name== null || name.isEmpty()){
			throw new YelpException("Category name is required!");
		}
		if(description== null || description.isEmpty()){
			throw new YelpException("Category Description is required!");
		}
		if(categoryName == null || categoryName.isEmpty()){
			throw new YelpException("Category name is required!");
		}
		String msg = db.updateCategory(name, description, categoryName);
		System.out.println(msg);
		return msg;
	}
	
	public String addElement(String name, String description, String address, String categoryName ) throws YelpException{
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		if(name== null || name.isEmpty()){
			throw new YelpException("Element name is required!");
		}
		if(description== null || description.isEmpty()){
			throw new YelpException("Element description is required!");
		}
		if(categoryName == null || categoryName.isEmpty()){
			throw new YelpException("Category name is required!");
		}
		String msg = db.addElement(name, description, address, categoryName);
		System.out.println(msg);
		return msg;
	}
	
	public String updateElement(String name, String description, String address, String oldName) throws YelpException{
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		if(name== null || name.isEmpty()){
			throw new YelpException("Element name is required!");
		}
		if(description== null || description.isEmpty()){
			throw new YelpException("Element Description is required!");
		}
		if(address == null || address.isEmpty()){
			throw new YelpException("Element address is required!");
		}
		if(oldName == null || oldName.isEmpty()){
			throw new YelpException("Element old name is required!");
		}
		String msg = db.updateElement(name, description, address, oldName);
		System.out.println(msg);
		return msg;
	}
	
	public String deleteCategory(String name) throws YelpException{
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		if(name== null || name.isEmpty()){
			throw new YelpException("Category name is required!");
		}
		String msg = db.deleteCategory(name);
		System.out.println(msg);
		return msg;
		
	}
	
	public String deleteElement(String name) throws YelpException{
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		if(name== null || name.isEmpty()){
			throw new YelpException("Element name is required!");
		}
		String msg = db.deleteElement(name);
		System.out.println(msg);
		return msg;
		
	}
	
	public ElementVO[] getElementList(String categoryName) throws YelpException{
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		ElementVO[] elements = null;
		try{
			if(categoryName== null || categoryName.isEmpty()){
				throw new YelpException("Category name is required!");
			}
			elements = db.getElements(categoryName);
			/*for(int i=0; i<elements.length; i++){
				System.out.println("ElementVO.Id : "+elements[i].getId());
				System.out.println("ElementVO.Name : "+elements[i].getName());
				System.out.println("ElementVO.Desc : "+elements[i].getDescription());
				System.out.println("ElementVO.Address : "+elements[i].getAddress());
				System.out.println("CategoryVO.Count : "+elements[i].getCount());
			}*/
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return elements;
	
	}
	
	public StatusVO addReview(String elementName, String email, int rating, String comment) throws YelpException{
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		StatusVO statusVO = new StatusVO();
		try{
			if(elementName== null || elementName.isEmpty()){
				statusVO.setIsSuccessful(false);
				statusVO.setMessgge("Element name is required!");
				throw new YelpException("Element name is required!");
			}
			statusVO = db.addReviews(elementName, email, rating, comment);
			System.out.println(statusVO.getMessgge());
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return statusVO;
	}
	
	public ReviewVO[] getReviews(String elementName) throws YelpException {
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
		if(elementName== null || elementName.isEmpty()){
			throw new YelpException("Element name is required!");
		}
		ReviewVO[] reviews = db.getReviews(elementName);
		for(int i=0; i<reviews.length; i++){
			System.out.println("ReviewVO.Id : "+reviews[i].getId());
			System.out.println("ReviewVO.Rating : "+reviews[i].getRating());
			System.out.println("ReviewVO.comment : "+reviews[i].getComment());
			System.out.println("ReviewVO.email : "+reviews[i].getEmail());
			System.out.println("ReviewVO.getSubmittedOn : "+reviews[i].getSubmittedOn());
			System.out.println("ReviewVO.elementName : "+reviews[i].getElementName());
			System.out.println("ReviewVO.elementId : "+reviews[i].getElementId());
			
		}
		return reviews;
	}
	
	
	
}
