/*
 * Author: Srinivas
 */
package com.pqike.servlet;

import com.pqike.Utility.ItemFactory;
import com.pqike.Utility.PropertiesLoader;
import com.pqike.model.ClerkSessionStore;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author User
 */
@WebServlet("/Seller/Add")
public class AddServlet extends HttpServlet {

    private static final String PATHSEPERATOR = File.separator;
    private static final String CONTEXT = PropertiesLoader.getInstance().getProperty("in.shopperlane.context", true);
    private static final String SERVLETCONTEXT = PropertiesLoader.getInstance().getProperty("in.shopperlane.JSPContext", true);
    private static final String SUCCESS = "/" + CONTEXT + SERVLETCONTEXT + "/Add.jsp?category=%s&primecategory=%s&message=" + "Successfully Added";
    private static final String FAILED = "/" + CONTEXT + SERVLETCONTEXT + "/Add.jsp?category=%s&primecategory=%s&message=" + "Code-2 Item Not added. Enter all values and check if there are exsisting items in the specified skuid series.";

    private static final String THUMBNAILFOLDER = "thumbs";
    private static final String HIGHRESFOLDER = "highres";

    private static final String ELASTICINSERT = "http://shopperlane.in:8080/SearchShopperLane/AddItem?cat=%s&type=%s&title=%s&id=%s&merchant=%s";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        Integer clerkId = (Integer) request.getSession().getAttribute("_id");
        Integer merchant = ClerkSessionStore.getClerkSessionStore().getClerkMerchant(clerkId);
        String category = null;
        String type = null;
        if (!ServletFileUpload.isMultipartContent(request)) {
            System.out.println("Not multipart request");
            return;
        }
        boolean isPrimary = false;
        boolean result = false;
        OutputStream out = null;
        OutputStream thumbOut = null;
        InputStream filecontent = null;
        HashMap<String, String> values = new HashMap<String, String>();
        try {
            System.out.println("before add");
            ServletFileUpload upload = new ServletFileUpload();
            if (true) {
                FileItemIterator iter = upload.getItemIterator(request);
                int count = 0;
                String tempDir = "temp" + PATHSEPERATOR + request.getSession().getId() + (int) (Math.random() * 10);
                File tempDirec = new File(tempDir);
                tempDirec.mkdir();
                File tempHigRes = new File(tempDir + PATHSEPERATOR + HIGHRESFOLDER);
                File thumbnailFolder = new File(tempDir + PATHSEPERATOR + THUMBNAILFOLDER);
                tempHigRes.mkdir();
                thumbnailFolder.mkdir();
                while (iter.hasNext()) {
                    FileItemStream item = iter.next();
                    String name = item.getFieldName();
                    InputStream stream = null;
                    System.out.println("Got " + name);
                    try {
                        stream = item.openStream();
                        if (item.isFormField()) {

                            if ("category".equals(name)) {
                                category = Streams.asString(stream);
                            } else if ("primecategory".equals(name)) {
                                type = Streams.asString(stream);
                            } else {
                                if (values.get(name) == null) {
                                    values.put(name, Streams.asString(stream));
                                } else {
                                    String value = values.get(name) + "-" + Streams.asString(stream);
                                    values.put(name, value);
                                }
                            }
                        } else {
                            if ("primary-image".equals(name)) {
                                out = new FileOutputStream(tempDir + PATHSEPERATOR + "primary.jpg");
                                BufferedImage img = ImageIO.read(stream);
                                ImageIO.write(img, "jpg", out);
                                System.out.println("Saved " + tempDirec + " " + count);
                            }
                            if ("other-image".equals(name)) {
                                out = new FileOutputStream(tempDir + PATHSEPERATOR + HIGHRESFOLDER + PATHSEPERATOR + (++count) + ".jpg");
                                thumbOut = new FileOutputStream(tempDir + PATHSEPERATOR + THUMBNAILFOLDER + PATHSEPERATOR + (count) + ".jpg");
                                byte[] b = new byte[2048];
                                int length;
                                while ((length = stream.read(b)) != -1) {
                                    out.write(b, 0, length);
                                }
                                BufferedImage img = ImageIO.read(new File(tempDir + PATHSEPERATOR + HIGHRESFOLDER + PATHSEPERATOR + (count) + ".jpg"));
                                saveImage(img, thumbOut);
                                //ImageIO.write(img, "jpg", out);
                                System.out.println("Saved " + tempDirec + " " + count);
                            }
                        }
                    } finally {
                        if (stream != null) {
                            stream.close();
                        }
                        if (out != null) {
                            out.close();
                            out = null;
                        }
                        if (thumbOut != null) {
                            thumbOut.close();
                            thumbOut = null;
                        }
                    }
                }
                if (category == null || type == null) {
                    response.sendRedirect("Error");
                }
                /*if (!isPrimary) {
                 System.out.println("Primary image not found");
                 return;
                 }*/
                List<Integer> ids = ItemFactory.add(clerkId, category, type, values); //GET ID from add. Create directory Id and save the images.

                if (ids == null) {
                    System.out.println("Add Servlet Id is null");
                    FileUtils.deleteDirectory(tempDirec);
                    result = false;
                } else {
                    String baseDirectory = System.getProperty("catalina.base") + File.separator + "images" + PATHSEPERATOR + category + PATHSEPERATOR + type;
                    File directory = new File(baseDirectory);
                    if (!directory.exists()) {
                        if (directory.mkdirs()) {
                            System.out.println("Created directory .." + baseDirectory);
                        } else {
                            System.out.println("Unable to create directory .." + category + PATHSEPERATOR + type);
                        }
                    }

                    URL url = null;
                    HttpURLConnection conn = null;

                    for (Integer id : ids) {
                        File saveDirectory = new File(baseDirectory + PATHSEPERATOR + id);
                        System.out.println("Made directories " + saveDirectory.mkdirs());
                        FileUtils.copyDirectory(tempDirec, saveDirectory);
                        System.out.println("Title is " + values.get("title")); 

                    }
                    System.out.println("Copied directories");
                    FileUtils.deleteDirectory(tempDirec);
                    result = true;
                    response.sendRedirect(String.format(ELASTICINSERT, category, type, values.get("title"), ids.get(0), merchant));
                }
            }

            //Saving images to filesystem
            System.out.println("after add");
        } catch (Exception e) {
            System.out.println(e);
            Logger.getLogger(this.getClass() + "").log(Level.WARNING, e.getMessage());
            //response.sendRedirect("/" + CONTEXT + "/Add.jsp?category=" + category + "&primecategory=" + type + "&message=" + "Code-1 Item Not added. Enter all values and check if there are exsisting items in the specified skuid series.");
            result = false;
        }
        if (result) {

        } else {
            response.sendRedirect(String.format(FAILED, category, type));
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
        processRequest(request, response);
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

    private void saveImage(BufferedImage sourceImage, OutputStream out) throws Exception {
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        float extraSize = height - 300;
        System.out.println("ExtraSize is " + extraSize);
        float percentHight = (extraSize / height) * 100;
        float percentWidth = width - ((width / 100) * percentHight);
        System.out.println("PercentHeight " + percentHight);
        System.out.println("PercentWidth " + percentWidth);
        BufferedImage img = new BufferedImage((int) percentWidth, 300, BufferedImage.TYPE_INT_RGB);
        Image scaledImage = sourceImage.getScaledInstance((int) percentWidth, 300, Image.SCALE_SMOOTH);
        img.createGraphics().drawImage(scaledImage, 0, 0, Color.WHITE, null);
        //BufferedImage img2 = new BufferedImage(100, 100 ,BufferedImage.TYPE_INT_RGB);
        //img2 = img.getSubimage((int)((0)/2), 0, 100, 100);
        System.out.println(ImageIO.write(img, "jpg", out));
    }
}
