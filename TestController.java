package com.fenbi.fbms.controller;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fenbi.fbms.entity.Course;
import com.fenbi.fbms.entity.FenbiResult;
import com.fenbi.fbms.mapper.CourseMapper;
import com.fenbi.fbms.service.CourseService;

/**
 * 
 * 当tomcat启动，就会读取WEB-INF目录下的springmvc-serlvet.xml配置文件，扫描com.fenbi.fbms.controller下的所有类
 * 当springmvc扫描到当前类时，将会把TestController当作一个Controller类纳入springmvc容器中进行管理。
 * 
 * Controller类的作用就是接受请求，处理请求，返回响应
 */

@Controller
public class TestController {
	@Autowired
	private CourseService courseService;

	@RequestMapping("/baidu")
	public String baidu() {
		
		return "redirect:http://www.baidu.com";
	}
	
	@RequestMapping("/getCourse")
	@ResponseBody
	public FenbiResult getCourse(Integer id){
		//查询课程列表  
		Course c = courseService.findById(id);
		return new FenbiResult(c);
	}
		
	
	/**
	 * 一般情况下json的格式如下：
	 * {status:200, msg:"", data:{} } 
	 *    status:状态码
	 *    msg:字符串类型的响应消息
	 *    data:服务端返回给客户端的数据
	 * @return
	 */
	@RequestMapping("/getJson")
	@ResponseBody
	public FenbiResult getJson(){
		//查询课程列表
		List<Course> courses = courseService.selectCoursesByTitleLike("Java");
		return new FenbiResult(courses);
	}
	
	/**
	 * 测试重定向
	 * @return
	 */
	@RequestMapping("/redirect")
	public String redirect() {
		return "redirect:/forward.do";
	}
	
	/**
	 * 测试请求转发
	 * @return
	 */
	@RequestMapping("/forward")
	public String testForward(Model model) {
		//查询课程列表
		InputStream is = TestController.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
		SqlSessionFactory fac = new SqlSessionFactoryBuilder().build(is);
		SqlSession sqlSession = fac.openSession();
		CourseMapper mapper = sqlSession.getMapper(CourseMapper.class);
		List<Course> courses = mapper.selectCoursesByTitleLike("Java");
		sqlSession.close();
		//带着集合一起转发给jsp，在jsp中显示列表数据
		model.addAttribute("list", courses);
		return "test.jsp";
		
	}
	
	/**
	 * 测试中文编码问题
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/encoding", produces="text/html;charset=utf-8")
	@ResponseBody
	public String testEncoding(String name) {
		System.out.println("name:"+name);
		return "<h1>name: "+name+"</h1>";
	}
	
	
	/**
	 * 
	 * RequestParam：/testPathVariable/{id} ：
	 * http://localhost:8080/fbms/testPathVariable/1.do
	 * http://localhost:8080/fbms/testPathVariable/112342.do
	 * http://localhost:8080/fbms/testPathVariable/abcdef.do
	 * 
	 * RequestParam：/{module}/{id}
	 * http://localhost:8080/fbms/user/1.do
	 * http://localhost:8080/fbms/course/234.do
	 * http://localhost:8080/fbms/lesson/5656.do
	 * 
	 * 
	 * 测试springmvc 路径参数 的用法
	 * @return
	 */
	@RequestMapping(value="/{module}/{id}")
	@ResponseBody
	public String testPathVariable(@PathVariable String id, @PathVariable String module) {
		System.out.println("module:"+module);
		System.out.println("id:"+id);
		return "ok";
	}
	
	
	
	/**
	 * http://localhost:8080/fbms/testParams.do?id=1&title=gaogaozhenshuai
	 * 测试springmvc请求参数的处理
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value="/testParams")
	@ResponseBody
	public String testParams(Integer id, String title, String createTimeString, Course course) throws ParseException {
		//createTimeString : 2011-10-10
		Date createDate = new SimpleDateFormat("yyyy-MM-dd").parse(createTimeString);
		course.setCreateTime(new Timestamp(createDate.getTime()));
		System.out.println("id:"+id);
		System.out.println("title:"+title);
		return "ok";
	}
	
	
	/**
	 * http://localhost:8080/fbms/testRequestMethod.do 
	 * 测试springmvc可以接受的请求方式  method= {RequestMethod.GET, RequestMethod.POST}
	 * @return
	 */
	@RequestMapping(value="/testRequestMethod", method= {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String testRequestMethod() {
		return "ok";
	}
	
	/**
	 * RequestMapping注解:
	 * springmvc启动后，将会把/hello.do请求url与当前的hello方法绑定在一起，这样当springmvc接收到/hello.do
	 * 请求后，竟会自动调用该hello方法执行业务。
	 * 
	 * http://localhost:8080/fbms/hello.do?name=zs&pwd=123456
	 * 
	 * ResponseBody注解：
	 * 如果方法上方有responseBody注解，springmvc将会把方法的返回值直接写给客户端
	 * @return
	 */
	@RequestMapping("/hello")
	@ResponseBody   
	public String asdfasdfsadfsadf(String name, String pwd) {
		
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<10; i++) {
			sb.append("<h3 style='color:red;'> "+name+":"+pwd+" </h3>");
		}
		return sb.toString();
	}
	
	/**
	 * 作业，完成登录业务
	 * @param name
	 * @param pwd
	 * @return
	 */
	public String login(String name, String pwd) {
		return "ok";
	}

	/**
	 * 作业，完成注册业务
	 * @param name
	 * @param pwd
	 * @return
	 */
	public String regist(String name, String pwd, String phone) {
		return "ok";
	}

}














