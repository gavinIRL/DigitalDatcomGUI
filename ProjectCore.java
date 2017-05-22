/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DatcomEditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
//import org.apache.commons.io.FileUtils;

public class ProjectCore {
    
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        

        Path currentRelativePath = Paths.get("");
        String relativePath = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + relativePath);
        System.out.println("------------------------");


        //Runner runner1 = new Runner();
        //runner1.relativePath = relativePath;
        //runner1.folder = "1";
        //runner1.start();

        //Runner runner2 = new Runner();
        //runner2.relativePath = relativePath;
        //runner2.folder = "2";
        //runner2.start();

        //Runner runner3 = new Runner();
        //runner3.relativePath = relativePath;
        //runner3.folder = "3";
        //runner3.start();

        //Runner runner4 = new Runner();
        //runner4.relativePath = relativePath;
        //runner4.folder = "4";
        //runner4.start();

        //for(int i=1;i<=10;i++) {
        //    createFolder(""+i);
        //}

        //ArrayList sampleData = readDCM(relativePath, "sample737.dcm");
        
        //createXML(relativePath, "1");
        
        //createDatcomFiles(relativePath, "1", sampleData);

        //deleteFolders(relativePath, "2");

        //readCSV(relativePath, "1");
        //DatcomEditor start = new DatcomEditor();
        
    }

    // Alternative way to run Datcom file from command prompt
    public static void runDatcomAlternate() {
        try {
            Runtime.getRuntime().exec("cmd.exe /c start a.dcm");
            System.out.println("ok");
            Runtime.getRuntime().exit(0);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // create the folders
    public static void createFolder(String path) {
        File dir = new File(path);
        dir.mkdir();

    }

    public static void createDatcomFiles(String path, String folderName, ArrayList data) {
        try {
            // create the .dcm file
            File file = new File(path + "/" + folderName + "/" + folderName + ".dcm");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);

            for (int i = 0; i < data.size(); i++) {
                writer.write((String) data.get(i) + "\n");
                writer.flush();
            }
            writer.close();
            
            // create the associated modified .xml file
            
            

        } catch (IOException ex) {
            Logger.getLogger(ProjectCore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void createXML(String path, String folderName) {
        try {
            File file2 = new File(path + "/" + folderName + "/" + "Datcom.ini");
            file2.createNewFile();
            FileWriter writer2 = new FileWriter(file2);
            
            String xml1 = "[Outputs]";
            String xml2 = "AC = Off";
            String xml3 = "Display_AC = Off";
            String xml4 = "AC3D_fuselage_lines = Off";
            String xml5 = "Pause_At_End = Off";
            String xml6 = "Matlab_3D = Off";
            String xml7 = "JSBSim = Off";
            String xml8 = "Airfoil = Off";
            String xml9 = "Fuselage = Off";
            String xml10 = "CSV = On";
            String xml11 = "Old_CSV = Off";
            String xml12 = "Log = Off";
            writer2.write(xml1 + "\n" + xml2 + "\n" + xml3 + "\n" + xml4 + "\n" 
                    + xml5 + "\n" + xml6 + "\n" + xml7 + "\n" + xml8 + "\n" + 
                    xml9 + "\n" + xml10 + "\n" + xml11 + "\n" + xml12);
            writer2.flush();
            writer2.close();
        } catch (IOException ex) {
            Logger.getLogger(ProjectCore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // rewrite to take all files
    public static ArrayList readDCM(String path, String fileName) {
        ArrayList sampleData = new ArrayList();
        try {
            String thisLine = "";
            int count = 0;
            

            BufferedReader br = new BufferedReader(new FileReader("sample737.dcm"));
            while ((thisLine = br.readLine()) != null) {
                sampleData.add(thisLine);
                System.out.println(count + " " + sampleData.get(count));
                count++;
            }
            return sampleData;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProjectCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProjectCore.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        return sampleData;
    }
        
    }

    public static void readCSV(String path, String folderName) {
        try {
            FileReader reader = new FileReader(path + "/" + folderName + "/" + folderName + ".csv");
            BufferedReader bReader = new BufferedReader(reader);

            String line = bReader.readLine();
            ArrayList allData = new ArrayList();

            while (line != null) {
                String[] dataStringArray = line.split(",");

                for (int i = 0; i < dataStringArray.length; i++) {
                    allData.add(dataStringArray[i]);
                }

                line = bReader.readLine();
            }

            for (int i = 0; i < allData.size(); i++) {
                //System.out.println(i + " " + allData.get(i));
            }

            // use this example to extract the data of all files
            System.out.println("These are the values of CLq");
            for(int i=2; i<208;i=i+16) {
                System.out.println(i + " " + allData.get(i));
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProjectCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProjectCore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deleteFolders(String path, String folderName) {
        System.out.println(path + "/" + folderName);
        File toDelete = new File(path + "/" + folderName);
        //FileUtils.deleteQuietly(toDelete);

    }
}
