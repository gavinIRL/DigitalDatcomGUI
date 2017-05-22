package DatcomEditor;

import java.awt.Color;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

// these imports are for plotting and are to be removed for now
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartFrame;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.CategoryPlot;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.data.category.DefaultCategoryDataset;
/**
 *
 * @author Gavin Feeney 2016-2017
 */
class Runner extends Thread {

    String relativePath = "";
    String folder = "";

    @Override
    public void run() {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
                "start", folder + ".dcm");
        builder.directory(new File(relativePath + "/" + folder));
        //System.out.println("New Path is: " + builder.directory());
        try {
            Process p = builder.start();
        } catch (IOException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

public class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        initialisePanelStates();
        initialiseFuselageData();
        initialiseImportData();
        initialiseProvisional();
        initialiseSectionList();
        initialiseSynthsLines();
        hideMultiRun();
    }
    // Here goes the ArrayList initialisation for imported Data
    static ArrayList importedData = new ArrayList();
    // Here go all the ArrayList initialisations for fuselage input
    ArrayList fuselageDistance = new ArrayList();
    ArrayList fuselageRadius = new ArrayList();
    ArrayList fuselageCircumference = new ArrayList();
    ArrayList fuselageArea = new ArrayList();
    ArrayList fuselageUpper = new ArrayList();
    ArrayList fuselageLower = new ArrayList();
    // Here goes the arraylist for the body lines
    ArrayList bodyLines = new ArrayList();
    // Here goes the arraylist for the wing lines for running
    ArrayList wingLines = new ArrayList();
    // Here goes the arraylist for the horz stab lines
    ArrayList tailHorzLines = new ArrayList();
    // Here goes the arraylist for the vert stab lines
    ArrayList tailVertLines = new ArrayList();
    // Here goes the arraylist for the first control surface data
    ArrayList elevatorLines = new ArrayList();
    // Here goes the arraylist for the second control surface data
    ArrayList aileronLines = new ArrayList();
    // Here goes the arraylist for the third control surface data
    ArrayList flapLines = new ArrayList();
    // Here goes the arraylist for the prop data
    ArrayList propLines = new ArrayList();
    // Here goes the arraylist for the fin data
    ArrayList finLines = new ArrayList();
    // Here goes the arraylist for the flight conditions data
    ArrayList fltconLines = new ArrayList();
    // Here goes the arraylist for the synths data
    // Please note this arraylist is initialised with 
    ArrayList synthsLines = new ArrayList();
    // Here goes the arraylist for holding the provisional inputs 
    ArrayList provDatcomData = new ArrayList();
    // Here goes the string to send to inputreview class to display
    static String inputReviewToSend = "";
    // Here goes the arraylist to indicate which sections to include in dcm
    ArrayList sectionList = new ArrayList();
    ArrayList previousList = new ArrayList(); // for holding previous when switching body-only
    // Here goes the arraylist for the final input to be turned into dcm
    ArrayList finalDatcomData = new ArrayList();
    // Here goes the 4 arrays for the 4 csv file sections
    ArrayList csvBasic = new ArrayList();
    ArrayList csvFlap = new ArrayList();
    ArrayList csvAileron = new ArrayList();
    ArrayList csvElevator = new ArrayList();
    // Here go all the arraylists for plotting
    // These were used for testing and probably no longer useful
    static ArrayList alpha = new ArrayList();
    static ArrayList CL_wbh = new ArrayList();
    static ArrayList CLq_deg = new ArrayList();
    static ArrayList CLadot_deg = new ArrayList();
    static ArrayList CD_wbh = new ArrayList();
    static ArrayList CYbeta_deg = new ArrayList();
    static ArrayList CYp_deg = new ArrayList();
    static ArrayList Clbeta_deg = new ArrayList();
    static ArrayList Clp_deg = new ArrayList();
    static ArrayList Clr_deg = new ArrayList();
    static ArrayList CM_wbh = new ArrayList();
    static ArrayList CMq_deg = new ArrayList();
    static ArrayList CMadot_deg = new ArrayList();
    static ArrayList CNbeta_deg = new ArrayList();
    static ArrayList CNp_deg = new ArrayList();
    static ArrayList CNr_deg = new ArrayList();
    static ArrayList cgValues = new ArrayList();
    // here go all the variable initialisations
    final int zero = 0; // use for clarity only
    int increments = 1; // global variable used for multiple analysis 
    static int engineID = 0; // for import
    boolean currentSessionResults = false; // for output
    int previousBodyIndex = 0; // for saving data to previous index
    String lastAircraftName = "No last"; // initialised with a space as spaces removed from all generated names
    // here goes the flag used for analysis whether multiple analysis or not
    boolean flagMulti = false;

    // here go all the non-listener method declarations for iteration
    // creates a folder for multiple analysis
    public static void createFolder(String path) {
        File dir = new File(path);
        dir.mkdir();

    }

    // creates dcm files in created folders
    public static void createDatcomFiles(String path, String folderName, ArrayList data) {
        try {
            // create the datcom file first in the correct folder
            File file = new File(path + "/" + folderName + "/" + folderName + ".dcm");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);

            for (int i = 0; i < data.size(); i++) {
                writer.write((String) data.get(i) + "\n");
                writer.flush();
            }
            writer.close();

            // then create the .ini file in the same folder
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
            String xml11 = "Old_CSV = On";
            String xml12 = "Log = Off";
            writer2.write(xml1 + "\n" + xml2 + "\n" + xml3 + "\n" + xml4 + "\n"
                    + xml5 + "\n" + xml6 + "\n" + xml7 + "\n" + xml8 + "\n"
                    + xml9 + "\n" + xml10 + "\n" + xml11 + "\n" + xml12);
            writer2.flush();
            writer2.close();

        } catch (IOException ex) {
            Logger.getLogger(ProjectCore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // this method processes the old csv file output
    // and returns an arraylist which has removed all empty lines
    // and replaced the asterisks with 3 x's for easier management
    private ArrayList readOldCSV(File results) throws FileNotFoundException, IOException {
        ArrayList returnLines = new ArrayList();
        FileReader fr = new FileReader(results);
        BufferedReader br = new BufferedReader(fr);

        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            returnLines.add(currentLine);
            //System.out.println(currentLine);
        }
        //System.out.println("End of bufferedreader reading");

        fr.close();
        br.close();
        return returnLines;
    }

    // Attempts to extract data from imported .dcm file
    // Not fully working currently, just testing
    public static ArrayList readImport(File imported) throws FileNotFoundException, IOException {
        ArrayList rawData = new ArrayList(); // raw input

        FileReader fr = new FileReader(imported);
        BufferedReader br = new BufferedReader(fr);

        // collect all data in rawdata arraylist
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            rawData.add(currentLine);
        }

        // delete lines that are pure comment or parts of lines that are comment
        for (int i = 0; i < rawData.size(); i++) {
            currentLine = rawData.get(i).toString();
            if ((currentLine.contains("*"))) {
                rawData.remove(i);
                i--; // to make sure it doesn't skip a double comment

            }
        }
        for (int i = 0; i < rawData.size(); i++) {
            currentLine = rawData.get(i).toString();
            if ((currentLine.contains("#"))) {
                rawData.remove(i);
                i--; // to make sure it doesn't skip a double comment

            }
        }
        for (int i = 0; i < rawData.size(); i++) {
            currentLine = rawData.get(i).toString();
            if ((currentLine.contains("!"))) {
                int index = currentLine.indexOf("!");
                if (index > 0) {
                    currentLine = currentLine.substring(0, index);
                    rawData.set(i, currentLine);
                } else if (index == 0) {
                    rawData.remove(i);
                    i--; // to make sure it doesn't skip a double comment
                }

            }
        }

        // close the readers
        br.close();
        fr.close();

        // add everything to one string
        String allText = "";
        for (int i = 0; i < rawData.size(); i++) {
            allText = allText + rawData.get(i);
        }

        // remove all white space
        allText = allText.replaceAll("\\s+", "");
        // then divide up into sections between the dollar signs
        ArrayList sections = new ArrayList();
        boolean flag = false;
        int dollarIndex = 0;
        int nextDollar = 0;
        //int iteration = 1;
        while (flag == false) {
            dollarIndex = allText.indexOf("$", nextDollar); // first index
            int start = dollarIndex + 1;
            if (dollarIndex == -1) {
                flag = true;
            } else {
                nextDollar = allText.indexOf("$", start);
                String toAdd = allText.substring(start, nextDollar);
                sections.add(toAdd);
                nextDollar++;
            }
            //System.out.println("Iteration: " + iteration);
            //iteration++;
        }

        // then find the airfoil codes if any
        ArrayList airfoils = new ArrayList();
        for (int i = 0; i < rawData.size(); i++) {
            String line = rawData.get(i).toString();
            if (line.contains("NACA")) {
                airfoils.add(line);
                // System.out.println("Line number " + i + ":" + line);
            }
        }

        // then find engine code if any
        String engine = "";
        for (int i = 0; i < rawData.size(); i++) {
            String line = rawData.get(i).toString();
            if (line.contains("eng=")) {
                engine = line;
            }
        }
        // System.out.println("Engine line: " + engine);
        String engineName = identifyEngine(engine);

        // then add to appropriate strings
        // need to initiliase as all will be checked later
        String fltconData = "";
        String bodyData = "";
        String inertaData = "";
        String optinsData = "";
        String synthsData = "";
        String wgplnfData = "";
        String wgschrData = "";
        String vtplnfData = "";
        String htplnfData = "";
        String symflp1Data = "";
        String symflp2Data = "";
        String asyflpData = "";
        String jetpwr1Data = ""; // max 9 of these
        String jetpwr2Data = "";
        String jetpwr3Data = "";
        String jetpwr4Data = "";
        String jetpwr5Data = "";
        String jetpwr6Data = "";
        String jetpwr7Data = "";
        String jetpwr8Data = "";
        String jetpwr9Data = "";

        for (int i = 0; i < sections.size(); i++) {
            String contents = sections.get(i).toString();
            if (contents.startsWith("FLTCON")) {
                fltconData = contents;
            }
            if (contents.startsWith("INERTA")) {
                inertaData = contents;
            }
            if (contents.startsWith("OPTINS")) {
                optinsData = contents;
            }
            if (contents.startsWith("SYNTHS")) {
                synthsData = contents;
            }
            if (contents.startsWith("BODY")) {
                bodyData = contents;
            }
            if (contents.startsWith("WGPLNF")) {
                wgplnfData = contents;
            }
            if (contents.startsWith("WGSCHR")) {
                wgschrData = contents;
            }
            if (contents.startsWith("VTPLNF")) {
                vtplnfData = contents;
            }
            if (contents.startsWith("HTPLNF")) {
                htplnfData = contents;
            }
            if (contents.startsWith("SYMFLP")) {
                // could have more than one
                if (symflp1Data.length() < 1) {
                    symflp1Data = contents;
                } else {
                    symflp2Data = contents;
                }
            }
            if (contents.startsWith("ASYFLP")) {
                asyflpData = contents;
            }
            if (contents.startsWith("JETPWR")) {
                // can have up to 9
                if (jetpwr1Data.length() < 1) {
                    jetpwr1Data = contents;
                } else if (jetpwr2Data.length() < 1) {
                    jetpwr2Data = contents;
                } else if (jetpwr3Data.length() < 1) {
                    jetpwr3Data = contents;
                } else if (jetpwr4Data.length() < 1) {
                    jetpwr4Data = contents;
                } else if (jetpwr5Data.length() < 1) {
                    jetpwr5Data = contents;
                } else if (jetpwr6Data.length() < 1) {
                    jetpwr6Data = contents;
                } else if (jetpwr7Data.length() < 1) {
                    jetpwr7Data = contents;
                } else if (jetpwr8Data.length() < 1) {
                    jetpwr8Data = contents;
                } else {
                    jetpwr9Data = contents;
                }


            }
        }

        // then fill up the input review string in preparation for review window
        for (int i = 0; i < sections.size(); i++) {
            inputReviewToSend = inputReviewToSend + (sections.get(i).toString() + "\n");
        }
        inputReviewToSend = inputReviewToSend + "Engine: " + engineName;
        for (int i = 0; i < airfoils.size(); i++) {
            inputReviewToSend = inputReviewToSend + "\nAirfoil: " + airfoils.get(i).toString();
        }
        // System.out.println(inputReviewToSend);

        // then read the data, and add data to arraylists in correct places
        // will probably create methods for this

        // test print the sections data now
        //for (int i = 0; i < sections.size(); i++) {
        //    System.out.println(sections.get(i));
        //}
        importedData.set(0, "Test Entry");
        if (true == true) {
            // System.out.println("Working");
            return importedData;
        } else {
            return importedData;
        }
    }

    // Add the imported data to the appropriate windows
    // Not working currently, just testing
    public static void displayImport(ArrayList data) {
        //System.out.println(data.get(0));
    }

    // need to change to account for other files (is this even necessary???)
    // used in the multiple analyses sample, probably not as useful as readimport
    public static ArrayList readDCM(String path, String fileName) {
        ArrayList sampleData = new ArrayList();
        try {
            String thisLine = "";
            int count = 0;


            BufferedReader br = new BufferedReader(new FileReader("sample737.dcm"));
            while ((thisLine = br.readLine()) != null) {
                sampleData.add(thisLine);
                //System.out.println(count + " " + sampleData.get(count));
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

    // reads from the csv file datcom creates
    // stopgap measure, will be replaced by readOutput later
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

            // this is used to print out and find values in emergency
            for (int i = 0; i < allData.size(); i++) {
                //System.out.println(i + " " + allData.get(i));
            }

            // the following are for first block of data
            for (int i = 16; i < 208; i = i + 16) {
                alpha.add(allData.get(i));
            }
            for (int i = 17; i < 208; i = i + 16) {
                CL_wbh.add(allData.get(i));
            }
            for (int i = 18; i < 208; i = i + 16) {
                CLq_deg.add(allData.get(i));
            }
            for (int i = 19; i < 208; i = i + 16) {
                CLadot_deg.add(allData.get(i));
            }
            for (int i = 20; i < 208; i = i + 16) {
                CD_wbh.add(allData.get(i));
            }
            for (int i = 21; i < 208; i = i + 16) {
                CYbeta_deg.add(allData.get(i));
            }
            for (int i = 22; i < 208; i = i + 16) {
                CYp_deg.add(allData.get(i));
            }
            for (int i = 23; i < 208; i = i + 16) {
                Clbeta_deg.add(allData.get(i));
            }
            for (int i = 24; i < 208; i = i + 16) {
                Clp_deg.add(allData.get(i));
            }
            for (int i = 25; i < 208; i = i + 16) {
                Clr_deg.add(allData.get(i));
            }
            for (int i = 26; i < 208; i = i + 16) {
                CM_wbh.add(allData.get(i));
            }
            for (int i = 27; i < 208; i = i + 16) {
                CMq_deg.add(allData.get(i));
            }
            for (int i = 28; i < 208; i = i + 16) {
                CMadot_deg.add(allData.get(i));
            }
            for (int i = 29; i < 208; i = i + 16) {
                CNbeta_deg.add(allData.get(i));
            }
            for (int i = 30; i < 208; i = i + 16) {
                CNp_deg.add(allData.get(i));
            }
            for (int i = 31; i < 208; i = i + 16) {
                CNr_deg.add(allData.get(i));
            }

            //quick test by printing

            bReader.close();
            reader.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProjectCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProjectCore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // deletes the folders created for multiple analysis
    public static void deleteFolders(String path, String folderName) {
        //System.out.println(path + "/" + folderName);
        //File toDelete = new File(path + "/" + folderName);
        //boolean success = FileUtils.deleteQuietly(toDelete);
        // requires import org.apache.commons.io.FileUtils;
        //if(success == true) {
        //    System.out.println("File Was Deleted");
        //}
        //else {
        //    System.out.println("File Was Not Deleted");
        //}
    }

    // runs each datcom file in a separate thread for multiple analysis
    private void runDatcom(String relativePath, int folderCount) {

        for (int i = 1; i <= folderCount; i++) {
            Runner runner1 = new Runner();
            runner1.relativePath = relativePath;
            runner1.folder = "" + i;
            runner1.start();
        }

    }

    // initialises the fuselage arraylist with 20 zeros
    private void initialiseFuselageData() {
        double zeroDouble = 0.0;
        for (int i = 1; i <= 20; i++) {
            fuselageDistance.add(zeroDouble);
            fuselageRadius.add(zeroDouble);
            fuselageCircumference.add(zeroDouble);
            fuselageArea.add(zeroDouble);
            fuselageUpper.add(zeroDouble);
            fuselageLower.add(zeroDouble);
        }
    }

    // initialises the import data array with 120 zeros
    private void initialiseImportData() {
        for (int i = 1; i <= 120; i++) {
            importedData.add(zero); // provisionally 120 lines
        }
    }

    // initialises the provisional input array with 120 zeros
    private void initialiseProvisional() {
        for (int i = 1; i <= 120; i++) {
            provDatcomData.add(zero); // provisionally add 120 lines
        }
    }

    // initialises the sectionList array. 0 indicates no section data therefore ignore
    // index 0 indicates number of fuselage points (as n)
    // index 1 indicates whether there is a breakpoint or not (1 = not, 2 = bp)
    // index 2 indicates whether there is a horz stab (1 = true)
    // index 3 indicates whether there is a vert stab (1 = true)
    // index 4 indicates whether there is propulsion data, 1 being true
    // index 5 indicates whether there are flaps, 1 being true
    // index 6 indicates whether there are elevators and/or ailerons
    // for index 6, 1 is both, 2 is elev only, 3 is aileron only
    // index 7 indicates the number of angles of attack (as n)
    // index 8 is kept for utility for now, serves no purpose currently
    private void initialiseSectionList() {
        sectionList.add(20); // fuselage
        sectionList.add(2); // wing
        sectionList.add(1); // horz stab
        sectionList.add(1); // vert stab
        sectionList.add(zero); // prop
        sectionList.add(zero); // flaps
        sectionList.add(zero); // controls
        sectionList.add(20); // angle attack
        sectionList.add(zero); // utility
        previousList.add(zero);
        previousList.add(zero);
        previousList.add(zero);
        previousList.add(zero);
        previousList.add(zero);
        previousList.add(zero);
        previousList.add(zero);
        previousList.add(zero);
        previousList.add(zero);
    }

    /**
     * This method is called from within the constructor to initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupRun = new javax.swing.ButtonGroup();
        groupImport = new javax.swing.ButtonGroup();
        groupProp = new javax.swing.ButtonGroup();
        groupCSV = new javax.swing.ButtonGroup();
        groupJSBSim = new javax.swing.ButtonGroup();
        groupFuselagePlot = new javax.swing.ButtonGroup();
        groupAC3D = new javax.swing.ButtonGroup();
        groupDisplayAC3D = new javax.swing.ButtonGroup();
        groupAC3DFuselage = new javax.swing.ButtonGroup();
        groupMatlab = new javax.swing.ButtonGroup();
        groupLog = new javax.swing.ButtonGroup();
        groupOldCSV = new javax.swing.ButtonGroup();
        groupAirfoilPlot = new javax.swing.ButtonGroup();
        groupOutput = new javax.swing.ButtonGroup();
        groupDamp = new javax.swing.ButtonGroup();
        groupPartial = new javax.swing.ButtonGroup();
        pnlMainTab = new javax.swing.JTabbedPane();
        pnlInput = new javax.swing.JPanel();
        pnlInnerTabs = new javax.swing.JTabbedPane();
        pnlImport = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jButton13 = new javax.swing.JButton();
        btnImportOpen = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        radioButtonManual = new javax.swing.JRadioButton();
        radioButtonImport = new javax.swing.JRadioButton();
        btnImportReview = new javax.swing.JButton();
        jLabel62 = new javax.swing.JLabel();
        txtImportStatus = new javax.swing.JTextField();
        btnManualBegin = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        pnlBody = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        comboNumberStations = new javax.swing.JComboBox();
        jLabel31 = new javax.swing.JLabel();
        comboFtM1 = new javax.swing.JComboBox();
        jLabel32 = new javax.swing.JLabel();
        txtRadiusBody = new javax.swing.JTextField();
        comboFtM2 = new javax.swing.JComboBox();
        jLabel33 = new javax.swing.JLabel();
        txtHighestBody = new javax.swing.JTextField();
        comboFtM3 = new javax.swing.JComboBox();
        jLabel34 = new javax.swing.JLabel();
        txtLowestBody = new javax.swing.JTextField();
        comboFtM4 = new javax.swing.JComboBox();
        jLabel35 = new javax.swing.JLabel();
        txtAreaBody = new javax.swing.JTextField();
        comboFtM5 = new javax.swing.JComboBox();
        jLabel36 = new javax.swing.JLabel();
        txtCircumBody = new javax.swing.JTextField();
        comboFtM6 = new javax.swing.JComboBox();
        btnNextStation = new javax.swing.JButton();
        btnPreviousStation = new javax.swing.JButton();
        btnHelp1 = new javax.swing.JButton();
        btnClearBodyAll = new javax.swing.JButton();
        btnClearBodyCurrent = new javax.swing.JButton();
        btnSaveNextBody = new javax.swing.JButton();
        btnVisualBody = new javax.swing.JButton();
        jPanel23 = new javax.swing.JPanel();
        checkBodyOnly = new javax.swing.JCheckBox();
        jLabel29 = new javax.swing.JLabel();
        comboStation = new javax.swing.JComboBox();
        checkAutoCalculateBody = new javax.swing.JCheckBox();
        jLabel30 = new javax.swing.JLabel();
        comboShape = new javax.swing.JComboBox();
        jLabel26 = new javax.swing.JLabel();
        txtNoseLength = new javax.swing.JTextField();
        comboFtM7 = new javax.swing.JComboBox();
        jLabel147 = new javax.swing.JLabel();
        txtAfterbodyLength = new javax.swing.JTextField();
        comboFtM8 = new javax.swing.JComboBox();
        txtDistanceNose = new javax.swing.JTextField();
        pnlWing = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        comboWingType = new javax.swing.JComboBox();
        jLabel47 = new javax.swing.JLabel();
        txtRootChord = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        txtTipChord = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        txtSSpan = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        txtSweep = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        txtTwist = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        txtDihedral = new javax.swing.JTextField();
        comboLength1 = new javax.swing.JComboBox();
        comboLength2 = new javax.swing.JComboBox();
        comboLength3 = new javax.swing.JComboBox();
        comboLength4 = new javax.swing.JComboBox();
        comboLength5 = new javax.swing.JComboBox();
        comboLength6 = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();
        txtIncidence = new javax.swing.JTextField();
        comboLength7 = new javax.swing.JComboBox();
        jPanel10 = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        txtChordBreak = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        txtOutSpan = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        txtOutSweep = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        txtOutDihedral = new javax.swing.JTextField();
        comboLength8 = new javax.swing.JComboBox();
        comboLength9 = new javax.swing.JComboBox();
        comboLength10 = new javax.swing.JComboBox();
        comboLength11 = new javax.swing.JComboBox();
        jPanel11 = new javax.swing.JPanel();
        jLabel57 = new javax.swing.JLabel();
        comboNACA = new javax.swing.JComboBox();
        jLabel58 = new javax.swing.JLabel();
        txtAirfoilCode = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtHorzWingPos = new javax.swing.JTextField();
        txtVertWingPos = new javax.swing.JTextField();
        comboLength13 = new javax.swing.JComboBox();
        comboLength12 = new javax.swing.JComboBox();
        jLabel42 = new javax.swing.JLabel();
        txtSSPNE = new javax.swing.JTextField();
        comboLength14 = new javax.swing.JComboBox();
        jLabel107 = new javax.swing.JLabel();
        txtCHSTAT = new javax.swing.JTextField();
        jLabel108 = new javax.swing.JLabel();
        checkAutoSSPNE = new javax.swing.JCheckBox();
        jLabel109 = new javax.swing.JLabel();
        comboTypeWing = new javax.swing.JComboBox();
        jPanel19 = new javax.swing.JPanel();
        btnHelp2 = new javax.swing.JButton();
        btnSaveNextWing = new javax.swing.JButton();
        btnClearWing = new javax.swing.JButton();
        btnVisualWing = new javax.swing.JButton();
        pnlTail = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        btnSaveNextTail = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jPanel25 = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        txtRootChord1 = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        txtTipChord1 = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        txtSSpan1 = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        txtSweep1 = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        txtCHSTAT1 = new javax.swing.JTextField();
        comboLength15 = new javax.swing.JComboBox();
        comboLength16 = new javax.swing.JComboBox();
        comboLength17 = new javax.swing.JComboBox();
        comboLength18 = new javax.swing.JComboBox();
        jLabel72 = new javax.swing.JLabel();
        txtCHSTAT2 = new javax.swing.JTextField();
        jLabel99 = new javax.swing.JLabel();
        checkHorzStab = new javax.swing.JCheckBox();
        jLabel130 = new javax.swing.JLabel();
        jLabel131 = new javax.swing.JLabel();
        jLabel132 = new javax.swing.JLabel();
        txtDihedral1 = new javax.swing.JTextField();
        comboLength19 = new javax.swing.JComboBox();
        jPanel27 = new javax.swing.JPanel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        txtHorzWingPos2 = new javax.swing.JTextField();
        txtVertWingPos2 = new javax.swing.JTextField();
        comboLength25 = new javax.swing.JComboBox();
        comboLength26 = new javax.swing.JComboBox();
        jLabel77 = new javax.swing.JLabel();
        txtSSPNE2 = new javax.swing.JTextField();
        comboLength27 = new javax.swing.JComboBox();
        jLabel101 = new javax.swing.JLabel();
        checkAutoSSPNE1 = new javax.swing.JCheckBox();
        comboHorzNACA = new javax.swing.JComboBox();
        jLabel110 = new javax.swing.JLabel();
        txtHorzCode = new javax.swing.JTextField();
        jPanel31 = new javax.swing.JPanel();
        jLabel92 = new javax.swing.JLabel();
        txtRootChord2 = new javax.swing.JTextField();
        jLabel93 = new javax.swing.JLabel();
        txtTipChord2 = new javax.swing.JTextField();
        jLabel94 = new javax.swing.JLabel();
        txtSSpan2 = new javax.swing.JTextField();
        jLabel95 = new javax.swing.JLabel();
        txtSweep2 = new javax.swing.JTextField();
        jLabel97 = new javax.swing.JLabel();
        txtCant = new javax.swing.JTextField();
        comboLength28 = new javax.swing.JComboBox();
        comboLength29 = new javax.swing.JComboBox();
        comboLength30 = new javax.swing.JComboBox();
        comboLength31 = new javax.swing.JComboBox();
        comboLength33 = new javax.swing.JComboBox();
        jLabel98 = new javax.swing.JLabel();
        txtYV = new javax.swing.JTextField();
        comboLength34 = new javax.swing.JComboBox();
        jLabel100 = new javax.swing.JLabel();
        checkVertStab = new javax.swing.JCheckBox();
        checkTwinVert = new javax.swing.JCheckBox();
        jPanel32 = new javax.swing.JPanel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        txtHorzWingPos3 = new javax.swing.JTextField();
        txtVertWingPos3 = new javax.swing.JTextField();
        comboLength35 = new javax.swing.JComboBox();
        comboLength36 = new javax.swing.JComboBox();
        jLabel104 = new javax.swing.JLabel();
        txtSSPNE3 = new javax.swing.JTextField();
        comboLength37 = new javax.swing.JComboBox();
        jLabel105 = new javax.swing.JLabel();
        checkAutoSSPNE2 = new javax.swing.JCheckBox();
        comboVertNACA = new javax.swing.JComboBox();
        jLabel111 = new javax.swing.JLabel();
        txtVertCode = new javax.swing.JTextField();
        pnlProp = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        btnSaveNextProp = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jPanel33 = new javax.swing.JPanel();
        jLabel115 = new javax.swing.JLabel();
        txtRootChord3 = new javax.swing.JTextField();
        jLabel116 = new javax.swing.JLabel();
        txtTipChord3 = new javax.swing.JTextField();
        jLabel117 = new javax.swing.JLabel();
        txtSSpan3 = new javax.swing.JTextField();
        jLabel118 = new javax.swing.JLabel();
        txtSweep3 = new javax.swing.JTextField();
        jLabel120 = new javax.swing.JLabel();
        txtDihedral3 = new javax.swing.JTextField();
        comboLength38 = new javax.swing.JComboBox();
        comboLength39 = new javax.swing.JComboBox();
        comboLength40 = new javax.swing.JComboBox();
        comboLength41 = new javax.swing.JComboBox();
        comboLength43 = new javax.swing.JComboBox();
        jLabel121 = new javax.swing.JLabel();
        txtIncidence3 = new javax.swing.JTextField();
        comboLength44 = new javax.swing.JComboBox();
        jLabel122 = new javax.swing.JLabel();
        checkVertStab1 = new javax.swing.JCheckBox();
        checkTwinFin = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        btnDatcomEngine = new javax.swing.JRadioButton();
        btnNoProp = new javax.swing.JRadioButton();
        comboProp = new javax.swing.JComboBox();
        jLabel113 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        btnUserEngine = new javax.swing.JRadioButton();
        jLabel112 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        jLabel123 = new javax.swing.JLabel();
        jLabel124 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jLabel125 = new javax.swing.JLabel();
        jLabel127 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel128 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel129 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel15 = new javax.swing.JPanel();
        jLabel119 = new javax.swing.JLabel();
        comboNACA1 = new javax.swing.JComboBox();
        jLabel126 = new javax.swing.JLabel();
        txtAirfoilCode1 = new javax.swing.JTextField();
        pnlControls = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButton9 = new javax.swing.JButton();
        btnSaveNextControl = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        radioButtonAileron = new javax.swing.JRadioButton();
        radioButtonFlaps = new javax.swing.JRadioButton();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        radioButtonElevator = new javax.swing.JRadioButton();
        txtSpanfo = new javax.swing.JTextField();
        txtChrdfo = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        jLabel134 = new javax.swing.JLabel();
        jLabel137 = new javax.swing.JLabel();
        comboElevDef = new javax.swing.JComboBox();
        jLabel135 = new javax.swing.JLabel();
        txtSpanfi = new javax.swing.JTextField();
        jLabel136 = new javax.swing.JLabel();
        jLabel133 = new javax.swing.JLabel();
        txtChrdfi = new javax.swing.JTextField();
        comboElevType = new javax.swing.JComboBox();
        jComboBox6 = new javax.swing.JComboBox();
        jComboBox7 = new javax.swing.JComboBox();
        jComboBox8 = new javax.swing.JComboBox();
        jComboBox9 = new javax.swing.JComboBox();
        radioButtonNoCont = new javax.swing.JRadioButton();
        jPanel24 = new javax.swing.JPanel();
        txtEDef1 = new javax.swing.JTextField();
        txtEDef2 = new javax.swing.JTextField();
        jComboBox10 = new javax.swing.JComboBox();
        jLabel139 = new javax.swing.JLabel();
        jComboBox11 = new javax.swing.JComboBox();
        jLabel138 = new javax.swing.JLabel();
        jLabel140 = new javax.swing.JLabel();
        jLabel141 = new javax.swing.JLabel();
        jLabel142 = new javax.swing.JLabel();
        txtEDef3 = new javax.swing.JTextField();
        txtEDef4 = new javax.swing.JTextField();
        txtEDef5 = new javax.swing.JTextField();
        jComboBox12 = new javax.swing.JComboBox();
        jComboBox13 = new javax.swing.JComboBox();
        jComboBox14 = new javax.swing.JComboBox();
        jPanel26 = new javax.swing.JPanel();
        txtEDef6 = new javax.swing.JTextField();
        txtEDef7 = new javax.swing.JTextField();
        jComboBox15 = new javax.swing.JComboBox();
        jLabel143 = new javax.swing.JLabel();
        jComboBox16 = new javax.swing.JComboBox();
        jLabel144 = new javax.swing.JLabel();
        jLabel145 = new javax.swing.JLabel();
        jLabel146 = new javax.swing.JLabel();
        txtEDef8 = new javax.swing.JTextField();
        txtEDef9 = new javax.swing.JTextField();
        jComboBox17 = new javax.swing.JComboBox();
        jComboBox18 = new javax.swing.JComboBox();
        pnlOther = new javax.swing.JPanel();
        pnlAng2 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtAng13 = new javax.swing.JTextField();
        txtAng16 = new javax.swing.JTextField();
        txtAng11 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtAng20 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtAng14 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtAng18 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtAng12 = new javax.swing.JTextField();
        txtAng19 = new javax.swing.JTextField();
        txtAng17 = new javax.swing.JTextField();
        txtAng15 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        lbl11 = new javax.swing.JLabel();
        lbl12 = new javax.swing.JLabel();
        lbl13 = new javax.swing.JLabel();
        lbl14 = new javax.swing.JLabel();
        lbl15 = new javax.swing.JLabel();
        lbl16 = new javax.swing.JLabel();
        lbl17 = new javax.swing.JLabel();
        lbl18 = new javax.swing.JLabel();
        lbl19 = new javax.swing.JLabel();
        lbl20 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        txtAltitude = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        comboWeight1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        txtMach = new javax.swing.JTextField();
        comboAlt1 = new javax.swing.JComboBox();
        comboAng = new javax.swing.JComboBox();
        txtAircraftWeight = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        comboDegRad = new javax.swing.JComboBox();
        jPanel7 = new javax.swing.JPanel();
        txtAng1 = new javax.swing.JTextField();
        txtAng10 = new javax.swing.JTextField();
        txtAng9 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtAng4 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtAng2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtAng6 = new javax.swing.JTextField();
        txtAng3 = new javax.swing.JTextField();
        txtAng7 = new javax.swing.JTextField();
        txtAng8 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtAng5 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lbl1 = new javax.swing.JLabel();
        lbl2 = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        lbl4 = new javax.swing.JLabel();
        lbl5 = new javax.swing.JLabel();
        lbl6 = new javax.swing.JLabel();
        lbl7 = new javax.swing.JLabel();
        lbl8 = new javax.swing.JLabel();
        lbl9 = new javax.swing.JLabel();
        lbl10 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        btnHelpOther = new javax.swing.JButton();
        btnSaveAndRun = new javax.swing.JButton();
        btnClearOther = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        txtCentreGravity = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        checkCOGAutoCalc = new javax.swing.JCheckBox();
        btnAutoCalcCOG = new javax.swing.JButton();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        txtHorzCOG = new javax.swing.JTextField();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel106 = new javax.swing.JLabel();
        txtVertCOG = new javax.swing.JTextField();
        jComboBox4 = new javax.swing.JComboBox();
        pnlRun = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlMainRun = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        btnSingle = new javax.swing.JRadioButton();
        btnMulti = new javax.swing.JRadioButton();
        panelMultiRun = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        comboIterationParameter = new javax.swing.JComboBox();
        jLabel38 = new javax.swing.JLabel();
        txtLowerValue = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        txtUpperValue = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        txtIncrements = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnBeginIteration = new javax.swing.JButton();
        jPanel28 = new javax.swing.JPanel();
        txtAircraftName = new javax.swing.JTextField();
        btnQuickReview = new javax.swing.JButton();
        checkName = new javax.swing.JCheckBox();
        pnlAdditionalOptions = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel83 = new javax.swing.JLabel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jLabel84 = new javax.swing.JLabel();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jLabel85 = new javax.swing.JLabel();
        btnAC3DOff = new javax.swing.JRadioButton();
        btnAC3DOn = new javax.swing.JRadioButton();
        jLabel86 = new javax.swing.JLabel();
        btnAC3DDispOff = new javax.swing.JRadioButton();
        btnAC3DDispOn = new javax.swing.JRadioButton();
        jLabel87 = new javax.swing.JLabel();
        btnDispFuselageOff = new javax.swing.JRadioButton();
        btnDispFuselageOn = new javax.swing.JRadioButton();
        jLabel88 = new javax.swing.JLabel();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jLabel89 = new javax.swing.JLabel();
        jRadioButton9 = new javax.swing.JRadioButton();
        jRadioButton10 = new javax.swing.JRadioButton();
        jLabel90 = new javax.swing.JLabel();
        jRadioButton11 = new javax.swing.JRadioButton();
        jRadioButton12 = new javax.swing.JRadioButton();
        jLabel91 = new javax.swing.JLabel();
        jRadioButton13 = new javax.swing.JRadioButton();
        jRadioButton14 = new javax.swing.JRadioButton();
        jLabel148 = new javax.swing.JLabel();
        btnDampOff = new javax.swing.JRadioButton();
        btnDampOn = new javax.swing.JRadioButton();
        jLabel149 = new javax.swing.JLabel();
        btnPartOff = new javax.swing.JRadioButton();
        btnPartOn = new javax.swing.JRadioButton();
        pnlOutput = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        btnImportResults = new javax.swing.JButton();
        btnTextual = new javax.swing.JButton();
        btnGraphical = new javax.swing.JButton();
        btnOpenOutputLocation = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        rbtnCurrentSessionOutput = new javax.swing.JRadioButton();
        rbtnDifferentSessionOutput = new javax.swing.JRadioButton();
        btnOutputXLS = new javax.swing.JButton();
        jRadioButton15 = new javax.swing.JRadioButton();
        pnlQuickPlot = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        btnPlotData = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        comboAlpha = new javax.swing.JComboBox();
        jLabel65 = new javax.swing.JLabel();
        comboPlotY = new javax.swing.JComboBox();
        jLabel61 = new javax.swing.JLabel();
        comboPlotX = new javax.swing.JComboBox();
        menuBar = new javax.swing.JMenuBar();
        menuSaveOptions = new javax.swing.JMenu();
        menuSaveXLSX = new javax.swing.JMenuItem();
        menuSaveDCM = new javax.swing.JMenuItem();
        menuInsert = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Datcom+ Pro GUI");
        setResizable(false);

        pnlMainTab.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        pnlMainTab.setPreferredSize(new java.awt.Dimension(600, 1077));

        pnlInput.setPreferredSize(new java.awt.Dimension(600, 1034));

        pnlInnerTabs.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        pnlInnerTabs.setPreferredSize(new java.awt.Dimension(600, 1034));

        jButton13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton13.setText("Help With This Page");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        btnImportOpen.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnImportOpen.setText("Find File To Import");
        btnImportOpen.setEnabled(false);
        btnImportOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportOpenActionPerformed(evt);
            }
        });

        groupImport.add(radioButtonManual);
        radioButtonManual.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        radioButtonManual.setSelected(true);
        radioButtonManual.setText("Input Data Manually");
        radioButtonManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonManualActionPerformed(evt);
            }
        });

        groupImport.add(radioButtonImport);
        radioButtonImport.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        radioButtonImport.setText("Import File");
        radioButtonImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonManual)
                    .addComponent(radioButtonImport))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(radioButtonManual)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonImport))
        );

        btnImportReview.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnImportReview.setText("Review and Edit Imported Data");
        btnImportReview.setEnabled(false);
        btnImportReview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportReviewActionPerformed(evt);
            }
        });

        jLabel62.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel62.setText("File Import Status:");

        txtImportStatus.setEditable(false);
        txtImportStatus.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtImportStatus.setText("Manual Input Selected");

        btnManualBegin.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnManualBegin.setText("Begin Manual Input");
        btnManualBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManualBeginActionPerformed(evt);
            }
        });

        jButton25.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton25.setText("Full Application Guide");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImportOpen, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImportReview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(jLabel62)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtImportStatus))
                    .addComponent(btnManualBegin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnManualBegin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImportOpen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62)
                    .addComponent(txtImportStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImportReview)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton25)
                .addContainerGap(309, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlImportLayout = new javax.swing.GroupLayout(pnlImport);
        pnlImport.setLayout(pnlImportLayout);
        pnlImportLayout.setHorizontalGroup(
            pnlImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImportLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(334, Short.MAX_VALUE))
        );
        pnlImportLayout.setVerticalGroup(
            pnlImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImportLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(223, Short.MAX_VALUE))
        );

        pnlInnerTabs.addTab("Import", pnlImport);

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel28.setText("Number of Fuselage Stations:");

        comboNumberStations.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboNumberStations.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        comboNumberStations.setSelectedIndex(18);
        comboNumberStations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboNumberStationsActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel31.setText("Distance from Nose:");

        comboFtM1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboFtM1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel32.setText("Radius/Half of Max Width:");

        txtRadiusBody.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtRadiusBody.setText("0.0");

        comboFtM2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboFtM2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel33.setText("Highest Point:");

        txtHighestBody.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtHighestBody.setText("0.0");

        comboFtM3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboFtM3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel34.setText("Lowest Point:");

        txtLowestBody.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtLowestBody.setText("0.0");

        comboFtM4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboFtM4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel35.setText("Area:");

        txtAreaBody.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAreaBody.setText("0.0");

        comboFtM5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboFtM5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel36.setText("Circumference:");

        txtCircumBody.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCircumBody.setText("0.0");

        comboFtM6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboFtM6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        btnNextStation.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnNextStation.setText("Next Station =>");
        btnNextStation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextStationActionPerformed(evt);
            }
        });

        btnPreviousStation.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnPreviousStation.setText("<= Previous Station");
        btnPreviousStation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousStationActionPerformed(evt);
            }
        });

        btnHelp1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnHelp1.setText("Help With This Page");
        btnHelp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHelp1ActionPerformed(evt);
            }
        });

        btnClearBodyAll.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnClearBodyAll.setText("Clear All Station Data");

        btnClearBodyCurrent.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnClearBodyCurrent.setText("Clear Data For Current Station");
        btnClearBodyCurrent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearBodyCurrentActionPerformed(evt);
            }
        });

        btnSaveNextBody.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnSaveNextBody.setText("Save Data And Go To Next Page");
        btnSaveNextBody.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveNextBodyActionPerformed(evt);
            }
        });

        btnVisualBody.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnVisualBody.setText("View Visual Model Of Data Input");
        btnVisualBody.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVisualBodyActionPerformed(evt);
            }
        });

        checkBodyOnly.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        checkBodyOnly.setText("Body-Only Analysis");
        checkBodyOnly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBodyOnlyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(checkBodyOnly)
                .addGap(0, 228, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(checkBodyOnly)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel29.setText("Selected Fuselage Station:");

        comboStation.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboStation.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        comboStation.setEnabled(false);
        comboStation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboStationActionPerformed(evt);
            }
        });

        checkAutoCalculateBody.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        checkAutoCalculateBody.setText("Autocalculate Missing Values");
        checkAutoCalculateBody.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAutoCalculateBodyActionPerformed(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel30.setText("Nose and Tail Shape:");

        comboShape.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboShape.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Conical/Conical", "Conical/Ogive", "Ogive/Conical", "Ogive/Ogive" }));
        comboShape.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboShapeActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setText("Nose Length:");

        txtNoseLength.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtNoseLength.setText("0.0");

        comboFtM7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboFtM7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel147.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel147.setText("Afterbody Length:");

        txtAfterbodyLength.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAfterbodyLength.setText("0.0");

        comboFtM8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboFtM8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        txtDistanceNose.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDistanceNose.setText("0.0");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnHelp1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClearBodyAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClearBodyCurrent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSaveNextBody, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel13Layout.createSequentialGroup()
                                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel28)
                                            .addComponent(jLabel31))
                                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel13Layout.createSequentialGroup()
                                                .addGap(131, 131, 131)
                                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(comboFtM2, 0, 128, Short.MAX_VALUE)
                                                    .addComponent(comboFtM1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(comboFtM3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(comboFtM4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(comboFtM5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(comboFtM6, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                            .addGroup(jPanel13Layout.createSequentialGroup()
                                                .addGap(24, 24, 24)
                                                .addComponent(comboNumberStations, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel13Layout.createSequentialGroup()
                                        .addGap(257, 257, 257)
                                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtDistanceNose, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(txtRadiusBody, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtHighestBody, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtLowestBody, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtAreaBody, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtCircumBody, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel13Layout.createSequentialGroup()
                                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel13Layout.createSequentialGroup()
                                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel32)
                                                    .addComponent(jLabel33)
                                                    .addComponent(jLabel34)
                                                    .addComponent(jLabel35)
                                                    .addComponent(jLabel36)
                                                    .addComponent(btnPreviousStation, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel26)
                                                    .addComponent(jLabel30))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(jPanel13Layout.createSequentialGroup()
                                                .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(18, 18, 18)))
                                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(comboStation, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(comboShape, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(jPanel13Layout.createSequentialGroup()
                                                .addComponent(txtNoseLength, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(comboFtM7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(btnNextStation, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                            .addGroup(jPanel13Layout.createSequentialGroup()
                                                .addComponent(txtAfterbodyLength, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(comboFtM8, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                                .addGap(7, 7, 7))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(checkAutoCalculateBody)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnVisualBody, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel147)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28)
                            .addComponent(comboNumberStations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel31)
                            .addComponent(comboFtM1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDistanceNose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtRadiusBody, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboFtM2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel33)
                            .addComponent(txtHighestBody, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboFtM3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34)
                            .addComponent(txtLowestBody, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboFtM4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35)
                            .addComponent(txtAreaBody, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboFtM5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel36)
                            .addComponent(txtCircumBody, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboFtM6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkAutoCalculateBody)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29)
                            .addComponent(comboStation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNextStation)
                            .addComponent(btnPreviousStation)))
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(36, 36, 36)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(comboShape, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtNoseLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboFtM7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel147)
                    .addComponent(txtAfterbodyLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboFtM8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(btnVisualBody)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClearBodyCurrent)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClearBodyAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHelp1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSaveNextBody)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlBodyLayout = new javax.swing.GroupLayout(pnlBody);
        pnlBody.setLayout(pnlBodyLayout);
        pnlBodyLayout.setHorizontalGroup(
            pnlBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBodyLayout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlBodyLayout.setVerticalGroup(
            pnlBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBodyLayout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 64, Short.MAX_VALUE))
        );

        pnlInnerTabs.addTab("Main Body", pnlBody);

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel45.setText("Breakpoints:");

        comboWingType.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboWingType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Breakpoints", "1 Breakpoint" }));
        comboWingType.setSelectedIndex(1);
        comboWingType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboWingTypeActionPerformed(evt);
            }
        });

        jLabel47.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel47.setText("Root Chord:");

        txtRootChord.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel48.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel48.setText("Tip Chord:");

        txtTipChord.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel49.setText("Semi-Span:");

        txtSSpan.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel52.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel52.setText("Sweep Angle:");

        txtSweep.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel54.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel54.setText("Twist Angle:");

        txtTwist.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel55.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel55.setText("Dihedral Angle:");

        txtDihedral.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        comboLength1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        comboLength5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        comboLength6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel25.setText("Incidence Angle:");

        txtIncidence.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        comboLength7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel47)
                    .addComponent(jLabel48)
                    .addComponent(jLabel49)
                    .addComponent(jLabel52)
                    .addComponent(jLabel54)
                    .addComponent(jLabel55)
                    .addComponent(jLabel45))
                .addGap(32, 32, 32)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(comboWingType, 0, 1, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDihedral, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(txtTwist)
                            .addComponent(txtRootChord)
                            .addComponent(txtTipChord)
                            .addComponent(txtSSpan)
                            .addComponent(txtSweep)
                            .addComponent(txtIncidence))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(comboLength4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboLength5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboLength6, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboLength1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboLength2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboLength3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboLength7, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
            .addComponent(jLabel25)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(comboWingType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(txtRootChord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(txtTipChord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(txtSSpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(txtSweep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(txtTwist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55)
                    .addComponent(txtDihedral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(txtIncidence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel50.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel50.setText("Chord at Breakpoint:");

        txtChordBreak.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel51.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel51.setText("Outer Panel Span:");

        txtOutSpan.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel53.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel53.setText("Outer Panel Sweep:");

        txtOutSweep.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel56.setText("Outer Panel Dihedral:");

        txtOutDihedral.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        comboLength8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        comboLength11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel50)
                            .addComponent(jLabel51)
                            .addComponent(jLabel53))
                        .addGap(23, 23, 23)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtOutSpan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                            .addComponent(txtChordBreak, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtOutSweep, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel56)
                        .addGap(18, 18, 18)
                        .addComponent(txtOutDihedral)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(comboLength10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength11, javax.swing.GroupLayout.Alignment.LEADING, 0, 1, Short.MAX_VALUE)
                    .addComponent(comboLength9, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboLength8, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(txtChordBreak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(txtOutSpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(txtOutSweep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(txtOutDihedral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(108, Short.MAX_VALUE))
        );

        jLabel57.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel57.setText("Airfoil Type:");

        comboNACA.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboNACA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "NACA 1-Series", "NACA 4-Series", "NACA 5-Series", "NACA 6-Series" }));
        comboNACA.setSelectedIndex(1);

        jLabel58.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel58.setText("Airfoil Code:");

        txtAirfoilCode.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel58)
                    .addComponent(jLabel57))
                .addGap(52, 52, 52)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboNACA, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(txtAirfoilCode, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57)
                    .addComponent(comboNACA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(txtAirfoilCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel23.setText("Horizontal Distance Between Leading Edge And Nose:");

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel24.setText("Vertical Distance Between Leading Edge And Nose:");

        txtHorzWingPos.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtVertWingPos.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        comboLength13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength13.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel42.setText("Exposed Semi-Span:");

        txtSSPNE.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        comboLength14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboLength14.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel107.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel107.setText("Sweep Measured From:");

        txtCHSTAT.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel108.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel108.setText("% Chord");

        checkAutoSSPNE.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        checkAutoSSPNE.setText("Autocalculate");
        checkAutoSSPNE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAutoSSPNEActionPerformed(evt);
            }
        });

        jLabel109.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel109.setText("Wing Type:");

        comboTypeWing.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboTypeWing.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Straight Tapered (default)", "Double Delta (AR<3)", "Cranked (AR>3)" }));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(jLabel42)
                    .addComponent(jLabel107)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(jLabel109))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboTypeWing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(txtVertWingPos, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboLength13, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(txtHorzWingPos, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboLength12, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(txtSSPNE, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboLength14, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkAutoSSPNE))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(txtCHSTAT, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel108)))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(txtHorzWingPos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(txtVertWingPos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(txtSSPNE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkAutoSSPNE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel107)
                    .addComponent(txtCHSTAT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel108))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel109)
                    .addComponent(comboTypeWing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        btnHelp2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnHelp2.setText("Help With This Page");
        btnHelp2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHelp2ActionPerformed(evt);
            }
        });

        btnSaveNextWing.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnSaveNextWing.setText("Save Data And Go To Next Page");
        btnSaveNextWing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveNextWingActionPerformed(evt);
            }
        });

        btnClearWing.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnClearWing.setText("Clear All Wing Data");
        btnClearWing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearWingActionPerformed(evt);
            }
        });

        btnVisualWing.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnVisualWing.setText("View Visual Model Of Data Input");
        btnVisualWing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVisualWingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnHelp2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSaveNextWing, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClearWing, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVisualWing, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnVisualWing)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClearWing)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHelp2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSaveNextWing)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlWingLayout = new javax.swing.GroupLayout(pnlWing);
        pnlWing.setLayout(pnlWingLayout);
        pnlWingLayout.setHorizontalGroup(
            pnlWingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWingLayout.createSequentialGroup()
                .addGroup(pnlWingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlWingLayout.createSequentialGroup()
                        .addGroup(pnlWingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlWingLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlWingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(pnlWingLayout.createSequentialGroup()
                                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 214, Short.MAX_VALUE))
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlWingLayout.setVerticalGroup(
            pnlWingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlWingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 241, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(155, Short.MAX_VALUE))
        );

        pnlInnerTabs.addTab("Wing", pnlWing);

        jButton1.setText("Help With This Page");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnSaveNextTail.setText("Save Data and Go To Next Page");
        btnSaveNextTail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveNextTailActionPerformed(evt);
            }
        });

        jButton17.setText("Clear All Tail Data");

        jButton24.setText("View Visual Model Of Data Input");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSaveNextTail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                    .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSaveNextTail)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel66.setText("Root Chord:");

        jLabel67.setText("Tip Chord:");

        jLabel68.setText("Semi-Span:");

        jLabel69.setText("Sweep Angle:");

        jLabel70.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel70.setText("Sweep Measured From");

        jLabel71.setText("Horizontal Stabiliser:");

        comboLength15.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength16.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength17.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength18.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        jLabel72.setText("Vertical Stabiliser:");

        jLabel99.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel99.setText("Horizontal Stabiliser");

        checkHorzStab.setSelected(true);
        checkHorzStab.setText("Include");
        checkHorzStab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkHorzStabActionPerformed(evt);
            }
        });

        jLabel130.setText("%Chord");

        jLabel131.setText("%Chord");

        jLabel132.setText("Dihedral Angle:");

        comboLength19.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addComponent(jLabel99)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(checkHorzStab))
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel72)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel66)
                            .addComponent(jLabel67)
                            .addComponent(jLabel68)
                            .addComponent(jLabel69)
                            .addComponent(jLabel70)
                            .addComponent(jLabel71)
                            .addComponent(jLabel132))
                        .addGap(32, 32, 32)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDihedral1)
                            .addComponent(txtCHSTAT1, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(txtRootChord1, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(txtTipChord1, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(txtSSpan1, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(txtSweep1, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(txtCHSTAT2))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(comboLength18, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength15, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength16, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength17, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel130)
                        .addComponent(comboLength19, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel131))
                .addGap(0, 22, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel99)
                    .addComponent(checkHorzStab))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel66)
                    .addComponent(txtRootChord1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67)
                    .addComponent(txtTipChord1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel68)
                    .addComponent(txtSSpan1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(txtSweep1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel132)
                    .addComponent(txtDihedral1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel70)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel71)
                    .addComponent(txtCHSTAT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel130))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel72)
                    .addComponent(txtCHSTAT2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel131)))
        );

        jLabel75.setText("Horizontal Distance between Leading Edge and Nose:");

        jLabel76.setText("Vertical Distance Between Leading Edge and Nose:");

        comboLength25.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength26.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel77.setText("Exposed Semi-Span:");

        comboLength27.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel101.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel101.setText("Horizontal Stabiliser");

        checkAutoSSPNE1.setText("Autocalculate");
        checkAutoSSPNE1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAutoSSPNE1ActionPerformed(evt);
            }
        });

        comboHorzNACA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "NACA 1-Series", "NACA 4-Series", "NACA 5-Series", "NACA 6-Series" }));
        comboHorzNACA.setSelectedIndex(1);

        jLabel110.setText("Code:");

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel77)
                    .addComponent(jLabel76)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel27Layout.createSequentialGroup()
                                .addComponent(jLabel101)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(comboHorzNACA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel110))
                            .addComponent(jLabel75))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel27Layout.createSequentialGroup()
                                .addComponent(txtVertWingPos2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboLength25, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel27Layout.createSequentialGroup()
                                .addComponent(txtSSPNE2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboLength27, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(checkAutoSSPNE1))
                            .addGroup(jPanel27Layout.createSequentialGroup()
                                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtHorzCode, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtHorzWingPos2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboLength26, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel101)
                    .addComponent(comboHorzNACA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel110)
                    .addComponent(txtHorzCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel75)
                    .addComponent(txtHorzWingPos2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel76)
                    .addComponent(txtVertWingPos2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(txtSSPNE2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkAutoSSPNE1))
                .addContainerGap())
        );

        jLabel92.setText("Root Chord:");

        jLabel93.setText("Tip Chord:");

        jLabel94.setText("Semi-Span:");

        jLabel95.setText("Sweep Angle:");

        jLabel97.setText("Cant Angle:");

        txtCant.setEditable(false);
        txtCant.setBackground(new java.awt.Color(128, 128, 128));

        comboLength28.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength29.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength30.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength31.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        comboLength33.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        jLabel98.setText("Centreline to Root:");

        txtYV.setEditable(false);
        txtYV.setBackground(new java.awt.Color(128, 128, 128));

        comboLength34.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel100.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel100.setText("Vertical Stabiliser");

        checkVertStab.setSelected(true);
        checkVertStab.setText("Include");
        checkVertStab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkVertStabActionPerformed(evt);
            }
        });

        checkTwinVert.setText("Twin Vertical Stabiliser");
        checkTwinVert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkTwinVertActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel98)
            .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel31Layout.createSequentialGroup()
                    .addComponent(jLabel100)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                    .addComponent(checkVertStab))
                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel31Layout.createSequentialGroup()
                    .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel92)
                        .addComponent(jLabel93)
                        .addComponent(jLabel94)
                        .addComponent(jLabel95)
                        .addComponent(jLabel97))
                    .addGap(38, 38, 38)
                    .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtCant, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                        .addComponent(txtRootChord2, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                        .addComponent(txtTipChord2, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                        .addComponent(txtSSpan2, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                        .addComponent(txtSweep2, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                        .addComponent(txtYV))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(comboLength31, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength33, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength28, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength29, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength30, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength34, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addComponent(checkTwinVert)
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel100)
                    .addComponent(checkVertStab))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel92)
                    .addComponent(txtRootChord2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel93)
                    .addComponent(txtTipChord2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel94)
                    .addComponent(txtSSpan2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel95)
                    .addComponent(txtSweep2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkTwinVert)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel97)
                    .addComponent(txtCant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel98)
                    .addComponent(txtYV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        jLabel102.setText("Horizontal Distance between Leading Edge and Nose:");

        jLabel103.setText("Vertical Distance Between Leading Edge and Nose:");

        comboLength35.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength36.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel104.setText("Exposed Semi-Span:");

        comboLength37.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel105.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel105.setText("Vertical Stabiliser");

        checkAutoSSPNE2.setText("Autocalculate");
        checkAutoSSPNE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAutoSSPNE2ActionPerformed(evt);
            }
        });

        comboVertNACA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "NACA 1-Series", "NACA 4-Series", "NACA 5-Series", "NACA 6-Series" }));
        comboVertNACA.setSelectedIndex(1);

        jLabel111.setText("Code:");

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel104)
                    .addComponent(jLabel103)
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel32Layout.createSequentialGroup()
                                .addComponent(jLabel105)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(comboVertNACA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel111))
                            .addComponent(jLabel102))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel32Layout.createSequentialGroup()
                                .addComponent(txtVertWingPos3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboLength35, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel32Layout.createSequentialGroup()
                                .addComponent(txtSSPNE3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboLength37, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(checkAutoSSPNE2))
                            .addGroup(jPanel32Layout.createSequentialGroup()
                                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtVertCode, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtHorzWingPos3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboLength36, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel105)
                    .addComponent(comboVertNACA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel111)
                    .addComponent(txtVertCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel102)
                    .addComponent(txtHorzWingPos3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel103)
                    .addComponent(txtVertWingPos3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel104)
                    .addComponent(txtSSPNE3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkAutoSSPNE2))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlTailLayout = new javax.swing.GroupLayout(pnlTail);
        pnlTail.setLayout(pnlTailLayout);
        pnlTailLayout.setHorizontalGroup(
            pnlTailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTailLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTailLayout.createSequentialGroup()
                        .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(333, Short.MAX_VALUE))
            .addGroup(pnlTailLayout.createSequentialGroup()
                .addGroup(pnlTailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlTailLayout.setVerticalGroup(
            pnlTailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTailLayout.createSequentialGroup()
                .addGroup(pnlTailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(331, 331, 331))
        );

        pnlInnerTabs.addTab("Tail", pnlTail);

        jButton8.setText("Help With This Page");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        btnSaveNextProp.setText("Save Data And Go To Next Page");
        btnSaveNextProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveNextPropActionPerformed(evt);
            }
        });

        jButton18.setText("Clear All Fin And Propulsion Data");

        jLabel115.setText("Root Chord:");

        txtRootChord3.setEditable(false);
        txtRootChord3.setBackground(new java.awt.Color(128, 128, 128));

        jLabel116.setText("Tip Chord:");

        txtTipChord3.setEditable(false);
        txtTipChord3.setBackground(new java.awt.Color(128, 128, 128));

        jLabel117.setText("Semi-Span:");

        txtSSpan3.setEditable(false);
        txtSSpan3.setBackground(new java.awt.Color(128, 128, 128));

        jLabel118.setText("Sweep Angle:");

        txtSweep3.setEditable(false);
        txtSweep3.setBackground(new java.awt.Color(128, 128, 128));

        jLabel120.setText("Cant Angle:");

        txtDihedral3.setEditable(false);
        txtDihedral3.setBackground(new java.awt.Color(128, 128, 128));

        comboLength38.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength39.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength40.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboLength41.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        comboLength43.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        jLabel121.setText("Centreline to Root:");

        txtIncidence3.setEditable(false);
        txtIncidence3.setBackground(new java.awt.Color(128, 128, 128));

        comboLength44.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        jLabel122.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel122.setText("Ventral Fin");

        checkVertStab1.setText("Include");
        checkVertStab1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkVertStab1ActionPerformed(evt);
            }
        });

        checkTwinFin.setText("Twin Ventral Fin");
        checkTwinFin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkTwinFinActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel121)
            .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel33Layout.createSequentialGroup()
                    .addComponent(jLabel122)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkVertStab1))
                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel33Layout.createSequentialGroup()
                    .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel115)
                        .addComponent(jLabel116)
                        .addComponent(jLabel117)
                        .addComponent(jLabel118)
                        .addComponent(jLabel120))
                    .addGap(32, 32, 32)
                    .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtDihedral3, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                        .addComponent(txtRootChord3)
                        .addComponent(txtTipChord3)
                        .addComponent(txtSSpan3)
                        .addComponent(txtSweep3)
                        .addComponent(txtIncidence3))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(comboLength41, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength43, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength38, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength39, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength40, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboLength44, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addComponent(checkTwinFin)
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel122)
                    .addComponent(checkVertStab1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel115)
                    .addComponent(txtRootChord3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel116)
                    .addComponent(txtTipChord3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel117)
                    .addComponent(txtSSpan3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel118)
                    .addComponent(txtSweep3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkTwinFin)
                .addGap(8, 8, 8)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel120)
                    .addComponent(txtDihedral3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel121)
                    .addComponent(txtIncidence3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLength44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 3, Short.MAX_VALUE))
        );

        groupProp.add(btnDatcomEngine);
        btnDatcomEngine.setText("Use Datcom Engine Data");
        btnDatcomEngine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDatcomEngineActionPerformed(evt);
            }
        });

        btnNoProp.setSelected(true);
        btnNoProp.setText("No Propulsion");
        btnNoProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoPropActionPerformed(evt);
            }
        });

        comboProp.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AJ26-33_nozzle", "AJ26-33A", "avco_lycoming_t53", "BR710", "BR-725", "CF6-80C2", "CFM56", "CFM56_5", "direct", "Dr1_propeller", "electric_1mw", "electric147kW", "eng_io320", "eng_PegasusXc", "eng_RRhawk", "engIO360C", "engIO470D", "engIO540AB1A5", "engRRMerlin61", "engRRMerlinXII", "engtm601", "F100-PW-229", "F119-PW-1", "GE-CF6-80C2-B1F", "HamiltonStd6243A-3", "J33-A-35", "J52", "J69-T25", "J79-GE-11A", "J85-GE-5", "JT15D", "JT9D-3", "MerlinV1650", "Oberursel-UrII", "Olympus593Mrk610", "output.txt", "P51prop", "prop_75in2f", "prop_81in2v", "prop_Clark_Y7570", "prop_deHavilland5000", "prop_generic2f", "prop_PT6", "prop_SSZ", "prop30FP2B", "propC10v", "propC6v", "propC8v", "propDA-R352_6-123-F_2", "propHO-V373-D", "propHS139v", "PT6A-27", "PT6A-68", "PW125B", "R-1820-97", "RB211-524", "RL10", "RL10_nozzle", "RollsRoyce", "s64_rotor", "SRB", "SRB_nozzle", "SSME", "SSME_nozzle", "t56", "t56_prop", "T76", "Tay-620", "Tay-651", "test_turbine", "TRENT-900", "twin_pratt_and_whitney_t73", "vrtule2", "XLR99", "xlr99_nozzle", "Zenoah_G-26A" }));

        jLabel113.setText("Coefficient #2:");

        jTextField1.setBackground(new java.awt.Color(128, 128, 128));

        jTextField2.setBackground(new java.awt.Color(128, 128, 128));

        groupProp.add(btnUserEngine);
        btnUserEngine.setText("Enter Propulsion Data Manually");
        btnUserEngine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserEngineActionPerformed(evt);
            }
        });

        jLabel112.setText("Coefficient #1:");

        jLabel96.setText("Coefficient #3:");

        jLabel114.setText("Coefficient #4:");

        jLabel123.setText("Coefficient #5:");

        jLabel124.setText("Coefficient #6:");

        jTextField3.setBackground(new java.awt.Color(128, 128, 128));

        jTextField4.setBackground(new java.awt.Color(128, 128, 128));

        jTextField5.setBackground(new java.awt.Color(128, 128, 128));

        jTextField6.setBackground(new java.awt.Color(128, 128, 128));

        jLabel125.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel125.setText("Propulsion");

        jLabel127.setText("Coefficient #7:");

        jTextField7.setBackground(new java.awt.Color(128, 128, 128));

        jLabel128.setText("Coefficient #8:");

        jTextField8.setBackground(new java.awt.Color(128, 128, 128));

        jLabel129.setText("Coefficient #9:");

        jTextField9.setBackground(new java.awt.Color(128, 128, 128));

        jLabel27.setText("Number of Coefficients:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" }));
        jComboBox1.setSelectedIndex(8);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDatcomEngine)
                    .addComponent(btnUserEngine)
                    .addComponent(btnNoProp)
                    .addComponent(jLabel125)
                    .addComponent(jLabel127)
                    .addComponent(jLabel128)
                    .addComponent(jLabel129)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel27)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel114)
                            .addGap(43, 43, 43)
                            .addComponent(jTextField4))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel96)
                            .addGap(43, 43, 43)
                            .addComponent(jTextField3))
                        .addComponent(comboProp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel123)
                                .addComponent(jLabel124))
                            .addGap(43, 43, 43)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField6)
                                .addComponent(jTextField5)
                                .addComponent(jTextField7)
                                .addComponent(jTextField8)
                                .addComponent(jTextField9)))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel113)
                                .addComponent(jLabel112))
                            .addGap(43, 43, 43)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField1)
                                .addComponent(jTextField2)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel125)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNoProp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDatcomEngine)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUserEngine)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboProp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel112)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel113)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel96)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel114)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel123)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel124)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel127)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel128)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel129)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jLabel119.setText("Airfoil Type:");

        comboNACA1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "NACA 1-Series", "NACA 4-Series", "NACA 5-Series", "NACA 6-Series" }));
        comboNACA1.setSelectedIndex(1);

        jLabel126.setText("Airfoil Code:");

        txtAirfoilCode1.setEditable(false);
        txtAirfoilCode1.setBackground(new java.awt.Color(128, 128, 128));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel126)
                    .addComponent(jLabel119))
                .addGap(41, 41, 41)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(txtAirfoilCode1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(comboNACA1, 0, 125, Short.MAX_VALUE)))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel119)
                    .addComponent(comboNACA1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel126)
                    .addComponent(txtAirfoilCode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSaveNextProp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(102, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(jButton18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSaveNextProp)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlPropLayout = new javax.swing.GroupLayout(pnlProp);
        pnlProp.setLayout(pnlPropLayout);
        pnlPropLayout.setHorizontalGroup(
            pnlPropLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPropLayout.createSequentialGroup()
                .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 373, Short.MAX_VALUE))
        );
        pnlPropLayout.setVerticalGroup(
            pnlPropLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPropLayout.createSequentialGroup()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 251, Short.MAX_VALUE))
        );

        pnlInnerTabs.addTab("Fin, Propulsion", pnlProp);

        jButton9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton9.setText("Clear All Control Surfaces Data");

        btnSaveNextControl.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnSaveNextControl.setText("Save Data and Go To Next Page");
        btnSaveNextControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveNextControlActionPerformed(evt);
            }
        });

        jButton14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton14.setText("Get Help With This Page");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        radioButtonAileron.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        radioButtonAileron.setText("Include Ailerons");
        radioButtonAileron.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonAileronActionPerformed(evt);
            }
        });

        radioButtonFlaps.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        radioButtonFlaps.setText("Include Flaps");
        radioButtonFlaps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonFlapsActionPerformed(evt);
            }
        });

        jLabel79.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel79.setText("Work In Progress");

        jLabel80.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel80.setText("Work In Progress");

        radioButtonElevator.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        radioButtonElevator.setText("Include Elevators");
        radioButtonElevator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonElevatorActionPerformed(evt);
            }
        });

        txtSpanfo.setEditable(false);
        txtSpanfo.setBackground(new java.awt.Color(128, 128, 128));
        txtSpanfo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtChrdfo.setEditable(false);
        txtChrdfo.setBackground(new java.awt.Color(128, 128, 128));
        txtChrdfo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel78.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel78.setText("Elevator Type:");

        jLabel134.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel134.setText("Outer Chord:");

        jLabel137.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel137.setText("Number of Deflection angles:");

        comboElevDef.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboElevDef.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" }));
        comboElevDef.setSelectedIndex(8);
        comboElevDef.setEnabled(false);
        comboElevDef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboElevDefActionPerformed(evt);
            }
        });

        jLabel135.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel135.setText("Inner Span:");

        txtSpanfi.setEditable(false);
        txtSpanfi.setBackground(new java.awt.Color(128, 128, 128));
        txtSpanfi.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel136.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel136.setText("Outer Span:");

        jLabel133.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel133.setText("Inner Chord:");

        txtChrdfi.setEditable(false);
        txtChrdfi.setBackground(new java.awt.Color(128, 128, 128));
        txtChrdfi.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        comboElevType.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboElevType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Plain (default)", "Single slotted ", "Fowler ", "Double slotted ", "Split ", "Leading edge flap", "Leading edge slat", "Krueger" }));
        comboElevType.setEnabled(false);

        jComboBox6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jComboBox7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jComboBox8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jComboBox9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel78)
                            .addComponent(jLabel133)
                            .addComponent(jLabel134)
                            .addComponent(jLabel135)
                            .addComponent(jLabel136))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtChrdfi, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtChrdfo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBox6, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBox7, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(txtSpanfi, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox8, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(txtSpanfo, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox9, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(comboElevType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(radioButtonElevator)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel137)
                        .addGap(8, 8, 8)
                        .addComponent(comboElevDef, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radioButtonElevator)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78)
                    .addComponent(comboElevType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel133)
                    .addComponent(txtChrdfi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel134)
                    .addComponent(txtChrdfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel135)
                    .addComponent(txtSpanfi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel136)
                    .addComponent(txtSpanfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel137)
                    .addComponent(comboElevDef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        radioButtonNoCont.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        radioButtonNoCont.setSelected(true);
        radioButtonNoCont.setText("No Control Surfaces");
        radioButtonNoCont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonNoContActionPerformed(evt);
            }
        });

        txtEDef1.setEditable(false);
        txtEDef1.setBackground(java.awt.Color.gray);
        txtEDef1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEDef2.setEditable(false);
        txtEDef2.setBackground(java.awt.Color.gray);
        txtEDef2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jComboBox10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        jLabel139.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel139.setText("Angle#2:");

        jComboBox11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        jLabel138.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel138.setText("Angle#1:");

        jLabel140.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel140.setText("Angle#3:");

        jLabel141.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel141.setText("Angle#4:");

        jLabel142.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel142.setText("Angle#5:");

        txtEDef3.setEditable(false);
        txtEDef3.setBackground(new java.awt.Color(128, 128, 128));
        txtEDef3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEDef4.setEditable(false);
        txtEDef4.setBackground(new java.awt.Color(128, 128, 128));
        txtEDef4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEDef5.setEditable(false);
        txtEDef5.setBackground(java.awt.Color.gray);
        txtEDef5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jComboBox12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        jComboBox13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox13.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        jComboBox14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox14.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel138, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel139, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtEDef1, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                            .addComponent(txtEDef2)))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel141, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel140, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel142, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEDef4)
                            .addComponent(txtEDef3)
                            .addComponent(txtEDef5))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel138)
                    .addComponent(txtEDef1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel139)
                    .addComponent(txtEDef2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel140)
                    .addComponent(txtEDef3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel141)
                    .addComponent(txtEDef4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel142)
                    .addComponent(txtEDef5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        txtEDef6.setEditable(false);
        txtEDef6.setBackground(new java.awt.Color(128, 128, 128));
        txtEDef6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEDef7.setEditable(false);
        txtEDef7.setBackground(new java.awt.Color(128, 128, 128));
        txtEDef7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jComboBox15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox15.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        jLabel143.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel143.setText("Angle#7:");

        jComboBox16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox16.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        jLabel144.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel144.setText("Angle#6:");

        jLabel145.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel145.setText("Angle#8:");

        jLabel146.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel146.setText("Angle#9:");

        txtEDef8.setEditable(false);
        txtEDef8.setBackground(new java.awt.Color(128, 128, 128));
        txtEDef8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEDef9.setEditable(false);
        txtEDef9.setBackground(new java.awt.Color(128, 128, 128));
        txtEDef9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jComboBox17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox17.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        jComboBox18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox18.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel146, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel145, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel144, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel143, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtEDef8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(txtEDef7, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEDef6, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEDef9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel144)
                    .addComponent(txtEDef6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel143)
                    .addComponent(txtEDef7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel145)
                    .addComponent(txtEDef8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel146)
                    .addComponent(txtEDef9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 175, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSaveNextControl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radioButtonFlaps)
                            .addComponent(radioButtonAileron)
                            .addComponent(jLabel79)
                            .addComponent(jLabel80)
                            .addComponent(radioButtonNoCont))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(radioButtonNoCont)
                        .addGap(30, 30, 30)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(9, 9, 9)
                .addComponent(radioButtonAileron)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel79)
                .addGap(55, 55, 55)
                .addComponent(radioButtonFlaps)
                .addGap(43, 43, 43)
                .addComponent(jLabel80)
                .addGap(63, 63, 63)
                .addComponent(jButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSaveNextControl)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlControlsLayout = new javax.swing.GroupLayout(pnlControls);
        pnlControls.setLayout(pnlControlsLayout);
        pnlControlsLayout.setHorizontalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlControlsLayout.setVerticalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlsLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 86, Short.MAX_VALUE))
        );

        pnlInnerTabs.addTab("Control & Flaps", pnlControls);

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel20.setText("Angle 18:");

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel21.setText("Angle 19:");

        txtAng13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng13.setMinimumSize(new java.awt.Dimension(6, 60));

        txtAng16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng16.setMinimumSize(new java.awt.Dimension(6, 60));

        txtAng11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng11.setMinimumSize(new java.awt.Dimension(6, 60));

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel19.setText("Angle 17:");

        txtAng20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng20.setMinimumSize(new java.awt.Dimension(6, 60));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setText("Angle 13:");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setText("Angle 14:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setText("Angle 11:");

        txtAng14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng14.setMinimumSize(new java.awt.Dimension(6, 60));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel22.setText("Angle 20:");

        txtAng18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng18.setMinimumSize(new java.awt.Dimension(6, 60));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setText("Angle 12:");

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel17.setText("Angle 15:");

        txtAng12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng12.setMinimumSize(new java.awt.Dimension(6, 60));

        txtAng19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng19.setMinimumSize(new java.awt.Dimension(6, 60));

        txtAng17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng17.setMinimumSize(new java.awt.Dimension(6, 60));

        txtAng15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng15.setMinimumSize(new java.awt.Dimension(6, 60));

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel18.setText("Angle 16:");

        lbl11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl11.setText("deg");

        lbl12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl12.setText("deg");

        lbl13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl13.setText("deg");

        lbl14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl14.setText("deg");

        lbl15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl15.setText("deg");

        lbl16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl16.setText("deg");

        lbl17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl17.setText("deg");

        lbl18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl18.setText("deg");

        lbl19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl19.setText("deg");

        lbl20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl20.setText("deg");

        javax.swing.GroupLayout pnlAng2Layout = new javax.swing.GroupLayout(pnlAng2);
        pnlAng2.setLayout(pnlAng2Layout);
        pnlAng2Layout.setHorizontalGroup(
            pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAng2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jLabel13)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addGap(51, 51, 51)
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtAng19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(txtAng18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl11)
                    .addComponent(lbl12)
                    .addComponent(lbl13)
                    .addComponent(lbl14)
                    .addComponent(lbl15)
                    .addComponent(lbl16)
                    .addComponent(lbl17)
                    .addComponent(lbl18)
                    .addComponent(lbl19)
                    .addComponent(lbl20))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAng2Layout.setVerticalGroup(
            pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAng2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtAng11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtAng12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtAng13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtAng14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtAng15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtAng16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtAng17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtAng18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtAng19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAng2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtAng20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl20))
                .addContainerGap())
        );

        txtAltitude.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel43.setText("Aircraft Weight:");

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel44.setText("Altitiude:");

        comboWeight1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboWeight1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "lbs", "kg" }));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Mach Number:");

        txtMach.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        comboAlt1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboAlt1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        comboAng.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboAng.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        comboAng.setSelectedIndex(18);
        comboAng.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboAngActionPerformed(evt);
            }
        });

        txtAircraftWeight.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Number of Angles of Attack:");

        jLabel59.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel59.setText("Units:");

        comboDegRad.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        comboDegRad.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "deg", "rad" }));
        comboDegRad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboDegRadActionPerformed(evt);
            }
        });

        txtAng1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng1.setMinimumSize(new java.awt.Dimension(6, 60));

        txtAng10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng10.setMinimumSize(new java.awt.Dimension(6, 60));
        txtAng10.setName(""); // NOI18N

        txtAng9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng9.setMinimumSize(new java.awt.Dimension(6, 60));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Angle 1:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Angle 7:");

        txtAng4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng4.setMinimumSize(new java.awt.Dimension(6, 60));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("Angle 8:");

        txtAng2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng2.setMinimumSize(new java.awt.Dimension(6, 60));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Angle 4:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Angle 6:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Angle 3:");

        txtAng6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng6.setMinimumSize(new java.awt.Dimension(6, 60));

        txtAng3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng3.setMinimumSize(new java.awt.Dimension(6, 60));

        txtAng7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng7.setMinimumSize(new java.awt.Dimension(6, 60));

        txtAng8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng8.setMinimumSize(new java.awt.Dimension(6, 60));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Angle 2:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Angle 10:");

        txtAng5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAng5.setMinimumSize(new java.awt.Dimension(6, 60));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Angle 5:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Angle 9:");

        lbl1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl1.setText("deg");

        lbl2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl2.setText("deg");

        lbl3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl3.setText("deg");

        lbl4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl4.setText("deg");

        lbl5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl5.setText("deg");

        lbl6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl6.setText("deg");

        lbl7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl7.setText("deg");

        lbl8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl8.setText("deg");

        lbl9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl9.setText("deg");

        lbl10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl10.setText("deg");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addGap(116, 116, 116)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtAng7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(txtAng6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAng1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl1)
                    .addComponent(lbl2)
                    .addComponent(lbl3)
                    .addComponent(lbl4)
                    .addComponent(lbl5)
                    .addComponent(lbl6)
                    .addComponent(lbl7)
                    .addComponent(lbl8)
                    .addComponent(lbl9)
                    .addComponent(lbl10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtAng1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtAng2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtAng3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtAng4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtAng5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtAng6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtAng7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtAng8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtAng9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtAng10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl10))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel43)
                    .addComponent(jLabel44)
                    .addComponent(jLabel59))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtMach)
                    .addComponent(comboAng, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAircraftWeight)
                    .addComponent(txtAltitude)
                    .addComponent(comboDegRad, 0, 64, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboWeight1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboAlt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtMach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(txtAircraftWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboWeight1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(txtAltitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboAlt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboAng, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel59)
                    .addComponent(comboDegRad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnHelpOther.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnHelpOther.setText("Help With This Page");
        btnHelpOther.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHelpOtherActionPerformed(evt);
            }
        });

        btnSaveAndRun.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnSaveAndRun.setText("Save Data And Go To Run");
        btnSaveAndRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveAndRunActionPerformed(evt);
            }
        });

        btnClearOther.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnClearOther.setText("Clear All Miscellaneous Data");
        btnClearOther.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearOtherActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnHelpOther, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSaveAndRun, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClearOther, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(btnClearOther)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHelpOther)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSaveAndRun)
                .addContainerGap())
        );

        jLabel63.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel63.setText("Centre of Gravity:");

        txtCentreGravity.setEditable(false);
        txtCentreGravity.setBackground(new java.awt.Color(128, 128, 128));
        txtCentreGravity.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel64.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel64.setText("%");

        checkCOGAutoCalc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        checkCOGAutoCalc.setText("Autocalc COG Position Using %");
        checkCOGAutoCalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkCOGAutoCalcActionPerformed(evt);
            }
        });

        btnAutoCalcCOG.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAutoCalcCOG.setText("Go");
        btnAutoCalcCOG.setEnabled(false);
        btnAutoCalcCOG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutoCalcCOGActionPerformed(evt);
            }
        });

        jLabel73.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel73.setText("Distance from Nose to Centre of Gravity:");

        jLabel74.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel74.setText("Horizontal:");

        txtHorzCOG.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jComboBox3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        jLabel106.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel106.setText("Vertical:");

        txtVertCOG.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jComboBox4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ft", "m" }));

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel63)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCentreGravity, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel64))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(checkCOGAutoCalc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAutoCalcCOG))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel74)
                            .addComponent(jLabel106))
                        .addGap(42, 42, 42)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtHorzCOG, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                            .addComponent(txtVertCOG))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel73))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(txtCentreGravity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkCOGAutoCalc)
                    .addComponent(btnAutoCalcCOG))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel73)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel74)
                    .addComponent(txtHorzCOG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel106)
                    .addComponent(txtVertCOG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlOtherLayout = new javax.swing.GroupLayout(pnlOther);
        pnlOther.setLayout(pnlOtherLayout);
        pnlOtherLayout.setHorizontalGroup(
            pnlOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOtherLayout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlAng2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(201, Short.MAX_VALUE))
            .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlOtherLayout.setVerticalGroup(
            pnlOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOtherLayout.createSequentialGroup()
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlAng2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75))
        );

        pnlInnerTabs.addTab("Misc", pnlOther);

        javax.swing.GroupLayout pnlInputLayout = new javax.swing.GroupLayout(pnlInput);
        pnlInput.setLayout(pnlInputLayout);
        pnlInputLayout.setHorizontalGroup(
            pnlInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlInnerTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 949, Short.MAX_VALUE)
        );
        pnlInputLayout.setVerticalGroup(
            pnlInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlInnerTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 868, Short.MAX_VALUE)
        );

        pnlMainTab.addTab("Input", pnlInput);

        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        groupRun.add(btnSingle);
        btnSingle.setSelected(true);
        btnSingle.setText("Single Analysis");
        btnSingle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSingleActionPerformed(evt);
            }
        });

        groupRun.add(btnMulti);
        btnMulti.setText("Multiple Analysis");
        btnMulti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMultiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSingle)
                    .addComponent(btnMulti))
                .addGap(0, 79, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(btnSingle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMulti))
        );

        jLabel37.setText("Vary the value of");

        comboIterationParameter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "XCG", "Aspect Ratio", "CHRDTP Wing", "CHRDR Wing", "Mach Number", "Altitude", "ZCG", "SSPN Wing", "SSPNE Wing", "CHRDTP HorzStab", "CHRDR HorzStab", "SSPN HorzStab", "SSPNE HorzStab", "Fuselage Scale", "TWISTA Wing", "DHDADI Wing", "ALIW", "ZW", "XW", "ZH", "XH", "Airfoil Thickness", "Airfoil Camber" }));
        comboIterationParameter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboIterationParameterActionPerformed(evt);
            }
        });

        jLabel38.setText("from");

        jLabel39.setText("to");

        jLabel40.setText("Use");

        jLabel41.setText("evenly spaced increments.");

        javax.swing.GroupLayout panelMultiRunLayout = new javax.swing.GroupLayout(panelMultiRun);
        panelMultiRun.setLayout(panelMultiRunLayout);
        panelMultiRunLayout.setHorizontalGroup(
            panelMultiRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMultiRunLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelMultiRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMultiRunLayout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboIterationParameter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelMultiRunLayout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIncrements, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel41))
                    .addGroup(panelMultiRunLayout.createSequentialGroup()
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLowerValue, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUpperValue, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(87, 87, 87))
        );
        panelMultiRunLayout.setVerticalGroup(
            panelMultiRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMultiRunLayout.createSequentialGroup()
                .addGroup(panelMultiRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(comboIterationParameter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMultiRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(txtLowerValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(txtUpperValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMultiRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(txtIncrements, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnBeginIteration.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnBeginIteration.setText("Run Datcom");
        btnBeginIteration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBeginIterationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnBeginIteration, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBeginIteration)
                .addContainerGap())
        );

        txtAircraftName.setText("SampleAircraftName");

        btnQuickReview.setText("Quick Review All Input Data");
        btnQuickReview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuickReviewActionPerformed(evt);
            }
        });

        checkName.setSelected(true);
        checkName.setText("Include Aircraft Name:");
        checkName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnQuickReview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addComponent(checkName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAircraftName)))
                .addContainerGap())
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAircraftName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnQuickReview)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel28, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(panelMultiRun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(254, 254, 254))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMultiRun, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(140, 140, 140))
        );

        javax.swing.GroupLayout pnlMainRunLayout = new javax.swing.GroupLayout(pnlMainRun);
        pnlMainRun.setLayout(pnlMainRunLayout);
        pnlMainRunLayout.setHorizontalGroup(
            pnlMainRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainRunLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 339, Short.MAX_VALUE))
        );
        pnlMainRunLayout.setVerticalGroup(
            pnlMainRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainRunLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(483, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Run", pnlMainRun);

        pnlAdditionalOptions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlAdditionalOptionsMousePressed(evt);
            }
        });

        jPanel30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel30MousePressed(evt);
            }
        });

        jLabel81.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel81.setText("Single Analysis");

        jLabel82.setText("Generate .csv File");

        groupCSV.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Off");

        groupCSV.add(jRadioButton2);
        jRadioButton2.setText("On");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jLabel83.setText("Generate JSBSim File");

        groupJSBSim.add(jRadioButton3);
        jRadioButton3.setSelected(true);
        jRadioButton3.setText("Off");

        groupJSBSim.add(jRadioButton4);
        jRadioButton4.setText("On");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        jLabel84.setText("Generate Fuselage Plots");

        groupFuselagePlot.add(jRadioButton5);
        jRadioButton5.setSelected(true);
        jRadioButton5.setText("Off");

        groupFuselagePlot.add(jRadioButton6);
        jRadioButton6.setText("On");
        jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton6ActionPerformed(evt);
            }
        });

        jLabel85.setText("Generate AC3D File");

        groupAC3D.add(btnAC3DOff);
        btnAC3DOff.setSelected(true);
        btnAC3DOff.setText("Off");
        btnAC3DOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAC3DOffActionPerformed(evt);
            }
        });

        groupAC3D.add(btnAC3DOn);
        btnAC3DOn.setText("On");
        btnAC3DOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAC3DOnActionPerformed(evt);
            }
        });

        jLabel86.setText("Display AC3D File");

        groupDisplayAC3D.add(btnAC3DDispOff);
        btnAC3DDispOff.setSelected(true);
        btnAC3DDispOff.setText("Off");

        groupDisplayAC3D.add(btnAC3DDispOn);
        btnAC3DDispOn.setText("On");
        btnAC3DDispOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAC3DDispOnActionPerformed(evt);
            }
        });

        jLabel87.setText("AC3D Fuselage Lines");

        groupAC3DFuselage.add(btnDispFuselageOff);
        btnDispFuselageOff.setSelected(true);
        btnDispFuselageOff.setText("Off");

        groupAC3DFuselage.add(btnDispFuselageOn);
        btnDispFuselageOn.setText("On");
        btnDispFuselageOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDispFuselageOnActionPerformed(evt);
            }
        });

        jLabel88.setText("Generate MATLAB 3D File");

        groupMatlab.add(jRadioButton7);
        jRadioButton7.setSelected(true);
        jRadioButton7.setText("Off");

        groupMatlab.add(jRadioButton8);
        jRadioButton8.setText("On");
        jRadioButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton8ActionPerformed(evt);
            }
        });

        jLabel89.setText("Generate Datcom Log");

        groupLog.add(jRadioButton9);
        jRadioButton9.setSelected(true);
        jRadioButton9.setText("Off");

        groupLog.add(jRadioButton10);
        jRadioButton10.setText("On");
        jRadioButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton10ActionPerformed(evt);
            }
        });

        jLabel90.setText("Generate Old .csv");

        groupOldCSV.add(jRadioButton11);
        jRadioButton11.setText("Off");
        jRadioButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton11ActionPerformed(evt);
            }
        });

        groupOldCSV.add(jRadioButton12);
        jRadioButton12.setSelected(true);
        jRadioButton12.setText("On");
        jRadioButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton12ActionPerformed(evt);
            }
        });

        jLabel91.setText("Generate Airfoil Plots");

        groupAirfoilPlot.add(jRadioButton13);
        jRadioButton13.setSelected(true);
        jRadioButton13.setText("Off");

        groupAirfoilPlot.add(jRadioButton14);
        jRadioButton14.setText("On");
        jRadioButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton14ActionPerformed(evt);
            }
        });

        jLabel148.setText("Dynamic Derivatives:");

        groupDamp.add(btnDampOff);
        btnDampOff.setText("Off");
        btnDampOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDampOffActionPerformed(evt);
            }
        });

        groupDamp.add(btnDampOn);
        btnDampOn.setSelected(true);
        btnDampOn.setText("On");
        btnDampOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDampOnActionPerformed(evt);
            }
        });

        jLabel149.setText("Partial Outputs:");

        groupPartial.add(btnPartOff);
        btnPartOff.setSelected(true);
        btnPartOff.setText("Off");
        btnPartOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPartOffActionPerformed(evt);
            }
        });

        groupPartial.add(btnPartOn);
        btnPartOn.setText("On");
        btnPartOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPartOnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel83)
                    .addComponent(jLabel84)
                    .addComponent(jLabel82)
                    .addComponent(jLabel81)
                    .addComponent(jLabel85)
                    .addComponent(jLabel86)
                    .addComponent(jLabel87)
                    .addComponent(jLabel88)
                    .addComponent(jLabel89)
                    .addComponent(jLabel90)
                    .addComponent(jLabel91))
                .addGap(22, 22, 22)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addComponent(jRadioButton13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton14))
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addComponent(jRadioButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton12))
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addComponent(jRadioButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton10))
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addComponent(jRadioButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton8))
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addComponent(btnDispFuselageOff)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDispFuselageOn))
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addComponent(btnAC3DDispOff)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAC3DDispOn))
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addComponent(btnAC3DOff)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAC3DOn))
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addComponent(jRadioButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton6))
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addComponent(jRadioButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton2)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel148))
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addComponent(jRadioButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton4)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel149)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addComponent(btnPartOff)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPartOn))
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addComponent(btnDampOff)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDampOn)))))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel81)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnDampOff)
                        .addComponent(btnDampOn))
                    .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel82)
                        .addComponent(jRadioButton1)
                        .addComponent(jRadioButton2)
                        .addComponent(jLabel148)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnPartOff)
                        .addComponent(btnPartOn))
                    .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel83)
                        .addComponent(jRadioButton3)
                        .addComponent(jRadioButton4)
                        .addComponent(jLabel149)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel84)
                    .addComponent(jRadioButton5)
                    .addComponent(jRadioButton6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel85)
                    .addComponent(btnAC3DOff)
                    .addComponent(btnAC3DOn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel86)
                    .addComponent(btnAC3DDispOff)
                    .addComponent(btnAC3DDispOn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel87)
                    .addComponent(btnDispFuselageOff)
                    .addComponent(btnDispFuselageOn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel88)
                    .addComponent(jRadioButton7)
                    .addComponent(jRadioButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel89)
                    .addComponent(jRadioButton9)
                    .addComponent(jRadioButton10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel90)
                    .addComponent(jRadioButton11)
                    .addComponent(jRadioButton12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel91)
                    .addComponent(jRadioButton13)
                    .addComponent(jRadioButton14))
                .addContainerGap(103, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlAdditionalOptionsLayout = new javax.swing.GroupLayout(pnlAdditionalOptions);
        pnlAdditionalOptions.setLayout(pnlAdditionalOptionsLayout);
        pnlAdditionalOptionsLayout.setHorizontalGroup(
            pnlAdditionalOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAdditionalOptionsLayout.createSequentialGroup()
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 296, Short.MAX_VALUE))
        );
        pnlAdditionalOptionsLayout.setVerticalGroup(
            pnlAdditionalOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAdditionalOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(433, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Additional Options", pnlAdditionalOptions);

        javax.swing.GroupLayout pnlRunLayout = new javax.swing.GroupLayout(pnlRun);
        pnlRun.setLayout(pnlRunLayout);
        pnlRunLayout.setHorizontalGroup(
            pnlRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        pnlRunLayout.setVerticalGroup(
            pnlRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pnlMainTab.addTab("Run", pnlRun);

        btnImportResults.setText("Import Results For Analysis");
        btnImportResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportResultsActionPerformed(evt);
            }
        });

        btnTextual.setText("View Textual Output Analysis");
        btnTextual.setEnabled(false);
        btnTextual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTextualActionPerformed(evt);
            }
        });

        btnGraphical.setText("View Graphical Output Analysis");
        btnGraphical.setEnabled(false);

        btnOpenOutputLocation.setText("Open Output Location");
        btnOpenOutputLocation.setEnabled(false);

        jButton4.setText("Find File And Import For Analysis");
        jButton4.setEnabled(false);

        groupOutput.add(rbtnCurrentSessionOutput);
        rbtnCurrentSessionOutput.setSelected(true);
        rbtnCurrentSessionOutput.setText("Use Data From Current Session");
        rbtnCurrentSessionOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnCurrentSessionOutputActionPerformed(evt);
            }
        });

        groupOutput.add(rbtnDifferentSessionOutput);
        rbtnDifferentSessionOutput.setText("Open Different .out File");
        rbtnDifferentSessionOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnDifferentSessionOutputActionPerformed(evt);
            }
        });

        btnOutputXLS.setText("Save Output Data To .xls File");
        btnOutputXLS.setEnabled(false);
        btnOutputXLS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOutputXLSActionPerformed(evt);
            }
        });

        groupOutput.add(jRadioButton15);
        jRadioButton15.setText("Open Multiple .out Files");
        jRadioButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton15ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTextual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGraphical, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnOpenOutputLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtnCurrentSessionOutput)
                            .addComponent(rbtnDifferentSessionOutput))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(btnImportResults, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(btnOutputXLS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jRadioButton15)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnImportResults)
                    .addComponent(rbtnCurrentSessionOutput))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(rbtnDifferentSessionOutput))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(btnTextual)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGraphical)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOpenOutputLocation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOutputXLS)
                .addContainerGap())
        );

        jLabel60.setText("with respect to");

        btnPlotData.setText("Plot!");
        btnPlotData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlotDataActionPerformed(evt);
            }
        });

        jLabel46.setText("Plot the variation of");

        comboAlpha.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-4", "-2", "0", "2", "4", "8", "9", "10", "12", "14", "16", "18" }));

        jLabel65.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel65.setText("Quick Plot:");

        comboPlotY.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CL", "CLq", "CLadot", "CD", "CYbeta", "CYp", "Clbeta", "Clp", "Clr", "CM", "CMq", "CMadot", "CNp", "CNr" }));

        jLabel61.setText("at alpha=");

        comboPlotX.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "cg position", "placeholder" }));

        javax.swing.GroupLayout pnlQuickPlotLayout = new javax.swing.GroupLayout(pnlQuickPlot);
        pnlQuickPlot.setLayout(pnlQuickPlotLayout);
        pnlQuickPlotLayout.setHorizontalGroup(
            pnlQuickPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQuickPlotLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlQuickPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlQuickPlotLayout.createSequentialGroup()
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboPlotY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel60)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboPlotX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel61)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPlotData))
                    .addGroup(pnlQuickPlotLayout.createSequentialGroup()
                        .addComponent(jLabel65)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlQuickPlotLayout.setVerticalGroup(
            pnlQuickPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQuickPlotLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlQuickPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPlotData)
                    .addComponent(jLabel46)
                    .addComponent(comboPlotY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60)
                    .addComponent(comboPlotX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61)
                    .addComponent(comboAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlOutputLayout = new javax.swing.GroupLayout(pnlOutput);
        pnlOutput.setLayout(pnlOutputLayout);
        pnlOutputLayout.setHorizontalGroup(
            pnlOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOutputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnlQuickPlot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(345, Short.MAX_VALUE))
        );
        pnlOutputLayout.setVerticalGroup(
            pnlOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOutputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlQuickPlot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(493, Short.MAX_VALUE))
        );

        pnlMainTab.addTab("Output", pnlOutput);

        menuSaveOptions.setText("File");

        menuSaveXLSX.setText("New File");
        menuSaveXLSX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveXLSXActionPerformed(evt);
            }
        });
        menuSaveOptions.add(menuSaveXLSX);

        menuSaveDCM.setText("Save Input Session to .dcm");
        menuSaveDCM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveDCMActionPerformed(evt);
            }
        });
        menuSaveOptions.add(menuSaveDCM);

        menuBar.add(menuSaveOptions);

        menuInsert.setText("Insert");

        jMenuItem1.setText("Insert Sample 737-300 Values");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menuInsert.add(jMenuItem1);

        menuBar.add(menuInsert);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMainTab, javax.swing.GroupLayout.DEFAULT_SIZE, 954, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMainTab, javax.swing.GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void comboDegRadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboDegRadActionPerformed
        int index = comboDegRad.getSelectedIndex();
        switch (index) {
            case 0:
                lbl1.setText("deg");
                lbl2.setText("deg");
                lbl3.setText("deg");
                lbl4.setText("deg");
                lbl5.setText("deg");
                lbl6.setText("deg");
                lbl7.setText("deg");
                lbl8.setText("deg");
                lbl9.setText("deg");
                lbl10.setText("deg");
                lbl11.setText("deg");
                lbl12.setText("deg");
                lbl13.setText("deg");
                lbl14.setText("deg");
                lbl15.setText("deg");
                lbl16.setText("deg");
                lbl17.setText("deg");
                lbl18.setText("deg");
                lbl19.setText("deg");
                lbl20.setText("deg");
                break;
            case 1:
                lbl1.setText("rad");
                lbl2.setText("rad");
                lbl3.setText("rad");
                lbl4.setText("rad");
                lbl5.setText("rad");
                lbl6.setText("rad");
                lbl7.setText("rad");
                lbl8.setText("rad");
                lbl9.setText("rad");
                lbl10.setText("rad");
                lbl11.setText("rad");
                lbl12.setText("rad");
                lbl13.setText("rad");
                lbl14.setText("rad");
                lbl15.setText("rad");
                lbl16.setText("rad");
                lbl17.setText("rad");
                lbl18.setText("rad");
                lbl19.setText("rad");
                lbl20.setText("rad");
                break;
        }
    }//GEN-LAST:event_comboDegRadActionPerformed

    private void comboAngActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboAngActionPerformed
        // edit sectionlist arraylist
        int number = comboAng.getSelectedIndex();
        number = number + 2;
        sectionList.set(7, number);

        // reset so all are editable again
        txtAng3.setEditable(true);
        txtAng4.setEditable(true);
        txtAng5.setEditable(true);
        txtAng6.setEditable(true);
        txtAng7.setEditable(true);
        txtAng8.setEditable(true);
        txtAng9.setEditable(true);
        txtAng10.setEditable(true);
        txtAng11.setEditable(true);
        txtAng12.setEditable(true);
        txtAng13.setEditable(true);
        txtAng14.setEditable(true);
        txtAng15.setEditable(true);
        txtAng16.setEditable(true);
        txtAng17.setEditable(true);
        txtAng18.setEditable(true);
        txtAng19.setEditable(true);
        txtAng20.setEditable(true);

        // reset all colours to white
        txtAng3.setBackground(Color.white);
        txtAng4.setBackground(Color.white);
        txtAng5.setBackground(Color.white);
        txtAng6.setBackground(Color.white);
        txtAng7.setBackground(Color.white);
        txtAng8.setBackground(Color.white);
        txtAng9.setBackground(Color.white);
        txtAng10.setBackground(Color.white);
        txtAng11.setBackground(Color.white);
        txtAng12.setBackground(Color.white);
        txtAng13.setBackground(Color.white);
        txtAng14.setBackground(Color.white);
        txtAng15.setBackground(Color.white);
        txtAng16.setBackground(Color.white);
        txtAng17.setBackground(Color.white);
        txtAng18.setBackground(Color.white);
        txtAng19.setBackground(Color.white);
        txtAng20.setBackground(Color.white);

        // get in the value and hide appropriate angles
        int selected = comboAng.getSelectedIndex();

        switch (selected) {
            case 0:

                txtAng3.setEditable(false);
                txtAng4.setEditable(false);
                txtAng5.setEditable(false);
                txtAng6.setEditable(false);
                txtAng7.setEditable(false);
                txtAng8.setEditable(false);
                txtAng9.setEditable(false);
                txtAng10.setEditable(false);
                txtAng11.setEditable(false);
                txtAng12.setEditable(false);
                txtAng13.setEditable(false);
                txtAng14.setEditable(false);
                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng3.setBackground(Color.gray);
                txtAng4.setBackground(Color.gray);
                txtAng5.setBackground(Color.gray);
                txtAng6.setBackground(Color.gray);
                txtAng7.setBackground(Color.gray);
                txtAng8.setBackground(Color.gray);
                txtAng9.setBackground(Color.gray);
                txtAng10.setBackground(Color.gray);
                txtAng11.setBackground(Color.gray);
                txtAng12.setBackground(Color.gray);
                txtAng13.setBackground(Color.gray);
                txtAng14.setBackground(Color.gray);
                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 1:

                txtAng4.setEditable(false);
                txtAng5.setEditable(false);
                txtAng6.setEditable(false);
                txtAng7.setEditable(false);
                txtAng8.setEditable(false);
                txtAng9.setEditable(false);
                txtAng10.setEditable(false);
                txtAng11.setEditable(false);
                txtAng12.setEditable(false);
                txtAng13.setEditable(false);
                txtAng14.setEditable(false);
                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng4.setBackground(Color.gray);
                txtAng5.setBackground(Color.gray);
                txtAng6.setBackground(Color.gray);
                txtAng7.setBackground(Color.gray);
                txtAng8.setBackground(Color.gray);
                txtAng9.setBackground(Color.gray);
                txtAng10.setBackground(Color.gray);
                txtAng11.setBackground(Color.gray);
                txtAng12.setBackground(Color.gray);
                txtAng13.setBackground(Color.gray);
                txtAng14.setBackground(Color.gray);
                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 2:

                txtAng5.setEditable(false);
                txtAng6.setEditable(false);
                txtAng7.setEditable(false);
                txtAng8.setEditable(false);
                txtAng9.setEditable(false);
                txtAng10.setEditable(false);
                txtAng11.setEditable(false);
                txtAng12.setEditable(false);
                txtAng13.setEditable(false);
                txtAng14.setEditable(false);
                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng5.setBackground(Color.gray);
                txtAng6.setBackground(Color.gray);
                txtAng7.setBackground(Color.gray);
                txtAng8.setBackground(Color.gray);
                txtAng9.setBackground(Color.gray);
                txtAng10.setBackground(Color.gray);
                txtAng11.setBackground(Color.gray);
                txtAng12.setBackground(Color.gray);
                txtAng13.setBackground(Color.gray);
                txtAng14.setBackground(Color.gray);
                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 3:

                txtAng6.setEditable(false);
                txtAng7.setEditable(false);
                txtAng8.setEditable(false);
                txtAng9.setEditable(false);
                txtAng10.setEditable(false);
                txtAng11.setEditable(false);
                txtAng12.setEditable(false);
                txtAng13.setEditable(false);
                txtAng14.setEditable(false);
                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng6.setBackground(Color.gray);
                txtAng7.setBackground(Color.gray);
                txtAng8.setBackground(Color.gray);
                txtAng9.setBackground(Color.gray);
                txtAng10.setBackground(Color.gray);
                txtAng11.setBackground(Color.gray);
                txtAng12.setBackground(Color.gray);
                txtAng13.setBackground(Color.gray);
                txtAng14.setBackground(Color.gray);
                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 4:

                txtAng7.setEditable(false);
                txtAng8.setEditable(false);
                txtAng9.setEditable(false);
                txtAng10.setEditable(false);
                txtAng11.setEditable(false);
                txtAng12.setEditable(false);
                txtAng13.setEditable(false);
                txtAng14.setEditable(false);
                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng7.setBackground(Color.gray);
                txtAng8.setBackground(Color.gray);
                txtAng9.setBackground(Color.gray);
                txtAng10.setBackground(Color.gray);
                txtAng11.setBackground(Color.gray);
                txtAng12.setBackground(Color.gray);
                txtAng13.setBackground(Color.gray);
                txtAng14.setBackground(Color.gray);
                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 5:

                txtAng8.setEditable(false);
                txtAng9.setEditable(false);
                txtAng10.setEditable(false);
                txtAng11.setEditable(false);
                txtAng12.setEditable(false);
                txtAng13.setEditable(false);
                txtAng14.setEditable(false);
                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng8.setBackground(Color.gray);
                txtAng9.setBackground(Color.gray);
                txtAng10.setBackground(Color.gray);
                txtAng11.setBackground(Color.gray);
                txtAng12.setBackground(Color.gray);
                txtAng13.setBackground(Color.gray);
                txtAng14.setBackground(Color.gray);
                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 6:

                txtAng9.setEditable(false);
                txtAng10.setEditable(false);
                txtAng11.setEditable(false);
                txtAng12.setEditable(false);
                txtAng13.setEditable(false);
                txtAng14.setEditable(false);
                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng9.setBackground(Color.gray);
                txtAng10.setBackground(Color.gray);
                txtAng11.setBackground(Color.gray);
                txtAng12.setBackground(Color.gray);
                txtAng13.setBackground(Color.gray);
                txtAng14.setBackground(Color.gray);
                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 7:

                txtAng10.setEditable(false);
                txtAng11.setEditable(false);
                txtAng12.setEditable(false);
                txtAng13.setEditable(false);
                txtAng14.setEditable(false);
                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng10.setBackground(Color.gray);
                txtAng11.setBackground(Color.gray);
                txtAng12.setBackground(Color.gray);
                txtAng13.setBackground(Color.gray);
                txtAng14.setBackground(Color.gray);
                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 8:
                txtAng11.setEditable(false);
                txtAng12.setEditable(false);
                txtAng13.setEditable(false);
                txtAng14.setEditable(false);
                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng11.setBackground(Color.gray);
                txtAng12.setBackground(Color.gray);
                txtAng13.setBackground(Color.gray);
                txtAng14.setBackground(Color.gray);
                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);

                break;
            case 9:
                txtAng12.setEditable(false);
                txtAng13.setEditable(false);
                txtAng14.setEditable(false);
                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng12.setBackground(Color.gray);
                txtAng13.setBackground(Color.gray);
                txtAng14.setBackground(Color.gray);
                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 10:

                txtAng13.setEditable(false);
                txtAng14.setEditable(false);
                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng13.setBackground(Color.gray);
                txtAng14.setBackground(Color.gray);
                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 11:

                txtAng14.setEditable(false);
                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng14.setBackground(Color.gray);
                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 12:

                txtAng15.setEditable(false);
                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng15.setBackground(Color.gray);
                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 13:

                txtAng16.setEditable(false);
                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng16.setBackground(Color.gray);
                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 14:

                txtAng17.setEditable(false);
                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng17.setBackground(Color.gray);
                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 15:

                txtAng18.setEditable(false);
                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng18.setBackground(Color.gray);
                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 16:

                txtAng19.setEditable(false);
                txtAng20.setEditable(false);

                txtAng19.setBackground(Color.gray);
                txtAng20.setBackground(Color.gray);
                break;
            case 17:

                txtAng20.setEditable(false);

                txtAng20.setBackground(Color.gray);
                break;
            case 18:

                break;

        }
    }//GEN-LAST:event_comboAngActionPerformed

    private void comboWingTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboWingTypeActionPerformed
        int index = comboWingType.getSelectedIndex();
        switch (index) {
            case 0:
                txtChordBreak.setEditable(false);
                txtChordBreak.setBackground(Color.gray);

                txtOutSpan.setEditable(false);
                txtOutSpan.setBackground(Color.gray);

                txtOutSweep.setEditable(false);
                txtOutSweep.setBackground(Color.gray);

                txtOutDihedral.setEditable(false);
                txtOutDihedral.setBackground(Color.gray);
                break;
            case 1:
                txtChordBreak.setEditable(true);
                txtChordBreak.setBackground(Color.white);

                txtOutSpan.setEditable(true);
                txtOutSpan.setBackground(Color.white);

                txtOutSweep.setEditable(true);
                txtOutSweep.setBackground(Color.white);

                txtOutDihedral.setEditable(true);
                txtOutDihedral.setBackground(Color.white);
                break;
        }
    }//GEN-LAST:event_comboWingTypeActionPerformed

    private void comboNumberStationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboNumberStationsActionPerformed
        int index = comboNumberStations.getSelectedIndex();

        // update sectionList
        int numberStat = index + 2;
        sectionList.set(0, numberStat);

        // edit the number of stations available to scroll through
        int stationSize = comboStation.getItemCount();
        index = index + 2;
        if (stationSize > index) {
            while (stationSize > index) {
                stationSize--;
                comboStation.removeItemAt(stationSize); // remove if excess
            }
        }
        // index = 18, stationSize = 19
        if (stationSize < index) {
            while (stationSize < index) {
                stationSize++;
                comboStation.addItem("" + stationSize); // add if shortage
            }
        }
        index = index - 2;
        // display the data for first section
        txtDistanceNose.setText(fuselageDistance.get(0).toString());
        txtRadiusBody.setText(fuselageRadius.get(0).toString());
        txtHighestBody.setText(fuselageUpper.get(0).toString());
        txtLowestBody.setText(fuselageLower.get(0).toString());
        txtAreaBody.setText(fuselageArea.get(0).toString());
        txtCircumBody.setText(fuselageCircumference.get(0).toString());
        previousBodyIndex = 0;

        // edit the data in the arraylists
        switch (index) {
            case 0:
                // set index to 0 of station number, clear array spaces above, 
                comboStation.setSelectedIndex(0);



                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 1:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 2:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 3:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 4:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 5:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 6:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 7:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 8:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 9:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 10:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 11:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 12:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 13:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 14:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 15:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 16:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 17:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
            case 18:
                comboStation.setSelectedIndex(0);
                index = index + 2;
                for (int i = 19; i >= index; i--) {
                    fuselageDistance.remove(i);
                    fuselageRadius.remove(i);
                    fuselageCircumference.remove(i);
                    fuselageArea.remove(i);
                    fuselageUpper.remove(i);
                    fuselageLower.remove(i);
                    fuselageDistance.add(zero);
                    fuselageRadius.add(zero);
                    fuselageCircumference.add(zero);
                    fuselageArea.add(zero);
                    fuselageUpper.add(zero);
                    fuselageLower.add(zero);
                }
                break;
        }
    }//GEN-LAST:event_comboNumberStationsActionPerformed

    private void comboIterationParameterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboIterationParameterActionPerformed
        
    }//GEN-LAST:event_comboIterationParameterActionPerformed

    private ArrayList assembleInputSingleOneControlSurface() {
        // toString was used to prevent errors
        ArrayList returnList = new ArrayList();
        String line1 = "CASEID ";
        if (checkName.isSelected() == true) {
            line1 = line1 + txtAircraftName.getText();
        } else {
            line1 = line1 + "Aircraft";
        }
        returnList.add(line1);
        // then add fltcon
        if (fltconLines.isEmpty() == false) {
            for (int i = 0; i < fltconLines.size(); i++) {
                returnList.add(fltconLines.get(i).toString());
            }
        }
        // these are messy because of errors appearing with commas
        // needed temp arraylist in order to allow rerunning
        ArrayList tempSynths = new ArrayList();
        for (int i = 0; i < synthsLines.size(); i++) {
            tempSynths.add(synthsLines.get(i));
        }
        if (synthsLines.get(3).toString().equalsIgnoreCase("0") == false) {
            String line = synthsLines.get(3).toString() + "$";
            tempSynths.set(3, line);
            line = synthsLines.get(2).toString() + ",";
            tempSynths.set(2, line);
            line = synthsLines.get(1).toString() + ",";
            tempSynths.set(1, line);
            line = synthsLines.get(0).toString() + ",";
            tempSynths.set(0, line);

        } else if (synthsLines.get(2).toString().equalsIgnoreCase("0") == false) {
            String line = synthsLines.get(2).toString() + "$";
            tempSynths.set(2, line);
            line = synthsLines.get(1).toString() + ",";
            tempSynths.set(1, line);
            line = synthsLines.get(0).toString() + ",";
            tempSynths.set(0, line);

        } else if (synthsLines.get(1).toString().equalsIgnoreCase("0") == false) {
            String line = synthsLines.get(1).toString() + "$";
            tempSynths.set(1, line);
            line = synthsLines.get(0).toString() + ",";
            tempSynths.set(0, line);

        } else {
            String line = synthsLines.get(0).toString() + "$";
            tempSynths.set(0, line);

        }
        // this is messy because commas need to be at the end of the line
        // having commas at the beginning, although a lot easier to code, throws errors in datcom
        for (int i = 0; i < synthsLines.size(); i++) {
            if (synthsLines.get(i).toString().equalsIgnoreCase("0") != true) {

                returnList.add(tempSynths.get(i).toString());

            }
        }
        // then body
        if (bodyLines.isEmpty() == false) {
            for (int i = 0; i < bodyLines.size(); i++) {
                returnList.add(bodyLines.get(i).toString());
            }
        }
        // then wings
        if (wingLines.isEmpty() == false) {
            for (int i = 0; i < wingLines.size(); i++) {
                returnList.add(wingLines.get(i).toString());
            }
        }

        // then tail
        if (tailVertLines.isEmpty() == false) {
            for (int i = 0; i < tailVertLines.size(); i++) {
                returnList.add(tailVertLines.get(i).toString());
            }
        }
        // ailerons plus new case would go here when they are enabled later
        // big if case would cover each of these, or else separate methods maybe??
        // then tail 
        if (tailHorzLines.isEmpty() == false) {
            for (int i = 0; i < tailHorzLines.size(); i++) {
                returnList.add(tailHorzLines.get(i).toString());
            }
        }
        // finally for now the elevators
        if (elevatorLines.isEmpty() == false) {
            for (int i = 0; i < elevatorLines.size(); i++) {
                returnList.add(elevatorLines.get(i).toString());
            }
        }
        // then finish off with damp, etc.
        line1 = "DAMP";
        returnList.add(line1);
        line1 = "NEXT CASE";
        returnList.add(line1);
        return returnList;
    }

    private void btnBeginIterationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBeginIterationActionPerformed
        if (btnSingle.isSelected() == true) {
            // set multi flag to false
            flagMulti = false;
            // assemble the file first based on number of control surfaces
            // just have one for now
            ArrayList lines = new ArrayList();
            lines = assembleInputSingleOneControlSurface();
            // test
            //System.out.println("This is the input sent to .dcm file:");
            //for (int i = 0; i < lines.size(); i++) {
            //System.out.println(lines.get(i));

            //}
            // find relative path/working directory first
            Path currentRelativePath = Paths.get("");
            String relativePath = currentRelativePath.toAbsolutePath().toString();

            String aircraftName;
            if (checkName.isSelected() == true) {
                aircraftName = txtAircraftName.getText();
                aircraftName = aircraftName.replaceAll("\\s+", ""); // remove whitespace
            } else {
                aircraftName = "Aircraft";
            }
            // create a new folder
            // catch error if already exists
            int folderCount = 0;
            boolean folderCreatedCheck = false;
            String originalName = aircraftName;
            while (folderCreatedCheck == false) {

                File dir2 = new File(aircraftName);
                folderCreatedCheck = dir2.mkdir();
                if (folderCreatedCheck != true) {
                    aircraftName = originalName + folderCount; // starting with zero
                    folderCount++;
                }
            }
            // create the .dcm file in the folder
            // also creates a .xml file so will need to edit this method later
            createDatcomFiles(relativePath, aircraftName, lines);
            // then run the actual file using runner class
            // using a new thread for sake of transferability to multiple analysis
            Runner runner1 = new Runner();
            runner1.relativePath = relativePath;
            runner1.folder = aircraftName;
            runner1.start();
            pnlMainTab.setSelectedIndex(2);

            // need to probably save the location to a global variable?
            lastAircraftName = aircraftName;
        } // ignore the multiple analysis tool for now
        else if (btnMulti.isSelected() == true) {
            // update flag for output analysis
            flagMulti = true;
            // need to do error check first
            String incrementsString = txtIncrements.getText();
            increments = Integer.parseInt(incrementsString);
            if (increments > 100) {
                increments = 100;
            } else if (increments < 1) {
                increments = 1;
            }

            // assemble the data as usual
            ArrayList lines = new ArrayList();
            lines = assembleInputSingleOneControlSurface();
            // test
            //System.out.println("This is the input sent to .dcm file:");
            //for (int i = 0; i < lines.size(); i++) {
            //System.out.println(lines.get(i));

            //}
            // find relative path/working directory first
            Path currentRelativePath = Paths.get("");
            String relativePath = currentRelativePath.toAbsolutePath().toString();

            // then get the upper and lower values
            // need error catching on this soon
            String upperValueString = txtUpperValue.getText();
            double upperValue = Double.parseDouble(upperValueString);
            String lowerValueString = txtLowerValue.getText();
            double lowerValue = Double.parseDouble(lowerValueString);
            double diff = upperValue - lowerValue; // difference between the two
            double deltaValuePerInc = diff / (increments - 1);

            // here will identify line to change with each increment
            boolean enabledFlag = false;
            String namelistFlag = null;
            String variableFlag = null;
            // these indices are used to place the incremental data in the correct position
            int lineIndex = 0;
            int columnIndex = 0;
            int parameterCode = comboIterationParameter.getSelectedIndex();
            // in this switch statement the correct position to edit is found, and the current value is removed
            // the codes are found by searching for namelist first then variable second
            switch (parameterCode) {
                case 0:
                    namelistFlag = "SYNTHS";
                    variableFlag = "XCG=";
                    break;
                case 1:
                    break;
                case 2:
                    namelistFlag = "WGPLNF";
                    variableFlag = "CHRDTP=";
                    break;
                case 3:
                    namelistFlag = "WGPLNF";
                    variableFlag = "CHRDR=";
                    break;
                case 4:
                    namelistFlag = "FLTCON";
                    variableFlag = "MACH(1)=";
                    break;
                case 5:
                    namelistFlag = "FLTCON";
                    variableFlag = "ALT(1)=";
                    break;
                case 6:
                    namelistFlag = "SYNTHS";
                    variableFlag = "ZCG=";
                    break;
                case 7:
                    namelistFlag = "WGPLNF";
                    variableFlag = "SSPN=";
                    break;
                case 8:
                    namelistFlag = "WGPLNF";
                    variableFlag = "SSPNE=";
                    break;
                case 9:
                    namelistFlag = "HTPLNF";
                    variableFlag = "CHRDTP=";
                    break;
                case 10:
                    namelistFlag = "HTPLNF";
                    variableFlag = "CHRDR=";
                    break;
                case 11:
                    namelistFlag = "HTPLNF";
                    variableFlag = "SSPN=";
                    break;
                case 12:
                    namelistFlag = "HTPLNF";
                    variableFlag = "SSPNE=";
                    break;
                case 13:
                    break;
                case 14:
                    namelistFlag = "WGPLNF";
                    variableFlag = "TWISTA=";
                    break;
                case 15:
                    namelistFlag = "WGPLNF";
                    variableFlag = "DHDADI=";
                    break;
                case 16:
                    namelistFlag = "SYNTHS";
                    variableFlag = "ALIW=";
                    break;
                case 17:
                    namelistFlag = "SYNTHS";
                    variableFlag = "ZW=";
                    break;
                case 18:
                    namelistFlag = "SYNTHS";
                    variableFlag = "XW=";
                    break;
                case 19:
                    namelistFlag = "SYNTHS";
                    variableFlag = "ZH=";
                    break;
                case 20:
                    namelistFlag = "SYNTHS";
                    variableFlag = "XH=";
                    break;
                case 21:
                    break;
                case 22:
                    break;
                case 23:
                    break;
                case 24:
                    break;
                case 25:
                    break;
                case 26:
                    break;
                case 27:
                    break;
                case 28:
                    break;
                case 29:
                    break;
            }
            // find index of namelist
            int startNamelist = 0;
            for(int i=0;i<lines.size();i++) {
                String line = lines.get(i).toString();
                if(line.contains(namelistFlag)==true) {
                    startNamelist = i;
                    i += lines.size(); //to end loop
                }
            }
            // find row and column index, and delete current value
            for(int i = startNamelist;i<(lines.size());i++) {
                String line = lines.get(i).toString();
                if(line.contains(variableFlag)==true) {
                    lineIndex = i;
                    columnIndex = line.indexOf(variableFlag);
                    columnIndex += variableFlag.length();
                    // then delete current value
                    int commaIndex = line.indexOf(",", columnIndex);
                    if(commaIndex == -1) { // ie a line with the $ and no comma
                        String beforeValue = line.substring(0, columnIndex);
                        String afterValue = "$";
                        String newLine = beforeValue + afterValue;
                        lines.set(i, newLine);
                    }
                    else {
                        String beforeValue = line.substring(0, columnIndex);
                        String afterValue = line.substring(commaIndex);
                        String newLine = beforeValue + afterValue;
                        lines.set(i, newLine);
                    }
                    
                    i += lines.size(); //to end loop
                    enabledFlag = true;
                }
            }
            
            // for parameters that aren't working yet
            if (enabledFlag == false) {
                WorkInProgressWindow wip = new WorkInProgressWindow();
                wip.setVisible(true);
                wip.changeText("This multiple analysis tool parameter is not available yet, please "
                        + "choose a different parameter.");
            } else {
                // then the folders and files are created and run

                // create the folders starting with number 1
                for (int i = 1; i <= increments; i++) {
                    createFolder("" + i);
                }
                // populate the folders with files
                // split the relevant line in two
                String fullLine = lines.get(lineIndex).toString();
                String firstHalf = fullLine.substring(0, columnIndex);
                String secondHalf = fullLine.substring(columnIndex);
                ArrayList toFile = new ArrayList();
                for (int i = 0; i < increments; i++) {
                    toFile.clear();
                    String combined = firstHalf;
                    double valueThisIteration = lowerValue + (i * deltaValuePerInc);
                    combined += valueThisIteration;
                    combined += secondHalf;
                    for (int j = 0; j < lines.size(); j++) {
                        toFile.add(lines.get(j));
                    }
                    toFile.set(lineIndex, combined);
                    i++; // to start at 1 rather than zero
                    createDatcomFiles(relativePath, "" + i, toFile);
                    i--;
                }
                // then run all the files
                runDatcom(relativePath, increments);
                pnlMainTab.setSelectedIndex(2);
            }



        }
    }//GEN-LAST:event_btnBeginIterationActionPerformed

    private void btnImportResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportResultsActionPerformed
        // need to check if csv file was created and hence file was run
        currentSessionResults = false;
        String fileName = lastAircraftName; // this is what the last file run should be named
        Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();
        File file = new File(path + "/" + fileName + "/" + fileName + ".csv");
        if (file.exists()) {
            currentSessionResults = true; // change back to true
            //System.out.println("Testing, file exists");
        }


        if (currentSessionResults == true) {

            try {
                // read in the file
                ArrayList rawOutput = readOldCSV(file);
                // then remove the asterisks
                for (int i = 0; i < rawOutput.size(); i++) {
                    if (rawOutput.get(i).toString().equalsIgnoreCase("*****************************")) {
                        rawOutput.remove(i);
                        i--;
                    }
                    // test print
                    //System.out.println("Line "+i+":"+rawOutput.get(i));
                }
                // then remove the empty lines
                for (int i = 0; i < rawOutput.size(); i++) {
                    if (rawOutput.get(i).toString().equalsIgnoreCase("")) {
                        rawOutput.remove(i);
                        i--;
                    }

                }
                //for(int i = 0;i<rawOutput.size();i++) {
                //    System.out.println("Line "+i+":"+rawOutput.get(i));
                //}
                // then need to separate each section up into areas 
                // the flap terms is elevator if there is no flap
                // i.e. the forward most symmetric control surface
                int flapTermsLocation = 0;
                int aileronTermsLocation = 0;
                int elevatorTermsLocation = 0;
                for (int i = 0; i < rawOutput.size(); i++) {
                    String line = rawOutput.get(i).toString();
                    if (line.equalsIgnoreCase("flap terms")) {
                        flapTermsLocation = i;
                    }
                    if (line.equalsIgnoreCase("aileron terms")) {
                        aileronTermsLocation = i;
                    }
                    if (line.equalsIgnoreCase("elevator terms")) {
                        elevatorTermsLocation = i;
                    }
                }
                // then add data to the correct arraylists
                for (int i = 0; i < flapTermsLocation; i++) {
                    csvBasic.add(rawOutput.get(i).toString());
                    //System.out.println(rawOutput.get(i).toString());
                }
                for (int i = flapTermsLocation; i < aileronTermsLocation; i++) {
                    csvFlap.add(rawOutput.get(i).toString());
                }
                for (int i = aileronTermsLocation; i < elevatorTermsLocation; i++) {
                    csvAileron.add(rawOutput.get(i).toString());
                }
                for (int i = elevatorTermsLocation; i < rawOutput.size(); i++) {
                    csvElevator.add(rawOutput.get(i).toString());
                }

                // then the user can go and press the textual and graphical analysis buttons
                btnGraphical.setEnabled(true); // feature disabled for now
                btnTextual.setEnabled(true);
                btnOpenOutputLocation.setEnabled(true); // feature disabled for now
                btnOutputXLS.setEnabled(true); // feature disabled for now
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }


            // the following comment lines are for the multiple analysis
            // and should be removed for now
            //String incrementsString = txtIncrements.getText();
            //increments = Integer.parseInt(incrementsString);
            // read in the data from the csv files and save
            //for (int i = 1; i <= increments; i++) {
            //    readCSV(relativePath, "" + i);
            //}
            // now delete all the files to clean up
            //for (int i = 1; i <= increments; i++) {
            //    deleteFolders(relativePath, "" + i);
            //}
            // test data that is saved
            //System.out.println("All values of CM_deg");
            //System.out.println("---------------------");
            //for(int i=0; i<CM_wbh.size();i++) {
            //    System.out.println(""+CM_wbh.get(i));
            //}
        } else {
            WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("There are no results available to analyse. The file"
                    + " is missing or an error has occured that prevented datcom from creating any output"
                    + ". Please check the folder.");
        }
    }//GEN-LAST:event_btnImportResultsActionPerformed

    private void btnPlotDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlotDataActionPerformed
        boolean check = false;
        if (check == false) {
        } else {
            // need to clear data for a new graph first!!!!!
            // find axes, etc.
            int yAxisVariable = comboPlotY.getSelectedIndex();
            int alphaValue = comboAlpha.getSelectedIndex();
            String yAxis = comboPlotY.getSelectedItem().toString();

            System.out.println("Size of array:" + CL_wbh.size());

            //DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

            switch (yAxisVariable) {
                case 0:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = alphaValue + (i * 12);
                        double value = Double.parseDouble(CL_wbh.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 1:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(CLq_deg.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 2:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(CLadot_deg.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 3:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(CD_wbh.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 4:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(CYbeta_deg.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 5:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(CYp_deg.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 6:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(Clbeta_deg.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 7:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(Clp_deg.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 8:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(Clr_deg.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 9:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(CM_wbh.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 10:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(CMq_deg.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 11:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(CMadot_deg.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 12:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(CNbeta_deg.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 13:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(CNp_deg.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;
                case 14:
                    for (int i = 0; i < cgValues.size(); i++) {
                        int j = (i * 12);
                        double value = Double.parseDouble(CNr_deg.get(j).toString());
                        double cgValue = Double.parseDouble(cgValues.get(i).toString());
                        //dataSet.setValue(value, yAxis + " per deg", "" + cgValue);
                    }
                    break;

            }
            //JFreeChart chart = ChartFactory.createBarChart("Chart Title", "cg position(ft)", yAxis + " per deg", dataSet, PlotOrientation.VERTICAL, false, true, false);
            //CategoryPlot p = chart.getCategoryPlot();
            //p.setRangeGridlinePaint(Color.BLACK);
            //ChartFrame frame2 = new ChartFrame("Default Title", chart);
            //frame2.setVisible(true);
            //frame2.setSize(950, 750);
        }
    }//GEN-LAST:event_btnPlotDataActionPerformed

    private void radioButtonManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonManualActionPerformed
        btnImportOpen.setEnabled(false);
        btnImportReview.setEnabled(false);
        btnManualBegin.setEnabled(true);
        txtImportStatus.setText("Manual Input Selected");
        pnlInnerTabs.setEnabledAt(1, false);
        pnlInnerTabs.setEnabledAt(2, false);
        pnlInnerTabs.setEnabledAt(3, false);
        pnlInnerTabs.setEnabledAt(4, false);
        pnlInnerTabs.setEnabledAt(5, false);
        pnlInnerTabs.setEnabledAt(6, false);
    }//GEN-LAST:event_radioButtonManualActionPerformed

    private void radioButtonImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonImportActionPerformed
        btnImportOpen.setEnabled(true);
        btnImportReview.setEnabled(true);
        btnManualBegin.setEnabled(false);
        txtImportStatus.setText("Please Open File");
        pnlInnerTabs.setEnabledAt(1, false);
        pnlInnerTabs.setEnabledAt(2, false);
        pnlInnerTabs.setEnabledAt(3, false);
        pnlInnerTabs.setEnabledAt(4, false);
        pnlInnerTabs.setEnabledAt(5, false);
        pnlInnerTabs.setEnabledAt(6, false);
    }//GEN-LAST:event_radioButtonImportActionPerformed

    private void radioButtonNoContActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonNoContActionPerformed

        if (radioButtonElevator.isSelected() == false) {
            if (radioButtonAileron.isSelected() == false) {
                if (radioButtonFlaps.isSelected() == false) {
                    radioButtonNoCont.setSelected(true);
                }
            }
        }
        radioButtonElevator.setSelected(false);
        radioButtonAileron.setSelected(false);
        radioButtonFlaps.setSelected(false);
    }//GEN-LAST:event_radioButtonNoContActionPerformed

    private void radioButtonAileronActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonAileronActionPerformed
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("Ailerons and flaps currently aren't supported");
        //radioButtonNoCont.setSelected(false);
        //if (radioButtonElevator.isSelected() == false) {
        //    if (radioButtonAileron.isSelected() == false) {
        //        if (radioButtonFlaps.isSelected() == false) {
        //            radioButtonNoCont.setSelected(true);
        //        }
        //    }
        //}
        radioButtonAileron.setSelected(false);
    }//GEN-LAST:event_radioButtonAileronActionPerformed

    private void radioButtonElevatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonElevatorActionPerformed

        radioButtonNoCont.setSelected(false);
        if (radioButtonElevator.isSelected() == false) {
            if (radioButtonAileron.isSelected() == false) {
                if (radioButtonFlaps.isSelected() == false) {
                    radioButtonNoCont.setSelected(true);
                }
            }
        }

        boolean selected = radioButtonElevator.isSelected();
        comboElevType.setEnabled(selected);
        comboElevDef.setSelectedIndex(8);
        comboElevDef.setEnabled(selected);

        txtChrdfi.setEditable(selected);
        txtChrdfo.setEditable(selected);
        txtSpanfi.setEditable(selected);
        txtSpanfo.setEditable(selected);
        txtEDef1.setEditable(selected);
        txtEDef2.setEditable(selected);
        txtEDef3.setEditable(selected);
        txtEDef4.setEditable(selected);
        txtEDef5.setEditable(selected);
        txtEDef6.setEditable(selected);
        txtEDef7.setEditable(selected);
        txtEDef8.setEditable(selected);
        txtEDef9.setEditable(selected);
        if (selected == true) {
            txtChrdfi.setBackground(Color.white);
            txtChrdfo.setBackground(Color.white);
            txtSpanfi.setBackground(Color.white);
            txtSpanfo.setBackground(Color.white);
            txtEDef1.setBackground(Color.white);
            txtEDef2.setBackground(Color.white);
            txtEDef3.setBackground(Color.white);
            txtEDef4.setBackground(Color.white);
            txtEDef5.setBackground(Color.white);
            txtEDef6.setBackground(Color.white);
            txtEDef7.setBackground(Color.white);
            txtEDef8.setBackground(Color.white);
            txtEDef9.setBackground(Color.white);
        }
        if (selected == false) {
            txtChrdfi.setBackground(Color.gray);
            txtChrdfo.setBackground(Color.gray);
            txtSpanfi.setBackground(Color.gray);
            txtSpanfo.setBackground(Color.gray);
            txtEDef1.setBackground(Color.gray);
            txtEDef2.setBackground(Color.gray);
            txtEDef3.setBackground(Color.gray);
            txtEDef4.setBackground(Color.gray);
            txtEDef5.setBackground(Color.gray);
            txtEDef6.setBackground(Color.gray);
            txtEDef7.setBackground(Color.gray);
            txtEDef8.setBackground(Color.gray);
            txtEDef9.setBackground(Color.gray);
        }
    }//GEN-LAST:event_radioButtonElevatorActionPerformed

    private void btnImportOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportOpenActionPerformed
        final JFileChooser importDCM = new JFileChooser();
        int returnVal = importDCM.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File importedFile = importDCM.getSelectedFile();
            // send the file to be read and data sorted
            ArrayList importedData;
            try {
                importedData = new ArrayList(readImport(importedFile));
                displayImport(importedData); // might get rid of these 
                txtImportStatus.setText("Import was successful. Proceed to review or edit.");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                txtImportStatus.setText("Import was unsuccessful. File not found. Please try again.");
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                txtImportStatus.setText("Import was unsuccessful. Import exception. Invalid file. Please try another file.");
            }
            // send the data to be displayed in the appropriate tabs

            // change message to confirm success or level of success

        }


        // maybe open a window summarising the imported data and what wasn't imported
    }//GEN-LAST:event_btnImportOpenActionPerformed

    private void btnImportReviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportReviewActionPerformed
        pnlInnerTabs.setEnabledAt(1, true);
        pnlInnerTabs.setEnabledAt(2, true);
        pnlInnerTabs.setEnabledAt(3, true);
        pnlInnerTabs.setEnabledAt(4, true);
        pnlInnerTabs.setEnabledAt(5, true);
        pnlInnerTabs.setEnabledAt(6, true);
        pnlInnerTabs.setSelectedIndex(1);
        // need to also open window for imported data
        // call the input review class here
        InputReview ir = new InputReview();
        ir.setVisible(true);
        ir.changeText(inputReviewToSend);
        // warn that assumes import is in feet
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("Imported data is assumed to be in feet for now. "
                + "The import function will be updated to recognise import as metres "
                + "later.");

        // need to paste the data into appropriate areas
        comboProp.setSelectedIndex(engineID);
    }//GEN-LAST:event_btnImportReviewActionPerformed

    private void btnSaveNextBodyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveNextBodyActionPerformed
        pnlInnerTabs.setSelectedIndex(2);
        bodyLines.clear();
        // first job is to save current station
        int current = comboStation.getSelectedIndex();
        // add data to arraylists
        double dist = Double.parseDouble(txtDistanceNose.getText());
        double radius = Double.parseDouble(txtRadiusBody.getText());
        double high = Double.parseDouble(txtHighestBody.getText());
        double low = Double.parseDouble(txtLowestBody.getText());
        double area = Double.parseDouble(txtAreaBody.getText());
        double circum = Double.parseDouble(txtCircumBody.getText());

        // need to autocalculate if necessary here
        // need to ignore this if all zero i.e. first section and last section
        if (checkAutoCalculateBody.isSelected() == true) {
            // first case is no area or circum, works for all circles and ovals
            if (area == 0.0 && circum == 0.0 && high != 0.0 && low != 0.0 && radius != 0.0) {
                if (high == radius) {
                    if (low == radius) {
                        area = 3.14159265 * radius * radius;
                        circum = 2 * 3.14159265 * radius;
                    }
                    if (low != radius) {
                        area = calculateArea(radius, high, low);
                        circum = calculateCircumference(radius, high, low);
                    }
                }
                if (low == radius) {
                    if (high != radius) {
                        area = calculateArea(radius, high, low);
                        circum = calculateCircumference(radius, high, low);
                    }
                }
                if (low != radius && high != radius) {
                    area = calculateArea(radius, high, low);
                    circum = calculateCircumference(radius, high, low);
                }
            }
            // second case is radius only
            if (area == 0.0 && high == 0.0 && low == 0.0 && circum == 0.0 && radius != 0.0) {
                area = 3.14159265 * radius * radius;
                circum = 2 * 3.14159265 * radius;
                high = radius;
                low = radius;
            }
            // third case is area only
            if (radius == 0.0 && high == 0.0 & low == 0.0 & circum == 0.0 && area != 0.0) {
                radius = Math.sqrt((area / 3.14159265));
                high = radius;
                low = radius;
                circum = 2 * 3.14159265 * radius;
            }
            // the final case is where all values are entered anyway so ignore
        }

        fuselageDistance.set(current, dist);
        fuselageRadius.set(current, radius);
        fuselageUpper.set(current, high);
        fuselageLower.set(current, low);
        fuselageArea.set(current, area);
        fuselageCircumference.set(current, circum);

        if (low < 0.0) {
            WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("Lower edge of the fuselage is usually negative");
        }

        // now need to add everything to the right lines for body
        String noseTailShapes = "";
        if (comboShape.getSelectedIndex() == 0) {
            noseTailShapes = "BNOSE=2.0,BTAIL=2.0,";
        } else if (comboShape.getSelectedIndex() == 1) {
            noseTailShapes = "BNOSE=2.0,BTAIL=1.0,";
        } else if (comboShape.getSelectedIndex() == 2) {
            noseTailShapes = "BNOSE=1.0,BTAIL=2.0,";
        } else {
            noseTailShapes = "BNOSE=1.0,BTAIL=1.0,";
        }
        double noseLength = Double.parseDouble(txtNoseLength.getText());
        double afterbodyLength = Double.parseDouble(txtAfterbodyLength.getText());
        String firstLine = "$BODY NX=" + comboStation.getItemCount() + ".0," + noseTailShapes
                + "BLN=" + noseLength + ",BLA=" + afterbodyLength + ",";
        String secondLine = "X(1)=";
        String thirdLine = "ZU(1)=";
        String fourthLine = "ZL(1)=";
        String fifthLine = "R(1)=";
        String sixthLine = "S(1)=";
        String seventhLine = "P(1)=";
        for (int i = 0; i < comboStation.getItemCount(); i++) {
            secondLine += (fuselageDistance.get(i) + ",");
            thirdLine += (fuselageUpper.get(i) + ",");
            fourthLine += (fuselageLower.get(i) + ",");
            fifthLine += (fuselageRadius.get(i) + ",");
            sixthLine += (fuselageArea.get(i) + ",");
            seventhLine += (fuselageCircumference.get(i) + ",");
        }
        // fix to add dollar sign at end
        seventhLine = seventhLine.substring(0, seventhLine.length() - 1);
        seventhLine += "$";
        //test
        //System.out.println("Here are the lines:");
        //  System.out.println(firstLine);
        //  System.out.println(secondLine);
        // System.out.println(thirdLine);
        //System.out.println(fourthLine);
        //System.out.println(fifthLine);
        //System.out.println(sixthLine);
        //System.out.println(seventhLine);
        bodyLines.add(firstLine);
        bodyLines.add(secondLine);
        bodyLines.add(thirdLine);
        bodyLines.add(fourthLine);
        bodyLines.add(fifthLine);
        bodyLines.add(sixthLine);
        bodyLines.add(seventhLine);


    }//GEN-LAST:event_btnSaveNextBodyActionPerformed

    private void btnSaveNextWingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveNextWingActionPerformed
        // currently missing wing type, will be implemented later
        boolean acceptableData = true;
        String warning = "";
        // clear the wingLines arraylist
        wingLines.clear();
        // need to make sure data is numbers only i.e. no text
        // naca 1 series has a hyphen, all else is free to delete
        if (checkBodyOnly.isSelected() == true) {
        } else if (acceptableData = true) {
            pnlInnerTabs.setSelectedIndex(3);
            // grab values and make sure they have a decimal point
            double rootChord = Double.parseDouble(txtRootChord.getText());
            String chrdr = decimalCheck(rootChord);
            double tipChord = Double.parseDouble(txtTipChord.getText());
            String chrdtp = decimalCheck(tipChord);
            double sSpan = Double.parseDouble(txtSSpan.getText());
            String sspn = decimalCheck(sSpan);
            double sweep = Double.parseDouble(txtSweep.getText());
            String savsi = decimalCheck(sweep);
            double twist = Double.parseDouble(txtTwist.getText());
            String twista = decimalCheck(twist);
            double dihedral = Double.parseDouble(txtDihedral.getText());
            String dhdadi = decimalCheck(dihedral);
            double incidence = Double.parseDouble(txtIncidence.getText());
            String aliw = decimalCheck(incidence);
            double horzPos = Double.parseDouble(txtHorzWingPos.getText());
            String xw = decimalCheck(horzPos);
            double vertPos = Double.parseDouble(txtVertWingPos.getText());
            String zw = decimalCheck(vertPos);
            double sSpanE = Double.parseDouble(txtSSPNE.getText());
            String sspne = decimalCheck(sSpanE);
            double sweepChord = Double.parseDouble(txtCHSTAT.getText());
            sweepChord = sweepChord / 100; // to get to decimal
            String chstat = decimalCheck(sweepChord);
            int wingType = comboTypeWing.getSelectedIndex();
            wingType++;
            String type = wingType + ".0";

            if (comboWingType.getSelectedIndex() == 1) {
                // if there is a breakpoint
                double chordBreak = Double.parseDouble(txtChordBreak.getText());
                String chrdbp = decimalCheck(chordBreak);
                double outSpan = Double.parseDouble(txtOutSpan.getText());
                String sspnop = decimalCheck(outSpan);
                double outSweep = Double.parseDouble(txtOutSweep.getText());
                String savso = decimalCheck(outSweep);
                double outDihedral = Double.parseDouble(txtOutDihedral.getText());
                String dhdado = decimalCheck(outDihedral);
                String firstLine = "$WGPLNF CHRDR=" + chrdr + ",CHRDTP=" + chrdtp
                        + ",CHRDBP=" + chrdbp + ",SSPN=" + sspn + ",SSPNOP=" + sspnop
                        + ",SSPNE=" + sspne + ",";
                String secondLine = "CHSTAT=" + chstat + ",TWISTA=" + twista
                        + ",TYPE=" + type + ",SAVSI=" + savsi + ",SAVSO=" + savso
                        + ",DHDADI=" + dhdadi + ",DHDADO=" + dhdado + "$";
                // then add to winglines arraylist
                wingLines.add(firstLine);
                wingLines.add(secondLine);
                // then add wing stuff to sysnths arraylist
            } else {
                // if there is no breakpoint
                String firstLine = "$WGPLNF CHRDR=" + chrdr + ",CHRDTP=" + chrdtp
                        + ",SSPN=" + sspn
                        + ",SSPNE=" + sspne + ",";
                String secondLine = "CHSTAT=" + chstat + ",TWISTA=" + twista
                        + ",TYPE=" + type + ",SAVSI=" + savsi
                        + ",DHDADI=" + dhdadi + "$";
                wingLines.add(firstLine);
                wingLines.add(secondLine);
            }
            // then add naca code
            String thirdLine = "";
            int nacaIndex = comboNACA.getSelectedIndex();
            switch (nacaIndex) {
                case 0:
                    thirdLine = "NACA-W-1-" + txtAirfoilCode.getText();
                    break;
                case 1:
                    thirdLine = "NACA-W-4-" + txtAirfoilCode.getText();
                    break;
                case 2:
                    thirdLine = "NACA-W-5-" + txtAirfoilCode.getText();
                    break;
                default:
                    thirdLine = "NACA-W-6-" + txtAirfoilCode.getText();
                    break;
            }
            wingLines.add(thirdLine);
            // then add data that belongs in the synths list
            String synthsLine = "XW=" + xw + ",ZW=" + zw + ",ALIW=" + aliw;
            synthsLines.set(1, synthsLine);
            // test
            //System.out.println(wingLines.get(0));
            //System.out.println(wingLines.get(1));
            //System.out.println(wingLines.get(2));
            //System.out.println(synthsLines.get(1));

        } else {
            WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("There is an error in the data due to text being detected. "
                    + "Further information: " + warning);
        }
    }//GEN-LAST:event_btnSaveNextWingActionPerformed

    private void btnSaveNextTailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveNextTailActionPerformed

        // need to add to arrays also
        boolean acceptableData = true;
        String warning = ""; // will be used to notify user of the problematic data
        // need to check if data is acceptable

        // clear the arrays
        tailHorzLines.clear();
        tailVertLines.clear();
        if (acceptableData == true && checkHorzStab.isSelected() == true) {
            pnlInnerTabs.setSelectedIndex(4);

            double rootChord = Double.parseDouble(txtRootChord1.getText());
            String chrdr = decimalCheck(rootChord);
            double tipChord = Double.parseDouble(txtTipChord1.getText());
            String chrdtp = decimalCheck(tipChord);
            double sSpan = Double.parseDouble(txtSSpan1.getText());
            String sspn = decimalCheck(sSpan);
            double sweep = Double.parseDouble(txtSweep1.getText());
            String savsi = decimalCheck(sweep);

            double dihedral = Double.parseDouble(txtDihedral1.getText());
            String dhdadi = decimalCheck(dihedral);

            double horzPos = Double.parseDouble(txtHorzWingPos2.getText());
            String xh = decimalCheck(horzPos);
            double vertPos = Double.parseDouble(txtVertWingPos2.getText());
            String zh = decimalCheck(vertPos);
            double sSpanE = Double.parseDouble(txtSSPNE2.getText());
            String sspne = decimalCheck(sSpanE);
            double sweepChord = Double.parseDouble(txtCHSTAT1.getText());
            sweepChord = sweepChord / 100; // to get to decimal
            String chstat = decimalCheck(sweepChord);

            // then add data to horz line
            String firstLine = "$HTPLNF CHRDR=" + chrdr + ",CHRDTP=" + chrdtp
                    + ",SSPN=" + sspn
                    + ",SSPNE=" + sspne + ",TWISTA=0.0,";
            String secondLine = "CHSTAT=" + chstat
                    + ",TYPE=1.0" + ",SAVSI=" + savsi
                    + ",DHDADI=" + dhdadi + "$";
            tailHorzLines.add(firstLine);
            tailHorzLines.add(secondLine);

            // then add naca code
            String thirdLine = "";
            int nacaIndex = comboHorzNACA.getSelectedIndex();
            switch (nacaIndex) {
                case 0:
                    thirdLine = "NACA-H-1-" + txtHorzCode.getText();
                    break;
                case 1:
                    thirdLine = "NACA-H-4-" + txtHorzCode.getText();
                    break;
                case 2:
                    thirdLine = "NACA-H-5-" + txtHorzCode.getText();
                    break;
                default:
                    thirdLine = "NACA-H-6-" + txtHorzCode.getText();
                    break;
            }
            // then add to tail horz lines
            tailHorzLines.add(thirdLine);
            // then add data that belongs in the synths list
            String synthsLine = "XH=" + xh + ",ZH=" + zh;
            synthsLines.set(2, synthsLine);

            //test
            //System.out.println(tailHorzLines.get(0));
            //System.out.println(tailHorzLines.get(1));
            //System.out.println(tailHorzLines.get(2));
            // System.out.println(synthsLines.get(2));

            // then do the same for the vert data
            // moved to method in order to reuse variable names to save time
            if (checkVertStab.isSelected() == true) {
                vertDataGrab();
            }
        } else if (acceptableData == true && checkVertStab.isSelected() == true) {
            pnlInnerTabs.setSelectedIndex(4);
            vertDataGrab();
        } else if (checkVertStab.isSelected() == false && checkHorzStab.isSelected() == false) { // the case for if no tail
            pnlInnerTabs.setSelectedIndex(4);
        } else {
            // if there is an error
            WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("There is an error in the data due to text being detected. "
                    + "Further information: " + warning);
        }
    }//GEN-LAST:event_btnSaveNextTailActionPerformed

    private void vertDataGrab() {
        double rootChord = Double.parseDouble(txtRootChord2.getText());
        String chrdr = decimalCheck(rootChord);
        double tipChord = Double.parseDouble(txtTipChord2.getText());
        String chrdtp = decimalCheck(tipChord);
        double sSpan = Double.parseDouble(txtSSpan2.getText());
        String sspn = decimalCheck(sSpan);
        double sweep = Double.parseDouble(txtSweep2.getText());
        String savsi = decimalCheck(sweep);



        double horzPos = Double.parseDouble(txtHorzWingPos3.getText());
        String xv = decimalCheck(horzPos);
        double vertPos = Double.parseDouble(txtVertWingPos3.getText());
        String zv = decimalCheck(vertPos);
        double sSpanE = Double.parseDouble(txtSSPNE3.getText());
        String sspne = decimalCheck(sSpanE);
        double sweepChord = Double.parseDouble(txtCHSTAT2.getText());
        sweepChord = sweepChord / 100; // to get to decimal
        String chstat = decimalCheck(sweepChord);

        // then add data to horz line
        String firstLine = "$VTPLNF CHRDR=" + chrdr + ",CHRDTP=" + chrdtp
                + ",SSPN=" + sspn
                + ",SSPNE=" + sspne + ",TWISTA=0.0,";
        String secondLine = "CHSTAT=" + chstat
                + ",TYPE=1.0" + ",SAVSI=" + savsi
                + "$";
        tailVertLines.add(firstLine);
        tailVertLines.add(secondLine);

        // then add naca code
        String thirdLine = "";
        int nacaIndex = comboVertNACA.getSelectedIndex();
        switch (nacaIndex) {
            case 0:
                thirdLine = "NACA-V-1-" + txtHorzCode.getText();
                break;
            case 1:
                thirdLine = "NACA-V-4-" + txtHorzCode.getText();
                break;
            case 2:
                thirdLine = "NACA-V-5-" + txtHorzCode.getText();
                break;
            default:
                thirdLine = "NACA-V-6-" + txtHorzCode.getText();
                break;
        }
        // then add to tail horz lines
        tailVertLines.add(thirdLine);
        // then add data that belongs in the synths list
        String synthsLine = "XV=" + xv + ",ZV=" + zv;
        synthsLines.set(3, synthsLine);

        //test
        //System.out.println(tailVertLines.get(0));
        //System.out.println(tailVertLines.get(1));
        //System.out.println(tailVertLines.get(2));
        //System.out.println(synthsLines.get(3));
    }

    private void btnSaveNextPropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveNextPropActionPerformed
        pnlInnerTabs.setSelectedIndex(5);
        // need to add to arrays also
    }//GEN-LAST:event_btnSaveNextPropActionPerformed

    private void btnSaveNextControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveNextControlActionPerformed
        boolean acceptableData = true;
        String warning = "";
        elevatorLines.clear();
        if (acceptableData == true && radioButtonElevator.isSelected() == true) {
            pnlInnerTabs.setSelectedIndex(6);
            // need to add to arrays also
            double innerChord = Double.parseDouble(txtChrdfi.getText());
            String chrdfi = decimalCheck(innerChord);
            double outerChord = Double.parseDouble(txtChrdfo.getText());
            String chrdfo = decimalCheck(outerChord);
            double inSpan = Double.parseDouble(txtSpanfi.getText());
            String spanfi = decimalCheck(inSpan);
            double outSpan = Double.parseDouble(txtSpanfo.getText());
            String spanfo = decimalCheck(outSpan);
            int elevType = comboElevType.getSelectedIndex();
            String ntype = (elevType + 1) + ".0";
            int numDeflections = comboElevDef.getSelectedIndex();
            numDeflections++;
            String ndelta = numDeflections + ".0";
            String delta = getElevDeflections(numDeflections);
            String firstLine = "$SYMFLP FTYPE=1.0,SPANFI=" + spanfi + ",SPANFO=" + spanfo
                    + ",CHRDFI=" + chrdfi + ",CHRDFO=" + chrdfo + ",";
            String secondLine = "NTYPE=" + ntype + ",NDELTA=" + ndelta + ",";
            String thirdLine = "DELTA(1)=" + delta + "$";
            //System.out.println("Lines are as follows:");
            //System.out.println(firstLine);
            //System.out.println(secondLine);
            //System.out.println(thirdLine);
            elevatorLines.add(firstLine);
            elevatorLines.add(secondLine);
            elevatorLines.add(thirdLine);
        } else if (acceptableData == false && radioButtonElevator.isSelected() == true) {
        } else {
            pnlInnerTabs.setSelectedIndex(6);
        }
    }//GEN-LAST:event_btnSaveNextControlActionPerformed

    private String getElevDeflections(int count) {
        // this is a mess due to so many errors arising
        // will need to clean up later, working for now
        String returnString = "";
        double def1 = Double.parseDouble(txtEDef1.getText());
        String first = decimalCheck(def1);
        returnString += first;
        String second = ",";
        String third = ",";
        String fourth = ",";
        String fifth = ",";
        String sixth = ",";
        String seventh = ",";
        String eighth = ",";
        String ninth = ",";
        if (count > 1) {
            double def2 = Double.parseDouble(txtEDef2.getText());
            second += decimalCheck(def2);
            returnString += second;
        }
        if (count > 2) {
            double def3 = Double.parseDouble(txtEDef3.getText());
            third += decimalCheck(def3);
            returnString += third;
        }
        if (count > 3) {
            double def4 = Double.parseDouble(txtEDef4.getText());
            fourth += decimalCheck(def4);
            returnString += fourth;
        }
        if (count > 4) {
            double def5 = Double.parseDouble(txtEDef5.getText());
            fifth += decimalCheck(def5);
            returnString += fifth;
        }
        if (count > 5) {
            double def6 = Double.parseDouble(txtEDef6.getText());
            sixth += decimalCheck(def6);
            returnString += sixth;
        }
        if (count > 6) {
            double def7 = Double.parseDouble(txtEDef7.getText());
            seventh += decimalCheck(def7);
            returnString += seventh;
        }
        if (count > 7) {
            double def8 = Double.parseDouble(txtEDef8.getText());
            eighth += decimalCheck(def8);
            returnString += eighth;
        }
        if (count > 8) {
            double def9 = Double.parseDouble(txtEDef9.getText());
            ninth += decimalCheck(def9);
            returnString += ninth;
        }
        return returnString;
    }
    private void btnSaveAndRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveAndRunActionPerformed

        boolean acceptableData = true;
        String warning = "";
        fltconLines.clear();
        // check data here to make sure no letters, blanks etc.

        // then act according to whether data was acceptable
        if (acceptableData == true) {
            // go to next tab
            pnlMainTab.setEnabledAt(1, true);
            pnlMainTab.setSelectedIndex(1);
            double machNum = Double.parseDouble(txtMach.getText());
            String mach = decimalCheck(machNum);
            double weight = Double.parseDouble(txtAircraftWeight.getText());
            String wt = decimalCheck(weight);
            double altitude = Double.parseDouble(txtAltitude.getText());
            String alt = decimalCheck(altitude);
            double horzCOG = Double.parseDouble(txtHorzCOG.getText());
            String xcg = decimalCheck(horzCOG);
            double vertCOG = Double.parseDouble(txtVertCOG.getText());
            String zcg = decimalCheck(vertCOG);

            int index = comboAng.getSelectedIndex();
            index += 2;
            String alphaValues = getAnglesAttack(index);

            String firstLine = "$FLTCON NMACH=1.0,MACH(1)=" + mach + ",WT=" + wt + ",NALT=1.0,ALT(1)="
                    + alt + ",NALPHA=" + index + ".0,";
            String secondLine = "ALSCHD(1)=" + alphaValues + ",";
            String thirdLine = "GAMMA=0.0,LOOP=2.0$";

            //System.out.println("The lines are as follows:");
            //System.out.println(firstLine);
            //System.out.println(secondLine);
            //System.out.println(thirdLine);
            fltconLines.add(firstLine);
            fltconLines.add(secondLine);
            fltconLines.add(thirdLine);

            String synthsLine = "$SYNTHS XCG=" + xcg + ",ZCG=" + zcg;
            synthsLines.set(0, synthsLine);
        } else {
            WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("There is an error in the data input. Details are as follows: " + warning);
        }
    }//GEN-LAST:event_btnSaveAndRunActionPerformed

    private String getAnglesAttack(int count) {
        // yet another method that is a massive mess due to errors popping up
        // will need to clean up later, working for now
        String returnString = "";
        // will always have at least two values
        // also start everything past the first with commas
        double ang1 = Double.parseDouble(txtAng1.getText());
        String first = decimalCheck(ang1);
        double ang2 = Double.parseDouble(txtAng2.getText());
        String second = "," + decimalCheck(ang2);
        String third = ",";
        String fourth = ",";
        String fifth = ",";
        String sixth = ",";
        String seventh = ",";
        String eighth = ",";
        String ninth = ",";
        String tenth = ",";
        String eleventh = ",";
        String twelfth = ",";
        String thirteenth = ",";
        String fourteenth = ",";
        String fifteenth = ",";
        String sixteenth = ",";
        String seventeenth = ",";
        String eighteenth = ",";
        String nineteenth = ",";
        String twentieth = ",";

        returnString += first;
        returnString += second;
        if (count > 2) {
            double ang3 = Double.parseDouble(txtAng3.getText());
            third += decimalCheck(ang3);
            returnString += third;
        }
        if (count > 3) {
            double ang4 = Double.parseDouble(txtAng4.getText());
            fourth += decimalCheck(ang4);
            returnString += fourth;
        }
        if (count > 4) {
            double ang5 = Double.parseDouble(txtAng5.getText());
            fifth += decimalCheck(ang5);
            returnString += fifth;
        }
        if (count > 5) {
            double ang6 = Double.parseDouble(txtAng6.getText());
            sixth += decimalCheck(ang6);
            returnString += sixth;
        }
        if (count > 6) {
            double ang7 = Double.parseDouble(txtAng7.getText());
            seventh += decimalCheck(ang7);
            returnString += seventh;
        }
        if (count > 7) {
            double ang8 = Double.parseDouble(txtAng8.getText());
            eighth += decimalCheck(ang8);
            returnString += eighth;
        }
        if (count > 8) {
            double ang9 = Double.parseDouble(txtAng9.getText());
            ninth += decimalCheck(ang9);
            returnString += ninth;
        }
        if (count > 9) {
            double ang10 = Double.parseDouble(txtAng10.getText());
            tenth += decimalCheck(ang10);
            returnString += tenth;
        }
        if (count > 10) {
            double ang11 = Double.parseDouble(txtAng11.getText());
            eleventh += decimalCheck(ang11);
            returnString += eleventh;
        }
        if (count > 11) {
            double ang12 = Double.parseDouble(txtAng12.getText());
            twelfth += decimalCheck(ang12);
            returnString += twelfth;
        }
        if (count > 12) {
            double ang13 = Double.parseDouble(txtAng13.getText());
            thirteenth += decimalCheck(ang13);
            returnString += thirteenth;
        }
        if (count > 13) {
            double ang14 = Double.parseDouble(txtAng14.getText());
            fourteenth += decimalCheck(ang14);
            returnString += fourteenth;
        }
        if (count > 14) {
            double ang15 = Double.parseDouble(txtAng15.getText());
            fifteenth += decimalCheck(ang15);
            returnString += fifteenth;
        }
        if (count > 15) {
            double ang16 = Double.parseDouble(txtAng16.getText());
            sixteenth += decimalCheck(ang16);
            returnString += sixteenth;
        }
        if (count > 16) {
            double ang17 = Double.parseDouble(txtAng17.getText());
            seventeenth += decimalCheck(ang17);
            returnString += seventeenth;
        }
        if (count > 17) {
            double ang18 = Double.parseDouble(txtAng18.getText());
            eighteenth += decimalCheck(ang18);
            returnString += eighteenth;
        }
        if (count > 18) {
            double ang19 = Double.parseDouble(txtAng19.getText());
            nineteenth += decimalCheck(ang19);
            returnString += nineteenth;
        }
        if (count > 19) {
            double ang20 = Double.parseDouble(txtAng20.getText());
            twentieth += decimalCheck(ang20);
            returnString += twentieth;
        }

        return returnString;
    }

    private void btnNextStationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextStationActionPerformed
        int current = comboStation.getSelectedIndex();
        int max = comboStation.getItemCount();
        max--;
        if (current < max) {
            current++;
            comboStation.setSelectedIndex(current);
            previousBodyIndex = current;
            // add data to station then
            double dist = Double.parseDouble(txtDistanceNose.getText());
            double radius = Double.parseDouble(txtRadiusBody.getText());
            double high = Double.parseDouble(txtHighestBody.getText());
            double low = Double.parseDouble(txtLowestBody.getText());
            double area = Double.parseDouble(txtAreaBody.getText());
            double circum = Double.parseDouble(txtCircumBody.getText());

            double otherRadius = (high - low) / 2;
            if (otherRadius < 0.0) {
                otherRadius = otherRadius * -1;
            }
            // need to autocalculate if necessary here
            // need to ignore this if all zero i.e. first section and last section
            if (checkAutoCalculateBody.isSelected() == true) {
                // first case is no area or circum, works for all circles and ovals
                if (area == 0.0 && circum == 0.0 && high != 0.0 && low != 0.0 && radius != 0.0) {
                    if (otherRadius == radius) {
                        area = 3.14159265 * radius * radius;
                        circum = 2 * 3.14159265 * radius;
                    }
                    if (otherRadius != radius) {
                        area = calculateArea(radius, high, low);
                        circum = calculateCircumference(radius, high, low);
                    }
                }

                // second case is radius only
                if (area == 0.0 && high == 0.0 && low == 0.0 && circum == 0.0 && radius != 0.0) {
                    area = 3.14159265 * radius * radius;
                    circum = 2 * 3.14159265 * radius;
                    high = radius;
                    low = radius;
                }
                // third case is area only
                if (radius == 0.0 && high == 0.0 & low == 0.0 & circum == 0.0 && area != 0.0) {
                    radius = Math.sqrt((area / 3.14159265));
                    high = radius;
                    low = radius;
                    circum = 2 * 3.14159265 * radius;
                }
                // the final case is where all values are entered anyway so ignore
            }
            fuselageDistance.set(current - 1, dist);
            fuselageRadius.set(current - 1, radius);
            fuselageUpper.set(current - 1, high);
            fuselageLower.set(current - 1, low);
            fuselageArea.set(current - 1, area);
            fuselageCircumference.set(current - 1, circum);
            // then display next station data
            txtDistanceNose.setText(fuselageDistance.get(current).toString());
            txtRadiusBody.setText(fuselageRadius.get(current).toString());
            txtHighestBody.setText(fuselageUpper.get(current).toString());
            txtLowestBody.setText(fuselageLower.get(current).toString());
            txtAreaBody.setText(fuselageArea.get(current).toString());
            txtCircumBody.setText(fuselageCircumference.get(current).toString());

            if (low < 0.0) {
                WorkInProgressWindow wip = new WorkInProgressWindow();
                wip.setVisible(true);
                wip.changeText("Lower edge of the fuselage is usually negative");
            }
        }
    }//GEN-LAST:event_btnNextStationActionPerformed

    private void btnPreviousStationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousStationActionPerformed
        int current = comboStation.getSelectedIndex();
        if (current > 0) {
            current--;
            comboStation.setSelectedIndex(current);
            previousBodyIndex = current;
            // add data to station then
            double dist = Double.parseDouble(txtDistanceNose.getText());
            double radius = Double.parseDouble(txtRadiusBody.getText());
            double high = Double.parseDouble(txtHighestBody.getText());
            double low = Double.parseDouble(txtLowestBody.getText());
            double area = Double.parseDouble(txtAreaBody.getText());
            double circum = Double.parseDouble(txtCircumBody.getText());

            if (checkAutoCalculateBody.isSelected() == true) {
                // first case is no area or circum, works for all circles and ovals
                if (area == 0.0 && circum == 0.0 && high != 0.0 && low != 0.0 && radius != 0.0) {
                    if (high == radius) {
                        if (low == radius) {
                            area = 3.14159265 * radius * radius;
                            circum = 2 * 3.14159265 * radius;
                        }
                        if (low != radius) {
                            area = calculateArea(radius, high, low);
                            circum = calculateCircumference(radius, high, low);
                        }
                    }
                    if (low == radius) {
                        if (high != radius) {
                            area = calculateArea(radius, high, low);
                            circum = calculateCircumference(radius, high, low);
                        }
                    }
                    if (low != radius && high != radius) {
                        area = calculateArea(radius, high, low);
                        circum = calculateCircumference(radius, high, low);
                    }
                }
                // second case is radius only
                if (area == 0.0 && high == 0.0 && low == 0.0 && circum == 0.0 && radius != 0.0) {
                    area = 3.14159265 * radius * radius;
                    circum = 2 * 3.14159265 * radius;
                    high = radius;
                    low = radius;
                }
                // third case is area only
                if (radius == 0.0 && high == 0.0 & low == 0.0 & circum == 0.0 && area != 0.0) {
                    radius = Math.sqrt((area / 3.14159265));
                    high = radius;
                    low = radius;
                    circum = 2 * 3.14159265 * radius;
                }
                // the final case is where all values are entered anyway so ignore
            }


            fuselageDistance.set(current + 1, dist);
            fuselageRadius.set(current + 1, radius);
            fuselageUpper.set(current + 1, high);
            fuselageLower.set(current + 1, low);
            fuselageArea.set(current + 1, area);
            fuselageCircumference.set(current + 1, circum);
            // then display next station data
            txtDistanceNose.setText(fuselageDistance.get(current).toString());
            txtRadiusBody.setText(fuselageRadius.get(current).toString());
            txtHighestBody.setText(fuselageUpper.get(current).toString());
            txtLowestBody.setText(fuselageLower.get(current).toString());
            txtAreaBody.setText(fuselageArea.get(current).toString());
            txtCircumBody.setText(fuselageCircumference.get(current).toString());

            if (low < 0.0) {
                WorkInProgressWindow wip = new WorkInProgressWindow();
                wip.setVisible(true);
                wip.changeText("Lower edge of the fuselage is usually negative");
            }
        }
    }//GEN-LAST:event_btnPreviousStationActionPerformed

    private void comboStationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboStationActionPerformed
        // editing stuff here messes up the change buttons unfortunately!
    }//GEN-LAST:event_comboStationActionPerformed

    private void btnClearBodyCurrentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearBodyCurrentActionPerformed
        txtDistanceNose.setText("0.0");
        txtRadiusBody.setText("0.0");
        txtHighestBody.setText("0.0");
        txtLowestBody.setText("0.0");
        txtAreaBody.setText("0.0");
        txtCircumBody.setText("0.0");
    }//GEN-LAST:event_btnClearBodyCurrentActionPerformed

    private void btnClearWingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearWingActionPerformed
        txtRootChord.setText("0");
        txtTipChord.setText("0");
        txtSSpan.setText("0");
        txtSweep.setText("0");
        txtTwist.setText("0");
        txtDihedral.setText("0");
        txtIncidence.setText("0");
        comboWingType.setSelectedIndex(0);

    }//GEN-LAST:event_btnClearWingActionPerformed

    private void btnVisualBodyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisualBodyActionPerformed
        // open WIP warning for now
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("The visual model feature is not currently available.");
    }//GEN-LAST:event_btnVisualBodyActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        // open WIP warning for now
        FullGuide wip = new FullGuide();
        wip.setVisible(true);
    }//GEN-LAST:event_jButton25ActionPerformed

    private void btnVisualWingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisualWingActionPerformed
        // open WIP warning for now
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("The visual model feature is not currently available.");
    }//GEN-LAST:event_btnVisualWingActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        // open WIP warning for now
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("The visual model feature is not currently available.");
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // open WIP warning for now
        FullGuide wip = new FullGuide();
        wip.setVisible(true);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void btnHelp1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHelp1ActionPerformed
        // open WIP warning for now
        FullGuide wip = new FullGuide();
        wip.setVisible(true);
    }//GEN-LAST:event_btnHelp1ActionPerformed

    private void btnHelp2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHelp2ActionPerformed
        // open WIP warning for now
        FullGuide wip = new FullGuide();
        wip.setVisible(true);
    }//GEN-LAST:event_btnHelp2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // open WIP warning for now
        FullGuide wip = new FullGuide();
        wip.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // open WIP warning for now
        FullGuide wip = new FullGuide();
        wip.setVisible(true);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // open WIP warning for now
        FullGuide wip = new FullGuide();
        wip.setVisible(true);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void btnHelpOtherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHelpOtherActionPerformed
        // open WIP warning for now
        FullGuide wip = new FullGuide();
        wip.setVisible(true);
    }//GEN-LAST:event_btnHelpOtherActionPerformed

    private void btnQuickReviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuickReviewActionPerformed
        // gives quick rundown of which sections have been input
        QuickDataReview qr = new QuickDataReview();
        qr.setVisible(true);
        // then check to see what data has been saved
        if (bodyLines.isEmpty() == false) {
            qr.changeBody(true);
        }
        if (wingLines.isEmpty() == false) {
            qr.changeWing(true);
            qr.changeOther(true);
        }
        if (tailHorzLines.isEmpty() == false) {
            qr.changeHorz(true);
            qr.changeOther(true);

        }
        if (tailVertLines.isEmpty() == false) {
            qr.changeVert(true);
            qr.changeOther(true);

        }
        if (elevatorLines.isEmpty() == false) {
            qr.changeElev(true);
        }
        if (aileronLines.isEmpty() == false) {
            qr.changeAil(true);
        }
        if (flapLines.isEmpty() == false) {
            qr.changeFlap(true);
        }
        if (finLines.isEmpty() == false) {
            qr.changeFin(true);
            qr.changeOther(true);
        }
        if (propLines.isEmpty() == false) {
            qr.changeProp(true);
        }
        if (fltconLines.isEmpty() == false) {
            qr.changeFLTCON(true);
        }

    }//GEN-LAST:event_btnQuickReviewActionPerformed

    private void btnClearOtherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearOtherActionPerformed
        txtAng1.setText(null);
        txtAng2.setText(null);
        txtAng3.setText(null);
        txtAng4.setText(null);
        txtAng5.setText(null);
        txtAng6.setText(null);
        txtAng7.setText(null);
        txtAng8.setText(null);
        txtAng9.setText(null);
        txtAng10.setText(null);
        txtAng11.setText(null);
        txtAng12.setText(null);
        txtAng13.setText(null);
        txtAng14.setText(null);
        txtAng15.setText(null);
        txtAng16.setText(null);
        txtAng17.setText(null);
        txtAng18.setText(null);
        txtAng19.setText(null);
        txtAng20.setText(null);
        txtMach.setText(null);
        txtAircraftWeight.setText(null);
        txtAltitude.setText(null);
        comboAng.setSelectedIndex(18);
        txtCentreGravity.setText(null);
    }//GEN-LAST:event_btnClearOtherActionPerformed

    private void btnManualBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManualBeginActionPerformed
        pnlInnerTabs.setSelectedIndex(1);
        pnlInnerTabs.setEnabledAt(1, true);
        pnlInnerTabs.setEnabledAt(2, true);
        pnlInnerTabs.setEnabledAt(3, true);
        pnlInnerTabs.setEnabledAt(4, true);
        pnlInnerTabs.setEnabledAt(5, true);
        pnlInnerTabs.setEnabledAt(6, true);
        menuInsert.setEnabled(true);
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("Please note that currently only the default units work "
                + "(feet, deg) and choosing metres or radians will not in fact "
                + "change anything. You can insert sample data from the Insert menu");
    }//GEN-LAST:event_btnManualBeginActionPerformed

    private void radioButtonFlapsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonFlapsActionPerformed

        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("Ailerons and flaps currently aren't supported");
        //radioButtonNoCont.setSelected(false);
        //if (radioButtonElevator.isSelected() == false) {
        //    if (radioButtonAileron.isSelected() == false) {
        //        if (radioButtonFlaps.isSelected() == false) {
        //            radioButtonNoCont.setSelected(true);
        //       }
        //    }
        //}
        radioButtonFlaps.setSelected(false);

    }//GEN-LAST:event_radioButtonFlapsActionPerformed

    private void btnDatcomEngineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDatcomEngineActionPerformed
        btnNoProp.setSelected(true);
        btnDatcomEngine.setSelected(false);
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("Propulsion is currently not supported");
    }//GEN-LAST:event_btnDatcomEngineActionPerformed

    private void btnUserEngineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserEngineActionPerformed
        btnNoProp.setSelected(true);
        btnUserEngine.setSelected(false);
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("Propulsion is currently not supported");
    }//GEN-LAST:event_btnUserEngineActionPerformed

    private void btnNoPropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoPropActionPerformed
        btnDatcomEngine.setSelected(false);
        btnUserEngine.setSelected(false);

    }//GEN-LAST:event_btnNoPropActionPerformed

    private void btnAC3DOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAC3DOffActionPerformed
        btnAC3DDispOff.setSelected(true);
        btnDispFuselageOff.setSelected(true);
    }//GEN-LAST:event_btnAC3DOffActionPerformed

    private void btnAC3DDispOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAC3DDispOnActionPerformed
        btnAC3DOn.setSelected(true);
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("These additional features are not currently working so please "
                + "ignore this tab for now");
    }//GEN-LAST:event_btnAC3DDispOnActionPerformed

    private void btnDispFuselageOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDispFuselageOnActionPerformed
        btnAC3DOn.setSelected(true);
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("These additional features are not currently working so please "
                + "ignore this tab for now");
    }//GEN-LAST:event_btnDispFuselageOnActionPerformed

    private void checkBodyOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBodyOnlyActionPerformed

        if (checkBodyOnly.isSelected() == true) {
            WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("This feature is not guaranteed to work, so proceed with caution.");
            // update previous list with current data first
            for (int i = 1; i <= 6; i++) {
                previousList.set(i, sectionList.get(i));
            }
            // Update sectionlist to ignore index 1 through 6
            for (int i = 1; i <= 6; i++) {
                sectionList.set(i, 0);
            }
            // need to set the colours of everything and disable
            // probably best to use a method for this
            bodyOnlyChanges(false);
        } else {

            // assign previouslist to sectionlist
            // need to check if this actually works tbh
            for (int i = 1; i <= 6; i++) {
                sectionList.set(i, previousList.get(i));
            }

            // make the changes
            bodyOnlyChanges(true);
        }
    }//GEN-LAST:event_checkBodyOnlyActionPerformed

    private void checkHorzStabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkHorzStabActionPerformed
        // need to update sectionlist
        // assign to boolean for clarity
        boolean check = checkHorzStab.isSelected();
        if (check == true) {
            sectionList.set(2, 1);

        } else {
            sectionList.set(2, 0);

        }
        // then disable/enable stuff
        horzStabiliserChanges(check);
    }//GEN-LAST:event_checkHorzStabActionPerformed

    private void checkVertStabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkVertStabActionPerformed
        // need to update sectionlist
        // assign to boolean for clarity
        boolean check = checkVertStab.isSelected();
        if (check == true) {
            sectionList.set(3, 1);

        } else {
            sectionList.set(3, 0);

        }
        // then disable/enable stuff
        vertStabiliserChanges(check);
    }//GEN-LAST:event_checkVertStabActionPerformed

    private void checkAutoSSPNE1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAutoSSPNE1ActionPerformed
        // open WIP warning for now
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("This feature has been trialled but is not fully functional currently. Will be finalised later.");
    }//GEN-LAST:event_checkAutoSSPNE1ActionPerformed

    private void checkAutoSSPNE2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAutoSSPNE2ActionPerformed
        // open WIP warning for now
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("This feature has been trialled but is not fully functional currently. Will be finalised later.");
    }//GEN-LAST:event_checkAutoSSPNE2ActionPerformed

    private void checkAutoSSPNEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAutoSSPNEActionPerformed
        // open WIP warning for now
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("This feature has been trialled but is not fully functional currently. Will be finalised later.");
    }//GEN-LAST:event_checkAutoSSPNEActionPerformed

    private void rbtnCurrentSessionOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnCurrentSessionOutputActionPerformed
        btnImportResults.setEnabled(true);
    }//GEN-LAST:event_rbtnCurrentSessionOutputActionPerformed

    private void rbtnDifferentSessionOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnDifferentSessionOutputActionPerformed
        rbtnCurrentSessionOutput.setSelected(true);
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("This feature is currently not available but has been tested.");
    }//GEN-LAST:event_rbtnDifferentSessionOutputActionPerformed

    private void menuSaveDCMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveDCMActionPerformed
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("This feature is currently not available, but when implemented it will allow the user to save progress"
                + " to a .dcm file that can be imported automatically upon next opening of the program.");
    }//GEN-LAST:event_menuSaveDCMActionPerformed

    private void menuSaveXLSXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveXLSXActionPerformed
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("This feature is not currently available, but when "
                + "implemented it will wipe all progress and bring the user"
                + "back to the start page");
    }//GEN-LAST:event_menuSaveXLSXActionPerformed

    private void btnOutputXLSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOutputXLSActionPerformed
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("This feature is not currently available, but when "
                + "implemented it will save the output to a .xls file and "
                + "open it. This will allow the user to export the data"
                + " per se.");
    }//GEN-LAST:event_btnOutputXLSActionPerformed

    private void pnlAdditionalOptionsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlAdditionalOptionsMousePressed
    }//GEN-LAST:event_pnlAdditionalOptionsMousePressed

    private void jPanel30MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel30MousePressed
    }//GEN-LAST:event_jPanel30MousePressed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("These additional features are not currently working so please "
                + "ignore this tab for now");
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("These additional features are not currently working so please "
                + "ignore this tab for now");
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton6ActionPerformed
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("These additional features are not currently working so please "
                + "ignore this tab for now");
    }//GEN-LAST:event_jRadioButton6ActionPerformed

    private void btnAC3DOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAC3DOnActionPerformed
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("These additional features are not currently working so please "
                + "ignore this tab for now");
    }//GEN-LAST:event_btnAC3DOnActionPerformed

    private void jRadioButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton8ActionPerformed
        // TODO add your handling code here:
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("These additional features are not currently working so please "
                + "ignore this tab for now");
    }//GEN-LAST:event_jRadioButton8ActionPerformed

    private void jRadioButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton10ActionPerformed
        // TODO add your handling code here:
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("These additional features are not currently working so please "
                + "ignore this tab for now");
    }//GEN-LAST:event_jRadioButton10ActionPerformed

    private void jRadioButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton12ActionPerformed
        // TODO add your handling code here:
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("These additional features are not currently working so please "
                + "ignore this tab for now");
    }//GEN-LAST:event_jRadioButton12ActionPerformed

    private void jRadioButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton14ActionPerformed
        // TODO add your handling code here:
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("These additional features are not currently working so please "
                + "ignore this tab for now");
    }//GEN-LAST:event_jRadioButton14ActionPerformed

    private void checkCOGAutoCalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkCOGAutoCalcActionPerformed
        if (checkCOGAutoCalc.isSelected()) {
            txtCentreGravity.setBackground(Color.white);
            txtCentreGravity.setEditable(true);
            btnAutoCalcCOG.setEnabled(true);
        } else {
            txtCentreGravity.setBackground(Color.gray);
            txtCentreGravity.setEditable(false);
            btnAutoCalcCOG.setEnabled(false);
        }
    }//GEN-LAST:event_checkCOGAutoCalcActionPerformed

    private void btnAutoCalcCOGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutoCalcCOGActionPerformed
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("This feature has been temporarily removed due "
                + "to errors. It enters in the position of the COG based "
                + "on calculations for the MAC length and placement.");
    }//GEN-LAST:event_btnAutoCalcCOGActionPerformed

    private void btnMultiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMultiActionPerformed
        //WorkInProgressWindow wip = new WorkInProgressWindow();
        //wip.setVisible(true);
        //wip.changeText("The multiple analysis feature has been temporarily disabled. "
        //        + "It is confirmed to work although support for most of the remaining"
        //        + " variables has to be completed. However you can still preview the"
        //        + " capabilities of the feature by scrolling through the options."
        //        + " The units associated with each option will not auto-update yet.");
        panelMultiRun.setVisible(true);
    }//GEN-LAST:event_btnMultiActionPerformed

    private void btnSingleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSingleActionPerformed
        panelMultiRun.setVisible(false);
    }//GEN-LAST:event_btnSingleActionPerformed

    private void checkVertStab1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkVertStab1ActionPerformed
        if (checkVertStab1.isSelected() == true) {
            WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("Ventral fins are currently not supported.");
        }
        checkVertStab1.setSelected(false);
    }//GEN-LAST:event_checkVertStab1ActionPerformed

    private void checkTwinFinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkTwinFinActionPerformed
        if (checkTwinFin.isSelected() == true) {
            WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("Not working yet.");
        }
        checkTwinFin.setSelected(false);
    }//GEN-LAST:event_checkTwinFinActionPerformed

    private void checkTwinVertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkTwinVertActionPerformed
        if (checkTwinVert.isSelected() == true) {
            WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("Not working yet.");
        }
        checkTwinVert.setSelected(false);
    }//GEN-LAST:event_checkTwinVertActionPerformed

    private void jRadioButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton15ActionPerformed
        rbtnCurrentSessionOutput.setSelected(true);
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("Currently not working, but will provide function"
                + " similar to running multiple analysis tool and then "
                + "providing analysis");
    }//GEN-LAST:event_jRadioButton15ActionPerformed

    private void comboElevDefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboElevDefActionPerformed
        // need to first reset all to make editable
        txtEDef1.setEditable(true);
        txtEDef2.setEditable(true);
        txtEDef3.setEditable(true);
        txtEDef4.setEditable(true);
        txtEDef5.setEditable(true);
        txtEDef6.setEditable(true);
        txtEDef7.setEditable(true);
        txtEDef8.setEditable(true);
        txtEDef9.setEditable(true);
        // then reset colours to white
        txtEDef1.setBackground(Color.white);
        txtEDef2.setBackground(Color.white);
        txtEDef3.setBackground(Color.white);
        txtEDef4.setBackground(Color.white);
        txtEDef5.setBackground(Color.white);
        txtEDef6.setBackground(Color.white);
        txtEDef7.setBackground(Color.white);
        txtEDef8.setBackground(Color.white);
        txtEDef9.setBackground(Color.white);
        // then based on number, disable and change colours
        int index = comboElevDef.getSelectedIndex();
        elevComboChanges(index + 1);

    }//GEN-LAST:event_comboElevDefActionPerformed

    private void checkAutoCalculateBodyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAutoCalculateBodyActionPerformed
        if (checkAutoCalculateBody.isSelected() == true) {
            WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("This feature will autocalculate the remaining values"
                    + " once the user switches to the next station or uses save at the bottom"
                    + " of the page. There may be some errors using this so please"
                    + " check the numbers.");
        }
    }//GEN-LAST:event_checkAutoCalculateBodyActionPerformed

    private void comboShapeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboShapeActionPerformed
    }//GEN-LAST:event_comboShapeActionPerformed

    private void checkNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkNameActionPerformed
        if (checkName.isSelected() == true) {
            txtAircraftName.setText("");
            txtAircraftName.setEditable(true);
            txtAircraftName.setBackground(Color.white);
        } else {
            txtAircraftName.setText("");
            txtAircraftName.setEditable(false);
            txtAircraftName.setBackground(Color.gray);
        }
    }//GEN-LAST:event_checkNameActionPerformed

    private void btnDampOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDampOnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDampOnActionPerformed

    private void btnDampOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDampOffActionPerformed
        if (btnDampOff.isSelected() == true) {
            WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("These features will be editable soon, they were removed due to errors.");
            btnDampOn.setSelected(true);

        }
    }//GEN-LAST:event_btnDampOffActionPerformed

    private void btnPartOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPartOffActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPartOffActionPerformed

    private void btnPartOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPartOnActionPerformed
        if (btnPartOn.isSelected() == true) {
            WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("These features will be editable soon, they were removed due to errors.");
            btnPartOff.setSelected(true);

        }
    }//GEN-LAST:event_btnPartOnActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // This here will insert sample data from the 737 file in manual
        // Pay attention to the fuselage data which is sent to the arrays
        // start off with wing data
        comboWingType.setSelectedIndex(1);
        txtRootChord.setText("23.3");
        txtTipChord.setText("5.31");
        txtSSpan.setText("47.4");
        txtSweep.setText("25.0");
        txtTwist.setText("-1.0");
        txtDihedral.setText("0.0");
        txtIncidence.setText("1.0");
        txtChordBreak.setText("12.85");
        txtOutSpan.setText("31.2");
        txtOutSweep.setText("25.0");
        txtOutDihedral.setText("6.0");
        txtCHSTAT.setText("25");
        comboTypeWing.setSelectedIndex(0);
        txtHorzWingPos.setText("34.9");
        txtVertWingPos.setText("-1.4");
        txtSSPNE.setText("41.2");
        comboNACA.setSelectedIndex(0);
        txtAirfoilCode.setText("4412");
        // next is tail data
        checkHorzStab.setSelected(false);
        checkVertStab.setSelected(false);
        checkVertStab.doClick();
        checkHorzStab.doClick();
        txtRootChord1.setText("11.9");
        txtTipChord1.setText("3.927");
        txtSweep1.setText("30.0");
        txtSSpan1.setText("20.8");
        txtSSPNE2.setText("19.5");
        txtCHSTAT1.setText("25");
        txtCHSTAT2.setText("25");
        txtDihedral1.setText("7.0");
        comboHorzNACA.setSelectedIndex(0);
        comboVertNACA.setSelectedIndex(0);
        txtHorzCode.setText("0012");
        txtVertCode.setText("0012");
        txtHorzWingPos2.setText("92.15");
        txtVertWingPos3.setText("5.0");
        txtHorzWingPos3.setText("81.2");
        txtVertWingPos2.setText("6.2");
        txtSSPNE3.setText("19.5");
        txtSSpan2.setText("21.4");
        txtRootChord2.setText("19.0");
        txtTipChord2.setText("4.8");
        txtSweep2.setText("35.0");
        // then elevator
        radioButtonElevator.setSelected(false);
        radioButtonElevator.doClick();
        comboElevType.setSelectedIndex(0);
        comboElevDef.setSelectedIndex(8);
        txtChrdfi.setText("2.5");
        txtChrdfo.setText("1.72");
        txtSpanfi.setText("1.3");
        txtSpanfo.setText("17.4");
        txtEDef1.setText("-30.0");
        txtEDef2.setText("-20.0");
        txtEDef3.setText("-10.0");
        txtEDef4.setText("-5.0");
        txtEDef5.setText("0.0");
        txtEDef6.setText("5.0");
        txtEDef7.setText("10.0");
        txtEDef8.setText("15.0");
        txtEDef9.setText("25.0");
        // then flight conditions/other
        txtMach.setText("0.5");
        txtAltitude.setText("0.0");
        txtAircraftWeight.setText("83000.0");
        comboAng.setSelectedIndex(10);
        txtAng1.setText("-4.0");
        txtAng2.setText("-2.0");
        txtAng3.setText("0.0");
        txtAng4.setText("2.0");
        txtAng5.setText("4.0");
        txtAng6.setText("8.0");
        txtAng7.setText("9.0");
        txtAng8.setText("10.0");
        txtAng9.setText("12.0");
        txtAng10.setText("14.0");
        txtAng11.setText("16.0");
        txtAng12.setText("18.0");
        txtHorzCOG.setText("48.8");
        txtVertCOG.setText("0.0");
        // finally add body data to: 1) tab
        comboNumberStations.setSelectedIndex(12);
        txtDistanceNose.setText("0.0");
        txtRadiusBody.setText("0.0");
        txtHighestBody.setText("0.0");
        txtLowestBody.setText("0.0");
        txtAreaBody.setText("0.0");
        txtCircumBody.setText("0.0");
        txtNoseLength.setText("2.0");
        txtAfterbodyLength.setText("20.0");
        checkBodyOnly.setSelected(true);
        checkBodyOnly.doClick();
        comboShape.setSelectedIndex(0);

        // finally add body data to: 2)arrays
        fuselageDistance.set(1, "1.38");
        fuselageDistance.set(2, "4.83");
        fuselageDistance.set(3, "6.9");
        fuselageDistance.set(4, "8.97");
        fuselageDistance.set(5, "13.8");
        fuselageDistance.set(6, "27.6");
        fuselageDistance.set(7, "70.75");
        fuselageDistance.set(8, "81.15");
        fuselageDistance.set(9, "84.55");
        fuselageDistance.set(10, "91.45");
        fuselageDistance.set(11, "98.35");
        fuselageDistance.set(12, "105.5");
        fuselageDistance.set(13, "105.7");
        fuselageUpper.set(1, "2.07");
        fuselageUpper.set(2, "3.45");
        fuselageUpper.set(3, "4.38");
        fuselageUpper.set(4, "5.87");
        fuselageUpper.set(5, "6.9");
        fuselageUpper.set(6, "8.28");
        fuselageUpper.set(7, "8.28");
        fuselageUpper.set(8, "8.28");
        fuselageUpper.set(9, "8.28");
        fuselageUpper.set(10, "7.94");
        fuselageUpper.set(11, "7.59");
        fuselageUpper.set(12, "7.5");
        fuselageUpper.set(13, "6.9");
        fuselageLower.set(1, "-1.73");
        fuselageLower.set(2, "-3.45");
        fuselageLower.set(3, "-3.8");
        fuselageLower.set(4, "-4.14");
        fuselageLower.set(5, "-4.49");
        fuselageLower.set(6, "-4.83");
        fuselageLower.set(7, "-4.83");
        fuselageLower.set(8, "-3.45");
        fuselageLower.set(9, "-2.76");
        fuselageLower.set(10, "-0.81");
        fuselageLower.set(11, "1.04");
        fuselageLower.set(12, "4.14");
        fuselageLower.set(13, "6.9");
        fuselageRadius.set(1, "1.38");
        fuselageRadius.set(2, "2.76");
        fuselageRadius.set(3, "3.45");
        fuselageRadius.set(4, "4.14");
        fuselageRadius.set(5, "5.18");
        fuselageRadius.set(6, "6.21");
        fuselageRadius.set(7, "6.21");
        fuselageRadius.set(8, "5.87");
        fuselageRadius.set(9, "5.52");
        fuselageRadius.set(10, "4.14");
        fuselageRadius.set(11, "2.76");
        fuselageRadius.set(12, "0.69");
        fuselageRadius.set(13, "0.0");
        fuselageArea.set(1, "8.23");
        fuselageArea.set(2, "28.89");
        fuselageArea.set(3, "44.31");
        fuselageArea.set(4, "65.06");
        fuselageArea.set(5, "92.63");
        fuselageArea.set(6, "128.81");
        fuselageArea.set(7, "127.81");
        fuselageArea.set(8, "108.11");
        fuselageArea.set(9, "95.68");
        fuselageArea.set(10, "56.88");
        fuselageArea.set(11, "28.39");
        fuselageArea.set(12, "3.64");
        fuselageArea.set(13, "0.0");
        fuselageCircumference.set(1, "10.43");
        fuselageCircumference.set(2, "19.63");
        fuselageCircumference.set(3, "23.77");
        fuselageCircumference.set(4, "28.86");
        fuselageCircumference.set(5, "34.2");
        fuselageCircumference.set(6, "40.12");
        fuselageCircumference.set(7, "40.12");
        fuselageCircumference.set(8, "36.87");
        fuselageCircumference.set(9, "34.68");
        fuselageCircumference.set(10, "26.76");
        fuselageCircumference.set(11, "19.03");
        fuselageCircumference.set(12, "8.07");
        fuselageCircumference.set(13, "0.0");
        // then simulate the clicks to save the data and go to run page
        btnSaveNextBody.doClick();
        btnSaveNextWing.doClick();
        btnSaveNextTail.doClick();
        btnSaveNextProp.doClick();
        btnSaveNextControl.doClick();
        btnSaveAndRun.doClick();
        txtAircraftName.setText("Boeing 737-800");
        // then pop the note giving a quick rundown
        //WorkInProgressWindow wip = new WorkInProgressWindow();
        //wip.setVisible(true);
        //wip.changeText("Data for 737-300 successfully imported, feel free to edit! "
        //        + "You can run the file straight away or go back and edit the data");
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jRadioButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton11ActionPerformed
        WorkInProgressWindow wip = new WorkInProgressWindow();
        wip.setVisible(true);
        wip.changeText("These additional features are not currently working so please "
                + "ignore this tab for now");
    }//GEN-LAST:event_jRadioButton11ActionPerformed

    private void btnTextualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTextualActionPerformed
        // find out what data is actually to be sent
        if (csvFlap.size() > 12) {
            AnalysisWindow analysis = new AnalysisWindow(csvBasic, csvFlap);
            analysis.setVisible(true);

        } else if (csvFlap.size() < 12) {
            AnalysisWindow analysis = new AnalysisWindow(csvBasic);
            analysis.setVisible(true);
        }

    }//GEN-LAST:event_btnTextualActionPerformed

    private void elevComboChanges(int count) {
        // leaving in changing first textbox for clarity only
        switch (count) {
            case 1:
                txtEDef2.setEditable(false);
                txtEDef3.setEditable(false);
                txtEDef4.setEditable(false);
                txtEDef5.setEditable(false);
                txtEDef6.setEditable(false);
                txtEDef7.setEditable(false);
                txtEDef8.setEditable(false);
                txtEDef9.setEditable(false);
                // colours
                txtEDef2.setBackground(Color.gray);
                txtEDef3.setBackground(Color.gray);
                txtEDef4.setBackground(Color.gray);
                txtEDef5.setBackground(Color.gray);
                txtEDef6.setBackground(Color.gray);
                txtEDef7.setBackground(Color.gray);
                txtEDef8.setBackground(Color.gray);
                txtEDef9.setBackground(Color.gray);
                break;
            case 2:
                txtEDef3.setEditable(false);
                txtEDef4.setEditable(false);
                txtEDef5.setEditable(false);
                txtEDef6.setEditable(false);
                txtEDef7.setEditable(false);
                txtEDef8.setEditable(false);
                txtEDef9.setEditable(false);

                txtEDef3.setBackground(Color.gray);
                txtEDef4.setBackground(Color.gray);
                txtEDef5.setBackground(Color.gray);
                txtEDef6.setBackground(Color.gray);
                txtEDef7.setBackground(Color.gray);
                txtEDef8.setBackground(Color.gray);
                txtEDef9.setBackground(Color.gray);
                break;
            case 3:
                txtEDef4.setEditable(false);
                txtEDef5.setEditable(false);
                txtEDef6.setEditable(false);
                txtEDef7.setEditable(false);
                txtEDef8.setEditable(false);
                txtEDef9.setEditable(false);

                txtEDef4.setBackground(Color.gray);
                txtEDef5.setBackground(Color.gray);
                txtEDef6.setBackground(Color.gray);
                txtEDef7.setBackground(Color.gray);
                txtEDef8.setBackground(Color.gray);
                txtEDef9.setBackground(Color.gray);
                break;
            case 4:
                txtEDef5.setEditable(false);
                txtEDef6.setEditable(false);
                txtEDef7.setEditable(false);
                txtEDef8.setEditable(false);
                txtEDef9.setEditable(false);

                txtEDef5.setBackground(Color.gray);
                txtEDef6.setBackground(Color.gray);
                txtEDef7.setBackground(Color.gray);
                txtEDef8.setBackground(Color.gray);
                txtEDef9.setBackground(Color.gray);
                break;
            case 5:
                txtEDef6.setEditable(false);
                txtEDef7.setEditable(false);
                txtEDef8.setEditable(false);
                txtEDef9.setEditable(false);

                txtEDef6.setBackground(Color.gray);
                txtEDef7.setBackground(Color.gray);
                txtEDef8.setBackground(Color.gray);
                txtEDef9.setBackground(Color.gray);
                break;
            case 6:
                txtEDef7.setEditable(false);
                txtEDef8.setEditable(false);
                txtEDef9.setEditable(false);

                txtEDef7.setBackground(Color.gray);
                txtEDef8.setBackground(Color.gray);
                txtEDef9.setBackground(Color.gray);
                break;
            case 7:
                txtEDef8.setEditable(false);
                txtEDef9.setEditable(false);

                txtEDef8.setBackground(Color.gray);
                txtEDef9.setBackground(Color.gray);
                break;
            case 8:
                txtEDef9.setEditable(false);

                txtEDef9.setBackground(Color.gray);
                break;
            default:
                break;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>


        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton btnAC3DDispOff;
    private javax.swing.JRadioButton btnAC3DDispOn;
    private javax.swing.JRadioButton btnAC3DOff;
    private javax.swing.JRadioButton btnAC3DOn;
    private javax.swing.JButton btnAutoCalcCOG;
    private javax.swing.JButton btnBeginIteration;
    private javax.swing.JButton btnClearBodyAll;
    private javax.swing.JButton btnClearBodyCurrent;
    private javax.swing.JButton btnClearOther;
    private javax.swing.JButton btnClearWing;
    private javax.swing.JRadioButton btnDampOff;
    private javax.swing.JRadioButton btnDampOn;
    private javax.swing.JRadioButton btnDatcomEngine;
    private javax.swing.JRadioButton btnDispFuselageOff;
    private javax.swing.JRadioButton btnDispFuselageOn;
    private javax.swing.JButton btnGraphical;
    private javax.swing.JButton btnHelp1;
    private javax.swing.JButton btnHelp2;
    private javax.swing.JButton btnHelpOther;
    private javax.swing.JButton btnImportOpen;
    private javax.swing.JButton btnImportResults;
    private javax.swing.JButton btnImportReview;
    private javax.swing.JButton btnManualBegin;
    private javax.swing.JRadioButton btnMulti;
    private javax.swing.JButton btnNextStation;
    private javax.swing.JRadioButton btnNoProp;
    private javax.swing.JButton btnOpenOutputLocation;
    private javax.swing.JButton btnOutputXLS;
    private javax.swing.JRadioButton btnPartOff;
    private javax.swing.JRadioButton btnPartOn;
    private javax.swing.JButton btnPlotData;
    private javax.swing.JButton btnPreviousStation;
    private javax.swing.JButton btnQuickReview;
    private javax.swing.JButton btnSaveAndRun;
    private javax.swing.JButton btnSaveNextBody;
    private javax.swing.JButton btnSaveNextControl;
    private javax.swing.JButton btnSaveNextProp;
    private javax.swing.JButton btnSaveNextTail;
    private javax.swing.JButton btnSaveNextWing;
    private javax.swing.JRadioButton btnSingle;
    private javax.swing.JButton btnTextual;
    private javax.swing.JRadioButton btnUserEngine;
    private javax.swing.JButton btnVisualBody;
    private javax.swing.JButton btnVisualWing;
    private javax.swing.JCheckBox checkAutoCalculateBody;
    private javax.swing.JCheckBox checkAutoSSPNE;
    private javax.swing.JCheckBox checkAutoSSPNE1;
    private javax.swing.JCheckBox checkAutoSSPNE2;
    private javax.swing.JCheckBox checkBodyOnly;
    private javax.swing.JCheckBox checkCOGAutoCalc;
    private javax.swing.JCheckBox checkHorzStab;
    private javax.swing.JCheckBox checkName;
    private javax.swing.JCheckBox checkTwinFin;
    private javax.swing.JCheckBox checkTwinVert;
    private javax.swing.JCheckBox checkVertStab;
    private javax.swing.JCheckBox checkVertStab1;
    private javax.swing.JComboBox comboAlpha;
    private javax.swing.JComboBox comboAlt1;
    private javax.swing.JComboBox comboAng;
    private javax.swing.JComboBox comboDegRad;
    private javax.swing.JComboBox comboElevDef;
    private javax.swing.JComboBox comboElevType;
    private javax.swing.JComboBox comboFtM1;
    private javax.swing.JComboBox comboFtM2;
    private javax.swing.JComboBox comboFtM3;
    private javax.swing.JComboBox comboFtM4;
    private javax.swing.JComboBox comboFtM5;
    private javax.swing.JComboBox comboFtM6;
    private javax.swing.JComboBox comboFtM7;
    private javax.swing.JComboBox comboFtM8;
    private javax.swing.JComboBox comboHorzNACA;
    private javax.swing.JComboBox comboIterationParameter;
    private javax.swing.JComboBox comboLength1;
    private javax.swing.JComboBox comboLength10;
    private javax.swing.JComboBox comboLength11;
    private javax.swing.JComboBox comboLength12;
    private javax.swing.JComboBox comboLength13;
    private javax.swing.JComboBox comboLength14;
    private javax.swing.JComboBox comboLength15;
    private javax.swing.JComboBox comboLength16;
    private javax.swing.JComboBox comboLength17;
    private javax.swing.JComboBox comboLength18;
    private javax.swing.JComboBox comboLength19;
    private javax.swing.JComboBox comboLength2;
    private javax.swing.JComboBox comboLength25;
    private javax.swing.JComboBox comboLength26;
    private javax.swing.JComboBox comboLength27;
    private javax.swing.JComboBox comboLength28;
    private javax.swing.JComboBox comboLength29;
    private javax.swing.JComboBox comboLength3;
    private javax.swing.JComboBox comboLength30;
    private javax.swing.JComboBox comboLength31;
    private javax.swing.JComboBox comboLength33;
    private javax.swing.JComboBox comboLength34;
    private javax.swing.JComboBox comboLength35;
    private javax.swing.JComboBox comboLength36;
    private javax.swing.JComboBox comboLength37;
    private javax.swing.JComboBox comboLength38;
    private javax.swing.JComboBox comboLength39;
    private javax.swing.JComboBox comboLength4;
    private javax.swing.JComboBox comboLength40;
    private javax.swing.JComboBox comboLength41;
    private javax.swing.JComboBox comboLength43;
    private javax.swing.JComboBox comboLength44;
    private javax.swing.JComboBox comboLength5;
    private javax.swing.JComboBox comboLength6;
    private javax.swing.JComboBox comboLength7;
    private javax.swing.JComboBox comboLength8;
    private javax.swing.JComboBox comboLength9;
    private javax.swing.JComboBox comboNACA;
    private javax.swing.JComboBox comboNACA1;
    private javax.swing.JComboBox comboNumberStations;
    private javax.swing.JComboBox comboPlotX;
    private javax.swing.JComboBox comboPlotY;
    private javax.swing.JComboBox comboProp;
    private javax.swing.JComboBox comboShape;
    private javax.swing.JComboBox comboStation;
    private javax.swing.JComboBox comboTypeWing;
    private javax.swing.JComboBox comboVertNACA;
    private javax.swing.JComboBox comboWeight1;
    private javax.swing.JComboBox comboWingType;
    private javax.swing.ButtonGroup groupAC3D;
    private javax.swing.ButtonGroup groupAC3DFuselage;
    private javax.swing.ButtonGroup groupAirfoilPlot;
    private javax.swing.ButtonGroup groupCSV;
    private javax.swing.ButtonGroup groupDamp;
    private javax.swing.ButtonGroup groupDisplayAC3D;
    private javax.swing.ButtonGroup groupFuselagePlot;
    private javax.swing.ButtonGroup groupImport;
    private javax.swing.ButtonGroup groupJSBSim;
    private javax.swing.ButtonGroup groupLog;
    private javax.swing.ButtonGroup groupMatlab;
    private javax.swing.ButtonGroup groupOldCSV;
    private javax.swing.ButtonGroup groupOutput;
    private javax.swing.ButtonGroup groupPartial;
    private javax.swing.ButtonGroup groupProp;
    private javax.swing.ButtonGroup groupRun;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox10;
    private javax.swing.JComboBox jComboBox11;
    private javax.swing.JComboBox jComboBox12;
    private javax.swing.JComboBox jComboBox13;
    private javax.swing.JComboBox jComboBox14;
    private javax.swing.JComboBox jComboBox15;
    private javax.swing.JComboBox jComboBox16;
    private javax.swing.JComboBox jComboBox17;
    private javax.swing.JComboBox jComboBox18;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JComboBox jComboBox9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel134;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel143;
    private javax.swing.JLabel jLabel144;
    private javax.swing.JLabel jLabel145;
    private javax.swing.JLabel jLabel146;
    private javax.swing.JLabel jLabel147;
    private javax.swing.JLabel jLabel148;
    private javax.swing.JLabel jLabel149;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private javax.swing.JRadioButton jRadioButton12;
    private javax.swing.JRadioButton jRadioButton13;
    private javax.swing.JRadioButton jRadioButton14;
    private javax.swing.JRadioButton jRadioButton15;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl10;
    private javax.swing.JLabel lbl11;
    private javax.swing.JLabel lbl12;
    private javax.swing.JLabel lbl13;
    private javax.swing.JLabel lbl14;
    private javax.swing.JLabel lbl15;
    private javax.swing.JLabel lbl16;
    private javax.swing.JLabel lbl17;
    private javax.swing.JLabel lbl18;
    private javax.swing.JLabel lbl19;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl20;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl4;
    private javax.swing.JLabel lbl5;
    private javax.swing.JLabel lbl6;
    private javax.swing.JLabel lbl7;
    private javax.swing.JLabel lbl8;
    private javax.swing.JLabel lbl9;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuInsert;
    private javax.swing.JMenuItem menuSaveDCM;
    private javax.swing.JMenu menuSaveOptions;
    private javax.swing.JMenuItem menuSaveXLSX;
    private javax.swing.JPanel panelMultiRun;
    private javax.swing.JPanel pnlAdditionalOptions;
    private javax.swing.JPanel pnlAng2;
    private javax.swing.JPanel pnlBody;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlImport;
    private javax.swing.JTabbedPane pnlInnerTabs;
    private javax.swing.JPanel pnlInput;
    private javax.swing.JPanel pnlMainRun;
    private javax.swing.JTabbedPane pnlMainTab;
    private javax.swing.JPanel pnlOther;
    private javax.swing.JPanel pnlOutput;
    private javax.swing.JPanel pnlProp;
    private javax.swing.JPanel pnlQuickPlot;
    private javax.swing.JPanel pnlRun;
    private javax.swing.JPanel pnlTail;
    private javax.swing.JPanel pnlWing;
    private javax.swing.JRadioButton radioButtonAileron;
    private javax.swing.JRadioButton radioButtonElevator;
    private javax.swing.JRadioButton radioButtonFlaps;
    private javax.swing.JRadioButton radioButtonImport;
    private javax.swing.JRadioButton radioButtonManual;
    private javax.swing.JRadioButton radioButtonNoCont;
    private javax.swing.JRadioButton rbtnCurrentSessionOutput;
    private javax.swing.JRadioButton rbtnDifferentSessionOutput;
    private javax.swing.JTextField txtAfterbodyLength;
    private javax.swing.JTextField txtAircraftName;
    private javax.swing.JTextField txtAircraftWeight;
    private javax.swing.JTextField txtAirfoilCode;
    private javax.swing.JTextField txtAirfoilCode1;
    private javax.swing.JTextField txtAltitude;
    private javax.swing.JTextField txtAng1;
    private javax.swing.JTextField txtAng10;
    private javax.swing.JTextField txtAng11;
    private javax.swing.JTextField txtAng12;
    private javax.swing.JTextField txtAng13;
    private javax.swing.JTextField txtAng14;
    private javax.swing.JTextField txtAng15;
    private javax.swing.JTextField txtAng16;
    private javax.swing.JTextField txtAng17;
    private javax.swing.JTextField txtAng18;
    private javax.swing.JTextField txtAng19;
    private javax.swing.JTextField txtAng2;
    private javax.swing.JTextField txtAng20;
    private javax.swing.JTextField txtAng3;
    private javax.swing.JTextField txtAng4;
    private javax.swing.JTextField txtAng5;
    private javax.swing.JTextField txtAng6;
    private javax.swing.JTextField txtAng7;
    private javax.swing.JTextField txtAng8;
    private javax.swing.JTextField txtAng9;
    private javax.swing.JTextField txtAreaBody;
    private javax.swing.JTextField txtCHSTAT;
    private javax.swing.JTextField txtCHSTAT1;
    private javax.swing.JTextField txtCHSTAT2;
    private javax.swing.JTextField txtCant;
    private javax.swing.JTextField txtCentreGravity;
    private javax.swing.JTextField txtChordBreak;
    private javax.swing.JTextField txtChrdfi;
    private javax.swing.JTextField txtChrdfo;
    private javax.swing.JTextField txtCircumBody;
    private javax.swing.JTextField txtDihedral;
    private javax.swing.JTextField txtDihedral1;
    private javax.swing.JTextField txtDihedral3;
    private javax.swing.JTextField txtDistanceNose;
    private javax.swing.JTextField txtEDef1;
    private javax.swing.JTextField txtEDef2;
    private javax.swing.JTextField txtEDef3;
    private javax.swing.JTextField txtEDef4;
    private javax.swing.JTextField txtEDef5;
    private javax.swing.JTextField txtEDef6;
    private javax.swing.JTextField txtEDef7;
    private javax.swing.JTextField txtEDef8;
    private javax.swing.JTextField txtEDef9;
    private javax.swing.JTextField txtHighestBody;
    private javax.swing.JTextField txtHorzCOG;
    private javax.swing.JTextField txtHorzCode;
    private javax.swing.JTextField txtHorzWingPos;
    private javax.swing.JTextField txtHorzWingPos2;
    private javax.swing.JTextField txtHorzWingPos3;
    private javax.swing.JTextField txtImportStatus;
    private javax.swing.JTextField txtIncidence;
    private javax.swing.JTextField txtIncidence3;
    private javax.swing.JTextField txtIncrements;
    private javax.swing.JTextField txtLowerValue;
    private javax.swing.JTextField txtLowestBody;
    private javax.swing.JTextField txtMach;
    private javax.swing.JTextField txtNoseLength;
    private javax.swing.JTextField txtOutDihedral;
    private javax.swing.JTextField txtOutSpan;
    private javax.swing.JTextField txtOutSweep;
    private javax.swing.JTextField txtRadiusBody;
    private javax.swing.JTextField txtRootChord;
    private javax.swing.JTextField txtRootChord1;
    private javax.swing.JTextField txtRootChord2;
    private javax.swing.JTextField txtRootChord3;
    private javax.swing.JTextField txtSSPNE;
    private javax.swing.JTextField txtSSPNE2;
    private javax.swing.JTextField txtSSPNE3;
    private javax.swing.JTextField txtSSpan;
    private javax.swing.JTextField txtSSpan1;
    private javax.swing.JTextField txtSSpan2;
    private javax.swing.JTextField txtSSpan3;
    private javax.swing.JTextField txtSpanfi;
    private javax.swing.JTextField txtSpanfo;
    private javax.swing.JTextField txtSweep;
    private javax.swing.JTextField txtSweep1;
    private javax.swing.JTextField txtSweep2;
    private javax.swing.JTextField txtSweep3;
    private javax.swing.JTextField txtTipChord;
    private javax.swing.JTextField txtTipChord1;
    private javax.swing.JTextField txtTipChord2;
    private javax.swing.JTextField txtTipChord3;
    private javax.swing.JTextField txtTwist;
    private javax.swing.JTextField txtUpperValue;
    private javax.swing.JTextField txtVertCOG;
    private javax.swing.JTextField txtVertCode;
    private javax.swing.JTextField txtVertWingPos;
    private javax.swing.JTextField txtVertWingPos2;
    private javax.swing.JTextField txtVertWingPos3;
    private javax.swing.JTextField txtYV;
    // End of variables declaration//GEN-END:variables

    private void bodyOnlyChanges(boolean bool) {
        // need to change the three pages based on true/flase given
        // check the sectionlist at the end if bodyonly is selected
        // wing stuff
        txtRootChord.setEditable(bool);
        txtTipChord.setEditable(bool);
        txtSSpan.setEditable(bool);
        txtSweep.setEditable(bool);
        txtTwist.setEditable(bool);
        txtDihedral.setEditable(bool);
        txtIncidence.setEditable(bool);
        txtChordBreak.setEditable(bool);
        txtOutSpan.setEditable(bool);
        txtOutSweep.setEditable(bool);
        txtOutDihedral.setEditable(bool);
        txtHorzWingPos.setEditable(bool);
        txtVertWingPos.setEditable(bool);
        txtSSPNE.setEditable(bool);
        txtCHSTAT.setEditable(bool);
        txtAirfoilCode.setEditable(bool);
        if (bool == true) {
            txtRootChord.setBackground(Color.white);
            txtTipChord.setBackground(Color.white);
            txtSSpan.setBackground(Color.white);
            txtSweep.setBackground(Color.white);
            txtTwist.setBackground(Color.white);
            txtDihedral.setBackground(Color.white);
            txtIncidence.setBackground(Color.white);
            txtChordBreak.setBackground(Color.white);
            txtOutSpan.setBackground(Color.white);
            txtOutSweep.setBackground(Color.white);
            txtOutDihedral.setBackground(Color.white);
            txtHorzWingPos.setBackground(Color.white);
            txtVertWingPos.setBackground(Color.white);
            txtSSPNE.setBackground(Color.white);
            txtCHSTAT.setBackground(Color.white);
            txtAirfoilCode.setBackground(Color.white);
        }
        if (bool == false) {
            txtRootChord.setBackground(Color.gray);
            txtTipChord.setBackground(Color.gray);
            txtSSpan.setBackground(Color.gray);
            txtSweep.setBackground(Color.gray);
            txtTwist.setBackground(Color.gray);
            txtDihedral.setBackground(Color.gray);
            txtIncidence.setBackground(Color.gray);
            txtChordBreak.setBackground(Color.gray);
            txtOutSpan.setBackground(Color.gray);
            txtOutSweep.setBackground(Color.gray);
            txtOutDihedral.setBackground(Color.gray);
            txtHorzWingPos.setBackground(Color.gray);
            txtVertWingPos.setBackground(Color.gray);
            txtSSPNE.setBackground(Color.gray);
            txtCHSTAT.setBackground(Color.gray);
            txtAirfoilCode.setBackground(Color.gray);
        }
        // tail stuff
        checkHorzStab.setSelected(bool);
        checkVertStab.setSelected(bool);
        horzStabiliserChanges(bool);
        vertStabiliserChanges(bool);
    }

    private void horzStabiliserChanges(boolean bool) {
        txtRootChord1.setEditable(bool);
        txtTipChord1.setEditable(bool);
        txtSSpan1.setEditable(bool);
        txtSweep1.setEditable(bool);

        txtCHSTAT1.setEditable(bool);
        txtDihedral1.setEditable(bool);
        txtHorzWingPos2.setEditable(bool);
        txtVertWingPos2.setEditable(bool);
        txtSSPNE2.setEditable(bool);
        txtHorzCode.setEditable(bool);
        if (bool == true) {
            txtRootChord1.setBackground(Color.white);
            txtTipChord1.setBackground(Color.white);
            txtSSpan1.setBackground(Color.white);
            txtSweep1.setBackground(Color.white);
            txtDihedral1.setBackground(Color.white);
            txtCHSTAT1.setBackground(Color.white);
            txtHorzCode.setBackground(Color.white);
            txtHorzWingPos2.setBackground(Color.white);
            txtVertWingPos2.setBackground(Color.white);
            txtSSPNE2.setBackground(Color.white);
        }
        if (bool == false) {
            txtRootChord1.setBackground(Color.gray);
            txtTipChord1.setBackground(Color.gray);
            txtSSpan1.setBackground(Color.gray);
            txtSweep1.setBackground(Color.gray);
            txtDihedral1.setBackground(Color.gray);
            txtCHSTAT1.setBackground(Color.gray);
            txtHorzCode.setBackground(Color.gray);
            txtHorzWingPos2.setBackground(Color.gray);
            txtVertWingPos2.setBackground(Color.gray);
            txtSSPNE2.setBackground(Color.gray);
        }
    }

    private void vertStabiliserChanges(boolean bool) {
        txtRootChord2.setEditable(bool);
        txtTipChord2.setEditable(bool);
        txtSSpan2.setEditable(bool);
        txtSweep2.setEditable(bool);
        txtCHSTAT2.setEditable(bool);
        //txtCant.setEditable(bool);
        //txtYV.setEditable(bool);
        txtHorzWingPos3.setEditable(bool);
        txtVertWingPos3.setEditable(bool);
        txtSSPNE3.setEditable(bool);
        txtVertCode.setEditable(bool);
        if (bool == true) {
            txtRootChord2.setBackground(Color.white);
            txtTipChord2.setBackground(Color.white);
            txtSSpan2.setBackground(Color.white);
            txtSweep2.setBackground(Color.white);
            txtCHSTAT2.setBackground(Color.white);
            //txtCant.setBackground(Color.white);
            //txtYV.setBackground(Color.white);
            txtHorzWingPos3.setBackground(Color.white);
            txtVertWingPos3.setBackground(Color.white);
            txtSSPNE3.setBackground(Color.white);
            txtVertCode.setBackground(Color.white);
        }
        if (bool == false) {
            txtRootChord2.setBackground(Color.gray);
            txtTipChord2.setBackground(Color.gray);
            txtSSpan2.setBackground(Color.gray);
            txtSweep2.setBackground(Color.gray);
            txtCHSTAT2.setBackground(Color.gray);
            //txtCant.setBackground(Color.gray);
            //txtYV.setBackground(Color.gray);
            txtHorzWingPos3.setBackground(Color.gray);
            txtVertWingPos3.setBackground(Color.gray);
            txtSSPNE3.setBackground(Color.gray);
            txtVertCode.setBackground(Color.gray);
        }
    }

    private static String identifyEngine(String engine) {
        if (engine.contains("AJ26-33_nozzle")) {
            engine = "AJ26-33_nozzle";
            engineID = 0;
        } else if (engine.contains("AJ26-33A")) {
            engine = "AJ26-33A";
            engineID = 1;
        } else if (engine.contains("avco_lycoming_t53")) {
            engine = "avco_lycoming_t53";
            engineID = 2;
        } else if (engine.contains("BR710")) {
            engine = "BR710";
            engineID = 3;
        } else if (engine.contains("BR-725")) {
            engine = "BR-725";
            engineID = 4;
        } else if (engine.contains("CF6-80C2")) {
            engine = "CF6-80C2";
            engineID = 5;
        } else if (engine.contains("CFM56_5")) {
            engine = "CFM56_5";
            engineID = 7;
        } else if (engine.contains("CFM56")) {
            engine = "CFM56";
            engineID = 6;
        } else if (engine.contains("direct")) {
            engine = "direct";
            engineID = 8;
        } else if (engine.contains("Dr1_propeller")) {
            engine = "Dr1_propeller";
            engineID = 9;
        } else if (engine.contains("electric_1mw")) {
            engine = "electric_1mw";
            engineID = 10;
        } else if (engine.contains("electric147kW")) {
            engine = "electric147kW";
            engineID = 11;
        } else if (engine.contains("eng_io320")) {
            engine = "eng_io320";
            engineID = 12;
        } else if (engine.contains("eng_PegasusXc")) {
            engine = "eng_PegasusXc";
            engineID = 13;
        } else if (engine.contains("eng_RRhawk")) {
            engine = "eng_RRhawk";
            engineID = 14;
        } else if (engine.contains("engIO360C")) {
            engine = "engIO360C";
            engineID = 15;
        } else if (engine.contains("engIO470D")) {
            engine = "engIO470D";
            engineID = 16;
        } else if (engine.contains("engIO540AB1A5")) {
            engine = "engIO540AB1A5";
            engineID = 17;
        } else if (engine.contains("engRRMerlin61")) {
            engine = "engRRMerlin61";
            engineID = 18;
        } else if (engine.contains("engRRMerlinXII")) {
            engine = "engRRMerlinXII";
            engineID = 19;
        } else if (engine.contains("engtm601")) {
            engine = "engtm601";
            engineID = 20;
        } else if (engine.contains("F100-PW-229")) {
            engine = "F100-PW-229";
            engineID = 21;
        } else if (engine.contains("F119-PW-1")) {
            engine = "F119-PW-1";
            engineID = 22;
        } else if (engine.contains("GE-CF6-80C2-B1F")) {
            engine = "GE-CF6-80C2-B1F";
            engineID = 23;
        } else if (engine.contains("HamiltonStd6243A-3")) {
            engine = "HamiltonStd6243A-3";
            engineID = 24;
        } else if (engine.contains("J33-A-35")) {
            engine = "J33-A-35";
            engineID = 25;
        } else if (engine.contains("J52")) {
            engine = "J52";
            engineID = 26;
        } else if (engine.contains("J69-T25")) {
            engine = "J69-T25";
            engineID = 27;
        } else if (engine.contains("J79-GE-11A")) {
            engine = "J79-GE-11A";
            engineID = 28;
        } else if (engine.contains("J85-GE-5")) {
            engine = "J85-GE-5";
            engineID = 29;
        } else if (engine.contains("JT15D")) {
            engine = "JT15D";
            engineID = 30;
        } else if (engine.contains("JT9D-3")) {
            engine = "JT9D-3";
            engineID = 31;
        } else if (engine.contains("MerlinV1650")) {
            engine = "MerlinV1650";
            engineID = 32;
        } else if (engine.contains("Oberursel-UrII")) {
            engine = "Oberursel-UrII";
            engineID = 33;
        } else if (engine.contains("Olympus593Mrk610")) {
            engine = "Olympus593Mrk610";
            engineID = 34;
        } else if (engine.contains("P51prop")) {
            engine = "P51prop";
            engineID = 35;
        } else if (engine.contains("prop_75in2f")) {
            engine = "prop_75in2f";
            engineID = 36;
        } else if (engine.contains("prop_81in2v")) {
            engine = "prop_81in2v";
            engineID = 37;
        } else if (engine.contains("prop_Clark_Y7570")) {
            engine = "prop_Clark_Y7570";
            engineID = 38;
        } else if (engine.contains("prop_deHavilland5000")) {
            engine = "prop_deHavilland5000";
            engineID = 39;
        } else if (engine.contains("prop_generic2f")) {
            engine = "prop_generic2f";
            engineID = 40;
        } else if (engine.contains("prop_PT6")) {
            engine = "prop_PT6";
            engineID = 41;
        } else if (engine.contains("prop_SSZ")) {
            engine = "prop_SSZ";
            engineID = 42;
        } else if (engine.contains("prop30FP2B")) {
            engine = "prop30FP2B";
            engineID = 43;
        } else if (engine.contains("propC10v")) {
            engine = "propC10v";
            engineID = 44;
        } else if (engine.contains("propC6v")) {
            engine = "propC6v";
            engineID = 45;
        } else if (engine.contains("propC8v")) {
            engine = "propC8v";
            engineID = 46;
        } else if (engine.contains("propDA-R352_6-123-F_2")) {
            engine = "propDA-R352_6-123-F_2";
            engineID = 47;
        } else if (engine.contains("propHO-V373-D")) {
            engine = "propHO-V373-D";
            engineID = 48;
        } else if (engine.contains("propHS139v")) {
            engine = "propHS139v";
            engineID = 49;
        } else if (engine.contains("PT6A-27")) {
            engine = "PT6A-27";
            engineID = 50;
        } else if (engine.contains("PT6A-68")) {
            engine = "PT6A-68";
            engineID = 51;
        } else if (engine.contains("PW125B")) {
            engine = "PW125B";
            engineID = 52;
        } else if (engine.contains("R-1820-97")) {
            engine = "R-1820-97";
            engineID = 53;
        } else if (engine.contains("RB211-524")) {
            engine = "RB211-524";
            engineID = 54;
        } else if (engine.contains("RL10_nozzle")) {
            engine = "RL10_nozzle";
            engineID = 56;
        } else if (engine.contains("RL10")) {
            engine = "RL10";
            engineID = 55;
        } else if (engine.contains("RollsRoyce")) {
            engine = "RollsRoyce";
            engineID = 57;
        } else if (engine.contains("s64_rotor")) {
            engine = "s64_rotor";
            engineID = 58;
        } else if (engine.contains("SRB_nozzle")) {
            engine = "SRB_nozzle";
            engineID = 60;
        } else if (engine.contains("SRB")) {
            engine = "SRB";
            engineID = 59;
        } else if (engine.contains("SSME_nozzle")) {
            engine = "SSME_nozzle";
            engineID = 62;
        } else if (engine.contains("SSME")) {
            engine = "SSME";
            engineID = 61;
        } else if (engine.contains("t56_prop")) {
            engine = "t56_prop";
            engineID = 63;
        } else if (engine.contains("T76")) {
            engine = "T76";
            engineID = 64;
        } else if (engine.contains("Tay-620")) {
            engine = "Tay-620";
            engineID = 65;
        } else if (engine.contains("Tay-651")) {
            engine = "Tay-651";
            engineID = 66;
        } else if (engine.contains("test_turbine")) {
            engine = "test_turbine";
            engineID = 67;
        } else if (engine.contains("TRENT-900")) {
            engine = "TRENT-900";
            engineID = 68;
        } else if (engine.contains("twin_pratt_and_whitney_t73")) {
            engine = "twin_pratt_and_whitney_t73";
            engineID = 69;
        } else if (engine.contains("vrtule2")) {
            engine = "vrtule2";
            engineID = 70;
        } else if (engine.contains("xlr99_nozzle")) {
            engine = "xlr99_nozzle";
            engineID = 72;
        } else if (engine.contains("XLR99")) {
            engine = "XLR99";
            engineID = 71;
        } else if (engine.contains("Zenoah_G-26A")) {
            engine = "Zenoah_G-26A";
            engineID = 73;
        } else {
            engine = "No Engine Recignised";
        }
        return engine;
    }

    private void initialisePanelStates() {
        pnlInnerTabs.setEnabledAt(1, false);
        pnlInnerTabs.setEnabledAt(2, false);
        pnlInnerTabs.setEnabledAt(3, false);
        pnlInnerTabs.setEnabledAt(4, false);
        pnlInnerTabs.setEnabledAt(5, false);
        pnlInnerTabs.setEnabledAt(6, false);
        pnlMainTab.setEnabledAt(1, false);
        menuInsert.setEnabled(false);
        pnlQuickPlot.setVisible(false);
    }

    // returns a double as a string with a guaranteed decimal place
    private String decimalCheck(double value) {
        String returnText = "";
        if (value % 1 == 0) {
            int number = (int) value;
            return returnText + number + ".0";
        } else {
            return returnText + value;
        }

    }

    private void initialiseSynthsLines() {
        // line 1 contains data that always exists eg cg
        // line 2 contains wing data
        // line 3 contains horz stab data
        // line 4 contains vert stab data
        // a $ will be added to the end of the last non-zero line
        for (int i = 1; i <= 4; i++) {
            synthsLines.add(zero);
        }

    }

    private void hideMultiRun() {
        panelMultiRun.setVisible(false);
    }

    private double calculateArea(double radius, double high, double low) {
        // need to fix this
        double otherRadius = (high + low) / 2;
        double answer = 0.0;
        answer = (radius * otherRadius * 3.14159265);
        return answer;
    }

    private double calculateCircumference(double radius, double high, double low) {

        double h = 0.0;
        double otherRadius = (high + low) / 2;
        if (otherRadius < 0.0) {
            otherRadius = otherRadius * -1.0;
        }
        if (radius > otherRadius) {
            h = (radius - otherRadius) / (radius + otherRadius);
        } else if (radius < otherRadius) {
            h = (otherRadius - radius) / (otherRadius + radius);
        }
        double answer = (3.14159265 * 0.5) * (radius + otherRadius);
        answer = answer * (1 + ((h * h) / 4) + ((h * h * h * h) / 64) + ((Math.pow(h, 6)) / 256) + (Math.pow(h, 8) / 16384));
        return answer;
    }
}
