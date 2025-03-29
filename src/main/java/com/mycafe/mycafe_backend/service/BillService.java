package com.mycafe.mycafe_backend.service;

import java.io.FileOutputStream;
import java.util.Map;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycafe.mycafe_backend.constant.CafeConstant;
import com.mycafe.mycafe_backend.jwt.JwtFilter;
import com.mycafe.mycafe_backend.model.Bill;
import com.mycafe.mycafe_backend.repository.BillRepo;
import com.mycafe.mycafe_backend.utils.CafeUtils;

@Service
public class BillService {
    
    @Autowired
    private BillRepo billRepo;

    @Autowired
    private JwtFilter jwtFilter;

    public ResponseEntity<String> generateReport(Map<String,Object> requestMap) {
        try {
            String fileName;
            if(validateRequestMap(requestMap)){
                // have to understand this logic
                if(requestMap.containsKey("isGenerate") &&  !(boolean)requestMap.get("isGenerate")){
                  fileName=(String)requestMap.get("uuid");
                }else{
                  fileName=CafeUtils.getUUID();
                  requestMap.put("uuid", fileName);
                  insertBill(requestMap);
                }
               
                //make a report

                String data="Name: "+requestMap.get("name")+"\n"+
                            "Contact Number: "+requestMap.get("contactNumber")+"\n"+
                            "Email: "+requestMap.get("email")+"\n"+
                            "Payment Method: "+requestMap.get("paymentMethod");

                Document document=new Document();
                PdfWriter.getInstance(document,new FileOutputStream(CafeConstant.STORE_LOCATION+"\\"+fileName+".pdf")); 
                
                document.open();
                setRectangleInPdf(document);

                Paragraph header=new Paragraph("MY CAFE",getFont("header"));
                header.setAlignment(Element.ALIGN_CENTER);
                document.add(header);

                Paragraph dataParagraph=new Paragraph("\n"+data+"\n \n",getFont("data"));
                document.add(dataParagraph);
                
                //5 columns
                PdfPTable table=new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);
                
                JSONArray jsonArray = CafeUtils.getJsonArrayFromString((String)requestMap.get("productDetail"));
                //convert JsonArray into Map
                for(int i=0;i<jsonArray.length();i++){
                  addRow(table,CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }

                document.add(table);


                Paragraph footer=new Paragraph("\n Total : "+requestMap.get("totalAmount")
                +"\n \n"+"THANK YOU, please visit again.",getFont("data"));

                document.add(footer);
                document.close();

                return new ResponseEntity<>("\"uuid\":\""+fileName+"\"",HttpStatus.OK);

            }else{
               return CafeUtils.getResponseEntitty(CafeConstant.INVALID_DATA,HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    

    private void addRow(PdfPTable table, Map<String,Object> map){
      table.addCell((String)map.get("name"));
      table.addCell((String)map.get("category"));
      table.addCell((String)map.get("quantity"));
      table.addCell(Double.toString((Double)map.get("price")));
      table.addCell(Double.toString((Double)map.get("total")));
    }


    private void addTableHeader(PdfPTable table){
        Stream.of("NAME","CATEGORY","QUANTITY","PRICE","TOTAL AMOUNT")
              .forEach(columnTitle ->{
                PdfPCell header=new PdfPCell();
                header.setBackgroundColor(BaseColor.YELLOW);
                header.setBorderWidth(2);
                header.setPhrase(new Phrase(columnTitle));
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setVerticalAlignment(Element.ALIGN_CENTER);
                table.addCell(header);
              });
    }

    private void setRectangleInPdf(Document document) throws DocumentException{
      Rectangle rect=new Rectangle(577,825,18,15);
      rect.enableBorderSide(1);
      rect.enableBorderSide(2);
      rect.enableBorderSide(4);
      rect.enableBorderSide(8);
      rect.setBorderColor(BaseColor.BLACK);
      rect.setBorderWidth(1);
      document.add(rect);
    }

    private Font getFont(String type){
      switch (type) {
        case "header":
            Font headerFont=FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.MAGENTA);
            headerFont.setStyle(Font.BOLDITALIC);
            return headerFont;

        case "data":
            Font dataFont=FontFactory.getFont(FontFactory.TIMES_ROMAN,12,BaseColor.BLACK);
            dataFont.setStyle(Font.BOLD);
            return dataFont;
         
        default:
            return new Font();
      }
    }

    private boolean validateRequestMap(Map<String,Object> requestMap){
      return requestMap.containsKey("name") && 
             requestMap.containsKey("email") &&
             requestMap.containsKey("contactNumber") &&
             requestMap.containsKey("totalAmount") &&
             requestMap.containsKey("paymentMethod") &&
             requestMap.containsKey("productDetail");
    }


    private void insertBill(Map<String,Object> requestMap){
        try {
            Bill bill=new Bill();
            bill.setUuid((String)requestMap.get("uuid"));
            bill.setName((String)requestMap.get("name"));
            bill.setEmail((String)requestMap.get("email"));
            bill.setContactNumber((String)requestMap.get("contactNumber"));
            bill.setTotalAmount(Integer.parseInt((String)requestMap.get("totalAmount")));
            bill.setPaymentMethod((String)requestMap.get("paymentMethod"));
            bill.setProductDetail((String)requestMap.get("productDetail"));
            bill.setCreatedBy(jwtFilter.currentUser());
            billRepo.save(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
