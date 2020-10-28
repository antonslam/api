package com.tss_app.api.request_class;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFamily;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.*;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class convert {
    public String base;
    public String skey;
    public String type;
    String saved_file;


    public convert(String base, String skey, String type){
        this.base = base;
        this.type = type;
        this.skey = skey;
    }

    public boolean is_skey(){
        return this.skey.equals("tss_aaa");
    }

    public void SaveFile(){

        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 15;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        byte[] data = Base64.decodeBase64(this.base);
        try (OutputStream stream = new FileOutputStream(generatedString+"."+this.type)) {
            stream.write(data);
            this.saved_file = generatedString;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String convert2PDF() throws IOException {
        java.io.File inputFile = new java.io.File(this.saved_file+"."+this.type);
        java.io.File outputFile = new java.io.File(this.saved_file+".pdf");

        OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
        connection.connect();

        DocumentConverter converter = new OpenOfficeDocumentConverter(connection);

        if(this.type.equals("docx")) {
            DocumentFormat docx = new DocumentFormat("Microsoft Word 2007 XML", DocumentFamily.TEXT, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
            converter.convert(inputFile, docx, outputFile, null);
        }else{
            converter.convert(inputFile, outputFile);
        }
        connection.disconnect();

        String str = encodeFileToBase64Binary(outputFile);
        inputFile.delete();
        outputFile.delete();
        return str;
    }

    private static String encodeFileToBase64Binary(File file) throws IOException {
        byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
        return new String(encoded, StandardCharsets.US_ASCII);
    }
}
