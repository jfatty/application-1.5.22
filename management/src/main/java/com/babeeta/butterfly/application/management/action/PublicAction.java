package com.babeeta.butterfly.application.management.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.management.entity.User;
import com.babeeta.butterfly.application.management.service.UserService;

/***
 * 公用action
 * @author zeyong.xia
 * @date 2011-12-13
 */
public class PublicAction extends BaseAction {

	/**
	 * @author zeyong.xia
	 * @date 2011-12-13
	 */
	private static final long serialVersionUID = 1L;

	private final static Logger logger = LoggerFactory
	.getLogger(MessageAction.class);
	
	private User user;
	
	private UserService userServiceImpl;
	
	private Font mFont = new Font("Arial Black", Font.PLAIN, 20);
	
	private String email;
	
	private String managerEmail;
	
	private String managerPass;
	
	private String address;
	
	private String company;

	
	
	
	/***
	 * 用户注册
	 * @return
	 */
	public String registerUser()
	{
		
		logger.debug("[PublicAction] registerUser");
		int totalCount=this.userServiceImpl.queryCount();
		//添加管理员
		if(totalCount==0)
		{
			logger.debug("[PublicAction] registerUser start add manager");
			User u=new User();
			u.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		    u.setCreateAt(new Date());
		    u.setRoleName("manager");
		    u.setAddress(this.address);
		    u.setCompanyName(this.company);
		    u.setContactMan("张三");
		    u.setContactPhone("13800000000");
		    u.setUserEmail(this.managerEmail);
		    u.setUserPass(this.managerPass);
			try{
			this.userServiceImpl.addUser(u);
			logger.debug("[PublicAction] registerUser start add manager success,manager is {}",u.getUserEmail());
			}
			catch(Exception e)
			{
				logger.debug("[PublicAction] registerManagerUser failure,exception is {}",e.getMessage());
				return ERROR;
			}
		}
		user.setId(UUID.randomUUID().toString().replaceAll("-", ""));
	    user.setCreateAt(new Date());
	    user.setRoleName("user");
		try{
		this.userServiceImpl.addUser(user);
		}
		catch(Exception e)
		{
			logger.debug("[PublicAction] registerUser failure,exception is {}",e.getMessage());
			return ERROR;
		}
		this.email=this.user.getUserEmail();
		logger.debug("[PublicAction] registerUser success");
		return SUCCESS;
	}
	
	/***
	 * 用户列表
	 * @return
	 */
	public String queryUserList()
	{
		logger.debug("[PublicAction] queryUserList");
		String email=(String)request.getSession().getAttribute("userEmail");
		if(email==null||email=="")
		{
			logger.debug("[FrontAction]queryUserList, user not login");
			return "login";
		}
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("roleName", "<>root");
		int totalCount=this.userServiceImpl.queryCount();
		int offset = this.setPage(totalCount, request);
		List<User> list=this.userServiceImpl.queryUserList(offset, 3,map);
		request.setAttribute("userList", list);
		logger.debug("[PublicAction] queryUserList success");
		return SUCCESS;
	}
	
	/***
	 * 注册成功后登录
	 * @return
	 */
	public String registerAndLogin()
	{
		logger.debug("[PublicAction]registerAndLogin");
		String userEmail=(String)request.getSession().getAttribute("userEmail");
		if(userEmail!=null&&!userEmail.equals(""))
		{
			request.getSession().removeAttribute("userEmail");
		}
		userEmail=request.getParameter("email");
		if(userEmail==null||userEmail.equals(""))
		{
			logger.debug("[PublicAction]registerAndLogin fail");
			return "login";
		}
		request.getSession().setAttribute("userEmail", userEmail);
		logger.debug("[PublicAction]registerAndLogin success");
		return SUCCESS;
	}
	
	
	/***
	 * 用户登录
	 * @return
	 */
	public String login()
	{
		logger.debug("[PublicAction]login");
		String email=request.getParameter("user.userEmail");
		String password=request.getParameter("user.userPass");
		if(email==null||password==null)
		{
			email="";
			return "login";
		}
		request.setAttribute("email",email);
		String rand=(String)request.getSession().getAttribute("rand");
		String randImg=request.getParameter("randImg");
		logger.debug("[PublicAction]login,rand is {},param randImg is {}",rand,randImg);
		if(rand==null||randImg==null||rand.equals("")||randImg.equals("")||!rand.equalsIgnoreCase(randImg))
		{
			request.setAttribute("result", "-1");
			return "login";			
		}
		
		user=this.userServiceImpl.login(email, password);
		if(user!=null)
		{
			request.getSession().setAttribute("userEmail", email);
			String role=user.getRoleName();
			logger.debug("[PublicAction]login roleName is {}",role);
			if(role!=null&&!role.equals("user"))
			{
				
				return "manager";
			}
			else
			{
				return "user";
			}
		}
		else
		{
			request.setAttribute("result", "0");
			return "login";
		}
	}
	
	/***
	 * 用户信息
	 * @return
	 */
	public String queryUser()
	{
		logger.debug("[PublicAction] queryUser {}",this);
		String userEmail=(String)request.getSession().getAttribute("userEmail");
		String result=request.getParameter("result");
		if(result!=null&&!"".equals(result))
		{
			request.setAttribute("result", result);
		}
		this.user=this.userServiceImpl.queryUserByEmail(userEmail);
		request.setAttribute("user", user);
		logger.debug("[PublicAction] queryUser success");
		return SUCCESS;
	}
	
	/****
	 * 验证邮箱
	 */
	public void vilidateEmail()
	{
		logger.debug("[PublicAction] vilidateEmail ");
		String email=request.getParameter("userEmail").trim();
		if(email==null||email=="")
		{
			logger.debug("[PublicAction] vilidateEmail,email is null ");
			PrintWriter out=null;
			try {
				out = response.getWriter();
				out.write("0");
				out.flush();
				out.close();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.debug("[PublicAction] vilidateEmail,email is null ,exception is {}",e.getMessage());
			}
			
		}
		User flag=this.userServiceImpl.queryEmail(email);
		if(flag!=null)
		{
			PrintWriter out=null;
			try {
				out = response.getWriter();
				out.write("1");
				out.flush();
				out.close();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.debug("[PublicAction] vilidateEmail,email has exists ,exception is {}",e.getMessage());
			}
		}
		else
		{
			PrintWriter out=null;
			try {
				out = response.getWriter();
				out.write("0");
				out.flush();
				out.close();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.debug("[PublicAction] vilidateEmail,email no exists ,exception is {}",e.getMessage());
			}
		}
	}

	/****
	 * 更新用户
	 * @return
	 */
	public String updateUser()
	{
		logger.debug("[PublicAction] updateUser ");
		String email=(String)this.request.getSession().getAttribute("userEmail");		
		if(email==null||email=="")
		{
			logger.debug("[PublicAction] updateUser fail,user not login");
			return "login";
		}
		try{
		//this.user=this.userServiceImpl.queryEmail(email);
//		String companyName=request.getParameter("user.companyName");
//		String contactMan=request.getParameter("user.contactMan");
//		String contactPhone=request.getParameter("user.contactPhone");
//		String address=this.request.getParameter("user.address");
//		user.setAddress(address);
//		user.setCompanyName(companyName);
//		user.setContactMan(contactMan);
//		user.setContactPhone(contactPhone);
		this.userServiceImpl.updateUser(this.user);
		}
		catch(Exception e)
		{
			logger.debug("[PublicAction] updateUser fail,exception is {}",e.getMessage());
			return "error";
			
		}
		logger.debug("[PublicAction] updateUser success");
		return SUCCESS;
	}
	
	/***
	 * 修改密码
	 * @return
	 */
	public String updatePass()
	{
		logger.debug("[PublicAction] updatePass ");
		String email=(String)this.request.getSession().getAttribute("userEmail");
		if(email==null||email=="")
		{
			logger.debug("[PublicAction] updateUser fail,user not login");
			return "login";
		}
		try{
			this.user=this.userServiceImpl.queryEmail(email);
			String npass=request.getParameter("newPass");
			user.setUserPass(npass);
			this.userServiceImpl.updateUser(user);
		}
		catch(Exception e)
		{
			logger.debug("[PublicAction] updatePass fail,exception is {}",e.getMessage());
			return "error";
			
		}
		return SUCCESS;
	}
	
	/***
	 * 退出
	 * @return
	 */
	public String loginOut()
	{
		request.getSession().removeAttribute("userEmail");
		request.setAttribute("email", "");
		return "login";
	}
	
	public String resetPass()
	{

		logger.debug("[PublicAction] resetPass ");
		String email=request.getParameter("userEmail");
		String npass=request.getParameter("newPass");
		if(email==null||email=="")
		{
			logger.debug("[PublicAction] updateUser fail,user not login");
			return "login";
		}
		try{
			this.user=this.userServiceImpl.queryEmail(email);
		
			user.setUserPass(npass);
			this.userServiceImpl.updateUser(user);
		}
		catch(Exception e)
		{
			logger.debug("[PublicAction] updatePass fail,exception is {}",e.getMessage());
			return "error";
			
		}
		return SUCCESS;
	
	}
	
	/***
	 * 产生随机码
	 */
	public void createImage()
	{

        response.setHeader("Pragma","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
       
        int width=100, height=30;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
       
        Graphics g = image.getGraphics();
        Random random = new Random();
        g.setColor(getRandColor(200,250));
        g.fillRect(1, 1, width-1, height-1);
        g.setColor(new Color(102,102,102));
        g.drawRect(0, 0, width-1, height-1);
        g.setFont(mFont);

        g.setColor(getRandColor(160,200));
        //干扰线:(循环的画出细小的线条)
        for (int i=0;i<155;i++)
        {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g.drawLine(x,y,x + xl,y + yl);
        }
        //干扰线:(循环的画出细小的线条)
        for (int i = 0;i < 70;i++)
        {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int xl = random.nextInt(12) + 1;
            int yl = random.nextInt(6) + 1;
            g.drawLine(x,y,x - xl,y - yl);
        }

        String sRand="";
        //绘制字母
        for (int i=0;i<4;i++)
        {
        	String tmp = getRandomChar();
            sRand += tmp;
            g.setColor(new Color(20+random.nextInt(110),20+random.nextInt(110),20+random.nextInt(110)));
            g.drawString(tmp,15*i+10,15);
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("rand",sRand);
        g.dispose();
        try {
			ImageIO.write(image, "JPEG", response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private String getRandomChar()
		    {
		  int rand = (int)Math.round(Math.random() * 2);
		  long itmp = 0;
		  char ctmp = '\u0000';
		  switch (rand)
		  {
		   case 1:
		    itmp = Math.round(Math.random() * 25 + 65);
		    ctmp = (char)itmp;
		    return String.valueOf(ctmp);
		   case 2:
		    itmp = Math.round(Math.random() * 25 + 97);
		    ctmp = (char)itmp;
		    return String.valueOf(ctmp);
		   default :
		    itmp = Math.round(Math.random() * 9);
		    return String.valueOf(itmp);
		  }
	}
	private Color getRandColor(int fc,int bc)
    {
        Random random = new Random();
        if(fc>255) fc=255;
        if(bc>255) bc=255;
        int r=fc+random.nextInt(bc-fc);
        int g=fc+random.nextInt(bc-fc);
        int b=fc+random.nextInt(bc-fc);
        return new Color(r,g,b);
    }

	
	
	
	public void setUser(User user) {
		this.user = user;
	}

	public User getUser()
	{
		return this.user;
	}
	public void setUserServiceImpl(UserService userServiceImpl) {
		this.userServiceImpl = userServiceImpl;
	}

	public static void main(String[] args)
	{
		PublicAction p=new PublicAction();
		System.out.println(p.getRandomChar());
		p.createImage();
		//System.out.println(p.);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getManagerEmail() {
		return managerEmail;
	}

	public void setManagerEmail(String managerEmail) {
		this.managerEmail = managerEmail;
	}

	public String getManagerPass() {
		return managerPass;
	}

	public void setManagerPass(String managerPass) {
		this.managerPass = managerPass;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
}
