package com.isg.demo.controller.topic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.line.LineStyle;

@Service
@Transactional
public class TopicService {
	@Autowired
	private TopicRepository repo;

	public List<Topic> listAll() {
		return repo.findAll();
	}

	public void save(Topic topic) {
		repo.save(topic);
	}

	public Topic get(Long id) {
		return repo.findTopic(id);
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}

	public boolean createPdf(List<Topic> listTopic,String path,String filename) throws IOException {
		String outputFileName = path + filename;
		
		PDFont fontPlain = PDType1Font.HELVETICA;
        PDFont fontBold = PDType1Font.HELVETICA_BOLD;
        PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
        PDFont fontMono = PDType1Font.COURIER;

        
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        
        PDRectangle rect = page.getMediaBox();
       
        document.addPage(page);
      
        PDPageContentStream cos = new PDPageContentStream(document, page);

        //Dummy Table
        float margin = 50;
        // starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin ofcourse)
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

        boolean drawContent = true;
        float yStart = yStartNewPage;
        float bottomMargin = 70;
       
        float yPosition = 550;
        
        BaseTable table = new BaseTable(yPosition, yStartNewPage,
                bottomMargin, tableWidth, margin, document, page, true, drawContent);
        
        
        Row<PDPage> headerRow = table.createRow(50);
     
        Cell<PDPage> cell = headerRow.createCell(100, "TableTopic");
        cell.setFont(fontBold);
        cell.setFontSize(20);
     
        cell.setValign(VerticalAlignment.MIDDLE);
      
        table.addHeaderRow(headerRow);
		
        Row<PDPage> row = table.createRow(50);
        cell = row.createCell(30 , "id");
        

       
        cell = row.createCell(30 , "name");
        

     
        cell = row.createCell(30 , "description");
        
      
        for(Topic topic: listTopic){
        	
        	 row = table.createRow(20);
        	 
        	 cell = row.createCell(30,String.valueOf(topic.getId()));
        	 cell.setTextColor(java.awt.Color.BLACK);
        	 cell.setFontSize(15);
        	 
        	 cell = row.createCell(30,String.valueOf(topic.getName()));
        	 cell.setTextColor(java.awt.Color.BLACK);
        	 cell.setFontSize(15);
        	 
        	 cell = row.createCell(30,String.valueOf(topic.getDescription()));
        	 cell.setTextColor(java.awt.Color.BLACK);
        	 cell.setFontSize(15);
			 
        	
        	
        	
        	
        }
        table.draw();
        float tableHeight = table.getHeaderAndDataHeight();
        
        
    	cos.close();
    	document.save(outputFileName);
    	document.close();
        
        

	return true;
	}

	public boolean createexcel(List<Topic> listTopic, ServletContext context, HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		String filepath = "D:/Topic.xls";
		File file = new File(filepath);
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("sample sheet");
		sheet.setDefaultColumnWidth(30);
        
		

		 FileOutputStream op = new FileOutputStream(file);
		 HSSFRow row = sheet.createRow(0);
		 
		 HSSFCell id = row.createCell(0);
		 id.setCellValue("id");
		 
		 HSSFCell name = row.createCell(1); 
		 name.setCellValue("name");
		 
		 HSSFCell description = row.createCell(2);
		 description.setCellValue("description");
		
		 int i =0;
	        for(Topic topic: listTopic){
	        	HSSFRow bodyRow = sheet.createRow(i);
	        	
	        	HSSFCell idValue = bodyRow.createCell(0);
                idValue.setCellValue(topic.getId());
             
                
                HSSFCell nameValue = bodyRow.createCell(1);
                nameValue.setCellValue(topic.getName());
                
                HSSFCell descriptionValue = bodyRow.createCell(2);
                descriptionValue.setCellValue(topic.getDescription());

	        	
	        	
	        	i++;
	        	
	        }
		 
		 
	        workbook.write(op);
            op.flush();
            op.close();
            return true;

	}

	public boolean createCsv(List<Topic> listTopic, ServletContext context, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String filepath = "D:/Topic.csv";
		File file = new File(filepath);
		
		FileWriter outputfile = new FileWriter(file); 
		
		BufferedWriter writer = new BufferedWriter(outputfile); 
		 CSVPrinter csvPrinter = new CSVPrinter( writer, CSVFormat.DEFAULT.withHeader("id", "name","Description"));
		
		 for(Topic topic: listTopic) {
			 
			 List<String> list = new ArrayList<>();
			 list.add(topic.getId().toString());
			 list.add(topic.getName());
			 list.add(topic.getDescription());
			 
			 csvPrinter.printRecord(list);
			 
		 }
		 
		 writer.flush();
	        writer.close();
		
		
		return true;
	}
}

	

//	public boolean createPdf(List<Topic> listTopic, ServletContext context, HttpServletRequest request, HttpServletResponse response) {
//		Document document = new Document(PageSize.A4);
//		PdfWriter writer = null;
//		try {
//			String filePath = "D:/";
//			File file = new File(filePath);
//			boolean exists = new File(filePath).exists();
//			if(!exists) {
//				new File(filePath).mkdirs();
//				
//			}
//			writer = PdfWriter.getInstance(document, new FileOutputStream("D:/topic2.pdf"));
//			document.open();
//			Font mainFont = FontFactory.getFont("Arial",10,BaseColor.BLACK);
//			
//			Paragraph paragraph = new Paragraph("All Topics",mainFont);
//			paragraph.setAlignment(Element.ALIGN_CENTER);
//			document.add(paragraph);
//			
//			PdfPTable table = new PdfPTable(3);
//			table.setWidthPercentage(100);
//			table.setSpacingBefore(10f);
//			table.setSpacingAfter(10f);
//			
//
//			 
//			 PdfPCell id = new PdfPCell(new Paragraph("id"));
//			 id.setBorderColor(BaseColor.BLACK);
//			 table.addCell(id);
//			 
//			 PdfPCell name = new PdfPCell(new Paragraph("name"));
//			 name.setBorderColor(BaseColor.BLACK);
//			 table.addCell(name);
//			 
//			 PdfPCell description = new PdfPCell(new Paragraph("description"));
//			 description.setBorderColor(BaseColor.BLACK);
//			 table.addCell(description);
//			 
//			 for(Topic topic: listTopic) {
//			
//				 PdfPCell idValue = new PdfPCell(new Paragraph(topic.getId()));
//				 idValue.setBorderColor(BaseColor.BLACK);
//				 idValue.setBackgroundColor(BaseColor.WHITE);
//				 table.addCell(idValue);
//				 
//				 PdfPCell nameValue = new PdfPCell(new Paragraph(topic.getName()));
//				 nameValue.setBorderColor(BaseColor.BLACK);
//				 nameValue.setBackgroundColor(BaseColor.WHITE);
//				 table.addCell(nameValue);
//				 
//				 PdfPCell descriptionValue = new PdfPCell(new Paragraph(topic.getDescription()));
//				 descriptionValue.setBorderColor(BaseColor.BLACK);
//				 descriptionValue.setBackgroundColor(BaseColor.WHITE);
//				 table.addCell(descriptionValue);
//				 
//				 
//			 }
//			 
//			 document.add(table);
//			 document.close();
//			 writer.close();
//			 
//			 return true;
//			 
//			
//			
//			
//			
//		}catch (Exception e) {
//			// TODO: handle exception
//			return false;
//		}
//		
//		
//		
//		
//	}
