package com.isg.demo.controller.topic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.pdf.codec.Base64.OutputStream;

@Controller

public class jspController {

	@Autowired
	private TopicService service;

	@Autowired
	private ServletContext context;

	@RequestMapping("/")
	public String viewHome(Model model) {
		List<Topic> listTopic = service.listAll();
		model.addAttribute("listTopic", listTopic);
		return "index";

	}

	@RequestMapping("/new")
	public String showNewProduct(Model model) {
		Topic topic = new Topic();
		model.addAttribute("topic", topic);
		return "new_topic.html";
	}

	@PostMapping(value = "/save")
	public String saveTopic(@ModelAttribute("topic") Topic topic) {
		service.save(topic);
		return "redirect:/";
	}

	@RequestMapping("/update/{id}")
	public ModelAndView updateTopic(@PathVariable(name = "id") long id) {
		ModelAndView mv = new ModelAndView("update_topic");
		Topic topic = service.get(id);
		System.out.println(topic);

		mv.addObject("topic", topic);
		return mv;

	}

	// Create PDF
	@RequestMapping(value = "/pdf")
	public void createpdf(HttpServletRequest request,HttpServletResponse response) throws IOException {

		List<Topic> listTopic = service.listAll();
		String path = "D:/";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HHmmss");
		String outputFileName = "Topic_" + LocalDateTime.now().format(formatter).toString() + ".pdf";
		boolean isFlag = service.createPdf(listTopic, path, outputFileName);
		if (isFlag) {
			
			filedownload(path, response, outputFileName);
		}
     
	}

	private void filedownload(String path, HttpServletResponse response, String filename) {
		File file = new File(path+filename);
		final int BUFFER_SIZE = 4096;
		if (file.exists()) {
			try {
				FileInputStream inputStream = new FileInputStream(file);
				String mimeType = context.getMimeType(path);
				response.setContentType(mimeType);
				response.setHeader("content-disposition", "attachment;filename=" + filename);
				java.io.OutputStream outputStream = response.getOutputStream();
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead = -1;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				inputStream.close();
				outputStream.close();
				file.delete();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	
	//CreateExcel
	@RequestMapping(value="/excel")
	public void createexcel(HttpServletRequest request,HttpServletResponse response) throws IOException {
		List<Topic> listTopic = service.listAll();
		String filepath = "D:/";
		
		boolean isFlag = service.createexcel(listTopic, context, request,response);
		if (isFlag) {
			
			filedownloadexcel(filepath, response, "Topic.xls");
		}
	}
	private void filedownloadexcel(String path, HttpServletResponse response, String filename) {
		File file = new File(path+filename);
		final int BUFFER_SIZE = 4096;
		if (file.exists()) {
			try {
				FileInputStream inputStream = new FileInputStream(file);
				String mimeType = context.getMimeType(path);
				response.setContentType(mimeType);
				response.setHeader("content-disposition", "attachment;filename=" + filename);
				java.io.OutputStream outputStream = response.getOutputStream();
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead = -1;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				inputStream.close();
				outputStream.close();
				file.delete();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	//Create csv
	@RequestMapping(value="/csv")
	public void createCsv(HttpServletRequest request,HttpServletResponse response) throws IOException {
		List<Topic> listTopic = service.listAll();
		String filepath = "D:/";
		
		boolean isFlag = service.createCsv(listTopic, context, request,response);
		if (isFlag) {
			
			filedownloadcsv(filepath, response, "Topic.csv");
		}
		}
		
		private void filedownloadcsv(String path, HttpServletResponse response, String filename) {
			File file = new File(path+filename);
			final int BUFFER_SIZE = 4096;
			if (file.exists()) {
				try {
					FileInputStream inputStream = new FileInputStream(file);
					String mimeType = context.getMimeType(path);
					response.setContentType(mimeType);
					response.setHeader("content-disposition", "attachment;filename=" + filename);
					java.io.OutputStream outputStream = response.getOutputStream();
					byte[] buffer = new byte[BUFFER_SIZE];
					int bytesRead = -1;
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}
					inputStream.close();
					outputStream.close();
					file.delete();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}

}
