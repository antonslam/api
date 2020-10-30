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

    public boolean SaveFile(){

        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 15;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        generatedString = "files/"+generatedString;


        try (OutputStream stream = new FileOutputStream(generatedString+"."+this.type)) {
            byte[] data = Base64.decodeBase64(this.base);
            stream.write(data);
            this.saved_file = generatedString;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public String convert2PDF() throws IOException {
        java.io.File inputFile = new java.io.File(this.saved_file+"."+this.type);
        java.io.File outputFile = new java.io.File(this.saved_file+".pdf");

        OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
        try {
            connection.connect();
        }catch (Exception e){
            e.printStackTrace();
            inputFile.delete();
            connection = null;
        }

        if (connection != null) {
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);

            try {

                if (this.type.equals("docx")) {
                    DocumentFormat docx = new DocumentFormat("Microsoft Word 2007 XML", DocumentFamily.TEXT, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
                    converter.convert(inputFile, docx, outputFile, null);
                } else if (this.type.equals("xlsx")) {
                    DocumentFormat xlsx = new DocumentFormat("Microsoft Excel 2007 XML", DocumentFamily.SPREADSHEET, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
                    converter.convert(inputFile, xlsx, outputFile, null);
                } else {
                    converter.convert(inputFile, outputFile);
                }
            } catch (Exception e){
                e.printStackTrace();
                ERROR(connection, inputFile);
                connection = null;
            }

            if(connection != null) {
                connection.disconnect();

                String str = encodeFileToBase64Binary(outputFile);
                inputFile.delete();
                outputFile.delete();
                return str;
            }else{
                return "Ошибка конвертации";
            }

        }
        return "Ошибка OpenOfficeConnection";
    }

    private static String encodeFileToBase64Binary(File file) throws IOException {
        byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
        return new String(encoded, StandardCharsets.US_ASCII);
    }
    private static void ERROR(OpenOfficeConnection connection, File inputFile){
        connection.disconnect();
        inputFile.delete();
    }
}
