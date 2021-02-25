/*
 * Author: Srinivas
 */
package com.pqike.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sun.misc.BASE64Encoder;

/**
 *
 * @author User
 */
@WebServlet("/Images")
public class ImageFetchServlet extends HttpServlet {

    private static final String IMAGE = "images";
    private static final String NAME = "names";
    private static final String IMAGEFOLDER = System.getProperty("catalina.base")+ File.separator + "images";
    private static final String THUMBNAILFOLDER = "thumbs";
    private static final String HIGHRESFOLDER = "highres";
    private static final String THUMBNAIL = "t";
    private static final String PRIMARY = "p";
    private static final String HIGHRES = "h";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String category = request.getParameter("category");
        String type = request.getParameter("type");
        String idString = request.getParameter("id");
        String imageUse = request.getParameter("use");
        String encString = request.getParameter("encoded");
        boolean done = false;
        //get index
        StringBuilder responseString = new StringBuilder();
        Integer id = null;
        boolean encode = false;
        BASE64Encoder encoder = new BASE64Encoder();
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {

        }
        try {
            encode = Boolean.parseBoolean(encString);
        } catch (Exception e) {

        }
        List<String> images = new ArrayList<String>();
        List<String> names = new ArrayList<String>();
        System.out.println("ImageFetch trying ");
        if (category == null || type == null || imageUse == null || id == null || imageUse == null || "".equals(category) || "".equals(type) || "".equals(imageUse)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("ImageFetch Bad Request ");
            return;
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        if (HIGHRES.equals(imageUse)) {
            Integer index = null;
            try {
                index = Integer.parseInt(request.getParameter("index"));
            } catch (NumberFormatException e) {

            }
            System.out.println("In image high ");
            //File f = new File(IMAGEFOLDER + File.separator + category + File.separator + type + File.separator + id + File.separator + HIGHRESFOLDER + File.separator + index + ".jpg");
            String path = IMAGEFOLDER + File.separator + category + File.separator + type + File.separator + id + File.separator + HIGHRESFOLDER + File.separator + index + ".jpg";
            System.out.println("High res path is " + path);
            if (encode) {
                //ImageIO.write(ImageIO.read(f), "jpg", bout);
                //images.add(encoder.encode(bout.toByteArray()));
            } else {
                OutputStream output = null;
                InputStream input = null;
                try {
                    
                    output = response.getOutputStream();
                    input = new FileInputStream( new File(path));
                    //ImageIO.write(ImageIO.read(input), "jpg", output);
//                    input = getServletContext().getResourceAsStream(path);
                    byte[] buffer = new byte[1024];
                    for (int length = 0; (length = input.read(buffer)) > 0;) {
                        output.write(buffer, 0, length);
                    }
                    done = true;
                } finally {
                    if (output != null) {
                        output.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                }
            }
        } else if (THUMBNAIL.equals(imageUse)) {
            System.out.println("ImageFetch in thumbnail ");
            File f = new File(IMAGEFOLDER + File.separator + category + File.separator + type + File.separator + id + File.separator + THUMBNAILFOLDER);
            for (File image : f.listFiles()) {
                System.out.println("ImageFetch listing files");
                if (image.isFile()) {
                    bout = new ByteArrayOutputStream();
                    System.out.println("Image fetch file name is " + image.getName());
                    ImageIO.write(ImageIO.read(image), "jpg", bout);
                    images.add('"' + encoder.encode(bout.toByteArray()) + '"');
                    names.add('"' + image.getName() + '"');
                }
            }
            done = false;
        } else if (PRIMARY.equals(imageUse)) {
            File f = new File(IMAGEFOLDER + File.separator + category + File.separator + type + File.separator + id + File.separator + "primary.jpg");
            if (encode) {
                ImageIO.write(ImageIO.read(f), "jpg", bout);
                images.add(encoder.encode(bout.toByteArray()));
            } else {
                OutputStream oOut = null;
                try {
                    oOut = response.getOutputStream();
                    ImageIO.write(ImageIO.read(f), "jpg", oOut);
                    done = true;
                } finally {
                    if (oOut != null) {
                        oOut.close();
                    }
                }
            }
        }
        if (!done) { //check for BASE64 string or not
            responseString.append("{");
            responseString.append('"');
            responseString.append(IMAGE);
            responseString.append('"');
            responseString.append(":");
            responseString.append(images);
            responseString.append(",");
            responseString.append('"');
            responseString.append(NAME);
            responseString.append('"');
            responseString.append(":");
            responseString.append(names);    
            responseString.append("}");
            PrintWriter out = null;
            try {
                out = response.getWriter();
                out.println(responseString.toString());
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
