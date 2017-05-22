/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DatcomEditor;

import java.util.ArrayList;

/**
 *
 * @author Gavin
 */
public class AnalysisWindow extends javax.swing.JFrame {

    /**
     * Creates new form AnalysisWindow
     */
    public AnalysisWindow() {
        initComponents();
        initialiseEverythingElse();
    }

    public AnalysisWindow(ArrayList basicData) {
        initComponents();
        sortBasicData(basicData);
        displayBasic();
        initialiseEverythingElse();
    }

    public AnalysisWindow(ArrayList basicData, ArrayList firstSymmData) {
        initComponents();
        sortBasicData(basicData);
        sortFirstSymmData(firstSymmData);
        displayBasic();
        initialiseEverythingElse();
    }
    // these ints hold the number of alpha and deflection angles
    int alphas = 0;
    int firstDefs = 0;
    // these arrays hold the alpha and deflection angles
    ArrayList alphaList = new ArrayList();
    ArrayList firstDefList = new ArrayList();
    // these derivatives don't change
    double cybeta = 0.0;
    double cnbeta = 0.0;
    double cLq = 0.0;
    double cmq = 0.0;
    // basic coefficient arraylists
    ArrayList cdalpha = new ArrayList();
    ArrayList cLalpha = new ArrayList();
    ArrayList cmalpha = new ArrayList();
    ArrayList clbeta = new ArrayList();
    ArrayList cLalphadot = new ArrayList();
    ArrayList cmalphadot = new ArrayList();
    ArrayList clp = new ArrayList();
    ArrayList cyp = new ArrayList();
    ArrayList cnp = new ArrayList();
    ArrayList cnr = new ArrayList();
    ArrayList clr = new ArrayList();
    // first symm control arraylist terms
    ArrayList firstCL = new ArrayList();
    ArrayList firstCm = new ArrayList();
    ArrayList firstCd = new ArrayList(); // this array is alpha * def long, row by row

    // this method sorts the data given in the constructor into the right places
    private void sortBasicData(ArrayList data) {
        // first step is find alphas
        int firstIndex = 0;
        int secondIndex = 0;
        outerLoop:
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).toString().equalsIgnoreCase("Alpha, Cd")) {
                firstIndex = i;
            }
            if (data.get(i).toString().equalsIgnoreCase("Alpha, CL")) {
                secondIndex = i;
                break outerLoop;
            }
        }
        alphas = (secondIndex - firstIndex - 1); // sets global variable for number of angles of attack
        // then need to find their values
        for (int i = firstIndex + 1; i < secondIndex; i++) {
            String line = data.get(i).toString();
            if (line.contains(",")) {
                int start = line.indexOf(",");
                String values = line.substring(0, start);
                values = values.replaceAll("\\s+", ""); // remove whitespace
                alphaList.add(values);
            }
        }
        // then find the locations of each derivative
        // using this variable-intensive method for clarity purposes
        int cdIndex = firstIndex;
        int cLIndex = secondIndex;
        int cmIndex = 0;
        int cybIndex = 0;
        int cnbIndex = 0;
        int clbIndex = 0;
        int cLqIndex = 0;
        int cmqIndex = 0;
        int cLadIndex = 0;
        int cmadIndex = 0;
        int clpIndex = 0;
        int cypIndex = 0;
        int cnpIndex = 0;
        int cnrIndex = 0;
        int clrIndex = 0;
        for (int i = 0; i < data.size(); i++) {
            String line = data.get(i).toString();
            switch (line) {
                case "Alpha, Cm":
                    cmIndex = i;
                    break;
                case "Alpha, Cyb":
                    cybIndex = i;
                    break;
                case "Alpha, Cnb":
                    cnbIndex = i;
                    break;
                case "Alpha, Clb":
                    clbIndex = i;
                    break;
                case "Alpha, CLq":
                    cLqIndex = i;
                    break;
                case "Alpha, Cmq":
                    cmqIndex = i;
                    break;
                case "Alpha, CLad_Basic":
                    cLadIndex = i;
                    break;
                case "Alpha, Cmad_Basic":
                    cmadIndex = i;
                    break;
                case "Alpha, Clp":
                    clpIndex = i;
                    break;
                case "Alpha, Cyp":
                    cypIndex = i;
                    break;
                case "Alpha, Cnp":
                    cnpIndex = i;
                    break;
                case "Alpha, Cnr":
                    cnrIndex = i;
                    break;
                case "Alpha, Clr":
                    clrIndex = i;
                    break;
                default:
                    break;
            }
        }
        // then remove all the leading edge stuff i.e. stuff in front of commas
        for (int i = 0; i < data.size(); i++) {
            String line = data.get(i).toString();
            if (line.contains(",")) {
                int start = line.indexOf(",");
                String remaining = line.substring(start + 1); // check this
                remaining = remaining.replaceAll("\\s+", ""); // remove whitespace
                data.set(i, remaining);
            }
        }
        // then add the data to the right arraylists
        for (int i = cdIndex + 1; i <= cdIndex + alphas; i++) {
            cdalpha.add(data.get(i));
        }
        for (int i = cLIndex + 1; i <= cLIndex + alphas; i++) {
            cLalpha.add(data.get(i));
        }
        for (int i = cmIndex + 1; i <= cmIndex + alphas; i++) {
            cmalpha.add(data.get(i));
        }
        for (int i = clbIndex + 1; i <= clbIndex + alphas; i++) {
            clbeta.add(data.get(i));
        }
        for (int i = cLadIndex + 1; i <= cLadIndex + alphas; i++) {
            cLalphadot.add(data.get(i));
        }
        for (int i = cmadIndex + 1; i <= cmadIndex + alphas; i++) {
            cmalphadot.add(data.get(i));
        }
        for (int i = clpIndex + 1; i <= clpIndex + alphas; i++) {
            clp.add(data.get(i));
        }
        for (int i = cypIndex + 1; i <= cypIndex + alphas; i++) {
            cyp.add(data.get(i));
        }
        for (int i = cnpIndex + 1; i <= cnpIndex + alphas; i++) {
            cnp.add(data.get(i));
        }
        for (int i = cnrIndex + 1; i <= cnrIndex + alphas; i++) {
            cnr.add(data.get(i));
        }
        for (int i = clrIndex + 1; i <= clrIndex + alphas; i++) {
            clr.add(data.get(i));
        }
        String extraDerivatives = data.get(cybIndex + 1).toString();
        cybeta = Double.parseDouble(extraDerivatives);
        extraDerivatives = data.get(cnbIndex + 1).toString();
        cnbeta = Double.parseDouble(extraDerivatives);
        extraDerivatives = data.get(cLqIndex + 1).toString();
        cLq = Double.parseDouble(extraDerivatives);
        extraDerivatives = data.get(cmqIndex + 1).toString();
        cmq = Double.parseDouble(extraDerivatives);
    }

    private void sortFirstSymmData(ArrayList data) {
        // first step is find deflections
        int firstIndex = 0;
        int secondIndex = 0;
        outerLoop:
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).toString().equalsIgnoreCase("DeltaFlap, CL")) {
                firstIndex = i;
            }
            if (data.get(i).toString().equalsIgnoreCase("DeltaFlap, Cm")) {
                secondIndex = i;
                break outerLoop;
            }
        }
        firstDefs = (secondIndex - firstIndex - 2); // sets global variable for number of defs
        // then need to find their values
        for (int i = firstIndex + 1; i < secondIndex - 1; i++) {
            String line = data.get(i).toString();
            if (line.contains(",")) {
                int start = line.indexOf(",");
                String values = line.substring(0, start);
                values = values.replaceAll("\\s+", ""); // remove whitespace
                firstDefList.add(values);
            }
        }
        // then find the location of the third derivative
        int flapcL = firstIndex;
        int flapcm = secondIndex;
        int flapcd = 0;

        for (int i = 0; i < data.size(); i++) {
            String line = data.get(i).toString();
            if (line.equalsIgnoreCase("Alpha, DeltaFlap")) {
                flapcd = i;
            }
        }
        // then remove all the leading edge stuff i.e. stuff in front of commas
        // even for the cd values
        for (int i = 0; i < data.size(); i++) {
            String line = data.get(i).toString();
            if (line.contains(",")) {
                int start = line.indexOf(",");
                String remaining = line.substring(start + 1); // check this
                remaining = remaining.replaceAll("\\s+", ""); // remove whitespace
                data.set(i, remaining);
            }
        }
        // then add data for the first two arrays
        for (int i = flapcL + 1; i <= flapcL + firstDefs; i++) {
            firstCL.add(data.get(i));
        }
        for (int i = flapcm + 1; i <= flapcm + firstDefs; i++) {
            firstCm.add(data.get(i));
        }
        for (int i = flapcd + 1; i <= flapcd + alphas; i++) {
            String line = data.get(i).toString();
            //remove comma at end
            line = line.substring(0, line.length() - 1);
            while (line.contains(",")) {
                int start = line.indexOf(",");
                String toCopy = line.substring(0, start);
                firstCd.add(toCopy);
                line = line.substring(start + 1);
            }
            // then add the last value
            firstCd.add(line);

        }


    }

    // this method displays an arraylist's data given the panel and combo indices
    private void displayData(ArrayList data, int panelIndex, int comboIndex, int columnIndex) {
        if (panelIndex == 1) {
        }
        if (panelIndex == 2) {
        }
    }

    private void displayBasic() {
        txtCybeta.setText("" + cybeta);
        txtCnbeta.setText("" + cnbeta);
        txtClq.setText("" + cLq);
        txtCmq.setText("" + cmq);
        // then hide rows based on alphas
        if (alphas < 20) {
            hideRow20();
        }
        if (alphas < 19) {
            hideRow19();
        }
        if (alphas < 18) {
            hideRow18();
        }
        if (alphas < 17) {
            hideRow17();
        }
        if (alphas < 16) {
            hideRow16();
        }
        if (alphas < 15) {
            hideRow15();
        }
        if (alphas < 14) {
            hideRow14();
        }
        if (alphas < 13) {
            hideRow13();
        }
        if (alphas < 12) {
            hideRow12();
        }
        if (alphas < 11) {
            hideRow11();
        }
        if (alphas < 10) {
            hideRow10();
        }
        if (alphas < 9) {
            hideRow9();
        }
        if (alphas < 8) {
            hideRow8();
        }
        if (alphas < 7) {
            hideRow7();
        }
        if (alphas < 6) {
            hideRow6();
        }
        if (alphas < 5) {
            hideRow5();
        }
        if (alphas < 4) {
            hideRow4();
        }
        if (alphas < 3) {
            hideRow3();
        }
        // add the angles of attack to the right boxes
        // first off the lateral page
        txtAlpha1.setText(alphaList.get(0).toString());
        txtAlpha2.setText(alphaList.get(1).toString());
        if (alphas > 2) {
            txtAlpha3.setText(alphaList.get(2).toString());
        }
        if (alphas > 3) {
            txtAlpha4.setText(alphaList.get(3).toString());
        }
        if (alphas > 4) {
            txtAlpha5.setText(alphaList.get(4).toString());
        }
        if (alphas > 5) {
            txtAlpha6.setText(alphaList.get(5).toString());
        }
        if (alphas > 6) {
            txtAlpha7.setText(alphaList.get(6).toString());
        }
        if (alphas > 7) {
            txtAlpha8.setText(alphaList.get(7).toString());
        }
        if (alphas > 8) {
            txtAlpha9.setText(alphaList.get(8).toString());
        }
        if (alphas > 9) {
            txtAlpha10.setText(alphaList.get(9).toString());
        }
        if (alphas > 10) {
            txtAlpha11.setText(alphaList.get(10).toString());
        }
        if (alphas > 11) {
            txtAlpha12.setText(alphaList.get(11).toString());
        }
        if (alphas > 12) {
            txtAlpha13.setText(alphaList.get(12).toString());
        }
        if (alphas > 13) {
            txtAlpha14.setText(alphaList.get(13).toString());
        }
        if (alphas > 14) {
            txtAlpha15.setText(alphaList.get(14).toString());
        }
        if (alphas > 15) {
            txtAlpha16.setText(alphaList.get(15).toString());
        }
        if (alphas > 16) {
            txtAlpha17.setText(alphaList.get(16).toString());
        }
        if (alphas > 17) {
            txtAlpha18.setText(alphaList.get(17).toString());
        }
        if (alphas > 18) {
            txtAlpha19.setText(alphaList.get(18).toString());
        }
        if (alphas > 19) {
            txtAlpha20.setText(alphaList.get(19).toString());
        }
        // then the longitudinal page
        txtAlpha21.setText(alphaList.get(0).toString());
        txtAlpha22.setText(alphaList.get(1).toString());
        if (alphas > 2) {
            txtAlpha23.setText(alphaList.get(2).toString());
        }
        if (alphas > 3) {
            txtAlpha24.setText(alphaList.get(3).toString());
        }
        if (alphas > 4) {
            txtAlpha25.setText(alphaList.get(4).toString());
        }
        if (alphas > 5) {
            txtAlpha26.setText(alphaList.get(5).toString());
        }
        if (alphas > 6) {
            txtAlpha27.setText(alphaList.get(6).toString());
        }
        if (alphas > 7) {
            txtAlpha28.setText(alphaList.get(7).toString());
        }
        if (alphas > 8) {
            txtAlpha29.setText(alphaList.get(8).toString());
        }
        if (alphas > 9) {
            txtAlpha30.setText(alphaList.get(9).toString());
        }
        if (alphas > 10) {
            txtAlpha31.setText(alphaList.get(10).toString());
        }
        if (alphas > 11) {
            txtAlpha32.setText(alphaList.get(11).toString());
        }
        if (alphas > 12) {
            txtAlpha33.setText(alphaList.get(12).toString());
        }
        if (alphas > 13) {
            txtAlpha34.setText(alphaList.get(13).toString());
        }
        if (alphas > 14) {
            txtAlpha35.setText(alphaList.get(14).toString());
        }
        if (alphas > 15) {
            txtAlpha36.setText(alphaList.get(15).toString());
        }
        if (alphas > 16) {
            txtAlpha37.setText(alphaList.get(16).toString());
        }
        if (alphas > 17) {
            txtAlpha38.setText(alphaList.get(17).toString());
        }
        if (alphas > 18) {
            txtAlpha39.setText(alphaList.get(18).toString());
        }
        if (alphas > 19) {
            txtAlpha40.setText(alphaList.get(19).toString());
        }

    }

    private void hideRow20() {
        txtAlpha20.setVisible(false);
        txtAlpha40.setVisible(false);
        jTextField40.setVisible(false);
        jTextField60.setVisible(false);
        jTextField80.setVisible(false);
        jTextField100.setVisible(false);
        jTextField120.setVisible(false);
        jTextField140.setVisible(false);
        jTextField160.setVisible(false);
        jTextField200.setVisible(false);
        jTextField220.setVisible(false);
        jTextField240.setVisible(false);
        jTextField260.setVisible(false);

    }

    private void hideRow19() {
        txtAlpha19.setVisible(false);
        txtAlpha39.setVisible(false);
        jTextField39.setVisible(false);
        jTextField59.setVisible(false);
        jTextField79.setVisible(false);
        jTextField99.setVisible(false);
        jTextField119.setVisible(false);
        jTextField139.setVisible(false);
        jTextField159.setVisible(false);
        jTextField199.setVisible(false);
        jTextField219.setVisible(false);
        jTextField239.setVisible(false);
        jTextField259.setVisible(false);
    }

    private void hideRow18() {
        txtAlpha18.setVisible(false);
        txtAlpha38.setVisible(false);
        jTextField38.setVisible(false);
        jTextField58.setVisible(false);
        jTextField78.setVisible(false);
        jTextField98.setVisible(false);
        jTextField118.setVisible(false);
        jTextField138.setVisible(false);
        jTextField158.setVisible(false);
        jTextField198.setVisible(false);
        jTextField218.setVisible(false);
        jTextField238.setVisible(false);
        jTextField258.setVisible(false);
    }

    private void hideRow17() {
        txtAlpha17.setVisible(false);
        txtAlpha37.setVisible(false);
        jTextField37.setVisible(false);
        jTextField57.setVisible(false);
        jTextField77.setVisible(false);
        jTextField97.setVisible(false);
        jTextField117.setVisible(false);
        jTextField137.setVisible(false);
        jTextField157.setVisible(false);
        jTextField197.setVisible(false);
        jTextField217.setVisible(false);
        jTextField237.setVisible(false);
        jTextField257.setVisible(false);
    }

    private void hideRow16() {
        txtAlpha16.setVisible(false);
        txtAlpha36.setVisible(false);
        jTextField36.setVisible(false);
        jTextField56.setVisible(false);
        jTextField76.setVisible(false);
        jTextField96.setVisible(false);
        jTextField116.setVisible(false);
        jTextField136.setVisible(false);
        jTextField156.setVisible(false);
        jTextField196.setVisible(false);
        jTextField216.setVisible(false);
        jTextField236.setVisible(false);
        jTextField256.setVisible(false);
    }

    private void hideRow15() {
        txtAlpha15.setVisible(false);
        txtAlpha35.setVisible(false);
        jTextField35.setVisible(false);
        jTextField55.setVisible(false);
        jTextField75.setVisible(false);
        jTextField95.setVisible(false);
        jTextField115.setVisible(false);
        jTextField135.setVisible(false);
        jTextField155.setVisible(false);
        jTextField195.setVisible(false);
        jTextField215.setVisible(false);
        jTextField235.setVisible(false);
        jTextField255.setVisible(false);
    }

    private void hideRow14() {
        txtAlpha14.setVisible(false);
        txtAlpha34.setVisible(false);
        jTextField34.setVisible(false);
        jTextField54.setVisible(false);
        jTextField74.setVisible(false);
        jTextField94.setVisible(false);
        jTextField114.setVisible(false);
        jTextField134.setVisible(false);
        jTextField154.setVisible(false);
        jTextField194.setVisible(false);
        jTextField214.setVisible(false);
        jTextField234.setVisible(false);
        jTextField254.setVisible(false);
    }

    private void hideRow13() {
        txtAlpha13.setVisible(false);
        txtAlpha33.setVisible(false);
        jTextField33.setVisible(false);
        jTextField53.setVisible(false);
        jTextField73.setVisible(false);
        jTextField93.setVisible(false);
        jTextField113.setVisible(false);
        jTextField133.setVisible(false);
        jTextField153.setVisible(false);
        jTextField193.setVisible(false);
        jTextField213.setVisible(false);
        jTextField233.setVisible(false);
        jTextField253.setVisible(false);
    }

    private void hideRow12() {
        txtAlpha12.setVisible(false);
        txtAlpha32.setVisible(false);
        jTextField32.setVisible(false);
        jTextField52.setVisible(false);
        jTextField72.setVisible(false);
        jTextField92.setVisible(false);
        jTextField112.setVisible(false);
        jTextField132.setVisible(false);
        jTextField152.setVisible(false);
        jTextField192.setVisible(false);
        jTextField212.setVisible(false);
        jTextField232.setVisible(false);
        jTextField252.setVisible(false);
    }

    private void hideRow11() {
        txtAlpha11.setVisible(false);
        txtAlpha31.setVisible(false);
        jTextField31.setVisible(false);
        jTextField51.setVisible(false);
        jTextField71.setVisible(false);
        jTextField91.setVisible(false);
        jTextField111.setVisible(false);
        jTextField131.setVisible(false);
        jTextField151.setVisible(false);
        jTextField191.setVisible(false);
        jTextField211.setVisible(false);
        jTextField231.setVisible(false);
        jTextField251.setVisible(false);
    }

    private void hideRow10() {
        txtAlpha10.setVisible(false);
        txtAlpha30.setVisible(false);
        jTextField30.setVisible(false);
        jTextField50.setVisible(false);
        jTextField70.setVisible(false);
        jTextField90.setVisible(false);
        jTextField110.setVisible(false);
        jTextField130.setVisible(false);
        jTextField150.setVisible(false);
        jTextField190.setVisible(false);
        jTextField210.setVisible(false);
        jTextField230.setVisible(false);
        jTextField250.setVisible(false);
    }

    private void hideRow9() {
        txtAlpha9.setVisible(false);
        txtAlpha29.setVisible(false);
        jTextField29.setVisible(false);
        jTextField49.setVisible(false);
        jTextField69.setVisible(false);
        jTextField89.setVisible(false);
        jTextField109.setVisible(false);
        jTextField129.setVisible(false);
        jTextField149.setVisible(false);
        jTextField189.setVisible(false);
        jTextField209.setVisible(false);
        jTextField229.setVisible(false);
        jTextField249.setVisible(false);
    }

    private void hideRow8() {
        txtAlpha8.setVisible(false);
        txtAlpha28.setVisible(false);
        jTextField28.setVisible(false);
        jTextField48.setVisible(false);
        jTextField68.setVisible(false);
        jTextField88.setVisible(false);
        jTextField108.setVisible(false);
        jTextField128.setVisible(false);
        jTextField148.setVisible(false);
        jTextField188.setVisible(false);
        jTextField208.setVisible(false);
        jTextField228.setVisible(false);
        jTextField248.setVisible(false);
    }

    private void hideRow7() {
        txtAlpha7.setVisible(false);
        txtAlpha27.setVisible(false);
        jTextField27.setVisible(false);
        jTextField47.setVisible(false);
        jTextField67.setVisible(false);
        jTextField87.setVisible(false);
        jTextField107.setVisible(false);
        jTextField127.setVisible(false);
        jTextField147.setVisible(false);
        jTextField187.setVisible(false);
        jTextField207.setVisible(false);
        jTextField227.setVisible(false);
        jTextField247.setVisible(false);
    }

    private void hideRow6() {
        txtAlpha6.setVisible(false);
        txtAlpha26.setVisible(false);
        jTextField26.setVisible(false);
        jTextField46.setVisible(false);
        jTextField66.setVisible(false);
        jTextField86.setVisible(false);
        jTextField106.setVisible(false);
        jTextField126.setVisible(false);
        jTextField146.setVisible(false);
        jTextField186.setVisible(false);
        jTextField206.setVisible(false);
        jTextField226.setVisible(false);
        jTextField246.setVisible(false);
    }

    private void hideRow5() {
        txtAlpha5.setVisible(false);
        txtAlpha25.setVisible(false);
        jTextField25.setVisible(false);
        jTextField45.setVisible(false);
        jTextField65.setVisible(false);
        jTextField85.setVisible(false);
        jTextField105.setVisible(false);
        jTextField125.setVisible(false);
        jTextField145.setVisible(false);
        jTextField185.setVisible(false);
        jTextField205.setVisible(false);
        jTextField225.setVisible(false);
        jTextField245.setVisible(false);
    }

    private void hideRow4() {
        txtAlpha4.setVisible(false);
        txtAlpha24.setVisible(false);
        jTextField24.setVisible(false);
        jTextField44.setVisible(false);
        jTextField64.setVisible(false);
        jTextField84.setVisible(false);
        jTextField104.setVisible(false);
        jTextField124.setVisible(false);
        jTextField144.setVisible(false);
        jTextField184.setVisible(false);
        jTextField204.setVisible(false);
        jTextField224.setVisible(false);
        jTextField244.setVisible(false);
    }

    private void hideRow3() {
        txtAlpha3.setVisible(false);
        txtAlpha23.setVisible(false);
        jTextField23.setVisible(false);
        jTextField43.setVisible(false);
        jTextField63.setVisible(false);
        jTextField83.setVisible(false);
        jTextField103.setVisible(false);
        jTextField123.setVisible(false);
        jTextField143.setVisible(false);
        jTextField183.setVisible(false);
        jTextField203.setVisible(false);
        jTextField223.setVisible(false);
        jTextField243.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JTabbedPane();
        pnlSummary = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCybeta = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCnbeta = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtClq = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtCmq = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        btnAileron = new javax.swing.JButton();
        btnFore = new javax.swing.JButton();
        btnAft = new javax.swing.JButton();
        pnlLatDer = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtAlpha2 = new javax.swing.JTextField();
        txtAlpha1 = new javax.swing.JTextField();
        txtAlpha3 = new javax.swing.JTextField();
        txtAlpha4 = new javax.swing.JTextField();
        txtAlpha5 = new javax.swing.JTextField();
        txtAlpha6 = new javax.swing.JTextField();
        txtAlpha7 = new javax.swing.JTextField();
        txtAlpha8 = new javax.swing.JTextField();
        txtAlpha9 = new javax.swing.JTextField();
        txtAlpha10 = new javax.swing.JTextField();
        txtAlpha11 = new javax.swing.JTextField();
        txtAlpha12 = new javax.swing.JTextField();
        txtAlpha13 = new javax.swing.JTextField();
        txtAlpha14 = new javax.swing.JTextField();
        txtAlpha15 = new javax.swing.JTextField();
        txtAlpha16 = new javax.swing.JTextField();
        txtAlpha17 = new javax.swing.JTextField();
        txtAlpha18 = new javax.swing.JTextField();
        txtAlpha19 = new javax.swing.JTextField();
        txtAlpha20 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jTextField21 = new javax.swing.JTextField();
        jTextField22 = new javax.swing.JTextField();
        jTextField23 = new javax.swing.JTextField();
        jTextField24 = new javax.swing.JTextField();
        jTextField25 = new javax.swing.JTextField();
        jTextField26 = new javax.swing.JTextField();
        jTextField27 = new javax.swing.JTextField();
        jTextField28 = new javax.swing.JTextField();
        jTextField29 = new javax.swing.JTextField();
        jTextField30 = new javax.swing.JTextField();
        jTextField31 = new javax.swing.JTextField();
        jTextField32 = new javax.swing.JTextField();
        jTextField33 = new javax.swing.JTextField();
        jTextField34 = new javax.swing.JTextField();
        jTextField35 = new javax.swing.JTextField();
        jTextField36 = new javax.swing.JTextField();
        jTextField37 = new javax.swing.JTextField();
        jTextField38 = new javax.swing.JTextField();
        jTextField39 = new javax.swing.JTextField();
        jTextField40 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel11 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jTextField41 = new javax.swing.JTextField();
        jTextField42 = new javax.swing.JTextField();
        jTextField43 = new javax.swing.JTextField();
        jTextField44 = new javax.swing.JTextField();
        jTextField45 = new javax.swing.JTextField();
        jTextField46 = new javax.swing.JTextField();
        jTextField47 = new javax.swing.JTextField();
        jTextField48 = new javax.swing.JTextField();
        jTextField49 = new javax.swing.JTextField();
        jTextField50 = new javax.swing.JTextField();
        jTextField51 = new javax.swing.JTextField();
        jTextField52 = new javax.swing.JTextField();
        jTextField53 = new javax.swing.JTextField();
        jTextField54 = new javax.swing.JTextField();
        jTextField55 = new javax.swing.JTextField();
        jTextField56 = new javax.swing.JTextField();
        jTextField57 = new javax.swing.JTextField();
        jTextField58 = new javax.swing.JTextField();
        jTextField59 = new javax.swing.JTextField();
        jTextField60 = new javax.swing.JTextField();
        jComboBox2 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jTextField61 = new javax.swing.JTextField();
        jTextField62 = new javax.swing.JTextField();
        jTextField63 = new javax.swing.JTextField();
        jTextField64 = new javax.swing.JTextField();
        jTextField65 = new javax.swing.JTextField();
        jTextField66 = new javax.swing.JTextField();
        jTextField67 = new javax.swing.JTextField();
        jTextField68 = new javax.swing.JTextField();
        jTextField69 = new javax.swing.JTextField();
        jTextField70 = new javax.swing.JTextField();
        jTextField71 = new javax.swing.JTextField();
        jTextField72 = new javax.swing.JTextField();
        jTextField73 = new javax.swing.JTextField();
        jTextField74 = new javax.swing.JTextField();
        jTextField75 = new javax.swing.JTextField();
        jTextField76 = new javax.swing.JTextField();
        jTextField77 = new javax.swing.JTextField();
        jTextField78 = new javax.swing.JTextField();
        jTextField79 = new javax.swing.JTextField();
        jTextField80 = new javax.swing.JTextField();
        jComboBox3 = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jPanel22 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jTextField81 = new javax.swing.JTextField();
        jTextField82 = new javax.swing.JTextField();
        jTextField83 = new javax.swing.JTextField();
        jTextField84 = new javax.swing.JTextField();
        jTextField85 = new javax.swing.JTextField();
        jTextField86 = new javax.swing.JTextField();
        jTextField87 = new javax.swing.JTextField();
        jTextField88 = new javax.swing.JTextField();
        jTextField89 = new javax.swing.JTextField();
        jTextField90 = new javax.swing.JTextField();
        jTextField91 = new javax.swing.JTextField();
        jTextField92 = new javax.swing.JTextField();
        jTextField93 = new javax.swing.JTextField();
        jTextField94 = new javax.swing.JTextField();
        jTextField95 = new javax.swing.JTextField();
        jTextField96 = new javax.swing.JTextField();
        jTextField97 = new javax.swing.JTextField();
        jTextField98 = new javax.swing.JTextField();
        jTextField99 = new javax.swing.JTextField();
        jTextField100 = new javax.swing.JTextField();
        jComboBox4 = new javax.swing.JComboBox();
        jPanel23 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jTextField101 = new javax.swing.JTextField();
        jTextField102 = new javax.swing.JTextField();
        jTextField103 = new javax.swing.JTextField();
        jTextField104 = new javax.swing.JTextField();
        jTextField105 = new javax.swing.JTextField();
        jTextField106 = new javax.swing.JTextField();
        jTextField107 = new javax.swing.JTextField();
        jTextField108 = new javax.swing.JTextField();
        jTextField109 = new javax.swing.JTextField();
        jTextField110 = new javax.swing.JTextField();
        jTextField111 = new javax.swing.JTextField();
        jTextField112 = new javax.swing.JTextField();
        jTextField113 = new javax.swing.JTextField();
        jTextField114 = new javax.swing.JTextField();
        jTextField115 = new javax.swing.JTextField();
        jTextField116 = new javax.swing.JTextField();
        jTextField117 = new javax.swing.JTextField();
        jTextField118 = new javax.swing.JTextField();
        jTextField119 = new javax.swing.JTextField();
        jTextField120 = new javax.swing.JTextField();
        jComboBox5 = new javax.swing.JComboBox();
        jPanel24 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jTextField121 = new javax.swing.JTextField();
        jTextField122 = new javax.swing.JTextField();
        jTextField123 = new javax.swing.JTextField();
        jTextField124 = new javax.swing.JTextField();
        jTextField125 = new javax.swing.JTextField();
        jTextField126 = new javax.swing.JTextField();
        jTextField127 = new javax.swing.JTextField();
        jTextField128 = new javax.swing.JTextField();
        jTextField129 = new javax.swing.JTextField();
        jTextField130 = new javax.swing.JTextField();
        jTextField131 = new javax.swing.JTextField();
        jTextField132 = new javax.swing.JTextField();
        jTextField133 = new javax.swing.JTextField();
        jTextField134 = new javax.swing.JTextField();
        jTextField135 = new javax.swing.JTextField();
        jTextField136 = new javax.swing.JTextField();
        jTextField137 = new javax.swing.JTextField();
        jTextField138 = new javax.swing.JTextField();
        jTextField139 = new javax.swing.JTextField();
        jTextField140 = new javax.swing.JTextField();
        jComboBox6 = new javax.swing.JComboBox();
        pnlLongDer = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtAlpha22 = new javax.swing.JTextField();
        txtAlpha21 = new javax.swing.JTextField();
        txtAlpha23 = new javax.swing.JTextField();
        txtAlpha24 = new javax.swing.JTextField();
        txtAlpha25 = new javax.swing.JTextField();
        txtAlpha26 = new javax.swing.JTextField();
        txtAlpha27 = new javax.swing.JTextField();
        txtAlpha28 = new javax.swing.JTextField();
        txtAlpha29 = new javax.swing.JTextField();
        txtAlpha30 = new javax.swing.JTextField();
        txtAlpha31 = new javax.swing.JTextField();
        txtAlpha32 = new javax.swing.JTextField();
        txtAlpha33 = new javax.swing.JTextField();
        txtAlpha34 = new javax.swing.JTextField();
        txtAlpha35 = new javax.swing.JTextField();
        txtAlpha36 = new javax.swing.JTextField();
        txtAlpha37 = new javax.swing.JTextField();
        txtAlpha38 = new javax.swing.JTextField();
        txtAlpha39 = new javax.swing.JTextField();
        txtAlpha40 = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jTextField201 = new javax.swing.JTextField();
        jTextField202 = new javax.swing.JTextField();
        jTextField203 = new javax.swing.JTextField();
        jTextField204 = new javax.swing.JTextField();
        jTextField205 = new javax.swing.JTextField();
        jTextField206 = new javax.swing.JTextField();
        jTextField207 = new javax.swing.JTextField();
        jTextField208 = new javax.swing.JTextField();
        jTextField209 = new javax.swing.JTextField();
        jTextField210 = new javax.swing.JTextField();
        jTextField211 = new javax.swing.JTextField();
        jTextField212 = new javax.swing.JTextField();
        jTextField213 = new javax.swing.JTextField();
        jTextField214 = new javax.swing.JTextField();
        jTextField215 = new javax.swing.JTextField();
        jTextField216 = new javax.swing.JTextField();
        jTextField217 = new javax.swing.JTextField();
        jTextField218 = new javax.swing.JTextField();
        jTextField219 = new javax.swing.JTextField();
        jTextField220 = new javax.swing.JTextField();
        jComboBox10 = new javax.swing.JComboBox();
        jPanel17 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jTextField181 = new javax.swing.JTextField();
        jTextField182 = new javax.swing.JTextField();
        jTextField183 = new javax.swing.JTextField();
        jTextField184 = new javax.swing.JTextField();
        jTextField185 = new javax.swing.JTextField();
        jTextField186 = new javax.swing.JTextField();
        jTextField187 = new javax.swing.JTextField();
        jTextField188 = new javax.swing.JTextField();
        jTextField189 = new javax.swing.JTextField();
        jTextField190 = new javax.swing.JTextField();
        jTextField191 = new javax.swing.JTextField();
        jTextField192 = new javax.swing.JTextField();
        jTextField193 = new javax.swing.JTextField();
        jTextField194 = new javax.swing.JTextField();
        jTextField195 = new javax.swing.JTextField();
        jTextField196 = new javax.swing.JTextField();
        jTextField197 = new javax.swing.JTextField();
        jTextField198 = new javax.swing.JTextField();
        jTextField199 = new javax.swing.JTextField();
        jTextField200 = new javax.swing.JTextField();
        jComboBox9 = new javax.swing.JComboBox();
        jButton9 = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jTextField221 = new javax.swing.JTextField();
        jTextField222 = new javax.swing.JTextField();
        jTextField223 = new javax.swing.JTextField();
        jTextField224 = new javax.swing.JTextField();
        jTextField225 = new javax.swing.JTextField();
        jTextField226 = new javax.swing.JTextField();
        jTextField227 = new javax.swing.JTextField();
        jTextField228 = new javax.swing.JTextField();
        jTextField229 = new javax.swing.JTextField();
        jTextField230 = new javax.swing.JTextField();
        jTextField231 = new javax.swing.JTextField();
        jTextField232 = new javax.swing.JTextField();
        jTextField233 = new javax.swing.JTextField();
        jTextField234 = new javax.swing.JTextField();
        jTextField235 = new javax.swing.JTextField();
        jTextField236 = new javax.swing.JTextField();
        jTextField237 = new javax.swing.JTextField();
        jTextField238 = new javax.swing.JTextField();
        jTextField239 = new javax.swing.JTextField();
        jTextField240 = new javax.swing.JTextField();
        jComboBox11 = new javax.swing.JComboBox();
        jPanel19 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jTextField241 = new javax.swing.JTextField();
        jTextField242 = new javax.swing.JTextField();
        jTextField243 = new javax.swing.JTextField();
        jTextField244 = new javax.swing.JTextField();
        jTextField245 = new javax.swing.JTextField();
        jTextField246 = new javax.swing.JTextField();
        jTextField247 = new javax.swing.JTextField();
        jTextField248 = new javax.swing.JTextField();
        jTextField249 = new javax.swing.JTextField();
        jTextField250 = new javax.swing.JTextField();
        jTextField251 = new javax.swing.JTextField();
        jTextField252 = new javax.swing.JTextField();
        jTextField253 = new javax.swing.JTextField();
        jTextField254 = new javax.swing.JTextField();
        jTextField255 = new javax.swing.JTextField();
        jTextField256 = new javax.swing.JTextField();
        jTextField257 = new javax.swing.JTextField();
        jTextField258 = new javax.swing.JTextField();
        jTextField259 = new javax.swing.JTextField();
        jTextField260 = new javax.swing.JTextField();
        jComboBox12 = new javax.swing.JComboBox();
        jButton10 = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jTextField141 = new javax.swing.JTextField();
        jTextField142 = new javax.swing.JTextField();
        jTextField143 = new javax.swing.JTextField();
        jTextField144 = new javax.swing.JTextField();
        jTextField145 = new javax.swing.JTextField();
        jTextField146 = new javax.swing.JTextField();
        jTextField147 = new javax.swing.JTextField();
        jTextField148 = new javax.swing.JTextField();
        jTextField149 = new javax.swing.JTextField();
        jTextField150 = new javax.swing.JTextField();
        jTextField151 = new javax.swing.JTextField();
        jTextField152 = new javax.swing.JTextField();
        jTextField153 = new javax.swing.JTextField();
        jTextField154 = new javax.swing.JTextField();
        jTextField155 = new javax.swing.JTextField();
        jTextField156 = new javax.swing.JTextField();
        jTextField157 = new javax.swing.JTextField();
        jTextField158 = new javax.swing.JTextField();
        jTextField159 = new javax.swing.JTextField();
        jTextField160 = new javax.swing.JTextField();
        jComboBox7 = new javax.swing.JComboBox();
        pnlLatModes = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnlLongModes = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pnlOther = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Datcom Textual Output Analysis");

        pnlMain.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("Derivatives that do not change with angle of attack:");

        jLabel7.setText("Cybeta:");

        txtCybeta.setEditable(false);

        jLabel8.setText("Cnbeta:");

        txtCnbeta.setEditable(false);

        jLabel9.setText("CLq:");

        txtClq.setEditable(false);

        jLabel10.setText("Cmq:");

        txtCmq.setEditable(false);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setText("Control Derivatives:");

        btnAileron.setText("Aileron Derivatives");
        btnAileron.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAileronActionPerformed(evt);
            }
        });

        btnFore.setText("Foremost Symmetric Control Surface Derivatives");
        btnFore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForeActionPerformed(evt);
            }
        });

        btnAft.setText("Aftmost Symmetric Control Surface Derivatives");
        btnAft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAftActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSummaryLayout = new javax.swing.GroupLayout(pnlSummary);
        pnlSummary.setLayout(pnlSummaryLayout);
        pnlSummaryLayout.setHorizontalGroup(
            pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSummaryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(pnlSummaryLayout.createSequentialGroup()
                        .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addGap(71, 71, 71)
                        .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCybeta, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                            .addComponent(txtCnbeta)
                            .addComponent(txtClq)
                            .addComponent(txtCmq)))
                    .addComponent(jLabel11)
                    .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnAft, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFore, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAileron, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(517, Short.MAX_VALUE))
        );
        pnlSummaryLayout.setVerticalGroup(
            pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSummaryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtCybeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtCnbeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtClq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtCmq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAileron)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAft)
                .addContainerGap(388, Short.MAX_VALUE))
        );

        pnlMain.addTab("Summary", pnlSummary);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Alpha (deg)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4)
            .addComponent(txtAlpha1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha7, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha8, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha9, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha10, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha11, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha12, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha13, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha14, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha15, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha16, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha17, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha18, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha19, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha20, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(9, 9, 9)
                .addComponent(txtAlpha1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jComboBox1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-none-", "Clbeta", "Clp", "Cyp", "Cnp", "Cnr", "Clr" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox1, 0, 70, Short.MAX_VALUE)
            .addComponent(jTextField22)
            .addComponent(jTextField21)
            .addComponent(jTextField23)
            .addComponent(jTextField24)
            .addComponent(jTextField25)
            .addComponent(jTextField26)
            .addComponent(jTextField27)
            .addComponent(jTextField28)
            .addComponent(jTextField29)
            .addComponent(jTextField30)
            .addComponent(jTextField31)
            .addComponent(jTextField32)
            .addComponent(jTextField33)
            .addComponent(jTextField34)
            .addComponent(jTextField35)
            .addComponent(jTextField36)
            .addComponent(jTextField37)
            .addComponent(jTextField38)
            .addComponent(jTextField39)
            .addComponent(jTextField40)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jComboBox2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-none-", "Clbeta", "Clp", "Cyp", "Cnp", "Cnr", "Clr" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox2, 0, 70, Short.MAX_VALUE)
            .addComponent(jTextField42)
            .addComponent(jTextField41)
            .addComponent(jTextField43)
            .addComponent(jTextField44)
            .addComponent(jTextField45)
            .addComponent(jTextField46)
            .addComponent(jTextField47)
            .addComponent(jTextField48)
            .addComponent(jTextField49)
            .addComponent(jTextField50)
            .addComponent(jTextField51)
            .addComponent(jTextField52)
            .addComponent(jTextField53)
            .addComponent(jTextField54)
            .addComponent(jTextField55)
            .addComponent(jTextField56)
            .addComponent(jTextField57)
            .addComponent(jTextField58)
            .addComponent(jTextField59)
            .addComponent(jTextField60)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jTextField42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton1.setText("Remove");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jComboBox3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-none-", "Clbeta", "Clp", "Cyp", "Cnp", "Cnr", "Clr" }));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox3, 0, 70, Short.MAX_VALUE)
            .addComponent(jTextField62)
            .addComponent(jTextField61)
            .addComponent(jTextField63)
            .addComponent(jTextField64)
            .addComponent(jTextField65)
            .addComponent(jTextField66)
            .addComponent(jTextField67)
            .addComponent(jTextField68)
            .addComponent(jTextField69)
            .addComponent(jTextField70)
            .addComponent(jTextField71)
            .addComponent(jTextField72)
            .addComponent(jTextField73)
            .addComponent(jTextField74)
            .addComponent(jTextField75)
            .addComponent(jTextField76)
            .addComponent(jTextField77)
            .addComponent(jTextField78)
            .addComponent(jTextField79)
            .addComponent(jTextField80)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jTextField62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField74, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField75, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField76, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField77, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField78, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField79, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton2.setText("Remove");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jButton3.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton3.setText("Remove");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jComboBox4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-none-", "Clbeta", "Clp", "Cyp", "Cnp", "Cnr", "Clr" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox4, 0, 70, Short.MAX_VALUE)
            .addComponent(jTextField82)
            .addComponent(jTextField81)
            .addComponent(jTextField83)
            .addComponent(jTextField84)
            .addComponent(jTextField85)
            .addComponent(jTextField86)
            .addComponent(jTextField87)
            .addComponent(jTextField88)
            .addComponent(jTextField89)
            .addComponent(jTextField90)
            .addComponent(jTextField91)
            .addComponent(jTextField92)
            .addComponent(jTextField93)
            .addComponent(jTextField94)
            .addComponent(jTextField95)
            .addComponent(jTextField96)
            .addComponent(jTextField97)
            .addComponent(jTextField98)
            .addComponent(jTextField99)
            .addComponent(jTextField100)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jTextField82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField84, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField85, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField86, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField87, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField88, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField89, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField90, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField91, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField92, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField93, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField94, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField95, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField96, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField97, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField98, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField99, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField100, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jButton4.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton4.setText("Remove");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jComboBox5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-none-", "Clbeta", "Clp", "Cyp", "Cnp", "Cnr", "Clr" }));
        jComboBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox5, 0, 70, Short.MAX_VALUE)
            .addComponent(jTextField102)
            .addComponent(jTextField101)
            .addComponent(jTextField103)
            .addComponent(jTextField104)
            .addComponent(jTextField105)
            .addComponent(jTextField106)
            .addComponent(jTextField107)
            .addComponent(jTextField108)
            .addComponent(jTextField109)
            .addComponent(jTextField110)
            .addComponent(jTextField111)
            .addComponent(jTextField112)
            .addComponent(jTextField113)
            .addComponent(jTextField114)
            .addComponent(jTextField115)
            .addComponent(jTextField116)
            .addComponent(jTextField117)
            .addComponent(jTextField118)
            .addComponent(jTextField119)
            .addComponent(jTextField120)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jTextField102, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField101, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField103, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField104, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField105, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField106, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField107, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField108, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField109, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField110, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField111, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField112, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField113, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField114, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField115, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField116, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField117, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField118, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField119, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField120, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jButton5.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton5.setText("Remove");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jComboBox6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-none-", "Clbeta", "Clp", "Cyp", "Cnp", "Cnr", "Clr" }));
        jComboBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox6, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField122, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField121, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField123, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField124, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField125, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField126, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField127, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField128, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField129, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField130, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField131, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField132, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField133, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField134, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField135, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField136, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField137, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField138, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField139, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField140, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jTextField122, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField121, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField123, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField124, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField125, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField126, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField127, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField128, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField129, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField130, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField131, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField132, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField133, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField134, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField135, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField136, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField137, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField138, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField139, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField140, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(318, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlLatDerLayout = new javax.swing.GroupLayout(pnlLatDer);
        pnlLatDer.setLayout(pnlLatDerLayout);
        pnlLatDerLayout.setHorizontalGroup(
            pnlLatDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLatDerLayout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 70, Short.MAX_VALUE))
        );
        pnlLatDerLayout.setVerticalGroup(
            pnlLatDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLatDerLayout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlMain.addTab("Lateral Derivatives", pnlLatDer);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Alpha (deg)");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5)
            .addComponent(txtAlpha21, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha22, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha23, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha24, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha25, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha26, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha27, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha28, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha29, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha30, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha31, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha32, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha33, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha34, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha35, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha36, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha37, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha38, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha39, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtAlpha40, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(9, 9, 9)
                .addComponent(txtAlpha21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlpha40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jComboBox10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-none-", "Cd", "CL", "Cm", "CLad", "Cmad" }));
        jComboBox10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox10, 0, 70, Short.MAX_VALUE)
            .addComponent(jTextField202)
            .addComponent(jTextField201)
            .addComponent(jTextField203)
            .addComponent(jTextField204)
            .addComponent(jTextField205)
            .addComponent(jTextField206)
            .addComponent(jTextField207)
            .addComponent(jTextField208)
            .addComponent(jTextField209)
            .addComponent(jTextField210)
            .addComponent(jTextField211)
            .addComponent(jTextField212)
            .addComponent(jTextField213)
            .addComponent(jTextField214)
            .addComponent(jTextField215)
            .addComponent(jTextField216)
            .addComponent(jTextField217)
            .addComponent(jTextField218)
            .addComponent(jTextField219)
            .addComponent(jTextField220)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jTextField202, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField201, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField203, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField204, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField205, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField206, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField207, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField208, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField209, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField210, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField211, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField212, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField213, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField214, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField215, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField216, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField217, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField218, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField219, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField220, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jComboBox9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-none-", "Cd", "CL", "Cm", "CLad", "Cmad" }));
        jComboBox9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox9, 0, 70, Short.MAX_VALUE)
            .addComponent(jTextField182)
            .addComponent(jTextField181)
            .addComponent(jTextField183)
            .addComponent(jTextField184)
            .addComponent(jTextField185)
            .addComponent(jTextField186)
            .addComponent(jTextField187)
            .addComponent(jTextField188)
            .addComponent(jTextField189)
            .addComponent(jTextField190)
            .addComponent(jTextField191)
            .addComponent(jTextField192)
            .addComponent(jTextField193)
            .addComponent(jTextField194)
            .addComponent(jTextField195)
            .addComponent(jTextField196)
            .addComponent(jTextField197)
            .addComponent(jTextField198)
            .addComponent(jTextField199)
            .addComponent(jTextField200)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jTextField182, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField181, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField183, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField184, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField185, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField186, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField187, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField188, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField189, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField190, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField191, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField192, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField193, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField194, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField195, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField196, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField197, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField198, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField199, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField200, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jButton9.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton9.setText("Remove");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jButton6.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton6.setText("Remove");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jComboBox11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-none-", "Cd", "CL", "Cm", "CLad", "Cmad" }));
        jComboBox11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox11, 0, 70, Short.MAX_VALUE)
            .addComponent(jTextField222)
            .addComponent(jTextField221)
            .addComponent(jTextField223)
            .addComponent(jTextField224)
            .addComponent(jTextField225)
            .addComponent(jTextField226)
            .addComponent(jTextField227)
            .addComponent(jTextField228)
            .addComponent(jTextField229)
            .addComponent(jTextField230)
            .addComponent(jTextField231)
            .addComponent(jTextField232)
            .addComponent(jTextField233)
            .addComponent(jTextField234)
            .addComponent(jTextField235)
            .addComponent(jTextField236)
            .addComponent(jTextField237)
            .addComponent(jTextField238)
            .addComponent(jTextField239)
            .addComponent(jTextField240)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jTextField222, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField221, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField223, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField224, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField225, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField226, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField227, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField228, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField229, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField230, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField231, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField232, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField233, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField234, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField235, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField236, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField237, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField238, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField239, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField240, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jComboBox12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-none-", "Cd", "CL", "Cm", "CLad", "Cmad" }));
        jComboBox12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox12, 0, 70, Short.MAX_VALUE)
            .addComponent(jTextField242)
            .addComponent(jTextField241)
            .addComponent(jTextField243)
            .addComponent(jTextField244)
            .addComponent(jTextField245)
            .addComponent(jTextField246)
            .addComponent(jTextField247)
            .addComponent(jTextField248)
            .addComponent(jTextField249)
            .addComponent(jTextField250)
            .addComponent(jTextField251)
            .addComponent(jTextField252)
            .addComponent(jTextField253)
            .addComponent(jTextField254)
            .addComponent(jTextField255)
            .addComponent(jTextField256)
            .addComponent(jTextField257)
            .addComponent(jTextField258)
            .addComponent(jTextField259)
            .addComponent(jTextField260)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jTextField242, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField241, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField243, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField244, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField245, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField246, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField247, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField248, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField249, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField250, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField251, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField252, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField253, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField254, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField255, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField256, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField257, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField258, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField259, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField260, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jButton10.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton10.setText("Remove");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jButton10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jButton8.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton8.setText("Remove");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jComboBox7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-none-", "Cd", "CL", "Cm", "CLad", "Cmad" }));
        jComboBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox7, 0, 70, Short.MAX_VALUE)
            .addComponent(jTextField142)
            .addComponent(jTextField141)
            .addComponent(jTextField143)
            .addComponent(jTextField144)
            .addComponent(jTextField145)
            .addComponent(jTextField146)
            .addComponent(jTextField147)
            .addComponent(jTextField148)
            .addComponent(jTextField149)
            .addComponent(jTextField150)
            .addComponent(jTextField151)
            .addComponent(jTextField152)
            .addComponent(jTextField153)
            .addComponent(jTextField154)
            .addComponent(jTextField155)
            .addComponent(jTextField156)
            .addComponent(jTextField157)
            .addComponent(jTextField158)
            .addComponent(jTextField159)
            .addComponent(jTextField160)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jTextField142, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField141, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField143, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField144, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField145, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField146, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField147, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField148, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField149, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField150, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField151, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField152, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField153, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField154, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField155, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField156, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField157, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField158, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField159, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField160, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(476, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlLongDerLayout = new javax.swing.GroupLayout(pnlLongDer);
        pnlLongDer.setLayout(pnlLongDerLayout);
        pnlLongDerLayout.setHorizontalGroup(
            pnlLongDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLongDerLayout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlLongDerLayout.setVerticalGroup(
            pnlLongDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLongDerLayout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlMain.addTab("Longitudinal Derivatives", pnlLongDer);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("Work In Progress");

        javax.swing.GroupLayout pnlLatModesLayout = new javax.swing.GroupLayout(pnlLatModes);
        pnlLatModes.setLayout(pnlLatModesLayout);
        pnlLatModesLayout.setHorizontalGroup(
            pnlLatModesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLatModesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(667, Short.MAX_VALUE))
        );
        pnlLatModesLayout.setVerticalGroup(
            pnlLatModesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLatModesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(607, Short.MAX_VALUE))
        );

        pnlMain.addTab("Lateral Modes", pnlLatModes);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel2.setText("Work In Progress");

        javax.swing.GroupLayout pnlLongModesLayout = new javax.swing.GroupLayout(pnlLongModes);
        pnlLongModes.setLayout(pnlLongModesLayout);
        pnlLongModesLayout.setHorizontalGroup(
            pnlLongModesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLongModesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(667, Short.MAX_VALUE))
        );
        pnlLongModesLayout.setVerticalGroup(
            pnlLongModesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLongModesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(607, Short.MAX_VALUE))
        );

        pnlMain.addTab("Longitudinal Modes", pnlLongModes);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel3.setText("Work In Progress");

        javax.swing.GroupLayout pnlOtherLayout = new javax.swing.GroupLayout(pnlOther);
        pnlOther.setLayout(pnlOtherLayout);
        pnlOtherLayout.setHorizontalGroup(
            pnlOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(667, Short.MAX_VALUE))
        );
        pnlOtherLayout.setVerticalGroup(
            pnlOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(607, Short.MAX_VALUE))
        );

        pnlMain.addTab("Other Data", pnlOther);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (jButton1.getText() == "Add") {
            jButton1.setText("Remove");
            jPanel3.setVisible(true);

        } else if (jButton1.getText() == "Remove") {
            jButton1.setText("Add");
            jPanel3.setVisible(false);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (jButton2.getText() == "Add") {
            jButton2.setText("Remove");
            jPanel4.setVisible(true);
        } else if (jButton2.getText() == "Remove") {
            jButton2.setText("Add");
            jPanel4.setVisible(false);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (jButton3.getText() == "Add") {
            jButton3.setText("Remove");
            jPanel5.setVisible(true);
        } else if (jButton3.getText() == "Remove") {
            jButton3.setText("Add");
            jPanel5.setVisible(false);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if (jButton4.getText() == "Add") {
            jButton4.setText("Remove");
            jPanel6.setVisible(true);
        } else if (jButton4.getText() == "Remove") {
            jButton4.setText("Add");
            jPanel6.setVisible(false);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (jButton5.getText() == "Add") {
            jButton5.setText("Remove");
            jPanel7.setVisible(true);
        } else if (jButton5.getText() == "Remove") {
            jButton5.setText("Add");
            jPanel7.setVisible(false);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        if (jButton9.getText() == "Add") {
            jButton9.setText("Remove");
            jPanel13.setVisible(true);
        } else if (jButton9.getText() == "Remove") {
            jButton9.setText("Add");
            jPanel13.setVisible(false);
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (jButton6.getText() == "Add") {
            jButton6.setText("Remove");
            jPanel15.setVisible(true);
        } else if (jButton6.getText() == "Remove") {
            jButton6.setText("Add");
            jPanel15.setVisible(false);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        if (jButton10.getText() == "Add") {
            jButton10.setText("Remove");
            jPanel16.setVisible(true);
        } else if (jButton10.getText() == "Remove") {
            jButton10.setText("Add");
            jPanel16.setVisible(false);
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        if (jButton8.getText() == "Add") {
            jButton8.setText("Remove");
            jPanel10.setVisible(true);
        } else if (jButton8.getText() == "Remove") {
            jButton8.setText("Add");
            jPanel10.setVisible(false);
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        int index = jComboBox1.getSelectedIndex();
        if (index == 0) {
            clearColumn(1, 1);
        } else if (index == 1) {
            showClbeta(1);
        }
        else if(index == 2) {
            showClp(1);
        }
        else if(index == 3) {
            showCyp(1);
        }
        else if(index == 4) {
            showCnp(1);
        }
        else if(index == 5) {
            showCnr(1);
        }
        else if(index == 6) {
            showClr(1);
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        int index = jComboBox2.getSelectedIndex();
        if (index == 0) {
            clearColumn(1, 2);
        } else if (index == 1) {
            showClbeta(2);
        }
        else if(index == 2) {
            showClp(2);
        }
        else if(index == 3) {
            showCyp(2);
        }
        else if(index == 4) {
            showCnp(2);
        }
        else if(index == 5) {
            showCnr(2);
        }
        else if(index == 6) {
            showClr(2);
        }
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        int index = jComboBox3.getSelectedIndex();
        if (index == 0) {
            clearColumn(1, 3);
        } else if (index == 1) {
            showClbeta(3);
        }
        else if(index == 2) {
            showClp(3);
        }
        else if(index == 3) {
            showCyp(3);
        }
        else if(index == 4) {
            showCnp(3);
        }
        else if(index == 5) {
            showCnr(3);
        }
        else if(index == 6) {
            showClr(3);
        }
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        int index = jComboBox4.getSelectedIndex();
        if (index == 0) {
            clearColumn(1, 4);
        } else if (index == 1) {
            showClbeta(4);
        }
        else if(index == 2) {
            showClp(4);
        }
        else if(index == 3) {
            showCyp(4);
        }
        else if(index == 4) {
            showCnp(4);
        }
        else if(index == 5) {
            showCnr(4);
        }
        else if(index == 6) {
            showClr(4);
        }
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
        int index = jComboBox5.getSelectedIndex();
        if (index == 0) {
            clearColumn(1, 5);
        } else if (index == 1) {
            showClbeta(5);
        }
        else if(index == 2) {
            showClp(5);
        }
        else if(index == 3) {
            showCyp(5);
        }
        else if(index == 4) {
            showCnp(5);
        }
        else if(index == 5) {
            showCnr(5);
        }
        else if(index == 6) {
            showClr(5);
        }
    }//GEN-LAST:event_jComboBox5ActionPerformed

    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        int index = jComboBox6.getSelectedIndex();
        if (index == 0) {
            clearColumn(1, 6);
        } else if (index == 1) {
            showClbeta(6);
        }
        else if(index == 2) {
            showClp(6);
        }
        else if(index == 3) {
            showCyp(6);
        }
        else if(index == 4) {
            showCnp(6);
        }
        else if(index == 5) {
            showCnr(6);
        }
        else if(index == 6) {
            showClr(6);
        }
    }//GEN-LAST:event_jComboBox6ActionPerformed

    private void jComboBox10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox10ActionPerformed
        int index = jComboBox10.getSelectedIndex();
        if (index == 0) {
            clearColumn(2, 1);
        } else if (index == 1) {
            showCd(1);
        }
        else if(index == 2) {
            showCL(1);
        }
        else if(index == 3) {
            showCm(1);
        }
        else if(index == 4) {
            showCLad(1);
        }
        else if(index == 5) {
            showCmad(1);
        }
    }//GEN-LAST:event_jComboBox10ActionPerformed

    private void jComboBox9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox9ActionPerformed
        int index = jComboBox9.getSelectedIndex();
        if (index == 0) {
            clearColumn(2, 2);
        } else if (index == 1) {
            showCd(2);
        }
        else if(index == 2) {
            showCL(2);
        }
        else if(index == 3) {
            showCm(2);
        }
        else if(index == 4) {
            showCLad(2);
        }
        else if(index == 5) {
            showCmad(2);
        }
    }//GEN-LAST:event_jComboBox9ActionPerformed

    private void jComboBox11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox11ActionPerformed
        int index = jComboBox11.getSelectedIndex();
        if (index == 0) {
            clearColumn(2, 3);
        } else if (index == 1) {
            showCd(3);
        }
        else if(index == 2) {
            showCL(3);
        }
        else if(index == 3) {
            showCm(3);
        }
        else if(index == 4) {
            showCLad(3);
        }
        else if(index == 5) {
            showCmad(3);
        }
    }//GEN-LAST:event_jComboBox11ActionPerformed

    private void jComboBox12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox12ActionPerformed
        int index = jComboBox12.getSelectedIndex();
        if (index == 0) {
            clearColumn(2, 4);
        } else if (index == 1) {
            showCd(4);
        }
        else if(index == 2) {
            showCL(4);
        }
        else if(index == 3) {
            showCm(4);
        }
        else if(index == 4) {
            showCLad(4);
        }
        else if(index == 5) {
            showCmad(4);
        }
    }//GEN-LAST:event_jComboBox12ActionPerformed

    private void jComboBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox7ActionPerformed
        int index = jComboBox7.getSelectedIndex();
        if (index == 0) {
            clearColumn(2, 5);
        } else if (index == 1) {
            showCd(5);
        }
        else if(index == 2) {
            showCL(5);
        }
        else if(index == 3) {
            showCm(5);
        }
        else if(index == 4) {
            showCLad(5);
        }
        else if(index == 5) {
            showCmad(5);
        }
    }//GEN-LAST:event_jComboBox7ActionPerformed

    private void btnAileronActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAileronActionPerformed
        WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("No aileron data detected.");
    }//GEN-LAST:event_btnAileronActionPerformed

    private void btnAftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAftActionPerformed
        WorkInProgressWindow wip = new WorkInProgressWindow();
            wip.setVisible(true);
            wip.changeText("Only one symmetric control surface detected.");
    }//GEN-LAST:event_btnAftActionPerformed

    private void btnForeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForeActionPerformed
        ControlSurfaceAnalysis csa = new ControlSurfaceAnalysis(firstDefList, firstCm, firstCL,firstDefs);
        csa.setVisible(true);
    }//GEN-LAST:event_btnForeActionPerformed

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
            java.util.logging.Logger.getLogger(AnalysisWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnalysisWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnalysisWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnalysisWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AnalysisWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAft;
    private javax.swing.JButton btnAileron;
    private javax.swing.JButton btnFore;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox10;
    private javax.swing.JComboBox jComboBox11;
    private javax.swing.JComboBox jComboBox12;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTextField jTextField100;
    private javax.swing.JTextField jTextField101;
    private javax.swing.JTextField jTextField102;
    private javax.swing.JTextField jTextField103;
    private javax.swing.JTextField jTextField104;
    private javax.swing.JTextField jTextField105;
    private javax.swing.JTextField jTextField106;
    private javax.swing.JTextField jTextField107;
    private javax.swing.JTextField jTextField108;
    private javax.swing.JTextField jTextField109;
    private javax.swing.JTextField jTextField110;
    private javax.swing.JTextField jTextField111;
    private javax.swing.JTextField jTextField112;
    private javax.swing.JTextField jTextField113;
    private javax.swing.JTextField jTextField114;
    private javax.swing.JTextField jTextField115;
    private javax.swing.JTextField jTextField116;
    private javax.swing.JTextField jTextField117;
    private javax.swing.JTextField jTextField118;
    private javax.swing.JTextField jTextField119;
    private javax.swing.JTextField jTextField120;
    private javax.swing.JTextField jTextField121;
    private javax.swing.JTextField jTextField122;
    private javax.swing.JTextField jTextField123;
    private javax.swing.JTextField jTextField124;
    private javax.swing.JTextField jTextField125;
    private javax.swing.JTextField jTextField126;
    private javax.swing.JTextField jTextField127;
    private javax.swing.JTextField jTextField128;
    private javax.swing.JTextField jTextField129;
    private javax.swing.JTextField jTextField130;
    private javax.swing.JTextField jTextField131;
    private javax.swing.JTextField jTextField132;
    private javax.swing.JTextField jTextField133;
    private javax.swing.JTextField jTextField134;
    private javax.swing.JTextField jTextField135;
    private javax.swing.JTextField jTextField136;
    private javax.swing.JTextField jTextField137;
    private javax.swing.JTextField jTextField138;
    private javax.swing.JTextField jTextField139;
    private javax.swing.JTextField jTextField140;
    private javax.swing.JTextField jTextField141;
    private javax.swing.JTextField jTextField142;
    private javax.swing.JTextField jTextField143;
    private javax.swing.JTextField jTextField144;
    private javax.swing.JTextField jTextField145;
    private javax.swing.JTextField jTextField146;
    private javax.swing.JTextField jTextField147;
    private javax.swing.JTextField jTextField148;
    private javax.swing.JTextField jTextField149;
    private javax.swing.JTextField jTextField150;
    private javax.swing.JTextField jTextField151;
    private javax.swing.JTextField jTextField152;
    private javax.swing.JTextField jTextField153;
    private javax.swing.JTextField jTextField154;
    private javax.swing.JTextField jTextField155;
    private javax.swing.JTextField jTextField156;
    private javax.swing.JTextField jTextField157;
    private javax.swing.JTextField jTextField158;
    private javax.swing.JTextField jTextField159;
    private javax.swing.JTextField jTextField160;
    private javax.swing.JTextField jTextField181;
    private javax.swing.JTextField jTextField182;
    private javax.swing.JTextField jTextField183;
    private javax.swing.JTextField jTextField184;
    private javax.swing.JTextField jTextField185;
    private javax.swing.JTextField jTextField186;
    private javax.swing.JTextField jTextField187;
    private javax.swing.JTextField jTextField188;
    private javax.swing.JTextField jTextField189;
    private javax.swing.JTextField jTextField190;
    private javax.swing.JTextField jTextField191;
    private javax.swing.JTextField jTextField192;
    private javax.swing.JTextField jTextField193;
    private javax.swing.JTextField jTextField194;
    private javax.swing.JTextField jTextField195;
    private javax.swing.JTextField jTextField196;
    private javax.swing.JTextField jTextField197;
    private javax.swing.JTextField jTextField198;
    private javax.swing.JTextField jTextField199;
    private javax.swing.JTextField jTextField200;
    private javax.swing.JTextField jTextField201;
    private javax.swing.JTextField jTextField202;
    private javax.swing.JTextField jTextField203;
    private javax.swing.JTextField jTextField204;
    private javax.swing.JTextField jTextField205;
    private javax.swing.JTextField jTextField206;
    private javax.swing.JTextField jTextField207;
    private javax.swing.JTextField jTextField208;
    private javax.swing.JTextField jTextField209;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField210;
    private javax.swing.JTextField jTextField211;
    private javax.swing.JTextField jTextField212;
    private javax.swing.JTextField jTextField213;
    private javax.swing.JTextField jTextField214;
    private javax.swing.JTextField jTextField215;
    private javax.swing.JTextField jTextField216;
    private javax.swing.JTextField jTextField217;
    private javax.swing.JTextField jTextField218;
    private javax.swing.JTextField jTextField219;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField220;
    private javax.swing.JTextField jTextField221;
    private javax.swing.JTextField jTextField222;
    private javax.swing.JTextField jTextField223;
    private javax.swing.JTextField jTextField224;
    private javax.swing.JTextField jTextField225;
    private javax.swing.JTextField jTextField226;
    private javax.swing.JTextField jTextField227;
    private javax.swing.JTextField jTextField228;
    private javax.swing.JTextField jTextField229;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField230;
    private javax.swing.JTextField jTextField231;
    private javax.swing.JTextField jTextField232;
    private javax.swing.JTextField jTextField233;
    private javax.swing.JTextField jTextField234;
    private javax.swing.JTextField jTextField235;
    private javax.swing.JTextField jTextField236;
    private javax.swing.JTextField jTextField237;
    private javax.swing.JTextField jTextField238;
    private javax.swing.JTextField jTextField239;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField240;
    private javax.swing.JTextField jTextField241;
    private javax.swing.JTextField jTextField242;
    private javax.swing.JTextField jTextField243;
    private javax.swing.JTextField jTextField244;
    private javax.swing.JTextField jTextField245;
    private javax.swing.JTextField jTextField246;
    private javax.swing.JTextField jTextField247;
    private javax.swing.JTextField jTextField248;
    private javax.swing.JTextField jTextField249;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField250;
    private javax.swing.JTextField jTextField251;
    private javax.swing.JTextField jTextField252;
    private javax.swing.JTextField jTextField253;
    private javax.swing.JTextField jTextField254;
    private javax.swing.JTextField jTextField255;
    private javax.swing.JTextField jTextField256;
    private javax.swing.JTextField jTextField257;
    private javax.swing.JTextField jTextField258;
    private javax.swing.JTextField jTextField259;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField260;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField34;
    private javax.swing.JTextField jTextField35;
    private javax.swing.JTextField jTextField36;
    private javax.swing.JTextField jTextField37;
    private javax.swing.JTextField jTextField38;
    private javax.swing.JTextField jTextField39;
    private javax.swing.JTextField jTextField40;
    private javax.swing.JTextField jTextField41;
    private javax.swing.JTextField jTextField42;
    private javax.swing.JTextField jTextField43;
    private javax.swing.JTextField jTextField44;
    private javax.swing.JTextField jTextField45;
    private javax.swing.JTextField jTextField46;
    private javax.swing.JTextField jTextField47;
    private javax.swing.JTextField jTextField48;
    private javax.swing.JTextField jTextField49;
    private javax.swing.JTextField jTextField50;
    private javax.swing.JTextField jTextField51;
    private javax.swing.JTextField jTextField52;
    private javax.swing.JTextField jTextField53;
    private javax.swing.JTextField jTextField54;
    private javax.swing.JTextField jTextField55;
    private javax.swing.JTextField jTextField56;
    private javax.swing.JTextField jTextField57;
    private javax.swing.JTextField jTextField58;
    private javax.swing.JTextField jTextField59;
    private javax.swing.JTextField jTextField60;
    private javax.swing.JTextField jTextField61;
    private javax.swing.JTextField jTextField62;
    private javax.swing.JTextField jTextField63;
    private javax.swing.JTextField jTextField64;
    private javax.swing.JTextField jTextField65;
    private javax.swing.JTextField jTextField66;
    private javax.swing.JTextField jTextField67;
    private javax.swing.JTextField jTextField68;
    private javax.swing.JTextField jTextField69;
    private javax.swing.JTextField jTextField70;
    private javax.swing.JTextField jTextField71;
    private javax.swing.JTextField jTextField72;
    private javax.swing.JTextField jTextField73;
    private javax.swing.JTextField jTextField74;
    private javax.swing.JTextField jTextField75;
    private javax.swing.JTextField jTextField76;
    private javax.swing.JTextField jTextField77;
    private javax.swing.JTextField jTextField78;
    private javax.swing.JTextField jTextField79;
    private javax.swing.JTextField jTextField80;
    private javax.swing.JTextField jTextField81;
    private javax.swing.JTextField jTextField82;
    private javax.swing.JTextField jTextField83;
    private javax.swing.JTextField jTextField84;
    private javax.swing.JTextField jTextField85;
    private javax.swing.JTextField jTextField86;
    private javax.swing.JTextField jTextField87;
    private javax.swing.JTextField jTextField88;
    private javax.swing.JTextField jTextField89;
    private javax.swing.JTextField jTextField90;
    private javax.swing.JTextField jTextField91;
    private javax.swing.JTextField jTextField92;
    private javax.swing.JTextField jTextField93;
    private javax.swing.JTextField jTextField94;
    private javax.swing.JTextField jTextField95;
    private javax.swing.JTextField jTextField96;
    private javax.swing.JTextField jTextField97;
    private javax.swing.JTextField jTextField98;
    private javax.swing.JTextField jTextField99;
    private javax.swing.JPanel pnlLatDer;
    private javax.swing.JPanel pnlLatModes;
    private javax.swing.JPanel pnlLongDer;
    private javax.swing.JPanel pnlLongModes;
    private javax.swing.JTabbedPane pnlMain;
    private javax.swing.JPanel pnlOther;
    private javax.swing.JPanel pnlSummary;
    private javax.swing.JTextField txtAlpha1;
    private javax.swing.JTextField txtAlpha10;
    private javax.swing.JTextField txtAlpha11;
    private javax.swing.JTextField txtAlpha12;
    private javax.swing.JTextField txtAlpha13;
    private javax.swing.JTextField txtAlpha14;
    private javax.swing.JTextField txtAlpha15;
    private javax.swing.JTextField txtAlpha16;
    private javax.swing.JTextField txtAlpha17;
    private javax.swing.JTextField txtAlpha18;
    private javax.swing.JTextField txtAlpha19;
    private javax.swing.JTextField txtAlpha2;
    private javax.swing.JTextField txtAlpha20;
    private javax.swing.JTextField txtAlpha21;
    private javax.swing.JTextField txtAlpha22;
    private javax.swing.JTextField txtAlpha23;
    private javax.swing.JTextField txtAlpha24;
    private javax.swing.JTextField txtAlpha25;
    private javax.swing.JTextField txtAlpha26;
    private javax.swing.JTextField txtAlpha27;
    private javax.swing.JTextField txtAlpha28;
    private javax.swing.JTextField txtAlpha29;
    private javax.swing.JTextField txtAlpha3;
    private javax.swing.JTextField txtAlpha30;
    private javax.swing.JTextField txtAlpha31;
    private javax.swing.JTextField txtAlpha32;
    private javax.swing.JTextField txtAlpha33;
    private javax.swing.JTextField txtAlpha34;
    private javax.swing.JTextField txtAlpha35;
    private javax.swing.JTextField txtAlpha36;
    private javax.swing.JTextField txtAlpha37;
    private javax.swing.JTextField txtAlpha38;
    private javax.swing.JTextField txtAlpha39;
    private javax.swing.JTextField txtAlpha4;
    private javax.swing.JTextField txtAlpha40;
    private javax.swing.JTextField txtAlpha5;
    private javax.swing.JTextField txtAlpha6;
    private javax.swing.JTextField txtAlpha7;
    private javax.swing.JTextField txtAlpha8;
    private javax.swing.JTextField txtAlpha9;
    private javax.swing.JTextField txtClq;
    private javax.swing.JTextField txtCmq;
    private javax.swing.JTextField txtCnbeta;
    private javax.swing.JTextField txtCybeta;
    // End of variables declaration//GEN-END:variables

    // method to hide/disable things on start
    private void initialiseEverythingElse() {
        jButton1.setText("Add");
        jButton2.setText("Add");
        jButton3.setText("Add");
        jButton4.setText("Add");
        jButton5.setText("Add");
        jButton9.setText("Add");
        jButton6.setText("Add");
        jButton8.setText("Add");
        jButton10.setText("Add");
        // 
        jPanel3.setVisible(false);
        jPanel4.setVisible(false);
        jPanel5.setVisible(false);
        jPanel6.setVisible(false);
        jPanel7.setVisible(false);
        jPanel10.setVisible(false);
        jPanel13.setVisible(false);
        jPanel15.setVisible(false);
        jPanel16.setVisible(false);
    }
    // method to clear all values given the panel and column number

    private void clearColumn(int panel, int column) {
        if (panel == 1) {
            if (column == 1) {
                jTextField21.setText("");
                jTextField22.setText("");
                jTextField23.setText("");
                jTextField24.setText("");
                jTextField25.setText("");
                jTextField26.setText("");
                jTextField27.setText("");
                jTextField28.setText("");
                jTextField29.setText("");
                jTextField30.setText("");
                jTextField31.setText("");
                jTextField32.setText("");
                jTextField33.setText("");
                jTextField34.setText("");
                jTextField35.setText("");
                jTextField36.setText("");
                jTextField37.setText("");
                jTextField38.setText("");
                jTextField39.setText("");
                jTextField40.setText("");
            }
            if (column == 2) {
                jTextField41.setText("");
                jTextField42.setText("");
                jTextField43.setText("");
                jTextField44.setText("");
                jTextField45.setText("");
                jTextField46.setText("");
                jTextField47.setText("");
                jTextField48.setText("");
                jTextField49.setText("");
                jTextField50.setText("");
                jTextField51.setText("");
                jTextField52.setText("");
                jTextField53.setText("");
                jTextField54.setText("");
                jTextField55.setText("");
                jTextField56.setText("");
                jTextField57.setText("");
                jTextField58.setText("");
                jTextField59.setText("");
                jTextField60.setText("");
            }
            if (column == 3) {
                jTextField61.setText("");
                jTextField62.setText("");
                jTextField63.setText("");
                jTextField64.setText("");
                jTextField65.setText("");
                jTextField66.setText("");
                jTextField67.setText("");
                jTextField68.setText("");
                jTextField69.setText("");
                jTextField70.setText("");
                jTextField71.setText("");
                jTextField72.setText("");
                jTextField73.setText("");
                jTextField74.setText("");
                jTextField75.setText("");
                jTextField76.setText("");
                jTextField77.setText("");
                jTextField78.setText("");
                jTextField79.setText("");
                jTextField80.setText("");
            }
            if (column == 4) {
                jTextField81.setText("");
                jTextField82.setText("");
                jTextField83.setText("");
                jTextField84.setText("");
                jTextField85.setText("");
                jTextField86.setText("");
                jTextField87.setText("");
                jTextField88.setText("");
                jTextField89.setText("");
                jTextField90.setText("");
                jTextField91.setText("");
                jTextField92.setText("");
                jTextField93.setText("");
                jTextField94.setText("");
                jTextField95.setText("");
                jTextField96.setText("");
                jTextField97.setText("");
                jTextField98.setText("");
                jTextField99.setText("");
                jTextField100.setText("");
            }
            if (column == 5) {
                jTextField101.setText("");
                jTextField102.setText("");
                jTextField103.setText("");
                jTextField104.setText("");
                jTextField105.setText("");
                jTextField106.setText("");
                jTextField107.setText("");
                jTextField108.setText("");
                jTextField109.setText("");
                jTextField110.setText("");
                jTextField111.setText("");
                jTextField112.setText("");
                jTextField113.setText("");
                jTextField114.setText("");
                jTextField115.setText("");
                jTextField116.setText("");
                jTextField117.setText("");
                jTextField118.setText("");
                jTextField119.setText("");
                jTextField120.setText("");
            }
            if (column == 6) {
                jTextField121.setText("");
                jTextField122.setText("");
                jTextField123.setText("");
                jTextField124.setText("");
                jTextField125.setText("");
                jTextField126.setText("");
                jTextField127.setText("");
                jTextField128.setText("");
                jTextField129.setText("");
                jTextField130.setText("");
                jTextField131.setText("");
                jTextField132.setText("");
                jTextField133.setText("");
                jTextField134.setText("");
                jTextField135.setText("");
                jTextField136.setText("");
                jTextField137.setText("");
                jTextField138.setText("");
                jTextField139.setText("");
                jTextField140.setText("");
            }
        } else if (panel == 2) {
            if (column == 1) {
                jTextField201.setText("");
                jTextField202.setText("");
                jTextField203.setText("");
                jTextField204.setText("");
                jTextField205.setText("");
                jTextField206.setText("");
                jTextField207.setText("");
                jTextField208.setText("");
                jTextField209.setText("");
                jTextField210.setText("");
                jTextField211.setText("");
                jTextField212.setText("");
                jTextField213.setText("");
                jTextField214.setText("");
                jTextField215.setText("");
                jTextField216.setText("");
                jTextField217.setText("");
                jTextField218.setText("");
                jTextField219.setText("");
                jTextField220.setText("");
            }
            if (column == 2) {
                jTextField181.setText("");
                jTextField182.setText("");
                jTextField183.setText("");
                jTextField184.setText("");
                jTextField185.setText("");
                jTextField186.setText("");
                jTextField187.setText("");
                jTextField188.setText("");
                jTextField189.setText("");
                jTextField190.setText("");
                jTextField191.setText("");
                jTextField192.setText("");
                jTextField193.setText("");
                jTextField194.setText("");
                jTextField195.setText("");
                jTextField196.setText("");
                jTextField197.setText("");
                jTextField198.setText("");
                jTextField199.setText("");
                jTextField200.setText("");
            }
            if (column == 3) {
                jTextField221.setText("");
                jTextField222.setText("");
                jTextField223.setText("");
                jTextField224.setText("");
                jTextField225.setText("");
                jTextField226.setText("");
                jTextField227.setText("");
                jTextField228.setText("");
                jTextField229.setText("");
                jTextField230.setText("");
                jTextField231.setText("");
                jTextField232.setText("");
                jTextField233.setText("");
                jTextField234.setText("");
                jTextField235.setText("");
                jTextField236.setText("");
                jTextField237.setText("");
                jTextField238.setText("");
                jTextField239.setText("");
                jTextField240.setText("");
            }
            if (column == 4) {
                jTextField241.setText("");
                jTextField242.setText("");
                jTextField243.setText("");
                jTextField244.setText("");
                jTextField245.setText("");
                jTextField246.setText("");
                jTextField247.setText("");
                jTextField248.setText("");
                jTextField249.setText("");
                jTextField250.setText("");
                jTextField251.setText("");
                jTextField252.setText("");
                jTextField253.setText("");
                jTextField254.setText("");
                jTextField255.setText("");
                jTextField256.setText("");
                jTextField257.setText("");
                jTextField258.setText("");
                jTextField259.setText("");
                jTextField260.setText("");
            }
            if (column == 5) {
                jTextField141.setText("");
                jTextField142.setText("");
                jTextField143.setText("");
                jTextField144.setText("");
                jTextField145.setText("");
                jTextField146.setText("");
                jTextField147.setText("");
                jTextField148.setText("");
                jTextField149.setText("");
                jTextField150.setText("");
                jTextField151.setText("");
                jTextField152.setText("");
                jTextField153.setText("");
                jTextField154.setText("");
                jTextField155.setText("");
                jTextField156.setText("");
                jTextField157.setText("");
                jTextField158.setText("");
                jTextField159.setText("");
                jTextField160.setText("");
            }
        }
    }
    
    private void showClbeta(int column) {
        if (column == 1) {
            jTextField21.setText(clbeta.get(1).toString());
            jTextField22.setText(clbeta.get(0).toString());
            if(alphas>2){jTextField23.setText(clbeta.get(2).toString());}
            if(alphas>3){jTextField24.setText(clbeta.get(3).toString());}
            if(alphas>4){jTextField25.setText(clbeta.get(4).toString());}
            if(alphas>5){jTextField26.setText(clbeta.get(5).toString());}
            if(alphas>6){jTextField27.setText(clbeta.get(6).toString());}
            if(alphas>7){jTextField28.setText(clbeta.get(7).toString());}
            if(alphas>8){jTextField29.setText(clbeta.get(8).toString());}
            if(alphas>9){jTextField30.setText(clbeta.get(9).toString());}
            if(alphas>10){jTextField31.setText(clbeta.get(10).toString());}
            if(alphas>11){jTextField32.setText(clbeta.get(11).toString());}
            if(alphas>12){jTextField33.setText(clbeta.get(12).toString());}
            if(alphas>13){jTextField34.setText(clbeta.get(13).toString());}
            if(alphas>14){jTextField35.setText(clbeta.get(14).toString());}
            if(alphas>15){jTextField36.setText(clbeta.get(15).toString());}
            if(alphas>16){jTextField37.setText(clbeta.get(16).toString());}
            if(alphas>17){jTextField38.setText(clbeta.get(17).toString());}
            if(alphas>18){jTextField39.setText(clbeta.get(18).toString());}
            if(alphas>19){jTextField40.setText(clbeta.get(19).toString());}
        } else if (column == 2) {
            jTextField41.setText(clbeta.get(1).toString());
            jTextField42.setText(clbeta.get(0).toString());
            if(alphas>2){jTextField43.setText(clbeta.get(2).toString());}
            if(alphas>3){jTextField44.setText(clbeta.get(3).toString());}
            if(alphas>4){jTextField45.setText(clbeta.get(4).toString());}
            if(alphas>5){jTextField46.setText(clbeta.get(5).toString());}
            if(alphas>6){jTextField47.setText(clbeta.get(6).toString());}
            if(alphas>7){jTextField48.setText(clbeta.get(7).toString());}
            if(alphas>8){jTextField49.setText(clbeta.get(8).toString());}
            if(alphas>9){jTextField50.setText(clbeta.get(9).toString());}
            if(alphas>10){jTextField51.setText(clbeta.get(10).toString());}
            if(alphas>11){jTextField52.setText(clbeta.get(11).toString());}
            if(alphas>12){jTextField53.setText(clbeta.get(12).toString());}
            if(alphas>13){jTextField54.setText(clbeta.get(13).toString());}
            if(alphas>14){jTextField55.setText(clbeta.get(14).toString());}
            if(alphas>15){jTextField56.setText(clbeta.get(15).toString());}
            if(alphas>16){jTextField57.setText(clbeta.get(16).toString());}
            if(alphas>17){jTextField58.setText(clbeta.get(17).toString());}
            if(alphas>18){jTextField59.setText(clbeta.get(18).toString());}
            if(alphas>19){jTextField60.setText(clbeta.get(19).toString());}
        } else if (column == 3) {
            jTextField61.setText(clbeta.get(1).toString());
            jTextField62.setText(clbeta.get(0).toString());
            if(alphas>2){jTextField63.setText(clbeta.get(2).toString());}
            if(alphas>3){jTextField64.setText(clbeta.get(3).toString());}
            if(alphas>4){jTextField65.setText(clbeta.get(4).toString());}
            if(alphas>5){jTextField66.setText(clbeta.get(5).toString());}
            if(alphas>6){jTextField67.setText(clbeta.get(6).toString());}
            if(alphas>7){jTextField68.setText(clbeta.get(7).toString());}
            if(alphas>8){jTextField69.setText(clbeta.get(8).toString());}
            if(alphas>9){jTextField70.setText(clbeta.get(9).toString());}
            if(alphas>10){jTextField71.setText(clbeta.get(10).toString());}
            if(alphas>11){jTextField72.setText(clbeta.get(11).toString());}
            if(alphas>12){jTextField73.setText(clbeta.get(12).toString());}
            if(alphas>13){jTextField74.setText(clbeta.get(13).toString());}
            if(alphas>14){jTextField75.setText(clbeta.get(14).toString());}
            if(alphas>15){jTextField76.setText(clbeta.get(15).toString());}
            if(alphas>16){jTextField77.setText(clbeta.get(16).toString());}
            if(alphas>17){jTextField78.setText(clbeta.get(17).toString());}
            if(alphas>18){jTextField79.setText(clbeta.get(18).toString());}
            if(alphas>19){jTextField80.setText(clbeta.get(19).toString());}
        } else if (column == 4) {
            jTextField81.setText(clbeta.get(1).toString());
            jTextField82.setText(clbeta.get(0).toString());
            if(alphas>2){jTextField83.setText(clbeta.get(2).toString());}
            if(alphas>3){jTextField84.setText(clbeta.get(3).toString());}
            if(alphas>4){jTextField85.setText(clbeta.get(4).toString());}
            if(alphas>5){jTextField86.setText(clbeta.get(5).toString());}
            if(alphas>6){jTextField87.setText(clbeta.get(6).toString());}
            if(alphas>7){jTextField88.setText(clbeta.get(7).toString());}
            if(alphas>8){jTextField89.setText(clbeta.get(8).toString());}
            if(alphas>9){jTextField90.setText(clbeta.get(9).toString());}
            if(alphas>10){jTextField91.setText(clbeta.get(10).toString());}
            if(alphas>11){jTextField92.setText(clbeta.get(11).toString());}
            if(alphas>12){jTextField93.setText(clbeta.get(12).toString());}
            if(alphas>13){jTextField94.setText(clbeta.get(13).toString());}
            if(alphas>14){jTextField95.setText(clbeta.get(14).toString());}
            if(alphas>15){jTextField96.setText(clbeta.get(15).toString());}
            if(alphas>16){jTextField97.setText(clbeta.get(16).toString());}
            if(alphas>17){jTextField98.setText(clbeta.get(17).toString());}
            if(alphas>18){jTextField99.setText(clbeta.get(18).toString());}
            if(alphas>19){jTextField100.setText(clbeta.get(19).toString());}
        } else if (column == 5) {
            jTextField101.setText(clbeta.get(1).toString());
            jTextField102.setText(clbeta.get(0).toString());
            if(alphas>2){jTextField103.setText(clbeta.get(2).toString());}
            if(alphas>3){jTextField104.setText(clbeta.get(3).toString());}
            if(alphas>4){jTextField105.setText(clbeta.get(4).toString());}
            if(alphas>5){jTextField106.setText(clbeta.get(5).toString());}
            if(alphas>6){jTextField107.setText(clbeta.get(6).toString());}
            if(alphas>7){jTextField108.setText(clbeta.get(7).toString());}
            if(alphas>8){jTextField109.setText(clbeta.get(8).toString());}
            if(alphas>9){jTextField110.setText(clbeta.get(9).toString());}
            if(alphas>10){jTextField111.setText(clbeta.get(10).toString());}
            if(alphas>11){jTextField112.setText(clbeta.get(11).toString());}
            if(alphas>12){jTextField113.setText(clbeta.get(12).toString());}
            if(alphas>13){jTextField114.setText(clbeta.get(13).toString());}
            if(alphas>14){jTextField115.setText(clbeta.get(14).toString());}
            if(alphas>15){jTextField116.setText(clbeta.get(15).toString());}
            if(alphas>16){jTextField117.setText(clbeta.get(16).toString());}
            if(alphas>17){jTextField118.setText(clbeta.get(17).toString());}
            if(alphas>18){jTextField119.setText(clbeta.get(18).toString());}
            if(alphas>19){jTextField120.setText(clbeta.get(19).toString());}
        } else if (column == 6) {
            jTextField121.setText(clbeta.get(1).toString());
            jTextField122.setText(clbeta.get(0).toString());
            if(alphas>2){jTextField123.setText(clbeta.get(2).toString());}
            if(alphas>3){jTextField124.setText(clbeta.get(3).toString());}
            if(alphas>4){jTextField125.setText(clbeta.get(4).toString());}
            if(alphas>5){jTextField126.setText(clbeta.get(5).toString());}
            if(alphas>6){jTextField127.setText(clbeta.get(6).toString());}
            if(alphas>7){jTextField128.setText(clbeta.get(7).toString());}
            if(alphas>8){jTextField129.setText(clbeta.get(8).toString());}
            if(alphas>9){jTextField130.setText(clbeta.get(9).toString());}
            if(alphas>10){jTextField131.setText(clbeta.get(10).toString());}
            if(alphas>11){jTextField132.setText(clbeta.get(11).toString());}
            if(alphas>12){jTextField133.setText(clbeta.get(12).toString());}
            if(alphas>13){jTextField134.setText(clbeta.get(13).toString());}
            if(alphas>14){jTextField135.setText(clbeta.get(14).toString());}
            if(alphas>15){jTextField136.setText(clbeta.get(15).toString());}
            if(alphas>16){jTextField137.setText(clbeta.get(16).toString());}
            if(alphas>17){jTextField138.setText(clbeta.get(17).toString());}
            if(alphas>18){jTextField139.setText(clbeta.get(18).toString());}
            if(alphas>19){jTextField140.setText(clbeta.get(19).toString());}
        }

    }
    
    private void showClp(int column) {
        if (column == 1) {
            jTextField21.setText(clp.get(1).toString());
            jTextField22.setText(clp.get(0).toString());
            if(alphas>2){jTextField23.setText(clp.get(2).toString());}
            if(alphas>3){jTextField24.setText(clp.get(3).toString());}
            if(alphas>4){jTextField25.setText(clp.get(4).toString());}
            if(alphas>5){jTextField26.setText(clp.get(5).toString());}
            if(alphas>6){jTextField27.setText(clp.get(6).toString());}
            if(alphas>7){jTextField28.setText(clp.get(7).toString());}
            if(alphas>8){jTextField29.setText(clp.get(8).toString());}
            if(alphas>9){jTextField30.setText(clp.get(9).toString());}
            if(alphas>10){jTextField31.setText(clp.get(10).toString());}
            if(alphas>11){jTextField32.setText(clp.get(11).toString());}
            if(alphas>12){jTextField33.setText(clp.get(12).toString());}
            if(alphas>13){jTextField34.setText(clp.get(13).toString());}
            if(alphas>14){jTextField35.setText(clp.get(14).toString());}
            if(alphas>15){jTextField36.setText(clp.get(15).toString());}
            if(alphas>16){jTextField37.setText(clp.get(16).toString());}
            if(alphas>17){jTextField38.setText(clp.get(17).toString());}
            if(alphas>18){jTextField39.setText(clp.get(18).toString());}
            if(alphas>19){jTextField40.setText(clp.get(19).toString());}
        } else if (column == 2) {
            jTextField41.setText(clp.get(1).toString());
            jTextField42.setText(clp.get(0).toString());
            if(alphas>2){jTextField43.setText(clp.get(2).toString());}
            if(alphas>3){jTextField44.setText(clp.get(3).toString());}
            if(alphas>4){jTextField45.setText(clp.get(4).toString());}
            if(alphas>5){jTextField46.setText(clp.get(5).toString());}
            if(alphas>6){jTextField47.setText(clp.get(6).toString());}
            if(alphas>7){jTextField48.setText(clp.get(7).toString());}
            if(alphas>8){jTextField49.setText(clp.get(8).toString());}
            if(alphas>9){jTextField50.setText(clp.get(9).toString());}
            if(alphas>10){jTextField51.setText(clp.get(10).toString());}
            if(alphas>11){jTextField52.setText(clp.get(11).toString());}
            if(alphas>12){jTextField53.setText(clp.get(12).toString());}
            if(alphas>13){jTextField54.setText(clp.get(13).toString());}
            if(alphas>14){jTextField55.setText(clp.get(14).toString());}
            if(alphas>15){jTextField56.setText(clp.get(15).toString());}
            if(alphas>16){jTextField57.setText(clp.get(16).toString());}
            if(alphas>17){jTextField58.setText(clp.get(17).toString());}
            if(alphas>18){jTextField59.setText(clp.get(18).toString());}
            if(alphas>19){jTextField60.setText(clp.get(19).toString());}
        } else if (column == 3) {
            jTextField61.setText(clp.get(1).toString());
            jTextField62.setText(clp.get(0).toString());
            if(alphas>2){jTextField63.setText(clp.get(2).toString());}
            if(alphas>3){jTextField64.setText(clp.get(3).toString());}
            if(alphas>4){jTextField65.setText(clp.get(4).toString());}
            if(alphas>5){jTextField66.setText(clp.get(5).toString());}
            if(alphas>6){jTextField67.setText(clp.get(6).toString());}
            if(alphas>7){jTextField68.setText(clp.get(7).toString());}
            if(alphas>8){jTextField69.setText(clp.get(8).toString());}
            if(alphas>9){jTextField70.setText(clp.get(9).toString());}
            if(alphas>10){jTextField71.setText(clp.get(10).toString());}
            if(alphas>11){jTextField72.setText(clp.get(11).toString());}
            if(alphas>12){jTextField73.setText(clp.get(12).toString());}
            if(alphas>13){jTextField74.setText(clp.get(13).toString());}
            if(alphas>14){jTextField75.setText(clp.get(14).toString());}
            if(alphas>15){jTextField76.setText(clp.get(15).toString());}
            if(alphas>16){jTextField77.setText(clp.get(16).toString());}
            if(alphas>17){jTextField78.setText(clp.get(17).toString());}
            if(alphas>18){jTextField79.setText(clp.get(18).toString());}
            if(alphas>19){jTextField80.setText(clp.get(19).toString());}
        } else if (column == 4) {
            jTextField81.setText(clp.get(1).toString());
            jTextField82.setText(clp.get(0).toString());
            if(alphas>2){jTextField83.setText(clp.get(2).toString());}
            if(alphas>3){jTextField84.setText(clp.get(3).toString());}
            if(alphas>4){jTextField85.setText(clp.get(4).toString());}
            if(alphas>5){jTextField86.setText(clp.get(5).toString());}
            if(alphas>6){jTextField87.setText(clp.get(6).toString());}
            if(alphas>7){jTextField88.setText(clp.get(7).toString());}
            if(alphas>8){jTextField89.setText(clp.get(8).toString());}
            if(alphas>9){jTextField90.setText(clp.get(9).toString());}
            if(alphas>10){jTextField91.setText(clp.get(10).toString());}
            if(alphas>11){jTextField92.setText(clp.get(11).toString());}
            if(alphas>12){jTextField93.setText(clp.get(12).toString());}
            if(alphas>13){jTextField94.setText(clp.get(13).toString());}
            if(alphas>14){jTextField95.setText(clp.get(14).toString());}
            if(alphas>15){jTextField96.setText(clp.get(15).toString());}
            if(alphas>16){jTextField97.setText(clp.get(16).toString());}
            if(alphas>17){jTextField98.setText(clp.get(17).toString());}
            if(alphas>18){jTextField99.setText(clp.get(18).toString());}
            if(alphas>19){jTextField100.setText(clp.get(19).toString());}
        } else if (column == 5) {
            jTextField101.setText(clp.get(1).toString());
            jTextField102.setText(clp.get(0).toString());
            if(alphas>2){jTextField103.setText(clp.get(2).toString());}
            if(alphas>3){jTextField104.setText(clp.get(3).toString());}
            if(alphas>4){jTextField105.setText(clp.get(4).toString());}
            if(alphas>5){jTextField106.setText(clp.get(5).toString());}
            if(alphas>6){jTextField107.setText(clp.get(6).toString());}
            if(alphas>7){jTextField108.setText(clp.get(7).toString());}
            if(alphas>8){jTextField109.setText(clp.get(8).toString());}
            if(alphas>9){jTextField110.setText(clp.get(9).toString());}
            if(alphas>10){jTextField111.setText(clp.get(10).toString());}
            if(alphas>11){jTextField112.setText(clp.get(11).toString());}
            if(alphas>12){jTextField113.setText(clp.get(12).toString());}
            if(alphas>13){jTextField114.setText(clp.get(13).toString());}
            if(alphas>14){jTextField115.setText(clp.get(14).toString());}
            if(alphas>15){jTextField116.setText(clp.get(15).toString());}
            if(alphas>16){jTextField117.setText(clp.get(16).toString());}
            if(alphas>17){jTextField118.setText(clp.get(17).toString());}
            if(alphas>18){jTextField119.setText(clp.get(18).toString());}
            if(alphas>19){jTextField120.setText(clp.get(19).toString());}
        } else if (column == 6) {
            jTextField121.setText(clp.get(1).toString());
            jTextField122.setText(clp.get(0).toString());
            if(alphas>2){jTextField123.setText(clp.get(2).toString());}
            if(alphas>3){jTextField124.setText(clp.get(3).toString());}
            if(alphas>4){jTextField125.setText(clp.get(4).toString());}
            if(alphas>5){jTextField126.setText(clp.get(5).toString());}
            if(alphas>6){jTextField127.setText(clp.get(6).toString());}
            if(alphas>7){jTextField128.setText(clp.get(7).toString());}
            if(alphas>8){jTextField129.setText(clp.get(8).toString());}
            if(alphas>9){jTextField130.setText(clp.get(9).toString());}
            if(alphas>10){jTextField131.setText(clp.get(10).toString());}
            if(alphas>11){jTextField132.setText(clp.get(11).toString());}
            if(alphas>12){jTextField133.setText(clp.get(12).toString());}
            if(alphas>13){jTextField134.setText(clp.get(13).toString());}
            if(alphas>14){jTextField135.setText(clp.get(14).toString());}
            if(alphas>15){jTextField136.setText(clp.get(15).toString());}
            if(alphas>16){jTextField137.setText(clp.get(16).toString());}
            if(alphas>17){jTextField138.setText(clp.get(17).toString());}
            if(alphas>18){jTextField139.setText(clp.get(18).toString());}
            if(alphas>19){jTextField140.setText(clp.get(19).toString());}
        }
    }
    
    private void showCyp(int column) {
        if (column == 1) {
            jTextField21.setText(cyp.get(1).toString());
            jTextField22.setText(cyp.get(0).toString());
            if(alphas>2){jTextField23.setText(cyp.get(2).toString());}
            if(alphas>3){jTextField24.setText(cyp.get(3).toString());}
            if(alphas>4){jTextField25.setText(cyp.get(4).toString());}
            if(alphas>5){jTextField26.setText(cyp.get(5).toString());}
            if(alphas>6){jTextField27.setText(cyp.get(6).toString());}
            if(alphas>7){jTextField28.setText(cyp.get(7).toString());}
            if(alphas>8){jTextField29.setText(cyp.get(8).toString());}
            if(alphas>9){jTextField30.setText(cyp.get(9).toString());}
            if(alphas>10){jTextField31.setText(cyp.get(10).toString());}
            if(alphas>11){jTextField32.setText(cyp.get(11).toString());}
            if(alphas>12){jTextField33.setText(cyp.get(12).toString());}
            if(alphas>13){jTextField34.setText(cyp.get(13).toString());}
            if(alphas>14){jTextField35.setText(cyp.get(14).toString());}
            if(alphas>15){jTextField36.setText(cyp.get(15).toString());}
            if(alphas>16){jTextField37.setText(cyp.get(16).toString());}
            if(alphas>17){jTextField38.setText(cyp.get(17).toString());}
            if(alphas>18){jTextField39.setText(cyp.get(18).toString());}
            if(alphas>19){jTextField40.setText(cyp.get(19).toString());}
        } else if (column == 2) {
            jTextField41.setText(cyp.get(1).toString());
            jTextField42.setText(cyp.get(0).toString());
            if(alphas>2){jTextField43.setText(cyp.get(2).toString());}
            if(alphas>3){jTextField44.setText(cyp.get(3).toString());}
            if(alphas>4){jTextField45.setText(cyp.get(4).toString());}
            if(alphas>5){jTextField46.setText(cyp.get(5).toString());}
            if(alphas>6){jTextField47.setText(cyp.get(6).toString());}
            if(alphas>7){jTextField48.setText(cyp.get(7).toString());}
            if(alphas>8){jTextField49.setText(cyp.get(8).toString());}
            if(alphas>9){jTextField50.setText(cyp.get(9).toString());}
            if(alphas>10){jTextField51.setText(cyp.get(10).toString());}
            if(alphas>11){jTextField52.setText(cyp.get(11).toString());}
            if(alphas>12){jTextField53.setText(cyp.get(12).toString());}
            if(alphas>13){jTextField54.setText(cyp.get(13).toString());}
            if(alphas>14){jTextField55.setText(cyp.get(14).toString());}
            if(alphas>15){jTextField56.setText(cyp.get(15).toString());}
            if(alphas>16){jTextField57.setText(cyp.get(16).toString());}
            if(alphas>17){jTextField58.setText(cyp.get(17).toString());}
            if(alphas>18){jTextField59.setText(cyp.get(18).toString());}
            if(alphas>19){jTextField60.setText(cyp.get(19).toString());}
        } else if (column == 3) {
            jTextField61.setText(cyp.get(1).toString());
            jTextField62.setText(cyp.get(0).toString());
            if(alphas>2){jTextField63.setText(cyp.get(2).toString());}
            if(alphas>3){jTextField64.setText(cyp.get(3).toString());}
            if(alphas>4){jTextField65.setText(cyp.get(4).toString());}
            if(alphas>5){jTextField66.setText(cyp.get(5).toString());}
            if(alphas>6){jTextField67.setText(cyp.get(6).toString());}
            if(alphas>7){jTextField68.setText(cyp.get(7).toString());}
            if(alphas>8){jTextField69.setText(cyp.get(8).toString());}
            if(alphas>9){jTextField70.setText(cyp.get(9).toString());}
            if(alphas>10){jTextField71.setText(cyp.get(10).toString());}
            if(alphas>11){jTextField72.setText(cyp.get(11).toString());}
            if(alphas>12){jTextField73.setText(cyp.get(12).toString());}
            if(alphas>13){jTextField74.setText(cyp.get(13).toString());}
            if(alphas>14){jTextField75.setText(cyp.get(14).toString());}
            if(alphas>15){jTextField76.setText(cyp.get(15).toString());}
            if(alphas>16){jTextField77.setText(cyp.get(16).toString());}
            if(alphas>17){jTextField78.setText(cyp.get(17).toString());}
            if(alphas>18){jTextField79.setText(cyp.get(18).toString());}
            if(alphas>19){jTextField80.setText(cyp.get(19).toString());}
        } else if (column == 4) {
            jTextField81.setText(cyp.get(1).toString());
            jTextField82.setText(cyp.get(0).toString());
            if(alphas>2){jTextField83.setText(cyp.get(2).toString());}
            if(alphas>3){jTextField84.setText(cyp.get(3).toString());}
            if(alphas>4){jTextField85.setText(cyp.get(4).toString());}
            if(alphas>5){jTextField86.setText(cyp.get(5).toString());}
            if(alphas>6){jTextField87.setText(cyp.get(6).toString());}
            if(alphas>7){jTextField88.setText(cyp.get(7).toString());}
            if(alphas>8){jTextField89.setText(cyp.get(8).toString());}
            if(alphas>9){jTextField90.setText(cyp.get(9).toString());}
            if(alphas>10){jTextField91.setText(cyp.get(10).toString());}
            if(alphas>11){jTextField92.setText(cyp.get(11).toString());}
            if(alphas>12){jTextField93.setText(cyp.get(12).toString());}
            if(alphas>13){jTextField94.setText(cyp.get(13).toString());}
            if(alphas>14){jTextField95.setText(cyp.get(14).toString());}
            if(alphas>15){jTextField96.setText(cyp.get(15).toString());}
            if(alphas>16){jTextField97.setText(cyp.get(16).toString());}
            if(alphas>17){jTextField98.setText(cyp.get(17).toString());}
            if(alphas>18){jTextField99.setText(cyp.get(18).toString());}
            if(alphas>19){jTextField100.setText(cyp.get(19).toString());}
        } else if (column == 5) {
            jTextField101.setText(cyp.get(1).toString());
            jTextField102.setText(cyp.get(0).toString());
            if(alphas>2){jTextField103.setText(cyp.get(2).toString());}
            if(alphas>3){jTextField104.setText(cyp.get(3).toString());}
            if(alphas>4){jTextField105.setText(cyp.get(4).toString());}
            if(alphas>5){jTextField106.setText(cyp.get(5).toString());}
            if(alphas>6){jTextField107.setText(cyp.get(6).toString());}
            if(alphas>7){jTextField108.setText(cyp.get(7).toString());}
            if(alphas>8){jTextField109.setText(cyp.get(8).toString());}
            if(alphas>9){jTextField110.setText(cyp.get(9).toString());}
            if(alphas>10){jTextField111.setText(cyp.get(10).toString());}
            if(alphas>11){jTextField112.setText(cyp.get(11).toString());}
            if(alphas>12){jTextField113.setText(cyp.get(12).toString());}
            if(alphas>13){jTextField114.setText(cyp.get(13).toString());}
            if(alphas>14){jTextField115.setText(cyp.get(14).toString());}
            if(alphas>15){jTextField116.setText(cyp.get(15).toString());}
            if(alphas>16){jTextField117.setText(cyp.get(16).toString());}
            if(alphas>17){jTextField118.setText(cyp.get(17).toString());}
            if(alphas>18){jTextField119.setText(cyp.get(18).toString());}
            if(alphas>19){jTextField120.setText(cyp.get(19).toString());}
        } else if (column == 6) {
            jTextField121.setText(cyp.get(1).toString());
            jTextField122.setText(cyp.get(0).toString());
            if(alphas>2){jTextField123.setText(cyp.get(2).toString());}
            if(alphas>3){jTextField124.setText(cyp.get(3).toString());}
            if(alphas>4){jTextField125.setText(cyp.get(4).toString());}
            if(alphas>5){jTextField126.setText(cyp.get(5).toString());}
            if(alphas>6){jTextField127.setText(cyp.get(6).toString());}
            if(alphas>7){jTextField128.setText(cyp.get(7).toString());}
            if(alphas>8){jTextField129.setText(cyp.get(8).toString());}
            if(alphas>9){jTextField130.setText(cyp.get(9).toString());}
            if(alphas>10){jTextField131.setText(cyp.get(10).toString());}
            if(alphas>11){jTextField132.setText(cyp.get(11).toString());}
            if(alphas>12){jTextField133.setText(cyp.get(12).toString());}
            if(alphas>13){jTextField134.setText(cyp.get(13).toString());}
            if(alphas>14){jTextField135.setText(cyp.get(14).toString());}
            if(alphas>15){jTextField136.setText(cyp.get(15).toString());}
            if(alphas>16){jTextField137.setText(cyp.get(16).toString());}
            if(alphas>17){jTextField138.setText(cyp.get(17).toString());}
            if(alphas>18){jTextField139.setText(cyp.get(18).toString());}
            if(alphas>19){jTextField140.setText(cyp.get(19).toString());}
        }
    }
    
    private void showCnp(int column) {
        if (column == 1) {
            jTextField21.setText(cnp.get(1).toString());
            jTextField22.setText(cnp.get(0).toString());
            if(alphas>2){jTextField23.setText(cnp.get(2).toString());}
            if(alphas>3){jTextField24.setText(cnp.get(3).toString());}
            if(alphas>4){jTextField25.setText(cnp.get(4).toString());}
            if(alphas>5){jTextField26.setText(cnp.get(5).toString());}
            if(alphas>6){jTextField27.setText(cnp.get(6).toString());}
            if(alphas>7){jTextField28.setText(cnp.get(7).toString());}
            if(alphas>8){jTextField29.setText(cnp.get(8).toString());}
            if(alphas>9){jTextField30.setText(cnp.get(9).toString());}
            if(alphas>10){jTextField31.setText(cnp.get(10).toString());}
            if(alphas>11){jTextField32.setText(cnp.get(11).toString());}
            if(alphas>12){jTextField33.setText(cnp.get(12).toString());}
            if(alphas>13){jTextField34.setText(cnp.get(13).toString());}
            if(alphas>14){jTextField35.setText(cnp.get(14).toString());}
            if(alphas>15){jTextField36.setText(cnp.get(15).toString());}
            if(alphas>16){jTextField37.setText(cnp.get(16).toString());}
            if(alphas>17){jTextField38.setText(cnp.get(17).toString());}
            if(alphas>18){jTextField39.setText(cnp.get(18).toString());}
            if(alphas>19){jTextField40.setText(cnp.get(19).toString());}
        } else if (column == 2) {
            jTextField41.setText(cnp.get(1).toString());
            jTextField42.setText(cnp.get(0).toString());
            if(alphas>2){jTextField43.setText(cnp.get(2).toString());}
            if(alphas>3){jTextField44.setText(cnp.get(3).toString());}
            if(alphas>4){jTextField45.setText(cnp.get(4).toString());}
            if(alphas>5){jTextField46.setText(cnp.get(5).toString());}
            if(alphas>6){jTextField47.setText(cnp.get(6).toString());}
            if(alphas>7){jTextField48.setText(cnp.get(7).toString());}
            if(alphas>8){jTextField49.setText(cnp.get(8).toString());}
            if(alphas>9){jTextField50.setText(cnp.get(9).toString());}
            if(alphas>10){jTextField51.setText(cnp.get(10).toString());}
            if(alphas>11){jTextField52.setText(cnp.get(11).toString());}
            if(alphas>12){jTextField53.setText(cnp.get(12).toString());}
            if(alphas>13){jTextField54.setText(cnp.get(13).toString());}
            if(alphas>14){jTextField55.setText(cnp.get(14).toString());}
            if(alphas>15){jTextField56.setText(cnp.get(15).toString());}
            if(alphas>16){jTextField57.setText(cnp.get(16).toString());}
            if(alphas>17){jTextField58.setText(cnp.get(17).toString());}
            if(alphas>18){jTextField59.setText(cnp.get(18).toString());}
            if(alphas>19){jTextField60.setText(cnp.get(19).toString());}
        } else if (column == 3) {
            jTextField61.setText(cnp.get(1).toString());
            jTextField62.setText(cnp.get(0).toString());
            if(alphas>2){jTextField63.setText(cnp.get(2).toString());}
            if(alphas>3){jTextField64.setText(cnp.get(3).toString());}
            if(alphas>4){jTextField65.setText(cnp.get(4).toString());}
            if(alphas>5){jTextField66.setText(cnp.get(5).toString());}
            if(alphas>6){jTextField67.setText(cnp.get(6).toString());}
            if(alphas>7){jTextField68.setText(cnp.get(7).toString());}
            if(alphas>8){jTextField69.setText(cnp.get(8).toString());}
            if(alphas>9){jTextField70.setText(cnp.get(9).toString());}
            if(alphas>10){jTextField71.setText(cnp.get(10).toString());}
            if(alphas>11){jTextField72.setText(cnp.get(11).toString());}
            if(alphas>12){jTextField73.setText(cnp.get(12).toString());}
            if(alphas>13){jTextField74.setText(cnp.get(13).toString());}
            if(alphas>14){jTextField75.setText(cnp.get(14).toString());}
            if(alphas>15){jTextField76.setText(cnp.get(15).toString());}
            if(alphas>16){jTextField77.setText(cnp.get(16).toString());}
            if(alphas>17){jTextField78.setText(cnp.get(17).toString());}
            if(alphas>18){jTextField79.setText(cnp.get(18).toString());}
            if(alphas>19){jTextField80.setText(cnp.get(19).toString());}
        } else if (column == 4) {
            jTextField81.setText(cnp.get(1).toString());
            jTextField82.setText(cnp.get(0).toString());
            if(alphas>2){jTextField83.setText(cnp.get(2).toString());}
            if(alphas>3){jTextField84.setText(cnp.get(3).toString());}
            if(alphas>4){jTextField85.setText(cnp.get(4).toString());}
            if(alphas>5){jTextField86.setText(cnp.get(5).toString());}
            if(alphas>6){jTextField87.setText(cnp.get(6).toString());}
            if(alphas>7){jTextField88.setText(cnp.get(7).toString());}
            if(alphas>8){jTextField89.setText(cnp.get(8).toString());}
            if(alphas>9){jTextField90.setText(cnp.get(9).toString());}
            if(alphas>10){jTextField91.setText(cnp.get(10).toString());}
            if(alphas>11){jTextField92.setText(cnp.get(11).toString());}
            if(alphas>12){jTextField93.setText(cnp.get(12).toString());}
            if(alphas>13){jTextField94.setText(cnp.get(13).toString());}
            if(alphas>14){jTextField95.setText(cnp.get(14).toString());}
            if(alphas>15){jTextField96.setText(cnp.get(15).toString());}
            if(alphas>16){jTextField97.setText(cnp.get(16).toString());}
            if(alphas>17){jTextField98.setText(cnp.get(17).toString());}
            if(alphas>18){jTextField99.setText(cnp.get(18).toString());}
            if(alphas>19){jTextField100.setText(cnp.get(19).toString());}
        } else if (column == 5) {
            jTextField101.setText(cnp.get(1).toString());
            jTextField102.setText(cnp.get(0).toString());
            if(alphas>2){jTextField103.setText(cnp.get(2).toString());}
            if(alphas>3){jTextField104.setText(cnp.get(3).toString());}
            if(alphas>4){jTextField105.setText(cnp.get(4).toString());}
            if(alphas>5){jTextField106.setText(cnp.get(5).toString());}
            if(alphas>6){jTextField107.setText(cnp.get(6).toString());}
            if(alphas>7){jTextField108.setText(cnp.get(7).toString());}
            if(alphas>8){jTextField109.setText(cnp.get(8).toString());}
            if(alphas>9){jTextField110.setText(cnp.get(9).toString());}
            if(alphas>10){jTextField111.setText(cnp.get(10).toString());}
            if(alphas>11){jTextField112.setText(cnp.get(11).toString());}
            if(alphas>12){jTextField113.setText(cnp.get(12).toString());}
            if(alphas>13){jTextField114.setText(cnp.get(13).toString());}
            if(alphas>14){jTextField115.setText(cnp.get(14).toString());}
            if(alphas>15){jTextField116.setText(cnp.get(15).toString());}
            if(alphas>16){jTextField117.setText(cnp.get(16).toString());}
            if(alphas>17){jTextField118.setText(cnp.get(17).toString());}
            if(alphas>18){jTextField119.setText(cnp.get(18).toString());}
            if(alphas>19){jTextField120.setText(cnp.get(19).toString());}
        } else if (column == 6) {
            jTextField121.setText(cnp.get(1).toString());
            jTextField122.setText(cnp.get(0).toString());
            if(alphas>2){jTextField123.setText(cnp.get(2).toString());}
            if(alphas>3){jTextField124.setText(cnp.get(3).toString());}
            if(alphas>4){jTextField125.setText(cnp.get(4).toString());}
            if(alphas>5){jTextField126.setText(cnp.get(5).toString());}
            if(alphas>6){jTextField127.setText(cnp.get(6).toString());}
            if(alphas>7){jTextField128.setText(cnp.get(7).toString());}
            if(alphas>8){jTextField129.setText(cnp.get(8).toString());}
            if(alphas>9){jTextField130.setText(cnp.get(9).toString());}
            if(alphas>10){jTextField131.setText(cnp.get(10).toString());}
            if(alphas>11){jTextField132.setText(cnp.get(11).toString());}
            if(alphas>12){jTextField133.setText(cnp.get(12).toString());}
            if(alphas>13){jTextField134.setText(cnp.get(13).toString());}
            if(alphas>14){jTextField135.setText(cnp.get(14).toString());}
            if(alphas>15){jTextField136.setText(cnp.get(15).toString());}
            if(alphas>16){jTextField137.setText(cnp.get(16).toString());}
            if(alphas>17){jTextField138.setText(cnp.get(17).toString());}
            if(alphas>18){jTextField139.setText(cnp.get(18).toString());}
            if(alphas>19){jTextField140.setText(cnp.get(19).toString());}
        }
    }
    
    private void showCnr(int column) {
        if (column == 1) {
            jTextField21.setText(cnr.get(1).toString());
            jTextField22.setText(cnr.get(0).toString());
            if(alphas>2){jTextField23.setText(cnr.get(2).toString());}
            if(alphas>3){jTextField24.setText(cnr.get(3).toString());}
            if(alphas>4){jTextField25.setText(cnr.get(4).toString());}
            if(alphas>5){jTextField26.setText(cnr.get(5).toString());}
            if(alphas>6){jTextField27.setText(cnr.get(6).toString());}
            if(alphas>7){jTextField28.setText(cnr.get(7).toString());}
            if(alphas>8){jTextField29.setText(cnr.get(8).toString());}
            if(alphas>9){jTextField30.setText(cnr.get(9).toString());}
            if(alphas>10){jTextField31.setText(cnr.get(10).toString());}
            if(alphas>11){jTextField32.setText(cnr.get(11).toString());}
            if(alphas>12){jTextField33.setText(cnr.get(12).toString());}
            if(alphas>13){jTextField34.setText(cnr.get(13).toString());}
            if(alphas>14){jTextField35.setText(cnr.get(14).toString());}
            if(alphas>15){jTextField36.setText(cnr.get(15).toString());}
            if(alphas>16){jTextField37.setText(cnr.get(16).toString());}
            if(alphas>17){jTextField38.setText(cnr.get(17).toString());}
            if(alphas>18){jTextField39.setText(cnr.get(18).toString());}
            if(alphas>19){jTextField40.setText(cnr.get(19).toString());}
        } else if (column == 2) {
            jTextField41.setText(cnr.get(1).toString());
            jTextField42.setText(cnr.get(0).toString());
            if(alphas>2){jTextField43.setText(cnr.get(2).toString());}
            if(alphas>3){jTextField44.setText(cnr.get(3).toString());}
            if(alphas>4){jTextField45.setText(cnr.get(4).toString());}
            if(alphas>5){jTextField46.setText(cnr.get(5).toString());}
            if(alphas>6){jTextField47.setText(cnr.get(6).toString());}
            if(alphas>7){jTextField48.setText(cnr.get(7).toString());}
            if(alphas>8){jTextField49.setText(cnr.get(8).toString());}
            if(alphas>9){jTextField50.setText(cnr.get(9).toString());}
            if(alphas>10){jTextField51.setText(cnr.get(10).toString());}
            if(alphas>11){jTextField52.setText(cnr.get(11).toString());}
            if(alphas>12){jTextField53.setText(cnr.get(12).toString());}
            if(alphas>13){jTextField54.setText(cnr.get(13).toString());}
            if(alphas>14){jTextField55.setText(cnr.get(14).toString());}
            if(alphas>15){jTextField56.setText(cnr.get(15).toString());}
            if(alphas>16){jTextField57.setText(cnr.get(16).toString());}
            if(alphas>17){jTextField58.setText(cnr.get(17).toString());}
            if(alphas>18){jTextField59.setText(cnr.get(18).toString());}
            if(alphas>19){jTextField60.setText(cnr.get(19).toString());}
        } else if (column == 3) {
            jTextField61.setText(cnr.get(1).toString());
            jTextField62.setText(cnr.get(0).toString());
            if(alphas>2){jTextField63.setText(cnr.get(2).toString());}
            if(alphas>3){jTextField64.setText(cnr.get(3).toString());}
            if(alphas>4){jTextField65.setText(cnr.get(4).toString());}
            if(alphas>5){jTextField66.setText(cnr.get(5).toString());}
            if(alphas>6){jTextField67.setText(cnr.get(6).toString());}
            if(alphas>7){jTextField68.setText(cnr.get(7).toString());}
            if(alphas>8){jTextField69.setText(cnr.get(8).toString());}
            if(alphas>9){jTextField70.setText(cnr.get(9).toString());}
            if(alphas>10){jTextField71.setText(cnr.get(10).toString());}
            if(alphas>11){jTextField72.setText(cnr.get(11).toString());}
            if(alphas>12){jTextField73.setText(cnr.get(12).toString());}
            if(alphas>13){jTextField74.setText(cnr.get(13).toString());}
            if(alphas>14){jTextField75.setText(cnr.get(14).toString());}
            if(alphas>15){jTextField76.setText(cnr.get(15).toString());}
            if(alphas>16){jTextField77.setText(cnr.get(16).toString());}
            if(alphas>17){jTextField78.setText(cnr.get(17).toString());}
            if(alphas>18){jTextField79.setText(cnr.get(18).toString());}
            if(alphas>19){jTextField80.setText(cnr.get(19).toString());}
        } else if (column == 4) {
            jTextField81.setText(cnr.get(1).toString());
            jTextField82.setText(cnr.get(0).toString());
            if(alphas>2){jTextField83.setText(cnr.get(2).toString());}
            if(alphas>3){jTextField84.setText(cnr.get(3).toString());}
            if(alphas>4){jTextField85.setText(cnr.get(4).toString());}
            if(alphas>5){jTextField86.setText(cnr.get(5).toString());}
            if(alphas>6){jTextField87.setText(cnr.get(6).toString());}
            if(alphas>7){jTextField88.setText(cnr.get(7).toString());}
            if(alphas>8){jTextField89.setText(cnr.get(8).toString());}
            if(alphas>9){jTextField90.setText(cnr.get(9).toString());}
            if(alphas>10){jTextField91.setText(cnr.get(10).toString());}
            if(alphas>11){jTextField92.setText(cnr.get(11).toString());}
            if(alphas>12){jTextField93.setText(cnr.get(12).toString());}
            if(alphas>13){jTextField94.setText(cnr.get(13).toString());}
            if(alphas>14){jTextField95.setText(cnr.get(14).toString());}
            if(alphas>15){jTextField96.setText(cnr.get(15).toString());}
            if(alphas>16){jTextField97.setText(cnr.get(16).toString());}
            if(alphas>17){jTextField98.setText(cnr.get(17).toString());}
            if(alphas>18){jTextField99.setText(cnr.get(18).toString());}
            if(alphas>19){jTextField100.setText(cnr.get(19).toString());}
        } else if (column == 5) {
            jTextField101.setText(cnr.get(1).toString());
            jTextField102.setText(cnr.get(0).toString());
            if(alphas>2){jTextField103.setText(cnr.get(2).toString());}
            if(alphas>3){jTextField104.setText(cnr.get(3).toString());}
            if(alphas>4){jTextField105.setText(cnr.get(4).toString());}
            if(alphas>5){jTextField106.setText(cnr.get(5).toString());}
            if(alphas>6){jTextField107.setText(cnr.get(6).toString());}
            if(alphas>7){jTextField108.setText(cnr.get(7).toString());}
            if(alphas>8){jTextField109.setText(cnr.get(8).toString());}
            if(alphas>9){jTextField110.setText(cnr.get(9).toString());}
            if(alphas>10){jTextField111.setText(cnr.get(10).toString());}
            if(alphas>11){jTextField112.setText(cnr.get(11).toString());}
            if(alphas>12){jTextField113.setText(cnr.get(12).toString());}
            if(alphas>13){jTextField114.setText(cnr.get(13).toString());}
            if(alphas>14){jTextField115.setText(cnr.get(14).toString());}
            if(alphas>15){jTextField116.setText(cnr.get(15).toString());}
            if(alphas>16){jTextField117.setText(cnr.get(16).toString());}
            if(alphas>17){jTextField118.setText(cnr.get(17).toString());}
            if(alphas>18){jTextField119.setText(cnr.get(18).toString());}
            if(alphas>19){jTextField120.setText(cnr.get(19).toString());}
        } else if (column == 6) {
            jTextField121.setText(cnr.get(1).toString());
            jTextField122.setText(cnr.get(0).toString());
            if(alphas>2){jTextField123.setText(cnr.get(2).toString());}
            if(alphas>3){jTextField124.setText(cnr.get(3).toString());}
            if(alphas>4){jTextField125.setText(cnr.get(4).toString());}
            if(alphas>5){jTextField126.setText(cnr.get(5).toString());}
            if(alphas>6){jTextField127.setText(cnr.get(6).toString());}
            if(alphas>7){jTextField128.setText(cnr.get(7).toString());}
            if(alphas>8){jTextField129.setText(cnr.get(8).toString());}
            if(alphas>9){jTextField130.setText(cnr.get(9).toString());}
            if(alphas>10){jTextField131.setText(cnr.get(10).toString());}
            if(alphas>11){jTextField132.setText(cnr.get(11).toString());}
            if(alphas>12){jTextField133.setText(cnr.get(12).toString());}
            if(alphas>13){jTextField134.setText(cnr.get(13).toString());}
            if(alphas>14){jTextField135.setText(cnr.get(14).toString());}
            if(alphas>15){jTextField136.setText(cnr.get(15).toString());}
            if(alphas>16){jTextField137.setText(cnr.get(16).toString());}
            if(alphas>17){jTextField138.setText(cnr.get(17).toString());}
            if(alphas>18){jTextField139.setText(cnr.get(18).toString());}
            if(alphas>19){jTextField140.setText(cnr.get(19).toString());}
        }
    }
    
    private void showClr(int column) {
        if (column == 1) {
            jTextField21.setText(clr.get(1).toString());
            jTextField22.setText(clr.get(0).toString());
            if(alphas>2){jTextField23.setText(clr.get(2).toString());}
            if(alphas>3){jTextField24.setText(clr.get(3).toString());}
            if(alphas>4){jTextField25.setText(clr.get(4).toString());}
            if(alphas>5){jTextField26.setText(clr.get(5).toString());}
            if(alphas>6){jTextField27.setText(clr.get(6).toString());}
            if(alphas>7){jTextField28.setText(clr.get(7).toString());}
            if(alphas>8){jTextField29.setText(clr.get(8).toString());}
            if(alphas>9){jTextField30.setText(clr.get(9).toString());}
            if(alphas>10){jTextField31.setText(clr.get(10).toString());}
            if(alphas>11){jTextField32.setText(clr.get(11).toString());}
            if(alphas>12){jTextField33.setText(clr.get(12).toString());}
            if(alphas>13){jTextField34.setText(clr.get(13).toString());}
            if(alphas>14){jTextField35.setText(clr.get(14).toString());}
            if(alphas>15){jTextField36.setText(clr.get(15).toString());}
            if(alphas>16){jTextField37.setText(clr.get(16).toString());}
            if(alphas>17){jTextField38.setText(clr.get(17).toString());}
            if(alphas>18){jTextField39.setText(clr.get(18).toString());}
            if(alphas>19){jTextField40.setText(clr.get(19).toString());}
        } else if (column == 2) {
            jTextField41.setText(clr.get(1).toString());
            jTextField42.setText(clr.get(0).toString());
            if(alphas>2){jTextField43.setText(clr.get(2).toString());}
            if(alphas>3){jTextField44.setText(clr.get(3).toString());}
            if(alphas>4){jTextField45.setText(clr.get(4).toString());}
            if(alphas>5){jTextField46.setText(clr.get(5).toString());}
            if(alphas>6){jTextField47.setText(clr.get(6).toString());}
            if(alphas>7){jTextField48.setText(clr.get(7).toString());}
            if(alphas>8){jTextField49.setText(clr.get(8).toString());}
            if(alphas>9){jTextField50.setText(clr.get(9).toString());}
            if(alphas>10){jTextField51.setText(clr.get(10).toString());}
            if(alphas>11){jTextField52.setText(clr.get(11).toString());}
            if(alphas>12){jTextField53.setText(clr.get(12).toString());}
            if(alphas>13){jTextField54.setText(clr.get(13).toString());}
            if(alphas>14){jTextField55.setText(clr.get(14).toString());}
            if(alphas>15){jTextField56.setText(clr.get(15).toString());}
            if(alphas>16){jTextField57.setText(clr.get(16).toString());}
            if(alphas>17){jTextField58.setText(clr.get(17).toString());}
            if(alphas>18){jTextField59.setText(clr.get(18).toString());}
            if(alphas>19){jTextField60.setText(clr.get(19).toString());}
        } else if (column == 3) {
            jTextField61.setText(clr.get(1).toString());
            jTextField62.setText(clr.get(0).toString());
            if(alphas>2){jTextField63.setText(clr.get(2).toString());}
            if(alphas>3){jTextField64.setText(clr.get(3).toString());}
            if(alphas>4){jTextField65.setText(clr.get(4).toString());}
            if(alphas>5){jTextField66.setText(clr.get(5).toString());}
            if(alphas>6){jTextField67.setText(clr.get(6).toString());}
            if(alphas>7){jTextField68.setText(clr.get(7).toString());}
            if(alphas>8){jTextField69.setText(clr.get(8).toString());}
            if(alphas>9){jTextField70.setText(clr.get(9).toString());}
            if(alphas>10){jTextField71.setText(clr.get(10).toString());}
            if(alphas>11){jTextField72.setText(clr.get(11).toString());}
            if(alphas>12){jTextField73.setText(clr.get(12).toString());}
            if(alphas>13){jTextField74.setText(clr.get(13).toString());}
            if(alphas>14){jTextField75.setText(clr.get(14).toString());}
            if(alphas>15){jTextField76.setText(clr.get(15).toString());}
            if(alphas>16){jTextField77.setText(clr.get(16).toString());}
            if(alphas>17){jTextField78.setText(clr.get(17).toString());}
            if(alphas>18){jTextField79.setText(clr.get(18).toString());}
            if(alphas>19){jTextField80.setText(clr.get(19).toString());}
        } else if (column == 4) {
            jTextField81.setText(clr.get(1).toString());
            jTextField82.setText(clr.get(0).toString());
            if(alphas>2){jTextField83.setText(clr.get(2).toString());}
            if(alphas>3){jTextField84.setText(clr.get(3).toString());}
            if(alphas>4){jTextField85.setText(clr.get(4).toString());}
            if(alphas>5){jTextField86.setText(clr.get(5).toString());}
            if(alphas>6){jTextField87.setText(clr.get(6).toString());}
            if(alphas>7){jTextField88.setText(clr.get(7).toString());}
            if(alphas>8){jTextField89.setText(clr.get(8).toString());}
            if(alphas>9){jTextField90.setText(clr.get(9).toString());}
            if(alphas>10){jTextField91.setText(clr.get(10).toString());}
            if(alphas>11){jTextField92.setText(clr.get(11).toString());}
            if(alphas>12){jTextField93.setText(clr.get(12).toString());}
            if(alphas>13){jTextField94.setText(clr.get(13).toString());}
            if(alphas>14){jTextField95.setText(clr.get(14).toString());}
            if(alphas>15){jTextField96.setText(clr.get(15).toString());}
            if(alphas>16){jTextField97.setText(clr.get(16).toString());}
            if(alphas>17){jTextField98.setText(clr.get(17).toString());}
            if(alphas>18){jTextField99.setText(clr.get(18).toString());}
            if(alphas>19){jTextField100.setText(clr.get(19).toString());}
        } else if (column == 5) {
            jTextField101.setText(clr.get(1).toString());
            jTextField102.setText(clr.get(0).toString());
            if(alphas>2){jTextField103.setText(clr.get(2).toString());}
            if(alphas>3){jTextField104.setText(clr.get(3).toString());}
            if(alphas>4){jTextField105.setText(clr.get(4).toString());}
            if(alphas>5){jTextField106.setText(clr.get(5).toString());}
            if(alphas>6){jTextField107.setText(clr.get(6).toString());}
            if(alphas>7){jTextField108.setText(clr.get(7).toString());}
            if(alphas>8){jTextField109.setText(clr.get(8).toString());}
            if(alphas>9){jTextField110.setText(clr.get(9).toString());}
            if(alphas>10){jTextField111.setText(clr.get(10).toString());}
            if(alphas>11){jTextField112.setText(clr.get(11).toString());}
            if(alphas>12){jTextField113.setText(clr.get(12).toString());}
            if(alphas>13){jTextField114.setText(clr.get(13).toString());}
            if(alphas>14){jTextField115.setText(clr.get(14).toString());}
            if(alphas>15){jTextField116.setText(clr.get(15).toString());}
            if(alphas>16){jTextField117.setText(clr.get(16).toString());}
            if(alphas>17){jTextField118.setText(clr.get(17).toString());}
            if(alphas>18){jTextField119.setText(clr.get(18).toString());}
            if(alphas>19){jTextField120.setText(clr.get(19).toString());}
        } else if (column == 6) {
            jTextField121.setText(clr.get(1).toString());
            jTextField122.setText(clr.get(0).toString());
            if(alphas>2){jTextField123.setText(clr.get(2).toString());}
            if(alphas>3){jTextField124.setText(clr.get(3).toString());}
            if(alphas>4){jTextField125.setText(clr.get(4).toString());}
            if(alphas>5){jTextField126.setText(clr.get(5).toString());}
            if(alphas>6){jTextField127.setText(clr.get(6).toString());}
            if(alphas>7){jTextField128.setText(clr.get(7).toString());}
            if(alphas>8){jTextField129.setText(clr.get(8).toString());}
            if(alphas>9){jTextField130.setText(clr.get(9).toString());}
            if(alphas>10){jTextField131.setText(clr.get(10).toString());}
            if(alphas>11){jTextField132.setText(clr.get(11).toString());}
            if(alphas>12){jTextField133.setText(clr.get(12).toString());}
            if(alphas>13){jTextField134.setText(clr.get(13).toString());}
            if(alphas>14){jTextField135.setText(clr.get(14).toString());}
            if(alphas>15){jTextField136.setText(clr.get(15).toString());}
            if(alphas>16){jTextField137.setText(clr.get(16).toString());}
            if(alphas>17){jTextField138.setText(clr.get(17).toString());}
            if(alphas>18){jTextField139.setText(clr.get(18).toString());}
            if(alphas>19){jTextField140.setText(clr.get(19).toString());}
        }
    }
    
    private void showCd(int column) {
        if (column == 1) {
            jTextField201.setText(cdalpha.get(1).toString());
            jTextField202.setText(cdalpha.get(0).toString());
            if(alphas>2){jTextField203.setText(cdalpha.get(2).toString());}
            if(alphas>3){jTextField204.setText(cdalpha.get(3).toString());}
            if(alphas>4){jTextField205.setText(cdalpha.get(4).toString());}
            if(alphas>5){jTextField206.setText(cdalpha.get(5).toString());}
            if(alphas>6){jTextField207.setText(cdalpha.get(6).toString());}
            if(alphas>7){jTextField208.setText(cdalpha.get(7).toString());}
            if(alphas>8){jTextField209.setText(cdalpha.get(8).toString());}
            if(alphas>9){jTextField210.setText(cdalpha.get(9).toString());}
            if(alphas>10){jTextField211.setText(cdalpha.get(10).toString());}
            if(alphas>11){jTextField212.setText(cdalpha.get(11).toString());}
            if(alphas>12){jTextField213.setText(cdalpha.get(12).toString());}
            if(alphas>13){jTextField214.setText(cdalpha.get(13).toString());}
            if(alphas>14){jTextField215.setText(cdalpha.get(14).toString());}
            if(alphas>15){jTextField216.setText(cdalpha.get(15).toString());}
            if(alphas>16){jTextField217.setText(cdalpha.get(16).toString());}
            if(alphas>17){jTextField218.setText(cdalpha.get(17).toString());}
            if(alphas>18){jTextField219.setText(cdalpha.get(18).toString());}
            if(alphas>19){jTextField200.setText(cdalpha.get(19).toString());}
        } else if (column == 2) {
            jTextField181.setText(cdalpha.get(1).toString());
            jTextField182.setText(cdalpha.get(0).toString());
            if(alphas>2){jTextField183.setText(cdalpha.get(2).toString());}
            if(alphas>3){jTextField184.setText(cdalpha.get(3).toString());}
            if(alphas>4){jTextField185.setText(cdalpha.get(4).toString());}
            if(alphas>5){jTextField186.setText(cdalpha.get(5).toString());}
            if(alphas>6){jTextField187.setText(cdalpha.get(6).toString());}
            if(alphas>7){jTextField188.setText(cdalpha.get(7).toString());}
            if(alphas>8){jTextField189.setText(cdalpha.get(8).toString());}
            if(alphas>9){jTextField190.setText(cdalpha.get(9).toString());}
            if(alphas>10){jTextField191.setText(cdalpha.get(10).toString());}
            if(alphas>11){jTextField192.setText(cdalpha.get(11).toString());}
            if(alphas>12){jTextField193.setText(cdalpha.get(12).toString());}
            if(alphas>13){jTextField194.setText(cdalpha.get(13).toString());}
            if(alphas>14){jTextField195.setText(cdalpha.get(14).toString());}
            if(alphas>15){jTextField196.setText(cdalpha.get(15).toString());}
            if(alphas>16){jTextField197.setText(cdalpha.get(16).toString());}
            if(alphas>17){jTextField198.setText(cdalpha.get(17).toString());}
            if(alphas>18){jTextField199.setText(cdalpha.get(18).toString());}
            if(alphas>19){jTextField200.setText(cdalpha.get(19).toString());}
        } else if (column == 3) {
            jTextField221.setText(cdalpha.get(1).toString());
            jTextField222.setText(cdalpha.get(0).toString());
            if(alphas>2){jTextField223.setText(cdalpha.get(2).toString());}
            if(alphas>3){jTextField224.setText(cdalpha.get(3).toString());}
            if(alphas>4){jTextField225.setText(cdalpha.get(4).toString());}
            if(alphas>5){jTextField226.setText(cdalpha.get(5).toString());}
            if(alphas>6){jTextField227.setText(cdalpha.get(6).toString());}
            if(alphas>7){jTextField228.setText(cdalpha.get(7).toString());}
            if(alphas>8){jTextField229.setText(cdalpha.get(8).toString());}
            if(alphas>9){jTextField230.setText(cdalpha.get(9).toString());}
            if(alphas>10){jTextField231.setText(cdalpha.get(10).toString());}
            if(alphas>11){jTextField232.setText(cdalpha.get(11).toString());}
            if(alphas>12){jTextField233.setText(cdalpha.get(12).toString());}
            if(alphas>13){jTextField234.setText(cdalpha.get(13).toString());}
            if(alphas>14){jTextField235.setText(cdalpha.get(14).toString());}
            if(alphas>15){jTextField236.setText(cdalpha.get(15).toString());}
            if(alphas>16){jTextField237.setText(cdalpha.get(16).toString());}
            if(alphas>17){jTextField238.setText(cdalpha.get(17).toString());}
            if(alphas>18){jTextField239.setText(cdalpha.get(18).toString());}
            if(alphas>19){jTextField240.setText(cdalpha.get(19).toString());}
        } else if (column == 4) {
            jTextField241.setText(cdalpha.get(1).toString());
            jTextField242.setText(cdalpha.get(0).toString());
            if(alphas>2){jTextField243.setText(cdalpha.get(2).toString());}
            if(alphas>3){jTextField244.setText(cdalpha.get(3).toString());}
            if(alphas>4){jTextField245.setText(cdalpha.get(4).toString());}
            if(alphas>5){jTextField246.setText(cdalpha.get(5).toString());}
            if(alphas>6){jTextField247.setText(cdalpha.get(6).toString());}
            if(alphas>7){jTextField248.setText(cdalpha.get(7).toString());}
            if(alphas>8){jTextField249.setText(cdalpha.get(8).toString());}
            if(alphas>9){jTextField250.setText(cdalpha.get(9).toString());}
            if(alphas>10){jTextField251.setText(cdalpha.get(10).toString());}
            if(alphas>11){jTextField252.setText(cdalpha.get(11).toString());}
            if(alphas>12){jTextField253.setText(cdalpha.get(12).toString());}
            if(alphas>13){jTextField254.setText(cdalpha.get(13).toString());}
            if(alphas>14){jTextField255.setText(cdalpha.get(14).toString());}
            if(alphas>15){jTextField256.setText(cdalpha.get(15).toString());}
            if(alphas>16){jTextField257.setText(cdalpha.get(16).toString());}
            if(alphas>17){jTextField258.setText(cdalpha.get(17).toString());}
            if(alphas>18){jTextField259.setText(cdalpha.get(18).toString());}
            if(alphas>19){jTextField260.setText(cdalpha.get(19).toString());}
        } else if (column == 5) {
            jTextField141.setText(cdalpha.get(1).toString());
            jTextField142.setText(cdalpha.get(0).toString());
            if(alphas>2){jTextField143.setText(cdalpha.get(2).toString());}
            if(alphas>3){jTextField144.setText(cdalpha.get(3).toString());}
            if(alphas>4){jTextField145.setText(cdalpha.get(4).toString());}
            if(alphas>5){jTextField146.setText(cdalpha.get(5).toString());}
            if(alphas>6){jTextField147.setText(cdalpha.get(6).toString());}
            if(alphas>7){jTextField148.setText(cdalpha.get(7).toString());}
            if(alphas>8){jTextField149.setText(cdalpha.get(8).toString());}
            if(alphas>9){jTextField150.setText(cdalpha.get(9).toString());}
            if(alphas>10){jTextField151.setText(cdalpha.get(10).toString());}
            if(alphas>11){jTextField152.setText(cdalpha.get(11).toString());}
            if(alphas>12){jTextField153.setText(cdalpha.get(12).toString());}
            if(alphas>13){jTextField154.setText(cdalpha.get(13).toString());}
            if(alphas>14){jTextField155.setText(cdalpha.get(14).toString());}
            if(alphas>15){jTextField156.setText(cdalpha.get(15).toString());}
            if(alphas>16){jTextField157.setText(cdalpha.get(16).toString());}
            if(alphas>17){jTextField158.setText(cdalpha.get(17).toString());}
            if(alphas>18){jTextField159.setText(cdalpha.get(18).toString());}
            if(alphas>19){jTextField160.setText(cdalpha.get(19).toString());}
        } 
    }
    
    private void showCL(int column) {
        if (column == 1) {
            jTextField201.setText(cLalpha.get(1).toString());
            jTextField202.setText(cLalpha.get(0).toString());
            if(alphas>2){jTextField203.setText(cLalpha.get(2).toString());}
            if(alphas>3){jTextField204.setText(cLalpha.get(3).toString());}
            if(alphas>4){jTextField205.setText(cLalpha.get(4).toString());}
            if(alphas>5){jTextField206.setText(cLalpha.get(5).toString());}
            if(alphas>6){jTextField207.setText(cLalpha.get(6).toString());}
            if(alphas>7){jTextField208.setText(cLalpha.get(7).toString());}
            if(alphas>8){jTextField209.setText(cLalpha.get(8).toString());}
            if(alphas>9){jTextField210.setText(cLalpha.get(9).toString());}
            if(alphas>10){jTextField211.setText(cLalpha.get(10).toString());}
            if(alphas>11){jTextField212.setText(cLalpha.get(11).toString());}
            if(alphas>12){jTextField213.setText(cLalpha.get(12).toString());}
            if(alphas>13){jTextField214.setText(cLalpha.get(13).toString());}
            if(alphas>14){jTextField215.setText(cLalpha.get(14).toString());}
            if(alphas>15){jTextField216.setText(cLalpha.get(15).toString());}
            if(alphas>16){jTextField217.setText(cLalpha.get(16).toString());}
            if(alphas>17){jTextField218.setText(cLalpha.get(17).toString());}
            if(alphas>18){jTextField219.setText(cLalpha.get(18).toString());}
            if(alphas>19){jTextField200.setText(cLalpha.get(19).toString());}
        } else if (column == 2) {
            jTextField181.setText(cLalpha.get(1).toString());
            jTextField182.setText(cLalpha.get(0).toString());
            if(alphas>2){jTextField183.setText(cLalpha.get(2).toString());}
            if(alphas>3){jTextField184.setText(cLalpha.get(3).toString());}
            if(alphas>4){jTextField185.setText(cLalpha.get(4).toString());}
            if(alphas>5){jTextField186.setText(cLalpha.get(5).toString());}
            if(alphas>6){jTextField187.setText(cLalpha.get(6).toString());}
            if(alphas>7){jTextField188.setText(cLalpha.get(7).toString());}
            if(alphas>8){jTextField189.setText(cLalpha.get(8).toString());}
            if(alphas>9){jTextField190.setText(cLalpha.get(9).toString());}
            if(alphas>10){jTextField191.setText(cLalpha.get(10).toString());}
            if(alphas>11){jTextField192.setText(cLalpha.get(11).toString());}
            if(alphas>12){jTextField193.setText(cLalpha.get(12).toString());}
            if(alphas>13){jTextField194.setText(cLalpha.get(13).toString());}
            if(alphas>14){jTextField195.setText(cLalpha.get(14).toString());}
            if(alphas>15){jTextField196.setText(cLalpha.get(15).toString());}
            if(alphas>16){jTextField197.setText(cLalpha.get(16).toString());}
            if(alphas>17){jTextField198.setText(cLalpha.get(17).toString());}
            if(alphas>18){jTextField199.setText(cLalpha.get(18).toString());}
            if(alphas>19){jTextField200.setText(cLalpha.get(19).toString());}
        } else if (column == 3) {
            jTextField221.setText(cLalpha.get(1).toString());
            jTextField222.setText(cLalpha.get(0).toString());
            if(alphas>2){jTextField223.setText(cLalpha.get(2).toString());}
            if(alphas>3){jTextField224.setText(cLalpha.get(3).toString());}
            if(alphas>4){jTextField225.setText(cLalpha.get(4).toString());}
            if(alphas>5){jTextField226.setText(cLalpha.get(5).toString());}
            if(alphas>6){jTextField227.setText(cLalpha.get(6).toString());}
            if(alphas>7){jTextField228.setText(cLalpha.get(7).toString());}
            if(alphas>8){jTextField229.setText(cLalpha.get(8).toString());}
            if(alphas>9){jTextField230.setText(cLalpha.get(9).toString());}
            if(alphas>10){jTextField231.setText(cLalpha.get(10).toString());}
            if(alphas>11){jTextField232.setText(cLalpha.get(11).toString());}
            if(alphas>12){jTextField233.setText(cLalpha.get(12).toString());}
            if(alphas>13){jTextField234.setText(cLalpha.get(13).toString());}
            if(alphas>14){jTextField235.setText(cLalpha.get(14).toString());}
            if(alphas>15){jTextField236.setText(cLalpha.get(15).toString());}
            if(alphas>16){jTextField237.setText(cLalpha.get(16).toString());}
            if(alphas>17){jTextField238.setText(cLalpha.get(17).toString());}
            if(alphas>18){jTextField239.setText(cLalpha.get(18).toString());}
            if(alphas>19){jTextField240.setText(cLalpha.get(19).toString());}
        } else if (column == 4) {
            jTextField241.setText(cLalpha.get(1).toString());
            jTextField242.setText(cLalpha.get(0).toString());
            if(alphas>2){jTextField243.setText(cLalpha.get(2).toString());}
            if(alphas>3){jTextField244.setText(cLalpha.get(3).toString());}
            if(alphas>4){jTextField245.setText(cLalpha.get(4).toString());}
            if(alphas>5){jTextField246.setText(cLalpha.get(5).toString());}
            if(alphas>6){jTextField247.setText(cLalpha.get(6).toString());}
            if(alphas>7){jTextField248.setText(cLalpha.get(7).toString());}
            if(alphas>8){jTextField249.setText(cLalpha.get(8).toString());}
            if(alphas>9){jTextField250.setText(cLalpha.get(9).toString());}
            if(alphas>10){jTextField251.setText(cLalpha.get(10).toString());}
            if(alphas>11){jTextField252.setText(cLalpha.get(11).toString());}
            if(alphas>12){jTextField253.setText(cLalpha.get(12).toString());}
            if(alphas>13){jTextField254.setText(cLalpha.get(13).toString());}
            if(alphas>14){jTextField255.setText(cLalpha.get(14).toString());}
            if(alphas>15){jTextField256.setText(cLalpha.get(15).toString());}
            if(alphas>16){jTextField257.setText(cLalpha.get(16).toString());}
            if(alphas>17){jTextField258.setText(cLalpha.get(17).toString());}
            if(alphas>18){jTextField259.setText(cLalpha.get(18).toString());}
            if(alphas>19){jTextField260.setText(cLalpha.get(19).toString());}
        } else if (column == 5) {
            jTextField141.setText(cLalpha.get(1).toString());
            jTextField142.setText(cLalpha.get(0).toString());
            if(alphas>2){jTextField143.setText(cLalpha.get(2).toString());}
            if(alphas>3){jTextField144.setText(cLalpha.get(3).toString());}
            if(alphas>4){jTextField145.setText(cLalpha.get(4).toString());}
            if(alphas>5){jTextField146.setText(cLalpha.get(5).toString());}
            if(alphas>6){jTextField147.setText(cLalpha.get(6).toString());}
            if(alphas>7){jTextField148.setText(cLalpha.get(7).toString());}
            if(alphas>8){jTextField149.setText(cLalpha.get(8).toString());}
            if(alphas>9){jTextField150.setText(cLalpha.get(9).toString());}
            if(alphas>10){jTextField151.setText(cLalpha.get(10).toString());}
            if(alphas>11){jTextField152.setText(cLalpha.get(11).toString());}
            if(alphas>12){jTextField153.setText(cLalpha.get(12).toString());}
            if(alphas>13){jTextField154.setText(cLalpha.get(13).toString());}
            if(alphas>14){jTextField155.setText(cLalpha.get(14).toString());}
            if(alphas>15){jTextField156.setText(cLalpha.get(15).toString());}
            if(alphas>16){jTextField157.setText(cLalpha.get(16).toString());}
            if(alphas>17){jTextField158.setText(cLalpha.get(17).toString());}
            if(alphas>18){jTextField159.setText(cLalpha.get(18).toString());}
            if(alphas>19){jTextField160.setText(cLalpha.get(19).toString());}
        } 
    }
    
    private void showCm(int column) {
        if (column == 1) {
            jTextField201.setText(cmalpha.get(1).toString());
            jTextField202.setText(cmalpha.get(0).toString());
            if(alphas>2){jTextField203.setText(cmalpha.get(2).toString());}
            if(alphas>3){jTextField204.setText(cmalpha.get(3).toString());}
            if(alphas>4){jTextField205.setText(cmalpha.get(4).toString());}
            if(alphas>5){jTextField206.setText(cmalpha.get(5).toString());}
            if(alphas>6){jTextField207.setText(cmalpha.get(6).toString());}
            if(alphas>7){jTextField208.setText(cmalpha.get(7).toString());}
            if(alphas>8){jTextField209.setText(cmalpha.get(8).toString());}
            if(alphas>9){jTextField210.setText(cmalpha.get(9).toString());}
            if(alphas>10){jTextField211.setText(cmalpha.get(10).toString());}
            if(alphas>11){jTextField212.setText(cmalpha.get(11).toString());}
            if(alphas>12){jTextField213.setText(cmalpha.get(12).toString());}
            if(alphas>13){jTextField214.setText(cmalpha.get(13).toString());}
            if(alphas>14){jTextField215.setText(cmalpha.get(14).toString());}
            if(alphas>15){jTextField216.setText(cmalpha.get(15).toString());}
            if(alphas>16){jTextField217.setText(cmalpha.get(16).toString());}
            if(alphas>17){jTextField218.setText(cmalpha.get(17).toString());}
            if(alphas>18){jTextField219.setText(cmalpha.get(18).toString());}
            if(alphas>19){jTextField200.setText(cmalpha.get(19).toString());}
        } else if (column == 2) {
            jTextField181.setText(cmalpha.get(1).toString());
            jTextField182.setText(cmalpha.get(0).toString());
            if(alphas>2){jTextField183.setText(cmalpha.get(2).toString());}
            if(alphas>3){jTextField184.setText(cmalpha.get(3).toString());}
            if(alphas>4){jTextField185.setText(cmalpha.get(4).toString());}
            if(alphas>5){jTextField186.setText(cmalpha.get(5).toString());}
            if(alphas>6){jTextField187.setText(cmalpha.get(6).toString());}
            if(alphas>7){jTextField188.setText(cmalpha.get(7).toString());}
            if(alphas>8){jTextField189.setText(cmalpha.get(8).toString());}
            if(alphas>9){jTextField190.setText(cmalpha.get(9).toString());}
            if(alphas>10){jTextField191.setText(cmalpha.get(10).toString());}
            if(alphas>11){jTextField192.setText(cmalpha.get(11).toString());}
            if(alphas>12){jTextField193.setText(cmalpha.get(12).toString());}
            if(alphas>13){jTextField194.setText(cmalpha.get(13).toString());}
            if(alphas>14){jTextField195.setText(cmalpha.get(14).toString());}
            if(alphas>15){jTextField196.setText(cmalpha.get(15).toString());}
            if(alphas>16){jTextField197.setText(cmalpha.get(16).toString());}
            if(alphas>17){jTextField198.setText(cmalpha.get(17).toString());}
            if(alphas>18){jTextField199.setText(cmalpha.get(18).toString());}
            if(alphas>19){jTextField200.setText(cmalpha.get(19).toString());}
        } else if (column == 3) {
            jTextField221.setText(cmalpha.get(1).toString());
            jTextField222.setText(cmalpha.get(0).toString());
            if(alphas>2){jTextField223.setText(cmalpha.get(2).toString());}
            if(alphas>3){jTextField224.setText(cmalpha.get(3).toString());}
            if(alphas>4){jTextField225.setText(cmalpha.get(4).toString());}
            if(alphas>5){jTextField226.setText(cmalpha.get(5).toString());}
            if(alphas>6){jTextField227.setText(cmalpha.get(6).toString());}
            if(alphas>7){jTextField228.setText(cmalpha.get(7).toString());}
            if(alphas>8){jTextField229.setText(cmalpha.get(8).toString());}
            if(alphas>9){jTextField230.setText(cmalpha.get(9).toString());}
            if(alphas>10){jTextField231.setText(cmalpha.get(10).toString());}
            if(alphas>11){jTextField232.setText(cmalpha.get(11).toString());}
            if(alphas>12){jTextField233.setText(cmalpha.get(12).toString());}
            if(alphas>13){jTextField234.setText(cmalpha.get(13).toString());}
            if(alphas>14){jTextField235.setText(cmalpha.get(14).toString());}
            if(alphas>15){jTextField236.setText(cmalpha.get(15).toString());}
            if(alphas>16){jTextField237.setText(cmalpha.get(16).toString());}
            if(alphas>17){jTextField238.setText(cmalpha.get(17).toString());}
            if(alphas>18){jTextField239.setText(cmalpha.get(18).toString());}
            if(alphas>19){jTextField240.setText(cmalpha.get(19).toString());}
        } else if (column == 4) {
            jTextField241.setText(cmalpha.get(1).toString());
            jTextField242.setText(cmalpha.get(0).toString());
            if(alphas>2){jTextField243.setText(cmalpha.get(2).toString());}
            if(alphas>3){jTextField244.setText(cmalpha.get(3).toString());}
            if(alphas>4){jTextField245.setText(cmalpha.get(4).toString());}
            if(alphas>5){jTextField246.setText(cmalpha.get(5).toString());}
            if(alphas>6){jTextField247.setText(cmalpha.get(6).toString());}
            if(alphas>7){jTextField248.setText(cmalpha.get(7).toString());}
            if(alphas>8){jTextField249.setText(cmalpha.get(8).toString());}
            if(alphas>9){jTextField250.setText(cmalpha.get(9).toString());}
            if(alphas>10){jTextField251.setText(cmalpha.get(10).toString());}
            if(alphas>11){jTextField252.setText(cmalpha.get(11).toString());}
            if(alphas>12){jTextField253.setText(cmalpha.get(12).toString());}
            if(alphas>13){jTextField254.setText(cmalpha.get(13).toString());}
            if(alphas>14){jTextField255.setText(cmalpha.get(14).toString());}
            if(alphas>15){jTextField256.setText(cmalpha.get(15).toString());}
            if(alphas>16){jTextField257.setText(cmalpha.get(16).toString());}
            if(alphas>17){jTextField258.setText(cmalpha.get(17).toString());}
            if(alphas>18){jTextField259.setText(cmalpha.get(18).toString());}
            if(alphas>19){jTextField260.setText(cmalpha.get(19).toString());}
        } else if (column == 5) {
            jTextField141.setText(cmalpha.get(1).toString());
            jTextField142.setText(cmalpha.get(0).toString());
            if(alphas>2){jTextField143.setText(cmalpha.get(2).toString());}
            if(alphas>3){jTextField144.setText(cmalpha.get(3).toString());}
            if(alphas>4){jTextField145.setText(cmalpha.get(4).toString());}
            if(alphas>5){jTextField146.setText(cmalpha.get(5).toString());}
            if(alphas>6){jTextField147.setText(cmalpha.get(6).toString());}
            if(alphas>7){jTextField148.setText(cmalpha.get(7).toString());}
            if(alphas>8){jTextField149.setText(cmalpha.get(8).toString());}
            if(alphas>9){jTextField150.setText(cmalpha.get(9).toString());}
            if(alphas>10){jTextField151.setText(cmalpha.get(10).toString());}
            if(alphas>11){jTextField152.setText(cmalpha.get(11).toString());}
            if(alphas>12){jTextField153.setText(cmalpha.get(12).toString());}
            if(alphas>13){jTextField154.setText(cmalpha.get(13).toString());}
            if(alphas>14){jTextField155.setText(cmalpha.get(14).toString());}
            if(alphas>15){jTextField156.setText(cmalpha.get(15).toString());}
            if(alphas>16){jTextField157.setText(cmalpha.get(16).toString());}
            if(alphas>17){jTextField158.setText(cmalpha.get(17).toString());}
            if(alphas>18){jTextField159.setText(cmalpha.get(18).toString());}
            if(alphas>19){jTextField160.setText(cmalpha.get(19).toString());}
        } 
    }
    
    private void showCLad(int column) {
        if (column == 1) {
            jTextField201.setText(cLalphadot.get(1).toString());
            jTextField202.setText(cLalphadot.get(0).toString());
            if(alphas>2){jTextField203.setText(cLalphadot.get(2).toString());}
            if(alphas>3){jTextField204.setText(cLalphadot.get(3).toString());}
            if(alphas>4){jTextField205.setText(cLalphadot.get(4).toString());}
            if(alphas>5){jTextField206.setText(cLalphadot.get(5).toString());}
            if(alphas>6){jTextField207.setText(cLalphadot.get(6).toString());}
            if(alphas>7){jTextField208.setText(cLalphadot.get(7).toString());}
            if(alphas>8){jTextField209.setText(cLalphadot.get(8).toString());}
            if(alphas>9){jTextField210.setText(cLalphadot.get(9).toString());}
            if(alphas>10){jTextField211.setText(cLalphadot.get(10).toString());}
            if(alphas>11){jTextField212.setText(cLalphadot.get(11).toString());}
            if(alphas>12){jTextField213.setText(cLalphadot.get(12).toString());}
            if(alphas>13){jTextField214.setText(cLalphadot.get(13).toString());}
            if(alphas>14){jTextField215.setText(cLalphadot.get(14).toString());}
            if(alphas>15){jTextField216.setText(cLalphadot.get(15).toString());}
            if(alphas>16){jTextField217.setText(cLalphadot.get(16).toString());}
            if(alphas>17){jTextField218.setText(cLalphadot.get(17).toString());}
            if(alphas>18){jTextField219.setText(cLalphadot.get(18).toString());}
            if(alphas>19){jTextField200.setText(cLalphadot.get(19).toString());}
        } else if (column == 2) {
            jTextField181.setText(cLalphadot.get(1).toString());
            jTextField182.setText(cLalphadot.get(0).toString());
            if(alphas>2){jTextField183.setText(cLalphadot.get(2).toString());}
            if(alphas>3){jTextField184.setText(cLalphadot.get(3).toString());}
            if(alphas>4){jTextField185.setText(cLalphadot.get(4).toString());}
            if(alphas>5){jTextField186.setText(cLalphadot.get(5).toString());}
            if(alphas>6){jTextField187.setText(cLalphadot.get(6).toString());}
            if(alphas>7){jTextField188.setText(cLalphadot.get(7).toString());}
            if(alphas>8){jTextField189.setText(cLalphadot.get(8).toString());}
            if(alphas>9){jTextField190.setText(cLalphadot.get(9).toString());}
            if(alphas>10){jTextField191.setText(cLalphadot.get(10).toString());}
            if(alphas>11){jTextField192.setText(cLalphadot.get(11).toString());}
            if(alphas>12){jTextField193.setText(cLalphadot.get(12).toString());}
            if(alphas>13){jTextField194.setText(cLalphadot.get(13).toString());}
            if(alphas>14){jTextField195.setText(cLalphadot.get(14).toString());}
            if(alphas>15){jTextField196.setText(cLalphadot.get(15).toString());}
            if(alphas>16){jTextField197.setText(cLalphadot.get(16).toString());}
            if(alphas>17){jTextField198.setText(cLalphadot.get(17).toString());}
            if(alphas>18){jTextField199.setText(cLalphadot.get(18).toString());}
            if(alphas>19){jTextField200.setText(cLalphadot.get(19).toString());}
        } else if (column == 3) {
            jTextField221.setText(cLalphadot.get(1).toString());
            jTextField222.setText(cLalphadot.get(0).toString());
            if(alphas>2){jTextField223.setText(cLalphadot.get(2).toString());}
            if(alphas>3){jTextField224.setText(cLalphadot.get(3).toString());}
            if(alphas>4){jTextField225.setText(cLalphadot.get(4).toString());}
            if(alphas>5){jTextField226.setText(cLalphadot.get(5).toString());}
            if(alphas>6){jTextField227.setText(cLalphadot.get(6).toString());}
            if(alphas>7){jTextField228.setText(cLalphadot.get(7).toString());}
            if(alphas>8){jTextField229.setText(cLalphadot.get(8).toString());}
            if(alphas>9){jTextField230.setText(cLalphadot.get(9).toString());}
            if(alphas>10){jTextField231.setText(cLalphadot.get(10).toString());}
            if(alphas>11){jTextField232.setText(cLalphadot.get(11).toString());}
            if(alphas>12){jTextField233.setText(cLalphadot.get(12).toString());}
            if(alphas>13){jTextField234.setText(cLalphadot.get(13).toString());}
            if(alphas>14){jTextField235.setText(cLalphadot.get(14).toString());}
            if(alphas>15){jTextField236.setText(cLalphadot.get(15).toString());}
            if(alphas>16){jTextField237.setText(cLalphadot.get(16).toString());}
            if(alphas>17){jTextField238.setText(cLalphadot.get(17).toString());}
            if(alphas>18){jTextField239.setText(cLalphadot.get(18).toString());}
            if(alphas>19){jTextField240.setText(cLalphadot.get(19).toString());}
        } else if (column == 4) {
            jTextField241.setText(cLalphadot.get(1).toString());
            jTextField242.setText(cLalphadot.get(0).toString());
            if(alphas>2){jTextField243.setText(cLalphadot.get(2).toString());}
            if(alphas>3){jTextField244.setText(cLalphadot.get(3).toString());}
            if(alphas>4){jTextField245.setText(cLalphadot.get(4).toString());}
            if(alphas>5){jTextField246.setText(cLalphadot.get(5).toString());}
            if(alphas>6){jTextField247.setText(cLalphadot.get(6).toString());}
            if(alphas>7){jTextField248.setText(cLalphadot.get(7).toString());}
            if(alphas>8){jTextField249.setText(cLalphadot.get(8).toString());}
            if(alphas>9){jTextField250.setText(cLalphadot.get(9).toString());}
            if(alphas>10){jTextField251.setText(cLalphadot.get(10).toString());}
            if(alphas>11){jTextField252.setText(cLalphadot.get(11).toString());}
            if(alphas>12){jTextField253.setText(cLalphadot.get(12).toString());}
            if(alphas>13){jTextField254.setText(cLalphadot.get(13).toString());}
            if(alphas>14){jTextField255.setText(cLalphadot.get(14).toString());}
            if(alphas>15){jTextField256.setText(cLalphadot.get(15).toString());}
            if(alphas>16){jTextField257.setText(cLalphadot.get(16).toString());}
            if(alphas>17){jTextField258.setText(cLalphadot.get(17).toString());}
            if(alphas>18){jTextField259.setText(cLalphadot.get(18).toString());}
            if(alphas>19){jTextField260.setText(cLalphadot.get(19).toString());}
        } else if (column == 5) {
            jTextField141.setText(cLalphadot.get(1).toString());
            jTextField142.setText(cLalphadot.get(0).toString());
            if(alphas>2){jTextField143.setText(cLalphadot.get(2).toString());}
            if(alphas>3){jTextField144.setText(cLalphadot.get(3).toString());}
            if(alphas>4){jTextField145.setText(cLalphadot.get(4).toString());}
            if(alphas>5){jTextField146.setText(cLalphadot.get(5).toString());}
            if(alphas>6){jTextField147.setText(cLalphadot.get(6).toString());}
            if(alphas>7){jTextField148.setText(cLalphadot.get(7).toString());}
            if(alphas>8){jTextField149.setText(cLalphadot.get(8).toString());}
            if(alphas>9){jTextField150.setText(cLalphadot.get(9).toString());}
            if(alphas>10){jTextField151.setText(cLalphadot.get(10).toString());}
            if(alphas>11){jTextField152.setText(cLalphadot.get(11).toString());}
            if(alphas>12){jTextField153.setText(cLalphadot.get(12).toString());}
            if(alphas>13){jTextField154.setText(cLalphadot.get(13).toString());}
            if(alphas>14){jTextField155.setText(cLalphadot.get(14).toString());}
            if(alphas>15){jTextField156.setText(cLalphadot.get(15).toString());}
            if(alphas>16){jTextField157.setText(cLalphadot.get(16).toString());}
            if(alphas>17){jTextField158.setText(cLalphadot.get(17).toString());}
            if(alphas>18){jTextField159.setText(cLalphadot.get(18).toString());}
            if(alphas>19){jTextField160.setText(cLalphadot.get(19).toString());}
        } 
    }
    
    private void showCmad(int column) {
        if (column == 1) {
            jTextField201.setText(cmalphadot.get(1).toString());
            jTextField202.setText(cmalphadot.get(0).toString());
            if(alphas>2){jTextField203.setText(cmalphadot.get(2).toString());}
            if(alphas>3){jTextField204.setText(cmalphadot.get(3).toString());}
            if(alphas>4){jTextField205.setText(cmalphadot.get(4).toString());}
            if(alphas>5){jTextField206.setText(cmalphadot.get(5).toString());}
            if(alphas>6){jTextField207.setText(cmalphadot.get(6).toString());}
            if(alphas>7){jTextField208.setText(cmalphadot.get(7).toString());}
            if(alphas>8){jTextField209.setText(cmalphadot.get(8).toString());}
            if(alphas>9){jTextField210.setText(cmalphadot.get(9).toString());}
            if(alphas>10){jTextField211.setText(cmalphadot.get(10).toString());}
            if(alphas>11){jTextField212.setText(cmalphadot.get(11).toString());}
            if(alphas>12){jTextField213.setText(cmalphadot.get(12).toString());}
            if(alphas>13){jTextField214.setText(cmalphadot.get(13).toString());}
            if(alphas>14){jTextField215.setText(cmalphadot.get(14).toString());}
            if(alphas>15){jTextField216.setText(cmalphadot.get(15).toString());}
            if(alphas>16){jTextField217.setText(cmalphadot.get(16).toString());}
            if(alphas>17){jTextField218.setText(cmalphadot.get(17).toString());}
            if(alphas>18){jTextField219.setText(cmalphadot.get(18).toString());}
            if(alphas>19){jTextField200.setText(cmalphadot.get(19).toString());}
        } else if (column == 2) {
            jTextField181.setText(cmalphadot.get(1).toString());
            jTextField182.setText(cmalphadot.get(0).toString());
            if(alphas>2){jTextField183.setText(cmalphadot.get(2).toString());}
            if(alphas>3){jTextField184.setText(cmalphadot.get(3).toString());}
            if(alphas>4){jTextField185.setText(cmalphadot.get(4).toString());}
            if(alphas>5){jTextField186.setText(cmalphadot.get(5).toString());}
            if(alphas>6){jTextField187.setText(cmalphadot.get(6).toString());}
            if(alphas>7){jTextField188.setText(cmalphadot.get(7).toString());}
            if(alphas>8){jTextField189.setText(cmalphadot.get(8).toString());}
            if(alphas>9){jTextField190.setText(cmalphadot.get(9).toString());}
            if(alphas>10){jTextField191.setText(cmalphadot.get(10).toString());}
            if(alphas>11){jTextField192.setText(cmalphadot.get(11).toString());}
            if(alphas>12){jTextField193.setText(cmalphadot.get(12).toString());}
            if(alphas>13){jTextField194.setText(cmalphadot.get(13).toString());}
            if(alphas>14){jTextField195.setText(cmalphadot.get(14).toString());}
            if(alphas>15){jTextField196.setText(cmalphadot.get(15).toString());}
            if(alphas>16){jTextField197.setText(cmalphadot.get(16).toString());}
            if(alphas>17){jTextField198.setText(cmalphadot.get(17).toString());}
            if(alphas>18){jTextField199.setText(cmalphadot.get(18).toString());}
            if(alphas>19){jTextField200.setText(cmalphadot.get(19).toString());}
        } else if (column == 3) {
            jTextField221.setText(cmalphadot.get(1).toString());
            jTextField222.setText(cmalphadot.get(0).toString());
            if(alphas>2){jTextField223.setText(cmalphadot.get(2).toString());}
            if(alphas>3){jTextField224.setText(cmalphadot.get(3).toString());}
            if(alphas>4){jTextField225.setText(cmalphadot.get(4).toString());}
            if(alphas>5){jTextField226.setText(cmalphadot.get(5).toString());}
            if(alphas>6){jTextField227.setText(cmalphadot.get(6).toString());}
            if(alphas>7){jTextField228.setText(cmalphadot.get(7).toString());}
            if(alphas>8){jTextField229.setText(cmalphadot.get(8).toString());}
            if(alphas>9){jTextField230.setText(cmalphadot.get(9).toString());}
            if(alphas>10){jTextField231.setText(cmalphadot.get(10).toString());}
            if(alphas>11){jTextField232.setText(cmalphadot.get(11).toString());}
            if(alphas>12){jTextField233.setText(cmalphadot.get(12).toString());}
            if(alphas>13){jTextField234.setText(cmalphadot.get(13).toString());}
            if(alphas>14){jTextField235.setText(cmalphadot.get(14).toString());}
            if(alphas>15){jTextField236.setText(cmalphadot.get(15).toString());}
            if(alphas>16){jTextField237.setText(cmalphadot.get(16).toString());}
            if(alphas>17){jTextField238.setText(cmalphadot.get(17).toString());}
            if(alphas>18){jTextField239.setText(cmalphadot.get(18).toString());}
            if(alphas>19){jTextField240.setText(cmalphadot.get(19).toString());}
        } else if (column == 4) {
            jTextField241.setText(cmalphadot.get(1).toString());
            jTextField242.setText(cmalphadot.get(0).toString());
            if(alphas>2){jTextField243.setText(cmalphadot.get(2).toString());}
            if(alphas>3){jTextField244.setText(cmalphadot.get(3).toString());}
            if(alphas>4){jTextField245.setText(cmalphadot.get(4).toString());}
            if(alphas>5){jTextField246.setText(cmalphadot.get(5).toString());}
            if(alphas>6){jTextField247.setText(cmalphadot.get(6).toString());}
            if(alphas>7){jTextField248.setText(cmalphadot.get(7).toString());}
            if(alphas>8){jTextField249.setText(cmalphadot.get(8).toString());}
            if(alphas>9){jTextField250.setText(cmalphadot.get(9).toString());}
            if(alphas>10){jTextField251.setText(cmalphadot.get(10).toString());}
            if(alphas>11){jTextField252.setText(cmalphadot.get(11).toString());}
            if(alphas>12){jTextField253.setText(cmalphadot.get(12).toString());}
            if(alphas>13){jTextField254.setText(cmalphadot.get(13).toString());}
            if(alphas>14){jTextField255.setText(cmalphadot.get(14).toString());}
            if(alphas>15){jTextField256.setText(cmalphadot.get(15).toString());}
            if(alphas>16){jTextField257.setText(cmalphadot.get(16).toString());}
            if(alphas>17){jTextField258.setText(cmalphadot.get(17).toString());}
            if(alphas>18){jTextField259.setText(cmalphadot.get(18).toString());}
            if(alphas>19){jTextField260.setText(cmalphadot.get(19).toString());}
        } else if (column == 5) {
            jTextField141.setText(cmalphadot.get(1).toString());
            jTextField142.setText(cmalphadot.get(0).toString());
            if(alphas>2){jTextField143.setText(cmalphadot.get(2).toString());}
            if(alphas>3){jTextField144.setText(cmalphadot.get(3).toString());}
            if(alphas>4){jTextField145.setText(cmalphadot.get(4).toString());}
            if(alphas>5){jTextField146.setText(cmalphadot.get(5).toString());}
            if(alphas>6){jTextField147.setText(cmalphadot.get(6).toString());}
            if(alphas>7){jTextField148.setText(cmalphadot.get(7).toString());}
            if(alphas>8){jTextField149.setText(cmalphadot.get(8).toString());}
            if(alphas>9){jTextField150.setText(cmalphadot.get(9).toString());}
            if(alphas>10){jTextField151.setText(cmalphadot.get(10).toString());}
            if(alphas>11){jTextField152.setText(cmalphadot.get(11).toString());}
            if(alphas>12){jTextField153.setText(cmalphadot.get(12).toString());}
            if(alphas>13){jTextField154.setText(cmalphadot.get(13).toString());}
            if(alphas>14){jTextField155.setText(cmalphadot.get(14).toString());}
            if(alphas>15){jTextField156.setText(cmalphadot.get(15).toString());}
            if(alphas>16){jTextField157.setText(cmalphadot.get(16).toString());}
            if(alphas>17){jTextField158.setText(cmalphadot.get(17).toString());}
            if(alphas>18){jTextField159.setText(cmalphadot.get(18).toString());}
            if(alphas>19){jTextField160.setText(cmalphadot.get(19).toString());}
        } 
    }
}
