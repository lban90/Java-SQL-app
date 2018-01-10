package sample;

import au.com.bytecode.opencsv.CSVWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.*;

import jxl.CellType;
import jxl.read.biff.BiffException;
//import jxl.Cell;
//import jxl.Sheet;
//import jxl.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import au.com.bytecode.opencsv.CSVReader;

import org.apache.poi.ss.usermodel.CreationHelper;

public class Controller {


    public TextField tfDatabaseAdress;
    public TextField tfUser;
    public TextField tfPassword;
    public TextArea taPage1;
    static Connection connection = null;
    public TextArea taPage2;
    public static String database;
    public static String user;
    public static String pass;
    public TextField tfTableName;
    public TextField tfTableData;
    public TextField tfTableDataCsv;
    public int csvNumber = 0;
    public TextArea taCustomQuery;
    public TextField tfSqlAddress;
    public TextField tfCsvAddress;
    public TextField tfXlsAddress;


    public void connectToDatabase(ActionEvent actionEvent) throws Exception {

        if (tfDatabaseAdress.getText().equals("")) {
            taPage1.setText("Please insert the database adress");
        } else {
            if (tfUser.getText().equals("")) {
                taPage1.setText("Please insert the user name");
            } else {
                if (tfPassword.getText().equals("")) {
                    taPage1.setText("Please insert the password");
                } else {
                    Statement stmt = null;
                    ResultSet rs = null;
                    try {
                        Class.forName("com.mysql.jdbc.Driver").newInstance();
                    } catch (Exception ex) {
                        taPage1.setText("Instantierea driverului a esuat cu eroarea  " + ex);
                    }
                    try {
                        connection = DriverManager.getConnection(/*"jdbc:mysql://localhost/firmaauto?" + "user=root&password=admin"*/"jdbc:mysql:" + tfDatabaseAdress.getText() + "?" + "user=" + tfUser.getText() + "&password=" + tfPassword.getText());
                        taPage1.setText("Conexiunea cu baza de date a fost stabilita");
                        database = tfDatabaseAdress.getText();
                        user=tfUser.getText();
                        pass=tfPassword.getText();

                        //Schimba pagina
                        Node source = (Node) actionEvent.getSource();
                        Stage stage1 = (Stage) source.getScene().getWindow();
                        stage1.close();

                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Page2.fxml"));
                            Parent root1 = (Parent) fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.setTitle("Pagina operatii");
                            stage.setScene(new Scene(root1));
                            stage.show();
                        } catch (Exception e) {
                            taPage1.setText(e.getMessage());
                        }

                    } catch (SQLException ex) {
                        taPage1.setText("Conexiunea nu a putut fi stabilita din cauza erorii:   " + ex.getMessage());
                    }
                }
            }
        }
    }

    public void goBackPage1(ActionEvent actionEvent) {
        //Schimba pagina
        Node source = (Node) actionEvent.getSource();
        Stage stage1 = (Stage) source.getScene().getWindow();
        stage1.close();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("startFrame.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Pagina de logare");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            taPage1.setText(e.getMessage());
        }
    }

    public void showSchemaStructure(ActionEvent actionEvent) {
        taPage2.setText("");
        String schema = database.substring(database.lastIndexOf("/") + 1);
        String[] types = {"TABLE"};
        ResultSet resultSet = null;
        ResultSet resultSet1 = null;
        try {
            resultSet = connection.getMetaData().getTables(schema, null, "%", types);
        } catch (SQLException e) {
            taPage2.setText("1 A fost gasita o eroare :  " + e.getMessage());
        }
        String tableName = "";
        try {
            taPage2.appendText("Tabelele bazei de date sunt: \n \n");
            while (resultSet.next()) {
                taPage2.appendText("\n\n");
                tableName = resultSet.getString(3);
                taPage2.appendText(tableName + "\n");


                DatabaseMetaData meta = connection.getMetaData();
                resultSet1 = meta.getColumns(schema, null, tableName, "%");
                taPage2.appendText("Coloanele tabelelor sunt: \n ");

                while (resultSet1.next()) {
                    taPage2.appendText("     " + resultSet1.getString(4) + "  tipul coloanei este  " + resultSet1.getString("TYPE_NAME") + "\n");

                }
            }
        } catch (SQLException e) {
            taPage2.setText("2 A fost gasita o eroare:  " + e.getMessage());
        }
        try {
            resultSet.close();
        } catch (SQLException e) {
            taPage2.setText("3 A fost gasita o eroare:  " + e.getMessage());
        }
    }

    public void closePage(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage1 = (Stage) source.getScene().getWindow();
        stage1.close();
    }

    public void listTableStructure(ActionEvent actionEvent) {
        taPage2.setText("");
        String schema = database.substring(database.lastIndexOf("/") + 1);
        ResultSet resultSet1 = null;
        DatabaseMetaData meta = null;

        try {
            meta = connection.getMetaData();
        } catch (SQLException e) {
            taPage2.setText("4 A fost gasita o eroare:   " + e.getMessage());
        }
        try {
            resultSet1 = meta.getColumns(schema, null, tfTableName.getText(), "%");
        } catch (SQLException e) {
            taPage2.setText("5 A fost gasita o eroare:   " + e.getMessage());
        }
        taPage2.appendText("Coloanele tabelei:" + tfTableName.getText() + " sunt: \n ");

        try {
            while (resultSet1.next()) {
                taPage2.appendText("     " + resultSet1.getString(4) + "  tipul coloanei este  " + resultSet1.getString("TYPE_NAME") + "\n");

            }
        } catch (SQLException e) {
            taPage2.setText("6 A fost gasita o eroare:   " + e.getMessage());
        }
    }

    public void listTableData(ActionEvent actionEvent) {
        taPage2.setText("");
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            taPage2.setText("7 A fost gasita o eroare:   " + e.getMessage());
        }
        try {
            rs = stmt.executeQuery("SELECT * FROM " + tfTableData.getText());
        } catch (SQLException e) {
            taPage2.setText("8 A fost gasita o eroare:   " + e.getMessage());
        }
        try {
            if (stmt.execute("SELECT *  FROM " + tfTableData.getText())) {
                rs = stmt.getResultSet();
            }
        } catch (SQLException e) {
            taPage2.setText("9 A fost gasita o eroare:   " + e.getMessage());
        }
        try {
            ArrayList<String> arrayList = new ArrayList<String>();
            ResultSetMetaData metadata = rs.getMetaData();
            int numberOfColumns = metadata.getColumnCount();
            while (rs.next()) {
                int i = 1;
                while (i <= numberOfColumns) {
                    arrayList.add(rs.getString(i++));
                }
            }

            for (int i = 1; i <= numberOfColumns; i++) {
                taPage2.appendText(metadata.getColumnName(i) + "--");
            }
            taPage2.appendText("\n");
            int j = 0;
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i) == null) {
                    if ((i % (numberOfColumns - 1 + j)) == 0 && (i != 0)) {
                        taPage2.appendText("-");
                        taPage2.appendText("\n");
                        j = j + numberOfColumns;
                    } else {
                        taPage2.appendText("-" + " ,");
                    }
                } else if (numberOfColumns - 1 + j == 0) {
                    taPage2.appendText(arrayList.get(i).toString());
                    taPage2.appendText("\n");
                    j = j + numberOfColumns;

                } else {
                    if ((i % (numberOfColumns - 1 + j)) == 0 && (i != 0)) {
                        taPage2.appendText(arrayList.get(i).toString());
                        taPage2.appendText("\n");
                        j = j + numberOfColumns;
                    } else {
                        taPage2.appendText(arrayList.get(i).toString() + " ,");
                    }
                }
                System.out.println("10+j este :" + (10 + j));
            }

        } catch (SQLException e) {
            taPage2.setText("10 A fost gasita o eroare:   " + e.getMessage());
        }
    }

    public void exportTableDataCsv(ActionEvent actionEvent) throws Exception{
        ResultSet rs = null;
        ResultSet rs1 = null;
        Statement stmt = null;
            String schema = database.substring(database.lastIndexOf("/") + 1);
            DatabaseMetaData meta = connection.getMetaData();
            rs1 = meta.getColumns(schema, null, tfTableDataCsv.getText(), "%");

            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tfTableDataCsv.getText());
            if (stmt.execute("SELECT * FROM " + tfTableDataCsv.getText())) {
                rs = stmt.getResultSet();
            }

            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();

            String csv = "E:\\Luci\\Alcatel project\\CSV\\" + tfTableDataCsv.getText() + csvNumber + ".csv";
            csvNumber++;
            CSVWriter writer = new CSVWriter(new FileWriter(csv));
            String[] tableData = new String[numberOfColumns];
            String[] tableData1 = new String[numberOfColumns];
            int col = 0;
            //introducem in csv coloanele
            while (rs1.next()) {
                tableData1[col] = rs1.getString(4);
                col++;
            }
            writer.writeNext(tableData1);
            //introducem in csv datele
            while (rs.next()) {
                for (int i = 1; i <= numberOfColumns; i++) {
                    tableData[i - 1] = rs.getString(i);
                }
                writer.writeNext(tableData);
            }

            taPage2.setText("CSV written successfully to E:\\Luci\\Alcatel project\\CSV");
            writer.close();
        }

    public void listCustomQuery(ActionEvent actionEvent) {
        taPage2.setText("");
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            taPage2.setText("7 A fost gasita o eroare:   " + e.getMessage());
        }
        try {
            rs = stmt.executeQuery(taCustomQuery.getText());
        } catch (SQLException e) {
            taPage2.setText("8 A fost gasita o eroare:   " + e.getMessage());
        }
        try {
            if (stmt.execute(taCustomQuery.getText())) {
                rs = stmt.getResultSet();
            }
        } catch (SQLException e) {
            taPage2.setText("9 A fost gasita o eroare:   " + e.getMessage());
        }
        try {
            ArrayList<String> arrayList = new ArrayList<String>();
            ResultSetMetaData metadata = rs.getMetaData();
            int numberOfColumns = metadata.getColumnCount();
            while (rs.next()) {
                int i = 1;
                while (i <= numberOfColumns) {
                    arrayList.add(rs.getString(i++));
                }
            }

            for (int i = 1; i <= numberOfColumns; i++) {
                taPage2.appendText(metadata.getColumnName(i) + "--");
            }
            taPage2.appendText("\n");
            int j = 0;
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i) == null) {
                    if ((i % (numberOfColumns - 1 + j)) == 0 && (i != 0)) {
                        taPage2.appendText("-");
                        taPage2.appendText("\n");
                        j = j + numberOfColumns;
                    } else {
                        taPage2.appendText("-" + " ,");
                    }
                } else if (numberOfColumns - 1 + j == 0) {
                    taPage2.appendText(arrayList.get(i).toString());
                    taPage2.appendText("\n");
                    j = j + numberOfColumns;

                } else {
                    if ((i % (numberOfColumns - 1 + j)) == 0 && (i != 0)) {
                        taPage2.appendText(arrayList.get(i).toString());
                        taPage2.appendText("\n");
                        j = j + numberOfColumns;
                    } else {
                        taPage2.appendText(arrayList.get(i).toString() + " ,");
                    }
                }
                System.out.println("10+j este :" + (10 + j));
            }

        } catch (SQLException e) {
            taPage2.setText("10 A fost gasita o eroare:   " + e.getMessage());
        }
    }

    public void importSqlFile(ActionEvent actionEvent) {
        //READ THE CONTENT OF THE FILE

        String content = null;
        File file = new File(tfSqlAddress.getText());
        try {
            FileReader reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            taPage2.setText("A fost gasita o eroare:    " + e.getMessage());
        }
        taPage2.setText("");
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            taPage2.setText("7 A fost gasita o eroare:   " + e.getMessage());
        }
        try {
            rs = stmt.executeQuery(content);
        } catch (SQLException e) {
            taPage2.setText("8 A fost gasita o eroare:   " + e.getMessage());
        }
        try {
            if (stmt.execute(content)) {
                rs = stmt.getResultSet();
            }
        } catch (SQLException e) {
            taPage2.setText("9 A fost gasita o eroare:   " + e.getMessage());
        }
        try {
            ArrayList<String> arrayList = new ArrayList<String>();
            ResultSetMetaData metadata = rs.getMetaData();
            int numberOfColumns = metadata.getColumnCount();
            while (rs.next()) {
                int i = 1;
                while (i <= numberOfColumns) {
                    arrayList.add(rs.getString(i++));
                }
            }

            for (int i = 1; i <= numberOfColumns; i++) {
                taPage2.appendText(metadata.getColumnName(i) + "--");
            }
            taPage2.appendText("\n");
            int j = 0;
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i) == null) {
                    if ((i % (numberOfColumns - 1 + j)) == 0 && (i != 0)) {
                        taPage2.appendText("-");
                        taPage2.appendText("\n");
                        j = j + numberOfColumns;
                    } else {
                        taPage2.appendText("-" + " ,");
                    }
                } else if (numberOfColumns - 1 + j == 0) {
                    taPage2.appendText(arrayList.get(i).toString());
                    taPage2.appendText("\n");
                    j = j + numberOfColumns;

                } else {
                    if ((i % (numberOfColumns - 1 + j)) == 0 && (i != 0)) {
                        taPage2.appendText(arrayList.get(i).toString());
                        taPage2.appendText("\n");
                        j = j + numberOfColumns;
                    } else {
                        taPage2.appendText(arrayList.get(i).toString() + " ,");
                    }
                }
                System.out.println("10+j este :" + (10 + j));
            }

        } catch (SQLException e) {
            taPage2.setText("10 A fost gasita o eroare:   " + e.getMessage());
        }
    }

    public void exportSchemaToXls(ActionEvent actionEvent) throws Exception {
        //PRELUAM TOATE TABELELE DIN SCHEMA
        String[] types = {"TABLE"};
        ResultSet resultSet = null;
        int numberOfTables = 0;
        String schema = database.substring(database.lastIndexOf("/") + 1);
        resultSet = connection.getMetaData().getTables(schema, null, "%", types);
        List<String> listaTabele = new ArrayList<String>();
        while (resultSet.next()) {
            String tableName = resultSet.getString(3);
            listaTabele.add(tableName);
            numberOfTables++;
        }
        //EXPORTAM TOATE TABELELE IN CSV
        for (int i = 0; i < numberOfTables; i++) {
            ResultSet rs = null;
            Statement stmt = null;

            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + listaTabele.get(i));
            if (stmt.execute("SELECT * FROM " + listaTabele.get(i))) {
                rs = stmt.getResultSet();
            }

            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();

            String csv = "E:\\Luci\\Alcatel project\\XLS\\" + listaTabele.get(i) + ".csv";
            csvNumber++;
            CSVWriter writer = new CSVWriter(new FileWriter(csv));
            String[] tableData = new String[numberOfColumns];
            while (rs.next()) {
                for (int j = 1; j <= numberOfColumns; j++) {
                    tableData[j - 1] = rs.getString(j);
                }
                writer.writeNext(tableData);
            }

            taPage2.setText("CSV written successfully ");
            writer.close();
        }
        // SCRIEM TOATE CSV INTR-UN XLS
        Workbook wb = new HSSFWorkbook();
        CreationHelper helper = wb.getCreationHelper();

        for (int i = 0; i < numberOfTables; i++) {
            Sheet sheet = wb.createSheet(listaTabele.get(i));
            CSVReader reader = new CSVReader(new FileReader("E:\\Luci\\Alcatel project\\XLS\\" + listaTabele.get(i) + ".csv"));
            String[] line;
            int r = 0;
            while ((line = reader.readNext()) != null) {
                Row row = sheet.createRow((short) r++);

                for (int j = 0; j < line.length; j++)
                    row.createCell(j)
                            .setCellValue(helper.createRichTextString(line[j]));
            }
            FileOutputStream fileOut = new FileOutputStream("workbook.xls");
            wb.write(fileOut);
            fileOut.close();
        }
        taPage2.setText("Fisierul XLS a fost creat cu succes:  Workbook.xls");


    }

    public void importCsvTable(ActionEvent actionEvent) throws SQLException {

        String schema = database.substring(database.lastIndexOf("/") + 1);
        String address = tfCsvAddress.getText().substring(tfCsvAddress.getText().lastIndexOf("\\") + 1);
        String[] split = address.split("\\.");
        String table = split[0];
        PreparedStatement pst;
        //PREIA AUTOMAT TABELA DIN ADRESA INTRODUSA DE LA TASTATURA

        ResultSet rs = null;
        DatabaseMetaData meta = connection.getMetaData();
        rs = meta.getColumns(schema, null, table, "%");
        int numarColoane = 0;
        while (rs.next()) {
            numarColoane++;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(tfCsvAddress.getText()));
            String line;
            int nrLinii = 1;
            String listaColoaneString = "(";
            while ((line = br.readLine()) != null) {
                String[] value = line.split(",");
                String listaValori = "(";
                if (nrLinii == 1) {
                    for (int i = 0; i < numarColoane; i++) {
                        if (i < (numarColoane - 1)) {
                            listaColoaneString = listaColoaneString + value[i] + ",";
                        } else {
                            listaColoaneString = listaColoaneString + value[i] + ")";
                        }
                    }
                } else {
                    for (int i = 0; i < numarColoane; i++) {
                        if (i < (numarColoane - 1)) {
                            listaValori = listaValori + "'" + value[i] + "'" + ",";
                        } else {
                            listaValori = listaValori + "'" + value[i] + "'" + ")";
                        }
                    }
                    String sql="INSERT INTO "+table+" "+listaColoaneString+" VALUES "+" "+listaValori;
                    pst=connection.prepareStatement(sql);
                    pst.executeUpdate();

                    System.out.println("Lista coloane String " + listaColoaneString);
                    System.out.println("Lista valori" + listaValori);
                    System.out.println(numarColoane);
                }
                nrLinii++;
            }

            br.close();
            taPage2.setText("Datele au fost introduse cu succes.");

        } catch (Exception e) {
            taPage2.appendText("A fost gasita eroarea " + e.getMessage());
        }
    }

    public void importXlsTable(ActionEvent actionEvent) throws Exception {
        File inputWorkbook = new File(tfXlsAddress.getText());
        jxl.Workbook w;
        w = jxl.Workbook.getWorkbook(inputWorkbook);
        int numberOfSheets = w.getNumberOfSheets();

        //PARCURGE TOATE FOILE DIN DOCUMENT
        for (int z = 0; z < numberOfSheets; z++) {
            String schema = database.substring(database.lastIndexOf("/") + 1);
            jxl.Sheet sheet = w.getSheet(z);
            //System.out.println(sheet.getName());
            String listaColoaneString = "(";
            ResultSet rs = null;
            DatabaseMetaData meta = connection.getMetaData();
            rs = meta.getColumns(schema, null, sheet.getName(), "%");
            PreparedStatement pst;
            int numarColoane = 0;
            List<String> coloane = new ArrayList<>();
            while (rs.next()) {
                numarColoane++;
                coloane.add(rs.getString(4));
            }

            //PARCURGE TOATE COLOANELE DIN TABELA SALVATA IN FOAIA CURENTA
            for (int i = 0; i < coloane.size(); i++) {

                if (i < (numarColoane - 1)) {
                    listaColoaneString = listaColoaneString + coloane.get(i) + ",";
                } else {
                    listaColoaneString = listaColoaneString + coloane.get(i) + ")";
                }
            }
           // System.out.println(listaColoaneString);

            //PARCURGE TOATE RANDURILE IN FOAIA CURENTA
            for (int j = 0; j < sheet.getRows(); j++) {
                String listaValori = "(";
                for (int i = 0; i < sheet.getColumns(); i++) {
                    jxl.Cell cell = sheet.getCell(i, j);
                    //System.out.print(cell.getContents()+" ");

                    if (i < (sheet.getColumns() - 1)) {
                        listaValori = listaValori + "'" + cell.getContents() + "'" + ",";
                    } else {
                        listaValori = listaValori + "'" + cell.getContents() + "'" + ")";
                    }
                }
                String sql = "INSERT INTO " + sheet.getName() + " " + listaColoaneString + " VALUES " + " " + listaValori;
                pst = connection.prepareStatement(sql);
                pst.executeUpdate();
                taPage2.setText("Baza de date a fost updatata cu success");
                System.out.println(listaValori);
            }


        }

        //rezolva problemele in cazul in care valoarea este NULL
    }

    public void exportDumpFile(ActionEvent actionEvent) throws Exception {
        Process p;
        String schema = database.substring(database.lastIndexOf("/") + 1);

        try {
            StringBuilder message = new StringBuilder();
            String line = "C:\\Program Files (x86)\\MySQL\\MySQL Server 5.6\\bin\\mysqldump -u "+user+" --password="+pass+" "+schema+" --result-file=\"E:\\Luci\\Alcatel project\\ImportData\\"+schema+".sql"+"\"";
            p = Runtime.getRuntime().exec(line);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = br.readLine()) != null) {
                message.append(line + "\n");
            }
            System.out.print(message.toString());
            taPage2.setText("Fisierul dump a fost creat  la adresa E:\\Luci\\Alcatel project\\ImportData"+"\n");

        } catch (IOException e) {
            taPage2.setText(e.getMessage());
        }
    }

    public void importDumpFile(ActionEvent actionEvent) throws Exception{
        try {
            StringBuilder message = new StringBuilder();
            String line;
            List cmdAndArgs = Arrays.asList("cmd", "/c", "sqldump.bat");
            File dir = new File("C:\\Program Files (x86)\\MySQL\\MySQL Server 5.6\\bin");
            ProcessBuilder pb = new ProcessBuilder(cmdAndArgs);
            pb.directory(dir);
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = br.readLine()) != null) {
                message.append(line + "\n");
            }
            taPage2.setText(message.toString());
        }catch(Exception e){
            taPage2.setText(e.getMessage());
        }
    }
}