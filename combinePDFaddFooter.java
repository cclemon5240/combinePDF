package test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
/*
https://codertw.com/%E7%A8%8B%E5%BC%8F%E8%AA%9E%E8%A8%80/547722/
https://blog.csdn.net/z446981439/article/details/103994221
https://vimsky.com/zh-tw/examples/detail/java-class-com.itextpdf.text.pdf.ColumnText.html

iText Doc
https://api.itextpdf.com/iText5/java/5.5.13.2/
*/
public class combinePDFaddFooter {

	
	public static void main(String[] args) {
		System.out.println("Start!!!");
		String[] files = {"/Users/ricktseng/a.pdf", "/Users/ricktseng/createSamplePDF2.pdf"};
		try {
			Document document = new Document();
			PdfCopy copy = new PdfSmartCopy(document, new FileOutputStream("/Users/ricktseng/combinePDF.pdf"));
			PdfImportedPage page;
			PdfCopy.PageStamp stamp;
			document.open();
			Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);
			PdfReader[] reader = new PdfReader[3];
			for (int i = 0; i < files.length; i++) {
	            reader[i] = new PdfReader(files[i]);
	            copy.addDocument(reader[i]);
	            copy.freeReader(reader[i]);
	            reader[i].close();
	        }
			document.close();
			PdfReader reader2 = new PdfReader("/Users/ricktseng/combinePDF.pdf");
			copy = new PdfSmartCopy(document, new FileOutputStream("/Users/ricktseng/combinePDFaddFooter.pdf"));
			document.open();
			int pageNum = reader2.getNumberOfPages();
			for (int i = 1; i <= pageNum; i ++) {
				page = copy.getImportedPage(reader2, i);
				stamp = copy.createPageStamp(page);
				ColumnText.showTextAligned(stamp.getUnderContent(), Element.ALIGN_CENTER,new Phrase(String.format("page %d of %d", i, pageNum)),297.5f, 28, 0);
				//Header  
				float x = document.top(-10);
				ColumnText.showTextAligned(stamp.getUnderContent(), Element.ALIGN_RIGHT,new Phrase("202107/1008"),document.right(), x, 0);
				stamp.alterContents();
				copy.addPage(page);
			}
			reader2.close();
			document.close();
			System.out.println("Finish!!!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
